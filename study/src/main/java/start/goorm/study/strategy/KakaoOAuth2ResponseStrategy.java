package start.goorm.study.strategy;

import org.springframework.stereotype.Component;
import start.goorm.study.dto.KakaoResponse;
import start.goorm.study.dto.Oauth2Response;

import java.util.Map;

@Component
public class KakaoOAuth2ResponseStrategy implements OAuth2ResponseStrategy{
    @Override
    public String getProviderName() {
        return "kakao";
    }

    @Override
    public Oauth2Response createOAuth2Response(Map<String, Object> attributes) {
        System.out.println(attributes);
        return new KakaoResponse(attributes);
    }
}
