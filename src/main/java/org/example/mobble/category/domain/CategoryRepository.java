package org.example.mobble.category.domain;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class CategoryRepository {
    private final EntityManager em;

    // 새로운 카테고리 저장
    public Category save(Category category) {
        em.persist(category);
        return category;
    }

    // 특정 유저의 모든 카테고리 목록 조회(id 내림차순 -> 최신순)
    public List<Category> findAllByUserIdOrderByIdDesc(Integer userId) {
        return em.createQuery(
                "select c from Category c where c.user.id = :userId order by c.id desc",
                Category.class
        ).setParameter("userId", userId).getResultList();
    }

    public List<String> getPopularList(Integer maxResult) {
        return em.createQuery("select c.category from Category c group by c.category order by count(c) desc", String.class).setFirstResult(0).setMaxResults(maxResult).getResultList();
    }

    // PK(id)로 카테고리 단건 조회
    public Optional<Category> findById(Integer id) {
        return Optional.ofNullable(em.find(Category.class, id));
    }

    // 특정 유저의 특정 카테고리명으로 단건 조회
    public Optional<Category> findByUserIdAndCategory(Integer userId, String category) {
        // 예외 대신 리스트로 안전 조회
        List<Category> rows = em.createQuery(
                        "select c from Category c where c.user.id = :userId and c.category = :category",
                        Category.class
                ).setParameter("userId", userId)
                .setParameter("category", category)
                .setMaxResults(1)
                .getResultList();
        return rows.stream().findFirst();
    }

    // 특정 유저가 같은 이름의 카테고리를 가지고 있는지 여부 확인
    public boolean existsByUserIdAndCategory(Integer userId, String category) {
        // 가장 가벼운 존재 확인: 1건만 조회
        List<Integer> rows = em.createQuery(
                        "select 1 from Category c where c.user.id = :userId and c.category = :category",
                        Integer.class
                ).setParameter("userId", userId)
                .setParameter("category", category)
                .setMaxResults(1)
                .getResultList();
        return !rows.isEmpty();
    }

    // PK(id)로 카테고리 삭제
    public void deleteById(Integer id) {
        Category found = em.find(Category.class, id);
        if (found != null) em.remove(found);
    }

    public void detachBoards(Integer categoryId) {
        em.createQuery("update Board b set b.category = null where b.category.id = :cid")
                .setParameter("cid", categoryId)
                .executeUpdate();
    }

}
