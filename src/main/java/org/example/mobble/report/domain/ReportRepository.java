package org.example.mobble.report.domain;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.example.mobble.report.dto.ReportResponse;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class ReportRepository {
    private final EntityManager em;

    public void save(Report report) {
        em.persist(report);
    }


    public List<Report> findAll() {
        return em.createQuery("select r FROM Report r", Report.class)
                .getResultList();
    }

    public Optional<Report> findById(Integer reportId) {
        return Optional.ofNullable(em.find(Report.class, reportId));
    }

    public void delete(Report report) {
            em.remove(report);
    }

    public List<Report> findAllByUserId(Integer userId) {
        return em.createQuery("select r FROM Report r where r.user.id = :userId", Report.class)
                .setParameter("userId", userId)
                .getResultList();

    }
}
