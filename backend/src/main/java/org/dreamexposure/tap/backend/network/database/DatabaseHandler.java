package org.dreamexposure.tap.backend.network.database;

import org.dreamexposure.novautils.database.DatabaseInfo;
import org.dreamexposure.novautils.database.DatabaseManager;
import org.dreamexposure.novautils.database.DatabaseSettings;
import org.dreamexposure.novautils.database.MySQL;
import org.dreamexposure.tap.backend.conf.SiteSettings;
import org.dreamexposure.tap.core.utils.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author NovaFox161
 * Date Created: 12/4/18
 * For Project: TAP-Backend
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
@SuppressWarnings({"UnusedReturnValue", "SqlNoDataSourceInspection", "Duplicates"})
public class DatabaseHandler {
    private static DatabaseHandler instance;
    private DatabaseInfo databaseInfo;
    
    private DatabaseHandler() {
    } //Prevent initialization.

    /**
     * Gets the instance of the {@link DatabaseHandler}.
     *
     * @return The instance of the {@link DatabaseHandler}
     */
    public static DatabaseHandler getHandler() {
        if (instance == null) {
            instance = new DatabaseHandler();
        }
        return instance;
    }
    
    /**
     * Connects to the MySQL server specified.
     */
    public void connectToMySQL() {
        DatabaseSettings settings = new DatabaseSettings(SiteSettings.SQL_HOST.get(), SiteSettings.SQL_PORT.get(), SiteSettings.SQL_DB.get(), SiteSettings.SQL_USER.get(), SiteSettings.SQL_PASSWORD.get(), SiteSettings.SQL_PREFIX.get());
    
        try {
            MySQL mySQL = new MySQL(settings.getHostname(), settings.getPort(), settings.getDatabase(), settings.getPrefix(), settings.getUser(), settings.getPassword());
        
            Connection mySQLConnection = mySQL.openConnection();
        
            databaseInfo = new DatabaseInfo(mySQL, mySQLConnection, settings);
            System.out.println("Connected to MySQL database!");

            //Init our data handlers to make this file smaller
            AccountDataHandler.get().init(databaseInfo);
            ConfirmationDataHandler.get().init(databaseInfo);
            AuthorizationDataHandler.get().init(databaseInfo);
            BlogDataHandler.get().init(databaseInfo);
            RecordDataHandler.get().init(databaseInfo);
            FollowerDataHandler.get().init(databaseInfo);
            PostDataHandler.get().init(databaseInfo);
            DataCountHandling.get().init(databaseInfo);
            FileDataHandler.get().init(databaseInfo);
        } catch (Exception e) {
            System.out.println("Failed to connect to MySQL database! Is it properly configured?");
            e.printStackTrace();
            Logger.getLogger().exception("Failed to connect to MySQL Database!", e, this.getClass());
        }
    }
    
    /**
     * Disconnects from the MySQL server if still connected.
     */
    public void disconnectFromMySQL() {
        if (databaseInfo != null) {
            DatabaseManager.disconnectFromMySQL(databaseInfo);
        }
    }
    
