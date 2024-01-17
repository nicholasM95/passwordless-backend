package be.nicholasmeyers.passwordlessbackend.user.registration.service;

import be.nicholasmeyers.passwordlessbackend.user.data.PasskeyRepository;
import be.nicholasmeyers.passwordlessbackend.user.data.UserRepository;
import be.nicholasmeyers.passwordlessbackend.user.extension.CustomClientExtensionOutput;
import be.nicholasmeyers.passwordlessbackend.user.model.Passkey;
import be.nicholasmeyers.passwordlessbackend.user.model.User;
import be.nicholasmeyers.passwordlessbackend.user.registration.resource.request.VerifyClientRequestResource;
import be.nicholasmeyers.passwordlessbackend.user.registration.resource.request.VerifyRegistrationRequestResource;
import be.nicholasmeyers.passwordlessbackend.user.registration.resource.response.VerifyRegistrationResponseResource;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.yubico.webauthn.FinishRegistrationOptions;
import com.yubico.webauthn.RegistrationResult;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.data.*;
import com.yubico.webauthn.data.exception.Base64UrlException;
import com.yubico.webauthn.exception.RegistrationFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static be.nicholasmeyers.passwordlessbackend.util.ByteArrayUtils.byteArrayToBytes;

@Slf4j
@RequiredArgsConstructor
@Service
public class VerifyRegistrationService {

    private final RelyingParty relyingParty;
    private final UserRepository userRepository;
    private final PasskeyRepository passkeyRepository;

    public VerifyRegistrationResponseResource verify(VerifyRegistrationRequestResource resource) throws Base64UrlException, IOException {
        Optional<User> user = userRepository.findByUsername(resource.username());

        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        AuthenticatorAttestationResponse authenticatorAttestationResponse
                = createAuthenticatorAttestationResponse(resource.response().response());

        PublicKeyCredentialCreationOptions publicKeyCredentials = createPublicKeyCredentialCreationOptions(user.get().getPublicKeyJson());
        PublicKeyCredential publicKeyCredential = createPublicKeyCredential(resource.response().id(), authenticatorAttestationResponse);

        FinishRegistrationOptions finishRegistrationOptions = createFinishRegistrationOptions(publicKeyCredentials, publicKeyCredential);

        RegistrationResult registrationResult;
        try {
            registrationResult = relyingParty.finishRegistration(finishRegistrationOptions);
        } catch (RegistrationFailedException e) {
            user.get().setPublicKeyJson(null);
            user.get().setRegistrationComplete(false);
            userRepository.save(user.get());
            return new VerifyRegistrationResponseResource(false);
        }

        user.get().setPublicKeyJson(null);
        user.get().setRegistrationComplete(true);
        userRepository.save(user.get());

        byte[] publicKey = byteArrayToBytes(registrationResult.getPublicKeyCose());
        byte[] keyId = byteArrayToBytes(registrationResult.getKeyId().getId());
        String type = registrationResult.getKeyId().getType().getId();
        String transport = "";
        long signatureCount = registrationResult.getSignatureCount();


        Optional<SortedSet<AuthenticatorTransport>> transports = registrationResult.getKeyId().getTransports();
        if (transports.isPresent()) {
            List<String> transportList = transports.get().stream().map(AuthenticatorTransport::getId).toList();
            transport = String.join(",", transportList);
        }

        Passkey passkey = new Passkey();
        passkey.setId(UUID.randomUUID());
        passkey.setUserHandle(user.get().getUserHandle());
        passkey.setPublicKey(publicKey);
        passkey.setKeyId(keyId);
        passkey.setType(type);
        passkey.setTransport(transport);
        passkey.setSignatureCount(signatureCount);

        log.info("Save passkey for transports {}", transport);
        passkeyRepository.save(passkey);

        return new VerifyRegistrationResponseResource(true);
    }

    private AuthenticatorAttestationResponse createAuthenticatorAttestationResponse(VerifyClientRequestResource client) throws Base64UrlException, IOException {
        Set<AuthenticatorTransport> transports = new HashSet<>();
        client.transports().forEach(transport -> {
            transports.add(AuthenticatorTransport.of(transport));
        });

        return AuthenticatorAttestationResponse.builder()
                .attestationObject(client.attestationObject())
                .clientDataJSON(client.clientDataJSON())
                .transports(transports)
                .build();
    }

    private PublicKeyCredentialCreationOptions createPublicKeyCredentialCreationOptions(String json) throws JsonProcessingException {
        return PublicKeyCredentialCreationOptions.fromJson(json);
    }

    private PublicKeyCredential createPublicKeyCredential(ByteArray id, AuthenticatorAttestationResponse authenticator) {
        CustomClientExtensionOutput extensionOutput = new CustomClientExtensionOutput();

        return PublicKeyCredential.builder()
                .id(id)
                .response(authenticator)
                .clientExtensionResults(extensionOutput)
                .build();
    }

    private FinishRegistrationOptions createFinishRegistrationOptions(PublicKeyCredentialCreationOptions publicKey, PublicKeyCredential credential) {
        return FinishRegistrationOptions.builder()
                .request(publicKey)
                .response(credential)
                .build();
    }
}
