package org.example.mobble.user.dto;

import lombok.Builder;
import lombok.Data;
import org.example.mobble.user.domain.User;

public class UserResponse {
    @Data
    public static class UserDetailDTO {
        private Integer userId;
        private String username;
        private String email;
        private String profileUrl;

        @Builder
        public UserDetailDTO(User user) {
            this.userId = user.getId();
            this.username = user.getUsername();
            this.email = user.getEmail();
            this.profileUrl = user.getProfileImage();
        }
    }
}
