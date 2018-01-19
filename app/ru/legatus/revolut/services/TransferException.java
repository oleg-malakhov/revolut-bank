package ru.legatus.revolut.services;

public class TransferException extends RevolutBankException {
   public TransferException(String message) {
      super(message);
   }
}