    /**
     * Creates all required tables in the database if they do not exist.
     */
    public void createTables() {
        try {
            Statement statement = databaseInfo.getConnection().createStatement();
            
            String accountsTableName = String.format("%saccounts", databaseInfo.getSettings().getPrefix());
            String confirmationTableName = String.format("%sconfirmation", databaseInfo.getSettings().getPrefix());
            String blogTableName = String.format("%sblog", databaseInfo.getSettings().getPrefix());
            String postTableName = String.format("%spost", databaseInfo.getSettings().getPrefix());
            String authTableName = String.format("%sauth", databaseInfo.getSettings().getPrefix());
            String recordTableName = String.format("%srecord", databaseInfo.getSettings().getPrefix());
            String followTableName = String.format("%sfollow", databaseInfo.getSettings().getPrefix());
            String fileTableName = String.format("%sfile", databaseInfo.getSettings().getPrefix());
            
            String createAccountsTable = "CREATE TABLE IF NOT EXISTS " + accountsTableName +
                    "(id VARCHAR(255) not NULL, " +
                    " username VARCHAR(255) not NULL, " +
                    " email LONGTEXT not NULL, " +
                    " hash LONGTEXT not NULL, " +
                    " phone_number VARCHAR(255) not NULL, " +
                    " birthday VARCHAR(255) not NULL, " +
                    " safe_search BOOLEAN not NULL, " +
                    " verified BOOLEAN not NULL, " +
                    " email_confirmed BOOLEAN not NULL, " +
                    " admin BOOLEAN not NULL, " +
                    " PRIMARY KEY (id))";
            String createConfirmationTable = "CREATE TABLE IF NOT EXISTS " + confirmationTableName +
                    "(id VARCHAR(255) not NULL, " +
                    " code VARCHAR(32) not NULL, " +
                    "PRIMARY KEY (id))";
            String createBlogTable = "CREATE TABLE IF NOT EXISTS " + blogTableName +
                    "(id VARCHAR(255) not NULL, " +
                    " base_url LONGTEXT not NULL, " +
                    " complete_url LONGTEXT not NULL, " +
                    " blog_type VARCHAR(255) not NULL, " +
                    " name LONGTEXT not NULL, " +
                    " description LONGTEXT not NULL, " +
                    " icon_url LONGTEXT not NULL, " +
                    " background_color VARCHAR(255) not NULL, " +
                    " background_url LONGTEXT not NULL, " +
                    " allow_under_18 BOOLEAN not NULL, " +
                    " nsfw BOOLEAN not NULL, " +
                    " show_age BOOLEAN NULL, " +
                    " owners LONGTEXT NULL, " +
                    " owner VARCHAR(255) null, " +
                    " PRIMARY KEY (id))";
            String createPostTable = "CREATE TABLE IF NOT EXISTS " + postTableName +
                    "(id VARCHAR(255) not NULL, " +
                    " creator_id VARCHAR(255) not NULL, " +
                    " origin_blog_id VARCHAR(255) not NULL, " +
                    " permalink LONGTEXT not NULL, " +
                    " full_url LONGTEXT not NULL, " +
                    " post_type VARCHAR(255) not NULL, " +
                    " timestamp LONG not NULL, " +
                    " title LONGTEXT not NULL, " +
                    " body LONGTEXT not NULL, " +
                    " nsfw BOOLEAN not NULL, " +
                    " parent VARCHAR(255) NULL, " +
                    " image_url LONGTEXT NULL, " +
                    " audio_url LONGTEXT NULL, " +
                    " video_url LONGTEXT NULL, " +
                    " PRIMARY KEY (id))";
            String createAuthTable = "CREATE TABLE IF NOT EXISTS " + authTableName +
                    "(id VARCHAR(255) NOT NULL, " +
                    " refresh_token VARCHAR(64) NOT NULL, " +
                    " access_token VARCHAR(64) NOT NULL, " +
                    " expire LONG NOT NULL, " +
                    " PRIMARY KEY (refresh_token))";
            String createRecordTable = "CREATE TABLE IF NOT EXISTS " + recordTableName +
                    "(blog_id VARCHAR(255) NOT NULL, " +
                    " record_id LONGTEXT NOT NULL, " +
                    " PRIMARY KEY (blog_id))";
            String createFollowTable = "CREATE TABLE IF NOT EXISTS " + followTableName +
                    "(id int auto_increment not null, " +
                    "user_id VARCHAR(255) not null," +
                    "following_id varchar(255) not null," +
                    "PRIMARY KEY (id))";
            String createFileTable = "CREATE TABLE IF NOT EXISTS " + fileTableName +
                    "(hash VARCHAR(255) not NULL, " +
                    " uploader_id VARCHAR(255) not NULL, " +
                    " name LONGTEXT not NULL, " +
                    " url LONGTEXT not NULL, " +
                    " path LONGTEXT not NULL, " +
                    " timestamp LONG not NULL, " +
                    " PRIMARY KEY (hash))";
            
            statement.execute(createAccountsTable);
            statement.execute(createConfirmationTable);
            statement.execute(createBlogTable);
            statement.execute(createPostTable);
            statement.execute(createAuthTable);
            statement.execute(createRecordTable);
            statement.execute(createFollowTable);
            statement.execute(createFileTable);
            
            statement.close();
            System.out.println("Successfully created needed tables in MySQL database!");
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to create database tables", e, this.getClass());
        }
    }
}
