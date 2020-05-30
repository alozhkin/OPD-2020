# OPD-2020
OPD-2020 is a small scraper which is intended to extract words from static and dynamic pages. OPD-2020 was made taking into account the fact that sites work on different technologies, and works great on most of them. It can filter links and knows how to distiguish pages by language, making scraped information useful.
## Technology stack
We use [Splash](https://github.com/scrapinghub/splash), Docker and [Jsoup](https://jsoup.org/). [WireMock](https://github.com/tomakehurst/wiremock) for server mocking. The primary source of docker-compose.yml and docker/haproxy.cfg files is [Aquarium](https://github.com/TeamHG-Memex/aquarium), though they were heavily rewritten.
## Getting started
1. Install docker-compose 

### MacOs 

Install [homebrew](https://docs.brew.sh/Installation) if you do not have it and
use Terminal or other methods described [here](https://pilsniak.com/how-to-install-docker-on-mac-os-using-brew/) 
```
brew install docker docker-compose docker-machine xhyve docker-machine-driver-xhyve
```
### Linux
```
sudo apt install docker-compose
```
2. Go to the folder with project using
```
cd YOUR_PATH_TO_PROJECT
```
3. Run gradle build (if you change code you must rebuild)
```
./gradlew build
```
4. Run docker-compose build (If you have problems at startup try [this](https://github.com/docker/compose/issues/2180#issuecomment-147769429)) (if you don't change code, you don't need to rebuild)
```
docker-compose build
```
5. Add websites to `docker/input/websites_data.csv`

6. Run program
```
docker-compose up
```
7. Get results in `docker/results` after opd termination.
## Alternative ways to run the program
You can run the program from Main class or console. You need working Splash on port 8050 and set property ```inside.container=false```.

To run from console just run jar ```java -jar words_extractor.jar "INPUT_FILE_PATH" ```, you can use the options ```-o OUTPUT_FILE_PATH``` and ```-db DATABASE_FILE_PATH```
## Features
First of all, we use *Splash* to render html and run js code. What can it do?

- Load pages in parallel.
- Use AdBlock filters (`docker/filters`) to block useless http requests. We do not load css, images and analytics.
- Communicate through HTTP API, what gives an opportunity to attach load balancer (haproxy) and run many instances at the same time with help of docker-compose.
- Splash is very lightweight. Splash is not a browser, it was originally created to take screenshots of websites, and later got new features.
- Splash has a disadvantage as well. It has little memory leak, and only way to get memory back is to restart splash. Splash restarts quickly (within 5 seconds). Splash automatically checks memory consumption every minute and restarts, when memory consumption is too high.

## Perfomance
The main question of perfomance is a balance between number of Splash slots (that allows to load pages in parallel) and number of Splash instances. The answer to that question depends on the CPU performance and the size of RAM.

Most of the time wastes on waiting for page to load and js to run. All other parts of project insignificant in comparison.

## Architecture
Program basis is **Spider** class. For every site in csv **Spider** runs **DomainTask** which purpose to get all words from website. **DomainTask** transfers link to **Scraper**, that sends async HTTP request to Splash and get html with related information. **PageTask** responsible for html processing, html goes through **Crawler** (responsible for link retrieving) to **LinkFilter** and through **WordExtractor** to **WordFilter**. After that links and words goes back to **DomainTask**, which accumulate words and sends new links to **Scraper**. After all pages on site are visited DomainTask stops its work and **Spider** adds words into database

## Configuration
In `docker-compose.yml` and `docker/haproxy`, you can increase quantity of Splash instances and their RAM. All properties in src/main/resources/properties.

## Documentation
JavaDoc

## FAQ
### Why don't we use selenium? 
Selenium is test automation framework and it is not intended to web scraping. It is too slow and lacks useful features like http responce codes.

### Why don't we use headless chrome? 
We tried, but the results were bad. Chrome is bigger than lightweight Splash, and we have not found a way to load many pages at once. So Splash were much faster.

