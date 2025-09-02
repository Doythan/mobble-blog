package org.example.mobble.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.mobble._util.error.ex.*;
import org.example.mobble.user.domain.User;
import org.example.mobble.user.domain.UserRepository;
import org.example.mobble.user.dto.UserRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    // 이미지는 base64 string으로 저장할거라 업로드 필요 X

    public User findByUser(UserRequest.LoginDTO reqDTO) {
        User user = userRepository.findByUsername(reqDTO.getUsername())
                .orElseThrow(() -> new Exception400("존재하지 않는 사용자 아이디입니다."));
        if (!user.getPassword().equals(reqDTO.getPassword())) throw new Exception401("비밀번호가 일치하지 않습니다.");
        return user;
    }

    @Transactional
    public void save(UserRequest.JoinDTO reqDTO) {
        User user = userRepository.findByUsername(reqDTO.getUsername()).orElse(null);
        if (user != null) throw new Exception302("중복되는 닉네임입니다.");
        String pw = bCryptPasswordEncoder.encode(reqDTO.getPassword());
        userRepository.save(new User(null, reqDTO.getUsername(), pw, reqDTO.getEmail(), null));
    }

    @Transactional
    public void changeProfile(User user, Integer userId, UserRequest.ProfileUpdateDTO reqDTO) {
        checkPermissions(user, userId);
        User userPS = getUser(user.getId());
        if (reqDTO == null || reqDTO.getProfileImage().isEmpty()) throw new Exception400("업로드된 파일이 없습니다.");
        if (userPS == null) throw new Exception404("해당 유저를 찾을 수 없습니다.");
        userPS.updateProfileImage(reqDTO.getProfileImage());
    }

    @Transactional
    public void changePassword(User user, Integer userId, UserRequest.PasswordUpdateDTO reqDTO) {
        if (reqDTO == null || reqDTO.getPassword().isEmpty()) throw new Exception400("비밀번호가 입력되지 않았습니다.");
        checkPermissions(user, userId);
        User userPS = getUser(user.getId());
        userPS.updatePassword(reqDTO.getPassword());
    }

    @Transactional
    public void delete(User user, Integer userId) {
        checkPermissions(user, userId);
        userRepository.delete(user);
    }

    // 해당 로직은 외부에서 중복 아이디를 확인하는 로직으로 private 설정이 불가하여 public으로 놔두었습니다.
    public boolean isNicknameDuplicate(String username) {
        return userRepository.existsByNickname(username);
    }

    private void checkPermissions(User user, Integer userId) {
        if (!user.getId().equals(userId)) throw new Exception403("해당 계정의 권한이 없습니다.");
    }

    private User getUser(Integer userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new Exception403("존재하지 않는 사용자 입니다.")
        );
    }
}
