package br.com.nicolastessuto.auth_integration_api.domain.service.contact;

import br.com.nicolastessuto.auth_integration_api.domain.service.contact.request.ContactRequest;
import br.com.nicolastessuto.auth_integration_api.domain.service.contact.request.integration.ContactIntegrationMessageRequest;
import br.com.nicolastessuto.auth_integration_api.domain.service.contact.response.ContactResponse;

public interface GenericContactClient {

    ContactResponse createContact(ContactRequest contactRequest, String authorization);

    Void createContact(ContactIntegrationMessageRequest contactRequest);
}
