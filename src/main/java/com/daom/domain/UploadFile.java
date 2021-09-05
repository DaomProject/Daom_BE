package com.daom.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class UploadFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;

    @Column(nullable = false, name ="origin_name")
    private String originName;

    @Column(nullable = false, name ="saved_name")
    private String savedName;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false)
    private String extension;

    @Builder
    public UploadFile(String originName, String savedName, Long size, String extension) {
        this.originName = originName;
        this.savedName = savedName;
        this.size = size;
        this.extension = extension;
    }
}
