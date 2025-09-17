package org.example.mobble.user.domain;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    public Optional<User> findActiveByUsername(String username) {
        try {
            return Optional.ofNullable(
                    em.createQuery(
                                    "select u from User u where u.username = :username and u.status <> :deleted",
                                    User.class
                            )
                            .setParameter("username", username)
                            .setParameter("deleted", UserStatus.DELETED)
                            .getSingleResult()
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<User> findAll() {
        return em.createQuery("select u from User u order by u.id", User.class)
                .getResultList();
    }

    public void save(User user) {
        em.persist(user);
    }

    public boolean existsByUsername(String username) {
        return em.createQuery("select count(u) from User u where u.username = :username", Long.class)
                .setParameter("username", username)
                .getSingleResult() > 0;
    }

    public Optional<User> findById(Integer userId) {
        return Optional.ofNullable(em.find(User.class, userId));
    }

    public void delete(Integer userId) {
        em.remove(em.find(User.class, userId));
    }
}
