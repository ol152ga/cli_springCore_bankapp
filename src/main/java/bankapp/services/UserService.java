package bankapp.services;/*
Сервис для управления пользователями. Содержит методы для создания пользователя,
поиска пользователя по ID и получения списка всех пользователей.
 */


import bankapp.exceptions.*;
import bankapp.models.Account;
import bankapp.models.User;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final List<User> userList;

    public User createUser(String login, List<Account> accounts) {
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
                .accountList(accounts)
                .build();


        userList.add(newUser);
        return newUser;
    }

    public User getUserById(String id){
        if(id == null || id.trim().isEmpty()){
            throw new InvalidUserId();
        }
        return userList.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new UserNotFound(id));
    }

    public List<User> getAllUserList(){
        if(userList.isEmpty()){
            throw new NoUsersInUserList();
        }
        return new ArrayList<>(userList); // возвращаем копию
    }
}
