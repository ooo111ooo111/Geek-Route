package com.github.paicoding.forum.service.session.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserSessionDTO {
    private Long id;
    private String Username;
    private String githubId;
    private String email;
    private String avatar;
} 