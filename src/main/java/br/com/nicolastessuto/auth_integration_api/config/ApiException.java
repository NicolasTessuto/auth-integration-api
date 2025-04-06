package br.com.nicolastessuto.auth_integration_api.config;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ApiException {
    private String message;
    private LocalDateTime date;
}
