package org.dreamexposure.tap.backend.network.database;

import org.dreamexposure.novautils.database.DatabaseInfo;
import org.dreamexposure.tap.core.enums.blog.BlogType;
import org.dreamexposure.tap.core.enums.post.PostType;
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
public class DataCountHandling {
    private static DataCountHandling instance;

    private DatabaseInfo databaseInfo;

    private DataCountHandling() {
    }

    public static DataCountHandling get() {
        if (instance == null) instance = new DataCountHandling();

        return instance;
    }

    void init(DatabaseInfo _info) {
        databaseInfo = _info;
    }

    public int getAccountCount() {
        int amount = -1;
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%saccounts", databaseInfo.getSettings().getPrefix());

                String query = "SELECT COUNT(*) FROM " + tableName + ";";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                ResultSet res = statement.executeQuery();

                if (res.next())
                    amount = res.getInt(1);
                else
                    amount = 0;


                res.close();
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get account count", e, this.getClass());
        }
        return amount;
    }

    public int getAuthCount(UUID accountId) {
        int amount = -1;
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%sauth", databaseInfo.getSettings().getPrefix());

                String query = "SELECT COUNT(*) FROM " + tableName + " WHERE id = " + accountId.toString() + ";";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                ResultSet res = statement.executeQuery();

                if (res.next())
                    amount = res.getInt(1);
                else
                    amount = 0;


                res.close();
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get auth count for account.", e, this.getClass());
        }
        return amount;
    }

    public int getBlogCount() {
        int amount = -1;
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%sblog", databaseInfo.getSettings().getPrefix());

                String query = "SELECT COUNT(*) FROM " + tableName + ";";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                ResultSet res = statement.executeQuery();

                if (res.next())
                    amount = res.getInt(1);
                else
                    amount = 0;


                res.close();
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get blog count", e, this.getClass());
        }
        return amount;
    }

    public int getBlogCount(BlogType type) {
        int amount = -1;
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%sblog", databaseInfo.getSettings().getPrefix());

                String query = "SELECT COUNT(*) FROM " + tableName + " WHERE blog_type = " + type.name() + ";";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                ResultSet res = statement.executeQuery();

                if (res.next())
                    amount = res.getInt(1);
                else
                    amount = 0;


                res.close();
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get blog count", e, this.getClass());
        }
        return amount;
    }

    public int getBlogCount(UUID accountId) {
        int amount = -1;
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%sblog", databaseInfo.getSettings().getPrefix());

                //Include personal AND group blogs
                String query = "SELECT COUNT(*) FROM " + tableName + " WHERE owner = " + accountId.toString() + " OR owners LIKE %" + accountId.toString() + "%;";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                ResultSet res = statement.executeQuery();

                if (res.next())
                    amount = res.getInt(1);
                else
                    amount = 0;


                res.close();
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get blog count for account", e, this.getClass());
        }
        return amount;
    }

    public int getPostCount() {
        int amount = -1;
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%spost", databaseInfo.getSettings().getPrefix());

                String query = "SELECT COUNT(*) FROM " + tableName + ";";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                ResultSet res = statement.executeQuery();

                if (res.next())
                    amount = res.getInt(1);
                else
                    amount = 0;


                res.close();
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get post count", e, this.getClass());
        }
        return amount;
    }

    public int getPostCount(PostType type) {
        int amount = -1;
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%spost", databaseInfo.getSettings().getPrefix());

                String query = "SELECT COUNT(*) FROM " + tableName + " WHERE post_type = " + type.name() + ";";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                ResultSet res = statement.executeQuery();

                if (res.next())
                    amount = res.getInt(1);
                else
                    amount = 0;


                res.close();
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get post count by type", e, this.getClass());
        }
        return amount;
    }

    public int getPostCountForBlog(UUID blogId) {
        int amount = -1;
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%spost", databaseInfo.getSettings().getPrefix());

                String query = "SELECT COUNT(*) FROM " + tableName + " WHERE origin_blog_id = " + blogId.toString() + ";";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                ResultSet res = statement.executeQuery();

                if (res.next())
                    amount = res.getInt(1);
                else
                    amount = 0;


                res.close();
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get post count for blog", e, this.getClass());
        }
        return amount;
    }

    public int getPostCountForBlog(UUID blogId, PostType type) {
        int amount = -1;
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%spost", databaseInfo.getSettings().getPrefix());

                String query = "SELECT COUNT(*) FROM " + tableName + " WHERE origin_blog_id = " + blogId.toString() + " AND post_type = " + type.name() + ";";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                ResultSet res = statement.executeQuery();

                if (res.next())
                    amount = res.getInt(1);
                else
                    amount = 0;


                res.close();
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get post count for blog", e, this.getClass());
        }
        return amount;
    }

    public int getPostcountForAccount(UUID accountId) {
        int amount = -1;
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%spost", databaseInfo.getSettings().getPrefix());

                String query = "SELECT COUNT(*) FROM " + tableName + " WHERE creator_id = " + accountId.toString() + ";";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                ResultSet res = statement.executeQuery();

                if (res.next())
                    amount = res.getInt(1);
                else
                    amount = 0;


                res.close();
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get post count for account", e, this.getClass());
        }
        return amount;
    }

    public int getPostcountForAccount(UUID accountId, PostType type) {
        int amount = -1;
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%spost", databaseInfo.getSettings().getPrefix());

                String query = "SELECT COUNT(*) FROM " + tableName + " WHERE creator_id = " + accountId.toString() + " AND post_type = " + type.name() + ";";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                ResultSet res = statement.executeQuery();

                if (res.next())
                    amount = res.getInt(1);
                else
                    amount = 0;


                res.close();
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get post count for account", e, this.getClass());
        }
        return amount;
    }

    public int getFollowingCount(UUID user) {
        int amount = -1;
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%sfollow", databaseInfo.getSettings().getPrefix());

                String query = "SELECT COUNT(*) FROM " + tableName + " WHERE user_id = " + user.toString() + ";";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                ResultSet res = statement.executeQuery();

                if (res.next())
                    amount = res.getInt(1);
                else
                    amount = 0;


                res.close();
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get following count for user", e, this.getClass());
        }
        return amount;
    }

    public int getFollowerCount(UUID following) {
        int amount = -1;
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%sfollow", databaseInfo.getSettings().getPrefix());

                String query = "SELECT COUNT(*) FROM " + tableName + " WHERE following_id = " + following.toString() + ";";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                ResultSet res = statement.executeQuery();

                if (res.next())
                    amount = res.getInt(1);
                else
                    amount = 0;


                res.close();
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get follower count for user", e, this.getClass());
        }
        return amount;
    }
}
