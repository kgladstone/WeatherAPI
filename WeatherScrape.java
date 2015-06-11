/***************************************************************
 * Attire Decider Weather Scraper
 * By: Keith Gladstone (keithag@princeton.edu)
 * Created in June 2015
 * 
 * This file scrapes the web for Weather data and
 * sends it to a time-stamped output file. This file
 * should be run by the server at regular intervals,
 * as the main weather analyzer checks the timestamp and
 * determines whether or not to pull new data itself.
 *
 * Requires Java Version 8
 *
 * Sample zip codes to test with as first argument
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
import java.io.PrintWriter;
import java.io.FileNotFoundException;

public class WeatherScrape {
    /***************************************************************/
    /* Run the weather scraper on a given zip code                 */
    /***************************************************************/
	public WeatherScrape(String zip)
	{        
        String URL = "http://www.wunderground.com/cgi-bin/findweather/getForecast?query=";
        In in = new In(URL + zip);
        LocalDateTime now = LocalDateTime.now();
        String data = in.readAll(); // scrape HTML site

        /*
         * Get HTML Data from input
         */
        String headString = getHeader(data);
        String town = getTown(headString); 
        String state = getState(headString);
        String temp = getTemperature(headString);
        String sky = getSky(headString);
        String rain = getRain(data);
        String humidity = getHumidity(data);
        String feel = getFeel(data);


        // Send data to a file named by the zip code
        File file = new File("data/" + zip);
        try
        {
            PrintWriter printWriter = new PrintWriter(file);
            // Output the following
            printWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            printWriter.println("<response>");
            printWriter.println("<time>" + now + "</time>");
            printWriter.println("<weather>");
            printWriter.println("<town>" + town + "</town>");
            printWriter.println("<state>" + state + "</state>");
            printWriter.println("<zip>" + zip + "</zip>");
            printWriter.println("<temp>" + temp + "</temp>");
            printWriter.println("<feel>" + feel + "</feel>");
            printWriter.println("<sky>" + sky + "</sky>");
            printWriter.println("<rain>" + rain + "</rain>");
            printWriter.println("<humidity>" + humidity + "</humidity>");
            printWriter.println("</weather>");
            printWriter.println("</response>");
            printWriter.close();       
        }
        catch (FileNotFoundException ex)  
        {
            System.out.println("FileNotFoundException: Error in creating file");
        }
	}

    /***************************************************************/
    /* Returns the head portion of the HTML string                 */
    /***************************************************************/
    private String getHeader(String text)
    {
        // Return the <head>
        int startHead = text.indexOf("<head>");
        int endHead = text.indexOf("</head>");
        return text.substring(startHead, endHead);

    }

    /***************************************************************/
    /* Break down an HTML head into the relevant                   */
    /* line for data extraction: a certain line of meta-data       */
    /***************************************************************/
    private String subInfo(String info)
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
    private String getSky(String info)
    {
        String subInfo = subInfo(info);
        int partition1 = subInfo.indexOf("|");
        int partition2 = subInfo.indexOf("|", partition1 + 1);
        return subInfo.substring(partition2 + 2, subInfo.length() - 2);
    }
    
    /***************************************************************/
    /* Extract town from HTML meta-data                            */
    /***************************************************************/
    private String getTown(String info)
    {
        String subInfo = subInfo(info);
        int partition1 = subInfo.indexOf(",");
        int partition2 = subInfo.indexOf("|", partition1 + 1);
        return subInfo.substring(1, partition1);
    }
    /***************************************************************/
    /* Extract location from HTML meta-data                        */
    /***************************************************************/
    private String getState(String info)
    {
        String subInfo = subInfo(info);
        int partition1 = subInfo.indexOf(",");
        int partition2 = subInfo.indexOf("|", partition1 + 1);
        return subInfo.substring(partition1 + 2, partition2 - 1);
    }
    /***************************************************************/
    /* Extract location from HTML meta-data                        */
    /***************************************************************/
    private String getZip(String info)
    {
        int partition1 = info.indexOf("(");
        int partition2 = info.indexOf(")", partition1 + 1);
        return info.substring(partition1 + 1, partition2);
    }
    
    /***************************************************************/
    /* Extract temperature from HTML meta-data                     */
    /***************************************************************/
    private String getTemperature(String info)
    {
        
        String subInfo = subInfo(info);
        int partition1 = subInfo.indexOf("|");
        int partition2 = subInfo.indexOf("|", partition1 + 1);
        return subInfo.substring(partition1 + 2, subInfo.indexOf("&"));
    }
    
    /***************************************************************/
    /* Extract rain from HTML meta-data                            */
    /***************************************************************/
    private String getRain(String text)
    {
        int rainTag = text.indexOf("precip_today");
        int midRainTag = text.indexOf("wx-value", rainTag);
        int endRainTag = text.indexOf("</span>", rainTag);
        return text.substring(midRainTag + 10, endRainTag);        
    }

    private String getHumidity(String text)
    {
        int tag = text.indexOf("\"humidity\":") + 11;
        int end = text.indexOf(",", tag);
        return text.substring(tag, end);
    }
    

    private String getFeel(String text)
    {
        int tag = text.indexOf("\"feelslike\":") + 12;
        int end = text.indexOf(",", tag);
        return text.substring(tag, end);
    }    /***************************************************************/
    /* Allow for command-line usage                                */
    /***************************************************************/
	public static void main(String[] args) 
	{
		String zip = "08540"; // default zip code
        
        if (args != null && args.length > 0)
            zip = args[0]; // set zip code to first argument on run

        WeatherScrape scraper = new WeatherScrape(zip); // run scraper
    }
}