package com.asy.spring_boot_rest_api.service.impl;

import com.asy.spring_boot_rest_api.advice.UserNotFound;
import com.asy.spring_boot_rest_api.dto.UserDto;
import com.asy.spring_boot_rest_api.entity.User;
import com.asy.spring_boot_rest_api.repository.UserRepository;
import com.asy.spring_boot_rest_api.util.CustomPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceImplTest {

    private UserServiceImpl userService; // test edeceğimiz servisin objesini oluşturuyoruz.

    // User servis nesnesine ihtiyaç olan sınıflara bakıyoruz
    private UserRepository userRepository;
    private ModelMapper modelMapper;

    // @BeforeEach -> Her test senaryosu çalıştırılmadan önce çalışan method. Hangi sınıf test edilecekse onun nesnesini yaratır.
    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class); // mock servis yaratmayı sağlar.
        modelMapper = Mockito.mock(ModelMapper.class);

        userService = new UserServiceImpl(userRepository, modelMapper);
    }

    // Setup'tan sonra Test senaryoları yazılmaya başlanabilir.

    @Test // method public ve void olmalı
    public void whenCreateUserCalledValidRequest_thenReturnValidUserDto() {

        // createUser fonksiyonunun parametre olarak alacağı mock userDto'yu oluşturuyoruz.
        UserDto userDto = new UserDto("Ahmet", "Admin");

        // DTO'yu Entity'ye dönüştürüyoruz.
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());

        // mock'ları yapılandırıyoruz.
        Mockito.when(modelMapper.map(userDto, User.class)).thenReturn(user); // DTO'yu User'a dönüştürüyoruz
        Mockito.when(userRepository.save(user)).thenReturn(user); // save metodunu mock'lıyoruz. user.save() fonksiyonu çalıştığı zaman user ı dönmesi gerektiğini mock servise öğretiyoruz.
        Mockito.when(modelMapper.map(user, UserDto.class)).thenReturn(userDto); // User'ı DTO'ya dönüştürüyoruz

        // Servis metodunu çağırıyoruz
        UserDto result = userService.createUser(userDto);

        // Doğruluğu test ediyoruz
        assertNotNull(result);
        assertEquals(userDto, result); // DTO'nun geri dönmesini bekliyoruz
        Mockito.verify(userRepository).save(user); // mock servislerin çağrılıp çağrılmadığını test ediyoruz
        Mockito.verify(modelMapper).map(user, UserDto.class); // modelMapper'ın doğru çağrıldığını kontrol ediyoruz
    }

    @Test
    public void whenCreateUserCalledWithoutData_thenReturnEmptyUserDto() {
        UserDto userDto = new UserDto();
        Mockito.when(modelMapper.map(userDto, User.class)).thenReturn(null);
    }

    @Test
    public void whenGetAllUsersCalledValidRequest_thenReturnUserDtoList() {

        // Test için bir User listesi oluştur
        List<User> userList = List.of(new User("Ahmet","YILDIRIM"), new User("Selman","YILDIRIM"));

        // Bu User listesinden beklenen UserDto listesini oluştur
        List<UserDto> userDtoList = List.of(
                new UserDto("Ahmet", "YILDIRIM"),
                new UserDto("Selman", "YILDIRIM")
        );

        // Mock davranışları tanımla
        Mockito.when(userRepository.findAll()).thenReturn(userList);
        Mockito.when(modelMapper.map(userList.get(0), UserDto.class)).thenReturn(userDtoList.get(0));
        Mockito.when(modelMapper.map(userList.get(1), UserDto.class)).thenReturn(userDtoList.get(1));

        // Servis metodunu çağır
        List<UserDto> actualUserDtoList = userService.getAllUsers();

        // Sonuçları doğrula
        assertNotNull(actualUserDtoList);
        assertEquals(userDtoList.size(), actualUserDtoList.size());
        assertEquals(userDtoList, actualUserDtoList);

        // Mock'ların çağrıldığını kontrol et
        Mockito.verify(userRepository).findAll();
        Mockito.verify(modelMapper).map(userList.get(0), UserDto.class);
        Mockito.verify(modelMapper).map(userList.get(1), UserDto.class);
    }

    @Test
    public void whenGetUserByIdCalledValidRequest_thenReturnUserDto() {
        // Test için bir User ve UserDto oluştur
        Long userId = 1L;
        User user = new User("User1", "Admin1");
        user.setId(userId); // ID eklenir
        UserDto expectedUserDto = new UserDto("User1", "Admin1");

        // Mock davranışları tanımla
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(modelMapper.map(user, UserDto.class)).thenReturn(expectedUserDto);

        // Servis metodunu çağır
        UserDto actualUserDto = userService.getUserById(userId);

        // Sonuçları doğrula
        assertNotNull(actualUserDto);
        assertEquals(expectedUserDto, actualUserDto);

        // Mock'ların çağrıldığını kontrol et
        Mockito.verify(userRepository).findById(userId);
        Mockito.verify(modelMapper).map(user, UserDto.class);
    }

    @Test
    public void whenFindByIdCalledWithInvalidId_thenThrowUserNotFound() {
        // Geçersiz bir ID tanımla
        Long invalidUserId = 999L;

        // Mock davranışlarını tanımla
        Mockito.when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        // Servis metodunu çağır ve istisnayı yakala
        Exception exception = assertThrows(UserNotFound.class, () -> userService.getUserById(invalidUserId));

        // İstisna mesajını doğrula
        assertEquals("User not found", exception.getMessage());

        // Mock'ların çağrıldığını kontrol et
        Mockito.verify(userRepository).findById(invalidUserId);
        Mockito.verifyNoInteractions(modelMapper); // modelMapper'ın çağrılmadığını doğrula
    }

    @Test
    public void whenUpdateUserCalledWithValidId_thenReturnUpdatedUserDto() {
        // Test için kullanıcı ID, User ve UserDto oluştur
        Long userId = 1L;
        User existingUser = new User("OldFirstName", "OldLastName");
        existingUser.setId(userId);

        UserDto updateRequest = new UserDto("NewFirstName", "NewLastName");

        User updatedUser = new User("NewFirstName", "NewLastName");
        updatedUser.setId(userId);
        updatedUser.setUpdatedAt(new Date());
        updatedUser.setUpdatedBy("Editor");

        UserDto expectedUserDto = new UserDto("NewFirstName", "NewLastName");

        // Mock davranışlarını tanımla
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(updatedUser);
        Mockito.when(modelMapper.map(Mockito.any(User.class), Mockito.eq(UserDto.class)))
                .thenReturn(expectedUserDto);

        // Servis metodunu çağır
        UserDto actualUserDto = userService.updateUser(userId, updateRequest);

        // Sonuçları doğrula
        assertNotNull(actualUserDto, "UserDto should not be null");
        assertEquals(expectedUserDto, actualUserDto, "Expected and actual UserDto should be the same");

        // Mock'ların çağrıldığını kontrol et
        Mockito.verify(userRepository).findById(userId);
        Mockito.verify(userRepository).save(Mockito.any(User.class));
        Mockito.verify(modelMapper).map(Mockito.any(User.class), Mockito.eq(UserDto.class));
    }

    @Test
    public void whenUpdateUserCalledWithInvalidId_thenThrowUserNotFound() {
        // Geçersiz kullanıcı ID tanımla
        Long invalidUserId = 999L;
        UserDto updateRequest = new UserDto("NewFirstName", "NewLastName");

        // Mock davranışlarını tanımla
        Mockito.when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        // Servis metodunu çağır ve istisnayı yakala
        Exception exception = assertThrows(UserNotFound.class, () -> userService.updateUser(invalidUserId, updateRequest));

        // İstisna mesajını doğrula
        assertEquals("User not found", exception.getMessage());

        // Mock'ların çağrıldığını kontrol et
        Mockito.verify(userRepository).findById(invalidUserId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verifyNoInteractions(modelMapper);
    }

    @Test
    public void whenDeleteUserCalledWithValidId_thenReturnTrue() {
        // Geçerli bir kullanıcı oluştur
        Long userId = 1L;
        User user = new User("FirstName", "LastName");
        user.setId(userId);

        // Mock davranışı
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Servis metodunu çağır
        Boolean result = userService.deleteUser(userId);

        // Sonuçları doğrula
        assertTrue(result, "Delete user should return true");
        Mockito.verify(userRepository).findById(userId);
        Mockito.verify(userRepository).delete(user);
    }

    @Test
    public void whenDeleteUserCalledWithInvalidId_thenThrowUserNotFound() {
        // Geçersiz bir ID için davranış tanımla
        Long invalidUserId = 999L;
        Mockito.when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        // İstisnayı doğrula
        Exception exception = assertThrows(UserNotFound.class, () -> userService.deleteUser(invalidUserId));

        assertEquals("User not found", exception.getMessage(), "Exception message should match");
        Mockito.verify(userRepository).findById(invalidUserId);
        Mockito.verify(userRepository, Mockito.never()).delete(Mockito.any());
    }

    @Test
    public void whenCustomPaginationCalled_thenReturnCustomPage() {
        // Test için örnek veri oluştur
        Pageable pageable = PageRequest.of(0, 10);
        List<User> userList = Arrays.asList(
                new User("John", "Doe"),
                new User("Jane", "Doe")
        );
        userList.get(0).setId(1L);
        userList.get(1).setId(2L);
        Page<User> userPage = new PageImpl<>(userList, pageable, userList.size());

        List<UserDto> userDtoList = Arrays.asList(
                new UserDto("John", "Doe"),
                new UserDto("Jane", "Doe")
        );

        // Mock davranışı
        Mockito.when(userRepository.findAll(pageable)).thenReturn(userPage);
        Mockito.when(modelMapper.map(userList, UserDto[].class)).thenReturn(userDtoList.toArray(new UserDto[0]));

        // Servis metodunu çağır
        CustomPage<UserDto> result = userService.customPagination(pageable);

        // Doğruluk kontrolü
        assertNotNull(result, "CustomPage should not be null");
        assertEquals(userPage.getTotalElements(), result.getTotalElements(), "Total elements should match");
        assertEquals(userPage.getNumber(), result.getPageNumber(), "Page number should match");
        assertEquals(userDtoList, result.getContent(), "Content should match the mapped UserDto list");

        // Mock doğrulamaları
        Mockito.verify(userRepository).findAll(pageable);
        Mockito.verify(modelMapper).map(userList, UserDto[].class);

    }



}