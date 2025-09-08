package org.example.mobble.bookmark.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.mobble._util.error.ex.Exception401;
import org.example.mobble._util.util.Resp;
import org.example.mobble.bookmark.dto.BookmarkResponse;
import org.example.mobble.bookmark.service.BookmarkService;
import org.example.mobble.user.domain.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@RequiredArgsConstructor
@Controller
public class BookmarkController {
    private final BookmarkService bookmarkService;
    private final HttpSession session;

    // 북마크 추가
    @PostMapping("/bookmark/{boardId}/save")
    public ResponseEntity<?> bookmarkInsert(@PathVariable("boardId") Integer boardId) {
        User user = getLoginUser();

        BookmarkResponse.BookmarkSaveDTO respDTO = bookmarkService.bookmarkSave(boardId,user.getId());
        return Resp.ok(respDTO);
    }

    // 북마크 삭제
    @PostMapping("/bookmark/{boardId}/delete")
    public ResponseEntity<?> bookmarkDelete(@PathVariable("boardId") Integer boardId) {
        User user = getLoginUser();

        bookmarkService.bookmarkDelete(boardId,user.getId());
        return Resp.ok(null);
    }

    // 북마크 리스트
    @GetMapping("/bookmarks")
    public String bookmarkList(HttpServletRequest request) {
        User user = getLoginUser();
        BookmarkResponse.BookmarkListDTO respDTO = bookmarkService.bookmarkList(user.getId());
        request.setAttribute("model", respDTO);
        return "mypage/main"; // bookmark 페이지로 이동
    }

    private User getLoginUser() {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new Exception401("로그인 후 이용 부탁드립니다."); // 에러 코드, 에러 메시지 컨벤션
        }
        return user;
    }
}
