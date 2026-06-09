package com.company.auth.service;

import com.company.auth.common.constant.AuthConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final RegisteredClientRepository clientRepository;

    @SuppressWarnings("unchecked")
    @RabbitListener(queues = AuthConstants.QUEUE_APP_APPROVED)
    public void handleAppApproved(Map<String, Object> event) {
        String clientId = (String) event.get("clientId");
        String clientSecret = (String) event.get("clientSecret");
        String appName = (String) event.get("appName");
        List<String> redirectUris = (List<String>) event.get("redirectUris");
        List<String> scopes = (List<String>) event.get("scopes");
        List<String> grantTypes = (List<String>) event.get("grantTypes");

        RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(clientId)
                .clientSecret(clientSecret)
                .clientName(appName)
                .redirectUris(uris -> uris.addAll(redirectUris))
                .scopes(s -> s.addAll(scopes))
                .authorizationGrantTypes(types -> types.addAll(
                        grantTypes.stream().map(gt -> switch (gt) {
                            case "authorization_code" -> AuthorizationGrantType.AUTHORIZATION_CODE;
                            case "client_credentials" -> AuthorizationGrantType.CLIENT_CREDENTIALS;
                            case "refresh_token" -> AuthorizationGrantType.REFRESH_TOKEN;
                            default -> throw new IllegalArgumentException("Unknown grant type: " + gt);
                        }).toList()
                ))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .build();

        clientRepository.save(client);
    }

    @RabbitListener(queues = AuthConstants.QUEUE_APP_REVOKED)
    public void handleAppRevoked(Map<String, Object> event) {
        String clientId = (String) event.get("clientId");
        RegisteredClient client = clientRepository.findByClientId(clientId);
        if (client != null) {
            clientRepository.save(RegisteredClient.from(client).build());
        }
    }
}
