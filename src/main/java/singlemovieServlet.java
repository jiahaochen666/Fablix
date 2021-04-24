package main.java;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Random;

@WebServlet(name = "singlemovieServlet", urlPatterns = "/api/singlemovie")
public class singlemovieServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        HashMap<String, String> items = (HashMap<String, String>) session.getAttribute("items");
        response.setContentType("application/json");

        String id = request.getParameter("movieid");

        PrintWriter out = response.getWriter();

        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb");
            Connection dbcon = ds.getConnection();


            ///////////////////////////////////////////////

            String query = "select * from movies, ratings where movies.id = ratings.movieId and  movies.id = ?";

            PreparedStatement statement = dbcon.prepareStatement(query);

            statement.setString(1, id);

            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            rs.next();
            String movieId = rs.getString("id");
            String movietitle = rs.getString("title");
            String year = rs.getString("year");
            String director = rs.getString("director");
            String rating = rs.getString("rating");


            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", movieId);
            jsonObject.addProperty("title", movietitle);
            jsonObject.addProperty("year", year);
            jsonObject.addProperty("director", director);
            jsonObject.addProperty("rating", rating);


            jsonArray.add(jsonObject);

            String query2 = String.format("select * from stars_in_movies as s1, stars as s2 where s2.id = s1.starId " +
                    "and s1.movieId = '%s' order by (select count(*) from stars_in_movies as s3 where s3.starId = s2.id) desc, s2.name asc", movieId);
            Statement statement2 = dbcon.createStatement();
            ResultSet rs2 = statement2.executeQuery(query2);
            JsonArray starsArray = new JsonArray();
            while (rs2.next()) {
                JsonObject object = new JsonObject();
                object.addProperty("id", rs2.getString("id"));
                object.addProperty("name", rs2.getString("name"));
                starsArray.add(object);
            }
            jsonArray.add(starsArray);

            String query3 = String.format("select name from genres, genres_in_movies where id = genreId and movieId = '%s' order by genres.name ", movieId);
            Statement statement3 = dbcon.createStatement();
            ResultSet rs3 = statement3.executeQuery(query3);
            JsonArray genresArray = new JsonArray();
            while (rs3.next()) {
                genresArray.add(rs3.getString("name"));
            }


            jsonArray.add(genresArray);
            JsonObject object = new JsonObject();
            if (items != null) {
                System.out.println(items.toString());
                for (HashMap.Entry<String, String> entry : items.entrySet()) {
                    if (entry.getValue() != null)
                        object.addProperty(entry.getKey(), entry.getValue());
                }
                jsonArray.add(object);
            }

            System.out.println("//////////////////////////////////");
            System.out.println(jsonArray.toString());
            out.write(jsonArray.toString());

            rs.close();
            rs2.close();
            rs3.close();
            statement.close();
            statement2.close();
            statement3.close();
            dbcon.close();
        } catch (Exception e) {
            //This exception is copied from cs122b-spring20-project1-api-example
            e.printStackTrace();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
        }
        out.close();

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String title = request.getParameter("title");
        String type = request.getParameter("type");
        String id = request.getParameter("id");
        System.out.println(title + "       222      " + type);
        log(title + "      " + type);
        HttpSession session = request.getSession();

        // get the previous items in a ArrayList
        JsonObject movie = new JsonObject();
        JsonArray previousItems = (JsonArray) session.getAttribute("previousItems");

        boolean existed = false;
        if (previousItems != null) {
            for (JsonElement j : previousItems) {
                if (id.equals(((JsonObject) j).get("id").getAsString())) {
                    System.out.println(title);
                    movie.addProperty("id", id);
                    movie.addProperty("title", title);
                    if ("add".equals(type)) {
                        movie.addProperty("num", ((JsonObject) j).get("num").getAsInt() + 1);
                        movie.addProperty("price", (((JsonObject) j).get("price").getAsInt() / ((JsonObject) j).get("num").getAsInt()) * ((JsonObject) j).get("num").getAsInt() + 1);

                    } else if ("del".equals(type)) {
                        if (((JsonObject) j).get("num").getAsInt() - 1 != 0)
                            movie.addProperty("num", ((JsonObject) j).get("num").getAsInt() - 1);
                        else if (((JsonObject) j).get("num").getAsInt() - 1 == 0)
                            previousItems.remove(j);
                    } else if ("rem".equals(type)) {
                        previousItems.remove(j);
                        response.getWriter().write(previousItems.toString());
                        return;
                    }
                    previousItems.remove(j);
                    existed = true;
                    break;
                }
            }
        }
        if (!existed) {
            Random rand = new Random();
            int price = rand.nextInt(50) + 20;
            movie.addProperty("price", price);
            movie.addProperty("title", title);
            movie.addProperty("num", 1);
            movie.addProperty("id", id);
        }

        if (previousItems == null) {
            previousItems = new JsonArray();
            previousItems.add(movie);
            session.setAttribute("previousItems", previousItems);
        } else {
            // prevent corrupted states through sharing under multi-threads
            // will only be executed by one thread at a time
            synchronized (previousItems) {
                previousItems.add(movie);
            }
        }
    }


}