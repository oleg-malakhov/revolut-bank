package ru.legatus.revolut.services;

public class NotEnoughMoneyException extends RevolutBankException {
   public NotEnoughMoneyException(String message) {
      super(message);
   }
}
