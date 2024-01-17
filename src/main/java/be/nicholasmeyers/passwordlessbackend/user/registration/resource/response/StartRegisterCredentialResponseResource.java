package be.nicholasmeyers.passwordlessbackend.user.registration.resource.response;

import com.yubico.webauthn.data.ByteArray;

public record StartRegisterCredentialResponseResource(ByteArray id, String type, String[] transports) {
}
