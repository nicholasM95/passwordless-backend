package be.nicholasmeyers.passwordlessbackend.user.registration.resource.request;

import com.yubico.webauthn.data.ByteArray;

public record VerifyAttestationRequestResource(ByteArray id, ByteArray rawId,
                                               VerifyClientRequestResource response, String type,
                                               Object clientExtensionResults, String authenticatorAttachment) {
}
