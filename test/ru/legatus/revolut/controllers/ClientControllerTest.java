package ru.legatus.revolut.controllers;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;
import ru.legatus.revolut.model.Client;

import static org.assertj.core.api.Assertions.assertThat;

public class ClientControllerTest extends AbstractControllerTest {

   @Test
   public void shouldReturnNoClients() {
      Response response = get("/client/");
      assertThat(response.listOf(Client.class)).hasSize(0);
   }

   @Test
   public void shouldCreateAndGetClient() {
      Response response = post(
         "/client/",
         "{ 'passport': '12341234', 'name': 'Jane', 'surname': 'Brown' }"
      );

      Client created = response.entityOf(Client.class);
      assertThat(created.getId()).isEqualTo(2);
      assertThat(created.getPassport()).isEqualTo("12341234");
      assertThat(created.getName()).isEqualTo("Jane");
      assertThat(created.getSurname()).isEqualTo("Brown");

      Client gotClient = get("/client/2").entityOf(Client.class);
      assertThat(gotClient).isEqualToComparingFieldByField(created);

      List<Client> listedClients = get("/client/").listOf(Client.class);
      assertThat(listedClients).hasSize(1);
      assertThat(listedClients.get(0)).isEqualToComparingFieldByField(created);
   }

   @Test
   public void shouldRerurnListOfClients() {
      post("/client/",
         "{ 'passport': '12341234', 'name': 'Jane', 'surname': 'Brown' }"
      );

      post("/client/",
         "{ 'passport': '43214321', 'name': 'Jack', 'surname': 'Black' }"
      );

      List<Client> clients = get("/client/").listOf(Client.class);
      assertThat(clients.size()).isEqualTo(2);
      assertThat(clients).contains(
         get("/client/2").entityOf(Client.class),
         get("/client/3").entityOf(Client.class)
      );
   }

   @Test
   public void shouldFindClientByPassport() {
      post("/client/",
         "{ 'passport': '12341234', 'name': 'Jane', 'surname': 'Brown' }"
      );

      post("/client/",
         "{ 'passport': '43214321', 'name': 'Jack', 'surname': 'Black' }"
      );

      Client client = get("/client/find?passport=12341234").entityOf(Client.class);
      assertThat(client).isEqualToComparingFieldByField(
         get("/client/2").entityOf(Client.class)
      );

      Client unexisting = get("/client/find?passport=432343", Http.Status.NOT_FOUND).entityOf(Client.class);
      assertThat(unexisting).isNull();
   }

}
