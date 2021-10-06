package ru.job4j.dream.store;

import ru.job4j.dream.model.User;

public class PsqlMain {
    public static void main(String[] args) {
        Store store = PsqlStore.instOf();
        store.saveUser(new User(0, "User1", "user1@mail.ru", "pass"));
        store.saveUser(new User(0, "User2", "user2@mail.ru", "pass"));
        System.out.println(store.findAllUsers());
        System.out.println();
        System.out.println(store.findUserByEmail("user2@mail.ru"));
        System.out.println();
        store.saveUser(new User(2, "newUser2", "newuser2@mail.ru", "newpass"));
        System.out.println(store.findAllUsers());
    }
}