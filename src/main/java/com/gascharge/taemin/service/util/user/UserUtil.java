package com.gascharge.taemin.service.util.user;

import com.gascharge.taemin.domain.entity.user.User;
import com.gascharge.taemin.service.user.dto.UserServiceResponseDto;

public class UserUtil {
    public static UserServiceResponseDto getUserServiceResponseDto(User user) {
        return UserServiceResponseDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .imageUrl(user.getImageUrl())
                .authority(user.getUserAuthority())
                .build();
    }

}
