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
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


@WebServlet(name = "dashboardServlet", urlPatterns = "/api/dashboard")
public class dashboardServlet extends HttpServlet {

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb");
            Connection dbcon = ds.getConnection();

            /////////////////////////////////


            String query = "show tables;";
            PreparedStatement statement = dbcon.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            JsonArray array = new JsonArray();
            while (resultSet.next()) {
                String table = resultSet.getString("Tables_in_moviedb");
                String query2 = "describe " + table;
                PreparedStatement statement2 = dbcon.prepareStatement(query2);
                ResultSet resultSet2 = statement2.executeQuery();
                JsonArray array2 = new JsonArray();
                while (resultSet2.next()) {
                    JsonObject content = new JsonObject();
                    content.addProperty("table_name", table);
                    content.addProperty("field", resultSet2.getString("Field"));
                    content.addProperty("type", resultSet2.getString("Type"));
                    content.addProperty("null", resultSet2.getString("Null"));
                    content.addProperty("key", resultSet2.getString("Key"));
                    content.addProperty("extra", resultSet2.getString("Extra"));
                    array2.add(content);
                }
                array.add(array2);
                statement2.close();
                resultSet2.close();
            }
            statement.close();
            resultSet.close();
            dbcon.close();
            out.write(array.toString());
        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String starName = request.getParameter("starName");
        String birthYear = request.getParameter("birthYear");

        try {
            Connection dbcon = dataSource.getConnection();
            String max_id = "select max(id) as max_id from stars";
            PreparedStatement s = dbcon.prepareStatement(max_id);
            ResultSet rs = s.executeQuery();
            rs.next();
            String raw_id = rs.getString("max_id");
            raw_id = raw_id.substring(2);
            int r_id = Integer.parseInt(raw_id);
            r_id++;
            rs.close();
            s.close();
            String id = "nm" + r_id;
            String query;
            PreparedStatement statement;
            if (birthYear.equals("")) {
                query = "INSERT INTO stars VALUES(?, ?, NULL)";
                statement = dbcon.prepareStatement(query);
                statement.setString(1, id);
                statement.setString(2, starName);
            } else {
                query = "INSERT INTO stars VALUES(?, ?, ?)";
                statement = dbcon.prepareStatement(query);
                statement.setString(1, id);
                statement.setString(2, starName);
                statement.setInt(3, Integer.parseInt(birthYear));
            }
            statement.executeUpdate();
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("message", "Successfully add " + starName + " starId: " + id);
            response.getWriter().write(responseJsonObject.toString());
            dbcon.close();
        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
        }
    }
}
