package ru.legatus.revolut.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import play.test.WithApplication;
import ru.legatus.revolut.model.Account;
import ru.legatus.revolut.model.Client;
import ru.legatus.revolut.model.Transfer;

import static org.assertj.core.api.Assertions.assertThat;

public class TransferServiceTest extends WithApplication {

   private ClientService clientService;
   private AccountService accountService;
   private TransferService transferService;

   @Before
   public void setupServices() {
      clientService = instanceOf(ClientService.class);
      accountService = instanceOf(AccountService.class);
      transferService = instanceOf(TransferService.class);
   }

   @Test
   public void shouldPutCash() {
      Client client = clientService.create("12345", "John", "Smith");
      Account account = accountService.createAccount(client.getId(), "Main account");

      LocalDateTime now = LocalDateTime.now();
      transferService.deposit(account.getId(), new BigDecimal("100"));

      account = accountService.get(account.getId());
      assertThat(account.getBalance()).isEqualByComparingTo("100");

      List<Transfer> transfers = transferService.listTransfers(account.getId());
      assertThat(transfers).hasSize(1);

      Transfer transfer = transfers.get(0);
      assertThat(transfer.getMoney()).isEqualByComparingTo("100");
      assertThat(transfer.getMessage()).isEqualTo("Deposit cash");
      assertThat(transfer.getTimestamp()).isAfter(now);
   }

   @Test
   public void shouldFailWithdrawIfNotEnoughMoney() {
      Client client = clientService.create("12345", "John", "Smith");
      Account account = accountService.createAccount(client.getId(), "Main account");
      transferService.deposit(account.getId(), new BigDecimal("100"));

      try {
         transferService.withdraw(account.getId(), new BigDecimal("200"));
      } catch (NotEnoughMoneyException ex) {
         assertThat(ex.getMessage())
            .isEqualTo("Account " + account.getId() + " has $100.00, but at least $200.00 required");
      }

      account = accountService.get(account.getId());
      assertThat(account.getBalance()).isEqualByComparingTo("100");

      List<Transfer> transfers = transferService.listTransfers(account.getId());
      assertThat(transfers).hasSize(1);
   }

   @Test
   public void shouldWithdrawCash() {
      Client client = clientService.create("12345", "John", "Smith");
      Account account = accountService.createAccount(client.getId(), "Main account");
      transferService.deposit(account.getId(), new BigDecimal("100"));

      transferService.withdraw(account.getId(), new BigDecimal("50"));

      account = accountService.get(account.getId());
      assertThat(account.getBalance()).isEqualByComparingTo("50");

      List<Transfer> transfers = transferService.listTransfers(account.getId());
      assertThat(transfers).hasSize(2);

      Transfer depositTransfer = transfers.get(1);
      assertThat(depositTransfer.getMoney()).isEqualByComparingTo("100");
      assertThat(depositTransfer.getMessage()).isEqualTo("Deposit cash");

      Transfer withdrawTransfer = transfers.get(0);
      assertThat(withdrawTransfer.getMoney()).isEqualByComparingTo("-50");
      assertThat(withdrawTransfer.getMessage()).isEqualTo("Withdraw cash");
   }
}
