package start.goorm.study.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import start.goorm.study.domain.User;
import start.goorm.study.dto.CustomOAuth2User;
import start.goorm.study.dto.Oauth2Response;
import start.goorm.study.dto.UserDto;
import start.goorm.study.repository.UserRepository;
import start.goorm.study.strategy.OAuth2ResponseFactory;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger log = LoggerFactory.getLogger(CustomOAuth2UserService.class);
    private final OAuth2ResponseFactory oAuth2ResponseFactory;
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.debug("oAuth2User {}", oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.debug("registrationId {}", registrationId);
        Oauth2Response oauth2Response = oAuth2ResponseFactory.createOAuth2Response(registrationId, oAuth2User.getAttributes());

        String username = oauth2Response.getProvider()+" " + oauth2Response.getProviderId();
        Optional<User> userOptional = userRepository.findByUsername(username);
        UserDto userDto = getUserDto(username, oauth2Response);
        if (userOptional.isEmpty()) {
            User user = User.builder()
                    .username(username)
                    .name(oauth2Response.getName())
//                    .email(oauth2Response.getEmail())
                    .role("ROLE_USER")
                    .build();
            userRepository.save(user);
        } else {
            User user = userOptional.get();
            user.update(username, oauth2Response.getName(), oauth2Response.getEmail());

        }
        return new CustomOAuth2User(userDto);
    }

    private static UserDto getUserDto(String username, Oauth2Response oauth2Response) {
        return UserDto.builder()
                .username(username)
                .name(oauth2Response.getName())
                .role("ROLE_USER")
                .build();
    }
}