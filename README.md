# AttireDecider
Weather Analyzer -- Now in Python!

Using webscraped weather data, app tells user what to wear. Makes use a separate scraper file that would be run on a server at certain predetermined intervals, and the main analyzer file checks the timestamp of the scraped data file before deciding to scrape from the Internet again -- conserving API calls.

Goals: use machine learning to adjust to user’s optimal temperature preferences. 

## Weather API

Using this program generates XML files containing weather data for a given zip code in real time. Technically, this takes the form of an API for further use.

