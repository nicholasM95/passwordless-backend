package be.nicholasmeyers.passwordlessbackend.user.login.resource.request;

public record VerifyLoginRequestResource(String username, AssertionRequestResource response) {
}
