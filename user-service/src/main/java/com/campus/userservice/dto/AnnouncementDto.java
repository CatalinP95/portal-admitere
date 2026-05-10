package com.campus.userservice.dto;

import com.campus.userservice.model.Announcement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class AnnouncementDto {

    private Long id;
    private String title;
    private String content;
    private Long createdBy;
    private LocalDateTime createdAt;
    private List<String> tags;

    public static AnnouncementDto from(Announcement announcement) {
        AnnouncementDto dto = new AnnouncementDto();
        dto.id = announcement.getId();
        dto.title = announcement.getTitle();
        dto.content = announcement.getContent();
        dto.createdBy = announcement.getCreatedBy();
        dto.createdAt = announcement.getCreatedAt();
        dto.tags = announcement.getTags().stream()
                .map(tag -> tag.getName())
                .collect(Collectors.toList());
        return dto;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public Long getCreatedBy() { return createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<String> getTags() { return tags; }
}
