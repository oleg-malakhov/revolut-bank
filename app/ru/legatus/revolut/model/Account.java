package ru.legatus.revolut.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Account {

   private int id;

   private int clientId;

   private BigDecimal balance;

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public int getClientId() {
      return clientId;
   }

   public void setClientId(int clientId) {
      this.clientId = clientId;
   }

   public BigDecimal getBalance() {
      return balance;
   }

   public void setBalance(BigDecimal balance) {
      this.balance = balance;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }
      Account account = (Account) o;
      return id == account.id &&
         clientId == account.clientId &&
         Objects.equals(balance, account.balance);
   }

   @Override
   public int hashCode() {

      return Objects.hash(id, clientId, balance);
   }
}
