package ru.legatus.revolut.controllers;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import play.libs.Json;
import play.mvc.Result;

import static play.mvc.Results.status;

final class U {

   static <T> Result jsonResult(ApiResponse<T> answer) {
      Json.mapper().registerModule(new JavaTimeModule());
      Json.mapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
      return status(answer.status, Json.toJson(answer));
   }

   private U() {
   }
}
