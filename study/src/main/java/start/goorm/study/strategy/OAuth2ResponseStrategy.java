package start.goorm.study.strategy;

import start.goorm.study.dto.Oauth2Response;

import java.util.Map;

public interface OAuth2ResponseStrategy {
    String getProviderName();
    Oauth2Response createOAuth2Response(Map<String, Object> attributes);
}
