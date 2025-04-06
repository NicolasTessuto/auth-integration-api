package br.com.nicolastessuto.auth_integration_api.domain.rest;

import br.com.nicolastessuto.auth_integration_api.domain.service.auth.AuthService;
import br.com.nicolastessuto.auth_integration_api.domain.service.auth.response.AuthLinkResponse;
import br.com.nicolastessuto.auth_integration_api.domain.service.auth.response.AvailableProvidersResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/available-providers")
    public ResponseEntity<AvailableProvidersResponse> getAvailableProvidersResponse() {
        return ResponseEntity.ok(authService.getAvailableProviders());
    }

    @GetMapping("/login-url")
    public ResponseEntity<AuthLinkResponse> getAuthLink(@RequestParam String provider) {
        return ResponseEntity.ok(authService.getAuthLink(provider));
    }

}
