package org.dreamexposure.tap.backend.network.database;

import org.dreamexposure.novautils.database.DatabaseInfo;
import org.dreamexposure.tap.core.objects.file.UploadedFile;
import org.dreamexposure.tap.core.utils.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@SuppressWarnings("Duplicates")
public class FileDataHandler {
    private static FileDataHandler instance;

    private DatabaseInfo masterInfo;
    private DatabaseInfo slaveInfo;

    private FileDataHandler() {
    }

    public static FileDataHandler get() {
        if (instance == null) instance = new FileDataHandler();

        return instance;
    }

    void init(DatabaseInfo _master, DatabaseInfo _slave) {
        masterInfo = _master;
        slaveInfo = _slave;
    }

    public void addFile(UploadedFile file) {
        try (final Connection connection = masterInfo.getSource().getConnection()) {
            String tableName = String.format("%sfile", masterInfo.getSettings().getPrefix());
            String query = "INSERT INTO " + tableName + " (hash, uploader_id, name, url, path, timestamp) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, file.getHash());
            statement.setString(2, file.getUploader().toString());
            statement.setString(3, file.getName());
            statement.setString(4, file.getUrl());
            statement.setString(5, file.getPath());
            statement.setLong(6, file.getTimestamp());

            statement.execute();
            statement.close();
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to input confirmation code", e, true, this.getClass());
        }
    }

    public UploadedFile getFileFromHash(String hash) {
        try (final Connection connection = slaveInfo.getSource().getConnection()) {
            String tableName = String.format("%sfile", slaveInfo.getSettings().getPrefix());
            String query = "SELECT * FROM " + tableName + " WHERE hash = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, hash);
            ResultSet res = statement.executeQuery();

            boolean hasStuff = res.next();

            if (hasStuff && res.getString("hash") != null) {
                UploadedFile file = new UploadedFile();

                file.setHash(hash);
                file.setUploader(UUID.fromString(res.getString("uploader_id")));
                file.setName(res.getString("name"));
                file.setUrl(res.getString("url"));
                file.setPath(res.getString("path"));
                file.setTimestamp(res.getLong("timestamp"));

                statement.close();

                return file;
            } else {
                //Data not present.
                statement.close();
                return null;
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get file data by hash", e, true, this.getClass());
        }
        return null;
    }

    public UploadedFile getFileFromUrl(String url) {
        try (final Connection connection = slaveInfo.getSource().getConnection()) {
            String tableName = String.format("%sfile", slaveInfo.getSettings().getPrefix());
            String query = "SELECT * FROM " + tableName + " WHERE url = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, url);
            ResultSet res = statement.executeQuery();

            boolean hasStuff = res.next();

            if (hasStuff && res.getString("hash") != null) {
                UploadedFile file = new UploadedFile();

                file.setHash(res.getString("hash"));
                file.setUploader(UUID.fromString(res.getString("uploader_id")));
                file.setName(res.getString("name"));
                file.setUrl(res.getString("url"));
                file.setPath(res.getString("path"));
                file.setTimestamp(res.getLong("timestamp"));

                statement.close();

                return file;
            } else {
                //Data not present.
                statement.close();
                return null;
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get file data by url", e, true, this.getClass());
        }
        return null;
    }

    public void removeFileByHash(String hash) {
        try (final Connection connection = masterInfo.getSource().getConnection()) {
            String tableName = String.format("%sfile", masterInfo.getSettings().getPrefix());
            String query = "DELETE FROM " + tableName + " WHERE hash = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, hash);

            statement.execute();
            statement.close();
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to delete file data by hash", e, true, this.getClass());
        }
    }

    public void removeByUrl(String url) {
        try (final Connection connection = masterInfo.getSource().getConnection()) {
            String tableName = String.format("%sfile", masterInfo.getSettings().getPrefix());
            String query = "DELETE FROM " + tableName + " WHERE url = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, url);

            statement.execute();
            statement.close();
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to delete file data by url", e, true, this.getClass());
        }
    }
}
