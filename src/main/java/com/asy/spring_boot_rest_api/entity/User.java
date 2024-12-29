package com.asy.spring_boot_rest_api.entity;

import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity // Database'de bir tablo olduğunu belirtir.
@Table(name = "Users") // Oluşturulan tablo düzenlenebilir.
@Data // Lombok'dan gelen anotasyondur. POJO class için gerekli Getter, Setter, toString() methodlarını ekler
@NoArgsConstructor
public class User extends BaseEntity {
    @Id
    @SequenceGenerator(name="user_seq_gen",initialValue=100,allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_gen")
    @Column(name = "ID", unique = true, nullable = false)
    private long id;
    @Column(name = "name", length = 100)
    private String firstName;
    @Column(name = "surname", length = 100)
    private String lastName;

    public User(String firstName, String lastName) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
