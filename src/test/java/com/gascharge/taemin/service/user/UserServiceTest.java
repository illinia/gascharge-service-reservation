package com.gascharge.taemin.service.user;

import com.gascharge.taemin.common.exception.jpa.NoEntityFoundException;
import com.gascharge.taemin.domain.entity.user.User;
import com.gascharge.taemin.domain.entity.user.UserTestData;
import com.gascharge.taemin.domain.entity.user.search.UserSearchStatus;
import com.gascharge.taemin.domain.repository.user.UserRepository;
import com.gascharge.taemin.service.user.dto.UserServiceResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.gascharge.taemin.service.util.user.UserUtil.getUserServiceResponseDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    private User user;
    private User admin;

    @BeforeEach
    void setUser() {
        this.user = UserTestData.getCloneUser();
        this.admin = UserTestData.getCloneAdmin();
    }

    @Test
    void findById() {
        // given user id
        Long id = 0L;

        // when
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(this.user));

        // then
        assertThat(userService.findById(id).toString()).isEqualTo(getUserServiceResponseDto(this.user).toString());

        // when
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> userService.findById(id))
                .isInstanceOf(NoEntityFoundException.class);
    }

    @Test
    void findAll() {
        // given user, admin, page 0, size 10, email desc
        UserSearchStatus userSearchStatus = new UserSearchStatus();
        int page = 0;
        int size = 10;
        Sort sort = Sort.by("email").descending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        List<User> users = new ArrayList<>();
        users.add(this.user);
        users.add(this.admin);
        Page mockPage = new PageImpl<>(users, pageRequest, users.size());

        // when
        when(userRepository.findUserWithSearchStatus(any(UserSearchStatus.class), any(Pageable.class)))
                .thenReturn(mockPage);
        List<UserServiceResponseDto> result = userService.findAll(userSearchStatus, pageRequest);

        // then
        assertThat(result).hasSize(2);
//        assertThat(result.getPageable().getPageNumber()).isEqualTo(page);
//        assertThat(result.getPageable().getSort()).isEqualTo(sort);
        assertThat(result.get(0).toString()).isEqualTo(getUserServiceResponseDto(this.user).toString());
        assertThat(result.get(1).toString()).isEqualTo(getUserServiceResponseDto(this.admin).toString());
    }
}
