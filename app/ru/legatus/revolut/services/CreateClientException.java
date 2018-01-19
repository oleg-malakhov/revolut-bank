package ru.legatus.revolut.services;

public class CreateClientException extends RevolutBankException {
   public CreateClientException(String message) {
      super(message);
   }

   public CreateClientException(String message, Throwable cause) {
      super(message, cause);
   }
}
