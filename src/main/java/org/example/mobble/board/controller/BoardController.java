package org.example.mobble.board.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.mobble.board.domain.Board;
import org.example.mobble.board.domain.SearchOrderCase;
import org.example.mobble.board.dto.BoardRequest;
import org.example.mobble.board.dto.BoardResponse;
import org.example.mobble.board.service.BoardService;
import org.example.mobble.category.service.CategoryService;
import org.example.mobble.user.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/boards")
public class BoardController {
    public static final Integer PER_PAGE = 10;

    private final BoardService boardService;
    private final HttpSession session;
    private final CategoryService categoryService;

    //게시글 저장 페이지 이동
    @GetMapping("/save-form")
    public String boardSaveForm() {
        return "board/save-page";
    }

    @GetMapping("/{id}/update-form")
    public String boardUpdateForm(@PathVariable(name = "id") Integer boardId, HttpServletRequest request) {
        User user = (User) session.getAttribute("user");
        BoardResponse.DetailDTO model = boardService.getUpdateBoardDetail(boardId, user);
        request.setAttribute("model", model);
        return "board/update-page";
    }

    // 모든 게시물 목록 찾기
    @GetMapping
    public String getBoardsList(HttpServletRequest request, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "CREATED_AT_DESC") String order) {
        User user = (User) session.getAttribute("user");
        System.out.println("[LOGIN] sessionId=" + session.getId() + ", user=" + user.getUsername());
        List<BoardResponse.DTO> boardDTOList = boardService.getList(getFirstIndex(page), PER_PAGE + 1, safeOrder(order));
        boardDTOList = applyPagingFlags(request, boardDTOList, page);
        BoardResponse.mainListDTO resDTO = getMainList(boardDTOList, null);
        request.setAttribute("model", resDTO);
        return "board/list-page";
    }

    @GetMapping("/{id}")
    public String getBoard(HttpServletRequest request, @PathVariable(name = "id") Integer boardId) {
        User user = (User) session.getAttribute("user");
        BoardResponse.DetailDTO model = boardService.getBoardDetail(boardId);
        request.setAttribute("model", model);
        return "board/detail-page";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable(name = "id") Integer boardId, BoardRequest.BoardUpdateDTO reqDTO, HttpServletRequest request) {
        User user = (User) session.getAttribute("user");
        boardService.update(boardId, reqDTO, user);
        return "redirect:/boards/" + boardId;
    }

    // 게시글 저장하기
    @PostMapping
    public String save(BoardRequest.BoardSaveDTO reqDTO) {
        User user = (User) session.getAttribute("user");
        Board board = boardService.save(reqDTO, user);
        return "redirect:/boards/" + board.getId();
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable(name = "id") Integer boardId) {
        User user = (User) session.getAttribute("user");
        boardService.delete(boardId, user);
        return "redirect:/boards";
    }


    @PostMapping("/{id}/report")
    public String reportSave(@PathVariable(name = "id") Integer boardId, BoardRequest.ReportSaveDTO reqDTO) {
        User user = (User) session.getAttribute("user");
        BoardResponse.ReportSaveDTO resDTO = boardService.reportSave(user, boardId, reqDTO);
        return "redirect:/boards/" + boardId;
    }


    // 모든 게시물 목록 찾기
    @GetMapping("/me")
    public String getMyFeedList(HttpServletRequest request, BoardRequest.MyFeedDTO reqDTO) {
        User user = (User) session.getAttribute("user");
        List<BoardResponse.DTO> boardDTOList = boardService.getMyFeedList(getFirstIndex(reqDTO.getPage()), PER_PAGE + 1, safeOrder(reqDTO.getOrder()), user);
        boardDTOList = applyPagingFlags(request, boardDTOList, reqDTO.getPage());
        BoardResponse.mainListDTO resDTO = getMainList(boardDTOList, user);
        request.setAttribute("model", resDTO);
        return "board/myfeed-page";
    }


    /*                             search board list part
     * ----------------------------------------------------------------------------------
     */
    @GetMapping("/search")
    public String findList(HttpServletRequest request, @RequestParam String keyword, @RequestParam(defaultValue = "CREATED_AT_DESC") String order, @RequestParam(defaultValue = "1") Integer page) {
        List<BoardResponse.DTO> boardDTOList = boardService.findBy(keyword, safeOrder(order), getFirstIndex(page), PER_PAGE + 1);
        boardDTOList = applyPagingFlags(request, boardDTOList, page);
        BoardResponse.mainListDTO resDTO = getMainList(boardDTOList, null);
        request.setAttribute("model", resDTO);
        return "board/list-page";
    }

    /*                             private logic part
     * ----------------------------------------------------------------------------------
     */

    private SearchOrderCase safeOrder(String order) {
        try {
            return SearchOrderCase.valueOf(order);
        } catch (IllegalArgumentException e) {
            return SearchOrderCase.CREATED_AT_DESC; // 안전 기본값
        }
    }

    private int getFirstIndex(int page) {
        return (normalizePage(page) - 1) * PER_PAGE;
    }

    // 1) 페이지 보정 유틸
    private int normalizePage(Integer page) {
        return (page == null || page < 1) ? 1 : page;
    }

    /**
     * 오버페치 결과(rows)의 개수를 이용해 isFirst / isLast를 계산하고,
     * 화면에는 pageSize개만 잘라서 내려줍니다.
     * request에 page, nextPage, prevPage도 함께 심습니다.
     */
    private <T> List<T> applyPagingFlags(HttpServletRequest request, List<T> rows, Integer page) {
        boolean isFirst = page <= 1;
        boolean isLast = rows.size() <= PER_PAGE; // 오버페치로 한 개 더 왔으면 다음 페이지가 존재
        if (!isLast) rows = rows.subList(0, PER_PAGE); // 화면에는 pageSize개만
        request.setAttribute("isFirst", isFirst);
        request.setAttribute("isLast", isLast);
        return rows;
    }

    private BoardResponse.mainListDTO getMainList(List<BoardResponse.DTO> boardDTOList, User user) {
        int getSize = 3;

        List<BoardResponse.DTO> popularList = boardService.getPopularList(getSize);
        List<String> categoryList;
        if (user == null) {
            categoryList = categoryService.getPopularList(3);
        } else {
            categoryList = categoryService.getMyFeedPopularList(3, user);
        }
        return BoardResponse.mainListDTO
                .builder()
                .boardList(boardDTOList)
                .popularList(popularList)
                .categoryList(categoryList)
                .build();
    }
}
