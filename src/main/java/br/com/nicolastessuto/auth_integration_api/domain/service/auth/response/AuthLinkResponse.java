package br.com.nicolastessuto.auth_integration_api.domain.service.auth.response;

import lombok.Builder;

@Builder
public record AuthLinkResponse(String link) {

}
