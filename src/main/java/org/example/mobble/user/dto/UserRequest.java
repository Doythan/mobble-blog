package org.example.mobble.user.dto;

import lombok.Builder;
import lombok.Data;

public class UserRequest {
    @Data
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
    public static class PasswordUpdateDTO {
        private String password;

        @Builder
        public PasswordUpdateDTO(String password) {
            this.password = password;
        }
    }

    @Data
    public static class ProfileUpdateDTO {
        private String profileImage;

        @Builder
        public ProfileUpdateDTO(String profileImage) {
            this.profileImage = profileImage;
        }
    }

    @Data
    public static class UsernameDTO {
        private String username;

        @Builder
        public UsernameDTO(String username) {
            this.username = username;
        }
    }

    @Data
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
