package ru.legatus.revolut.model;

import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Client {

   private int id;

   private String passport;

   private String name;

   private String surname;

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getPassport() {
      return passport;
   }

   public void setPassport(String passport) {
      this.passport = passport;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getSurname() {
      return surname;
   }

   public void setSurname(String surname) {
      this.surname = surname;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }
      Client client = (Client) o;
      return id == client.id &&
         Objects.equals(passport, client.passport) &&
         Objects.equals(name, client.name) &&
         Objects.equals(surname, client.surname);
   }

   @Override
   public int hashCode() {

      return Objects.hash(id, passport, name, surname);
   }

   @Override
   public String toString() {
      return new ToStringBuilder(this)
         .append("id", id)
         .append("passport", passport)
         .append("name", name)
         .append("surname", surname)
         .toString();
   }
}
