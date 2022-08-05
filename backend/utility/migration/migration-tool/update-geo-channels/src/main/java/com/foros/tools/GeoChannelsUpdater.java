package com.foros.tools;

import com.foros.migration.Migration;
import com.foros.tools.csv.CsvReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.support.rowset.SqlRowSet;


public class GeoChannelsUpdater implements Migration.Executor {

    private static final String CHARSET_NAME = "ISO-8859-1";
    private static final int BATCH_SIZE = 100;

    @Autowired
    private Logger logger;

    @Autowired
    @Qualifier("postgresJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        Migration.perform(GeoChannelsUpdater.class);
    }

    @Override
    public void run() throws Exception {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        LogManager.getLogManager().readConfiguration(contextClassLoader.getResourceAsStream("logging.properties"));

        Properties config = new Properties();
        config.load(contextClassLoader.getResourceAsStream("config.properties"));

        String locationsPath = config.getProperty("maxmind.locationcsv");
        InputStream locationsStream = null;
        try {
            locationsStream = new FileInputStream(locationsPath);
        } catch (FileNotFoundException e) {
            logger.severe("Unable to open:" + locationsPath);
            logger.severe(e.toString());
            System.exit(1);
        }

        String regCodesPath = config.getProperty("maxmind.regioncodes");
        InputStream regCodesStream = null;
        try {
            regCodesStream = new FileInputStream(regCodesPath);
        } catch (FileNotFoundException e) {
            logger.severe("Unable to open:" + regCodesPath);
            logger.severe(e.toString());
            System.exit(1);
        }

        try {
            loadCountryChannels();
            loadChannels();
            loadRegionCodes(regCodesStream);
            updatePlaces(locationsStream);
            clearPlaces();
            checkStatuses();
            prepareGroups();
            saveChanges();
            logger.info("Done successfully.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error!", e);
            System.exit(1);
        }
    }

    private Map<String, Long> countries = new HashMap<>();
    private Map<String, Map<String, String>> regions = new HashMap<>();
    private Map<LocationName, LocationDetail> places = new LinkedHashMap<>();
    Map<GroupKey, Collection<Map.Entry<LocationName, LocationDetail>>> groups;

    private void loadCountryChannels() throws SQLException {
        logger.info("Extracting countries...");

        SqlRowSet rs = jdbcTemplate.queryForRowSet("select channel_id, country_code from Channel where channel_type = 'G' and geo_type = 'CNTRY'");
        while (rs.next()) {
            countries.put(rs.getString("country_code"), rs.getLong("channel_id"));
        }
    }

    private void loadChannels() throws SQLException {
        jdbcTemplate.setFetchSize(1000);

        logger.info("Executing channels query...");

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("select c.channel_id, c.country_code, s.name region, c.name, c.city_list, c.latitude, c.longitude" +
                " from channel c left join channel s on c.parent_channel_id=s.channel_id and s.geo_type = 'STATE' where c.channel_type='G' and c.geo_type='CITY'");
        logger.info("Loading locations...");
        while (rowSet.next()) {
            String name = rowSet.getString("name");
            String[] cities = rowSet.getString("city_list").split("[\n\r]+");
            TreeSet<String> aliases = new TreeSet<>(Arrays.asList(cities));
            aliases.add(name);

            String country = rowSet.getString("country_code");
            String region = rowSet.getString("region");
            Long rsChannelId = rowSet.getLong("channel_id");
            BigDecimal latitude = rowSet.getBigDecimal("latitude");
            BigDecimal longitude = rowSet.getBigDecimal("longitude");

            for (String city : aliases) {
                LocationName locationName = new LocationName(country, toLower(region), toLower(city));
                Long channelId = city.equals(name) ? rsChannelId : null;
                GeoCoordinates coordinates = new GeoCoordinates(latitude, longitude);
                LocationDetail locationDetail = places.get(locationName);
                if (locationDetail == null) {
                    places.put(locationName, new LocationDetail(coordinates, region, channelId, city, aliases));
                } else if (channelId != null) {
                    if (locationDetail.getChannelId() != null) {
                        logger.warning(String.format("Duplicated channels %s with ids %d & %d",
                                locationName, channelId, locationDetail.getChannelId()));
                    } else {
                        // Replacing location from city_list by identical location created as geo channel
                        places.put(locationName, new LocationDetail(coordinates, region, channelId, city, aliases));
                    }
                } else {
                    logger.info(String.format("Ignored duplicated city %s as alias of %s", locationName, name));
                }
            }
        }
    }

