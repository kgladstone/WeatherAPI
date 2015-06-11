/***************************************************************
 * Attire Decider Weather Analyzer
 * By: Keith Gladstone (keithag@princeton.edu)
 * Created in June 2015
 * 
 * This program takes advantage of the archiving ability of the
 * scraper. Namely, it can pull scraped data from a stored XML file
 * with a timestamp before deciding to pull data again from the Internet.
 * This method conserves HTTP calls and reduces traffic, allowing for
 * rapid testing at no risk of disturbing the data source.
 * 
 * Requires Java Version 8
 * Assume that if XML file exists, it is formatted correctly.
 *
 * Sample zip codes to test with 
 * 
 * Princeton : 08540    // for testing normal
 * Seattle   : 98101    // for testing rain
 * Miami     : 33101    // for testing heat
 * Chicago   : 60290    // for testing cool
 * Caribou, ME : 04736  // for testing cold
 * Lahaina, HI : 96761  // for testing different time of day
 * Schenectady, NY : 12345 // for interesting zip code
 ***************************************************************/
import java.time.LocalDateTime;
import java.io.File;
public class Weather
{
    /***************************************************************/
    /* What to Wear: given weather data output what to wear        */
    /* Enter any valid U.S. zip code as the first argument         */
    /***************************************************************/
    public static void main(String[] args)
    {
        final int CHRONINTERVAL = 30; // tolerance for age of input data, in minutes

        /*
         * Pre-determined user temperature preferences
         */
        final int HOT = 70;
        final int WARM = 60;
        final int COOL = 50;
        final int COLD = 35;
        final int FREEZING = 15;
                
        /*
         * Handle commandline arguments
         */
        String zip = "08540";
        if (args != null && args.length > 0)
            zip = args[0]; // set zip code to argument on run
        String filename = "data/" + zip;
        File f = new File(filename);
        String xmlText;
        LocalDateTime now = LocalDateTime.now(); // grab current time

        if(!f.exists()) // Need new data because input file does not exist
            WeatherScrape scraper = new WeatherScrape(zip);

        /*
         * Read XML file
         */
        In in = new In(filename); 
        xmlText = in.readAll(); 

        /*
         * Gather the timestamp and zip code in the XML file
         */
        LocalDateTime then = LocalDateTime.parse(getTime(xmlText));
        String thatZip = getZip(xmlText); // Parse zip code of input data

        /*
        * Refresh the weather data if necessary (mismatched location OR data too old)
        */
        if (!thatZip.equals(zip) || now.minusMinutes(CHRONINTERVAL).compareTo(then) > 0) // if at least XXX time later
        {
            WeatherScrape scraper = new WeatherScrape(zip);
        } 
        
        /***************************************************************/

        /*
         * Gather weather data from XML
         */
        String location = getTown(xmlText) + ", " + getState(xmlText); 
        double temp = Double.parseDouble(getTemperature(xmlText));
        String sky = getSky(xmlText);
        
        /*
         * Handle rain variable
         */
        Double rainVal = Double.parseDouble(getRain(xmlText));
        boolean rain;
        if (rainVal == 0)
            rain = false;
        else
            rain = true;
       
        /*
         * Print collected XML weather data
         */
        StdOut.println("Weather for " + location);
        StdOut.println("Current Temperature is: " + temp + " degrees Fahrenheit");
        StdOut.println("Sky is " + sky);
        StdOut.println("Rain is " + rainVal + " in.");
        StdOut.print("Consider wearing: ");
        tempAnalysis(temp, HOT, WARM, COOL, COLD, FREEZING);

        if (rain)
            StdOut.println("Bring an umbrella");
        if (sky.equals("Clear"))
            StdOut.println("Bring sunglasses");
    }
    
    /***************************************************************/
    /* Handle analysis of temperature given inputs                 */
    /***************************************************************/
    public static void tempAnalysis(double temp, int HOT, int WARM, int COOL, int COLD, int FREEZING)
    {
        if (temp >= HOT)
            StdOut.println("T-Shirt, Shorts");
        
        else if (temp <= HOT && temp >= WARM)
            StdOut.println("T-Shirt and Shorts + Layer");
        
        else if (temp <= WARM && temp >= COOL)
            StdOut.println("Long Pants, Light Jacket");
        
        else if (temp <= COOL && temp >= COLD)
            StdOut.println("Long Pants, Outer Layer and/or Light Jacket");
        
        else if (temp <= COLD && temp >= FREEZING)
            StdOut.println("Long Pants, Winter Jacket, Hat");
        
        else if (temp <= FREEZING)
            StdOut.println("FREEZING: minimize outdoor exposure");
    }

    /*-----------------------------------------------------------------------*/
    /* Methods that grab values from XML file                                */
    /*-----------------------------------------------------------------------*/

    /***************************************************************/
    /* Extract time from HTML meta-data                            */
    /***************************************************************/
    public static String getTime(String xmlText)
    {
        int start = xmlText.indexOf("<time>") + 6;
        int end = xmlText.indexOf("</time>");
        return xmlText.substring(start, end);
    }
    
    /***************************************************************/
    /* Extract sky from HTML meta-data                             */
    /***************************************************************/
    public static String getSky(String xmlText)
    {
        int start = xmlText.indexOf("<sky>") + 5;
        int end = xmlText.indexOf("</sky>");
        return xmlText.substring(start, end);
    }
    
    /***************************************************************/
    /* Extract town from HTML meta-data                            */
    /***************************************************************/
    public static String getTown(String xmlText)
    {
        int start = xmlText.indexOf("<town>") + 6;
        int end = xmlText.indexOf("</town>");
        return xmlText.substring(start, end);
    }

    /***************************************************************/
    /* Extract state from HTML meta-data                           */
    /***************************************************************/
    public static String getState(String xmlText)
    {
        int start = xmlText.indexOf("<state>") + 7;
        int end = xmlText.indexOf("</state>");
        return xmlText.substring(start, end);
    }

    /***************************************************************/
    /* Extract location from HTML meta-data                        */
    /***************************************************************/
    public static String getZip(String xmlText)
    {
        int start = xmlText.indexOf("<zip>") + 5;
        int end = xmlText.indexOf("</zip>");
        return xmlText.substring(start, end);
    }
    
    /***************************************************************/
    /* Extract temperature from HTML meta-data                     */
    /***************************************************************/
    public static String getTemperature(String xmlText)
    {
        int start = xmlText.indexOf("<temp>") + 6;
        int end = xmlText.indexOf("</temp>");
        return xmlText.substring(start, end);
    }
    
    /***************************************************************/
    /* Extract rain from HTML meta-data                            */
    /***************************************************************/
    public static String getRain(String xmlText)
    {
        int start = xmlText.indexOf("<rain>") + 6;
        int end = xmlText.indexOf("</rain>");
        return xmlText.substring(start, end);
    }
}