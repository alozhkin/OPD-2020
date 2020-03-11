package database;

import database.models.Website;
import database.models.Word;
import database.utils.CSVUtils;
import database.utils.DatabaseUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

class DatabaseImpl implements Database {

    private String url;
    private String username;
    private String password;

    /* package-private

        DO NOT CHANGE ACCESS MODIFIER!

        Use IDatabase.newInstance() to create database object
   */
    DatabaseImpl() {
        try {
            parseProperties();
            initDatabase();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean putWebsitesFromCsv(String csvFile) {
        return putWebsites(CSVUtils.parseLines(csvFile));
    }

    @Override
    public boolean putWebsite(int companyId, String website) {
        String statement = "INSERT INTO websites (company_id, website) VALUES (?, ?)";
        return executeStatementWithParams(companyId, website, statement);
    }

    @Override
    public boolean putWebsite(Website website) {
        return putWebsite(website.getCompanyId(), website.getLink());
    }

    @Override
    public boolean putWebsites(List<Website> websites) {
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
    public boolean putWord(int websiteId, String word) {
        String statement = "INSERT INTO words (website_id, word) VALUES (?, ?)";
        return executeStatementWithParams(websiteId, word, statement);
    }

    @Override
    public boolean putWord(Word word) {
        return putWord(word.getWebsiteId(), word.getWord());
    }

    @Override
    public boolean putWords(List<Word> words) {
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
        return executeStatement(statement);
    }

    @Override
    public boolean clearWords() {
        String statement = "DELETE FROM words";
        return executeStatement(statement);
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
    public List<Website> getWebsites() {
        String query = "SELECT * FROM websites";
        return getWebsitesByQuery(query);
    }

    @Override
    public List<Website> getWebsites(String word) {
        String query = "SELECT * FROM websites WHERE company_id=(SELECT 2 FROM words_websites WHERE word_id=" +
                "(SELECT 2 FROM words WHERE word='"+word+"'))";
        return getWebsitesByQuery(query);
    }

    @Override
    public List<Website> getWebsites(int companyId) {
        String query = "SELECT * FROM websites WHERE company_id='" + companyId + "'";
        return getWebsitesByQuery(query);
    }

    @Override
    public List<String> getWebsiteLink(int companyId) {
        String query = "SELECT * FROM websites WHERE company_id='" + companyId + "'";
        List<String> list = new ArrayList<>();
        try (Connection connection = getConnection()) {
            try (Statement statement = connection.createStatement()) {
                ResultSet set = statement.executeQuery(query);
                while(set.next()){
                    String link = set.getString(3);
                    list.add(link);
                }
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return list;
        }
    }

    @Override
    public List<Word> getWords() {
        String query = "SELECT * FROM words";
        return getWords(query);
    }

    @Override
    public List<Word> getWords(int websiteId) {
        String query = "SELECT * FROM words WHERE id = (SELECT 1 FROM words_websites WHERE website_id='"+websiteId+"')";
        return getWords(query);
    }

    @Override
    public Word getWord(int wordId) {
        Word word = null;
        String query = "SELECT * FROM words WHERE id = '" + wordId + "'";
        try (Connection connection = getConnection()) {
            try (Statement statement = connection.createStatement()) {
                ResultSet set = statement.executeQuery(query);
                set.next();
                String wordStr = set.getString(2);
                word = new Word(wordId, wordStr);
                return word;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return word;
        }
    }

    private void parseProperties() {
        Properties properties = new Properties();

        try (InputStream in = Files.newInputStream(Paths.get("src/main/java/database/properties/database.properties"))) {
            properties.load(in);

            url = properties.getProperty("url");
            username = properties.getProperty("username");
            password = properties.getProperty("password");
        } catch (IOException e) {
            e.printStackTrace();
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
        return DriverManager.getConnection(url, username, password);
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
                ResultSet set = statement.executeQuery(query);
                set.next();
                return set.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private List<Word> getWords(String query) {
        List<Word> list = new ArrayList<>();
        try (Connection connection = getConnection()) {
            try (Statement statement = connection.createStatement()) {
                ResultSet set = statement.executeQuery(query);
                while (set.next()) {
                    int wordId = set.getInt(1);
                    String word = set.getString(2);
                    list.add(new Word(wordId, word));
                }
                return list;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return list;
        }
    }

    private List<Website> getWebsitesByQuery(String query) {
        List<Website> list = new ArrayList<>();
        try (Connection connection = getConnection()) {
            try (Statement statement = connection.createStatement()) {
                ResultSet set = statement.executeQuery(query);
                while (set.next()) {
                    int companyId = set.getInt(2);
                    String website = set.getString(3);
                    list.add(new Website(companyId, website));
                }
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return list;
        }
    }
}