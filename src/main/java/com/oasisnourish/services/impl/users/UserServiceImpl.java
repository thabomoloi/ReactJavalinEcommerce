package com.oasisnourish.services.impl.users;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.oasisnourish.dao.users.UserDao;
import com.oasisnourish.dto.UserInputDto;
import com.oasisnourish.exceptions.EmailExistsException;
import com.oasisnourish.exceptions.NotFoundException;
import com.oasisnourish.models.users.User;
import com.oasisnourish.services.users.UserService;

/**
 * Implementation of the {@link UserService} for user-related operations.
 */
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
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
        userDao.findByEmail(userDto.getEmail()).ifPresent((_) -> {
            throw new EmailExistsException("The email has already been taken.");
        });

        var password = passwordEncoder.encode(userDto.getPassword());
        User user = new User(0, userDto.getName(), userDto.getEmail(), password);

        userDao.save(user);
    }

    @Override
    public void updateUser(UserInputDto userDto) {
        User user = userDao.find(userDto.getId()).orElseThrow(() -> new NotFoundException("User does not exist."));

        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());

        if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        userDao.update(user);
    }

    @Override
    public void deleteUser(int id) {
        userDao.find(id).orElseThrow(() -> new NotFoundException("User does not exist."));
        userDao.delete(id);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Override
    public void verifyEmail(String email) {
        userDao.verifyEmail(email);
    }

    @Override
    public void updatePassword(int userId, String password) {
        User user = userDao.find(userId).orElseThrow(() -> new NotFoundException("User does not exist."));
        user.setPassword(passwordEncoder.encode(password));
        userDao.update(user);
    }

}
