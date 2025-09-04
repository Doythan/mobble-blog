package org.example.mobble.board.domain;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.example.mobble.board.dto.BoardResponse;
import org.example.mobble.category.Category;
import org.example.mobble.user.domain.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class BoardRepository {
    private final EntityManager em;

    public Board save(Board board) {
        em.persist(board);
        return board;
    }

    public Optional<Board> findById(Integer boardId) {
        return Optional.ofNullable(em.find(Board.class, boardId));
    }

    public Optional<BoardResponse.DetailDTO> findByIdDetail(Integer boardId) {
        return Optional.ofNullable(em.createQuery("""
                        select b, u, c, bm
                        from Board b
                        left join Bookmark bm on bm.boardId = b.id
                        left join Category c on c.id = b.categoryId
                        left join User u on u.id = b.userId
                        where b.id = :boardId
                        """, BoardResponse.DetailDTO.class)
                .setParameter("boardId", boardId).getSingleResult());
    }

    public Optional<BoardResponse.DetailDTO> findByIdDetail(Integer boardId, Integer userId) {
        return Optional.ofNullable(em.createQuery("""
                        select b, u, c, bm
                        from Board b
                        left join Bookmark bm on bm.boardId = b.id
                        left join Category c on c.id = b.categoryId
                        left join User u on u.id = b.userId
                        where b.id = :boardId
                        and b.userId = :userId
                        """, BoardResponse.DetailDTO.class)
                .setParameter("boardId", boardId)
                .setParameter("userId", userId)
                .getSingleResult());
    }

    public void delete(Integer boardId) {
        em.remove(em.find(Board.class, boardId));
    }

    /*                             search board list part
     * ----------------------------------------------------------------------------------
     */
    public List<BoardResponse.DTO> findAll(String orderBy, Integer firstIndex, Integer maxResult) {
        String jpql = getBaseJpql(null, orderBy);
        return mapping(
                em.createQuery(jpql, Object[].class)
                        .setFirstResult(firstIndex)
                        .setMaxResults(maxResult)
                        .getResultList());
    }

    public List<BoardResponse.DTO> findByTitleAndContent(String keyword, String orderBy, Integer firstIndex, Integer maxResult) {
        String jpql = getBaseJpql(" where (lower(b.title) like :q or lower(b.content) like :q) ", orderBy);

        return mapping(getObjArrListWithParam(keyword, jpql, firstIndex, maxResult));
    }

    public List<BoardResponse.DTO> findByCategory(String keyword, String orderBy, Integer firstIndex, Integer maxResult) {
        String jpql = getBaseJpql(" where lower(c.category) like :q ", orderBy);
        return mapping(getObjArrListWithParam(keyword, jpql, firstIndex, maxResult));
    }

    public List<BoardResponse.DTO> findByUsername(String keyword, String orderBy, Integer firstIndex, Integer maxResult) {
        String jpql = getBaseJpql(" where lower(u.username) like :q ", orderBy);
        return mapping(getObjArrListWithParam(keyword, jpql, firstIndex, maxResult));
    }

    /* ------------------------ private logic part ------------------------ */

    private String getBaseJpql(String whereClause, String orderByClause) {
        String where = (whereClause == null || whereClause.isBlank()) ? "" : whereClause;
        return """
                select b, u, c, count(bm)
                from Board b
                left join Bookmark bm on bm.boardId = b.id
                left join Category c on c.id = b.categoryId
                left join User u on u.id = b.userId
                """ + where +
                " group by b, u, c " +
                safeOrderBy(orderByClause);
    }

    // orderBy 외부 문자열을 쓸 경우 방어적으로 공백/기본값 보정
    private String safeOrderBy(String orderByClause) {
        if (orderByClause == null || orderByClause.isBlank()) {
            // 기본 정렬(예: 최신순 + 타이브레이커)
            return " order by b.createdAt desc, b.id desc";
        }
        // 반드시 앞에 공백 포함해서 붙이기
        String trimmed = orderByClause.strip();
        return trimmed.toLowerCase().startsWith("order by") ? " " + trimmed : " order by " + trimmed;
    }

    private List<BoardResponse.DTO> mapping(List<Object[]> rows) {
        return rows.stream()
                .map(row -> {
                    Board b = (Board) row[0];
                    User u = (User) row[1];
                    Category c = (Category) row[2];
                    Long cnt = (Long) row[3];

                    return BoardResponse.DTO.builder()
                            .board(b)
                            .user(u)
                            .category(c)
                            .bookmarkCount(cnt != null ? cnt.intValue() : 0)
                            .image(null)
                            .build();
                })
                .toList();
    }

    private List<Object[]> getObjArrListWithParam(String keyword, String jpql, Integer firstResult, Integer maxResult) {
        return em.createQuery(jpql, Object[].class)
                .setParameter("q", "%" + keyword + "%")
                .setFirstResult(firstResult)
                .setMaxResults(maxResult)
                .getResultList();
    }


}
