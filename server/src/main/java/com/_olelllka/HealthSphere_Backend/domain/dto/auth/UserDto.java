package com._olelllka.HealthSphere_Backend.domain.dto.auth;

import com._olelllka.HealthSphere_Backend.domain.entity.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {
    private String email;
    @JsonIgnore
    private String password;
    private Role role;
    private Date createdAt;
    private Date updatedAt;
}
