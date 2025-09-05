package org.example.mobble.user.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.mobble.user.domain.User;
import org.example.mobble.user.dto.UserRequest;
import org.example.mobble.user.dto.UserResponse;
import org.example.mobble.user.service.UserService;
import org.springframework.http.MediaType;
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

    @GetMapping("/join-form")
    public String joinForm() {
        return "auth/join-page";
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, value = "/login")
    public String login(@ModelAttribute UserRequest.LoginDTO reqDTO) {
        User user = userService.findByUser(reqDTO);
        session.setAttribute("user", user);
        return "redirect:/boards";
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, value = "/join")
    public String join(@ModelAttribute UserRequest.JoinDTO reqDTO) {
        userService.save(reqDTO);
        return "redirect:/login-form";
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
        User user = (User) session.getAttribute("user");
        session.setAttribute("model", new UserResponse.UserDetailDTO(user));
        return "mypage/main";
    }

    // 이미지 변경
    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, value = "/users/{id}/profile")
    public String updateUsersProfile(@PathVariable(name = "id") Integer userId, @ModelAttribute UserRequest.ProfileUpdateDTO reqDTO) {
        User user = (User) session.getAttribute("user");
        User userPS = userService.changeProfile(user, userId, reqDTO);
        session.setAttribute("user", userPS);
        return "redirect:/users";
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, value = "/users/{id}/password")
    public String updateUsersPassword(@PathVariable(name = "id") Integer userId, @ModelAttribute UserRequest.PasswordUpdateDTO reqDTO) {
        User user = (User) session.getAttribute("user");
        userService.changePassword(user, userId, reqDTO);
        return "redirect:/users";
    }

    @DeleteMapping("/users/{id}")
    public String deleteUsers(@PathVariable(name = "id") Integer userId) {
        User user = (User) session.getAttribute("user");
        userService.delete(user, userId);
        return "redirect:/";
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, value = "/users/check-username")
    @ResponseBody
    public Map<String, Boolean> checkUsername(@ModelAttribute UserRequest.UsernameDTO reqDTO) {
        return Map.of("duplicate", userService.isUsernameDuplicate(reqDTO));

    }
}