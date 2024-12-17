package com.asy.spring_boot_rest_api.service.impl;

import com.asy.spring_boot_rest_api.advice.UserNotFound;
import com.asy.spring_boot_rest_api.dto.UserDto;
import com.asy.spring_boot_rest_api.entity.User;
import com.asy.spring_boot_rest_api.repository.UserRepository;
import com.asy.spring_boot_rest_api.service.UserService;
import com.asy.spring_boot_rest_api.util.CustomPage;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service // IoC için spring'e sınıfımızı servis olarak tanıtıyoruz
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


    @Override
    public UserDto createUser(UserDto userDto) {
        User user = modelMapper.map(userDto, User.class);
        user.setCreatedAt(new Date());
        user.setCreatedBy("Admin");
        return modelMapper.map(userRepository.save(user), UserDto.class); // JpaRepository'nin save metodunu kullanarak gelen user'ı database'e kaydediyoruz.
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> userList = userRepository.findAll();
        List<UserDto> userDtoList = userList.stream().map(user -> modelMapper.map(user, UserDto.class)).collect(Collectors.toList());
        return userDtoList;
    }

    @Override
    public UserDto getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return modelMapper.map(user.get(), UserDto.class);
        }
        throw new UserNotFound("User not found");
    }

    @Override
    public UserDto updateUser(Long id, UserDto user) throws UserNotFound {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User updatedUser = userOptional.get();
            updatedUser.setFirstName(user.getFirstName() !=null ? user.getFirstName() : updatedUser.getFirstName());
            updatedUser.setLastName(user.getLastName() !=null ? user.getLastName() : updatedUser.getLastName());
            updatedUser.setUpdatedAt(new Date());
            updatedUser.setUpdatedBy("Editor");
            userRepository.save(updatedUser);
            return modelMapper.map(updatedUser, UserDto.class);
        }
        throw new UserNotFound("User not found");
    }

    @Override
    public Boolean deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.delete(user.get());
            return true;
        }
        throw new UserNotFound("User not found");
    }

    @Override
    public Page<User> pagination(int currentPage, int pageSize) {
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        return userRepository.findAll(pageable);
    }

    // Bütün veriyi görmek için yorucu bir sorgu çalıştırır.
    @Override
    public Page<User> pagination(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    // İstenilen kayıt sayısından 1 fazlasına bakarak başka kayıt var mı yok mu kontrol eder.
    @Override
    public Slice<User> slice(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public CustomPage<UserDto> customPagination(Pageable pageable) {
        Page<User> data = userRepository.findAll(pageable);
        UserDto[] dtos = modelMapper.map(data.getContent(), UserDto[].class);
        return new CustomPage<UserDto>(data, Arrays.asList(dtos));
    }

}
