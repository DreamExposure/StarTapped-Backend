package org.dreamexposure.tap.backend.network.database;

import org.dreamexposure.novautils.database.DatabaseInfo;
import org.dreamexposure.tap.core.objects.account.Account;
import org.dreamexposure.tap.core.objects.confirmation.EmailConfirmation;
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
public class ConfirmationDataHandler {
    private static ConfirmationDataHandler instance;

    private DatabaseInfo databaseInfo;

    private ConfirmationDataHandler() {
    }

    public static ConfirmationDataHandler get() {
        if (instance == null) instance = new ConfirmationDataHandler();

        return instance;
    }

    public void init(DatabaseInfo _info) {
        databaseInfo = _info;
    }

    public void addPendingConfirmation(Account account, String code) {
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%sconfirmation", databaseInfo.getSettings().getPrefix());
                String query = "INSERT INTO " + tableName + " (id, code) VALUES (?, ?)";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);

                statement.setString(1, account.getAccountId().toString());
                statement.setString(2, code);

                statement.execute();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to input confirmation code", e, this.getClass());
        }
    }

    public EmailConfirmation getConfirmationInfo(String code) {
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%sconfirmation", databaseInfo.getSettings().getPrefix());
                String query = "SELECT * FROM " + tableName + " WHERE code = '" + code + "';";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                ResultSet res = statement.executeQuery();

                boolean hasStuff = res.next();

                if (hasStuff && res.getString("code") != null) {
                    EmailConfirmation con = new EmailConfirmation();
                    con.setUserId(UUID.fromString(res.getString("id")));
                    con.setCode(code);

                    statement.close();

                    return con;
                } else {
                    //Data not present.
                    statement.close();
                    return null;
                }
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get confirmation data", e, this.getClass());
        }
        return null;
    }

    public void removeConfirmationInfo(String code) {
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%sconfirmation", databaseInfo.getSettings().getPrefix());
                String query = "DELETE FROM " + tableName + " WHERE code = '" + code + "';";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);

                statement.execute();
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to delete confirmation data", e, this.getClass());
        }
    }
}
