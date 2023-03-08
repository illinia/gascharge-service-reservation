package com.gascharge.taemin.service.user.dto;

import com.gascharge.taemin.domain.enums.user.UserAuthority;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserServiceResponseDto {
    private String name;
    private String email;
    private String imageUrl;
    private UserAuthority authority;

    @Builder
    public UserServiceResponseDto(String name, String email, String imageUrl, UserAuthority authority) {
        this.name = name;
        this.email = email;
        this.imageUrl = imageUrl;
        this.authority = authority;
    }
}
