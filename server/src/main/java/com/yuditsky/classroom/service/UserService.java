package com.yuditsky.classroom.service;

import com.yuditsky.classroom.model.User;

public interface UserService {
    User create(User user);

    User findOrCreateByUsername(String username);
}
