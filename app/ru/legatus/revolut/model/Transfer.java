package ru.legatus.revolut.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transfer {

   private LocalDateTime timestamp;
   private BigDecimal money;
   private String message;

   public LocalDateTime getTimestamp() {
      return timestamp;
   }

   public void setTimestamp(LocalDateTime timestamp) {
      this.timestamp = timestamp;
   }

   public BigDecimal getMoney() {
      return money;
   }

   public void setMoney(BigDecimal money) {
      this.money = money;
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }
}
