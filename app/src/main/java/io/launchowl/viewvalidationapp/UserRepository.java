package io.launchowl.viewvalidationapp;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class UserRepository {
    private final Set<User> users = new HashSet<>(Arrays.asList(
       new User("realteal"), new User("drysandpiper"), new User("thunderousgull"), new User("regalkangaroo")
    ));

    public User getUser(String userName) {
        for (User user : users) {
            if (user.userName().equals(userName)) {
                return user;
            }
        }

        return null;
    }
}
