package be.nicholasmeyers.passwordlessbackend.user.registration.resource.request;

public record VerifyRegistrationRequestResource(String username, VerifyAttestationRequestResource response) {
}
