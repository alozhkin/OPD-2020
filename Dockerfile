FROM openjdk:11
VOLUME /tmp
ADD build/libs/words_extractor-1.0-SNAPSHOT.jar words_extractor.jar
ENTRYPOINT ["java","-jar","/words_extractor.jar", "/usr/local/etc/input/websites_data.csv", "-o", "/usr/local/etc/results/export.csv"]