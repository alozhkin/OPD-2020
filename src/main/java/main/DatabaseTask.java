package main;

import database.Database;
import database.models.Word;
import utils.Link;

import java.util.Collection;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseTask {
    private Database database;
    private Link domain;
    private Collection<String> words;

    private final Logger log = LoggerFactory.getLogger("main");


    public DatabaseTask(Database database, Link domain, Collection<String> words) {
        this.database = database;
        this.domain = domain;
        this.words = words;
    }

    public boolean run() {
        try {
            return database.putWords(words.stream()
                    // TODO: Put website_id instead of domain.hashCode()
                    .map(word -> new Word(domain.toString().hashCode(), word))
                    .collect(Collectors.toSet()));
        } catch (Exception e) {
            log.error("Fatal error occurred:", e);
            return false;
        }
    }
}
