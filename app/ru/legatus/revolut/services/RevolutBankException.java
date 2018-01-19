package ru.legatus.revolut.services;

public class RevolutBankException extends RuntimeException {

   public RevolutBankException(String message) {
      super(message);
   }

   public RevolutBankException(String message, Throwable cause) {
      super(message, cause);
   }
}
