package org.dreamexposure.tap.backend.network.database;

import org.dreamexposure.novautils.database.DatabaseInfo;
import org.dreamexposure.novautils.database.DatabaseManager;
import org.dreamexposure.novautils.database.DatabaseSettings;
import org.dreamexposure.tap.core.conf.SiteSettings;
import org.dreamexposure.tap.core.utils.Logger;
import org.flywaydb.core.Flyway;

import java.util.HashMap;
import java.util.Map;

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

    public void handleMigrations() {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("prefix", SiteSettings.SQL_PREFIX.get());

        try {
            Flyway flyway = Flyway.configure()
                    .dataSource(masterInfo.getSource())
                    .cleanDisabled(true)
                    .baselineOnMigrate(true)
                    .table(SiteSettings.SQL_PREFIX.get() + "schema_history")
                    .placeholders(placeholders)
                    .load();
            int sm = flyway.migrate();
            Logger.getLogger().debug("Migrations Successful, " + sm + " migrations applied!", true);
        } catch (Exception e) {
            Logger.getLogger().exception("Migrations Failure", e, true, getClass());
            System.exit(2);
        }
    }
}
