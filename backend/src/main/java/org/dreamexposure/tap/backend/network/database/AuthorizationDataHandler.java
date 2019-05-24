package org.dreamexposure.tap.backend.network.database;

import org.dreamexposure.novautils.database.DatabaseInfo;
import org.dreamexposure.tap.core.objects.auth.AccountAuthentication;
import org.dreamexposure.tap.core.utils.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
public class AuthorizationDataHandler {
    private static AuthorizationDataHandler instance;

    private DatabaseInfo masterInfo;
    private DatabaseInfo slaveInfo;

    private AuthorizationDataHandler() {
    }

    public static AuthorizationDataHandler get() {
        if (instance == null) instance = new AuthorizationDataHandler();

        return instance;
    }

    void init(DatabaseInfo _master, DatabaseInfo _slave) {
        masterInfo = _master;
        slaveInfo = _slave;
    }

    public void saveAuth(AccountAuthentication auth) {
        try (final Connection connection = masterInfo.getSource().getConnection()) {
            String tableName = String.format("%sauth", masterInfo.getSettings().getPrefix());
            String query = "INSERT INTO " + tableName + " (id, refresh_token, access_token, expire) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, auth.getAccountId().toString());
            statement.setString(2, auth.getRefreshToken());
            statement.setString(3, auth.getAccessToken());
            statement.setLong(4, auth.getExpire());

            statement.execute();
            statement.close();
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to add Authentication data.", e, true, this.getClass());
        }
    }

    public void updateAuth(AccountAuthentication auth) {
        try (final Connection masterConnection = masterInfo.getSource().getConnection()) {
            String tableName = String.format("%sauth", slaveInfo.getSettings().getPrefix());
            String query = "SELECT * FROM " + tableName + " WHERE refresh_token = ?";
            Connection slaveConnection = slaveInfo.getSource().getConnection();
            PreparedStatement statement = slaveConnection.prepareStatement(query);
            statement.setString(1, auth.getRefreshToken());

            ResultSet res = statement.executeQuery();

            boolean hasStuff = res.next();

            if (hasStuff && res.getString("refresh_token") != null) {
                //Has stuff, lets update
                String update = "UPDATE " + tableName
                        + " SET access_token = ?, expire = ? WHERE refresh_token = ?";
                PreparedStatement ps = masterConnection.prepareStatement(update);

                ps.setString(1, auth.getAccessToken());
                ps.setLong(2, auth.getExpire());
                ps.setString(3, auth.getRefreshToken());

                ps.executeUpdate();
                ps.close();
            }
            statement.close();
            slaveConnection.close();
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to update auth data", e, true, this.getClass());
        }
    }

    public AccountAuthentication getAuthFromAccessToken(String accessToken) {
        try (final Connection connection = slaveInfo.getSource().getConnection()) {
            String tableName = String.format("%sauth", slaveInfo.getSettings().getPrefix());
            String query = "SELECT * FROM " + tableName + " WHERE access_token = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, accessToken);

            ResultSet res = statement.executeQuery();

            boolean hasStuff = res.next();

            if (hasStuff) {
                AccountAuthentication auth = new AccountAuthentication();
                auth.setAccountId(UUID.fromString(res.getString("id")));
                auth.setRefreshToken(res.getString("refresh_token"));
                auth.setAccessToken(res.getString("access_token"));
                auth.setExpire(res.getLong("expire"));

                statement.close();
                return auth;
            }
            statement.close();
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get auth data from database by access token", e, true, this.getClass());
        }
        return null;
    }

    public AccountAuthentication getAuthFromRefreshToken(String refreshToken) {
        try (final Connection connection = slaveInfo.getSource().getConnection()) {
            String tableName = String.format("%sauth", slaveInfo.getSettings().getPrefix());
            String query = "SELECT * FROM " + tableName + " WHERE refresh_token = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, refreshToken);

            ResultSet res = statement.executeQuery();

            boolean hasStuff = res.next();

            if (hasStuff) {
                AccountAuthentication auth = new AccountAuthentication();
                auth.setAccountId(UUID.fromString(res.getString("id")));
                auth.setRefreshToken(res.getString("refresh_token"));
                auth.setAccessToken(res.getString("access_token"));
                auth.setExpire(res.getLong("expire"));

                statement.close();
                return auth;
            }
            statement.close();
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get auth data from database by refresh token", e, true, this.getClass());
        }
        return null;
    }

    public List<AccountAuthentication> getAllAuth(UUID accountId) {
        List<AccountAuthentication> all = new ArrayList<>();

        try (final Connection connection = slaveInfo.getSource().getConnection()) {
            String tableName = String.format("%sauth", slaveInfo.getSettings().getPrefix());
            String query = "SELECT * FROM " + tableName + " WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, accountId.toString());

            ResultSet res = statement.executeQuery();

            while (res.next()) {
                if (res.getString("id") != null) {
                    AccountAuthentication auth = new AccountAuthentication();
                    auth.setAccountId(accountId);
                    auth.setRefreshToken(res.getString("refresh_token"));
                    auth.setAccessToken(res.getString("access_token"));
                    auth.setExpire(res.getLong("expire"));

                    all.add(auth);
                }
            }
            statement.close();
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get auth data from database by refresh token", e, true, this.getClass());
        }

        return all;
    }

    public void removeAuth(UUID accountId) {
        try (final Connection connection = masterInfo.getSource().getConnection()) {
            String tableName = String.format("%sauth", masterInfo.getSettings().getPrefix());
            String query = "DELETE FROM " + tableName + " WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, accountId.toString());

            statement.execute();
            statement.close();
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to delete auth data.", e, true, this.getClass());
        }
    }

    public void removeAuthByAccessToken(String accessToken) {
        try (final Connection connection = masterInfo.getSource().getConnection()) {
            String tableName = String.format("%sauth", masterInfo.getSettings().getPrefix());
            String query = "DELETE FROM " + tableName + " WHERE access_token = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, accessToken);

            statement.execute();
            statement.close();
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to delete auth data by access token.", e, true, this.getClass());
        }
    }

    public void removeAuthByRefreshToken(String refreshToken) {
        try (final Connection connection = masterInfo.getSource().getConnection()) {
            String tableName = String.format("%sauth", masterInfo.getSettings().getPrefix());
            String query = "DELETE FROM " + tableName + " WHERE refresh_token = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, refreshToken);

            statement.execute();
            statement.close();
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to delete auth data by refresh token.", e, true, this.getClass());
        }
    }
}
