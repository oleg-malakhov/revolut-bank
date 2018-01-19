package ru.legatus.revolut.services.jdbi;

import java.util.List;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.transaction.TransactionIsolationLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

import ru.legatus.revolut.model.Account;
import ru.legatus.revolut.services.AccountNotFoundException;
import ru.legatus.revolut.services.AccountService;

public class JdbiAccountService implements AccountService {

   private static final Logger LOG = LoggerFactory.getLogger(JdbiAccountService.class);

   private static final String SELECT_BY_ID =
      "SELECT id, client_id, name, balance " +
         "FROM accounts " +
         "WHERE id = :accountId";

   private static final String SELECT_BY_CLIENT =
      "SELECT id, client_id, name, balance " +
         "FROM accounts " +
         "WHERE client_id = :clientId";

   private final Jdbi jdbi;

   @Inject
   public JdbiAccountService(Jdbi jdbi) {
      this.jdbi = jdbi;
   }

   public Account createAccount(int clientId, String accountName) {
      JdbiClientService clientService = new JdbiClientService(jdbi);

      int accountId = jdbi.inTransaction(TransactionIsolationLevel.REPEATABLE_READ, h -> {

         clientService.getInternal(clientId, h);

         h.createUpdate("INSERT INTO accounts(client_id, name) VALUES(:clientId, :accountName)")
            .bind("clientId", clientId)
            .bind("accountName", accountName)
            .execute();
         return h.createQuery("CALL SCOPE_IDENTITY()").mapTo(Integer.class).findOnly();
      });

      return get(accountId);
   }

   public Account get(int accountId) {
      return jdbi.withHandle(h -> getAccountInternal(accountId, h));
   }

   public List<Account> listByClient(int clientId) {
      new JdbiClientService(jdbi).get(clientId);
      return jdbi.withHandle(h -> h.createQuery(SELECT_BY_CLIENT)
         .bind("clientId", clientId)
         .mapToBean(Account.class)
         .list()
      );
   }

   Account getAccountInternal(int accountId, Handle h) {
      try {
         return h.createQuery(SELECT_BY_ID)
            .bindMap(ImmutableMap.of(
               "accountId", accountId
            )).mapToBean(Account.class)
            .findOnly();
      } catch (IllegalStateException ex) {
         String message = String.format("Can't get account %d", accountId);
         LOG.warn(message);

         throw new AccountNotFoundException(accountId);
      }
   }


}
