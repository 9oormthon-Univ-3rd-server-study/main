package start.goorm.study.strategy;

import org.springframework.stereotype.Component;
import start.goorm.study.dto.GoogleResponse;
import start.goorm.study.dto.Oauth2Response;

import java.util.Map;

@Component
public class GoogleOAuth2ResponseStrategy implements OAuth2ResponseStrategy{
    @Override
    public String getProviderName() {
        return "google";
    }

    @Override
    public Oauth2Response createOAuth2Response(Map<String, Object> attributes) {
        System.out.println(attributes);
        return new GoogleResponse(attributes);
    }
}
