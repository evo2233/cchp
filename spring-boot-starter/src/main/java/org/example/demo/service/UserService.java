package org.example.demo.service;

import org.example.demo.model.UserTable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserService {

    public List<UserTable> getUserTables() {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("data.txt");
        List<String> lines = new ArrayList<String>();
        try(java.io.BufferedReader reader =
                    new java.io.BufferedReader(
                            new java.io.InputStreamReader(in, StandardCharsets.UTF_8))) {
            lines = reader.lines().collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<UserTable> userList = lines.stream().map( line -> {
            String[] split = line.split(",");
            String name = split[0];
            String id = split[1];
            String password = split[2];
            return new UserTable(name, id, password);
        }).collect(Collectors.toList());

        return userList;
    }

}
