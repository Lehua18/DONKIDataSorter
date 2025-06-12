import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;



public class Main {
    public static void main(String[] args){
        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter the URL you would like to extract:");
        String url = scan.nextLine();
        System.out.println(URLToJSON(url).toString());


    }
    public static JSONArray URLToJSON(String URLString){
        try {
            URI uri = URI.create(URLString);
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                System.out.println(response.toString());
                String newJSONString = response.toString()/*.replace("\"","\\\"")*/;
//            String anotherNewJSONString = newJSONString.substring(1, response.toString().length()-2);
                System.out.println(newJSONString);

                try {
                    JSONArray object = new JSONArray(newJSONString);
                    System.out.println(object.toString());
                    return object;
                } catch (JSONException e) {
                    System.out.println("JSON creation failed: "+e);
                    return null;
                }

            } else {
                System.out.println("GET request failed. Response code: " + responseCode);
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}