package com.foros.tools;

import com.foros.session.channel.descriptors.ChannelTriggersContainer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Properties;
import java.util.logging.Logger;
import oracle.jdbc.driver.OracleDriver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public class AddChannelTrigger {
    private static final Logger logger = Logger.getLogger(AddChannelTrigger.class.getName());

    public static void main(String[] args) throws SQLException {
        if (args.length < 2) {
            logger.severe("Wrong params");
            System.exit(1);
        }

        Properties properties = new Properties();
        String propertyFileName = args[0];
        String dbUrl;
        String dbUser;
        String dbPassword;

        try {
            properties.load(new FileInputStream(propertyFileName));
        } catch (IOException e) {
            logger.severe("Unable to load properties:");
            e.printStackTrace();
            System.exit(1);
        }

        dbUrl = properties.getProperty("ora_url");

        if (dbUrl == null) {
            throw new RuntimeException("DB URL is not properly set");
        }

        dbUser = properties.getProperty("ora_user");

        if (dbUser == null) {
            throw new RuntimeException("DB User is not properly set");
        }

        dbPassword = properties.getProperty("ora_password");

        if (dbPassword == null) {
            throw new RuntimeException("DB Password is not properly set");
        }

        Properties connectionProperties = new Properties();
        connectionProperties.put("user", dbUser);
        connectionProperties.put("password", dbPassword);
        connectionProperties.put("oracle.jdbc.createDescriptorUseCurrentSchemaForSchemaName", "true");

        OracleDriver oracleDriver = new OracleDriver();
        Connection connection = null;

        try {
            DriverManager.registerDriver(oracleDriver);
            connection = DriverManager.getConnection(dbUrl, connectionProperties);
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            logger.severe("Failed to establish a connection");
            e.printStackTrace();
            System.exit(1);
        }

        String csvFileName = args[1];
        Collection<ChannelTriggersContainer> triggersToAdd = null;
        try {
            triggersToAdd = BlowoutCsvReader.read(new File(csvFileName));
            logger.info("CSV is read " + csvFileName);
        } catch (Exception e) {
            logger.severe("Unable to read csv from " + csvFileName);
            e.printStackTrace();
            System.exit(1);
        }

        try {
            JdbcTemplate template = new JdbcTemplate(new SingleConnectionDataSource(connection, true));
            Updater updater = new Updater(template);
            updater.setTriggersToAdd(triggersToAdd);
            updater.prepareChannels();
            updater.doUpdate();
            connection.commit();
        } catch (Exception e) {
            logger.severe("Error during processing triggers");
            e.printStackTrace();
            connection.rollback();
            System.exit(1);
        } finally {
            if (!connection.isClosed()) {
                connection.close();
            }
        }

        DriverManager.deregisterDriver(oracleDriver);
    }
}

