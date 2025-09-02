package org.example.mobble.board.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.mobble._util.error.ex.Exception401;
import org.example.mobble.board.domain.Board;
import org.example.mobble.board.dto.BoardRequest;
import org.example.mobble.board.dto.BoardResponse;
import org.example.mobble.board.service.BoardService;
import org.example.mobble.user.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/boards")
public class BoardController {
    private final BoardService boardService;
    private final HttpSession session;

    //게시글 저장 페이지 이동
    @GetMapping("/save-form")
    public String boardSaveForm() {
        User user = getLoginUser();
        return "board/save-page";
    }

    @GetMapping("/{id}/update-form")
    public String boardUpdateForm(@PathVariable(name = "id") Integer boardId) {
        User user = getLoginUser();
        session.setAttribute("model", boardService.getBoard(boardId));
        return "board/update-page";
    }

    // 모든 게시물 목록 찾기
    @GetMapping
    public String getBoardsList(HttpServletRequest request) {
        User user = getLoginUser();
        List<BoardResponse.DTO> resDTO = boardService.getList();
        request.setAttribute("model", resDTO);
        return "board/list-page";
    }

    @GetMapping("/{id}")
    public String getBoard(HttpServletRequest request, @PathVariable(name = "id") Integer boardId) {
        User user = getLoginUser();
        request.setAttribute("model", boardService.getBoard(boardId));
        return "board/detail-page";
    }

    @PutMapping("/{id}")
    public String update(@PathVariable(name = "id") Integer boardId, @RequestBody BoardRequest.BoardUpdateDTO reqDTO) {
        User user = getLoginUser();
        boardService.update(boardId, reqDTO, user);
        return "redirect:/boards/" + boardId;
    }

    // 게시글 저장하기
    @PostMapping
    public String save(BoardRequest.BoardSaveDTO reqDTO) {
        User user = getLoginUser();
        Board board = boardService.save(reqDTO, user);
        return "redirect:/boards/" + board.getId();
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable(name = "id") Integer boardId) {
        User user = getLoginUser();
        boardService.delete(boardId, user);
        return "redirect:/boards";
    }

    @PostMapping("/{id}/report")
    public String report(@PathVariable(name = "id") Integer boardId, @RequestBody BoardRequest.BoardReportDTO reqDTO) {
        User user = getLoginUser();
        boardService.report(user, boardId, reqDTO);
        return "redirect:/boards/" + boardId;
    }

    //서비스 이용자 확인 절차.
    private User getLoginUser() {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new Exception401("로그인 후 이용 부탁드립니다."); // 에러 코드, 에러 메시지 컨벤션
        }
        return user;
    }
}
