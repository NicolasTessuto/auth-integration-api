package br.com.nicolastessuto.auth_integration_api.domain.service.contact;

import br.com.nicolastessuto.auth_integration_api.domain.contact.Target;
import br.com.nicolastessuto.auth_integration_api.domain.service.contact.client.HubspotContactClient;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ContactFactory {

    private static ApplicationContext context;

    public ContactFactory(ApplicationContext context) {
        ContactFactory.context = context;
    }

    public static GenericContactClient getContactServiceProvider(Target target) {
        return switch (target) {
            case HUBSPOT -> context.getBean(HubspotContactClient.class);
        };
    }

}
