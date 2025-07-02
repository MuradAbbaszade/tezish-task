package com.company.dto;

import com.company.model.UserAuthority;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDTO {
    private String username;
    private String password;
    private UserAuthority authority; // USER, GUEST, ADMIN
}
