package org.example.mobble.board.service;


import lombok.RequiredArgsConstructor;
import org.example.mobble._util.error.ex.Exception400;
import org.example.mobble._util.error.ex.Exception403;
import org.example.mobble._util.error.ex.Exception404;
import org.example.mobble.board.domain.Board;
import org.example.mobble.board.domain.BoardRepository;
import org.example.mobble.board.dto.BoardRequest;
import org.example.mobble.board.dto.BoardResponse;
import org.example.mobble.category.Category;
import org.example.mobble.category.CategoryRepository;
import org.example.mobble.report.domain.Report;
import org.example.mobble.report.domain.ReportCase;
import org.example.mobble.report.domain.ReportRepository;
import org.example.mobble.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final CategoryRepository categoryRepository;
    private final ReportRepository reportRepository;

    public List<BoardResponse.DTO> getList() {
        return boardRepository.findAllCreatedAtWithBookmarkCount();
    }

    @Transactional
    public Board save(BoardRequest.BoardSaveDTO reqDTO, User user) {
        Category category = categoryRepository.findByUserIdAndCategory(user.getId(), reqDTO.getCategory()).orElse(null);
        if (category == null) {
            category = categoryRepository.save(
                    Category.builder()
                            .userId(user.getId())
                            .category(reqDTO.getCategory())
                            .build()
            );
        }
        Board board =
                Board.builder()
                        .title(reqDTO.getTitle())
                        .content(reqDTO.getContent())
                        .userId(user.getId())
                        .categoryId(category.getId())
                        .build();
        return boardRepository.save(board);
    }

    public Board getBoard(Integer boardId) {
        return boardRepository.findById(boardId).orElseThrow(
                () -> new Exception404("해당 게시글이 존재하지 않습니다.")
        );
    }

    @Transactional
    public void update(Integer boardId, BoardRequest.BoardUpdateDTO reqDTO, User user) {
        if (boardId == null || boardId.equals(reqDTO.getId()))
            throw new Exception400("잘못된 요청입니다. 게시글 목록으로 돌아간 뒤 다시시도해주세요.");
        Board boardPS = getBoard(boardId);
        // 권한 체크 (403)
        checkPermissions(boardPS, user);
        boardPS.update(reqDTO);
    }

    @Transactional
    public void delete(Integer boardId, User user) {
        if (boardId == null) throw new Exception400("잘못된 요청입니다. 게시글 목록으로 돌아간 뒤 다시시도해주세요.");
        Board boardPS = getBoard(boardId);
        // 권한 체크 (403)
        checkPermissions(boardPS, user);
        boardRepository.delete(boardPS);
    }

    @Transactional
    public void report(User user, Integer boardId, BoardRequest.BoardReportDTO reqDTO) {
        Board boardPS = getBoard(boardId);
        ReportCase reportCase = ReportCase.valueOf(reqDTO.getResult());
        Report report =
                Report.builder()
                        .userId(user.getId())
                        .boardId(boardId)
                        .content(reqDTO.getContent())
                        .result(reportCase)
                        .build();
        if (reportCase.equals(ReportCase.ETC)) report.updateResultEtc(reqDTO.getResultEtc());
        reportRepository.save(report);

    }

    // 권한 확인 로직
    private void checkPermissions(Board board, User user) {
        if (!board.getUserId().equals(user.getId())) throw new Exception403("해당 게시물을 등록한 사용자가 아닙니다.");
    }
}
