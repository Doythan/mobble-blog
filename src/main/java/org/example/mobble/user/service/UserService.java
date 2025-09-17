package org.example.mobble.user.service;

import lombok.RequiredArgsConstructor;
import org.example.mobble._util.error.ErrorEnum;
import org.example.mobble._util.error.ex.*;
import org.example.mobble._util.util.UploadImgUtil;
import org.example.mobble.user.domain.User;
import org.example.mobble.user.domain.UserRepository;
import org.example.mobble.user.dto.UserRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

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

        try {
            String oldProfile = user.getProfileImage();
            if (oldProfile != null) {
                Path path = Paths.get("src/main/resources/static" + oldProfile);
                Files.deleteIfExists(path);
            }

            UploadImgUtil.SaveOptions opts = new UploadImgUtil.SaveOptions();
            opts.subDir = null; // profile 폴더 바로 밑에 저장
            opts.fixedFileName = user.getUsername() + "_" + System.currentTimeMillis() + ".jpg"; // 사용자 ID 기반 저장

            UploadImgUtil.SaveResult result = UploadImgUtil.saveImage(reqDTO.getProfileImage(), Paths.get("src/main/resources/static/profile"), "/profile", opts);
            userPS.updateProfileImage(result.publicUrl);
            return userPS;
        } catch (Exception e) {
            throw new Exception500(ErrorEnum.INVALID_DATABASE_DATA);
        }

    }

    @Transactional
    public void changePassword(User user, Integer userId, UserRequest.PasswordUpdateDTO reqDTO) {
        if (reqDTO == null || reqDTO.getPassword().isEmpty())
            throw new Exception400(ErrorEnum.BAD_REQUEST_NO_EXISTS_PASSWORD);
        checkPermissions(user, userId);
        User userPS = getUser(user.getId());
        userPS.updatePassword(bCryptPasswordEncoder.encode(reqDTO.getPassword()));
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