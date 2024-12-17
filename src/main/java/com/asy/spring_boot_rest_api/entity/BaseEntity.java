package com.asy.spring_boot_rest_api.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@MappedSuperclass // bu sınıftan kalıtım alan tüm sınıflar fieldları alacak
@Getter
@Setter
@ToString
public class BaseEntity implements Serializable { // Serializable ile nesne network'e taşınıp, diske yazılıp okunabilir

    private Date createdAt;
    private String createdBy;
    private Date updatedAt;
    private String updatedBy;

}
