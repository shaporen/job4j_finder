package ru.job4j;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;

public class Finder {
    public static List<Path> search(Path root, Predicate<Path> condition) throws IOException {
        SearchFiles searcher = new SearchFiles(condition);
        Files.walkFileTree(root, searcher);
        return searcher.getPaths();
    }

    public static String convertMaskToRegex(String string) {
        string = string.replace(".", "\\.")
                .replace('?', '.')
                .replace("*", ".*");
        return string;
    }

    @SuppressWarnings("checkstyle:OperatorWrap")
    public static void validationParameters(ArgsName argsName) {
        File file = new File(argsName.get("d"));
        String needFindFile = argsName.get("n");
        String howToSearch = argsName.get("t");
        String fileToWrite = argsName.get("o");
        if (!file.exists()) {
            throw new IllegalArgumentException(String.format("Not exist %s",
                    file.getAbsoluteFile()));
        }
        if (!file.isDirectory()) {
            throw new IllegalArgumentException(String.format("Not directory %s",
                    file.getAbsoluteFile()));
        }
        if (!"regex".equals(howToSearch) && !"mask".equals(howToSearch)
                && !needFindFile.matches("[A-Za-z0-9]*\\.[a-z0-9]{2,}$")) {
            throw new IllegalArgumentException(String.format("Incorrect search file format: %s",
                    needFindFile));
        }
        if ("mask".equals(howToSearch)
                && !needFindFile.matches("[A-Za-z0-9?*]*\\.[a-z0-9?*]{2,}$")) {
            throw new IllegalArgumentException(String.format("Incorrect search file format: %s",
                    needFindFile));
        }
        if (!"mask".equals(howToSearch) && !"name".equals(howToSearch)
                && !"regex".equals(howToSearch)) {
            throw new IllegalArgumentException(String.format("Incorrect third parameter: %s. "
                    + "This must be: \"mask\", \"name\" or \"regex\".", howToSearch));
        }
        if (!fileToWrite.matches("^.*\\.[a-z0-9]{2,5}$")) {
            throw new IllegalArgumentException(String.format("Incorrect file format: %s",
                    fileToWrite));
        }
    }

    public static void writeResultsToFile(String path, List<Path> pathList) throws IOException {
        String newPath = "data\\" + path;
        try (PrintWriter writer = new PrintWriter(new FileWriter(newPath, false))) {
            for (Path onePath : pathList) {
                writer.println(onePath.toString());
                System.out.println(onePath);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        ArgsName argsName = ArgsName.of(args);
        validationParameters(argsName);
        List<Path> results;
        String t = argsName.get("t");
        if (t.equals("regex")) {
            results = search(Paths.get(argsName.get("d")), path -> path.toFile()
                    .getName()
                    .matches(argsName.get("n")));
        } else if (t.equals("mask")) {
            String regex = convertMaskToRegex(argsName.get("n"));
            results = search(Paths.get(argsName.get("d")), path -> path.toFile().
                    getName()
                    .matches(regex));
        } else if (t.equals("name")) {
            results = search(Paths.get(argsName.get("d")), path -> path.toFile()
                    .getName()
                    .endsWith(argsName.get("n")));
        } else {
            results = new ArrayList<>();
        }
        if (!results.isEmpty()) {
            writeResultsToFile(argsName.get("o"), results);
        }
        System.out.println(results.size());
    }
}
