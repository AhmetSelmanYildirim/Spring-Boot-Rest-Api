package com.asy.spring_boot_rest_api.api;

import com.asy.spring_boot_rest_api.dto.UserDto;
import com.asy.spring_boot_rest_api.entity.User;
import com.asy.spring_boot_rest_api.service.UserService;
import com.asy.spring_boot_rest_api.util.CustomPage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController // Springe bu sınıfın bir Controller olduğunu tanıtıyoruz.
@RequestMapping("/user") // /user route'una istek geldiğinde çalışacak.

public class UserController {

    // @Autowired // IoC içinde tuttuğumuz servis intance'ını inject ediyoruz.
    // private UserService userService;

    // Best Pactice** Constructor injection
    // Böylelikle runtime'da propertimize herhangi bir atama yapmasını engelliyoruz.
    private final UserService userService;

    // Constructor'ı elle oluşturmak yerine @RequiredArgsConstructor lombok anotasyonunu da yazabiliriz.
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto user) { // @RequestBody json nesnesi almayı sağlar
        UserDto resultUser = userService.createUser(user);
        return ResponseEntity.ok(resultUser);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getUsers() {
        List<UserDto> userList = userService.getAllUsers();
        return ResponseEntity.ok(userList);
    }

    @GetMapping("user/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") Long id) {
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("id") Long id , @RequestBody UserDto user) {
        UserDto resultUser = userService.updateUser(id,user);
        return ResponseEntity.ok(resultUser);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> deleteUser(@PathVariable("id") Long id) {
        Boolean result = userService.deleteUser(id);
        return ResponseEntity.ok(result);
    }

    // İstenilen sayıda datayı istenilen sayfa sayısı kadar bölüp performans artırabiliriz.
    @GetMapping("/pagination")
    public ResponseEntity<Page<User>> pagination(@RequestParam int  currentPage, @RequestParam int pageSize) {
        return ResponseEntity.ok(userService.pagination(currentPage, pageSize));
    }

    // Springin Pageable sınıfı sayesinde @RequestParamları kullanmaya gerek kalmaz.
    @GetMapping("/pagination/v1")
    public ResponseEntity<Page<User>> pagination(Pageable pageable) {
        return ResponseEntity.ok(userService.pagination(pageable));
    }

    // İstenilen kayıt sayısından 1 fazlasına bakarak başka kayıt var mı yok mu kontrol eder. Page'den daha performanslıdır.
    @GetMapping("/pagination/v2")
    public ResponseEntity<Slice<User>> slice(Pageable pageable) {
        return ResponseEntity.ok(userService.slice(pageable));
    }

    // Page ve Slice sınıfları tüm entity'i döndüğü için bir CustomPage sınıfı oluşturdum ve datayı dto kullanarak döndüm
    @GetMapping("/pagination/v3")
    public ResponseEntity<CustomPage<UserDto>> customPagination(Pageable pageable) {
        return ResponseEntity.ok(userService.customPagination(pageable));
    }

}
