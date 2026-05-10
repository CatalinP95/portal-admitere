package com.campus.userservice.repository;

import com.campus.userservice.model.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    Page<Announcement> findByEnabledTrue(Pageable pageable);

    Page<Announcement> findByEnabledTrueAndTagsName(String tagName, Pageable pageable);
}
