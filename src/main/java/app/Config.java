package app;

public final class Config {

    private final String directoryPath;

    private char delimiter = ',';

    private int recordsResultSize = 1000;

    private int recordsMaxCountWithSameProduct = 20;

    public Config(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public String getDirectoryPath() {
        return directoryPath;
    }

    public char getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(char delimiter) {
        this.delimiter = delimiter;
    }

    public int getRecordsResultSize() {
        return recordsResultSize;
    }

    public void setRecordsResultSize(int recordsResultSize) {
        this.recordsResultSize = recordsResultSize;
    }

    public int getRecordsMaxCountWithSameProduct() {
        return recordsMaxCountWithSameProduct;
    }

    public void setRecordsMaxCountWithSameProduct(int recordsMaxCountWithSameProduct) {
        this.recordsMaxCountWithSameProduct = recordsMaxCountWithSameProduct;
    }
}
