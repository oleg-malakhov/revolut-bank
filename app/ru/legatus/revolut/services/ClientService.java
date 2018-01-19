package ru.legatus.revolut.services;

import java.util.List;
import java.util.Optional;

import ru.legatus.revolut.model.Client;

public interface ClientService {

   /**
    * Creates a new client
    * @param passport Client's passport number
    * @param name Client's name
    * @param surname Client's surname
    * @return new {@link Client} instance with assigned ID.
    * @throws CreateClientException if a client with such passport number already exists.
    */
   Client create(String passport, String name, String surname);

   /**
    * Update Client's data
    * @param clientId id of the Client to be updated.
    * @param clientData {@link Client} instance with new data (id is ignored)
    * @return {@link Client} instance with updated data
    * @throws ClientNotFoundException if clients with such id is not found
    */
   Client update(int clientId, Client clientData);

   /**
    * Gets client by id
    * @param id Client's id
    * @return an existing Client
    * @throws ClientNotFoundException if a client with the given id doesn't exist
    */
   Client get(int id);

   /**
    * Tries to find a Client by passport number
    * @param passport Client's passport number
    * @return {@link Optional} with the found Client or empty
    */
   Optional<Client> findByPassport(String passport);

   /**
    * Returns all Clients except the one reserved for Bank (with id=1)
    * @return list of found Clients or empty list.
    */
   List<Client> list();
}
