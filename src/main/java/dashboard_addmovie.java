package main.java;

import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

@WebServlet(name = "dashboard_addmovie", urlPatterns = "/api/dashboard_addmovie")
public class dashboard_addmovie extends HttpServlet {
    @Resource(name = "jdbc/moviedb")

    private DataSource dataSource;
    JsonObject jsonObject = new JsonObject();
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String movie_title = request.getParameter("movie_title");
        String movie_year = request.getParameter("movie_year");
        String movie_director = request.getParameter("movie_director");
        String movie_star = request.getParameter("movie_star");
        String movie_genre = request.getParameter("movie_genre");
        try {


            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb-write");
            Connection dbcon = ds.getConnection();


            //////////////////////////////////

            String query = "{call add_movie(?, ? , ? , ? , ?)};";
            CallableStatement s = dbcon.prepareCall(query);
            s.setString(1, movie_title);
            s.setInt(2, Integer.parseInt(movie_year));
            s.setString(3, movie_director);
            s.setString(4, movie_star);
            s.setString(5, movie_genre);
            ResultSet row = s.executeQuery();
            row.next();
            if (row.getString("movieId").equals("Exist")) {
                System.out.println("row");
                jsonObject.addProperty("message", movie_title + " is not added successfully because duplicated");
            } else {
                jsonObject.addProperty("message", movie_title + " is added successfully movieId: "
                        + row.getString("movieId") + "starId: " + row.getString("starexist") + " " + row.getString("starId")
                        + " genreId: " + row.getString("genreexist") + " " + row.getString("genreId"));
            }
            row.close();
            out.write(jsonObject.toString());
            s.close();
            dbcon.close();
        } catch (Exception e) {
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
        }
    }
}
