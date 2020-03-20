package Genealogy.URLConnexion;

/**
 * Created by Dan on 15/04/2016.
 */

import Genealogy.Model.Town;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MyHttpURLConnexion {

    private final String USER_AGENT = "Mozilla/5.0";
    private static final String testingInternetConnexion = "http://whatthecommit.com/";
    final static Logger logger = LogManager.getLogger(MyHttpURLConnexion.class);
    private static int cpt = 0;

    // HTTP GET request
    public String sendGet(String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        logger.info("\nSending 'GET' request to URL : " + url);
        logger.info("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    public String sendAddressRequest(String city, String county) throws Exception {
        Thread.sleep(1000);
        String googleApi = "https://maps.googleapis.com/maps/api/geocode/json?address=";
        String openstreetmapApiDefault = "https://nominatim.openstreetmap.org/search?city=Tataouine&format=json";
        try {
            String newCity = URLEncoder.encode(city, "UTF-8");
            String newCounty = URLEncoder.encode(county, "UTF-8");
            String openstreetmapApi = "https://nominatim.openstreetmap.org/search?city=" + newCity
                    + "&county=" + newCounty + "&format=json";
            String res = sendGet(openstreetmapApi);
            logger.info("Result of request to Nominatim API :\n" + res);
            if (res.contains("ERROR")){
                Thread.sleep(1000);
                return sendAddressRequest(city, county);
            } else if (StringUtils.equals(res,"[]")) {
                //Pas de réponse : mettre Tataouine comme ville par défaut
                logger.warn("No result found for " + city);
                Town.addLostTowns(city);
                return sendGet(openstreetmapApiDefault);
            }
            else  {
                return res;
            }
        }
        catch (ConnectException e) {
            logger.error("Timeout",e);
            cpt++;
            if (cpt >= 5){
                throw new URLException("Impossible de se connecter à l'API");
            } else {
                Thread.sleep(5000);
                return sendAddressRequest(city, county);
            }
        }catch (Exception e) {
            logger.error("Impossible de se connecter à l'API",e);
           throw new URLException("Impossible de se connecter à l'API");
        }
    }

    public String sendGoogleAddressRequest(String city) throws Exception {
        try {
            String newCity = URLEncoder.encode(city, "UTF-8");
            String res = sendGet("https://maps.googleapis.com/maps/api/geocode/json?address=" + newCity);
            logger.info("Result of request to Google Map API :\n" + res);
            if (res.contains("OVER_QUERY_LIMIT")){
                Thread.sleep(1000);
                return sendGoogleAddressRequest(city);
            } else if (res.contains("ZERO_RESULTS")) {
                //Pas de réponse : mettre Tataouine comme ville par défaut
                logger.warn("No result found for " + city);
                Town.addLostTowns(city);
                return sendGet("https://maps.googleapis.com/maps/api/geocode/json?address=" + "Tataouine");
            }
            else  {
                return res;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new URLException("Impossible de se connecter à l'API de Google Map");
        }
    }


    public static boolean testInternetConnexion(){
        MyHttpURLConnexion connexion = new MyHttpURLConnexion();
        try {
            connexion.sendGet(testingInternetConnexion);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) throws Exception {
        MyHttpURLConnexion connexion = new MyHttpURLConnexion();
        System.out.println(connexion.sendAddressRequest("Rennes","Ille et Vilaine"));
    }

}
