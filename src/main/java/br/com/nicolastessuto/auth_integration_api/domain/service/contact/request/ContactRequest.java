package br.com.nicolastessuto.auth_integration_api.domain.service.contact.request;

public record ContactRequest(
        String email,
        String firstName,
        String lastName
) {
}
