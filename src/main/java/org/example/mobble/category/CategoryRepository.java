package org.example.mobble.category;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class CategoryRepository {
    private final EntityManager em;

    public Category save(Category category) {
        em.persist(category);
        return category;
    }

    public Optional<Category> findByUserIdAndCategory(Integer id, String category) {
        try {
            return Optional.ofNullable(em.createQuery("select c from Category c where c.userId = :userId and c.category = :category", Category.class)
                    .setParameter("userId", id)
                    .setParameter("category", category)
                    .getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
