package be.nicholasmeyers.passwordlessbackend.user.registration.service;

import be.nicholasmeyers.passwordlessbackend.user.data.UserRepository;
import be.nicholasmeyers.passwordlessbackend.user.model.User;
import be.nicholasmeyers.passwordlessbackend.user.registration.resource.request.StartRegisterRequestResource;
import be.nicholasmeyers.passwordlessbackend.user.registration.resource.response.StartRegisterResponseResource;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.StartRegistrationOptions;
import com.yubico.webauthn.data.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static be.nicholasmeyers.passwordlessbackend.util.ByteArrayUtils.bytesToByteArray;

@RequiredArgsConstructor
@Service
public class StartRegistrationService {

    private final UserRepository userRepository;
    private final AuthenticatorSelectionCriteria authenticatorSelection;
    private final RelyingPartyIdentity relyingPartyIdentity;
    private final RelyingParty relyingParty;
    private final List<PublicKeyCredentialParameters> publicKeyCredentialParameters;
    private final Random random = new Random();

    public StartRegisterResponseResource startRegistration(StartRegisterRequestResource resource) throws JsonProcessingException {
        UUID userId = UUID.randomUUID();
        byte[] userHandle = new byte[36];
        random.nextBytes(userHandle);

        UserIdentity userIdentity = createUserIdentity(resource.username(), bytesToByteArray(userHandle));
        StartRegistrationOptions startRegistrationOptions = createStartRegistrationOptions(userIdentity);
        PublicKeyCredentialCreationOptions pbOptions = relyingParty.startRegistration(startRegistrationOptions);

        User user = createUser(userId, resource.username(), pbOptions.toJson(), userHandle);
        userRepository.save(user);

        return new StartRegisterResponseResource(pbOptions.getChallenge().getBase64Url(), relyingPartyIdentity,
                userIdentity, publicKeyCredentialParameters, 60000, "none", Collections.emptyList(), authenticatorSelection);
    }

    private UserIdentity createUserIdentity(String username, ByteArray userHandle) {
        return UserIdentity.builder()
                .name(username)
                .displayName(username)
                .id(userHandle)
                .build();
    }

    private StartRegistrationOptions createStartRegistrationOptions(UserIdentity userIdentity) {
        return StartRegistrationOptions.builder()
                .user(userIdentity)
                .timeout(60000)
                .authenticatorSelection(authenticatorSelection)
                .build();
    }

    private User createUser(UUID userId, String username, String publicKey, byte[] userHandle) {
        User user = new User();
        user.setId(userId);
        user.setUsername(username);
        user.setPublicKeyJson(publicKey);
        user.setUserHandle(userHandle);
        user.setRegistrationComplete(false);
        return user;
    }
}