package database.utils;

import database.models.Website;
import database.models.Word;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public class DatabaseUtil {

    private DatabaseUtil() {
    }

    /**
     * The function calls the script, which first deletes the previous table and creates a new one with new parameters.
     *
     * @param connection - database connection
     * @param table - sql table path
     * @throws IOException  - if file doesn't exist
     * @throws SQLException - some error with sql database
     */
    public static void initDatabaseTable(Connection connection, String table) throws IOException, SQLException {
        ScriptRunner sr = new ScriptRunner(connection, false, false);
        Reader reader = new BufferedReader(new FileReader(table));
        sr.runScript(reader);
    }

    public static PreparedStatement getWebsitesPreparedStatement(Collection<Website> websites, Connection connection) throws SQLException {
        StringBuilder statement = new StringBuilder();
        statement.append("INSERT INTO websites (company_id, website) VALUES (?, ?)");

        statement.append(", (?, ?)".repeat(Math.max(0, websites.size() - 1)));

        PreparedStatement preparedStatement = connection.prepareStatement(statement.toString());

        int position = 1;
        for (Website element : websites) {
            preparedStatement.setInt(position, element.getCompanyId());
            preparedStatement.setString(position + 1, element.getLink().getAbsoluteURL());

            position += 2;
        }

        return preparedStatement;
    }

    public static PreparedStatement getWordsPreparedStatement(Collection<Word> words, Connection connection) throws SQLException {
        StringBuilder statement = new StringBuilder();
        statement.append("INSERT INTO words (website_id, word) VALUES (?, ?)");

        for (int i = 1; i < words.size(); i++) {
            statement.append(", (?, ?)");
        }

        PreparedStatement preparedStatement = connection.prepareStatement(statement.toString());

        int position = 1;
        for (Word element : words) {
            preparedStatement.setInt(position, element.getWebsiteId());
            preparedStatement.setString(position + 1, element.getWord());

            position += 2;
        }
        return preparedStatement;
    }
}
