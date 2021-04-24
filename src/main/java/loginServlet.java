package main.java;

import com.google.gson.JsonObject;
import org.jasypt.util.password.StrongPasswordEncryptor;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "loginServlet", urlPatterns = "/api/login")
public class loginServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //Delete because not use in project 4
        /*
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);
        JsonObject responseJsonObject = new JsonObject();
        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse, request.getSession().getAttribute("robot-check"));
            request.getSession().setAttribute("robot-check", "success");
        } catch (Exception e) {
            responseJsonObject.addProperty("message", "Please check the recaptcha first!");
            response.getWriter().write(responseJsonObject.toString());
            System.out.println("fail");
            return;
        }
        */
        JsonObject responseJsonObject = new JsonObject();

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        PreparedStatement selectCustomers;
        PreparedStatement selectEmployees;

        String query1 = "select * from employees where email = ?";
        String query = "select * from customers where email = ?";

        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb");
            Connection dbcon = ds.getConnection();


            /////////////////////////
            selectEmployees = dbcon.prepareStatement(query1);
            selectEmployees.setString(1, email);
            ResultSet rs = selectEmployees.executeQuery();
            boolean success = false;
            if (rs.next()) {
                System.out.println(11111);
                String db_email = rs.getString("email");
                String db_password = rs.getString("password");
                success = new StrongPasswordEncryptor().checkPassword(password, db_password);
                System.out.println(success);
                System.out.println(password);
                if (email.equals(db_email) && success) {
                    request.getSession().setAttribute("email", email);
                    request.getSession().setAttribute("status", "administor");
                    responseJsonObject.addProperty("status", "administor");
                    responseJsonObject.addProperty("message", "success");
                } else {
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "incorrect password for admin");
                }
                selectEmployees.close();
            } else {
                System.out.println(122221);
                selectCustomers = dbcon.prepareStatement(query);
                selectCustomers.setString(1, email);
                ResultSet r = selectCustomers.executeQuery();
                if (r.next()) {
                    String db_email = r.getString("email");
                    String db_password = r.getString("password");
                    success = new StrongPasswordEncryptor().checkPassword(password, db_password);
                    System.out.println(password);
                    System.out.println(db_password);
                    StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
                    System.out.println(passwordEncryptor.encryptPassword(password));
                    System.out.println(success);
                    if (email.equals(db_email) && success) {
                        request.getSession().setAttribute("email", email);
                        request.getSession().setAttribute("status", "customer");
                        responseJsonObject.addProperty("status", "customer");
                        responseJsonObject.addProperty("message", "success");

                    } else {
                        responseJsonObject.addProperty("status", "fail");
                        responseJsonObject.addProperty("message", "incorrect password for customer");
                    }
                } else {
                    responseJsonObject.addProperty("message", "No record for this email");
                }
                r.close();
            }
            response.getWriter().write(responseJsonObject.toString());
            response.getWriter().close();
            dbcon.close();
            rs.close();

        } catch (Exception e) {
            responseJsonObject.addProperty("message", "Cannot connect to database");
        }

    }
}