package ru.legatus.revolut.controllers;

import java.math.BigDecimal;
import java.util.concurrent.CompletionStage;

import com.google.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;

import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import ru.legatus.revolut.handlers.TransferHandler;

public class TransferController extends Controller {

   private final HttpExecutionContext ec;
   private final TransferHandler transferHandler;

   @Inject
   public TransferController(HttpExecutionContext ec, TransferHandler transferHandler) {
      this.ec = ec;
      this.transferHandler = transferHandler;
   }

   public CompletionStage<Result> deposit() {
      JsonNode json = request().body().asJson();
      TransferDetails details = Json.fromJson(json, TransferDetails.class);
      return transferHandler.deposit(details.accountIdTo, details.money)
         .thenApplyAsync(U::jsonResult, ec.current());
   }

   public CompletionStage<Result> withdraw() {
      JsonNode json = request().body().asJson();
      TransferDetails details = Json.fromJson(json, TransferDetails.class);
      return transferHandler.withdraw(details.accountIdFrom, details.money)
         .thenApplyAsync(U::jsonResult, ec.current());
   }

   public CompletionStage<Result> transfer() {
      JsonNode json = request().body().asJson();
      TransferDetails details = Json.fromJson(json, TransferDetails.class);
      return transferHandler.transfer(details.accountIdFrom, details.accountIdTo, details.money)
         .thenApplyAsync(U::jsonResult, ec.current());
   }

   public CompletionStage<Result> transferList(Integer accountId) {
      return transferHandler.transferList(accountId).thenApplyAsync(U::jsonResult, ec.current());
   }


   static class TransferDetails {
      int accountIdFrom;
      int accountIdTo;
      BigDecimal money;

      public void setAccountIdFrom(int accountIdFrom) {
         this.accountIdFrom = accountIdFrom;
      }

      public void setAccountIdTo(int accountIdTo) {
         this.accountIdTo = accountIdTo;
      }

      public void setMoney(BigDecimal money) {
         this.money = money;
      }
   }


}
