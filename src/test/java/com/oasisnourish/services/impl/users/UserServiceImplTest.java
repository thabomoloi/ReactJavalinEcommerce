package com.oasisnourish.services.impl.users;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.oasisnourish.dao.users.UserDao;
import com.oasisnourish.dto.users.UserInputDto;
import com.oasisnourish.enums.Role;
import com.oasisnourish.exceptions.EmailExistsException;
import com.oasisnourish.exceptions.NotFoundException;
import com.oasisnourish.models.users.User;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void testFindUserById_Success() {
        User mockUser = new User(1, "John Doe", "john.doe@test.com", "hashedPassword");
        when(userDao.find(1)).thenReturn(Optional.of(mockUser));

        Optional<User> result = userService.findUserById(1);
        verify(userDao, times(1)).find(1);
        assertEquals(mockUser, result.get());
    }

    @Test
    void findUserById_UserDoesNotExist() {
        when(userDao.find(1)).thenReturn(Optional.empty());

        Optional<User> result = userService.findUserById(1);

        assertFalse(result.isPresent());
        verify(userDao, times(1)).find(1);
    }

    @Test
    public void testFindAllUsers() {
        List<User> mockUserList = Arrays.asList(
                new User(1, "John Doe", "john.doe@test.com", "password123", Role.USER),
                new User(2, "Jane Doe", "jane.doe@test.com", "password456", Role.ADMIN));

        when(userDao.findAll()).thenReturn(mockUserList);

        List<User> result = userService.findAllUsers();
        assertEquals(mockUserList, result);
        verify(userDao, times(1)).findAll();
    }

    @Test
    public void testCreateUser_EmailAlreadyExists() {
        UserInputDto userDto = new UserInputDto(0, "John Doe", "john.doe@test.com", "password123");

        when(userDao.findByEmail(userDto.getEmail())).thenReturn(Optional.of(new User()));

        EmailExistsException exception = assertThrows(EmailExistsException.class, () -> userService.createUser(userDto));
        assertEquals("The email has already been taken.", exception.getMessage());
        verify(userDao, times(1)).findByEmail(userDto.getEmail());
        verify(userDao, never()).save(any(User.class));
    }

    @Test
    public void testCreateUser_Success() {
        UserInputDto userDto = new UserInputDto(0, "John Doe", "john.doe@test.com", "password123");

        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");
        when(userDao.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1); // Simulate setting an ID after saving
            return null; // No need to return anything for void methods
        }).when(userDao).save(any(User.class));

        userService.createUser(userDto);

        verify(userDao, times(1)).findByEmail(userDto.getEmail());
        verify(passwordEncoder, times(1)).encode(userDto.getPassword());
        verify(userDao, times(1)).save(any(User.class));
    }

    @Test
    public void testUpdateUser_UserExists() {
        UserInputDto userDto = new UserInputDto(1, "John Doe", "john.doe@test.com", "newPassword");
        User existingUser = new User(1, "Old Name", "old.email@test.com", "oldPassword");

        when(userDao.find(userDto.getId())).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedNewPassword");

        userService.updateUser(userDto);

        verify(userDao).update(any(User.class));
        assertEquals(userDto.getName(), existingUser.getName());
        assertEquals(userDto.getEmail(), existingUser.getEmail());
        assertEquals("encodedNewPassword", existingUser.getPassword());
        verify(userDao, times(1)).find(1);
        verify(userDao, times(1)).update(existingUser);
    }

    @Test
    public void testUpdateUser_UserDoesNotExist() {
        UserInputDto userDto = new UserInputDto(1, "John Doe", "john.doe@test.com",
                "newPassword");

        when(userDao.find(1)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.updateUser(userDto));
        assertEquals("User does not exist.", exception.getMessage());
        verify(userDao, times(1)).find(1);
        verify(userDao, never()).update(any(User.class));
    }

    @Test
    public void testDeleteUser_UserExists() {
        User existingUser = new User(1, "John Doe", "john.doe@test.com", "hashedPassword");

        when(userDao.find(1)).thenReturn(Optional.of(existingUser));

        userService.deleteUser(1);

        verify(userDao).delete(1);
        verify(userDao, times(1)).delete(1);
    }

    @Test
    public void testDeleteUser_UserDoesNotExist() {
        when(userDao.find(1)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.deleteUser(1));
        assertEquals("User does not exist.", exception.getMessage());
        verify(userDao, times(1)).find(1);
        verify(userDao, never()).delete(1);
    }

    @Test
    public void testFindUserByEmail_UserExists() {
        User mockUser = new User(1, "John Doe", "john.doe@test.com", "hashedPassword");

        when(userDao.findByEmail("john.doe@test.com")).thenReturn(Optional.of(mockUser));

        Optional<User> result = userService.findUserByEmail("john.doe@test.com");

        assertTrue(result.isPresent());
        assertEquals(mockUser, result.get());
        verify(userDao, times(1)).findByEmail(mockUser.getEmail());
    }

    @Test
    public void testFindUserByEmail_UserDoesNotExist() {
        when(userDao.findByEmail("john.doe@test.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.findUserByEmail("john.doe@test.com");

        assertFalse(result.isPresent());
        verify(userDao, times(1)).findByEmail("john.doe@test.com");
    }

    @Test
    public void testVerifyEmail_Success() {
        String email = "john.doe@test.com";
        userService.verifyEmail(email);

        verify(userDao, times(1)).verifyEmail(email);
    }

    @Test
    void updatePassword_UserExists_UpdatesPassword() {
        User user = new User(1, "Jane", "jane@example.com", "oldPassword");

        when(userDao.find(1)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        userService.updatePassword(1, "newPassword");

        assertEquals("encodedNewPassword", user.getPassword());
        verify(userDao, times(1)).find(1);
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(userDao, times(1)).update(user);
    }

    @Test
    void updatePassword_UserDoesNotExist_ThrowsNotFoundException() {
        when(userDao.find(1)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.updatePassword(1, "newPassword"));
        assertEquals("User does not exist.", exception.getMessage());
        verify(userDao, times(1)).find(1);
        verify(userDao, never()).update(any(User.class));
    }
}
