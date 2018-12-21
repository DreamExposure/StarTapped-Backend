package org.dreamexposure.tap.backend.network.database;

import org.dreamexposure.novautils.database.DatabaseInfo;
import org.dreamexposure.tap.core.objects.account.Account;
import org.dreamexposure.tap.core.objects.blog.IBlog;
import org.dreamexposure.tap.core.utils.Logger;

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
@SuppressWarnings("Duplicates")
public class FollowerDataHandler {
    private static FollowerDataHandler instance;

    private DatabaseInfo databaseInfo;

    private FollowerDataHandler() {
    }

    public static FollowerDataHandler get() {
        if (instance == null) instance = new FollowerDataHandler();

        return instance;
    }

    public void init(DatabaseInfo _info) {
        databaseInfo = _info;
    }

    public void follow(UUID user, UUID following) {
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%sfollow", databaseInfo.getSettings().getPrefix());

                String query = "SELECT * FROM " + tableName + " WHERE user_id = ? AND following_id = ?;";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                statement.setString(1, user.toString());
                statement.setString(2, following.toString());

                ResultSet res = statement.executeQuery();

                boolean hasStuff = res.next();

                if (!hasStuff || res.getInt("id") <= 0) {
                    //Data not present. ADD
                    String put = "INSERT into " + tableName + "(user_id, following_id) VALUES (?, ?)";
                    PreparedStatement ps = databaseInfo.getConnection().prepareStatement(put);

                    ps.setString(1, user.toString());
                    ps.setString(2, following.toString());

                    ps.execute();
                    ps.close();
                }
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to add follower", e, this.getClass());
        }
    }

    public void unfollow(UUID user, UUID following) {
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%sfollow", databaseInfo.getSettings().getPrefix());

                String query = "SELECT * FROM " + tableName + " WHERE user_id = ? AND following_id = ?;";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                statement.setString(1, user.toString());
                statement.setString(2, following.toString());
                ResultSet res = statement.executeQuery();

                boolean hasStuff = res.next();

                if (hasStuff && res.getInt("id") > 0) {
                    String put = "DELETE FROM " + tableName + " WHERE id = ?";
                    PreparedStatement ps = databaseInfo.getConnection().prepareStatement(put);

                    ps.setInt(1, res.getInt("id"));

                    ps.execute();
                    ps.close();
                }
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to remove follower", e, this.getClass());
        }
    }

    public List<UUID> getFollowingIdList(UUID user) {
        List<UUID> following = new ArrayList<>();
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%sfollow", databaseInfo.getSettings().getPrefix());

                String query = "SELECT * FROM " + tableName + " WHERE user_id = ?;";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                statement.setString(1, user.toString());
                ResultSet res = statement.executeQuery();

                while (res.next()) {
                    following.add(UUID.fromString(res.getString("following_id")));
                }

                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get following", e, this.getClass());
        }

        return following;
    }

    public List<UUID> getFollowersIdList(UUID following) {
        List<UUID> followers = new ArrayList<>();
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%sfollow", databaseInfo.getSettings().getPrefix());

                String query = "SELECT * FROM " + tableName + " WHERE following_id = ?;";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                statement.setString(1, following.toString());
                ResultSet res = statement.executeQuery();

                while (res.next()) {
                    followers.add(UUID.fromString(res.getString("user_id")));
                }

                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get followers", e, this.getClass());
        }

        return followers;
    }

    public List<IBlog> getFollowingBlogList(UUID user) {
        List<IBlog> following = new ArrayList<>();
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%sfollow", databaseInfo.getSettings().getPrefix());

                String query = "SELECT * FROM " + tableName + " WHERE user_id = ?;";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                statement.setString(1, user.toString());
                ResultSet res = statement.executeQuery();

                while (res.next()) {
                    UUID fid = UUID.fromString(res.getString("following_id"));
                    IBlog fb = BlogDataHandler.get().getBlog(fid);
                    if (fb != null)
                        following.add(fb);
                }

                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get following", e, this.getClass());
        }

        return following;
    }

    public List<Account> getFollowersAccountList(UUID following) {
        List<Account> followers = new ArrayList<>();
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%sfollow", databaseInfo.getSettings().getPrefix());

                String query = "SELECT * FROM " + tableName + " WHERE following_id = ?;";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                statement.setString(1, following.toString());
                ResultSet res = statement.executeQuery();

                while (res.next()) {
                    UUID fid = UUID.fromString(res.getString("user_id"));
                    Account fa = AccountDataHandler.get().getAccountFromId(fid);
                    if (fa != null)
                        followers.add(fa);
                }

                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get followers", e, this.getClass());
        }

        return followers;
    }
}
