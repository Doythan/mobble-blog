package org.example.mobble.user.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.mobble._util.error.ex.Exception401;
import org.example.mobble.user.domain.User;
import org.example.mobble.user.dto.UserRequest;
import org.example.mobble.user.dto.UserResponse;
import org.example.mobble.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@Controller
public class UserController {
    private final HttpSession session;
    private final UserService userService;

    /*                      Auth part
     *  ------------------------------------------------------------------
     */
    @GetMapping("/login-form")
    public String loginForm() {
        return "auth/login-page";
    }

    @GetMapping("/join")
    public String join(UserRequest.JoinDTO reqDTO) {
        userService.save(reqDTO);
        return "redirect:/login-form";
    }

    @GetMapping("/join-form")
    public String joinForm() {
        return "auth/join-page";
    }

    @PostMapping("/login")
    public String login(@RequestBody UserRequest.LoginDTO reqDTO) {
        User user = userService.findByUser(reqDTO);
        session.setAttribute("model", user);
        return "redirect:/boards";
    }

    @GetMapping("/logout")
    public String logout() {
        session.invalidate();
        return "redirect:/login-form"; // login-form
    }

    /*                      User part
     *  ------------------------------------------------------------------
     */
    @GetMapping("/users")
    public String getUsers() {
        User user = getLoginUser();
        session.setAttribute("model", new UserResponse.UserDetailDTO(user));
        return "mypage/main";
    }

    // 이미지 변경
    @PutMapping("/users/{id}/profile")
    public String updateUsersProfile(@PathVariable(name = "id") Integer userId, @RequestBody UserRequest.ProfileUpdateDTO reqDTO) {
        User user = getLoginUser();
        userService.changeProfile(user, userId, reqDTO);
        return "redirect:/users";
    }

    @PutMapping("/users/{id}/password")
    public String updateUsersPassword(@PathVariable(name = "id") Integer userId, @RequestBody UserRequest.PasswordUpdateDTO reqDTO) {
        User user = getLoginUser();
        userService.changePassword(user, userId, reqDTO);
        return "redirect:/users";
    }

    @DeleteMapping("/users/{id}")
    public String deleteUsers(@PathVariable(name = "id") Integer userId) {
        User user = getLoginUser();
        userService.delete(user, userId);
        return "redirect:/";
    }

    @PostMapping("/users/check-username")
    @ResponseBody
    public Map<String, Boolean> checkUsername(@RequestBody UserRequest.UsernameDTO reqDTO) {
        return Map.of("duplicate", userService.isUsernameDuplicate(reqDTO));
    }

    private User getLoginUser() {
        User user = (User) session.getAttribute("user");
        if (user == null) throw new Exception401("로그인 후 이용부탁드립니다.");
        return user;
    }
}
