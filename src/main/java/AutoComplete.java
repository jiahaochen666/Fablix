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
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


// server endpoint URL
@WebServlet("/auto-suggestion")
public class AutoComplete extends HttpServlet {
    private static final long serialVersionUID = 1L;
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    /*
     * populate the Super hero hash map.
     * Key is hero ID. Value is hero name.
     */

    public AutoComplete() {
        super();
    }

    /*
     * Generate the JSON Object from hero to be like this format:
     * {
     *   "value": "Iron Man",
     *   "data": { "heroID": 11 }
     * }
     *
     */
    private static JsonObject generateJsonObject(String heroID, String heroName) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", heroName);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("heroID", heroID);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }

    /*
     *
     * Match the query against superheroes and return a JSON response.
     *
     * For example, if the query is "super":
     * The JSON response look like this:
     * [
     * 	{ "value": "Superman", "data": { "heroID": 101 } },
     * 	{ "value": "Supergirl", "data": { "heroID": 113 } }
     * ]
     *
     * The format is like this because it can be directly used by the
     *   JSON auto complete library this example is using. So that you don't have to convert the format.
     *
     * The response contains a list of suggestions.
     * In each suggestion object, the "value" is the item string shown in the dropdown list,
     *   the "data" object can contain any additional information.
     *
     *
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {

            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb");

            // setup the response json arrray
            JsonArray jsonArray = new JsonArray();

            // get the query string from parameter
            String query = request.getParameter("query");


            // return the empty json array if query is null or empty
            if (query == null || query.trim().isEmpty()) {
                response.getWriter().write(jsonArray.toString());
                return;
            }

            Connection dbcon = ds.getConnection();
            if (dbcon == null)
                System.out.println("dbcon is null.");

            StringBuilder query1 = new StringBuilder("select distinct movies.id as id, title, year, director, rating from movies, ratings, stars, stars_in_movies where movies.id = ratings.movieId " +
                    "and movies.id = stars_in_movies.movieId and stars.id = stars_in_movies.starId " +
                    "and ratings.movieId = stars_in_movies.movieId ");
            String[] s = query.split("\\s+");
            query1.append("and match(title) against ('");
            for (String x : s) {
                query1.append("+").append(x).append("* ");
            }
            query1.deleteCharAt(query1.lastIndexOf(" ")).append("' in boolean mode) limit 10");

            System.out.println(1111111111);
            System.out.println(query1);

            Statement statement = dbcon.createStatement();

            ResultSet r = statement.executeQuery(query1.toString());

            while (r.next()) {
                jsonArray.add(generateJsonObject(r.getString("id"), r.getString("title")));
            }
//            for (Integer id : superHeroMap.keySet()) {
//                String heroName = superHeroMap.get(id);
//                if (heroName.toLowerCase().contains(query.toLowerCase())) {
//                    jsonArray.add(generateJsonObject(id, heroName));
//                }
//            }

            response.getWriter().write(jsonArray.toString());

            r.close();
            statement.close();
            dbcon.close();

        } catch (Exception e) {
            System.out.println(e);
            response.sendError(500, e.getMessage());
        }
    }


}