    private void loadRegionCodes(InputStream regCodesStream) throws IOException {
        logger.info("Loading regions...");
        try (CsvReader csvReader = new CsvReader(new InputStreamReader(regCodesStream, CHARSET_NAME))) {
            while (csvReader.readRecord()) {
                String countryCode = csvReader.get(0);
                String regionCode = csvReader.get(1);
                String regionName = csvReader.get(2);
                if (!regions.containsKey(countryCode)) {
                    regions.put(countryCode, new HashMap<String, String>());
                }
                regions.get(countryCode).put(regionCode, regionName);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void updatePlaces(InputStream locationsStream) throws IOException {
        logger.info("Updating locations...");

        Map<LocationName, LocationDetail> newPlaces = new HashMap<>();
        try (CsvReader csvReader = new CsvReader(new InputStreamReader(locationsStream, CHARSET_NAME))) {
            csvReader.readHeaders();
            if (csvReader.getHeaderCount() < 7) {
                csvReader.readHeaders();
            }
            if (csvReader.getHeaderCount() < 7) {
                throw new RuntimeException("Incorrect csv format");
            }

            while (csvReader.readRecord()) {
                String countryCode = csvReader.get(1);

                if (isBlank(countryCode)) {
                    logger.info(String.format("Empty country code: %s", Arrays.asList(csvReader.getValues())));
                    continue;
                }

                if (!countries.containsKey(countryCode)) {
                    logger.info(String.format("Country code is excluded: %s", Arrays.asList(csvReader.getValues())));
                    continue;
                }

                String regionCode = csvReader.get(2);
                String region = regions.get(countryCode) != null ? regions.get(countryCode).get(regionCode) : null;

                if (region == null) {
                    // Unknown region, standalone 'city' geo channel (with parent CNTRY) will be created
                    logger.info(String.format("Region not found: %s", Arrays.asList(csvReader.getValues())));
                }

                String city = csvReader.get(3);

                if (isBlank(city)) {
                    logger.info(String.format("Empty city name: %s", Arrays.asList(csvReader.getValues())));
                    continue;
                }

                GeoCoordinates coordinates = new GeoCoordinates(csvReader.get(5), csvReader.get(6));
                LocationName locationName = new LocationName(countryCode, toLower(region), toLower(city));

                // MaxMind CSV contains several records with the same countryCode, regionCode, city, but various geo coordinates
                // We choose same coordinates as in DB if possible, otherwise - coordinates from first record
                LocationDetail newLocationDetail = newPlaces.get(locationName);
                if (newLocationDetail == null) {
                    newLocationDetail = new LocationDetail(coordinates, region, city, LocationDetail.Status.NEW);
                    newPlaces.put(locationName, newLocationDetail);
                } else {
                    LocationDetail locationDetail = places.get(locationName);
                    if (locationDetail != null && locationDetail.getCoordinates().equals(coordinates) && !newLocationDetail.getCoordinates().equals(coordinates)) {
                        newLocationDetail.setCoordinates(coordinates);
                        newLocationDetail.setName(city);
                        newLocationDetail.setRegion(region);
                    }
                }
            }
        }

        for (Map.Entry<LocationName, LocationDetail> newPlaceEntry : newPlaces.entrySet()) {
            LocationName locationName = newPlaceEntry.getKey();
            LocationDetail newLocationDetail = newPlaceEntry.getValue();

            LocationDetail locationDetail = places.get(locationName);
            if (locationDetail == null) {
                places.put(locationName, newLocationDetail);
                continue;
            }

            if (locationDetail.getCoordinates().equals(newLocationDetail.getCoordinates())) {
                locationDetail.setStatus(LocationDetail.Status.UNCHANGED);
            } else {
                locationDetail.setStatus(LocationDetail.Status.UPDATED);
                locationDetail.setCoordinates(newLocationDetail.getCoordinates());
            }
        }
    }

    private void clearPlaces() {
        logger.info("Clearing outdated...");
        Iterator<Map.Entry<LocationName, LocationDetail>> iterator = places.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<LocationName, LocationDetail> entry = iterator.next();
            LocationDetail locationDetail = entry.getValue();
            if (locationDetail.getStatus() == null) {
                if (locationDetail.getChannelId() == null) {
                    iterator.remove();
                    logger.info(String.format("Removed city from city_list: %s %s",
                            entry.getKey(), locationDetail.getCoordinates()));
                } else {
                    locationDetail.setStatus(LocationDetail.Status.DELETED);
                }
            }
        }
    }

    private void checkStatuses() {
        for (Map.Entry<LocationName, LocationDetail> entry : places.entrySet()) {
            LocationDetail detail = entry.getValue();
            LocationDetail.Status status = detail.getStatus();
            if (status == null) {
                logger.warning(String.format("Location with unknown status: %s %s", entry.getKey(), detail));
            } else if (status.equals(LocationDetail.Status.NEW) && detail.getChannelId() != null) {
                logger.warning(String.format("New location with existing channel: %s %s", entry.getKey(), detail));
            }
        }
    }

    private void prepareGroups() {
        logger.info("Preparing groups...");
        groups = new HashMap<>(places.size() / 3);
        for (Map.Entry<LocationName, LocationDetail> entry : places.entrySet()) {
            GroupKey key = new GroupKey(entry.getKey(), entry.getValue());
            Collection<Map.Entry<LocationName, LocationDetail>> locations = groups.get(key);
            if (locations == null) {
                locations = new ArrayList<>();
                groups.put(key, locations);
            }
            locations.add(entry);
        }
    }

    private void saveChanges() throws SQLException {
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        connection.setAutoCommit(false);

        logger.info("Saving locations...");
        long mergedRegions = 0;
        try {
            logger.info("Merging regions...");

            Set<LocationName> regions = new HashSet<>();
            for (GroupKey key : groups.keySet()) {
                if (key.getRegion() != null) {
                    regions.add(new LocationName(key.getCountryCode(), key.getRegion(), null));
                }
            }

            int[][] batchInsertsNum = jdbcTemplate.batchUpdate("insert into channel" +
                            " (parent_channel_id, country_code, name, channel_type, geo_type, visibility, namespace, status, qa_status, display_status_id, flags, message_sent) " +
                            " select ?, ?, ?, 'G', 'STATE', 'PUB', 'G', 'A', 'A', 1, 0, 0 " +
                            " where not exists (" +
                            "     select 1 from channel where channel_type = 'G' and geo_type = 'STATE' and country_code = ? and lower(name) = lower(?)" +
                            ")",
                    regions,
                    BATCH_SIZE,
                    new ParameterizedPreparedStatementSetter<LocationName>() {
                        @Override
                        public void setValues(PreparedStatement ps, LocationName argument) throws SQLException {
                            ps.setLong(1, countries.get(argument.getCountryCode()));
                            ps.setString(2, argument.getCountryCode());
                            ps.setString(3, argument.getRegion());
                            ps.setString(4, argument.getCountryCode());
                            ps.setString(5, argument.getRegion());
                        }
                    });
            for (int[] insertsNum : batchInsertsNum) {
                for (int insertNum : insertsNum) {
                    mergedRegions += insertNum;
                }
            }

            logger.info("Merging cities...");

            Collection<Collection<Map.Entry<LocationName, LocationDetail>>> cityLocations = groups.values();
            List<LocationDetailFull> toInsertInState = new ArrayList<>(cityLocations.size());
            List<LocationDetailFull> toInsertInCountry = new ArrayList<>(cityLocations.size());
            List<LocationDetailFull> toUpdate = new ArrayList<>(cityLocations.size());

            for (Collection<Map.Entry<LocationName, LocationDetail>> locations : cityLocations) {
                SortedSet<String> aliases = new TreeSet<>();
                List<Map.Entry<LocationName, LocationDetail>> existings = new ArrayList<>(locations.size());
                for (Map.Entry<LocationName, LocationDetail> location : locations) {
                    aliases.add(location.getValue().getName());
                    if (location.getValue().getChannelId() != null) {
                        existings.add(location);
                    }
                }
                if (existings.isEmpty()) {
                    Map.Entry<LocationName, LocationDetail> location = locations.iterator().next();
                    if (location.getKey().getRegion() != null) {
                        toInsertInState.add(new LocationDetailFull(location.getKey(), location.getValue(), GeoChannelsUpdater.toString(aliases)));
                    } else {
                        toInsertInCountry.add(new LocationDetailFull(location.getKey(), location.getValue(), GeoChannelsUpdater.toString(aliases)));
                    }
                } else {
                    if (existings.size() > 1) {
                        logger.info(String.format("Duplicated existings: " + existings + " new aliases - " + aliases));
                    }
                    for (Map.Entry<LocationName, LocationDetail> location : existings) {
                        SortedSet<String> mergedAliases = new TreeSet<>(aliases);
                        mergedAliases.addAll(location.getValue().getAliases());
                        if (location.getValue().getStatus().equals(LocationDetail.Status.UPDATED) ||
                                !mergedAliases.equals(location.getValue().getAliases())) {
                            toUpdate.add(new LocationDetailFull(location.getKey(), location.getValue(), GeoChannelsUpdater.toString(aliases)));
                        }
                    }
                }
            }

            logger.info("Insert in state num: " + toInsertInState.size());
            jdbcTemplate.batchUpdate("insert into channel" +
                            " (parent_channel_id, country_code, name, latitude, longitude, city_list, channel_type, geo_type, visibility, namespace, status, qa_status, display_status_id, flags, message_sent) " +
                            " select (select channel_id from Channel where channel_type='G' and geo_type='STATE' and country_code=? and name=?), ?, ?, ?, ?, ?, 'G', 'CITY', 'PUB', 'G', 'A', 'A', 1, 0, 0 ",
                    toInsertInState,
                    BATCH_SIZE,
                    new ParameterizedPreparedStatementSetter<LocationDetailFull>() {
                        @Override
                        public void setValues(PreparedStatement ps, LocationDetailFull argument) throws SQLException {
                            ps.setString(1, argument.getName().getCountryCode());
                            ps.setString(2, argument.getDetail().getRegion());
                            ps.setString(3, argument.getName().getCountryCode());
                            ps.setString(4, argument.getDetail().getName());
                            ps.setBigDecimal(5, argument.getDetail().getCoordinates().getLatitude());
                            ps.setBigDecimal(6, argument.getDetail().getCoordinates().getLongitude());
                            ps.setString(7, argument.getAliases());
                        }
                    });
            logger.info("Insert in country num: " + toInsertInCountry.size());
            jdbcTemplate.batchUpdate("insert into channel" +
                            " (parent_channel_id, country_code, name, latitude, longitude, city_list, channel_type, geo_type, visibility, namespace, status, qa_status, display_status_id, flags, message_sent) " +
                            " select ?, ?, ?, ?, ?, ?, 'G', 'CITY', 'PUB', 'G', 'A', 'A', 1, 0, 0 ",
                    toInsertInCountry,
                    BATCH_SIZE,
                    new ParameterizedPreparedStatementSetter<LocationDetailFull>() {
                        @Override
                        public void setValues(PreparedStatement ps, LocationDetailFull argument) throws SQLException {
                            ps.setLong(1, countries.get(argument.getName().getCountryCode()));
                            ps.setString(2, argument.getName().getCountryCode());
                            ps.setString(3, argument.getDetail().getName());
                            ps.setBigDecimal(4, argument.getDetail().getCoordinates().getLatitude());
                            ps.setBigDecimal(5, argument.getDetail().getCoordinates().getLongitude());
                            ps.setString(6, argument.getAliases());
                        }
                    });
            long insertedLocations = toInsertInState.size() + toInsertInCountry.size();

            jdbcTemplate.batchUpdate("update Channel set version=now(), latitude=?, longitude=?, city_list=? where channel_id=?",
                    toUpdate,
                    BATCH_SIZE,
                    new ParameterizedPreparedStatementSetter<LocationDetailFull>() {
                        @Override
                        public void setValues(PreparedStatement ps, LocationDetailFull argument) throws SQLException {
                            ps.setBigDecimal(1, argument.getDetail().getCoordinates().getLatitude());
                            ps.setBigDecimal(2, argument.getDetail().getCoordinates().getLongitude());
                            ps.setString(3, argument.getAliases());
                            ps.setLong(4, argument.getDetail().getChannelId());
                        }
                    });
            long updatedLocations = toUpdate.size();

            connection.commit();
            logger.info(String.format("Merged %d regions from %d, inserted %d locations, updated %d locations",
                    mergedRegions, regions.size(), insertedLocations, updatedLocations));
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private static boolean isBlank(String str) {
        return str == null || str.isEmpty();
    }

    private static String toString(SortedSet<String> strings) {
        StringWriter writer = new StringWriter();
        PrintWriter printer = new PrintWriter(writer);
        boolean first = true;
        for (String str : strings) {
            if (first) {
                first = false;
            } else {
                printer.println();
            }
            printer.print(str);
        }
        printer.flush();
        return writer.toString();
    }

    private static String toLower(String original) {
        if (original == null) {
            return null;
        }
        return original.toLowerCase();
    }
}
