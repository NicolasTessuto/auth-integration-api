package br.com.nicolastessuto.auth_integration_api.domain.service.contact.impl;

import br.com.nicolastessuto.auth_integration_api.domain.contact.Target;
import br.com.nicolastessuto.auth_integration_api.domain.service.contact.ContactFactory;
import br.com.nicolastessuto.auth_integration_api.domain.service.contact.ContactService;
import br.com.nicolastessuto.auth_integration_api.domain.service.contact.GenericContactClient;
import br.com.nicolastessuto.auth_integration_api.domain.service.contact.request.ContactRequest;
import br.com.nicolastessuto.auth_integration_api.domain.service.contact.response.AvailableTargetsResponse;
import br.com.nicolastessuto.auth_integration_api.domain.service.contact.response.ContactResponse;
import br.com.nicolastessuto.auth_integration_api.domain.service.contact.response.integration.ContactIntegrationCallback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    @Override
    public ContactResponse createNewContact(ContactRequest contactRequest, String targetRequest, String authorization) {
        GenericContactClient genericContactClient = validateProviderAndGetContactClient(targetRequest);
        return genericContactClient.createContact(contactRequest, authorization);
    }

    @Override
    public AvailableTargetsResponse getAvailableTargets() {
        List<String> targets = new ArrayList<>();

        for (Target target : Target.values()) {
            targets.add(target.name());
        }
        return AvailableTargetsResponse.builder()
                .targets(targets)
                .build();
    }

    @Override
    public Void receiveAndLogContactCreationCallback(List<ContactIntegrationCallback> contactResultIntegration) {
        log.info("-=-=-=-=-NEW CONTACT CREATED-=-=-=-=-");
        log.info(contactResultIntegration.toString());
        log.info("-=-=-=-=-NEW CONTACT CREATED-=-=-=-=-");
        return null;
    }

    private GenericContactClient validateProviderAndGetContactClient(String targetRequest) {
        Target target = validateAndGetTarget(targetRequest);
        return ContactFactory.getContactServiceProvider(target);
    }

    private Target validateAndGetTarget(String targetRequest) {
        try {
            return Target.valueOf(targetRequest.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Provider not supported");
        }
    }

}
