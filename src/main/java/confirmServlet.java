package main.java;

import com.google.gson.JsonArray;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "confirmServlet", urlPatterns = "/api/confirm")

public class confirmServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(true);

        JsonArray previous = (JsonArray) session.getAttribute("previousItems");
        System.out.println("###################################");
        System.out.println(previous.toString());
        response.getWriter().write(previous.toString());
        previous = new JsonArray();
        session.setAttribute("previousItems", previous);
    }
}
