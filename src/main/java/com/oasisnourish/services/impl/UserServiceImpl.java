package com.oasisnourish.services.impl;

import java.util.List;
import java.util.Optional;

import com.oasisnourish.dao.UserDao;
import com.oasisnourish.dto.UserInputDto;
import com.oasisnourish.enums.Role;
import com.oasisnourish.exceptions.EmailExistsException;
import com.oasisnourish.exceptions.NotFoundException;
import com.oasisnourish.models.User;
import com.oasisnourish.services.UserService;
import com.oasisnourish.util.PasswordUtil;

/**
 * Implementation of the {@link UserService} for user-related operations.
 */
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Optional<User> findUserById(int id) {
        return userDao.find(id);
    }

    @Override
    public List<User> findAllUsers() {
        return userDao.findAll();
    }

    @Override
    public void createUser(UserInputDto userDto) {
        Optional<User> existingUser = userDao.findByEmail(userDto.getEmail());
        if (existingUser.isPresent()) {
            throw new EmailExistsException("The email has already been taken");
        }
        User user = new User(
                userDto.getName(),
                userDto.getEmail(),
                PasswordUtil.hashPassword(userDto.getPassword()));

        userDao.save(user);
    }

    @Override
    public void updateUser(UserInputDto userDto) {
        Optional<User> exisitingUser = userDao.find(userDto.getId());
        if (exisitingUser.isEmpty()) {
            throw new NotFoundException("User with id " + userDto.getId() + " not found");
        }
        User user = exisitingUser.get();
        if (!user.getEmail().equals(userDto.getEmail())) {
            user.setRole(Role.UNVERIFIED_USER);
            user.setEmailVerified(null);
        }
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        if (userDto.getPassword() != null && !userDto.getPassword().trim().isEmpty()) {
            user.setPassword(userDto.getPassword());
        }
        userDao.update(user);
    }

    @Override
    public void deleteUser(int id) {
        userDao.find(id).ifPresentOrElse(
                user -> userDao.delete(user.getId()),
                () -> {
                    throw new NotFoundException("User with id " + id + " not found");
                });
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Override
    public void verifyEmail(String email) {
        userDao.verifyEmail(email);
    }

}
