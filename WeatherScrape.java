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
 * Use "> [filename]" in the commandline to output
 * the data to [filename] with a timestamp prefix in the text.
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
	private String data; // weather data
	private LocalDateTime now; // time of capture
    /***************************************************************/
    /* Run the weather scraper on a given zip code                 */
    /***************************************************************/
	public WeatherScrape(String zip)
	{        
        String name = "http://www.wunderground.com/cgi-bin/findweather/getForecast?query=";
        In in = new In(name + zip);

        now = LocalDateTime.now();
        data = in.readAll(); // scrape HTML site

        // Send data to a file named by the zip code
        File file = new File("data/" + zip);
        try
        {
            PrintWriter printWriter = new PrintWriter(file);
            // Output the following
            printWriter.println(now);
            printWriter.println(data);
            printWriter.close();       
        }
        catch (FileNotFoundException ex)  
        {

            System.out.println("Error: FileNotFoundException");// insert code to run when exception occurs
        }
	}

	public String getData()
	{
		return data;
	}

	public LocalDateTime getTimeOfCapture()
	{
		return now;
	}
    /***************************************************************/
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