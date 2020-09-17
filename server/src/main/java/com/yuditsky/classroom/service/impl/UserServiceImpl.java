package com.yuditsky.classroom.service.impl;

import com.yuditsky.classroom.converter.UserDtoToEntityConverter;
import com.yuditsky.classroom.converter.UserEntityToDtoConverter;
import com.yuditsky.classroom.entity.UserEntity;
import com.yuditsky.classroom.exception.AlreadyExistedException;
import com.yuditsky.classroom.model.User;
import com.yuditsky.classroom.repository.UserRepository;
import com.yuditsky.classroom.service.UserService;
import com.yuditsky.classroom.validator.impl.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final UserDtoToEntityConverter userDtoToEntityConverter;
    private final UserEntityToDtoConverter userEntityToDtoConverter;

    @Autowired
    public UserServiceImpl(
            UserRepository userRepository,
            UserValidator userValidator,
            UserDtoToEntityConverter userDtoToEntityConverter,
            UserEntityToDtoConverter userEntityToDtoConverter
    ) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
        this.userDtoToEntityConverter = userDtoToEntityConverter;
        this.userEntityToDtoConverter = userEntityToDtoConverter;
    }

    @Override
    public User create(User user) {
        userRepository.findByUsername(user.getUsername()).ifPresent((userEntity -> {
            throw new AlreadyExistedException("User with username {0} is already existing", user.getUsername());
        }));
        userValidator.validate(user);
        UserEntity userEntity = userDtoToEntityConverter.convert(user);
        return userEntityToDtoConverter.convert(userRepository.save(userEntity));
    }

    @Override
    public User findOrCreateByUsername(String username) {
        return userRepository.findByUsername(username).map(userEntityToDtoConverter::convert)
                .orElseGet(() ->
                        create(User.builder()
                                .username(username)
                                .build()));

    }
}
