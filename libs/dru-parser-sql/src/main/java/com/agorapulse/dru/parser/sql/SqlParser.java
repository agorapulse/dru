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

import com.agorapulse.dru.Source;
import com.agorapulse.dru.parser.AbstractParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SqlParser extends AbstractParser {

    static final Logger LOGGER = LoggerFactory.getLogger(SqlParser.class);

    private static final int INDEX = 1000;

    @Override
    public int getIndex() {
        return INDEX;
    }

    @Override
    public boolean isSupported(Source source) {
        if (!source.getPath().endsWith(".sql")) {
            return false;
        }

        if (source.getReferenceObject() instanceof DataSourceProvider) {
            return true;
        }

        LOGGER.warn("SQL source should be loaded but the unit test does not implement DataSourceProvider!");

        return  false;
    }

    @Override
    public Object getContent(Source source) {
        Object unitTest = source.getReferenceObject();
        if (!(unitTest instanceof DataSourceProvider)) {
            LOGGER.warn("SQL source should be loaded but the unit test does not implement DataSourceProvider!");
            return Collections.emptyList();
        }
        DataSource dataSource = ((DataSourceProvider) unitTest).getDataSource();

        try {
            Map<String, List<Map<String, Object>>> result = new ScriptRunner(dataSource.getConnection()).runScript(new InputStreamReader(source.getSourceStream()));
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Source {} loaded as \n\n{}", source, result);
            }
            return result;
        } catch (SQLException e) {
            throw new IllegalStateException("Exception obtaining connection from the DataSource " + dataSource, e);
        }
    }

}
