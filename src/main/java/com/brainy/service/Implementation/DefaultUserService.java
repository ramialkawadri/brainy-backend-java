package com.brainy.service.Implementation;

import org.springframework.stereotype.Service;

import com.brainy.entity.User;
import com.brainy.service.UserService;

import jakarta.persistence.EntityManager;

@Service
public class DefaultUserService implements UserService {
    
    private EntityManager entityManager;

    public DefaultUserService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public User getUserByUsername(String name) {
        User user = entityManager.find(User.class, name);
        return user;
    }
    
}
