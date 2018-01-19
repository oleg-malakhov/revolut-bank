package ru.legatus.revolut.services;

import java.util.List;

import ru.legatus.revolut.model.Account;

public interface AccountService {

   /**
    * Create new account for the Client
    * @param clientId client id to create account for
    * @param accountName account name
    * @return created {@link Account} instance
    * @throws ClientNotFoundException if a Client with such id is not found.
    */
   Account createAccount(int clientId, String accountName);

   /**
    * Get Account data by Account Id
    * @param accountId Account Id
    * @return {@link Account} instance with data
    *
    */
   Account get(int accountId);

   /**
    * List accounts for the Client with the provided id
    * @param clientId client ID
    * @return list of {@link Account} for the specified client
    * @throws ClientNotFoundException if there's no client with such clientId
    */
   List<Account> listByClient(int clientId);
}
