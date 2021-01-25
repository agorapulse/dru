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
package com.agorapulse.dru.parser;

import com.agorapulse.dru.Source;

import java.util.*;

public class Parsers {

    private Parsers() { }

    private static Set<Parser> availableParsers;

    static {
        Map<Integer, Parser> parsersMap = new TreeMap<>();
        ServiceLoader<Parser> allParsers = ServiceLoader.load(Parser.class);
        for (Parser parser : allParsers) {
            parsersMap.put(parser.
                getIndex(), parser);
        }
        availableParsers = Collections.unmodifiableSet(new LinkedHashSet<>(parsersMap.values()));
    }

    public static Parser findParser(Source source) {
        for (Parser parser : availableParsers) {
            if (parser.isSupported(source)) {
                return parser;
            }
        }
        throw new IllegalArgumentException("No parser supports " + source);
    }

}
