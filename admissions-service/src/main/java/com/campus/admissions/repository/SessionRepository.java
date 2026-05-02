package com.campus.admissions.repository;

import com.campus.admissions.model.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Integer> {

    Session findSessionById(Integer id);
    List<Session> findByTypeAndEnabled(int type, int enabled);
    List<Session> findByTypeInAndEnabled(ArrayList<Integer> types, int enabled);
    List<Session> findByEnabled(int enabled);
    Page<Session> findAll(Pageable pageable);

    @Query("SELECT s FROM session s WHERE s.enabled = 1 AND s.startDate <= :now AND s.endDate >= :now")
    List<Session> findActiveSessions(@Param("now") Date now);

    List<Session> findByAnUniversitar(String anUniversitar);
}
