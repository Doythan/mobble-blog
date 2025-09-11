package org.example.mobble.user.service;

import lombok.RequiredArgsConstructor;
import org.example.mobble._util.error.ErrorEnum;
import org.example.mobble._util.error.ex.*;
import org.example.mobble.user.domain.User;
import org.example.mobble.user.domain.UserRepository;
import org.example.mobble.user.dto.UserRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    // 이미지는 base64 string으로 저장할거라 업로드 필요 X

    public User findByUser(UserRequest.LoginDTO reqDTO) {
        User user = userRepository.findByUsername(reqDTO.getUsername())
                .orElseThrow(() -> new Exception403(ErrorEnum.NOT_FOUND_USER_TO_USERNAME));
        if (!bCryptPasswordEncoder.matches(reqDTO.getPassword(), user.getPassword()))
            throw new Exception401(ErrorEnum.FORBIDDEN_NO_MATCH_PASSWORD);
        return user;
    }

    @Transactional
    public void save(UserRequest.JoinDTO reqDTO) {
        User user = userRepository.findByUsername(reqDTO.getUsername()).orElse(null);
        if (user != null) throw new Exception302(ErrorEnum.FOUND_USER_TO_USERNAME);
        String pw = bCryptPasswordEncoder.encode(reqDTO.getPassword());
        userRepository.save(
                User.builder()
                        .username(reqDTO.getUsername())
                        .email(reqDTO.getEmail())
                        .password(pw)
                        .role("user")
                        .build()
        );
    }

    @Transactional
    public User changeProfile(User user, Integer userId, UserRequest.ProfileUpdateDTO reqDTO) {
        checkPermissions(user, userId);
        User userPS = getUser(user.getId());
        if (reqDTO == null || reqDTO.getProfileImage().isEmpty())
            throw new Exception400(ErrorEnum.BAD_REQUEST_NO_EXISTS_FILE);
        userPS.updateProfileImage(reqDTO.getProfileImage());
        return userPS;
    }

    @Transactional
    public void changePassword(User user, Integer userId, UserRequest.PasswordUpdateDTO reqDTO) {
        if (reqDTO == null || reqDTO.getPassword().isEmpty())
            throw new Exception400(ErrorEnum.BAD_REQUEST_NO_EXISTS_PASSWORD);
        checkPermissions(user, userId);
        User userPS = getUser(user.getId());
        userPS.updatePassword(reqDTO.getPassword());
    }

    @Transactional
    public void delete(User user, Integer userId) {
        checkPermissions(user, userId);
        userRepository.delete(user.getId());
    }

    // 해당 로직은 외부에서 중복 아이디를 확인하는 로직으로 private 설정이 불가하여 public으로 놔두었습니다.
    public boolean isUsernameDuplicate(UserRequest.UsernameDTO reqDTO) {
        return userRepository.existsByUsername(reqDTO.getUsername());
    }

    private void checkPermissions(User user, Integer userId) {
        if (!user.getId().equals(userId)) throw new Exception403(ErrorEnum.FORBIDDEN_USER_TO_USER);
    }

    private User getUser(Integer userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new Exception404(ErrorEnum.NOT_FOUND_USER_TO_USERID)
        );
    }
}