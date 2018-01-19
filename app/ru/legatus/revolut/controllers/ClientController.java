package ru.legatus.revolut.controllers;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.fasterxml.jackson.databind.JsonNode;

import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import ru.legatus.revolut.handlers.ClientHandler;
import ru.legatus.revolut.model.Client;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Singleton
public class ClientController extends Controller {

   private final HttpExecutionContext ec;
   private final ClientHandler clientHandler;

   @Inject
   public ClientController(HttpExecutionContext ec, ClientHandler clientHandler) {
      this.ec = ec;
      this.clientHandler = clientHandler;
   }

   public CompletionStage<Result> createClient() {
      JsonNode json = request().body().asJson();
      Client client = Json.fromJson(json, Client.class);

      return clientHandler.createClient(client).thenApplyAsync(U::jsonResult, ec.current());
   }

   public CompletionStage<Result> updateClient(Integer id) {
      JsonNode json = request().body().asJson();
      Client clientData = Json.fromJson(json, Client.class);

      return clientHandler.update(id, clientData).thenApplyAsync(U::jsonResult, ec.current());
   }

   public CompletionStage<Result> findClient(String passport) {
      if(isBlank(passport)) {
         CompletableFuture.runAsync(() -> U.jsonResult(new ApiResponse<>(
            Http.Status.NOT_ACCEPTABLE,
            "Blank passport number",
            null
         )));
      }
      return clientHandler.findByPassport(passport).thenApplyAsync(U::jsonResult, ec.current());
   }

   public CompletionStage<Result> listClients() {
      return clientHandler.listClients().thenApplyAsync(U::jsonResult, ec.current());
   }

   public CompletionStage<Result> getClient(Integer id) {
      return clientHandler.getClient(id).thenApplyAsync(U::jsonResult, ec.current());
   }


}
