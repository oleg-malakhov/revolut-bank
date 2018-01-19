package ru.legatus.revolut.services;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import play.test.WithApplication;
import ru.legatus.revolut.model.Account;
import ru.legatus.revolut.model.Bank;
import ru.legatus.revolut.model.Client;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountServiceTest extends WithApplication {

   private ClientService clientService;
   private AccountService accountService;

   @Before
   public void setupServices() {
      clientService = instanceOf(ClientService.class);
      accountService = instanceOf(AccountService.class);
   }

   @Test
   public void shouldCreateAccountServiceWithBankAccounts() {
      Account put = accountService.get(Bank.ACCOUNT_PUT_MONEY);

      assertThat(put).isNotNull();
      assertThat(put.getId()).isEqualTo(Bank.ACCOUNT_PUT_MONEY);
      assertThat(put.getClientId()).isEqualTo(Bank.ID);
      assertThat(put.getBalance()).isZero();

      Account withdraw = accountService.get(Bank.ACCOUNT_WITHDRAW_MONEY);
      assertThat(withdraw).isNotNull();
      assertThat(withdraw.getId()).isEqualTo(Bank.ACCOUNT_WITHDRAW_MONEY);
      assertThat(withdraw.getClientId()).isEqualTo(Bank.ID);
      assertThat(withdraw.getBalance()).isZero();
   }



   @Test
   public void shouldCreateAccountsForClient() {
      Client client = clientService.create("12345", "John", "Smith");
      Account mainAccount = accountService.createAccount(client.getId(), "Main");
      Account secondaryAccount = accountService.createAccount(client.getId(), "Secondary");

      assertThat(mainAccount).isNotNull();
      assertThat(secondaryAccount).isNotNull();

      List<Account> accounts = accountService.listByClient(client.getId());
      assertThat(accounts).hasSize(2);
      assertThat(accounts).contains(mainAccount, secondaryAccount);
   }
}
