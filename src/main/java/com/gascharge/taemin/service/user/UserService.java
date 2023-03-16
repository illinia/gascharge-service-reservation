package com.gascharge.taemin.service.user;

import com.gascharge.taemin.common.exception.jpa.NoEntityFoundException;
import com.gascharge.taemin.domain.entity.user.User;
import com.gascharge.taemin.domain.entity.user.search.UserSearchStatus;
import com.gascharge.taemin.domain.repository.user.UserRepository;
import com.gascharge.taemin.redis.annotation.Cache;
import com.gascharge.taemin.service.user.dto.UserServiceResponseDto;
import com.gascharge.taemin.service.util.user.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.gascharge.taemin.service.util.user.UserUtil.getUserServiceResponseDto;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Cache(value = "user", key = "id")
    public UserServiceResponseDto findById(Long id) {
        Optional<User> byId = userRepository.findById(id);

        if (byId.isEmpty()) {
            throw new NoEntityFoundException(User.class, id.toString());
        }

        return getUserServiceResponseDto(byId.get());
    }

    @Transactional(readOnly = true)
    public List<UserServiceResponseDto> findAll(UserSearchStatus userSearchStatus, Pageable pageable) {
        Page<User> users = userRepository.findUserWithSearchStatus(userSearchStatus, pageable);
        List<UserServiceResponseDto> collect = users.getContent().stream()
                .map(UserUtil::getUserServiceResponseDto).collect(Collectors.toList());
        return collect;
    }
}