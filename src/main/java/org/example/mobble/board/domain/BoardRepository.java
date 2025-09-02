package org.example.mobble.board.domain;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.example.mobble.board.dto.BoardResponse;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class BoardRepository {
    private final EntityManager em;

    public List<BoardResponse.DTO> findAllCreatedAtWithBookmarkCount() {
        List<Object[]> rows = em.createQuery(
                "select b, count(bm) " +
                        "from Board b " +
                        "left join Bookmark bm on bm.boardId = b.id " +
                        "group by b " +
                        "order by b.createdAt desc",
                Object[].class
        ).getResultList();
        return rows.stream()
                .map(row -> {
                    Board b = (Board) row[0];
                    Long cnt = (Long) row[1];
                    return BoardResponse.DTO.builder()
                            .board(b)
                            .bookmarkCount(cnt != null ? cnt.intValue() : 0)
                            // TODO: imageUrl이 다른 테이블이면 여기서 채워 넣기
                            .imageUrl(null)
                            .build();
                })
                .toList();
    }

    public Board save(Board board) {
        em.persist(board);
        return board;
    }

    public Optional<Board> findById(Integer boardId) {
        try {
            // 이 줄만
            return Optional.ofNullable(em.find(Board.class, boardId));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void delete(Board boardPS) {
        em.remove(boardPS.getId());
    }
}
