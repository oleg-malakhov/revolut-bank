package ru.legatus.revolut.controllers;

public class ApiResponse<T> {

   public final int status;

   public final String message;

   public final T body;

   public ApiResponse(int status, String message, T body) {
      this.status = status;
      this.message = message;
      this.body = body;
   }
}
