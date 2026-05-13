package com.campus.admissions.repository;

import com.campus.admissions.model.Application;
import com.campus.admissions.model.Faculty;
import com.campus.admissions.model.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Integer> {

    Page<Application> findByFacultyAndUserId(Faculty faculty, Long userId, Pageable pageable);
    Page<Application> findAll(Pageable pageable);
    Page<Application> findByUserId(Long userId, Pageable pageable);

    List<Application> findByUserIdAndSession(Long userId, Session session);
    List<Application> findByUserId(Long userId);
    List<Application> findBySessionIdAndStatus(Integer sessionId, String status);
    List<Application> findBySessionAndStatus(Session session, String status);
    List<Application> findBySessionAndFacultyAndStatus(Session session, Faculty faculty, String status);
    boolean existsByUserIdAndSessionIdAndEnabled(Long userId, Integer sessionId, Integer enabled);
    @Query("SELECT a FROM addmissionapplic a WHERE a.session = :session AND a.faculty = :faculty " +
           "AND a.status IN ('PENDING','APPROVED','WAITING_LIST') ORDER BY a.waitingListPosition ASC NULLS FIRST")
    List<Application> findRankedBySessionAndFaculty(@Param("session") Session session,
                                                     @Param("faculty") Faculty faculty);

    @Query("SELECT a FROM addmissionapplic a WHERE a.session = :session AND a.faculty = :faculty " +
           "AND a.status = 'WAITING_LIST' ORDER BY a.waitingListPosition ASC")
    List<Application> findWaitingListBySessionAndFaculty(@Param("session") Session session,
                                                          @Param("faculty") Faculty faculty);

    @Query("SELECT a FROM addmissionapplic a WHERE a.status = 'APPROVED' AND a.confirmationDeadline < :now")
    List<Application> findExpiredApplications(@Param("now") Date now);

}
