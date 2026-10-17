package ng.ourChemo.controllers;

import ng.ourChemo.dtos.requests.RegisterUserRequest;
import ng.ourChemo.dtos.requests.UserLoginRequest;
import ng.ourChemo.dtos.requests.UserLogoutRequest;
import ng.ourChemo.dtos.responses.ApiResponse;
import ng.ourChemo.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthControllers {

    private final AuthService authService;

    public AuthControllers(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterUserRequest request) {
        return ResponseEntity.ok(new ApiResponse(true, authService.registerChemist(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody UserLoginRequest request) {
        return ResponseEntity.ok(new ApiResponse(true, authService.login(request)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@RequestBody UserLogoutRequest request) {
        return ResponseEntity.ok(new ApiResponse(true, authService.logout(request)));
    }
}
