package com.quizBuilder.project.Service;

import com.quizBuilder.project.Exception.BadRequestException;
import com.quizBuilder.project.Exception.ResourceNotFoundException;
import com.quizBuilder.project.Exception.UnauthorizedException;
import com.quizBuilder.project.Model.Authentication.SigninRequest;
import com.quizBuilder.project.Model.Authentication.SigninResponse;
import com.quizBuilder.project.Model.Authentication.SignupRequest;
import com.quizBuilder.project.Model.Authentication.SignupResponse;
import com.quizBuilder.project.Entity.User;
import com.quizBuilder.project.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final JWTService jwtService;

    public SignupResponse signUp(SignupRequest request) {
        System.out.println("ab");

        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (user != null) {
            throw new BadRequestException("User already exists.");
        }

        user = User.builder()
                .email(request.getEmail())
                .role(request.getRole())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        return SignupResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public SigninResponse signIn(SigninRequest requestDTO) {

        User user = userRepository
                .findByEmail(requestDTO.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        if (!passwordEncoder.matches(requestDTO.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        String JWTtoken = jwtService.createToken(user);

        return SigninResponse.builder()
                .jwtToken(JWTtoken)
                .role(user.getRole())
                .build();
    }
}

//p0 logging SLF4J
//p0 basic 4xx validation
//p1 global error mapper
//p1 global response mapper
//p0 encode password

