package main;

import database.Database;
import database.models.Word;
import utils.Link;

import java.util.Collection;
import java.util.stream.Collectors;

public class DatabaseTask {
    private Database database;
    private Link domain;
    private Collection<String> words;

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
            e.printStackTrace();
            return false;
        }
    }
}