package pl.michalkarwowski.api.security.config;

public class SecurityConstants {
    public static final String SECRET = "MichalMistrz";
    public static final long EXPIRATION_TIME = 86_400_000; // 1 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/users/sign-up";
    public static final String VERIFY_URL = "/verify";
}
