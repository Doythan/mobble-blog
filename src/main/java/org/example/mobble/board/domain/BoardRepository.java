package org.example.mobble.board.domain;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.example.mobble._util.util.HtmlUtil;
import org.example.mobble.board.dto.BoardResponse;
import org.example.mobble.category.domain.Category;
import org.example.mobble.user.domain.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class BoardRepository {
    private final EntityManager em;

    public Board save(Board board) {
        em.persist(board);
        return board;
    }

    public Optional<Board> findById(Integer boardId) {
        return Optional.ofNullable(em.find(Board.class, boardId));
    }

    public Optional<BoardResponse.DetailDTO> findByIdDetail(Integer boardId, Integer userId) {
        List<Object[]> rows = em.createQuery("""
                        select b, u, c,
                               count(distinct bm),
                               count(distinct bm2) as myCount
                        from Board b
                        left join b.bookmarks bm
                        left join b.bookmarks bm2 on bm2.user.id = :userId and bm2.board.id = b.id
                        left join b.category c
                        left join b.user u
                        where b.id = :boardId
                        group by b, u, c
                        """, Object[].class)
                .setParameter("boardId", boardId)
                .setParameter("userId", userId)
                .getResultList();

        return rows.stream().findFirst().map(result ->
                BoardResponse.DetailDTO.builder()
                        .board((Board) result[0])
                        .user((User) result[1])
                        .category((Category) result[2])
                        .bookmarkCount(((Number) result[3]).intValue())
                        .isBookmark(((Number) result[4]).intValue() > 0)
                        .loginUserId(userId)
                        .build()
        );
    }


    public void delete(Integer boardId) {
        em.remove(em.find(Board.class, boardId));
    }

    /*                             search board list part
     * ----------------------------------------------------------------------------------
     */
    public List<BoardResponse.DTO> findAll(Integer userId, String orderBy, Integer firstIndex, Integer maxResult) {
        String jpql = getBaseJpql(null, orderBy);
        return mapping(
                em.createQuery(jpql, Object[].class)
                        .setParameter("userId", userId)
                        .setFirstResult(firstIndex)
                        .setMaxResults(maxResult)
                        .getResultList());
    }

    public List<BoardResponse.DTO> findByTitleAndContent(Integer userId, String keyword, String orderBy, Integer firstIndex, Integer maxResult) {
        String where = " where ( lower(b.title) like :q " +
                " or lower(cast(b.content as string)) like :q ) ";

        String jpql = getBaseJpql(where, orderBy);

        return mapping(getObjArrListWithParam(userId, keyword, jpql, firstIndex, maxResult));
    }

    public List<BoardResponse.DTO> findByCategory(Integer userId, String keyword, String orderBy, Integer firstIndex, Integer maxResult) {
        String jpql = getBaseJpql(" where lower(c.category) like :q ", orderBy);
        return mapping(getObjArrListWithParam(userId, keyword, jpql, firstIndex, maxResult));
    }

    public List<BoardResponse.DTO> findByUsername(Integer userId, String keyword, String orderBy, Integer firstIndex, Integer maxResult) {
        String jpql = getBaseJpql(" where lower(u.username) like :q ", orderBy);
        return mapping(getObjArrListWithParam(userId, keyword, jpql, firstIndex, maxResult));
    }

    /* ------------------------ private logic part ------------------------ */

    private String getBaseJpql(String whereClause, String orderByClause) {
        String where = (whereClause == null || whereClause.isBlank()) ? "" : whereClause;
        return """
                select b, u, c, count(bm), count(distinct bm2) as myCount
                from Board b
                left join Bookmark bm on bm.board.id = b.id
                left join b.bookmarks bm2 on bm2.user.id = :userId and bm2.board.id = b.id
                left join Category c on c.id = b.category.id
                left join User u on u.id = b.user.id
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
                    Boolean myBookmark = ((Number) row[4]).intValue() > 0;
                    return BoardResponse.DTO.builder()
                            .board(b)
                            .user(u)
                            .category(c)
                            .excerpt(HtmlUtil.extractFirstParagraph(b.getContent(), 100))
                            .bookmarkCount(cnt != null ? cnt.intValue() : 0)
                            .isBookmark(myBookmark)
                            .image(b.getThumbnailUrl())
                            .build();
                })
                .toList();
    }

    private List<Object[]> getObjArrListWithParam(Integer userId, String keyword, String jpql, Integer firstResult, Integer maxResult) {
        return em.createQuery(jpql, Object[].class)
                .setParameter("q", "%" + keyword + "%")
                .setParameter("userId", userId)
                .setFirstResult(firstResult)
                .setMaxResults(maxResult)
                .getResultList();
    }


    public List<BoardResponse.DTO> findAllByUserId(String orderBy, int firstIndex, int maxResult, User user) {
        String jpql = getBaseJpql(" where u.id = :userId ", orderBy);
        return mapping(
                em.createQuery(jpql, Object[].class)
                        .setParameter("userId", user.getId())
                        .setFirstResult(firstIndex)
                        .setMaxResults(maxResult)
                        .getResultList());
    }
}
