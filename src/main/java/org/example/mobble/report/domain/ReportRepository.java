package org.example.mobble.report.domain;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ReportRepository {
    private final EntityManager em;

    public void save(Report report) {
        em.persist(report);
    }
}
