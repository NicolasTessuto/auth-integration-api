package br.com.nicolastessuto.auth_integration_api.domain.service.auth.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AvailableProvidersResponse {

    private List<String> providers;

}
