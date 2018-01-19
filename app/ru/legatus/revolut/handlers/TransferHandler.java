package ru.legatus.revolut.handlers;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletionStage;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

import play.mvc.Http;
import ru.legatus.revolut.controllers.ApiResponse;
import ru.legatus.revolut.model.Account;
import ru.legatus.revolut.model.Transfer;
import ru.legatus.revolut.services.AccountNotFoundException;
import ru.legatus.revolut.services.AccountService;
import ru.legatus.revolut.services.NotEnoughMoneyException;
import ru.legatus.revolut.services.TransferService;
import ru.legatus.revolut.services.jdbi.DatabaseExecutionContext;

public class TransferHandler {

   private final DatabaseExecutionContext dbEc;
   private final TransferService transferService;
   private final AccountService accountService;

   @Inject
   public TransferHandler(DatabaseExecutionContext dbEc, TransferService transferService, AccountService accountService) {
      this.dbEc = dbEc;
      this.transferService = transferService;
      this.accountService = accountService;
   }

   public CompletionStage<ApiResponse<Account>> deposit(int accountId, BigDecimal money) {
      return dbEc.call(() -> {
         try {
            transferService.deposit(accountId, money);
            Account updated = accountService.get(accountId);
            return new ApiResponse<>(Http.Status.OK, "Deposit complete", updated);
         } catch (AccountNotFoundException ex) {
            return new ApiResponse<>(Http.Status.NOT_FOUND, ex.getMessage(), null);
         }
      });
   }

   public CompletionStage<ApiResponse<Account>> withdraw(int accountId, BigDecimal money) {
      return dbEc.call(() -> {
         try {
            transferService.withdraw(accountId, money);
            Account updated = accountService.get(accountId);
            return new ApiResponse<>(Http.Status.OK, "Withdraw complete", updated);
         } catch (AccountNotFoundException ex) {
            return new ApiResponse<>(Http.Status.NOT_FOUND, ex.getMessage(), null);
         } catch (NotEnoughMoneyException ex) {
            return new ApiResponse<>(Http.Status.NOT_ACCEPTABLE, ex.getMessage(), null);
         }
      });
   }

   public CompletionStage<ApiResponse<List<Account>>> transfer(int accountIdFrom, int accountIdTo, BigDecimal money) {
      return dbEc.call(() -> {
         try {
            String message = String.format("Transfer from Account[%d] to Account[%d]", accountIdFrom, accountIdTo);
            transferService.transfer(accountIdFrom, accountIdTo, money, message);
            Account updatedFrom = accountService.get(accountIdFrom);
            Account updatedTo = accountService.get(accountIdTo);
            return new ApiResponse<>(Http.Status.OK, "Withdraw complete", Lists.newArrayList(updatedFrom, updatedTo));
         } catch (AccountNotFoundException ex) {
            return new ApiResponse<>(Http.Status.NOT_FOUND, ex.getMessage(), null);
         } catch (NotEnoughMoneyException ex) {
            return new ApiResponse<>(Http.Status.NOT_ACCEPTABLE, ex.getMessage(), null);
         }
      });
   }

   public CompletionStage<ApiResponse<List<Transfer>>> transferList(int accountId) {
      return dbEc.call(() -> {
         try {
            List<Transfer> transfers = transferService.listTransfers(accountId);
            return new ApiResponse<>(Http.Status.OK, "Transfers are received", transfers);
         } catch (AccountNotFoundException ex) {
            return new ApiResponse<>(Http.Status.NOT_FOUND, ex.getMessage(), null);
         }
      });
   }
}
