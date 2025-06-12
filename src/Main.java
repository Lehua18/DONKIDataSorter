import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Objects;
import java.util.Scanner;



public class Main {
    public static void main(String[] args){
        Scanner scan = new Scanner(System.in);
        String timeKey = "";
        String eventType = "";
        do {
            System.out.println("Please enter the type of event you would like to view:");
            eventType = scan.nextLine();

            //to get correct time key depending on event. Also functions to validate user entry
            if (eventType.equals("CME") || eventType.equals("GST")) {
                timeKey = "startTime";
            } else if (eventType.equals("IPS") || eventType.equals("SEP") || eventType.equals("MPC") || eventType.equals("RBE") || eventType.equals("HSS")) {
                timeKey = "eventTime";
            } else if (eventType.equals("FLR")) {
                timeKey = "peakTime";
            } else {
                System.out.println("You have not entered a valid event type. Please try again");
            }
        }while (timeKey.isEmpty());

        System.out.println("Please enter a start date in the form yyyy-MM-dd");
        String startDate = scan.nextLine();
        System.out.println("Please enter an end date in the form yyyy-MM-dd");
        String endDate = scan.nextLine();

        //creates URL to get JSON data
        String url = "https://kauai.ccmc.gsfc.nasa.gov/DONKI/WS/get/"+ eventType +"?startDate="+startDate+"&endDate="+endDate ;
        JSONArray array = URLToJSON(url);

        //Gets and prints initial info for each event in array
        for(int i = 0; i< Objects.requireNonNull(array).length(); i++){
            JSONObject event = array.getJSONObject(i);

            System.out.print(eventType +" "+i+": ");
            System.out.print(formattedDate(event.getString(timeKey))+"; ");

            //Get event specific info
            if(eventType.equals("CME")){
                int bestAnalysis = -1;
                String type = "?";
                if(event.optJSONArray("cmeAnalyses").length() != 0) {
                    JSONArray cmeAnalyses = event.getJSONArray("cmeAnalyses");
                    for (int k = 0; k < cmeAnalyses.length(); k++) {
                        if (cmeAnalyses.getJSONObject(k).getBoolean("isMostAccurate")) {
                            bestAnalysis = k;
                        }
                    }
                    type = cmeAnalyses.getJSONObject(bestAnalysis).getString("type");
                }
                System.out.println("Type "+type );
                
            } else if (eventType.equals("GST")) {
                
            }else if(eventType.equals("IPS")){

            }else if(eventType.equals("FLR")){

            }else if(eventType.equals("SEP")){

            }else if(eventType.equals("MPC")){

            }else if(eventType.equals("RBE")){

            }else if(eventType.equals("HSS")){

            }else{
                System.out.println("Oops, something went wrong. Please stop the program and try again.");
                break; //sorry Mr. Zickert, I'll find a better way...
            }
            System.out.println();
        }

        //Allows user to view more info or end program
        System.out.println("Please enter the index of the event you would like more information on or type 'end' to finish.");
        String eventString = scan.nextLine();
        while(!eventString.equalsIgnoreCase("end")) {

            //Get correct event
            int eventIndex = Integer.parseInt(eventString);
            JSONObject singleEvent = array.getJSONObject(eventIndex);
            System.out.println(eventType + " " + eventIndex + ":");

            //Get time
            System.out.println("\tTime: " + formattedDate(singleEvent.getString("startTime")));

            //Event specific analysis
            if (eventType.equals("CME")) {
            //get different analyses from cme analyses
            } else if (eventType.equals("GST")) {

            } else if (eventType.equals("IPS")) {

            } else if (eventType.equals("FLR")) {

            } else if (eventType.equals("SEP")) {

            } else if (eventType.equals("MPC")) {

            } else if (eventType.equals("RBE")) {

            } else if (eventType.equals("HSS")) {

            } else {
                System.out.println("Oops, something went wrong. Please stop the program and try again.");
            }

            //get related events
            if(singleEvent.optJSONArray("linkedEvents") != null){
                JSONArray linkedArray = singleEvent.getJSONArray("linkedEvents");
                System.out.println("\tLinked Events:");
                for(int j = 0; j<linkedArray.length(); j++){
                    System.out.println("\t\tEvent "+j+": "+formatEvent(linkedArray.getJSONObject(j)));
                }
                //while loop to look at events specifically?

            }
            System.out.println("Please enter the index of the event you would like more information on or type 'end' to finish.");
            eventString = scan.nextLine();
        }
        System.exit(0);
    }


    //Gets JSONArray from provided URL
    public static JSONArray URLToJSON(String URLString){
        try {
            //Convert string to URL
            URI uri = URI.create(URLString);
            URL url = uri.toURL();

            //Connect and pull data from URL and store in String
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
                String newJSONString = response.toString();

                //convert String back to JSON Array
                try {
                    return new JSONArray(newJSONString);
                } catch (JSONException e) {
                    System.out.println("JSON creation failed: "+e);
                    return null;
                }

            } else {
                System.out.println("GET request failed. Response code: " + responseCode);
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace(); /*Okay because this is not production software*/
            return null;
        }
    }

    //changes date format to something more readable
    public static String formattedDate(String date){ //date is in form yyyy-MM-ddThh:mmZ
        String year = date.substring(0,4);
        String month = date.substring(5,7);
        String day = date.substring(8,10);
        String hour = date.substring(11,13);
        String minute = date.substring(14,16);
        return hour+":"+minute+" UTC on "+month+"/"+day+"/"+year;
    }

    //formats "linkedEvent" into something more readable
    public static String formatEvent(JSONObject event){
        String jsonString = event.getString("activityID"); //id is in form yyyy-MM-ddThh-mm-ss-EVENTTYPE-???
        String date = formattedDate(jsonString.substring(0,17));
        String type = jsonString.substring(20,24);
        return type+" at "+date;
    }
}