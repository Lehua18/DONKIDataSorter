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
            timeKey = getTimeString(eventType);
        }while (timeKey.isEmpty());

        //Get data endpoints
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

            //prints type and date
            System.out.print("\t"+eventType +" "+i+": ");
            System.out.print(formattedDate(event.getString(timeKey))+"; ");

            //Get and print event specific info
            if(eventType.equals("CME")){
                int bestAnalysis = -1;
                String type = "?";
                //Only prints the type associated with the most accurate analysis
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
                //Only prints the highest KP index
                if(event.optJSONArray("allKpIndex") != null) {
                    double highestKP = 0;
                    JSONArray KPIndices = event.getJSONArray("allKpIndex");
                    for (int j = 0; j < KPIndices.length(); j++){
                        if(KPIndices.getJSONObject(j).getDouble("kpIndex") > highestKP){
                            highestKP = KPIndices.getJSONObject(j).getDouble("kpIndex");
                        }
                    }
                    System.out.println("KP Index = "+highestKP);
                }


            }else if(eventType.equals("IPS")){

            }else if(eventType.equals("FLR")){
                System.out.println("Type = "+event.getString("classType"));

            }else if(eventType.equals("SEP")){

            }else if(eventType.equals("MPC")){

            }else if(eventType.equals("RBE")){

            }else if(eventType.equals("HSS")){

            }else{
                System.out.println("Oops, something went wrong. Please stop the program and try again.");
                System.exit(1);
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

            //Event specific analysis
            printSpecificData(singleEvent,eventType);

            //get related events

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
        String type = jsonString.substring(20,23);
        return type+" at "+date;
    }

    //prints specific event data
    public static void printSpecificData(JSONObject event, String eventType){
        //Create new scanner
        Scanner scan = new Scanner(System.in);

        //Get and print time
        String timeKey = getTimeString(eventType);
        System.out.println("\tStart time: " + formattedDate(event.getString(timeKey)));
        if(event.optJSONArray("instruments") != null){
            JSONArray instruments = event.getJSONArray("instruments");
            System.out.println("\tInstruments:");
            for(int k = 0; k < instruments.length(); k++){
                System.out.println("\t\t"+instruments.getJSONObject(k).getString("displayName"));
            }
        }

        //Print Location if available
        if(event.optString("sourceLocation") != null && !event.optString("sourceLocation").isEmpty()){
            String location = event.getString("sourceLocation");
            System.out.println("\tLocation:");
            System.out.println("\t\t"+location.substring(1,3)+"°"+location.charAt(0)+", "+location.substring(4)+"°"+location.charAt(3));
        }

        //print data specific to event type
        if (eventType.equals("CME")) {
            System.out.println("\tStrength:");
            if(event.optJSONArray("cmeAnalyses").length() != 0) {
                JSONArray cmeAnalyses = event.getJSONArray("cmeAnalyses");

                //prints all different types from different analyses
                for (int k = 0; k < cmeAnalyses.length(); k++) {
                    System.out.print("\t\tType "+cmeAnalyses.getJSONObject(k).getString("type"));
                    if (cmeAnalyses.getJSONObject(k).getBoolean("isMostAccurate")) {
                       System.out.print(" **BEST**");
                    }
                    System.out.println();
                }

                //prints any notes entered with analyses
                System.out.println("\tNotes:");
                for(int l = 0; l<cmeAnalyses.length(); l++){
                    System.out.println("\t\t"+cmeAnalyses.getJSONObject(l).optString("note"));
                }

            }
        } else if (eventType.equals("GST")) {
            //prints all measured kp indices and times recorded
            if(event.optJSONArray("allKpIndex") != null) {
                System.out.println("\tKP Index:");
                JSONArray KPIndices = event.getJSONArray("allKpIndex");
                for (int j = 0; j < KPIndices.length(); j++){
                    System.out.println("\t\t"+KPIndices.getJSONObject(j).getDouble("kpIndex")+" at "+ formattedDate(KPIndices.getJSONObject(j).getString("observedTime"))+" from "+KPIndices.getJSONObject(j).getString("source"));
                }
            }

        } else if (eventType.equals("IPS")) {

        } else if (eventType.equals("FLR")) {
            if(event.optString("peakTime") != null) {
                System.out.println("\tPeak Time:");
                System.out.println("\t\t" + formattedDate(event.getString("peakTime")));
            }
            if(event.optString("endTime") != null){
                System.out.println("\tEnd time:");
                System.out.println("\t\t"+formattedDate(event.getString("endTime")));
            }
            System.out.println("\tClass type:");
            System.out.println("\t\t"+event.getString("classType"));

        } else if (eventType.equals("SEP")) {

        } else if (eventType.equals("MPC")) {

        } else if (eventType.equals("RBE")) {

        } else if (eventType.equals("HSS")) {

        } else {
            System.out.println("Oops, something went wrong. Please stop the program and try again.");
            System.exit(1);
        }

        //Print any notes
        if(event.optString("note") != null){
            System.out.println("\tNote:");
            System.out.println("\t\t"+event.optString("note"));
        }

        //Print related events
        if(event.optJSONArray("linkedEvents") != null){
            JSONArray linkedArray = event.getJSONArray("linkedEvents");
            System.out.println("\tLinked Events:");
            for(int j = 0; j<linkedArray.length(); j++){
                System.out.println("\t\tEvent "+j+": "+formatEvent(linkedArray.getJSONObject(j)));
            }
            //view more info on related events or continue
            System.out.println("Please type the number of the event you would like to view further, or type 'next' to continue.");
            String relatedEventString = scan.nextLine();
            while(!relatedEventString.equalsIgnoreCase("next")){
                int singleRelInt = Integer.parseInt(relatedEventString);
                JSONObject singleRelEvent = linkedArray.getJSONObject(singleRelInt);
                String singleRelID = singleRelEvent.getString("activityID");
                String year = singleRelID.substring(0,4);
                String month = singleRelID.substring(5,7);
                String day = singleRelID.substring(8,10);
                String hour = singleRelID.substring(11,13);
                String minute = singleRelID.substring(14,16);
                String newType = singleRelID.substring(20,23);
                String relTimeKey = getTimeString(newType);
                JSONArray relEventFinder = URLToJSON("https://kauai.ccmc.gsfc.nasa.gov/DONKI/WS/get/"+newType+"?startDate="+year+"-"+month+"-"+day+"&endDate="+year+"-"+month+"-"+day);
                JSONObject relEvent = null;
                for(int i = 0; i< relEventFinder.length(); i++){
                    JSONObject posRelEvent = relEventFinder.getJSONObject(i);
                    String eventTime = posRelEvent.getString(relTimeKey);
                    if(eventTime.substring(11,16).equals(hour+":"+minute)){
                        relEvent = posRelEvent;
                    }
                }
                if(relEvent != null) {
                    System.out.println();
                    System.out.println("\t"+newType+":");
                    printSpecificData(relEvent, newType);
                }else{
                    System.out.println("Error getting related event");
                }
                System.out.println("Please type the number of the event you would like to view further, or type 'next' to continue.");
                relatedEventString = scan.nextLine();
            }
        }

    }

    //get name of time variable
    public static String getTimeString(String eventType) {
        String timeKey = "";
        if (eventType.equals("CME") || eventType.equals("GST")) {
            timeKey = "startTime";
        } else if (eventType.equals("IPS") || eventType.equals("SEP") || eventType.equals("MPC") || eventType.equals("RBE") || eventType.equals("HSS")) {
            timeKey = "eventTime";
        } else if (eventType.equals("FLR")) {
            timeKey = "beginTime";
//        } else if (eventType.equals("ALL")) {
//            timeKey = "ALL";
        } else {
            System.out.println("You have not entered a valid event type, or something else went wrong. Please try again");
        }
        return timeKey;
    }
}