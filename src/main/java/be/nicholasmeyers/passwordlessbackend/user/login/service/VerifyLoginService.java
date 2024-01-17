package be.nicholasmeyers.passwordlessbackend.user.login.service;

import be.nicholasmeyers.passwordlessbackend.user.data.UserRepository;
import be.nicholasmeyers.passwordlessbackend.user.extension.CustomClientExtensionOutput;
import be.nicholasmeyers.passwordlessbackend.user.login.resource.request.VerifyLoginRequestResource;
import be.nicholasmeyers.passwordlessbackend.user.login.resource.response.VerifyLoginResponseResource;
import be.nicholasmeyers.passwordlessbackend.user.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.yubico.webauthn.AssertionRequest;
import com.yubico.webauthn.AssertionResult;
import com.yubico.webauthn.FinishAssertionOptions;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.data.AuthenticatorAssertionResponse;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.exception.Base64UrlException;
import com.yubico.webauthn.exception.AssertionFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class VerifyLoginService {

    private final RelyingParty relyingParty;
    private final UserRepository userRepository;

    public VerifyLoginResponseResource verify(VerifyLoginRequestResource resource) throws IOException, Base64UrlException {
        User user = getUser(resource.username());

        AssertionRequest assertionRequest = createAssertionRequest(user.getAssertion());
        AuthenticatorAssertionResponse assertionResponse = createAuthenticatorAssertionResponse(resource);

        PublicKeyCredential publicKeyCredential = createPublicKeyCredential(resource, assertionResponse);

        FinishAssertionOptions finishAssertionOptions = createFinishAssertionOptions(assertionRequest, publicKeyCredential);

        AssertionResult result;
        try {
            result = relyingParty.finishAssertion(finishAssertionOptions);
        } catch (AssertionFailedException e) {
            user.setAssertion(null);
            userRepository.save(user);
            return new VerifyLoginResponseResource(false);
        }

        user.setAssertion(null);
        userRepository.save(user);
        if (result.isSuccess()) {
            return new VerifyLoginResponseResource(true);
        } else {
            return new VerifyLoginResponseResource(false);
        }

    }

    private User getUser(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty() || !user.get().isRegistrationComplete()) {
            if (user.isEmpty()) {
                log.error("User with username {} not found", username);
            } else {
                log.error("Registration for username {} is not complete", username);
            }
            throw new RuntimeException(String.format("User with username %s not registered", username));
        }
        return user.get();
    }

    private AssertionRequest createAssertionRequest(String json) throws JsonProcessingException {
        return AssertionRequest.fromJson(json);
    }

    private AuthenticatorAssertionResponse createAuthenticatorAssertionResponse(VerifyLoginRequestResource resource) throws Base64UrlException, IOException {
        return AuthenticatorAssertionResponse.builder()
                .authenticatorData(resource.response().response().authenticatorData())
                .clientDataJSON(resource.response().response().clientDataJSON())
                .signature(resource.response().response().signature())
                .build();
    }

    private PublicKeyCredential createPublicKeyCredential(VerifyLoginRequestResource resource, AuthenticatorAssertionResponse response) {
        CustomClientExtensionOutput customClientExtensionOutput = new CustomClientExtensionOutput();

        return PublicKeyCredential.builder()
                .id(resource.response().id())
                .response(response)
                .clientExtensionResults(customClientExtensionOutput)
                .build();
    }

    private FinishAssertionOptions createFinishAssertionOptions(AssertionRequest assertionRequest, PublicKeyCredential publicKeyCredential) {
        return FinishAssertionOptions.builder()
                .request(assertionRequest)
                .response(publicKeyCredential)
                .build();
    }
}

