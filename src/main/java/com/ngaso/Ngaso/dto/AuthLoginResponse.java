package com.ngaso.Ngaso.dto;

import com.ngaso.Ngaso.Models.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthLoginResponse {
    private Integer userId;
    private Role role;
    private String message;
}
