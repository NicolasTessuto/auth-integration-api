package br.com.nicolastessuto.auth_integration_api.domain.service.contact.response;

public record ContactResponse(
        String email,
        String firstName,
        String lastName
)
{
    public ContactResponse(ContactIntegrationResponse contactDataIntegrationResponse){
        this(
                contactDataIntegrationResponse.properties().email(),
                contactDataIntegrationResponse.properties().firstName(),
                contactDataIntegrationResponse.properties().lastName()
        );
    }

}
