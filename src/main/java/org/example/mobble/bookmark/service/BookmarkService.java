package org.example.mobble.bookmark.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.mobble._util.error.ErrorEnum;
import org.example.mobble._util.error.ex.Exception302;
import org.example.mobble._util.error.ex.Exception401;
import org.example.mobble._util.error.ex.Exception403;
import org.example.mobble._util.error.ex.Exception404;
import org.example.mobble.board.domain.Board;
import org.example.mobble.board.domain.BoardRepository;
import org.example.mobble.bookmark.domain.Bookmark;
import org.example.mobble.bookmark.domain.BookmarkRepository;
import org.example.mobble.bookmark.domain.BookmarkSortType;
import org.example.mobble.bookmark.dto.BookmarkResponse;
import org.example.mobble.user.domain.User;
import org.example.mobble.user.domain.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    // 북마크 저장
    @Transactional
    public BookmarkResponse.BookmarkSaveDTO bookmarkSave(Integer boardId, Integer userId) {
        // board가 있는 지 먼저 확인
        Board foundBoard = boardRepository.findById(boardId).orElse(null);

        // board null이면 404 에러
        if (foundBoard == null) { throw new Exception404(ErrorEnum.NOT_FOUND); }

        // 북마크 찾아보고 있으면 에러, 없으면 저장
        Bookmark foundBookmark = bookmarkRepository.findByBoardIdAndUserId(boardId, userId).orElse(null);

        if (foundBookmark != null) {
            // 이미 북마크가 있으면 중복 에러 처리
            throw new Exception302(ErrorEnum.FOUND); // 또는 이미 존재 메시지
        } else {
            Optional<User> foundUser = userRepository.findById(userId);
            User user = foundUser.orElseThrow(() -> new Exception401(ErrorEnum.UNAUTHORIZED_NO_EXISTS_USER_INFO));
            Bookmark bookmark = Bookmark.builder()
                    .board(foundBoard)
                    .user(user)
                    .build();
            Bookmark bookmarkPs = bookmarkRepository.BookmarkSave(bookmark);
            return new BookmarkResponse.BookmarkSaveDTO(bookmarkPs);
        }

    }




    // 북마크 삭제
    @Transactional
    public void bookmarkDelete(Integer boardId, Integer userId) {
        // board가 있는 지 먼저 확인
        Board foundBoard = boardRepository.findById(boardId).orElse(null);

        // 없으면 404 에러
        if (foundBoard == null) { throw new Exception404(ErrorEnum.NOT_FOUND); }

        // 북마크 있는지 확인
        Bookmark foundBookmark = bookmarkRepository.findByBoardIdAndUserId(boardId, userId).orElse(null);

        // 북마크가 없으면
        if (foundBookmark == null) { throw new Exception404(ErrorEnum.NOT_FOUND);}

        // 북마크 유저아이디와 삭제요청한 유저아이디 맞는지 확인
        if (!foundBookmark.getUser().getId().equals(userId)) { throw new Exception403(ErrorEnum.FORBIDDEN);}

        bookmarkRepository.BookmarkDelete(boardId, userId);
    }

    // 북마크 리스트
    public BookmarkResponse.BookmarkListDTO bookmarkList(Integer userId, String sort,int page,int size) {
        BookmarkSortType sortType = BookmarkSortType.from(sort);
        List<Bookmark> bookmarkList;

        switch (sortType) {
            case CREATE_AT_DESC -> bookmarkList = bookmarkRepository.bookmarkListOrderByCreatedAt(userId,page,size);
            case VIEWS_DESC -> bookmarkList = bookmarkRepository.bookmarkListOrderByViews(userId,page,size);
            case BOOK_COUNT_DESC -> bookmarkList = bookmarkRepository.bookmarkListOrderByBookmarkCount(userId,page,size);
            default -> bookmarkList = bookmarkRepository.bookmarkListOrderByCreatedAt(userId,page,size);
        }

        return new BookmarkResponse.BookmarkListDTO(bookmarkList,page,size,bookmarkRepository.totalCount(userId));
    }

}
