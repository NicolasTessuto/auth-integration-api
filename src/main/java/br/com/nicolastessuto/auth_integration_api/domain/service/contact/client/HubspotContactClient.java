package br.com.nicolastessuto.auth_integration_api.domain.service.contact.client;

import br.com.nicolastessuto.auth_integration_api.config.exception.HubspotIntegrationException;
import br.com.nicolastessuto.auth_integration_api.domain.service.contact.GenericContactClient;
import br.com.nicolastessuto.auth_integration_api.domain.service.contact.request.ContactDataIntegrationRequest;
import br.com.nicolastessuto.auth_integration_api.domain.service.contact.request.ContactIntegrationMessageRequest;
import br.com.nicolastessuto.auth_integration_api.domain.service.contact.request.ContactIntegrationRequest;
import br.com.nicolastessuto.auth_integration_api.domain.service.contact.request.ContactRequest;
import br.com.nicolastessuto.auth_integration_api.domain.service.contact.response.ContactDataIntegrationResponse;
import br.com.nicolastessuto.auth_integration_api.domain.service.contact.response.ContactIntegrationResponse;
import br.com.nicolastessuto.auth_integration_api.domain.service.contact.response.ContactResponse;
import br.com.nicolastessuto.auth_integration_api.domain.service.rabbitMq.RabbitMqService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubspotContactClient implements GenericContactClient {

    private final WebClient webClient;
    private final RabbitMqService rabbitMqService;

    @Value("${rabbitmq.hubspot-contact-delay-fallback-queue}")
    private String HUBSPOT_FALLBACK_DELAY_QUEUE;

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
                        .exchangeToMono(clientResponse ->
                                validateCreateIntegrationResponse(clientResponse, contactIntegrationRequest, authorization)
                        )
                        .block();

        return new ContactResponse(integrationResponse);
    }

    @Override
    public Void createContact(ContactIntegrationMessageRequest contactRequest) {
        webClient.post()
                .uri(CONTACT_BASE_URL)
                .header("Authorization", validateAndUpdate(contactRequest.authorization()))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(contactRequest)
                .exchangeToMono(clientResponse ->
                        validateOrThrowException(clientResponse, contactRequest.contactIntegration())
                )
                .block();

        return null;
    }

    private Mono<Object> validateOrThrowException(ClientResponse clientResponse, ContactIntegrationRequest contactRequest) {
        if(clientResponse.statusCode().isError()) {
            log.error("Error in trying to resend a contact to hubspot" + contactRequest);
            throw new RuntimeException("Error in trying to resend a contact to hubspot " + clientResponse.statusCode());
        }
        return Mono.just(contactRequest);
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


    private Mono<ContactIntegrationResponse> validateCreateIntegrationResponse(ClientResponse clientResponse,
                                                                               ContactIntegrationRequest contactIntegrationRequest,
                                                                               String authorization) {
        HttpStatusCode status = clientResponse.statusCode();

        switch (status) {
            case OK, CREATED, ACCEPTED -> {
                return clientResponse.bodyToMono(ContactIntegrationResponse.class);
            }
            case TOO_MANY_REQUESTS -> {
                log.warn("Too many requests, sending to queue" + contactIntegrationRequest);
                rabbitMqService.publishObjectInQueue(HUBSPOT_FALLBACK_DELAY_QUEUE, generateNewPayload(contactIntegrationRequest, authorization));
                return generateFakeSuccessIntegrationResponse(contactIntegrationRequest);
            }
            case CONFLICT -> throw new DuplicateKeyException("Contact already registered");
            case UNAUTHORIZED -> throw new HubspotIntegrationException("Unauthorized access", status);
            case PAYMENT_REQUIRED -> throw new HubspotIntegrationException("Payment required", status);
            default -> throw new HubspotIntegrationException("Integration error not captured", status);
        }
    }

    private ContactIntegrationMessageRequest generateNewPayload(ContactIntegrationRequest contactIntegrationRequest, String authorization) {
        return ContactIntegrationMessageRequest.builder()
                .authorization(authorization)
                .contactIntegration(contactIntegrationRequest)
                .build();
    }

    private Mono<ContactIntegrationResponse> generateFakeSuccessIntegrationResponse(ContactIntegrationRequest contactIntegrationRequest) {
        var contactIntegrationFakeDataResponse = ContactDataIntegrationResponse.builder()
                .email(contactIntegrationRequest.properties().email())
                .firstName(contactIntegrationRequest.properties().firstName())
                .lastName(contactIntegrationRequest.properties().lastName())
                .build();

        var contactIntegrationFakeResponse = ContactIntegrationResponse.builder()
                .properties(contactIntegrationFakeDataResponse)
                .build();

        return Mono.just(contactIntegrationFakeResponse);
    }

}
