package com.agorapulse.dru.parser;

import java.util.*;

public class Parsers {

    private Parsers() { }

    private static Set<Parser> availableParsers;

    static {
        Map<Integer, Parser> parsersMap = new TreeMap<>();
        ServiceLoader<Parser> allParsers = ServiceLoader.load(Parser.class);
        for (Parser parser : allParsers) {
            parsersMap.put(parser.getIndex(), parser);
        }
        availableParsers = Collections.unmodifiableSet(new LinkedHashSet<>(parsersMap.values()));
    }

    public static Parser findParser(String relativePath) {
        for (Parser parser : availableParsers) {
            if (parser.isSupported(relativePath)) {
                return parser;
            }
        }
        throw new IllegalArgumentException("No parser supports " + relativePath);
    }

}
