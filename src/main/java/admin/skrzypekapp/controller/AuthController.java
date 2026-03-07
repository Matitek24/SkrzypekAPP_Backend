package admin.skrzypekapp.controller;

import admin.skrzypekapp.dto.LoginRequest;
import admin.skrzypekapp.dto.LoginResponse;
import admin.skrzypekapp.dto.RegisterRequest;
import admin.skrzypekapp.security.JwtTokenProvider;
import admin.skrzypekapp.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password())
        );

        String jwt = jwtTokenProvider.generateToken(authentication);

        ResponseCookie cookie = ResponseCookie.from("accessToken", jwt)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(24 * 60 * 60) // 24h
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new LoginResponse("Sukces", loginRequest.username()));
    }
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        authService.registerUser(request);
        return ResponseEntity.ok("Użytkownik zarejestrowany!");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Wylogowano pomyślnie");
    }
}