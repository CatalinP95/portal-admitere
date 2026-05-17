package com.campus.userservice.repository;

import com.campus.userservice.model.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    Page<Announcement> findByEnabledTrue(Pageable pageable);

    Page<Announcement> findByEnabledTrueAndTagsName(String tagName, Pageable pageable);

    @Query(value = "SELECT DISTINCT a FROM Announcement a LEFT JOIN a.tags t WHERE a.enabled = true " +
                   "AND (:tag IS NULL OR t.name = :tag) " +
                   "AND (:search IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :search, '%')))",
           countQuery = "SELECT COUNT(DISTINCT a.id) FROM Announcement a LEFT JOIN a.tags t WHERE a.enabled = true " +
                        "AND (:tag IS NULL OR t.name = :tag) " +
                        "AND (:search IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Announcement> findWithFilters(@Param("tag") String tag, @Param("search") String search, Pageable pageable);
}
