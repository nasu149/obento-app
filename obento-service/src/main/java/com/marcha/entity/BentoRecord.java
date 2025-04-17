// src/main/java/com/example/entity/BentoRecord.java
package com.marcha.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDate;

@Entity
@Table(name = "bento_records", uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "date"}))
public class BentoRecord extends PanacheEntity {

    public String userId;

    public LocalDate date;

    @Enumerated(EnumType.STRING)
    public Who who;

    public enum Who {
        self, mom, buy
    }

    @Override
    public String toString() {
        return "BentoRecord [userId=" + userId + ", date=" + date + ", who=" + who + "]";
    }
}