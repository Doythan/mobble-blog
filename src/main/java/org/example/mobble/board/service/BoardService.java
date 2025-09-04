package org.example.mobble.board.service;


import lombok.RequiredArgsConstructor;
import org.example.mobble._util.error.ErrorEnum;
import org.example.mobble._util.error.ex.Exception400;
import org.example.mobble._util.error.ex.Exception403;
import org.example.mobble._util.error.ex.Exception404;
import org.example.mobble.board.domain.Board;
import org.example.mobble.board.domain.BoardRepository;
import org.example.mobble.board.domain.SearchOrderCase;
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

    @Transactional(readOnly = true)
    public List<BoardResponse.DTO> getList(int firstIndex, int size) {
        String orderBy = orderByToString(SearchOrderCase.CREATED_AT_DESC);
        return boardRepository.findAll(orderBy, firstIndex, size);
    }

    @Transactional(readOnly = true)
    public BoardResponse.DetailDTO getBoardDetail(Integer boardId) {
        return boardRepository.findByIdDetail(boardId).orElseThrow(
                () -> new Exception404(ErrorEnum.NOT_FOUND_BOARD)
        );
    }
    
    @Transactional(readOnly = true)
    public BoardResponse.DetailDTO getUpdateBoardDetail(Integer boardId, User user) {
        checkPermissions(findById(boardId), user);
        return getBoardDetail(boardId);
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

    @Transactional
    public Board update(Integer boardId, BoardRequest.BoardUpdateDTO reqDTO, User user) {
        checkBoardId(boardId);
        checkBoardId(reqDTO.getId());
        if (!boardId.equals(reqDTO.getId()))
            throw new Exception400(ErrorEnum.BAD_REQUEST_NO_MATCHED_BOARD_ID);
        Board boardPS = findById(boardId);
        // 권한 체크 (403)
        checkPermissions(boardPS, user);
        boardPS.update(reqDTO);

        return boardPS;
    }

    @Transactional
    public void delete(Integer boardId, User user) {
        checkBoardId(boardId);
        Board boardPS = findById(boardId);
        // 권한 체크 (403)
        checkPermissions(boardPS, user);
        boardRepository.delete(boardId);
    }

    @Transactional
    public Report report(User user, Integer boardId, BoardRequest.BoardReportDTO reqDTO) {
        Board boardPS = findById(boardId);
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
        return report;

    }

    /*                             search board list part
     * ----------------------------------------------------------------------------------
     */

    private Board findById(Integer boardId) {
        return boardRepository.findById(boardId).orElseThrow(
                () -> new Exception404(ErrorEnum.NOT_FOUND_BOARD)
        );
    }

    @Transactional(readOnly = true)
    public List<BoardResponse.DTO> findBy(String keyword, SearchOrderCase order, Integer firstIndex, Integer size) {
        String orderBy = orderByToString(order);
        String q = keyword == null ? "" : keyword.trim();
        if (q.isEmpty()) throw new Exception400(ErrorEnum.BAD_REQUEST_NO_EXISTS_KEYWORD);
        char searchKey = q.charAt(0);
        if (q.length() == 1 && (searchKey == '#' || searchKey == '@'))
            throw new Exception400(ErrorEnum.BAD_REQUEST_ONLY_PREFIX);
        q = (searchKey == '#' || searchKey == '@') ? q.substring(1) : q;
        return switch (searchKey) {
            case '#' -> boardRepository.findByCategory(q, orderBy, firstIndex, size);
            case '@' -> boardRepository.findByUsername(q, orderBy, firstIndex, size);
            default -> boardRepository.findByTitleAndContent(q, orderBy, firstIndex, size);
        };
    }

    /*                             private logic part
     * ----------------------------------------------------------------------------------
     */
    // 권한 확인 로직
    private void checkPermissions(Board board, User user) {
        if (!board.getUserId().equals(user.getId())) throw new Exception403(ErrorEnum.FORBIDDEN_USER_AT_BOARD);
    }

    private void checkBoardId(Integer boardId) {
        if (boardId == null) throw new Exception400(ErrorEnum.BAD_REQUEST_NO_EXISTS_BOARD_ID);
    }

    // 🔢 정렬 컬럼 결정 (bookmarkCount는 count(bm))
    private String orderByToString(SearchOrderCase order) {
        String orderColumn = switch (order) {
            case VIEW_COUNT_ASC, VIEW_COUNT_DESC -> "b.views";
            case BOOKMARK_COUNT_ASC, BOOKMARK_COUNT_DESC -> "count(bm)";
            default -> "b.createdAt";
        };
        String direction = order.getDirection().isAscending() ? "asc" : "desc";
        return orderColumn + " " + direction + ", b.id desc";
    }


}
