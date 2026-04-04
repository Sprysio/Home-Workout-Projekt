// java
package com.homeworkout.auth.dto;

import com.homeworkout.auth.model.Role;
import java.util.Set;

public class UserDto {
    private Long id;
    private String username;
    private String email;
    private Set<Role> roles;

    public UserDto() {}

    public UserDto(Long id, String username, String email, Set<Role> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }
}