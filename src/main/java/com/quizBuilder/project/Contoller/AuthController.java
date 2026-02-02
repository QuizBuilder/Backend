package com.quizBuilder.project.Contoller;

import com.quizBuilder.project.Model.Authentication.SigninRequest;
import com.quizBuilder.project.Model.Authentication.SigninResponse;
import com.quizBuilder.project.Model.Authentication.SignupRequest;
import com.quizBuilder.project.Model.Authentication.SignupResponse;
import com.quizBuilder.project.Service.AuthService;
import com.quizBuilder.project.Service.JWTService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {
    private final AuthService authService;
    private final JWTService jwtService;
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signUp(@Valid @RequestBody SignupRequest requestDTO){

        SignupResponse data = authService.signUp(requestDTO);

        return ResponseEntity.ok(data);
    }

    @PostMapping("/signin")
    public ResponseEntity<SigninResponse> singIn(@Valid @RequestBody SigninRequest requestDTO){
        SigninResponse data = authService.signIn(requestDTO);
        return ResponseEntity.ok(data);
    }

    @PostMapping("/jwt/check")
    public ResponseEntity<Boolean> checkJWT(@RequestBody String jwtToken){
        Boolean isValid = jwtService.validateToken(jwtToken);
        return ResponseEntity.ok(isValid);
    }
}
