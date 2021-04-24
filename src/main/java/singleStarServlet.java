package main.java;

import com.google.gson.JsonArray;
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
import java.util.HashMap;

@WebServlet(name = "singleStarServlet", urlPatterns = "/api/singleStar")
public class singleStarServlet extends HttpServlet {
    private static final long serialVersionUID = 3L;

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        HashMap<String, String> items = (HashMap<String, String>) session.getAttribute("items");

        response.setContentType("application/json");

        String id = request.getParameter("starid");

        PrintWriter out = response.getWriter();

        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb");
            Connection dbcon = ds.getConnection();

            //////////////////////////////////////////

//            String query = "select stars.id, stars.name, stars.birthYear, movies.title from stars, movies, stars_in_movies where stars_in_movies.starId = ?" +
//                    "movies.id = stars_in_movies.movieId and stars.id = stars_in_movies.starId";

            String query = String.format("select stars.id as sid, stars.name, stars.birthYear, movies.id as mid, movies.title from stars, movies, stars_in_movies where stars_in_movies.starId = '%s' " +
                    "and movies.id = stars_in_movies.movieId and stars.id = stars_in_movies.starId order by movies.year desc, title asc", id);

            PreparedStatement statement = dbcon.prepareStatement(query);

//            statement.setString(1, id);

            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            rs.next();
            String starId = rs.getString("sid");
            String name= rs.getString("name");
            String birth = rs.getString("birthYear");
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("sid", starId);
            jsonObject.addProperty("name", name);
            jsonObject.addProperty("birth", birth);
            jsonArray.add(jsonObject);

            JsonArray array = new JsonArray();
            JsonObject object = new JsonObject();
            String title = rs.getString("title");
            String movieId = rs.getString("mid");
            object.addProperty("title", title);
            object.addProperty("movieId", movieId);
            array.add(object);
            while (rs.next()) {
                JsonObject obj = new JsonObject();
                String title1 = rs.getString("title");
                String movieId1 = rs.getString("mid");
                obj.addProperty("title", title1);
                obj.addProperty("movieId", movieId1);
                array.add(obj);
            }
            jsonArray.add(array);

            JsonObject obj = new JsonObject();
            for (HashMap.Entry<String, String> entry : items.entrySet()) {
                if(entry.getValue() != null)
                    obj.addProperty(entry.getKey(), entry.getValue());
            }
            jsonArray.add(obj);

            out.write(jsonArray.toString());

            rs.close();
            statement.close();
            dbcon.close();
        } catch (Exception e) {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

        }
        out.close();

    }

}