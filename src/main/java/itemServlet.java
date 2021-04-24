package main.java;

import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "item", urlPatterns = "/api/singleItem")
public class itemServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getParameter("title");
        request.getParameter("year");
        request.getParameter("director");

        JsonObject responseJsonObject = new JsonObject();


    }

}
