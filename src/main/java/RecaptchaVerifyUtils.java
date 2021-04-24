package main.java;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class RecaptchaVerifyUtils extends HttpServlet {

    public static final String SITE_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    public static void verify(String gRecaptchaResponse, Object attribute) throws Exception {
        if (attribute != null) {
            return;
        }

        if (gRecaptchaResponse == null || gRecaptchaResponse.length() == 0) {
            throw new Exception("recaptcha verification failed: gRecaptchaResponse is null or empty");
        }

        URL verifyUrl = new URL(SITE_VERIFY_URL);

        HttpsURLConnection conn = (HttpsURLConnection) verifyUrl.openConnection();


        conn.setRequestMethod("POST");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");


        String postParams = "secret=" + RecaptchaConstants.SECRET_KEY + "&response=" + gRecaptchaResponse;

        conn.setDoOutput(true);

        OutputStream outStream = conn.getOutputStream();
        outStream.write(postParams.getBytes());

        outStream.flush();
        outStream.close();

        int responseCode = conn.getResponseCode();
        System.out.println("responseCode=" + responseCode);


        InputStream inputStream = conn.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

        JsonObject jsonObject = new Gson().fromJson(inputStreamReader, JsonObject.class);

        inputStreamReader.close();

        System.out.println("Response: " + jsonObject.toString());

        if (jsonObject.get("success").getAsBoolean()) {
            return;
        }

        throw new Exception("recaptcha verification failed: response is " + jsonObject.toString());

    }
}
