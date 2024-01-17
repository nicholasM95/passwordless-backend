package be.nicholasmeyers.passwordlessbackend.user.login.resource.request;

import com.yubico.webauthn.data.ByteArray;

public record AssertionResource(ByteArray authenticatorData, ByteArray clientDataJSON, ByteArray signature) {
}
