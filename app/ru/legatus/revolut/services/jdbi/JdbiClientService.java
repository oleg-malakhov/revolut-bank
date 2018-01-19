package ru.legatus.revolut.services.jdbi;

import java.util.List;
import java.util.Optional;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.transaction.TransactionIsolationLevel;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

import ru.legatus.revolut.model.Client;
import ru.legatus.revolut.services.ClientNotFoundException;
import ru.legatus.revolut.services.ClientService;
import ru.legatus.revolut.services.CreateClientException;

public class JdbiClientService implements ClientService {

   private static final String SELECT_ALL =
      "SELECT id, passport, name, surname " +
         "FROM clients " +
         "WHERE id > 1";

   private static final String SELECT_BY_ID =
      "SELECT id, passport, name, surname " +
         "FROM clients " +
         "WHERE id = :id";

   private static final String SELECT_BY_PASSPORT =
      "SELECT id, passport, name, surname " +
         "FROM clients " +
         "WHERE passport = :passport";

   private static final String CREATE_CLIENT =
      "INSERT INTO clients(passport, name, surname) " +
         "VALUES(:passport, :name, :surname)";

   private static final String UPDATE_CLIENT =
      "UPDATE clients " +
         "SET passport = :passport, name = :name, surname = :surname " +
         "WHERE id = :clientId";

   private static final String CHECK_CLIENT =
      "SELECT count(id) FROM clients WHERE passport = :passport";

   private final Jdbi jdbi;

   @Inject
   public JdbiClientService(Jdbi jdbi) {
      this.jdbi = jdbi;
   }

   public Client create(String passport, String name, String surname) {

      jdbi.useTransaction(TransactionIsolationLevel.REPEATABLE_READ,
         handle -> {

         int found = handle.createQuery(CHECK_CLIENT)
            .bind("passport", passport)
            .mapTo(Integer.class)
            .findOnly();

         if(found > 0) {
            String message = String.format("Client with passport '%s' already exists", passport);
            throw new CreateClientException(message);
         }

         handle.createUpdate(CREATE_CLIENT)
            .bindMap(ImmutableMap.of(
               "passport", passport,
               "name", name,
               "surname", surname
            )).execute();
      });

      return findByPassport(passport).orElseThrow(() -> new CreateClientException("Couldn't find created Client"));
   }

   @Override
   public Client update(int clientId, Client clientData) {
      jdbi.useTransaction(TransactionIsolationLevel.REPEATABLE_READ, h -> {
         get(clientId);

         h.createUpdate(UPDATE_CLIENT)
            .bind("clientId", clientId)
            .bind("passport", clientData.getPassport())
            .bind("name", clientData.getName())
            .bind("surname", clientData.getSurname())
            .execute();
      });

      return get(clientId);
   }

   @Override
   public Client get(int id) {
      try {
         return jdbi.withHandle(h -> getInternal(id, h)
         );
      } catch (IllegalStateException ex) {
         throw new ClientNotFoundException(id);
      }
   }

   Client getInternal(int id, Handle h) {
      return h.createQuery(SELECT_BY_ID)
         .bind("id", id)
         .mapToBean(Client.class)
         .findOnly();
   }

   public Optional<Client> findByPassport(String passport) {
      return jdbi.withHandle(h -> h.createQuery(SELECT_BY_PASSPORT)
         .bind("passport", passport)
         .mapToBean(Client.class)
         .findFirst()
      );
   }

   public List<Client> list() {
      return jdbi.withHandle(handle -> handle.createQuery(SELECT_ALL)
         .mapToBean(Client.class)
         .list());
   }
}
