package org.example.mobble.user.domain;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class UserRepository {
    private final EntityManager em;

    public Optional<User> findByUsername(String username) {
        try {
            return Optional.ofNullable(em.createQuery("select u from User u where u.username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void save(User user) {
        em.persist(user);
    }

    public boolean existsByNickname(String username) {
        return em.createQuery("select count(u) from User u where u.username = :username", Long.class)
                .setParameter("username", username)
                .getSingleResult() > 0;
    }

    public Optional<User> findById(Integer userId) {
        try {
            // 이 줄만 있어도 정상 작동
            return Optional.ofNullable(em.find(User.class, userId));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void delete(User user) {
        // userId -> em.remove(em.find(User.class, userId));
        em.remove(user);
    }
}
