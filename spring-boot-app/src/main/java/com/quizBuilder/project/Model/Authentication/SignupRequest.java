package com.quizBuilder.project.Model.Authentication;

import com.quizBuilder.project.Entity.Enum.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {
     @NotBlank(message = "Email is required")
     @Email(message = "Email must be valid")
     private String email;

     @NotBlank(message = "Name is required")
     private String name;

     @NotBlank(message = "Password is required")
     @Size(min = 6, message = "Password must be of atleast 6 characters")
     private String password;

     @NotNull(message = "Role is required")
     private Role role;
}
