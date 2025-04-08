package br.com.nicolastessuto.auth_integration_api.config.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class HubspotIntegrationException extends ResponseStatusException {
    public HubspotIntegrationException(String message, HttpStatusCode httpStatusCode) {
        super(httpStatusCode, message);
    }
}
