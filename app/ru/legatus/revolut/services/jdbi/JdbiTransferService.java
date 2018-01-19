package ru.legatus.revolut.services.jdbi;

import java.math.BigDecimal;
import java.util.List;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.transaction.TransactionIsolationLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import ru.legatus.revolut.model.Account;
import ru.legatus.revolut.model.Bank;
import ru.legatus.revolut.model.Transfer;
import ru.legatus.revolut.services.NotEnoughMoneyException;
import ru.legatus.revolut.services.TransferException;
import ru.legatus.revolut.services.TransferService;

public class JdbiTransferService implements TransferService {

   private static final Logger LOG = LoggerFactory.getLogger(JdbiTransferService.class);

   private static final String CREATE_TRANSFER =
      "INSERT INTO transfers(account_from, account_to, money, message) " +
         "VALUES(:from, :to, :money, :message)";

   private static final String UPDATE_BALANCE_FROM = "UPDATE accounts " +
      "SET balance = balance - :money " +
      "WHERE id = :accountId";

   private static final String UPDATE_BALANCE_TO = "UPDATE accounts " +
      "SET balance = balance + :money " +
      "WHERE id = :accountId";

   private static final String SELECT_TRANSFERS_FOR_ACCOUNT =
      "SELECT timestamp, account_id, money, message FROM (" +
         "SELECT timestamp, account_to as account_id, money, message FROM transfers WHERE account_to = :accountId " +
         "UNION ALL " +
         "SELECT timestamp, account_from as account_id, -money, message FROM transfers WHERE account_from = :accountId" +
         ") t ORDER BY timestamp DESC";

   private final Jdbi jdbi;

   @Inject
   public JdbiTransferService(Jdbi jdbi) {
      this.jdbi = jdbi;
   }

   public void transfer(int accountIdFrom, int accountIdTo, BigDecimal money, String message) {
      if(accountIdFrom == Bank.ACCOUNT_WITHDRAW_MONEY) {
         LOG.error("Illegal try to withdraw money from Bank account [" + accountIdFrom + "]");
         throw new TransferException("Wrong transfer operation");
      }

      if(accountIdTo == Bank.ACCOUNT_PUT_MONEY) {
         LOG.error("Illegal try to deposit money to Bank account [" + accountIdFrom + "]");
         throw new TransferException("Wrong transfer operation");
      }

      jdbi.useTransaction(TransactionIsolationLevel.REPEATABLE_READ, h -> {
         JdbiAccountService accountService = new JdbiAccountService(jdbi);

         Account from = accountService.getAccountInternal(accountIdFrom, h);
         Account to = accountService.getAccountInternal(accountIdTo, h);

         if(from.getId() != Bank.ACCOUNT_PUT_MONEY && from.getBalance().compareTo(money) < 0) {
            String errorMessage = String.format("Account %d has $%.2f, but at least $%.2" +
                  "f required",
               from.getId(), from.getBalance(), money);
            throw new NotEnoughMoneyException(errorMessage);
         }

         h.createUpdate(CREATE_TRANSFER)
            .bind("from", from.getId())
            .bind("to", to.getId())
            .bind("money", money)
            .bind("message", message)
            .execute();


         h.createUpdate(UPDATE_BALANCE_FROM)
            .bind("accountId", from.getId())
            .bind("money", money)
            .execute();

         h.createUpdate(UPDATE_BALANCE_TO)
            .bind("accountId", to.getId())
            .bind("money", money)
            .execute();
      });
   }

   @Override
   public void deposit(int accountId, BigDecimal money) {
      transfer(Bank.ACCOUNT_PUT_MONEY, accountId, money, "Deposit cash");
   }

   @Override
   public void withdraw(int accountId, BigDecimal money) {
      transfer(accountId, Bank.ACCOUNT_WITHDRAW_MONEY, money, "Withdraw cash");
   }

   @Override
   public List<Transfer> listTransfers(int accountId) {
      return jdbi.withHandle(h -> h.createQuery(SELECT_TRANSFERS_FOR_ACCOUNT)
         .bind("accountId", accountId)
         .mapToBean(Transfer.class)
         .list()
      );
   }
}
