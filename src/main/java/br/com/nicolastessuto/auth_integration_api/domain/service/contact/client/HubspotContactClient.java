package br.com.nicolastessuto.auth_integration_api.domain.service.contact.client;

import br.com.nicolastessuto.auth_integration_api.domain.service.contact.GenericContactClient;
import br.com.nicolastessuto.auth_integration_api.domain.service.contact.request.ContactDataIntegrationRequest;
import br.com.nicolastessuto.auth_integration_api.domain.service.contact.request.ContactIntegrationRequest;
import br.com.nicolastessuto.auth_integration_api.domain.service.contact.request.ContactRequest;
import br.com.nicolastessuto.auth_integration_api.domain.service.contact.response.ContactIntegrationResponse;
import br.com.nicolastessuto.auth_integration_api.domain.service.contact.response.ContactResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class HubspotContactClient implements GenericContactClient {

    private final WebClient webClient;

    private final String CONTACT_BASE_URL = "https://api.hubapi.com/crm/v3/objects/contacts";

    @Override
    public ContactResponse createContact(ContactRequest contactRequest, String authorization) {

        ContactIntegrationRequest contactIntegrationRequest = generateContactIntegrationPayload(contactRequest);

       ContactIntegrationResponse integrationResponse =
               webClient.post()
                       .uri(CONTACT_BASE_URL)
                       .header("Authorization", validateAndUpdate(authorization))
                       .contentType(MediaType.APPLICATION_JSON)
                       .bodyValue(contactIntegrationRequest)
                       .exchangeToMono(this::validateCreateIntegrationResponse)
                       .block();

        return new ContactResponse(integrationResponse);
    }

    private ContactIntegrationRequest generateContactIntegrationPayload(ContactRequest contactRequest) {
        var contactDataIntegrationRequest =
                ContactDataIntegrationRequest.builder()
                        .email(contactRequest.email())
                        .firstName(contactRequest.firstName())
                        .lastName(contactRequest.lastName())
                        .build();

       return ContactIntegrationRequest.builder()
                .properties(contactDataIntegrationRequest)
                .build();
    }

    private String validateAndUpdate(String authorization) {
        if (!authorization.startsWith("Bearer ")) {
            authorization = "Bearer " + authorization;
        }
        return authorization;
    }


    private Mono<ContactIntegrationResponse> validateCreateIntegrationResponse(ClientResponse clientResponse) {
        if (clientResponse.statusCode().is2xxSuccessful()) {
            return clientResponse.bodyToMono(ContactIntegrationResponse.class);
        } else if (clientResponse.statusCode().equals(HttpStatus.TOO_MANY_REQUESTS)) {
            return Mono.error(new RuntimeException("Limite de requisições excedido (429)"));
        } else {
            return Mono.error(new RuntimeException("Erro ao criar contato"));
        }
    }

}
