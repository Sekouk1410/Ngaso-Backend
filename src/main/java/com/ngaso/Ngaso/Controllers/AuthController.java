package com.ngaso.Ngaso.Controllers;

import com.ngaso.Ngaso.Services.AuthService;
import com.ngaso.Ngaso.dto.AuthLoginRequest;
import com.ngaso.Ngaso.dto.AuthLoginResponse;
import com.ngaso.Ngaso.dto.NoviceSignupRequest;
import com.ngaso.Ngaso.dto.ProfessionnelSignupRequest;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/register/novice", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthLoginResponse> registerNovice(@RequestBody NoviceSignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerNovice(request));
    }

    @PostMapping(value = "/register/professionnel", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthLoginResponse> registerProfessionnel(@RequestBody ProfessionnelSignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerProfessionnel(request));
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthLoginResponse> login(@RequestBody AuthLoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}

