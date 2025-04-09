package br.com.nicolastessuto.auth_integration_api.domain.rest.contact;

import br.com.nicolastessuto.auth_integration_api.domain.service.contact.ContactService;
import br.com.nicolastessuto.auth_integration_api.domain.service.contact.request.ContactRequest;
import br.com.nicolastessuto.auth_integration_api.domain.service.contact.response.AvailableTargetsResponse;
import br.com.nicolastessuto.auth_integration_api.domain.service.contact.response.ContactResponse;
import br.com.nicolastessuto.auth_integration_api.domain.service.contact.response.integration.ContactIntegrationCallback;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/contacts")
public class ContactController {

    private final ContactService contactService;

    @GetMapping("/available-targets")
    public ResponseEntity<AvailableTargetsResponse> getAvailableTargetsResponse() {
        return ResponseEntity.ok(contactService.getAvailableTargets());
    }

    @PostMapping
    public ResponseEntity<ContactResponse> createNewContact(@RequestBody ContactRequest contactRequest,
                                                            @RequestParam(defaultValue = "HUBSPOT") String target,
                                                            @RequestHeader(value = "Authorization") String authorization) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contactService.createNewContact(contactRequest, target, authorization));
    }

    @PostMapping("/callback")
    public ResponseEntity<Void> receiveCreateContactCallback(@RequestBody List<ContactIntegrationCallback> contactResultIntegration) {
        return ResponseEntity.ok().body(contactService.receiveAndLogContactCreationCallback(contactResultIntegration));
    }

}
