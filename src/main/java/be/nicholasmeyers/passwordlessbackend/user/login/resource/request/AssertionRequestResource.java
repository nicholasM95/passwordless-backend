package be.nicholasmeyers.passwordlessbackend.user.login.resource.request;

import com.yubico.webauthn.data.ByteArray;

public record AssertionRequestResource(ByteArray id, ByteArray rawId, AssertionResource response,
                                       String type, Object clientExtensionResults,
                                       String authenticatorAttachment) {
}
