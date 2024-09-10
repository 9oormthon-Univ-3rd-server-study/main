package start.goorm.study.common;

import start.goorm.study.dto.Oauth2Response;

import java.util.EnumSet;

public enum Registration {
    google, kakao;


    private static final EnumSet<Registration> REGISTRATIONS = EnumSet.allOf(Registration.class);

    public static boolean contains(String registrationId) {
        return REGISTRATIONS.stream()
                .anyMatch(registration -> registration.name().equalsIgnoreCase(registrationId));
    }

//    public static Oauth2Response getOauth2Response(String registrationId) {
//        REGISTRATIONS.stream()
//    }
}
