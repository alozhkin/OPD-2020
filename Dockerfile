FROM openjdk:11
VOLUME /tmp
ADD build/libs/words_extractor.jar words_extractor.jar
ENTRYPOINT ["java","-jar","/words_extractor.jar", "/usr/local/etc/input/websites_data.csv", "-o", "/usr/local/etc/results/export_", "-db", "/usr/local/etc/results/websites.db"]
