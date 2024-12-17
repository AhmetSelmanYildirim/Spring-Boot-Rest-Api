package com.asy.spring_boot_rest_api.repository;

import com.asy.spring_boot_rest_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

// @Repository // JpaRepository kullanırken belirtmeye gerek yok
public interface UserRepository extends JpaRepository<User, Long> {
    // User getReferenceById(int id); // JpaRepository basic CRUD işlemlerini yapmak için hazır methodlar içerir.
    // User findByFirstName(String firstName); // hazır methodlarda yoksa yapılmasını istediğimiz işlemi basitçe burada tanımlayabiliyoruz
    // User findByFirstNameAndLastName(String firstName, String lastName);
    // Üstteki gibi sorguları otomatik oluşturamıyorsak @Query anotasyonuyla kendi sorgularımızı yazabiliriz.

}
