package ru.legatus.revolut.services;

import org.junit.Before;
import org.junit.Test;

import play.test.WithApplication;
import ru.legatus.revolut.model.Bank;
import ru.legatus.revolut.model.Client;

import static org.assertj.core.api.Assertions.assertThat;

public class ClientServiceTest extends WithApplication {

   private ClientService clientService;

   @Before
   public void setupServices() {
      clientService = instanceOf(ClientService.class);
   }

   @Test
   public void shouldCreateClientServiceWithBankRecord() {
      Client client = clientService.get(Bank.ID);

      assertThat(client).isNotNull();

      assertThat(client.getId()).isEqualTo(Bank.ID);
      assertThat(client.getPassport()).isEqualTo(Bank.PS);
      assertThat(client.getName()).isEqualTo(Bank.NAME);
      assertThat(client.getSurname()).isEqualTo(Bank.NAME);
   }

   @Test(expected = CreateClientException.class)
   public void shouldPreventClientsWithTheSamePassport() {
      clientService.create("12345", "John", "Smith");
      clientService.create("12345", "John Second", "Smith");
   }
}
