package ru.legatus.revolut.controllers;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import play.libs.Json;
import play.mvc.Result;
import play.test.Helpers;
import ru.legatus.revolut.services.RevolutBankException;

public class Response {

   private final JsonNode jsonBody;


   Response(Result result) {
      this.jsonBody = Json.parse(Helpers.contentAsString(result));
   }

   int status() {
      return jsonBody.findPath("status").intValue();
   }

   String message() {
      return jsonBody.findPath("message").asText();
   }

   <T> T entityOf(Class<T> clazz) {
      JsonNode body = jsonBody.findPath("body");
      return Json.fromJson(body, clazz);
   }

   <T> List<T> listOf(Class<T> clazz) {
      JsonNode body = jsonBody.findPath("body");

      ObjectMapper mapper = Json.mapper();
      CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, clazz);

      try {
         return mapper.readValue(Json.stringify(body), collectionType);
      } catch (IOException e) {
         throw new RevolutBankException("Exception in Tests", e);
      }
   }


}
