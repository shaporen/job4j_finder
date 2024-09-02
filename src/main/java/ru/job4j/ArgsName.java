package ru.job4j;

import java.util.*;

public class ArgsName {
    private final Map<String, String> values = new HashMap<>();

    public String get(String key) {
        checkForKey(key);
        return values.get(key);
    }

    private void checkForKey(String key) {
        if (!values.containsKey(key)) {
            throw new IllegalArgumentException(String.format("This key: '%s' is missing", key));
        }
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        if (!key.matches("^[a-z]+$")) {
            throw new IllegalArgumentException("The key can only contain small letters");
        }
    }

    private void parse(String[] args) {
        for (String parameter : args) {
            int index = parameter.indexOf("=");
            values.put(parameter.substring(1, index), parameter.substring(index + 1));
        }
    }

    public static ArgsName of(String[] args) {
        validateData(args);
        ArgsName names = new ArgsName();
        names.parse(args);
        return names;
    }

    public static void validateData(String[] args) {
        if (args.length < 4) {
            throw new IllegalArgumentException("""
                    Incorrect parameters. \
                    The program must be launched with 4 parameters,
                     for example: -d=c: -n=*.?xt -t=mask -o=log.txt
                    Keys
                    -d - directory in which to start searching.
                    -n - file name, mask, or any regular expression.
                    -t - search type: mask -- search by mask, name -- \
                    by location of name match, regex -- by regular expression.
                    -o - write the result to a file.
                    """);
        }
        for (String parameter : args) {
            if (parameter.startsWith("-=")) {
                throw new IllegalArgumentException(String.format("Error: This argument "
                        + "'%s' does not contain a key", parameter));
            }
            if (!parameter.startsWith("-")) {
                throw new IllegalArgumentException(String.format("Error: This argument '%s' "
                        + "does not start with a '-' character", parameter));
            }
            if (parameter.matches("^-[A-Za-z0-9\\\\.]+=$")) {
                throw new IllegalArgumentException(String.format("Error: This argument "
                        + "'%s' does not contain a value", parameter));
            }
            if (!parameter.contains("=")) {
                throw new IllegalArgumentException(String.format("Error: This argument "
                        + "'%s' does not contain an equal sign", parameter));
            }
        }
    }
}
