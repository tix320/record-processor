package csv;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public final class CSVWriter {

    private final String filePath;

    private final List<String[]> data;

    private final char delimiter;

    public CSVWriter(String filePath, List<String[]> data, char delimiter) {
        this.filePath = filePath;
        this.data = data;
        this.delimiter = delimiter;
    }

    public void write() throws IOException {
        File csvOutputFile = new File(filePath);
        if (csvOutputFile.exists()) {
            throw new IllegalStateException("File already exists: " + filePath);
        }
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            for (String[] line : data) {
                pw.println(String.join(String.valueOf(delimiter), line));
            }
        }
    }
}
