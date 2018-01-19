package ru.legatus.revolut.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractControllerTest extends WithApplication {
   private static final Logger LOG = LoggerFactory.getLogger(AbstractControllerTest.class);

   Response get(String uri) {
      return make(H.get(uri), Http.Status.OK);
   }

   Response get(String uri, int statusCode) {
      return make(H.get(uri), statusCode);
   }

   Response post(String uri, String body) {
      return make(H.post(uri, body), Http.Status.OK);
   }

   Response post(String uri, String body, int statusCode) {
      return make(H.post(uri, body), statusCode);
   }

   private Response make(Http.RequestBuilder request, int expectedStatusCode) {
      Result result = Helpers.route(app, request);
      Response response = new Response(result);

      LOG.info("Request body: {}", Helpers.contentAsString(result));
      assertThat(result.status()).isEqualTo(expectedStatusCode);

      assertThat(response.status()).isEqualTo(expectedStatusCode);
      return response;
   }
}
