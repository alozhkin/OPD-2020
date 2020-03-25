package database;

import config.ConfigurationUtils;
import database.models.Website;
import database.models.Word;
import database.utils.DatabaseUtil;
import utils.CSVParser;
import utils.Link;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

class DatabaseImpl implements Database {

    private String url;

    /* package-private

        DO NOT CHANGE ACCESS MODIFIER!

        Use IDatabase.newInstance() to create database object
   */
    DatabaseImpl() {
        try {
            url = ConfigurationUtils.parseDatabaseUrl();
            initDatabase();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean putWebsitesFromCSV(String CSVFile) {
        CSVParser parser = new CSVParser();
        parser.parse(CSVFile);
        return putWebsites(parser.getWebsites());
    }

    @Override
    public boolean putWebsite(Website website) {
        return putWebsite(website.getCompanyId(), website.getLink().getAbsoluteURL());
    }

    @Override
    public boolean putWebsites(Collection<Website> websites) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement preparedStatement = DatabaseUtil.getWebsitesPreparedStatement(websites, connection)) {
                preparedStatement.executeUpdate();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean putWord(Word word) {
        return putWord(word.getWebsiteId(), word.getWord());
    }

    @Override
    public boolean putWords(Collection<Word> words) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement preparedStatement = DatabaseUtil.getWordsPreparedStatement(words, connection)) {
                preparedStatement.executeUpdate();

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean clearWebsites() {
        String statement = "DELETE FROM websites";
        if (executeStatement(statement)) {
            String resetIncrement = "DELETE FROM SQLITE_SEQUENCE WHERE NAME='websites'";
            return executeStatement(resetIncrement);
        } else {
            return false;
        }
    }

    @Override
    public boolean clearWords() {
        String statement = "DELETE FROM words";
        if (executeStatement(statement)) {
            String resetIncrement = "DELETE FROM SQLITE_SEQUENCE WHERE NAME='words'";
            return executeStatement(resetIncrement);
        } else {
            return false;
        }
    }

    @Override
    public int getWebsitesSize() {
        String query = "SELECT COUNT(*) FROM websites";
        return getSizeFromQuery(query);
    }

    @Override
    public int getWordsSize() {
        String query = "SELECT COUNT(*) FROM words";
        return getSizeFromQuery(query);
    }

    @Override
    public boolean exportDataToCSV(String filepath) {
        File file = new File(filepath);
        file.delete();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            String header = "\"id\";\"website_id\";\"word\"";
            writer.append(header);
            List<Word> list = getWordsData();
            for (Word w : list) {
                writer.append(String.format("\n%d;%d;\"%s\"", w.getId(), w.getWebsiteId(), w.getWord()));
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public HashSet<Website> getWebsites() {
        String query = "SELECT * FROM websites";
        return getWebsitesByQuery(query);
    }

    @Override
    public HashSet<Website> getWebsites(String word) {
        String query = "SELECT * FROM websites WHERE company_id=(SELECT website_id FROM words WHERE word='" + word + "')";
        return getWebsitesByQuery(query);
    }

    @Override
    public HashSet<Website> getWebsites(int companyId) {
        String query = "SELECT * FROM websites WHERE company_id='" + companyId + "'";
        return getWebsitesByQuery(query);
    }

    @Override
    public HashSet<String> getWebsiteLink(int companyId) {
        String query = "SELECT * FROM websites WHERE company_id='" + companyId + "'";
        HashSet<String> set = new HashSet<>();

        try (Connection connection = getConnection()) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet rset = statement.executeQuery(query)) {
                    while (rset.next()) {
                        String link = rset.getString(3);
                        set.add(link);
                    }
                    return set;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return set;
        }
    }

    @Override
    public HashSet<Word> getWords() {
        String query = "SELECT * FROM words";
        return getWords(query);
    }

    @Override
    public HashSet<Word> getWords(int websiteId) {
        String query = "SELECT * FROM words WHERE website_id = '" + websiteId + "'";
        return getWords(query);
    }

    @Override
    public Word getWord(int wordId) {
        String query = "SELECT * FROM words WHERE id = '" + wordId + "'";

        try (Connection connection = getConnection()) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet rset = statement.executeQuery(query)) {
                    rset.next();
                    String wordStr = rset.getString(3);
                    int websiteId = rset.getInt(2);
                    int id = rset.getInt(1);
                    return new Word(id, websiteId, wordStr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getWordId(String word) {
        String query = "SELECT * FROM words WHERE word = '" + word + "'";

        try (Connection connection = getConnection()) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet rset = statement.executeQuery(query)) {
                    rset.next();
                    return rset.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void initDatabase() throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");

        try (Connection connection = getConnection()) {

            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS websites ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , 'company_id' int(11) NOT NULL , 'website' TEXT NOT NULL)");
            statement.execute("CREATE TABLE IF NOT EXISTS words ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , 'website_id' int(11) NOT NULL , 'word' TEXT NOT NULL)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    private boolean putWebsite(int companyId, String website) {
        String statement = "INSERT INTO websites (company_id, website) VALUES (?, ?)";
        return executeStatementWithParams(companyId, website, statement);
    }

    private boolean putWord(int websiteId, String word) {
        String statement = "INSERT INTO words (website_id, word) VALUES (?, ?)";
        return executeStatementWithParams(websiteId, word, statement);
    }

    private boolean executeStatement(String statement) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean executeStatementWithParams(int subId, String content, String statement) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {

                preparedStatement.setInt(1, subId);
                preparedStatement.setString(2, content);
                preparedStatement.executeUpdate();

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private int getSizeFromQuery(String query) {
        try (Connection connection = getConnection()) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet rset = statement.executeQuery(query)) {
                    rset.next();
                    return rset.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private HashSet<Word> getWords(String query) {
        HashSet<Word> set = new HashSet<>();
        try (Connection connection = getConnection()) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet rset = statement.executeQuery(query)) {
                    while (rset.next()) {
                        int id = rset.getInt(1);
                        int websiteId = rset.getInt(2);
                        String word = rset.getString(3);
                        set.add(new Word(id, websiteId, word));
                    }
                    return set;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return set;
        }
    }

    private HashSet<Website> getWebsitesByQuery(String query) {
        HashSet<Website> set = new HashSet<>();
        try (Connection connection = getConnection()) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet rset = statement.executeQuery(query)) {
                    while (rset.next()) {
                        int companyId = rset.getInt(2);
                        String website = rset.getString(3);
                        set.add(new Website(companyId, new Link(website)));
                    }
                    return set;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return set;
        }
    }

    private List<Word> getWordsData() throws SQLException {
        String query = "SELECT * FROM words";
        try (Connection connection = getConnection()) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet rset = statement.executeQuery(query)) {
                    List<Word> wordList = new ArrayList<>();
                    while (rset.next()) {
                        int id = rset.getInt(1);
                        int websiteId = rset.getInt(2);
                        String word = rset.getString(3);
                        Word tempWord = new Word(id, websiteId, word);
                        wordList.add(tempWord);
                    }
                    return wordList;
                }
            }
        }
    }
}