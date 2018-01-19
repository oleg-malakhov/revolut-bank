package ru.legatus.revolut.handlers;

import java.util.List;
import java.util.concurrent.CompletionStage;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import play.mvc.Http;
import ru.legatus.revolut.controllers.ApiResponse;
import ru.legatus.revolut.model.Client;
import ru.legatus.revolut.services.ClientNotFoundException;
import ru.legatus.revolut.services.ClientService;
import ru.legatus.revolut.services.CreateClientException;
import ru.legatus.revolut.services.jdbi.DatabaseExecutionContext;

@Singleton
public class ClientHandler {

   private final DatabaseExecutionContext dbEc;

   private final ClientService clientService;

   @Inject
   public ClientHandler(DatabaseExecutionContext dbEc, ClientService clientService) {
      this.dbEc = dbEc;
      this.clientService = clientService;
   }

   public CompletionStage<ApiResponse<Client>> createClient(Client client) {
      return dbEc.call(() -> {
         try {
            Client createdClient = clientService.create(client.getPassport(), client.getName(), client.getSurname());
            return new ApiResponse<>(Http.Status.OK, "Client has been created", createdClient);
         } catch (CreateClientException ex) {
            return new ApiResponse<>(Http.Status.NOT_ACCEPTABLE, ex.getMessage(), null);
         }
      });
   }

   public CompletionStage<ApiResponse<Client>> update(int clientId, Client clientData) {
      return dbEc.call(() -> {
         try {
            Client updatedClient = clientService.update(clientId, clientData);
            return new ApiResponse<>(Http.Status.OK, "Client has been updated", updatedClient);
         } catch (ClientNotFoundException ex) {
            return new ApiResponse<>(Http.Status.NOT_FOUND, ex.getMessage(), null);
         }
      });
   }

   public CompletionStage<ApiResponse<Client>> getClient(int id) {
      return dbEc.call(() -> {
         try {
            Client client = clientService.get(id);
            return new ApiResponse<>(Http.Status.OK, "Client has been found", client);
         } catch (ClientNotFoundException ex) {
            return new ApiResponse<>(Http.Status.NOT_FOUND, ex.getMessage(), null);
         }
      });
   }

   public CompletionStage<ApiResponse<Client>> findByPassport(String passport) {
      return dbEc.call(() -> clientService.findByPassport(passport)
         .map(client -> new ApiResponse<>(Http.Status.OK, "Client has been found", client))
         .orElse(new ApiResponse<>(Http.Status.NOT_FOUND, "Client not found", null)));
   }

   public CompletionStage<ApiResponse<List<Client>>> listClients() {
      return dbEc.call(() -> {
         List<Client> clients = clientService.list();
         return new ApiResponse<>(Http.Status.OK, "List of clients", clients);
      });
   }
}
