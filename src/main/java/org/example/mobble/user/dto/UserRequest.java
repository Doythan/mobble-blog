package org.example.mobble.user.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserRequest {
    @Data
    @NoArgsConstructor
    public static class LoginDTO {
        private String username;
        private String password;

        @Builder
        public LoginDTO(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    @Data
    @NoArgsConstructor
    public static class PasswordUpdateDTO {
        private String password;

        @Builder
        public PasswordUpdateDTO(String password) {
            this.password = password;
        }
    }

    @Data
    @NoArgsConstructor
    public static class ProfileUpdateDTO {
        private MultipartFile profileImage;

        @Builder
        public ProfileUpdateDTO(MultipartFile profileImage) {
            this.profileImage = profileImage;
        }
    }

    @Data
    @NoArgsConstructor
    public static class UsernameDTO {
        private String username;

        @Builder
        public UsernameDTO(String username) {
            this.username = username;
        }
    }

    @Data
    @NoArgsConstructor
    public static class JoinDTO {
        private String email;
        private String username;
        private String password;

        @Builder
        public JoinDTO(String email, String username, String password) {
            this.email = email;
            this.username = username;
            this.password = password;
        }
    }
}
