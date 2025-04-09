package br.com.nicolastessuto.auth_integration_api.domain.service.contact;

import br.com.nicolastessuto.auth_integration_api.domain.service.contact.request.ContactRequest;
import br.com.nicolastessuto.auth_integration_api.domain.service.contact.response.AvailableTargetsResponse;
import br.com.nicolastessuto.auth_integration_api.domain.service.contact.response.ContactResponse;
import br.com.nicolastessuto.auth_integration_api.domain.service.contact.response.integration.ContactIntegrationCallback;

import java.util.List;


public interface ContactService {
    ContactResponse createNewContact(ContactRequest contactRequest, String target, String authorization);

    AvailableTargetsResponse getAvailableTargets();

    Void receiveAndLogContactCreationCallback(List<ContactIntegrationCallback> contactResultIntegration);

}
