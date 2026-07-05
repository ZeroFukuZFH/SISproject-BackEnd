package com.example.SISproject.repositories;

import com.example.SISproject.models.UserModel;
import com.example.SISproject.utils.Database;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class UserRepository extends Database {
    public UserModel findUserByEmail(String email) {
        try {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("""
                SELECT * FROM user WHERE email=?
            """);
            preparedStatement.setString(1,email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return new UserModel(
                      resultSet.getInt("userId"),
                      resultSet.getString("username"),
                        resultSet.getString("email"),
                        resultSet.getString("password"),
                        resultSet.getString("activityStatus")
                );
            }
        } catch (SQLException e){
            System.out.println("SQL ERROR: " + e.getMessage());
        }
        return null;
    }

    public Boolean addNewUser(String username, String email, String password){
        try {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("""
                INSERT INTO user (username,email,password) VALUES (?,?,?)
            """);
            preparedStatement.setString(1,username);
            preparedStatement.setString(2,email);
            preparedStatement.setString(3,password);

            int result = preparedStatement.executeUpdate();
            return result > 0;
        } catch (SQLException e){
            System.out.println("SQL ERROR: " + e.getMessage());
        }
        return false;
    }
}
