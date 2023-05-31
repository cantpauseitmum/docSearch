package org.example;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException, CsvException {
        String[] filesToCheck = {"pl.json", "Messages_pl.properties", "app_pl.arb"};
        List<String> fileInput = getFileInput(filesToCheck);
        List<String> csvInput = getCSVInput();
        List<List<String>> data = searchJava(fileInput, csvInput);
        myTXTWriter(data);
    }

    private static List<String> getFileInput(String[] extensions) {
        List<String> fileOutput;
        try (Stream<Path> paths = Files.walk(Paths.get("C:\\Users\\HubertFelicjaniak\\IdeaProjects"))) {
            fileOutput = paths
                    .filter(p -> !Files.isDirectory(p))
                    .map(p -> p.toString().toLowerCase())
                    .filter(f -> filesToCheck(f, extensions))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileOutput;
    }

    private static boolean filesToCheck(String file, String[] extensions) {
        for (String fileExtension : extensions) {
            if (file.contains(fileExtension))
                return true;
        }
        return false;
    }

    private static List<String> getCSVInput() throws IOException, CsvException {
        CSVReader reader = new CSVReaderBuilder(new FileReader(getCurrentPath() + "\\input.csv")).build();
        List<String> result = new ArrayList<>();
        String[] value;
        while ((value = reader.readNext()) != null) {
            result.add(value[0]);
        }
        return result;
    }

    private static List<List<String>> listToListList(List<String> list) {
        List<List<String>> toCSV = new ArrayList<>();
        for (String value : list) {
            List<String> convertList = new ArrayList<>();
            convertList.add(value);
            toCSV.add(convertList);
        }
        return toCSV;
    }

    private static void myTXTWriter(List<List<String>> data) throws IOException {
        File file = new File(getCurrentPath() + "/output.txt");
        FileWriter outputFile = new FileWriter(file);
        for (List<String> datum : data) {
            for (String text : datum) {
                outputFile.write(text + "^");
            }
            outputFile.write(System.lineSeparator());
        }
        outputFile.close();
    }

    private static void myCSVWriter(List<List<String>> data) throws IOException {
        File file = new File(getCurrentPath() + "/output.csv");
        FileWriter outputFile = new FileWriter(file);
        CSVWriter writer = new CSVWriter(outputFile);
        for (List<String> datum : data) {
            String[] header = datum.toArray(new String[0]);
            writer.writeNext(header);
        }
        writer.close();
    }

    private static String getCurrentPath() throws IOException {
        return new File(".").getCanonicalPath();
    }

    private static String getProjectName(String filePath) throws IOException {
        String currentPath = "C:\\Users\\HubertFelicjaniak\\IdeaProjects";
        filePath = filePath.replace(currentPath.toLowerCase(), "");
        String[] temp = filePath.split("\\\\");
        if (temp.length == 2) {
            String[] tempPath = currentPath.split("\\\\");
            return tempPath[tempPath.length - 1];
        }
        return temp[1];
    }

    private static List<List<String>> searchJava(List<String> fileInput, List<String> csvInput) throws IOException {
        List<List<String>> resultList = listToListList(csvInput);
        for (String s : fileInput) {
            Path javaPath = Paths.get(s);
            List<String> result;
            try (Stream<String> lines = Files.lines(javaPath)) {
                result = lines.collect(Collectors.toList());
            }
            for (int j = 0; j < csvInput.size(); j++) {
                List<String> tempList;
                tempList = (resultList.get(j));
                for (String list : result) {
                    if (list.contains(csvInput.get(j))) {
                        String[] split = list.split(":");
                        tempList.add(getProjectName(s) + " : " + split[0]);
                        System.out.println(getProjectName(s) + " : " + split[0]);
                        resultList.set(j, tempList);
                        break;
                    }
                }
            }
        }
        return resultList;
    }
}
