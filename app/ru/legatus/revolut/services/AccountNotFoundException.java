package ru.legatus.revolut.services;

public class AccountNotFoundException extends RevolutBankException {
   public AccountNotFoundException(int accountId) {
      super("Account with id " + accountId + " doesn't exist");
   }
}
