package br.com.nicolastessuto.auth_integration_api.domain.service.auth.response;

import lombok.Builder;

@Builder
public record TokenResponse(String token) {

}
