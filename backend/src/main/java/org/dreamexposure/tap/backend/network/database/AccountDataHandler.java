package org.dreamexposure.tap.backend.network.database;

import org.dreamexposure.novautils.database.DatabaseInfo;
import org.dreamexposure.tap.core.objects.account.Account;
import org.dreamexposure.tap.core.utils.Logger;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Connection;
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
public class AccountDataHandler {
    private static AccountDataHandler instance;

    private DatabaseInfo masterInfo;
    private DatabaseInfo slaveInfo;

    private AccountDataHandler() {
    }

    public static AccountDataHandler get() {
        if (instance == null)
            instance = new AccountDataHandler();

        return instance;
    }

    void init(DatabaseInfo _master, DatabaseInfo _slave) {
        masterInfo = _master;
        slaveInfo = _slave;
    }

    public void createAccount(String username, String email, String hash, String birthday) {
        try (final Connection connection = masterInfo.getSource().getConnection()) {
            String tableName = String.format("%saccounts", masterInfo.getSettings().getPrefix());
            String query = "INSERT INTO " + tableName + " (id, username, email, hash, phone_number, birthday, safe_search, verified, email_confirmed, admin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, UUID.randomUUID().toString());
            statement.setString(2, username);
            statement.setString(3, email);
            statement.setString(4, hash);
            statement.setString(5, "000.000.0000");
            statement.setString(6, birthday);
            statement.setBoolean(7, false);
            statement.setBoolean(8, false);
            statement.setBoolean(9, false);
            statement.setBoolean(10, false);

            statement.execute();
            statement.close();
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to register new user", e, true, this.getClass());
        }
    }

    public Account getAccountFromUsername(String username) {
        try (final Connection connection = slaveInfo.getSource().getConnection()) {
            String tableName = String.format("%saccounts", slaveInfo.getSettings().getPrefix());
            String query = "SELECT * FROM " + tableName + " WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);

            ResultSet res = statement.executeQuery();

            boolean hasStuff = res.next();

            if (hasStuff) {
                Account a = new Account();
                a.setAccountId(UUID.fromString(res.getString("id")));
                a.setUsername(username);
                a.setEmail(res.getString("email"));
                a.setPhoneNumber(res.getString("phone_number"));
                a.setBirthday(res.getString("birthday"));
                a.setSafeSearch(res.getBoolean("safe_search"));
                a.setVerified(res.getBoolean("verified"));
                a.setEmailConfirmed(res.getBoolean("email_confirmed"));

                statement.close();
                return a;
            }
            statement.close();
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get user from database by username", e, true, this.getClass());
        }
        return null;
    }

    public Account getAccountFromEmail(String email) {
        try (final Connection connection = slaveInfo.getSource().getConnection()) {
            String tableName = String.format("%saccounts", slaveInfo.getSettings().getPrefix());
            String query = "SELECT * FROM " + tableName + " WHERE email = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);

            ResultSet res = statement.executeQuery();

            boolean hasStuff = res.next();

            if (hasStuff) {
                Account a = new Account();
                a.setAccountId(UUID.fromString(res.getString("id")));
                a.setUsername(res.getString("username"));
                a.setEmail(email);
                a.setPhoneNumber(res.getString("phone_number"));
                a.setBirthday(res.getString("birthday"));
                a.setSafeSearch(res.getBoolean("safe_search"));
                a.setVerified(res.getBoolean("verified"));
                a.setEmailConfirmed(res.getBoolean("email_confirmed"));

                statement.close();
                return a;
            }
            statement.close();
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get user from database by email", e, true, this.getClass());
        }
        return null;
    }

    public Account getAccountFromId(UUID id) {
        try (final Connection connection = slaveInfo.getSource().getConnection()) {
            String tableName = String.format("%saccounts", slaveInfo.getSettings().getPrefix());
            String query = "SELECT * FROM " + tableName + " WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, id.toString());

            ResultSet res = statement.executeQuery();

            boolean hasStuff = res.next();

            if (hasStuff) {
                Account a = new Account();
                a.setAccountId(id);
                a.setUsername(res.getString("username"));
                a.setEmail(res.getString("email"));
                a.setPhoneNumber(res.getString("phone_number"));
                a.setBirthday(res.getString("birthday"));
                a.setSafeSearch(res.getBoolean("safe_search"));
                a.setVerified(res.getBoolean("verified"));
                a.setEmailConfirmed(res.getBoolean("email_confirmed"));

                statement.close();
                return a;
            }
            statement.close();
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get user from database by id", e, true, this.getClass());
        }
        return null;
    }

