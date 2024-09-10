package start.goorm.study.strategy;

import org.springframework.stereotype.Component;
import start.goorm.study.dto.Oauth2Response;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OAuth2ResponseFactory {
    private final Map<String, OAuth2ResponseStrategy> strategies;

    public OAuth2ResponseFactory(List<OAuth2ResponseStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(OAuth2ResponseStrategy::getProviderName
                , Function.identity()));
    }

    public Oauth2Response createOAuth2Response(String registrationId,
        Map<String, Object> attributes) {
        OAuth2ResponseStrategy strategy = strategies.get(registrationId);
        if (Objects.equals(strategy, null)) throw new IllegalArgumentException("지원하지 않는 provider");
        return strategy.createOAuth2Response(attributes);
    }
}
