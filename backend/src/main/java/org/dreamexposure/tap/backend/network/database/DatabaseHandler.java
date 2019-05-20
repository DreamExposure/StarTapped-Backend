package org.dreamexposure.tap.backend.network.database;

import org.dreamexposure.novautils.database.DatabaseInfo;
import org.dreamexposure.novautils.database.DatabaseManager;
import org.dreamexposure.novautils.database.DatabaseSettings;
import org.dreamexposure.tap.core.conf.SiteSettings;
import org.dreamexposure.tap.core.utils.Logger;

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
    private DatabaseInfo masterInfo;
    private DatabaseInfo slaveInfo;

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
        //Change these to proper settings for each
        DatabaseSettings masterSettings = new DatabaseSettings(SiteSettings.MASTER_SQL_HOST.get(), SiteSettings.MASTER_SQL_PORT.get(), SiteSettings.SQL_DB.get(), SiteSettings.MASTER_SQL_USER.get(), SiteSettings.MASTER_SQL_PASS.get(), SiteSettings.SQL_PREFIX.get());

        DatabaseSettings slaveSettings = new DatabaseSettings(SiteSettings.SLAVE_SQL_HOST.get(), SiteSettings.SLAVE_SQL_PORT.get(), SiteSettings.SQL_DB.get(), SiteSettings.SLAVE_SQL_USER.get(), SiteSettings.SLAVE_SQL_PASSWORD.get(), SiteSettings.SQL_PREFIX.get());


        //Check if SSH is needed for master, and if so, use it.
        if (SiteSettings.MASTER_SQL_USE_SSH.get().equalsIgnoreCase("true")) {
            masterSettings.withSSH(SiteSettings.SSH_HOST.get(), Integer.valueOf(SiteSettings.SSH_PORT.get()), SiteSettings.SSH_USER.get(), null, SiteSettings.SSH_KEY_FILE.get());
        }

        try {
            //Connect for them...
            masterInfo = DatabaseManager.connectToMySQL(masterSettings);
            slaveInfo = DatabaseManager.connectToMySQL(slaveSettings);

            System.out.println("Connected to MySQL database!");

            //Init our data handlers to make this file smaller
            AccountDataHandler.get().init(masterInfo, slaveInfo);
            ConfirmationDataHandler.get().init(masterInfo, slaveInfo);
            AuthorizationDataHandler.get().init(masterInfo, slaveInfo);
            BlogDataHandler.get().init(masterInfo, slaveInfo);
            RecordDataHandler.get().init(masterInfo, slaveInfo);
            FollowerDataHandler.get().init(masterInfo, slaveInfo);
            PostDataHandler.get().init(masterInfo, slaveInfo);
            DataCountHandling.get().init(masterInfo, slaveInfo);
            FileDataHandler.get().init(masterInfo, slaveInfo);
            BookmarkDataHandler.get().init(masterInfo, slaveInfo);
        } catch (Exception e) {
            System.out.println("Failed to connect to MySQL database! Is it properly configured?");
            e.printStackTrace();
            Logger.getLogger().exception("Failed to connect to MySQL Database!", e, true, this.getClass());
        }
    }
    
    /**
     * Disconnects from the MySQL server if still connected.
     */
    public void disconnectFromMySQL() {
        if (masterInfo != null)
            DatabaseManager.disconnectFromMySQL(masterInfo);
        if (slaveInfo != null)
            DatabaseManager.disconnectFromMySQL(slaveInfo);
    }
    
    /**
     * Creates all required tables in the database if they do not exist.
     */
    public void createTables() {
        try {
            Statement statement = masterInfo.getSource().getConnection().createStatement();

            String accountsTableName = String.format("%saccounts", masterInfo.getSettings().getPrefix());
            String confirmationTableName = String.format("%sconfirmation", masterInfo.getSettings().getPrefix());
            String blogTableName = String.format("%sblog", masterInfo.getSettings().getPrefix());
            String postTableName = String.format("%spost", masterInfo.getSettings().getPrefix());
            String authTableName = String.format("%sauth", masterInfo.getSettings().getPrefix());
            String recordTableName = String.format("%srecord", masterInfo.getSettings().getPrefix());
            String followTableName = String.format("%sfollow", masterInfo.getSettings().getPrefix());
            String fileTableName = String.format("%sfile", masterInfo.getSettings().getPrefix());
            String bookmarkTableName = String.format("%sbookmark", masterInfo.getSettings().getPrefix());
            
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
                    " tags LONGTEXT not NULL, " +
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
            String createBookmarkTable = "CREATE TABLE IF NOT EXISTS " + bookmarkTableName +
                    "(id int auto_increment NOT NULL, " +
                    " user_id VARCHAR(255) NOT NULL, " +
                    " post_id VARCHAR(255) NOT NULL, " +
                    " timestamp LONG NOT NULL," +
                    " PRIMARY KEY (id))";
            
            statement.execute(createAccountsTable);
            statement.execute(createConfirmationTable);
            statement.execute(createBlogTable);
            statement.execute(createPostTable);
            statement.execute(createAuthTable);
            statement.execute(createRecordTable);
            statement.execute(createFollowTable);
            statement.execute(createFileTable);
            statement.execute(createBookmarkTable);
            
            statement.close();
            System.out.println("Successfully created needed tables in MySQL database!");
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to create database tables", e, true, this.getClass());
        }
    }
}
