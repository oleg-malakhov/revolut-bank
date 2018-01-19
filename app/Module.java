import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import play.db.Database;
import ru.legatus.revolut.model.Bank;
import ru.legatus.revolut.model.Client;
import ru.legatus.revolut.services.AccountService;
import ru.legatus.revolut.services.ClientService;
import ru.legatus.revolut.services.jdbi.JdbiAccountService;
import ru.legatus.revolut.services.jdbi.JdbiClientService;
import ru.legatus.revolut.services.jdbi.JdbiTransferService;
import ru.legatus.revolut.services.TransferService;

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.
 *
 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
public class Module extends AbstractModule {

    private static final Logger LOG = LoggerFactory.getLogger(Module.class);

    private static final Joiner J = Joiner.on(" ");
    private static final Splitter S = Splitter.on(";").omitEmptyStrings();

    @Override
    public void configure() {
        bind(TransferService.class).to(JdbiTransferService.class);
    }

    @Provides @Singleton
    public Jdbi provideJdbi(Database database) throws IOException {
        Jdbi jdbi = Jdbi.create(database.getDataSource());
        jdbi.useHandle(h -> h.createScript(readDDL()).executeAsSeparateStatements());

        return jdbi;
    }

    @Provides @Singleton
    public ClientService provideClientService(Jdbi jdbi) {
        ClientService clientService = new JdbiClientService(jdbi);

        if(clientService.findByPassport(Bank.PS).isPresent()) {
            LOG.info("ClientService already contains the Bank record");
        } else {
            clientService.create(Bank.PS, Bank.NAME, Bank.NAME);
            LOG.info("ClientService create the Bank record");
        }

        return clientService;
    }

    @Provides @Singleton
    public AccountService provideAccountService(Jdbi jdbi, ClientService clientService) {
        Client bank = clientService.get(Bank.ID);

        AccountService accountService = new JdbiAccountService(jdbi);

        if(accountService.listByClient(bank.getId()).size() != 0) {
            LOG.info("AccountService already contains the Bank accounts");
        } else {
            accountService.createAccount(bank.getId(), "PUT");
            accountService.createAccount(bank.getId(), "WITHDRAW");
            LOG.info("AccountService created the Bank accounts");
        }

        return accountService;
    }

    private String readDDL() throws IOException {

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(Module.class.getResourceAsStream("/schema.ddl")))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

}
