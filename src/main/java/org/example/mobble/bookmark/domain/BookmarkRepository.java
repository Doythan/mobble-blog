package org.example.mobble.bookmark.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.example.mobble.board.domain.Board;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Repository
public class BookmarkRepository {
    private final EntityManager em;


    public Optional<Bookmark> findByBoardIdAndUserId(int boardId, int userId) {
        try {
            Bookmark BookmarkPS = em.createQuery(
                            "select b from Bookmark b where b.board.id = :boardId and b.user.id = :userId",
                            Bookmark.class
                    )
                    .setParameter("boardId", boardId)
                    .setParameter("userId", userId)
                    .getSingleResult();
            return Optional.of(BookmarkPS);
        }catch (Exception e) {
            return Optional.ofNullable(null);
        }
    }


    public void BookmarkDelete(Integer boardId, Integer userId) {
        Query query =  em.createQuery("delete from Bookmark b where b.board.id = :boardId and b.user.id = :userId");
        query.setParameter("boardId", boardId);
        query.setParameter("userId", userId);
        query.executeUpdate();
    }

    public Bookmark BookmarkSave(Bookmark bookmark) {
       em.persist(bookmark);
        return bookmark;
    }

    /*----------------------------------------------------*/
    public List<Bookmark> bookmarkListOrderByCreatedAt(Integer userId,int page,int size) {
        return em.createQuery("select bm from Bookmark bm join fetch bm.board b join fetch b.user where bm.user.id = :userId order by b.createdAt desc ,bm.board.id DESC ", Bookmark.class)
                .setParameter("userId", userId)
                .setFirstResult(page * size)   // OFFSET
                .setMaxResults(size)           // LIMIT
                .getResultList();
    }

    public List<Bookmark> bookmarkListOrderByViews(Integer userId,int page,int size) {
        return em.createQuery("select bm from Bookmark bm join fetch bm.board b join fetch b.user where bm.user.id = :userId order by b.views DESC, bm.board.id ",Bookmark.class)
                .setParameter("userId",userId)
                .setFirstResult(page * size)   // OFFSET
                .setMaxResults(size)           // LIMIT
                .getResultList();
    }

    public List<Bookmark> bookmarkListOrderByBookmarkCount(Integer userId,int page,int size) {
        String jpql = """
        select bm
        from Bookmark bm
        join fetch bm.board b
        join fetch b.user
        where bm.user.id = :userId
        order by
          (select count(bm2) from Bookmark bm2 where bm2.board = b) desc,
          b.id desc,
          bm.id desc
        """;

        return em.createQuery(jpql, Bookmark.class)
                .setParameter("userId", userId)
                .setFirstResult(page * size)   // OFFSET
                .setMaxResults(size)           // LIMIT
                .getResultList();
    }

    public Long totalCount(Integer userId) {
        return (Long) em.createQuery("select count(b) from Bookmark b where b.user.id = :userId")
                .setParameter("userId", userId)
                .getSingleResult();
    }
}
