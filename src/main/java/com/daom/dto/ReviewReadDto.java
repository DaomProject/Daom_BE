package com.daom.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ReviewReadDto {
    private Long id;
    private String userThumbnail;
    private String nickname;
    private int level;
    private String content;
    private List<String> tags;
    private int like;
    private int unlike;
    private List<String> photos;

    @Builder
    public ReviewReadDto(Long id, String nickname, String userThumbnail, int level, String content, List<String> tags, int like, int unlike, List<String> photos) {
        this.userThumbnail = userThumbnail;
        this.id = id;
        this.nickname = nickname;
        this.level = level;
        this.content = content;
        this.tags = tags;
        this.like = like;
        this.unlike = unlike;
        this.photos = photos;
    }
}
