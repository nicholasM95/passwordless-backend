package be.nicholasmeyers.passwordlessbackend.user.login.web;

import be.nicholasmeyers.passwordlessbackend.user.login.resource.request.LoginRequestResource;
import be.nicholasmeyers.passwordlessbackend.user.login.resource.request.VerifyLoginRequestResource;
import be.nicholasmeyers.passwordlessbackend.user.login.resource.response.LoginResponseResource;
import be.nicholasmeyers.passwordlessbackend.user.login.resource.response.VerifyLoginResponseResource;
import be.nicholasmeyers.passwordlessbackend.user.login.service.StartLoginService;
import be.nicholasmeyers.passwordlessbackend.user.login.service.VerifyLoginService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.yubico.webauthn.data.exception.Base64UrlException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/login")
@CrossOrigin("http://localhost:4200")
public class LoginController {
    private final StartLoginService startLoginService;
    private final VerifyLoginService verifyLoginService;

    @PostMapping("/start")
    public ResponseEntity<LoginResponseResource> startLogin(@RequestBody LoginRequestResource resource) throws JsonProcessingException {
        return ResponseEntity.ok(startLoginService.startLogin(resource));
    }

    @PostMapping("/verify")
    public ResponseEntity<VerifyLoginResponseResource> verifyLogin(@RequestBody VerifyLoginRequestResource resource) throws Base64UrlException, IOException {
        return ResponseEntity.ok(verifyLoginService.verify(resource));
    }
}

