package start.goorm.study.dto;

public interface Oauth2Response {
    // e.g. kakao, google
    String getProvider();
    String getProviderId();
    String getEmail();
    String getName();
}
