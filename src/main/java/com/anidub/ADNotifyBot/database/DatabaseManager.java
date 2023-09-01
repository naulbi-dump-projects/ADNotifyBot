package com.anidub.ADNotifyBot.database;

import com.anidub.ADNotifyBot.BotLauncher;
import com.zaxxer.hikari.*;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static flaticommunity.log.TypeLogger.INFO;

public class DatabaseManager {
    public Connection connection;
    public HikariDataSource hikariDataSource;

    public DatabaseManager(String url, String username, String password) throws Exception {
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);

        if (false) { // legacy-driver
            hikariConfig.setDataSourceClassName("com.mysql.jdbc.Driver");
        }

        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
        hikariConfig.addDataSourceProperty("useLocalSessionState", "true");
        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        hikariConfig.addDataSourceProperty("cacheResultSetMetadata", "true");
        hikariConfig.addDataSourceProperty("cacheServerConfiguration", "true");
        hikariConfig.addDataSourceProperty("elideSetAutoCommits", "true");
        hikariConfig.addDataSourceProperty("maintainTimeStats", "false");

        hikariConfig.addDataSourceProperty("characterEncoding", "utf8");
        hikariConfig.addDataSourceProperty("useUnicode", "true");
        hikariConfig.setConnectionInitSql("SET NAMES utf8 COLLATE utf8_general_ci");

        hikariConfig.setMinimumIdle(16);
        hikariConfig.setMaximumPoolSize(32);
        hikariConfig.setIdleTimeout(30000);

        hikariConfig.setPoolName("ADNotifyBotPool");

        hikariDataSource = new HikariDataSource(hikariConfig);
        connection = hikariDataSource.getConnection();
    }

    public List<Integer> executeQuery() throws Exception {
        String sql = "SELECT `videoIds` FROM `live` WHERE `id` = 1";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String videoIds = resultSet.getString("videoIds");
                if(videoIds.isEmpty()) return Collections.emptyList();

                BotLauncher.flatiLogger.log(INFO, "Database VideoIDs on executeQuery(): " + videoIds);
                return Arrays.stream(videoIds.split(","))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public void updateData() {
        if(BotLauncher.videosIds.isEmpty()) return;

        String sql = "UPDATE `live` SET `videoIds` = ? WHERE `id` = 1";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, StringUtils.join(BotLauncher.videosIds, ","));

            int rowsAffected = preparedStatement.executeUpdate();
            BotLauncher.flatiLogger.log(INFO, "Updated " + rowsAffected + " rows.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
