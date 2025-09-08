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

    public List<Bookmark> bookmarkList(Integer userId) {
        return em.createQuery("select bm from Bookmark bm join fetch bm.board b join fetch b.user where bm.user.id = :userId", Bookmark.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}
