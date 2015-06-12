# /***************************************************************
#  * Attire Decider Weather Analyzer <PYTHON VERSION>
#  * By: Keith Gladstone (keithag@princeton.edu)
#  * Created in June 2015
#  * 
#  * This program takes advantage of the archiving ability of the
#  * scraper. Namely, it can pull scraped data from a stored XML file
#  * with a timestamp before deciding to pull data again from the Internet.
#  * This method conserves HTTP calls and reduces traffic, allowing for
#  * rapid testing at no risk of disturbing the data source.
#  * 
#  * Requires Python
#  * Assume that if XML file exists, it is formatted correctly.
#  *
#  * Sample zip codes to test with 
#  * 
#  * Princeton : 08540    // for testing normal
#  * Seattle   : 98101    // for testing rain
#  * Miami     : 33101    // for testing heat
#  * Chicago   : 60290    // for testing cool
#  * Caribou, ME : 04736  // for testing cold
#  * Lahaina, HI : 96761  // for testing different time of day
#  * Schenectady, NY : 12345 // for interesting zip code
#  ***************************************************************/
import datetime
import sys
import os.path

# Universal XML parser
def get(xmlText, tag):
	start = xmlText.index("<" + tag + ">") + len(tag) + 2
	end = xmlText.index("</" + tag + ">")
	return xmlText[start:end]

def tempAnalysis(temp, HOT, WARM, COOL, COLD, FREEZING):
    if temp >= HOT:
        print("T-Shirt, Shorts")
    
    elif temp <= HOT and temp >= WARM:
        print("T-Shirt and Shorts + Layer")
    
    elif temp <= WARM and temp >= COOL:
        print("Long Pants, Light Jacket")
    
    elif temp <= COOL and temp >= COLD:
        print("Long Pants, Outer Layer and/or Light Jacket")
    
    elif temp <= COLD and temp >= FREEZING:
        print("Long Pants, Winter Jacket, Hat")
    
    elif temp <= FREEZING:
        print("FREEZING: minimize outdoor exposure")

# Execution

CHRONINTERVAL = 30

HOT = 70
WARM = 60
COOL = 50
COLD = 35
FREEZING = 15

zip = "08540" # default zip code if no argument

if len(sys.argv) > 1:
	zip = sys.argv[1]

# Update subjective weather preferences
if len(sys.argv) > 3:
    COLD = float(sys.argv[2])
    WARM = float(sys.argv[3])
    COOL = (WARM + COLD) / 2
    FREEZING = COLD - 0.5*(COOL - COLD)
    HOT = WARM + 0.5*(WARM - COOL)
    print("Custom Temperature Preferences:")
    print("Hot:     \t" + str(HOT))
    print("Warm:    \t" + str(WARM))
    print("Cool:    \t" + str(COOL))
    print("Cold:    \t" + str(COLD))
    print("Freezing:\t" + str(FREEZING))


filename = "data/" + zip
now = datetime.datetime.now()

if os.path.isfile(filename) == False: 
	os.system("python WeatherScrape.py " + zip)

with open(filename) as f:
    xmlText = f.read()

then = datetime.datetime.strptime(get(xmlText, "time"), "%Y-%m-%dT%H:%M:%S.%f")
thatZip = get(xmlText, "zip")

#  Refresh the weather data if necessary (mismatched location OR data too old)
diff = now - then 
if (thatZip != zip or (diff.seconds / 60) > CHRONINTERVAL):
	os.system("python WeatherScrape.py " + zip)

# Get weather variables
location = get(xmlText, "town") + ", " + get(xmlText, "state")
temp = float(get(xmlText, "temp"))
feel = float(get(xmlText, "feel"))
humidity = float(get(xmlText, "humidity"))
sky = get(xmlText, "sky")

# Handle rain
rainVal = float(get(xmlText, "rain"))
if rainVal == 0:
	rain = False
else:
	rain = True

print("Weather for " + location)
print("Current Temperature is: " + str(temp) + " degrees Fahrenheit")
print("Feels like: " + str(feel) + " degrees Fahrenheit")
print("Humidity: " + str(humidity) + "%")
print("Sky is " + sky)
print("Rain is " + str(rainVal) + " in.")
print("Consider wearing: ")
tempAnalysis(temp, HOT, WARM, COOL, COLD, FREEZING)

if rain:
	print("Bring an umbrella\n")
if sky == "Clear":
	print("Bring sunglasses\n")