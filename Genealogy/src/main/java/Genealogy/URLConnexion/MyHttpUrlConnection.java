package Genealogy.URLConnexion;

/**
 * MyHttpUrlConnection class : class that handles internet connection
 */

import Genealogy.Model.Exception.URLException;
import Genealogy.Model.Gedcom.Town;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MyHttpUrlConnection {

    /**
     * String user agent to send get
     */
    private final String USER_AGENT = "Mozilla/5.0";
    /**
     * String url used to test the internet connection
     */
    private static final String testingInternetConnection = "http://whatthecommit.com/";
    /**
     * Class logger
     */
    final static Logger logger = LogManager.getLogger(MyHttpUrlConnection.class);
    /**
     * Int timeoutCounter, set to 0 when the timeouts stop
     */
    private int timeoutCounter = 0;
    /**
     * String defaultLocation : result of defaultLocation query
     */
    private String defaultLocation;
    /**
     * String Nominatim default API call
     */
    private String openstreetmapApiDefault = "https://nominatim.openstreetmap.org/search?city=Tataouine&format=json";

    private static MyHttpUrlConnection instance;

    /**
     * Instance getter + initialisation if null
     *
     * @return
     */
    public static MyHttpUrlConnection getInstance() {
        if (instance == null) {
            instance = new MyHttpUrlConnection();
        }
        return instance;
    }

    /**
     * Private default constructor
     */
    private MyHttpUrlConnection() {
    }

    /**
     * Function sendGet : send a get with the string url and returns the answer
     *
     * @param url
     * @return
     * @throws Exception if fails to send the request or timeout
     */
    public String sendGet(String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
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

    /**
     * Function sendGpsRequest : send gps request, if not found, set default location
     *
     * @param city
     * @param county
     * @return
     * @throws Exception if the connection fails
     */
    public String sendGpsRequest(String city, String county) throws Exception {
        return sendGpsRequest(city, county, true);
    }

    /**
     * Function sendGpsRequest : send request to Nominatim API with String city and String county with 1 second sleep.
     * If not found with boolean defaultSearch, will set to defaultLocation of Tatouine, otherwise throw UrlException
     * If timeout less than 50 times, will call again with a 5 seconds sleep, otherwisee throw UrlException
     *
     * @param city
     * @param county
     * @param defaultSearch
     * @return
     * @throws Exception
     */
    public String sendGpsRequest(String city, String county, boolean defaultSearch) throws Exception {
        //Sleep 1 second before a request
        if (defaultSearch) {
            Thread.sleep(1000);
        }
        try {
            String newCity = URLEncoder.encode(city, "UTF-8");
            String newCounty = URLEncoder.encode(county, "UTF-8");
            String openstreetmapApi = "https://nominatim.openstreetmap.org/search?city=" + newCity
                    + "&county=" + newCounty + "&format=json";
            String res = sendGet(openstreetmapApi);
            logger.info("Result of request to Nominatim API :\n" + res);
            if (StringUtils.equals(res, "[]")) {
                if (!defaultSearch) {
                    throw new URLException("Place not found [" + city + "," + county + "]");
                }
                if (defaultLocation == null) {
                    //No answer : set to defaultLocation
                    logger.warn("No result found for " + city);
                    Town.addLostTowns(city, county);
                    defaultLocation = sendGet(openstreetmapApiDefault);
                }
                return defaultLocation;
            } else {
                timeoutCounter = 0;
                return res;
            }
        } catch (ConnectException e) {
            logger.error("Timeout", e);
            timeoutCounter++;
            if (timeoutCounter >= 50) {
                throw new URLException("Failed to connect to the API");
            } else {
                logger.info("Failed to connect to the API, will wait 5 seconds before trying again (" + timeoutCounter + ")");
                Thread.sleep(5000);
                return sendGpsRequest(city, county);
            }
        } catch (Exception e) {
            logger.error("Failed to connect to the API", e);
            throw new URLException("Failed to connect to the API");
        }
    }

    /**
     * Function testInternetConnection : test internet connection sending get to url test site
     *
     * @return
     */
    public static boolean testInternetConnection() {
        MyHttpUrlConnection connexion = new MyHttpUrlConnection();
        try {
            connexion.sendGet(testingInternetConnection);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
