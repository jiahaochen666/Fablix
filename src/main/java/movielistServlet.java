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
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


@WebServlet(name = "movielistServlet", urlPatterns = "/api/movielist")
public class movielistServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println(11111111);

        long startTime1 = System.nanoTime();
        System.out.println(startTime1);

        HttpSession session = request.getSession();
        HashMap<String, String> items = new HashMap<>();
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String title = request.getParameter("title");
        String year = request.getParameter("year");
        String director = request.getParameter("director");
        String actor = request.getParameter("actor");
        String genre = request.getParameter("genre");
        String start = request.getParameter("start");
        String frontpage = request.getParameter("frontpage");
        String size = request.getParameter("size");
        String sort = request.getParameter("sort");
        String order = request.getParameter("order");

        if (session.getAttribute("items") == null) {
            if (title != null) {
                items.put("title", title);
            }
            if (year != null) {
                items.put("year", year);
            }
            if (director != null) {
                items.put("director", director);
            }
            if (actor != null) {
                items.put("actor", actor);
            }
            if (genre != null) {
                items.put("genre", genre);
            }
            if (start != null) {
                items.put("start", start);
            }
            if (frontpage != null) {
                items.put("frontpage", frontpage);
            }
            if (size != null) {
                items.put("size", size);
            }
            if (sort != null) {
                items.put("sort", sort);
            }
            if (order != null) {
                items.put("order", order);
            }
        } else {
            if (title != null) {
                items.put("title", title);
                items.put("genre", null);
                items.put("start", null);
            }
            if (year != null) {
                items.put("year", year);
            }
            if (director != null) {
                items.put("director", director);
            }
            if (actor != null) {
                items.put("actor", actor);
            }
            if (genre != null) {
                items.put("genre", genre);
                items.put("start", null);
                items.put("title", null);
                items.put("year", null);
                items.put("director", null);
                items.put("actor", null);
            }
            if (start != null) {
                items.put("start", start);
                items.put("genre", null);
                items.put("title", null);
                items.put("year", null);
                items.put("director", null);
                items.put("actor", null);
            }
            if (frontpage != null) {
                items.put("frontpage", frontpage);
            }
            if (size != null) {
                items.put("size", size);
            }
            if (sort != null) {
                items.put("sort", sort);
            }
            if (order != null) {
                items.put("order", order);
            }

        }

        session.setAttribute("items", items);

        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb");

            long startTime2 = System.nanoTime();
            Connection dbcon = ds.getConnection();
            System.out.println(startTime2);
            /////////////////////////////

            PreparedStatement selectMovies;

            String query1;
            ArrayList<String> sb = new ArrayList<>();

            if (genre == null && start == null) {
                query1 = "select distinct movies.id as id, title, year, director, rating from movies, ratings, stars, stars_in_movies where movies.id = ratings.movieId " +
                        "and movies.id = stars_in_movies.movieId and stars.id = stars_in_movies.starId " +
                        "and ratings.movieId = stars_in_movies.movieId ";

                if (!title.equals("null")) {
                    String[] s = title.split("\\s+");
                    query1 += "and match(title) against (" + "'";
                    for (String x : s) {
                        query1 += "+" + x + "* ";
                    }
                    query1 = query1.substring(0, query1.length() - 1);
                    query1 += "'" + " in boolean mode)";
                    System.out.println(sb.toString());
                }

                if (!year.equals("null")) {
                    query1 += "and year = ? ";
                    sb.add(year);
                }

                if (!director.equals("null")) {
                    query1 += "and director like ? ";
                    sb.add("%" + director + "%");
                }

                if (!actor.equals("null")) {
                    query1 += "and stars.name like ? ";
                    sb.add("%" + actor + "%");
                }
            } else {
                if (start == null) {
                    query1 = "select distinct movies.id as id, title, year, director, rating from movies, ratings, genres, genres_in_movies  where movies.id = ratings.movieId " +
                            "and  movies.id = genres_in_movies.movieId and genres.id = genres_in_movies.genreId and genres.name = '" + genre + "' ";
                } else {
                    if (start.equals("*")) {
                        query1 = "select distinct movies.id as id, title, year, director, rating from movies, ratings where movies.id = ratings.movieId " +
                                "and  title REGEXP '^[^a-z0-9A-z]'";
                    } else {
                        query1 = "select distinct movies.id as id, title, year, director, rating from movies, ratings where movies.id = ratings.movieId " +
                                "and  title like '" + start + "%' ";
                    }
                }
            }
            if (sort.equals("title")) {
                if (order.equals("Asc1Asc2"))
                    query1 += "order by title asc, rating asc";
                if (order.equals("Asc1Desc2"))
                    query1 += "order by title asc, rating desc";
                if (order.equals("Desc1Asc2"))
                    query1 += "order by title desc, rating asc";
                if (order.equals("Desc1Desc2"))
                    query1 += "order by title desc, rating desc";
            } else {
                if (order.equals("Asc1Asc2"))
                    query1 += "order by rating asc, title asc";
                if (order.equals("Asc1Desc2"))
                    query1 += "order by rating asc, title desc";
                if (order.equals("Desc1Asc2"))
                    query1 += "order by rating desc, title asc";
                if (order.equals("Desc1Desc2"))
                    query1 += "order by rating desc, title desc";
            }

            selectMovies = dbcon.prepareStatement(query1);
            for (int i = 0; i < sb.size(); i++) {
                selectMovies.setString(i + 1, sb.get(i));
            }
            ResultSet r = selectMovies.executeQuery();

            int count = 0;
            while (r.next()) {
                count++;
            }
            r.close();

            query1 += " limit " + size + " offset " + frontpage;
            selectMovies = dbcon.prepareStatement(query1);
            for (int i = 0; i < sb.size(); i++) {
                selectMovies.setString(i + 1, sb.get(i));
            }
            ResultSet rs = selectMovies.executeQuery();

            JsonArray jsonArray = new JsonArray();

            while (rs.next()) {
                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_rating = rs.getString("rating");

                String query2 = String.format("select * from genres_in_movies, genres where genres.id = genres_in_movies.genreId and genres_in_movies.movieId = '%s' order by genres.name ", movie_id);
                Statement statement2 = dbcon.createStatement();
                ResultSet rs2 = statement2.executeQuery(query2);
                JsonArray moviegenres = new JsonArray();
                int i = 0;
                while (rs2.next()) {
                    moviegenres.add(rs2.getString("name"));
                    i++;
                    if (i == 3) {
                        break;
                    }
                }
                statement2.close();
                rs2.close();


                String query3 = String.format("select * from stars_in_movies as s1, stars as s2 where s2.id = s1.starId " +
                        "and s1.movieId = '%s' order by (select count(*) from stars_in_movies as s3 where s3.starId = s2.id) desc, s2.name asc", movie_id);
                Statement statement3 = dbcon.createStatement();
                ResultSet rs3 = statement3.executeQuery(query3);
                JsonArray moviestars = new JsonArray();
                int j = 0;
                while (rs3.next()) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("id", rs3.getString("id"));
                    obj.addProperty("name", rs3.getString("name"));
                    moviestars.add(obj);
                    j++;
                    if (j == 3) {
                        break;
                    }
                }
                statement3.close();
                rs3.close();


                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", movie_id);
                jsonObject.addProperty("title", movie_title);
                jsonObject.addProperty("year", movie_year);
                jsonObject.addProperty("rating", movie_rating);
                jsonObject.addProperty("director", movie_director);
                jsonObject.add("moviestars", moviestars);
                jsonObject.add("moviegenres", moviegenres);

                jsonArray.add(jsonObject);
            }
            JsonObject obj = new JsonObject();
            obj.addProperty("total", count);
            jsonArray.add(obj);
            out.write(jsonArray.toString());


            rs.close();
            dbcon.close();
            long endTime2 = System.nanoTime();
            long passed2 = endTime2 - startTime2;
            System.out.println("passed:" + passed2);
            String home = System.getProperty("user.home");
            FileWriter tj = new FileWriter(home + "/proj5/TJ.txt", true);
            BufferedWriter bufferedWriter = new BufferedWriter(tj);
            PrintWriter outputFile2 = new PrintWriter(bufferedWriter);
            synchronized (outputFile2) {
                outputFile2.println(passed2);
            }
            outputFile2.close();
        } catch (Exception e) {
            //This exception is copied from cs122b-spring20-project1-api-example
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
        }
        out.close();
        long endTime1 = System.nanoTime();
        long passed1 = endTime1 - startTime1;
        String home = System.getProperty("user.home");
        FileWriter ts = new FileWriter(home + "/proj5/TS.txt", true);
        BufferedWriter bufferedWriter = new BufferedWriter(ts);
        PrintWriter outputFile1 = new PrintWriter(bufferedWriter);
        synchronized (outputFile1) {
            outputFile1.println(passed1);
        }
        outputFile1.close();
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String title = request.getParameter("title");
        String type = request.getParameter("type");
        String id = request.getParameter("id");

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

