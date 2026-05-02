package com.campus.admissions.service;

import com.campus.admissions.model.Session;
import com.campus.admissions.repository.SessionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public Page<Session> findAll(Pageable pageable) {
        return sessionRepository.findAll(pageable);
    }

    public List<Session> findAll() {
        return sessionRepository.findAll();
    }

    public Session findById(Integer id) {
        return sessionRepository.findSessionById(id);
    }

    public List<Session> findByTypeAndEnabled(int type, int enabled) {
        return sessionRepository.findByTypeAndEnabled(type, enabled);
    }

    public List<Session> findByTypeInAndEnabled(ArrayList<Integer> types, int enabled) {
        return sessionRepository.findByTypeInAndEnabled(types, enabled);
    }

    public List<Session> findActiveSessions() {
        return sessionRepository.findActiveSessions(new Date());
    }

    public Session save(Session session) {
        return sessionRepository.save(session);
    }

    public void delete(Session session) {
        sessionRepository.delete(session);
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> findCustom(String start, String end) {
        return entityManager.createNativeQuery(
            "select * from session where start_date >= '" + start +
            "' and end_date <= '" + end + "' and enabled = 1 and type = 0").getResultList();
    }
}
