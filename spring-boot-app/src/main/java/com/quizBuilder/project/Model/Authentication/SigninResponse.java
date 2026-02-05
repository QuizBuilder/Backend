package com.quizBuilder.project.Model.Authentication;

import com.quizBuilder.project.Entity.Enum.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SigninResponse {
    private String jwtToken;
    private Role role;
}
