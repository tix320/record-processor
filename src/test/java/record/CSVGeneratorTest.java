package record;

import csv.CSVWriter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

class CSVGeneratorTest {

    private static final String directoryPath = "your/path/here";

    private static final int FILES_COUNT = 100;

    private static final int ROWS_COUNT = 1000000;

    public static void main(String[] args) throws IOException {
        RecordGenerator recordGenerator = new RecordGenerator(ROWS_COUNT);

        for (int i = 1; i <= FILES_COUNT; i++) {
            List<Record> generatedData = recordGenerator.generate();
            List<String[]> rawData = generatedData.stream().map(Record::toRaw).collect(Collectors.toList());

            String filePath = directoryPath + i + ".csv";
            CSVWriter csvWriter = new CSVWriter(filePath, rawData, ',');
            csvWriter.write();
            System.out.println("Generated: " + filePath);
        }
    }
}
