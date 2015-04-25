/***************************************************************
 * What-To-Wear
 * By: Keith Gladstone (keithag@princeton.edu)
 * Created in April 2015
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
public class Weather
{
    /***************************************************************/
    /* What to Wear: given weather data output what to wear        */
    /* Enter any valid U.S. zip code as the first argument         */
    /***************************************************************/
    public static void main(String[] args)
    {
        /*
         * Pre-determined user temperature preferences
         */
        final int HOT = 70;
        final int WARM = 60;
        final int COOL = 50;
        final int COLD = 35;
        final int FREEZING = 15;
        
        String zip = "08540"; // default zip code
        
        if (args != null && args.length > 0)
            zip = args[0];
          
        // Exceeding Daily Limits
        //String geoURL = "http://www.zip-info.com/cgi-local/zipsrch.exe?ll=ll&zip=";
        //In geoIn = new In(geoURL + zip);
        //String geoText = geoIn.readAll();
        //String latitude = getLatitude(geoText);
        //String longitude = getLongitude(geoText);
        
        
        String name = "http://www.wunderground.com/cgi-bin/findweather/getForecast?query=";
        In in = new In(name + zip);
        String text = in.readAll(); // scrape HTML site
        int startHead = text.indexOf("<head>");
        int endHead = text.indexOf("</head>");
        String headString = text.substring(startHead, endHead);
      
        /*
         * Get HTML Data
         */
        double temp = getTemperature(headString);
        String location = getLocation(headString);
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
        //StdOut.println("Latitude is " + latitude);  
        //StdOut.println("Longitude is " + longitude);
        StdOut.println("Current Temperature is: " + temp + " degrees Fahrenheit");
        StdOut.println("Sky is " + sky);
        StdOut.println("Rain is " + rainVal + " in.");
        StdOut.print("Consider wearing: ");
        
        
        /*
         * Output temperature analysis
         */

        tempAnalysis(temp, HOT, WARM, COOL, COLD, FREEZING);

        if (rain)
            StdOut.println("Bring an umbrella");
        if (sky.equals("Clear"))
            StdOut.println("Bring sunglasses");
    }
    
    public static String getLatitude(String geoText)
    {
        String tbl1 = "Mailing";
        String tbl2 = "</table>";
        StdOut.println(geoText);
        int i1 = geoText.indexOf(tbl1);
        int i2 = geoText.indexOf(tbl2, i1);
        String table = geoText.substring(i1, i2);
        
        String td = "<td align=center>";
        String td2 = "</font>";
        int iTown = table.indexOf(td);
        int iState = table.indexOf(td, iTown + 1);
        int iZip = table.indexOf(td, iState + 1);
        int iLat = table.indexOf(td, iZip + 1);
        int iLong = table.indexOf(td, iLat + 1);
        
        return table.substring(iLat + td.length(), table.indexOf(td2, iLat));
    }
    public static String getLongitude(String geoText)
    {
        String tbl1 = "Mailing";
        String tbl2 = "</table>";
        int i1 = geoText.indexOf(tbl1);
        int i2 = geoText.indexOf(tbl2, i1);
        String table = geoText.substring(i1, i2);
        
        String td = "<td align=center>";
        String td2 = "</font>";
        int iTown = table.indexOf(td);
        int iState = table.indexOf(td, iTown + 1);
        int iZip = table.indexOf(td, iState + 1);
        int iLat = table.indexOf(td, iZip + 1);
        int iLong = table.indexOf(td, iLat + 1);
        
        return table.substring(iLong + td.length(), table.indexOf(td2, iLong));
    }
    
    /***************************************************************/
    /* Break down an HTML head into the relevant                   */
    /* line for data extraction                                    */
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