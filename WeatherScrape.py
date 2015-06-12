# /***************************************************************
#  * Attire Decider Weather Scraper <PYTHON VERSION>
#  * By: Keith Gladstone (keithag@princeton.edu)
#  * Created in June 2015
#  * 
#  * This file scrapes the web for Weather data and
#  * sends it to a time-stamped output file. This file
#  * should be run by the server at regular intervals,
#  * as the main weather analyzer checks the timestamp and
#  * determines whether or not to pull new data itself.
#  *
#  * Requires Python on server
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
import urllib
import datetime
import sys

# Scraping functions

# Isolate <head> of the HTML page
def getHeader(text):
	start = text.index("<head>") + 6
	end = text.index("</head>")
	return text[start:end]

# Isolate the meta-data item that contains weather data
def metaData(info):
	left = "<meta property=\"og:title\" content="
	right = "/>"
	start = info.index(left) + len(left)
	end = info.index(right, start)
	return info[start:end]

def getSky(info):
	subinfo = metaData(info)
	start = subinfo.index("|")
	end = subinfo.index("|", start + 1)
	return subinfo[end + 2 : len(subinfo) - 2]

def getTown(info):
	subinfo = metaData(info)
	start = subinfo.index(",")
	end = subinfo.index("|", start + 1)
	return subinfo[1 : start]

def getState(info):
	subinfo = metaData(info)
	start = subinfo.index(",")
	end = subinfo.index("|", start + 1)
	return subinfo[start + 2 : end - 1]

def getZip(info):
	subinfo = metaData(info)
	start = subinfo.index("(")
	end = subinfo.index(")", start + 1)
	return subinfo[start + 1 : end]

def getTemperature(info):
	subinfo = metaData(info)
	start = subinfo.index("|")
	end = subinfo.index("&")
	return subinfo[start + 2 : end]

def getRain(text):
	rainTag = text.index("precip_today")
	midRainTag = text.index("wx-value", rainTag)
	endRainTag = text.index("</span>", rainTag)
	return text[midRainTag + 10 : endRainTag]

def getHumidity(text):
	tag = text.index("\"humidity\":") + 11
	end = text.index(",", tag)
	return text[tag:end]

def getFeel(text):
	tag = text.index("\"feelslike\":") + 13
	end = text.index(",", tag)
	return text[tag:end]

def formatTime(time):
	i = time.index(" ")
	fmtd = time[0:i] + "T" + time [i + 1 : len(time)]
	return fmtd


# Execution below


# This scrapes the HTML
root = "http://www.wunderground.com/cgi-bin/findweather/getForecast?query="
zip = "08540" # default zip code if no argument

if len(sys.argv) > 1:
	zip = sys.argv[1]

URL = root + zip
sock = urllib.urlopen(URL)
content = sock.read()
sock.close()

# Get items from content of HTML page
now = str(datetime.datetime.now())
header = getHeader(content)
town = getTown(header)
state = getState(header)
temp = getTemperature(header)
sky = getSky(header)
rain = getRain(content)
humidity = getHumidity(content)
feel = getFeel(content)


# Open the file with writing permission
filename = "data/" + zip
myfile = open(filename, 'w')
 
# Write data to the XML-formatted file
myfile.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
myfile.write("<response>\n")
myfile.write("<time>" + formatTime(now) + "</time>\n")
myfile.write("<weather>\n")
myfile.write("<town>" + town + "</town>\n")
myfile.write("<state>" + state + "</state>\n")
myfile.write("<zip>" + zip + "</zip>\n")
myfile.write("<temp>" + temp + "</temp>\n")
myfile.write("<feel>" + feel + "</feel>\n")
myfile.write("<sky>" + sky + "</sky>\n")
myfile.write("<rain>" + rain + "</rain>\n")
myfile.write("<humidity>" + humidity + "</humidity>\n")
myfile.write("</weather>\n")
myfile.write("</response>\n")
 
# Close the file
myfile.close()