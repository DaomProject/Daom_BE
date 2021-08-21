package com.daom.dto;

import com.daom.domain.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StudentJoinDto {
    private String username;
    private String password;
    private String nickname;
    private String univName;
    private Long admissionYear;

}
