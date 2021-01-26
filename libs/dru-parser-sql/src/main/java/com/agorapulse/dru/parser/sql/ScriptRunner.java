/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2021 Agorapulse.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.agorapulse.dru.parser.sql;

import java.io.BufferedReader;
import java.io.Reader;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Inspired by iBatis ScriptRunner.
 */
class ScriptRunner {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

    private static final String DEFAULT_DELIMITER = ";";

    private static final Pattern DELIMITER_PATTERN = Pattern.compile("^\\s*((--)|(//))?\\s*(//)?\\s*@DELIMITER\\s+([^\\s]+)", Pattern.CASE_INSENSITIVE);

    private final Connection connection;

    private String delimiter = DEFAULT_DELIMITER;

    public ScriptRunner(Connection connection) {
        this.connection = connection;
    }

    public Map<String, List<Map<String, Object>>> runScript(Reader reader) {
        return executeLineByLine(reader);
    }

    private Map<String, List<Map<String, Object>>> executeLineByLine(Reader reader) {
        Map<String, List<Map<String, Object>>> allResults = new LinkedHashMap<>();
        StringBuilder command = new StringBuilder();
        try {
            BufferedReader lineReader = new BufferedReader(reader);
            String line;
            while ((line = lineReader.readLine()) != null) {
                Map<String, List<Map<String, Object>>> results = handleLine(command, line);
                results.forEach((key, value) -> allResults.computeIfAbsent(key, k -> new ArrayList<>()).addAll(value));
            }
            commitConnection();
            checkForMissingLineTerminator(command);
            return allResults;
        } catch (Exception e) {
            String message = "Error executing: " + command + ".  Cause: " + e;
            SqlParser.LOGGER.error(message, e);
            throw new IllegalArgumentException(message, e);
        }
    }

    private void commitConnection() throws SQLException {
        if (!connection.getAutoCommit()) {
            connection.commit();
        }
    }

    private void checkForMissingLineTerminator(StringBuilder command) {
        if (command != null && command.toString().trim().length() > 0) {
            throw new IllegalArgumentException("Line missing end-of-line terminator (" + delimiter + ") => " + command);
        }
    }

    private Map<String, List<Map<String, Object>>> handleLine(StringBuilder command, String line) throws SQLException {
        String trimmedLine = line.trim();
        if (lineIsComment(trimmedLine)) {
            Matcher matcher = DELIMITER_PATTERN.matcher(trimmedLine);
            if (matcher.find()) {
                delimiter = matcher.group(5);
            }
            SqlParser.LOGGER.debug(trimmedLine);
            return Collections.emptyMap();
        }
        if (commandReadyToExecute(trimmedLine)) {
            command.append(line, 0, line.lastIndexOf(delimiter));
            command.append(LINE_SEPARATOR);
            if (SqlParser.LOGGER.isInfoEnabled()) {
                SqlParser.LOGGER.info(command.toString());
            }
            Map<String, List<Map<String, Object>>> results = executeStatement(command.toString());
            command.setLength(0);
            return results;
        }
        if (trimmedLine.length() > 0) {
            command.append(line);
            command.append(LINE_SEPARATOR);
        }
        return Collections.emptyMap();
    }

    private boolean lineIsComment(String trimmedLine) {
        return trimmedLine.startsWith("//") || trimmedLine.startsWith("--");
    }

    private boolean commandReadyToExecute(String trimmedLine) {
        return trimmedLine.contains(delimiter);
    }

    private Map<String, List<Map<String, Object>>> executeStatement(String command) throws SQLException {
        Map<String, List<Map<String, Object>>> allResults = new LinkedHashMap<>();
        try (Statement statement = connection.createStatement()) {
            String sql = command.replaceAll("\r\n", "\n");
            boolean hasResults = statement.execute(sql);
            while (!(!hasResults && statement.getUpdateCount() == -1)) {
                checkWarnings(statement);
                Map<String, List<Map<String, Object>>> results = readResults(statement, hasResults);
                if (hasResults && !results.isEmpty()) {
                    results.forEach((key, value) -> allResults.computeIfAbsent(key, k -> new ArrayList<>()).addAll(value));
                }
                hasResults = statement.getMoreResults();
            }
        }
        if (SqlParser.LOGGER.isDebugEnabled()) {
            SqlParser.LOGGER.debug("Results: {}", allResults);
        }
        return allResults;
    }

    private void checkWarnings(Statement statement) throws SQLException {
        // In Oracle, CREATE PROCEDURE, FUNCTION, etc. returns warning
        // instead of throwing exception if there is compilation error.
        SQLWarning warning = statement.getWarnings();
        if (warning != null) {
            throw warning;
        }
    }

    private Map<String, List<Map<String, Object>>> readResults(Statement statement, boolean hasResults) throws SQLException {
        if (!hasResults) {
            return Collections.emptyMap();
        }

        Map<String, List<Map<String, Object>>> results = new LinkedHashMap<>();

        try (ResultSet rs = statement.getResultSet()) {
            int rowIndex = 0;
            while (rs.next()) {
                ResultSetMetaData md = rs.getMetaData();
                int cols = md.getColumnCount();


                for (int i = 0; i < cols; i++) {
                    String table = md.getTableName(i + 1);
                    String label = md.getColumnLabel(i + 1);
                    Object value = rs.getObject(i + 1);

                    List<Map<String, Object>> rows = results.computeIfAbsent(table, t -> new ArrayList<>());

                    if (rows.size() <= rowIndex) {
                        rows.add(new LinkedHashMap<>());
                    }

                    Map<String, Object> row = rows.get(rowIndex);
                    row.put(label, value);
                }

                rowIndex++;
            }
        }

        return results;
    }
}
