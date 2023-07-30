package com.brainy.dao;

import org.springframework.stereotype.Repository;

import com.brainy.model.entity.User;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Repository
public class DefaultUserDAO implements UserDao {
    
    private EntityManager entityManager;

    public DefaultUserDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public User findUserByUserName(String username) {
        User user = entityManager.find(User.class, username);
        return user;
    }

    @Override
    @Transactional
    public void registerUser(User user) {
        entityManager.persist(user);
    }
}