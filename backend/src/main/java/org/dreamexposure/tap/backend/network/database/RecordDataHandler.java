package org.dreamexposure.tap.backend.network.database;

import org.dreamexposure.novautils.database.DatabaseInfo;
import org.dreamexposure.tap.core.objects.cloudflare.DnsRecord;
import org.dreamexposure.tap.core.utils.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * @author NovaFox161
 * Date Created: 12/20/2018
 * For Project: TAP-Backend
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
@SuppressWarnings({"UnusedReturnValue", "SqlNoDataSourceInspection", "Duplicates"})
public class RecordDataHandler {
    private static RecordDataHandler instance;

    private DatabaseInfo databaseInfo;

    private RecordDataHandler() {
    }

    public static RecordDataHandler get() {
        if (instance == null) instance = new RecordDataHandler();

        return instance;
    }

    void init(DatabaseInfo _info) {
        databaseInfo = _info;
    }

    public boolean createRecord(DnsRecord record) {
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%srecord", databaseInfo.getMySQL().getPrefix());
                String query = "INSERT INTO " + tableName + " (blog_id, record_id) VALUES (?, ?)";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);

                statement.setString(1, record.getBlogId().toString());
                statement.setString(2, record.getRecordId());

                statement.execute();
                statement.close();
                return true;
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to add dns record data.", e, this.getClass());
        }
        return false;
    }

    public DnsRecord getRecord(UUID blogId) {
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%srecord", databaseInfo.getSettings().getPrefix());
                String query = "SELECT * FROM " + tableName + " WHERE blog_id = ?";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                statement.setString(1, blogId.toString());

                ResultSet res = statement.executeQuery();

                boolean hasStuff = res.next();

                if (hasStuff) {
                    DnsRecord record = new DnsRecord();
                    record.setBlogId(UUID.fromString(res.getString("blog_id")));
                    record.setRecordId(res.getString("record_id"));

                    statement.close();
                    return record;
                }
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get dns record data from database by blog id", e, this.getClass());
        }
        return null;
    }

    public boolean deleteRecord(UUID blogId) {
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%srecord", databaseInfo.getSettings().getPrefix());
                String query = "DELETE FROM " + tableName + " WHERE blog_id = ?";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                statement.setString(1, blogId.toString());

                statement.execute();
                statement.close();
                return true;
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to delete dns record data by blog ID", e, this.getClass());
        }
        return false;
    }

    public boolean deleteRecord(String recordId) {
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%srecord", databaseInfo.getSettings().getPrefix());
                String query = "DELETE FROM " + tableName + " WHERE record_id = '" + recordId + "';";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);

                statement.execute();
                statement.close();
                return true;
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to delete dns record data by record ID", e, this.getClass());
        }
        return false;
    }
}
