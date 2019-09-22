package app;

import csv.CSVWriter;
import record.Record;
import record.RecordProcessor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class Main {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        ConfigReader configReader;

        System.out.print("Input config file path: ");
        while (true) {
            try {
                String configFilePath = scanner.nextLine();
                configReader = new ConfigReader(configFilePath);
                break;
            } catch (FileNotFoundException e) {
                System.err.print("Config file not found! Input correct path: ");
            }
        }

        Config config = configReader.getConfig();

        String csvDirectoryPath = config.getDirectoryPath();

        List<Path> csvFiles = getCSVFiles(csvDirectoryPath);
        if (csvFiles.isEmpty()) {
            System.err.println(String.format("No CSV files were found in the directory: %s", csvDirectoryPath));
            return;
        }

        System.out.println(String.format("Found %s CSV files, process started...", csvFiles.size()));
        long start = System.currentTimeMillis();

        RecordProcessor processor = new RecordProcessor(csvFiles, config);
        ProgressBar progressBar = new ProgressBar();
        processor.setProgressHandler(progressBar::tick);

        List<Record> cheapestRecords = processor.getCheapestRecords();
        System.out.println();
        System.out.println("Data result size: " + cheapestRecords.size());
        System.out.println("Elapsed time: " + (System.currentTimeMillis() - start) + " ms");

        String filePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss")) + ".csv";
        writeRecordsToCSVFile(filePath, cheapestRecords, config.getDelimiter());

        System.out.println("Output file: " + new File(filePath).getAbsolutePath());
    }

    private static void writeRecordsToCSVFile(String filePath, List<Record> data, char delimiter) {
        CSVWriter writer = new CSVWriter(filePath, data.stream().map(Record::toRaw).collect(toList()), delimiter);

        try {
            writer.write();
        } catch (IOException e) {
            throw new RuntimeException("See cause.", e);
        }
    }

    private static List<Path> getCSVFiles(String directoryPath) {
        try (Stream<Path> files = Files.walk(Paths.get(directoryPath), 1)) {
            return files.filter(path -> Files.isRegularFile(path)).filter(file -> file.toString().endsWith(".csv")).collect(toList());
        } catch (IOException e) {
            throw new RuntimeException("See cause.", e);
        }
    }
}
