package be.nicholasmeyers.passwordlessbackend.user.registration.web;

import be.nicholasmeyers.passwordlessbackend.user.registration.resource.request.StartRegisterRequestResource;
import be.nicholasmeyers.passwordlessbackend.user.registration.resource.request.VerifyRegistrationRequestResource;
import be.nicholasmeyers.passwordlessbackend.user.registration.resource.response.StartRegisterResponseResource;
import be.nicholasmeyers.passwordlessbackend.user.registration.resource.response.VerifyRegistrationResponseResource;
import be.nicholasmeyers.passwordlessbackend.user.registration.service.StartRegistrationService;
import be.nicholasmeyers.passwordlessbackend.user.registration.service.VerifyRegistrationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.yubico.webauthn.data.exception.Base64UrlException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/register")
@CrossOrigin("http://localhost:4200")
public class RegistrationController {

    private final StartRegistrationService startRegistrationService;
    private final VerifyRegistrationService verifyRegistrationService;

    @PostMapping("/start")
    public ResponseEntity<StartRegisterResponseResource> startRegistration(@RequestBody StartRegisterRequestResource resource) throws JsonProcessingException {
        return ResponseEntity.ok(startRegistrationService.startRegistration(resource));
    }

    @PostMapping("/verify")
    public ResponseEntity<VerifyRegistrationResponseResource> verifyRegistration(@RequestBody VerifyRegistrationRequestResource resource) throws Base64UrlException, IOException {
        return ResponseEntity.ok(verifyRegistrationService.verify(resource));
    }
}
