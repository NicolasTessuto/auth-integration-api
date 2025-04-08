package br.com.nicolastessuto.auth_integration_api.domain.rest.auth;

import br.com.nicolastessuto.auth_integration_api.domain.service.auth.AuthService;
import br.com.nicolastessuto.auth_integration_api.domain.service.auth.response.AuthLinkResponse;
import br.com.nicolastessuto.auth_integration_api.domain.service.auth.response.AvailableProvidersResponse;
import br.com.nicolastessuto.auth_integration_api.domain.service.auth.response.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<AuthLinkResponse> getAuthLink(@RequestParam(defaultValue = "HUBSPOT") String provider) {
        return ResponseEntity.ok(authService.getAuthLink(provider));
    }

    @GetMapping("/callback")
    public ResponseEntity<TokenResponse> generateTokens(@RequestParam String code,
                                                        @RequestParam("state") String provider,
                                                        HttpServletRequest servletRequest) {
        return ResponseEntity.ok(authService.generateTokens(code, provider, servletRequest));
    }

}
