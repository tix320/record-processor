package csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public final class CSVReader {

    private final BufferedReader reader;

    private final char delimiter;

    private final int rowsCount;

    private final int columnsCount;

    private String[] lastReadRow;

    public CSVReader(Reader reader, char delimiter, int rowsCount, int columnsCount) {
        this.reader = new BufferedReader(reader);
        this.delimiter = delimiter;
        this.rowsCount = rowsCount;
        this.columnsCount = columnsCount;
        this.lastReadRow = null;
    }

    public boolean hasNext() {
        if (lastReadRow != null) {
            return true;
        } else {
            String[] row = readRow();
            lastReadRow = row;
            return row != null;
        }
    }

    public List<String[]> next() {
        List<String[]> buffer = new ArrayList<>(rowsCount + 1);

        if (lastReadRow != null) {
            String[] temp = this.lastReadRow;
            lastReadRow = null;
            buffer.add(temp);

            readRows(buffer, rowsCount - 1);
        } else {
            readRows(buffer, rowsCount);
        }
        return buffer;
    }

    private String[] readRow() {
        String row;
        try {
            row = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (row == null) {
            return null;
        }

        // splitting was wrote manually, because split() is very slow
        String[] data = new String[columnsCount];

        int previousIndex = -1;
        for (int i = 0; i < columnsCount; i++) {
            int currentIndex = row.indexOf(delimiter, previousIndex + 1);
            String substring = row.substring(previousIndex + 1, currentIndex == -1 ? row.length() : currentIndex);
            data[i] = substring;
            previousIndex = currentIndex;
        }

        return data;
    }

    private void readRows(List<String[]> buffer, int count) {
        for (int i = 0; i < count; i++) {
            String[] row = readRow();
            if (row == null) {
                if (buffer.isEmpty()) {
                    throw new NoSuchElementException();
                } else {
                    return;
                }
            }
            buffer.add(row);
        }
    }
}
