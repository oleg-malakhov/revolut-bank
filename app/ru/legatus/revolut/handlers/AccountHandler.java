package ru.legatus.revolut.handlers;

import java.util.List;
import java.util.concurrent.CompletionStage;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import play.mvc.Http;
import ru.legatus.revolut.controllers.ApiResponse;
import ru.legatus.revolut.model.Account;
import ru.legatus.revolut.services.AccountNotFoundException;
import ru.legatus.revolut.services.AccountService;
import ru.legatus.revolut.services.ClientNotFoundException;
import ru.legatus.revolut.services.jdbi.DatabaseExecutionContext;

@Singleton
public class AccountHandler {

   private final DatabaseExecutionContext dbEc;
   private final AccountService accountService;

   @Inject
   public AccountHandler(DatabaseExecutionContext dbEc, AccountService accountService) {
      this.dbEc = dbEc;
      this.accountService = accountService;
   }

   public CompletionStage<ApiResponse<Account>> createAccount(int clientId, String accountName) {
      return dbEc.call(() -> {
         try {
            Account account = accountService.createAccount(clientId, accountName);
            return new ApiResponse<>(Http.Status.OK, "Account has been created", account);
         } catch (ClientNotFoundException ex) {
            return new ApiResponse<>(Http.Status.NOT_FOUND, ex.getMessage(), null);
         }
      });
   }

   public CompletionStage<ApiResponse<Account>> getAccount(int accountId) {
      return dbEc.call(() -> {
         try {
            Account account = accountService.get(accountId);
            return new ApiResponse<>(Http.Status.OK, "Account data", account);
         } catch (AccountNotFoundException ex) {
            return new ApiResponse<>(Http.Status.NOT_FOUND, ex.getMessage(), null);
         }
      });
   }

   public CompletionStage<ApiResponse<List<Account>>> listAccountsForClient(int clientId) {
      return dbEc.call(() -> {
         try {
            List<Account> accounts = accountService.listByClient(clientId);
            return new ApiResponse<>(Http.Status.OK, "Accounts are received", accounts);
         } catch (ClientNotFoundException ex) {
            return new ApiResponse<>(Http.Status.NOT_FOUND, ex.getMessage(), null);
         }
      });
   }
}
