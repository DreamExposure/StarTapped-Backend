package org.dreamexposure.tap.backend.network.database;

import org.dreamexposure.novautils.database.DatabaseInfo;
import org.dreamexposure.tap.core.objects.bookmark.Bookmark;
import org.dreamexposure.tap.core.utils.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookmarkDataHandler {
    private static BookmarkDataHandler instance;

    private DatabaseInfo masterInfo;
    private DatabaseInfo slaveInfo;

    private String tableName;

    private BookmarkDataHandler() {
    }

    public static BookmarkDataHandler get() {
        if (instance == null) instance = new BookmarkDataHandler();

        return instance;
    }

    void init(DatabaseInfo _master, DatabaseInfo _slave) {
        masterInfo = _master;
        slaveInfo = _slave;
        tableName = String.format("%sbookmark", masterInfo.getSettings().getPrefix());
    }

    public Bookmark getBookmark(UUID accountId, UUID postId) {
        try (final Connection connection = slaveInfo.getSource().getConnection()) {
            String query = "SELECT * FROM " + tableName + " WHERE user_id = ? AND post_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, accountId.toString());
            statement.setString(2, postId.toString());

            ResultSet res = statement.executeQuery();

            boolean hasNext = res.next();

            if (hasNext && res.getString("user_id") != null) {
                Bookmark b = new Bookmark();
                b.setAccountId(accountId);
                b.setPostId(postId);
                b.setTimestamp(res.getLong("timestamp"));

                statement.close();
                return b;
            }
            statement.close();
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get bookmark", e, true, this.getClass());
        }
        return null;
    }

    public List<Bookmark> getBookmarks(UUID accountId, long before, int inclusiveLimit) {
        List<Bookmark> bookmarks = new ArrayList<>();
        try (final Connection connection = slaveInfo.getSource().getConnection()) {
            String query = "SELECT * FROM " + tableName + " WHERE user_id = ? AND timestamp < ? ORDER BY timestamp DESC";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, accountId.toString());
            ps.setLong(2, before);

            ResultSet res = ps.executeQuery();

            while (res.next() && bookmarks.size() <= inclusiveLimit) {
                if (res.getString("user_id") != null) {
                    //Data present, let's grab it.
                    Bookmark b = new Bookmark();
                    b.setAccountId(accountId);
                    b.setPostId(UUID.fromString(res.getString("post_id")));
                    b.setTimestamp(res.getLong("timestamp"));
                    bookmarks.add(b);
                }
            }

            ps.close();
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get bookmarks", e, true, this.getClass());
        }
        return bookmarks;
    }

    public boolean addBookmark(Bookmark bookmark) {
        try (final Connection masterConnection = masterInfo.getSource().getConnection()) {
            String hasDataQuery = "SELECT * FROM " + tableName + " WHERE user_id = ? AND post_id = ?";
            Connection slaveConnection = slaveInfo.getSource().getConnection();
            PreparedStatement statement = slaveConnection.prepareStatement(hasDataQuery);
            statement.setString(1, bookmark.getAccountId().toString());
            statement.setString(2, bookmark.getPostId().toString());


            ResultSet res = statement.executeQuery();

            boolean hasData = res.next();

            if (!hasData || res.getString("id") == null) {
                //No data present, add
                String insert = "INSERT INTO " + tableName +
                        " (user_id, post_id, timestamp)" +
                        " VALUES (?, ?, ?)";
                PreparedStatement ps = masterConnection.prepareStatement(insert);

                ps.setString(1, bookmark.getAccountId().toString());
                ps.setString(2, bookmark.getPostId().toString());
                ps.setLong(3, bookmark.getTimestamp());

                ps.execute();
                ps.close();
                statement.close();
                slaveConnection.close();
                return true;
            } else {
                //Data present... this shit should not be possible
                Logger.getLogger().debug("Tried to add bookmark already present!", false);

                statement.close();
                slaveConnection.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to add bookmark", e, true, this.getClass());
        }
        return false;
    }

    public boolean removeBookmark(Bookmark bookmark) {
        try (final Connection connection = masterInfo.getSource().getConnection()) {
            String query = "DELETE FROM " + tableName + " WHERE user_id = ? AND post_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, bookmark.getAccountId().toString());
            statement.setString(2, bookmark.getPostId().toString());

            statement.execute();
            statement.close();

            return true;
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to remove bookmark", e, true, this.getClass());
        }
        return false;
    }
}

