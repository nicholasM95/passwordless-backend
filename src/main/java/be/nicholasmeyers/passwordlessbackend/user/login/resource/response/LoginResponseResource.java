package be.nicholasmeyers.passwordlessbackend.user.login.resource.response;

import java.util.List;

public record LoginResponseResource(String challenge, List<AllowCredentialsResponseResource> allowCredentials,
                                    int timeout, String userVerification, String rpId) {
}
