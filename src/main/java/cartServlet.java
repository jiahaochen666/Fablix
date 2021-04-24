package main.java;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Random;

/**
 * This IndexServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "IndexServlet", urlPatterns = "/api/cart")
public class cartServlet extends HttpServlet {
    /**
     * handles GET requests to store session information
     */

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        JsonArray previousItems = (JsonArray) session.getAttribute("previousItems");
        response.getWriter().write(previousItems.toString());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String title = request.getParameter("title");
        String type = request.getParameter("type");
        String id = request.getParameter("id");

        System.out.println(title + "       222      " + type);
        HttpSession session = request.getSession();
        JsonObject movie = new JsonObject();
        JsonArray previousItems = (JsonArray) session.getAttribute("previousItems");

        synchronized (previousItems) {
            boolean existed = false;
            if (previousItems != null) {
                for (JsonElement j : previousItems) {
                    if (id.equals(((JsonObject) j).get("id").getAsString())) {
                        log(((JsonObject) j).get("title").toString() + " 2--------------------------");
                        movie.addProperty("title", title);
                        movie.addProperty("id", id);
                        int num = ((JsonObject) j).get("num").getAsInt();
                        int price = ((JsonObject) j).get("price").getAsInt() / num;
                        if ("add".equals(type)) {
                            int increment = ((JsonObject) j).get("num").getAsInt() + 1;
                            ((JsonObject) j).remove("num");
                            ((JsonObject) j).remove("price");

                            ((JsonObject) j).addProperty("price", price * increment);
                            ((JsonObject) j).addProperty("num", increment);
                        } else if ("del".equals(type)) {
                            int decrement = ((JsonObject) j).get("num").getAsInt() - 1;
                            if (decrement > 0) {
                                ((JsonObject) j).remove("num");
                                ((JsonObject) j).remove("price");
                                ((JsonObject) j).addProperty("num", decrement);
                                ((JsonObject) j).addProperty("price", decrement * price);

                            } else if (decrement == 0) {
                                previousItems.remove(j);
                            }
                        } else if ("rem".equals(type)) {
                            previousItems.remove(j);
                            response.getWriter().write(previousItems.toString());
                            return;
                        }
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
                previousItems.add(movie);
            }
        }

        if (previousItems == null) {
            previousItems = new JsonArray();
            previousItems.add(movie);
            session.setAttribute("previousItems", previousItems);
        } else {
            // prevent corrupted states through sharing under multi-threads
            // will only be executed by one thread at a time
            synchronized (previousItems) {
                System.out.println(movie.toString());

            }
        }

        response.getWriter().write(previousItems.toString());
    }
}



