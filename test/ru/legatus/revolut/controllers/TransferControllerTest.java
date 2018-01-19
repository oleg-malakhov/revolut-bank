package ru.legatus.revolut.controllers;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Test;

import play.mvc.Http;
import ru.legatus.revolut.model.Account;
import ru.legatus.revolut.model.Client;
import ru.legatus.revolut.model.Transfer;

import static org.assertj.core.api.Assertions.assertThat;

public class TransferControllerTest extends AbstractControllerTest {

   private static final String MAKE_DEPOSIT = "{ 'accountIdTo': %d, 'money': '%.2f' }";
   private static final String MAKE_WITHDRAW = "{ 'accountIdFrom': %d, 'money': '%.2f' }";
   private static final String MAKE_TRANSFER = "{ 'accountIdFrom': %d, 'accountIdTo': %d, 'money': '%.2f' }";

   @Test
   public void shouldDepositCash() {
      Client client = post("/client/", C.CLIENT_JACK).entityOf(Client.class);
      Account account = post("/account/",
         String.format(C.ACCOUNT, client.getId(), "Main"))
         .entityOf(Account.class);

      Account updated = post("/transfer/deposit", String.format(MAKE_DEPOSIT, account.getId(), 100.0))
         .entityOf(Account.class);

      assertThat(updated).isNotNull();
      assertThat(updated.getBalance()).isEqualByComparingTo("100");
   }

   @Test
   public void shouldWithdrawCash() {
      Client client = post("/client/", C.CLIENT_JACK).entityOf(Client.class);
      Account account = post("/account/",
         String.format(C.ACCOUNT, client.getId(), "Main"))
         .entityOf(Account.class);

      post("/transfer/deposit", String.format(MAKE_DEPOSIT, account.getId(), 100.0))
         .entityOf(Account.class);

      Account updated = post("/transfer/withdraw", String.format(MAKE_WITHDRAW, account.getId(), 50.0))
         .entityOf(Account.class);

      assertThat(updated).isNotNull();
      assertThat(updated.getBalance()).isEqualByComparingTo("50");
   }

   @Test
   public void shouldFailIfNotEnoughMoney() {
      Client client = post("/client/", C.CLIENT_JACK).entityOf(Client.class);
      Account account = post("/account/",
         String.format(C.ACCOUNT, client.getId(), "Main"))
         .entityOf(Account.class);

      post("/transfer/deposit", String.format(MAKE_DEPOSIT, account.getId(), 100.0))
         .entityOf(Account.class);

      Response response = post(
         "/transfer/withdraw",
         String.format(MAKE_WITHDRAW, account.getId(), 200.0),
         Http.Status.NOT_ACCEPTABLE
      );

      assertThat(response.status()).isEqualTo(Http.Status.NOT_ACCEPTABLE);

      String expectedMessage = String.format("Account %d has $%.2f, but at least $%.2f required",
         account.getId(), 100.0, 200.0);

      assertThat(response.message()).isEqualTo(expectedMessage);
      assertThat(response.entityOf(Account.class)).isNull();

   }

   @Test
   public void shouldListTransfers() {
      Client client = post("/client/", C.CLIENT_JACK).entityOf(Client.class);
      Account account = post("/account/",
         String.format(C.ACCOUNT, client.getId(), "Main"))
         .entityOf(Account.class);

      post("/transfer/deposit", String.format(MAKE_DEPOSIT, account.getId(), 100.0))
         .entityOf(Account.class);

      post("/transfer/withdraw", String.format(MAKE_WITHDRAW, account.getId(), 50.0))
         .entityOf(Account.class);

      List<Transfer> transfers = get("/account/" + account.getId() + "/transfers").listOf(Transfer.class);
      assertThat(transfers).hasSize(2);

      assertThat(transfers.get(0).getMoney()).isEqualByComparingTo("-50");
      assertThat(transfers.get(1).getMoney()).isEqualByComparingTo("100");

      assertThat(transfers.get(0).getTimestamp()).isAfter(
         transfers.get(1).getTimestamp()
      );
   }

   @Test
   public void shouldTransferBetweenAccounts() {
      Client jane = post("/client/", C.CLIENT_JANE).entityOf(Client.class);
      Client jack = post("/client/", C.CLIENT_JACK).entityOf(Client.class);

      Account janeAccount = post("/account/",
         String.format(C.ACCOUNT, jane.getId(), "Jane")
      ).entityOf(Account.class);

      Account jackAccount = post("/account/",
         String.format(C.ACCOUNT, jack.getId(), "Jack")
      ).entityOf(Account.class);

      post("/transfer/deposit", String.format(MAKE_DEPOSIT, janeAccount.getId(), 100.0))
         .entityOf(Account.class);

      List<Account> updatedAccounts = post("/transfer/between",
         String.format(MAKE_TRANSFER, janeAccount.getId(), jackAccount.getId(), 25.5)
      ).listOf(Account.class);

      Map<Integer, Account> map = updatedAccounts.stream()
         .collect(Collectors.toMap(Account::getId, Function.identity()));

      Account updatedJaneAccount = map.get(janeAccount.getId());
      Account updatedJackAccount = map.get(jackAccount.getId());

      assertThat(updatedJaneAccount.getBalance()).isEqualByComparingTo("74.5");
      assertThat(updatedJackAccount.getBalance()).isEqualByComparingTo("25.5");
   }


}
