package br.com.nicolastessuto.auth_integration_api.domain.service.contact.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AvailableTargetsResponse {

    private List<String> targets;

}