    public boolean validLogin(String email, String password) {
        try (final Connection connection = slaveInfo.getSource().getConnection()) {
            String tableName = String.format("%saccounts", slaveInfo.getSettings().getPrefix());
            String query = "SELECT * FROM " + tableName + " WHERE email = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);

            ResultSet res = statement.executeQuery();

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if (res.next() && encoder.matches(password, res.getString("hash"))) {
                statement.close();
                return true;
            }
            statement.close();
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to validate login", e, true, this.getClass());
        }
        return false;
    }

    public boolean usernameOrEmailTaken(String username, String email) {
        try (final Connection connection = slaveInfo.getSource().getConnection()) {
            String tableName = String.format("%saccounts", slaveInfo.getSettings().getPrefix());

            //Try email first....
            String query = "SELECT * FROM " + tableName + " WHERE email = ? OR username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            statement.setString(2, username);

            ResultSet res = statement.executeQuery();

            boolean hasStuff = res.next();

            if (hasStuff) {
                statement.close();
                return true;
            }
            statement.close();
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to verify username/email taken", e, true, this.getClass());
        }
        return false;
    }

    public boolean emailTaken(String email) {
        try (final Connection connection = slaveInfo.getSource().getConnection()) {
            String tableName = String.format("%saccounts", slaveInfo.getSettings().getPrefix());

            //Try email first....
            String query = "SELECT * FROM " + tableName + " WHERE email = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);

            ResultSet res = statement.executeQuery();

            boolean hasStuff = res.next();

            if (hasStuff) {
                statement.close();
                return true;
            }
            statement.close();
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to verify email taken", e, true, this.getClass());
        }
        return false;
    }

    public boolean updateAccount(Account account) {
        try (Connection masterConnection = masterInfo.getSource().getConnection()) {
            String tableName = String.format("%saccounts", slaveInfo.getSettings().getPrefix());

            String query = "SELECT * FROM " + tableName + " WHERE id = ?";
            Connection slaveConnection = slaveInfo.getSource().getConnection();
            PreparedStatement statement = slaveConnection.prepareStatement(query);
            statement.setString(1, account.getAccountId().toString());

            ResultSet res = statement.executeQuery();

            boolean hasStuff = res.next();

            if (!hasStuff || res.getString("id") == null) {
                //Data not present. this should not be possible.
                slaveConnection.close();
                statement.close();

                return false;
            } else {
                //Data present, update.
                String update = "UPDATE " + tableName
                        + " SET username = ?, email = ?, phone_number = ?, birthday = ?, " +
                        " safe_search = ?, verified = ?, email_confirmed = ?, admin = ?" +
                        " WHERE id = ?";
                PreparedStatement ps = masterConnection.prepareStatement(update);

                ps.setString(1, account.getUsername());
                ps.setString(2, account.getEmail());
                //Skip hash
                ps.setString(3, account.getPhoneNumber());
                ps.setString(4, account.getBirthday());
                ps.setBoolean(5, account.isSafeSearch());
                ps.setBoolean(6, account.isVerified());
                ps.setBoolean(7, account.isEmailConfirmed());
                ps.setBoolean(8, account.isAdmin());

                ps.setString(9, account.getAccountId().toString());

                ps.executeUpdate();

                ps.close();
                statement.close();
                slaveConnection.close();
            }
            return true;
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to update Account Info", e, true, this.getClass());
        }
        return false;
    }

    public boolean updateAccountHash(Account account, String hash) {
        try (final Connection masterConnection = masterInfo.getSource().getConnection()) {
            String tableName = String.format("%saccounts", masterInfo.getSettings().getPrefix());

            String query = "SELECT * FROM " + tableName + " WHERE id = ?";
            Connection slaveConnection = slaveInfo.getSource().getConnection();
            PreparedStatement statement = slaveConnection.prepareStatement(query);
            statement.setString(1, account.getAccountId().toString());

            ResultSet res = statement.executeQuery();

            boolean hasStuff = res.next();

            if (!hasStuff || res.getString("id") == null) {
                //Data not present. this should not be possible.
                statement.close();
                slaveConnection.close();
                return false;
            } else {
                //Data present, update.
                String update = "UPDATE " + tableName + " SET hash = ? WHERE id = ?";
                PreparedStatement ps = masterConnection.prepareStatement(update);

                ps.setString(1, hash);
                ps.setString(2, account.getAccountId().toString());

                ps.executeUpdate();

                ps.close();
                statement.close();
                slaveConnection.close();
            }
            return true;
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to update Account Info", e, true, this.getClass());
        }
        return false;
    }
}
