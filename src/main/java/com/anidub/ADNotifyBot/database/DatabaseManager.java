package com.anidub.ADNotifyBot.database;

import java.sql.*;
import java.util.*;
import java.util.stream.*;
import com.zaxxer.hikari.*;
import com.anidub.ADNotifyBot.*;
import static flaticommunity.log.TypeLogger.*;

public class DatabaseManager {
    public Connection connection;
    public HikariDataSource hikariDataSource;

    public DatabaseManager(String url, String username, String password) throws Exception {
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);

        /*
        if (false) { // legacy-driver
            hikariConfig.setDataSourceClassName("com.mysql.jdbc.Driver");
        }
        */

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

    public String executeSQL = "SELECT `videos` FROM `live` WHERE `id` = 1";
    public Map<Integer, Integer> executeQuery() {
        try (PreparedStatement preparedStatement = connection.prepareStatement(executeSQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String videos = resultSet.getString("videos");
                if(videos.isEmpty()) return Collections.emptyMap();
                BotLauncher.flatiLogger.log(INFO, "Database VideoIDs on executeQuery(): " + videos);

                return Arrays.stream(videos.split(";"))
                        .map(pair -> pair.split(":"))
                        .filter(keyValue -> keyValue.length == 2)
                        .collect(Collectors.toMap(
                                keyValue -> Integer.parseInt(keyValue[0]),
                                keyValue -> Integer.parseInt(keyValue[1])
                        ));

                /*Arrays.stream(videos.split(","))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList())*/
            }
            resultSet.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }

    public final String updateSQL = "UPDATE `live` SET `videos` = ? WHERE `id` = 1";
    public void updateData() {
        if(BotLauncher.videos.isEmpty()) return;

        StringBuilder stringBuilder = new StringBuilder();
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
            BotLauncher.videos.forEach((videoId, series) -> stringBuilder.append(videoId).append(":").append(series).append(";"));

            final int lastSymbol = (stringBuilder.length() - 1);
            if(stringBuilder.charAt(lastSymbol) == ';') { // try to fix last symbol, my code isn't send me error this symbol, but I see he
                stringBuilder.setLength(lastSymbol);
            }

            preparedStatement.setString(1, stringBuilder.toString());

            final int rowsAffected = preparedStatement.executeUpdate();
            BotLauncher.flatiLogger.log(INFO, "Updated " + rowsAffected + " rows.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
