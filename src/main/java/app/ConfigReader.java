package app;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public final class ConfigReader {

    private Config config;

    public ConfigReader(String configPath) throws IOException {
        readFromConfigFile(configPath);
    }

    private void readFromConfigFile(String configPath) throws IOException {
        Properties props = new Properties();
        props.load(new FileReader(configPath));

        String directoryPath = props.getProperty("directoryPath");
        String delimiter = props.getProperty("delimiter");
        String resultSize = props.getProperty("resultSize");
        String recordsMaxCountWithSameProduct = props.getProperty("recordsMaxCountWithSameProduct");

        if (directoryPath != null) {
            config = new Config(directoryPath);
        } else {
            throw new IllegalArgumentException("'directoryPath' is required");
        }

        if (delimiter != null) {
            if (delimiter.length() == 1) {
                config.setDelimiter(delimiter.charAt(0));
            } else {
                throw new IllegalArgumentException("'delimiter' must have one character");
            }
        }

        if (resultSize != null) {
            try {
                config.setRecordsResultSize(Integer.parseInt(resultSize));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("'resultSize' must be Integer");
            }
        }

        if (recordsMaxCountWithSameProduct != null) {
            try {
                config.setRecordsMaxCountWithSameProduct(Integer.parseInt(recordsMaxCountWithSameProduct));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("'recordsCountWithSameId' must be Integer");
            }
        }
    }

    public Config getConfig() {
        return config;
    }
}
