package ru.legatus.revolut.controllers;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import play.mvc.Http;
import play.test.Helpers;
import ru.legatus.revolut.services.RevolutBankException;

final class H {

   static Http.RequestBuilder post(String uri, String body) {
      return Helpers.fakeRequest()
         .method("POST")
         .uri(uri)
         .bodyText(json(body))
         .header(Http.HeaderNames.CONTENT_TYPE, Http.MimeTypes.JSON);
   }

   static Http.RequestBuilder get(String uri) {
      return Helpers.fakeRequest()
         .method("GET")
         .uri(uri);
   }

   static String json(String s) {
      return StringUtils.replaceAll(s, "'", "\"");
   }

   private H() {
   }
}
