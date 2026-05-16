package com.campus.userservice.service;

import com.campus.userservice.dto.AnnouncementDto;
import com.campus.userservice.dto.AnnouncementRequest;
import com.campus.userservice.exception.AnnouncementNotFoundException;
import com.campus.userservice.model.Announcement;
import com.campus.userservice.model.Tag;
import com.campus.userservice.repository.AnnouncementRepository;
import com.campus.userservice.repository.TagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AnnouncementService {

    private static final Logger log = LoggerFactory.getLogger(AnnouncementService.class);

    private final AnnouncementRepository announcementRepository;
    private final TagRepository tagRepository;

    public AnnouncementService(AnnouncementRepository announcementRepository,
                                TagRepository tagRepository) {
        this.announcementRepository = announcementRepository;
        this.tagRepository = tagRepository;
    }

    @Transactional(readOnly = true)
    public Page<AnnouncementDto> getAll(Pageable pageable) {
        return announcementRepository.findByEnabledTrue(pageable).map(AnnouncementDto::from);
    }

    @Transactional(readOnly = true)
    public Page<AnnouncementDto> getByTag(String tagName, Pageable pageable) {
        return announcementRepository.findByEnabledTrueAndTagsName(tagName, pageable)
                .map(AnnouncementDto::from);
    }

    @Transactional(readOnly = true)
    public Page<AnnouncementDto> getFiltered(String tag, String search, Pageable pageable) {
        String tagParam = (tag == null || tag.isBlank()) ? null : tag.toUpperCase();
        String searchParam = (search == null || search.isBlank()) ? null : search;
        return announcementRepository.findWithFilters(tagParam, searchParam, pageable)
                .map(AnnouncementDto::from);
    }

    @Transactional
    public AnnouncementDto create(Long userId, AnnouncementRequest request) {
        Announcement announcement = new Announcement();
        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        announcement.setCreatedBy(userId);
        announcement.setTags(resolveTags(request.getTagNames()));

        Announcement saved = announcementRepository.save(announcement);
        log.info("Created announcement '{}' by user {}", saved.getTitle(), userId);
        return AnnouncementDto.from(saved);
    }

    @Transactional
    public AnnouncementDto update(Long id, AnnouncementRequest request) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new AnnouncementNotFoundException(id));

        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        announcement.setTags(resolveTags(request.getTagNames()));

        log.info("Updated announcement {}", id);
        return AnnouncementDto.from(announcementRepository.save(announcement));
    }

    @Transactional
    public void delete(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new AnnouncementNotFoundException(id));
        announcement.setEnabled(false);
        announcementRepository.save(announcement);
        log.info("Soft-deleted announcement {}", id);
    }

    private Set<Tag> resolveTags(List<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        if (tagNames == null || tagNames.isEmpty()) {
            return tags;
        }
        for (String name : tagNames) {
            Tag tag = tagRepository.findByName(name.toUpperCase())
                    .orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setName(name.toUpperCase());
                        return tagRepository.save(newTag);
                    });
            tags.add(tag);
        }
        return tags;
    }
}
