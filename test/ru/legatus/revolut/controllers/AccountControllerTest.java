package ru.legatus.revolut.controllers;

import java.util.List;

import org.junit.Test;

import ru.legatus.revolut.model.Account;
import ru.legatus.revolut.model.Client;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountControllerTest extends AbstractControllerTest {



   @Test
   public void shouldCreateAccount() {
      Client client = post("/client/", C.CLIENT_JANE).entityOf(Client.class);

      Account account = post("/account/",
         String.format(C.ACCOUNT, client.getId(), "Main account")
      ).entityOf(Account.class);

      assertThat(account).isNotNull();
      assertThat(account.getClientId()).isEqualTo(client.getId());
      assertThat(account.getBalance()).isEqualByComparingTo("0.0");
   }

   @Test
   public void shouldListAllClientAccounts() {
      Client client = post("/client/", C.CLIENT_JANE).entityOf(Client.class);

      Account main = post("/account/",
         String.format(C.ACCOUNT, client.getId(), "Main account")
      ).entityOf(Account.class);

      Account second = post("/account/",
         String.format(C.ACCOUNT, client.getId(), "Second account")
      ).entityOf(Account.class);

      List<Account> accounts = get("/client/" + client.getId() + "/accounts")
         .listOf(Account.class);

      assertThat(accounts).hasSize(2);
      assertThat(accounts).contains(main, second);
   }
}
