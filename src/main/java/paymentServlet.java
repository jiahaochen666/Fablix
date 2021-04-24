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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@WebServlet(name = "paymentServlet", urlPatterns = "/api/payment")

public class paymentServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String first = request.getParameter("first");
        String last = request.getParameter("last");
        String card = request.getParameter("card");
        String date = request.getParameter("date");
        JsonObject responseJsonObject = new JsonObject();
        HttpSession session = request.getSession(true);
        JsonArray previousItems = (JsonArray) session.getAttribute("previousItems");
        if (previousItems.size() == 0) {
            responseJsonObject.addProperty("message", "Shopping cart is empty!");
            response.getWriter().write(responseJsonObject.toString());
            return;
        }
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb-write");
            Connection dbcon = ds.getConnection();


            ///////////////////////////////////
            PreparedStatement s;
            String query1 = "select * from creditcards, customers where creditcards.firstName = ? and creditcards.lastName  = ? and creditcards.id = ? and expiration = ? and ccid = creditcards.id";

            s = dbcon.prepareStatement(query1);
            s.setString(1, first);
            s.setString(2, last);
            s.setString(3, card);
            s.setString(4, date);
            ResultSet r = s.executeQuery();
            if (r.next()) {
                String query2 = "select max(id) as m_id from sales";
                Statement s2 = dbcon.createStatement();
                ResultSet r2 = s2.executeQuery(query2);
                r2.next();
                int m = r2.getInt("m_id");
                int c_id = r.getInt("customers.id");

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                dateFormat.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
                Date d = new Date();
                String currentDate = dateFormat.format(d);


                String movieId;
                String query3;
                Statement s3;
                JsonArray idArray = new JsonArray();

                for (JsonElement j : previousItems) {
                    for (int i = 0; i < ((JsonObject) j).get("num").getAsInt(); i++) {
                        m++;
                        movieId = ((JsonObject) j).get("id").getAsString();
                        query3 = "INSERT INTO sales " +
                                "VALUES(" + m + "," + c_id + ",'" + movieId + "','" + currentDate + "')";
                        s3 = dbcon.createStatement();
                        s3.executeUpdate(query3);
                        idArray.add(m);
                    }
                    ((JsonObject) j).addProperty("saleId", String.valueOf(idArray));
                    idArray = new JsonArray();
                }
                responseJsonObject.addProperty("message", "success");
                int price = 0;
                for (JsonElement j : previousItems) {
                    price += ((JsonObject) j).get("price").getAsInt();
                }
                session.setAttribute("idArray", idArray);
                responseJsonObject.addProperty("price", price);

            } else {
                responseJsonObject.addProperty("message", "incorrect information");
            }


        } catch (Exception e) {
            responseJsonObject.addProperty("message", "incorrect information");
        }
        System.out.println("11111111111111111111111");
        response.getWriter().write(responseJsonObject.toString());

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        JsonArray previousItems = (JsonArray) session.getAttribute("previousItems");
        JsonObject responseJsonObject = new JsonObject();
        int price = 0;
        for (JsonElement j : previousItems) {
            price += ((JsonObject) j).get("price").getAsInt();
        }
        responseJsonObject.addProperty("price", price);
        response.getWriter().write(responseJsonObject.toString());
    }
}
