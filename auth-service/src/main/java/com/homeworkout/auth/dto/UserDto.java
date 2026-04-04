package com.homeworkout.auth.dto;

import com.homeworkout.auth.model.Role;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private Set<Role> roles;
}