package com.asy.spring_boot_rest_api.service;

import com.asy.spring_boot_rest_api.dto.UserDto;
import com.asy.spring_boot_rest_api.entity.User;
import com.asy.spring_boot_rest_api.util.CustomPage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.*;

public interface UserService {
    UserDto createUser(UserDto user);
    List<UserDto> getAllUsers();
    UserDto getUserById(Long id);
    UserDto updateUser(Long id, UserDto user);
    Boolean deleteUser(Long id);
    Page<User> pagination( int  currentPage, int pageSize);
    Page<User> pagination(Pageable pageable);
    Slice<User> slice(Pageable pageable);

    CustomPage<UserDto> customPagination(Pageable pageable);
}
