package com.marcha.entity;

import java.time.LocalDate;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "allowance_records", uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "date"}))
public class AllowanceEntity extends PanacheEntity{
   
    public String userId;
    public LocalDate date;
    public int earned;

    @Enumerated(EnumType.STRING)
    public Who who;

    @Enumerated(EnumType.STRING)
    public Status status = Status.PENDING;

    @Override
    public String toString() {
        return "AllowanceEntity [userId=" + userId + ", date=" + date + ", earned=" + earned + ", who=" + who
                + ", status=" + status + "]";
    }

    public enum Status {
        PENDING, PAID
    }

    public enum Who {
        self, mom, buy
    }
}
