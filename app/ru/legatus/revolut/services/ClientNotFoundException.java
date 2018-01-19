package ru.legatus.revolut.services;

public class ClientNotFoundException extends RevolutBankException {

   public ClientNotFoundException(int clientId) {
      super("Client with id " + clientId + " doesn't exist");
   }
}
