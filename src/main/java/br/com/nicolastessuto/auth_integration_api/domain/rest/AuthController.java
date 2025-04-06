package br.com.nicolastessuto.auth_integration_api.domain.rest;

import br.com.nicolastessuto.auth_integration_api.domain.service.auth.AuthService;
import br.com.nicolastessuto.auth_integration_api.domain.service.auth.response.AuthLinkResponse;
import br.com.nicolastessuto.auth_integration_api.domain.service.auth.response.AvailableProvidersResponse;
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
    public ResponseEntity<AuthLinkResponse> getAuthLink(@RequestParam String provider) {
        return ResponseEntity.ok(authService.getAuthLink(provider));
    }

    @GetMapping("/callback")
    public ResponseEntity<Void> generateTokens(@RequestParam String code,
                                                                       @RequestParam("state") String provider,
                                                                       HttpServletRequest servletRequest) {
        return authService.generateTokens(code, provider, servletRequest);
    }

}
