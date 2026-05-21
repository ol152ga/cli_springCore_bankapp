package bankapp.services;/*
Сервис для управления пользователями. Содержит методы для создания пользователя,
поиска пользователя по ID и получения списка всех пользователей.
 */


import bankapp.exceptions.*;
import bankapp.models.User;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final List<User> userList = new ArrayList<>();

    public User createUser(String login) {
        if(login == null || login.trim().isEmpty()){
            throw new InvalidLogin();
        }

        boolean loginexists = userList.stream()
                .anyMatch(user -> user.getLogin().equalsIgnoreCase(login));
        if(loginexists){
            throw new NotUniqueLogin(login);
        }

        User newUser = User.builder()
                .id(UUID.randomUUID().toString())
                .login(login)
                .build();


        userList.add(newUser);
        return newUser;
    }

    public User getUserByUserId(String userId){
        if(userId == null || userId.trim().isEmpty()){
            throw new InvalidUserId();
        }
        return userList.stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new UserNotFound(userId));
    }

    public List<User> getAllUserList(){
        return new ArrayList<>(userList); // возвращаем копию
    }

}
