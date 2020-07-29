package com.example.demo.dao;

import com.example.demo.bean.User;

import java.util.ArrayList;
import java.util.List;

public class UserDao {
    public static final UserDao dao = new UserDao();

    public static List<User> userList = new ArrayList<User>(16);

    public static boolean addUser(User user) {
        if (user != null) {
            userList.add(user);
            return true;
        }
        return false;
    }

}
