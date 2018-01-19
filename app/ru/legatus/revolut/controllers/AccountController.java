package ru.legatus.revolut.controllers;

import java.util.concurrent.CompletionStage;

import com.google.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;

import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import ru.legatus.revolut.handlers.AccountHandler;

public class AccountController extends Controller {

   private final HttpExecutionContext ec;
   private final AccountHandler accountHandler;

   @Inject
   public AccountController(HttpExecutionContext ec, AccountHandler accountHandler) {
      this.ec = ec;
      this.accountHandler = accountHandler;
   }

   public CompletionStage<Result> createAccount() {
      JsonNode request = request().body().asJson();
      CreateAccount data = Json.fromJson(request, CreateAccount.class);

      return accountHandler.createAccount(data.clientId, data.accountName).thenApplyAsync(U::jsonResult, ec.current());
   }

   public CompletionStage<Result> getAccount(Integer accountId) {
      return accountHandler.getAccount(accountId).thenApplyAsync(U::jsonResult, ec.current());
   }

   public CompletionStage<Result> listForClient(Integer clientId) {
      return accountHandler.listAccountsForClient(clientId).thenApplyAsync(U::jsonResult, ec.current());
   }


   private static class CreateAccount {
      private int clientId;
      private String accountName;

      public void setClientId(int clientId) {
         this.clientId = clientId;
      }

      public void setAccountName(String accountName) {
         this.accountName = accountName;
      }
   }
}
