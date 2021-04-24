package main.java;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "mainServlet", urlPatterns = "/api/main")
public class mainServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession();

        if(session.getAttribute("item") != null){
            session.setAttribute("item", null);
        }

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb");
            Connection dbcon = ds.getConnection();

            //////////////////////////////////////


            String query = "select name from genres";

            Statement statement = dbcon.createStatement();
            ResultSet rs = statement.executeQuery(query);

            JsonArray jsonArray = new JsonArray();
            while (rs.next()) {
                jsonArray.add(rs.getString("name"));
            }
            out.write(jsonArray.toString());
            rs.close();
            statement.close();
            dbcon.close();
        } catch (Exception e) {
            //This exception is copied from cs122b-spring20-project1-api-example
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
        }
        out.close();

    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("-------------------------------asljfkasf");

        String title = "null";
        String year = "null";
        String director = "null";
        String actor = "null";


        JsonObject responseJsonObject = new JsonObject();
        if (!request.getParameter("title").equals(""))
            title = request.getParameter("title");
        System.out.println("111111111111111111");

//        if (!request.getParameter("year").equals(""))
//            year = request.getParameter("year");
//        System.out.println("22222222222222");
//
//        if (!request.getParameter("director").equals(""))
//            director = request.getParameter("director");
//        System.out.println("3333333333333");
//
//        if (!request.getParameter("actor").equals(""))
//            actor = request.getParameter("actor");
//        System.out.println("33333333444444444444444444");


        responseJsonObject.addProperty("title", title);
        responseJsonObject.addProperty("year", year);
        responseJsonObject.addProperty("director", director);
        responseJsonObject.addProperty("actor", actor);

        System.out.println("aslkfhjkalfjaslk;fjilashiot");
        response.getWriter().write(responseJsonObject.toString());
        System.out.println(response.toString());
    }
}