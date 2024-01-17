package be.nicholasmeyers.passwordlessbackend.user.config;

import be.nicholasmeyers.passwordlessbackend.user.data.MyCredentialRepository;
import be.nicholasmeyers.passwordlessbackend.user.data.PasskeyRepository;
import be.nicholasmeyers.passwordlessbackend.user.data.UserRepository;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.data.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class ServerConfiguration {

    private final UserRepository userRepository;
    private final PasskeyRepository passkeyRepository;

    @Bean
    public RelyingPartyIdentity relyingPartyIdentity() {
        RelyingPartyIdentity rpIdentity = RelyingPartyIdentity.builder()
                .id("localhost")
                .name("WebAuthn - Nicholas Meyers")
                .build();
        return rpIdentity;
    }

    @Bean
    public RelyingParty relyingParty() {
        RelyingParty rp = RelyingParty.builder()
                .identity(relyingPartyIdentity())
                .credentialRepository(new MyCredentialRepository(userRepository, passkeyRepository))
                .allowOriginPort(true)
                .build();
        return rp;
    }

    @Bean
    public AuthenticatorSelectionCriteria authenticatorSelectionCriteria() {
        AuthenticatorAttachment authenticatorAttachment = AuthenticatorAttachment.CROSS_PLATFORM;
        ResidentKeyRequirement residentKeyRequirement = ResidentKeyRequirement.PREFERRED;
        UserVerificationRequirement userVerificationRequirement = UserVerificationRequirement.PREFERRED;

        return AuthenticatorSelectionCriteria.builder()
                .authenticatorAttachment(authenticatorAttachment)
                .residentKey(residentKeyRequirement)
                .userVerification(userVerificationRequirement)
                .build();
    }

    @Bean
    public List<PublicKeyCredentialParameters> publicKeyCredentialParameters() {
        List<PublicKeyCredentialParameters> pubKeyCredParams = new ArrayList<>();

        PublicKeyCredentialParameters param1 = PublicKeyCredentialParameters.ES256;
        PublicKeyCredentialParameters param2 = PublicKeyCredentialParameters.RS256;
        pubKeyCredParams.add(param1);
        pubKeyCredParams.add(param2);
        return pubKeyCredParams;
    }
}
