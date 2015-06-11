/***************************************************************
 * Attire Decider Weather Analyzer
 * By: Keith Gladstone (keithag@princeton.edu)
 * Created in June 2015
 * 
 * This program takes advantage of the archiving ability of the
 * scraper. Namely, it can pull scraped data from a stored file
 * with a timestamp before deciding to pull data again from the Internet.
 * This method conserves API calls and reduces traffic, allowing for
 * rapid testing at no risk of disturbing the data source.
 * 
 * Requires Java Version 8
 * Assume that if input file exists, it is formatted correctly.
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
        
        LocalDateTime now = LocalDateTime.now(); // grab current time
        
        /*
         * Handle commandline arguments
         */

        String filename = args[0]; // weather input filename
        String zip = "08540";
        if (args.length > 1)
            zip = args[1]; // set zip code to second argument on run

        /*
         * Read weather data from saved file
         */
        File f = new File(filename);
        String headString;
        String location;
        String text;

        if(!f.exists()) // Need new data because input file does not exist
        { 
            text = getNewData(zip); // PERFORM A SCRAPE
            headString = getHeader(text);
            location = getLocation(headString); // UPDATE location
        }

        else // Input file exists
        {
            In in = new In(filename);
            text = in.readAll(); // GET DATA FROM EXISTING FILE
            int htmlStart = text.indexOf("\n<!DOCTYPE html>");

            // Capture the timestamp and print it
            String thenStr = text.substring(0, htmlStart - 1);
            LocalDateTime then = LocalDateTime.parse(thenStr);

            /*
             * Markers for parsing the input file
            */
            headString = getHeader(text);

            location = getLocation(headString); // Parse geographical location of input data
            String thatZip = getZip(headString); // Parse zip code of input data


            /*
            * Refresh the weather data if necessary (mismatched location OR data too old)
            */
            if (!thatZip.equals(zip) || now.minusMinutes(CHRONINTERVAL).compareTo(then) > 0) // if at least XXX time later
            {
                text = getNewData(zip); // PERFORM A SCRAPE
                headString = getHeader(text);
                location = getLocation(headString); // UPDATE location
            }
        }

        /*
         * Get HTML Data from input
         */
        double temp = getTemperature(headString);
        String sky = getSky(headString);
        
        /*
         * Deal with rain variable
         */
        Double rainVal = getRain(text);
        boolean rain;
        if (rainVal == 0)
            rain = false;
        else
            rain = true;
       
        /*
         * Output collected HTML data
         */
        StdOut.println("Weather for " + location);
        StdOut.println("Current Temperature is: " + temp + " degrees Fahrenheit");
        StdOut.println("Sky is " + sky);
        StdOut.println("Rain is " + rainVal + " in.");
        StdOut.print("Consider wearing: ");
        
        
        /*
         * Output temperature analysis
         * This section needs to be catered to user preferences in the next version
         */

        tempAnalysis(temp, HOT, WARM, COOL, COLD, FREEZING);

        if (rain)
            StdOut.println("Bring an umbrella");
        if (sky.equals("Clear"))
            StdOut.println("Bring sunglasses");
    }
    


    /*-----------------------------------------------------------------------*/
    /* Analysis methods                                                      */
    /*-----------------------------------------------------------------------*/


    /***************************************************************/
    /* Returns the head portion of the HTML string                 */
    /***************************************************************/
    public static String getHeader(String text)
    {
        // Return the <head>
        int startHead = text.indexOf("<head>");
        int endHead = text.indexOf("</head>");
        return text.substring(startHead, endHead);

    }


    /***************************************************************/
    /* Performs another scrape and returns the full HTML text      */
    /***************************************************************/
    public static String getNewData(String zip)
    {
        WeatherScrape scraper = new WeatherScrape(zip);

        String text = scraper.getData();
        //StdOut.println("FILE outdated or different location...\n\tnew data captured at " 
        //+ scraper.getTimeOfCapture());
        return text;
    }

    /***************************************************************/
    /* Break down an HTML head into the relevant                   */
    /* line for data extraction: a certain line of meta-data       */
    /***************************************************************/
    public static String subInfo(String info)
    {
        String left = "<meta property=\"og:title\" content=";
        String right = "/>";
        int index1 = info.indexOf(left) + left.length();
        int index2 = info.indexOf(right, index1);
        
        return info.substring(index1, index2);
    }
    
    /***************************************************************/
    /* Extract sky from HTML meta-data                             */
    /***************************************************************/
    public static String getSky(String info)
    {
        String subInfo = subInfo(info);
        int partition1 = subInfo.indexOf("|");
        int partition2 = subInfo.indexOf("|", partition1 + 1);
        return subInfo.substring(partition2 + 2, subInfo.length() - 2);
    }
    
    /***************************************************************/
    /* Extract location from HTML meta-data                        */
    /***************************************************************/
    public static String getLocation(String info)
    {
        String subInfo = subInfo(info);
        int partition1 = subInfo.indexOf("|");
        int partition2 = subInfo.indexOf("|", partition1 + 1);
        return subInfo.substring(1, partition1);
    }
    /***************************************************************/
    /* Extract location from HTML meta-data                        */
    /***************************************************************/
    public static String getZip(String info)
    {
        int partition1 = info.indexOf("(");
        int partition2 = info.indexOf(")", partition1 + 1);
        return info.substring(partition1 + 1, partition2);
    }
    
    /***************************************************************/
    /* Extract temperature from HTML meta-data                     */
    /***************************************************************/
    public static double getTemperature(String info)
    {
        
        String subInfo = subInfo(info);
        int partition1 = subInfo.indexOf("|");
        int partition2 = subInfo.indexOf("|", partition1 + 1);
        String temperature = subInfo.substring(partition1 + 2, subInfo.indexOf("&"));
        return Double.parseDouble(temperature);
    }
    
    /***************************************************************/
    /* Extract rain from HTML meta-data                            */
    /***************************************************************/
    public static double getRain(String text)
    {
        int rainTag = text.indexOf("precip_today");
        int midRainTag = text.indexOf("wx-value", rainTag);
        int endRainTag = text.indexOf("</span>", rainTag);
        String rainStr = text.substring(midRainTag + 10, endRainTag);
        
        return Double.parseDouble(rainStr);
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
}