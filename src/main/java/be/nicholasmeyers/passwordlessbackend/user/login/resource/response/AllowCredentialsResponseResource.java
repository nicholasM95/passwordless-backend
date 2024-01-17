package be.nicholasmeyers.passwordlessbackend.user.login.resource.response;

import com.yubico.webauthn.data.AuthenticatorTransport;
import com.yubico.webauthn.data.ByteArray;

import java.util.Set;

public record AllowCredentialsResponseResource(ByteArray id, String type,
                                               Set<AuthenticatorTransport> transports) {
}
