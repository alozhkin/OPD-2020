# OPD-2020
OPD-2020 is a small scraper which is intended to extract words from static and dynamic pages. OPD-2020 was made taking into account the fact that sites can be made in different ways, and works great on most of them. It can filter links and knows how to distiguish by language pages, to make scraped information useful.
## Technology stack
We use Splash like page renderer inside Docker, Jsoup like parser. WireMock for Server mocking on tests.
## Installation
### Mac OS
0. Instal [homebrew](https://docs.brew.sh/Installation) if you do not have it
1. Install docker-compose using Terminal or use other methods described [here](https://pilsniak.com/how-to-install-docker-on-mac-os-using-brew/) 

```
brew install docker docker-compose docker-machine xhyve docker-machine-driver-xhyve
```
2. Go to the folder **YOUR_PATH_TO_PROJECT/src/main/aquarium** using
```
cd YOUR_PATH_TO_PROJECT/src/main/aquarium
```
3. Run command (If you have problems at startup try [this](https://github.com/docker/compose/issues/2180#issuecomment-147769429))
```
docker-compose up
```
4. Run program

### Linux 
1. Install docker-compose using in Terminal
```
sudo apt install docker-compose
```
2. Go to the folder **YOUR_PATH_TO_PROJECT/src/main/aquarium** using
```
cd YOUR_PATH_TO_PROJECT/src/main/aquarium
```
3. Run command
```
sudo docker-compose up
```
4. Run program
### Another systems may have problems with 8050 Splash port access.

## Program launch
There are three ways to run a program:
1. Run "main" method in main.Main. The default values will be used as the input and output paths. The program will start without a progress bar. 
2. Run "main" method in ui.ConsoleUI. As program arguments, you must pass the path to the input file with sites, and optionally you can pass the path to the output file using option ```-o OUTPUT_FILE_PATH``` and the path to the database using option ```-db DATABASE_FILE_PATH```. The program will start with the progress bar.
3. Build Jar file via running "jar" in build.gradle. After that run 
```
java -jar words_extractor.jar "INPUT_FILE_PATH" 
```
just like in ConsoleUI, you can use the options ```-o OUTPUT_FILE_PATH``` and ```-db DATABASE_FILE_PATH```
## How does it work?
First of all, we use Splash to render html and run js code. What can it do?

- Load pages in parallel.
- Use AdBlock filters to block useless http requests. We do not load css, images and analytics.
- Splash communicate through HTTP API, what gives an opportunity to attach load balancer and run many instances at the same time. We use haproxy which come together with aquarium.
- Splash is very lightweight. Splash is not a browser, it was originally created to take screenshots of websites, and later got new features.
- Splash has a disadvantage as well. It has little memory leak, and only way to get memory back is to restart splash. Splash restarts quick (within 5 seconds). Splash automatically checks memory consumption every minute and restarts, when memory consumption is too high.
Why don't we use selenium? Selenium is test automation framework and it is not intended to web scraping. It is too slow and lacks useful features like http responce codes.

Why don't we use headless chrome? We tried, but the results were bad. Chrome is bigger than lightweight Splash, and we have not found a way to load many pages at once. So Splash were much faster.

## Perfomance.
The main question of perfomance is a balance between number of Splash slots (that allows to load pages in parallel) and number of Splash instances. The answer to that question depends on the CPU performance and the size of RAM.

Now most of the time wastes on waiting for page to load and js to run. All other parts of project insignificant in comparison.

## Architecture
Program basis is Spider class, that takes path to csv file and returns ???. For every site in csv Spider runs DomainTask which purpose to get all words from website. DomainTask transfers link to Scraper. Scraper takes link, sends async request to Splash and get html with related information. Scraper runs PageTask and transfers html to it. PageTask sends html to Crawler and Extractor. Crawler retrieve links from html with help of Jsoup and then PageTask transfer them to LinkFilter which need to minimalize number of useless pages. Extractor extracts words from html with help of jsoup, and PageTask sends it to WordFilter, which gets rid of prepositions and not words. After that links and words goes back to DomainTask, which accumulate words and sends new links to scraper.

It tries to avoid scraping useless pages, knows how to distiguish page by language.

After all pages on site are visited DomainTask stops its work and Spider adds words into database
