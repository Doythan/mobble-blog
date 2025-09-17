package org.example.mobble.board.service;


import lombok.RequiredArgsConstructor;
import org.example.mobble._util.error.ErrorEnum;
import org.example.mobble._util.error.ex.Exception400;
import org.example.mobble._util.error.ex.Exception403;
import org.example.mobble._util.error.ex.Exception404;
import org.example.mobble._util.util.HtmlUtil;
import org.example.mobble._util.util.ImgUtil;
import org.example.mobble._util.util.MarkdownUtil;
import org.example.mobble.board.domain.Board;
import org.example.mobble.board.domain.BoardRepository;
import org.example.mobble.board.domain.SearchOrderCase;
import org.example.mobble.board.dto.BoardRequest;
import org.example.mobble.board.dto.BoardResponse;
import org.example.mobble.category.domain.Category;
import org.example.mobble.category.domain.CategoryRepository;
import org.example.mobble.report.domain.Report;
import org.example.mobble.report.domain.ReportCase;
import org.example.mobble.report.domain.ReportRepository;
import org.example.mobble.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final CategoryRepository categoryRepository;
    private final ReportRepository reportRepository;

    @Transactional(readOnly = true)
    public List<BoardResponse.DTO> getList(User user, int firstIndex, int size, SearchOrderCase order) {
        String orderBy = orderByToString(order);
        return boardRepository.findAll(user.getId(), orderBy, firstIndex, size);
    }

    @Transactional()
    public BoardResponse.DetailDTO getBoardDetail(Integer boardId, User user) {
        Board boardPS = boardRepository.findById(boardId).orElseThrow(() -> new Exception404(ErrorEnum.NOT_FOUND_BOARD));
        boardPS.viewsCounting();
        boardRepository.flush();
        BoardResponse.DetailDTO respDTO = boardRepository.findByIdDetail(boardId, user.getId()).orElseThrow(
                () -> new Exception404(ErrorEnum.NOT_FOUND_BOARD));
        return respDTO;
    }

    @Transactional(readOnly = true)
    public BoardResponse.DetailDTO getUpdateBoardDetail(Integer boardId, User user) {
        checkPermissions(findById(boardId), user);
        return getBoardDetail(boardId, user);
    }

    @Transactional
    public Board save(BoardRequest.BoardSaveDTO reqDTO, User user) {
        Category category = categoryRepository.findByUserIdAndCategory(user.getId(), reqDTO.getCategory()).orElse(null);
        if (category == null) {
            category = categoryRepository.save(
                    Category.builder()
                            .user(user)
                            .category(reqDTO.getCategory())
                            .build()
            );
        }
        String safeHtml = HtmlUtil.HtmlSanitizer.sanitize(reqDTO.getContent());
        String finalHtml = MarkdownUtil.applyBasicMarkdown(safeHtml);

        Board board =
                Board.builder()
                        .title(reqDTO.getTitle())
                        .content("")
                        .user(user)
                        .category(category)
                        .views(0)
                        .build();

        board = boardRepository.save(board);

        ImgUtil.Result r;
        try {
            r = ImgUtil.replaceDataUrlsWithSavedFiles(
                    finalHtml,
                    user.getUsername(),
                    /* 저장 전엔 ID가 없을 수 있으니, 임시 0 or 저장 후에 재치환 전략 중 택1 */
                    board.getId(),
                    LocalDateTime.now()
            );
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패", e);
        }

        // boardId를 얻은 후 사진 새로 저장, html 및 이미지 갱신
        board.saveThumbnail(r.html(), r.firstImageUrl());

        return board;
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

        Map<String, byte[]> oldImg = getOldImgData(Paths.get("src/main/resources/static/img"), user.getUsername() + "-" + boardId, reqDTO);
        // 0) 기존 이미지 삭제
        ImgUtil.deleteAllImagesForPost(user.getUsername(), boardId);
        // 재사용 파일 저장
        saveOldFile(oldImg);
        // HTML 정화
        String safeHtml = HtmlUtil.HtmlSanitizer.sanitize(reqDTO.getContent());
        String finalHtml = MarkdownUtil.applyBasicMarkdown(safeHtml);
        // 2) dataURL → 파일 저장 & src 교체 (이번에는 boardId가 있으니 네이밍 완벽)
        ImgUtil.Result r;
        try {
            r = ImgUtil.replaceDataUrlsWithSavedFiles(
                    finalHtml,
                    user.getUsername(),
                    boardId,
                    LocalDateTime.now()
            );
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패", e);
        }

        System.out.println("firstImage : " + r.firstImageUrl());

        reqDTO.setContent(r.html());
        boardPS.update(reqDTO, r.firstImageUrl());

        return boardPS;
    }

    private void saveOldFile(Map<String, byte[]> oldImg) {
        Path targetDir = Paths.get("src/main/resources/static/img");

        try {
            Files.createDirectories(targetDir); // 경로 없으면 생성
        } catch (IOException e) {
            System.out.println("디렉토리 생성 실패: " + e.getMessage());
            return;
        }

        for (Map.Entry<String, byte[]> entry : oldImg.entrySet()) {
            String fileName = entry.getKey();
            byte[] data = entry.getValue();

            try {
                Path targetPath = targetDir.resolve(fileName);

                Files.write(targetPath, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

                System.out.println("이미지 재저장 완료: " + targetPath);
            } catch (Exception e) {
                System.out.println("이미지 재저장 실패: " + fileName + " → " + e.getMessage());
            }
        }
    }

    private Map<String, byte[]> getOldImgData(Path path, String prefix, BoardRequest.BoardUpdateDTO reqDTO) {
        try (Stream<Path> paths = Files.list(path)) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .filter(f -> f.getName().startsWith(prefix))
                    .filter(f -> reqDTO.getContent().contains(f.getName()))
                    .collect(Collectors.toMap(
                            File::getName,
                            f -> {
                                try {
                                    return Files.readAllBytes(f.toPath());
                                } catch (IOException e) {
                                    throw new UncheckedIOException(e);
                                }
                            }
                    ));
        } catch (Exception e) {
            System.out.println("기존 파일 불러오기 실패: " + e.getMessage());
            return Map.of();
        }
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
    public BoardResponse.ReportSaveDTO reportSave(User user, Integer boardId, BoardRequest.ReportSaveDTO reqDTO) {
        Board boardPS = findById(boardId);
        Report report =
                Report.builder()
                        .user(user)
                        .board(boardPS)
                        .content(reqDTO.getContent())
                        .result(reqDTO.getResult())
                        .build();
        if (reqDTO.getResult().equals(ReportCase.ETC)) report.updateResultEtc(reqDTO.getResultEtc());
        reportRepository.save(report);

        return new BoardResponse.ReportSaveDTO(report);

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
    public List<BoardResponse.DTO> findBy(User user, String keyword, SearchOrderCase order, Integer firstIndex, Integer size) {
        String orderBy = orderByToString(order);
        String q = keyword == null ? "" : keyword.trim();
        if (q.isEmpty()) throw new Exception400(ErrorEnum.BAD_REQUEST_NO_EXISTS_KEYWORD);
        char searchKey = q.charAt(0);
        if (q.length() == 1 && (searchKey == '#' || searchKey == '@'))
            throw new Exception400(ErrorEnum.BAD_REQUEST_ONLY_PREFIX);
        q = (searchKey == '#' || searchKey == '@') ? q.substring(1) : q;
        return switch (searchKey) {
            case '#' -> boardRepository.findByCategory(user.getId(), q, orderBy, firstIndex, size);
            case '@' -> boardRepository.findByUsername(user.getId(), q, orderBy, firstIndex, size);
            default -> boardRepository.findByTitleAndContent(user.getId(), q, orderBy, firstIndex, size);
        };
    }

    /*                             private logic part
     * ----------------------------------------------------------------------------------
     */
    // 권한 확인 로직
    private void checkPermissions(Board board, User user) {
        if (!board.getUser().getId().equals(user.getId())) throw new Exception403(ErrorEnum.FORBIDDEN_USER_AT_BOARD);
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

    @Transactional(readOnly = true)
    public List<BoardResponse.DTO> getPopularList(User user, int size) {
        return getList(user, 0, size, SearchOrderCase.VIEW_COUNT_DESC);
    }


    //마이 피드 list
    public List<BoardResponse.DTO> getMyFeedList(int firstIndex, int size, SearchOrderCase order, User user) {

        String orderBy = orderByToString(order);
        return boardRepository.findAllByUserId(orderBy, firstIndex, size, user);
    }
}
