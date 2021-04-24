package main.java;

import org.jasypt.util.password.StrongPasswordEncryptor;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class UpdateEmpoyeePassword {


    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    public static void main(String[] args) throws Exception {

        String loginUser = "root";
        String loginPasswd = "as123@zx";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        Class.forName("com.mysql.jdbc.Driver").newInstance();

        Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        Statement statement = connection.createStatement();

        // change the customers table password column from VARCHAR(20) to VARCHAR(128)
        String alterQuery1 = "ALTER TABLE employees MODIFY COLUMN password VARCHAR(128)";
        int alterResult1 = statement.executeUpdate(alterQuery1);

        // get the ID and password for each customer

        System.out.println("altering employees table schema completed, " + alterResult1 + " rows affected");

        // get the ID and password for each customer
        String query1 = "SELECT email, password from employees";

        ResultSet rs1 = statement.executeQuery(query1);
        StrongPasswordEncryptor passwordEncryptor;
        // we use the StrongPasswordEncryptor from jasypt library (Java Simplified Encryption)
        //  it internally use SHA-256 algorithm and 10,000 iterations to calculate the encrypted password
        passwordEncryptor = new StrongPasswordEncryptor();

        ArrayList<String> updateQueryList1 = new ArrayList<>();

        System.out.println("encrypting password (this might take a while)");
        while (rs1.next()) {
            // get the ID and plain text password from current table
            String email = rs1.getString("email");
            String password = rs1.getString("password");

            // encrypt the password using StrongPasswordEncryptor
            String encryptedPassword = passwordEncryptor.encryptPassword(password);

            // generate the update query
            String updateQuery = String.format("UPDATE employees SET password='%s' WHERE email='%s';", encryptedPassword,
                    email);
            updateQueryList1.add(updateQuery);
        }


        rs1.close();

        // execute the update queries to update the password
        System.out.println("updating password");
        int count = 0;
        for (String updateQuery : updateQueryList1) {
            System.out.println(updateQuery);
            int updateResult = statement.executeUpdate(updateQuery);
            count += updateResult;
        }
        System.out.println("updating password completed, " + count + " rows affected");

        statement.close();
        connection.close();

        System.out.println("finished");

    }

}
