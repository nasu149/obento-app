package com.marcha.repository;

import java.util.List;

import com.marcha.entity.AllowanceEntity;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class AllowanceRepository implements PanacheRepository<AllowanceEntity> {
    
    public List<AllowanceEntity> findByUser(String userId) {
        return list("userId = ?1 order by date desc", userId);
    }

    public List<AllowanceEntity> findPendingByUser(String userId) {
        return list("userId = ?1 and status = ?2", userId, AllowanceEntity.Status.PENDING);
    }

    public int calculatePendingTotal(String userId) {
        return findPendingByUser(userId).stream().mapToInt(a -> a.earned).sum();
    }

    @Transactional
    public int markAsPaid(String userId) {
        List<AllowanceEntity> pending = findPendingByUser(userId);
        pending.forEach(a -> a.status = AllowanceEntity.Status.PAID);
        return pending.size();
    }
}