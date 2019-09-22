package record;

import app.Config;
import csv.CSVReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class RecordProcessor {

    private static final Comparator<Record> PRICE_ASCENDING_COMPARATOR = Comparator.comparingDouble(Record::getPrice);
    private static final Comparator<Record> PRICE_DESCENDING_COMPARATOR = Comparator.comparingDouble(Record::getPrice).reversed();

    private final List<Path> csvFiles;

    private final char delimiter;

    private final int recordsResultSize;

    private final int recordsMaxCountWithSameProduct;

    private Consumer<Long> progressHandler = (ignored) -> {};

    public RecordProcessor(List<Path> csvFiles, Config config) {
        if (csvFiles.isEmpty()) {
            throw new IllegalArgumentException("Must be least one file");
        }
        this.csvFiles = csvFiles;
        this.delimiter = config.getDelimiter();
        this.recordsResultSize = config.getRecordsResultSize();
        this.recordsMaxCountWithSameProduct = config.getRecordsMaxCountWithSameProduct();
    }

    public void setProgressHandler(Consumer<Long> handler) {
        this.progressHandler = handler;
    }

    public List<Record> getCheapestRecords() {
        ExecutorService processors = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        BlockingQueue<Record[]> processedRecords = new LinkedBlockingQueue<>();

        int actionsCount = csvFiles.size() + 1;
        CountDownLatch processedActions = new CountDownLatch(actionsCount);
        csvFiles.forEach(path -> processors.submit(() -> {
            try {
                CSVReader parser;
                try {
                    parser = new CSVReader(new FileReader(path.toString()), delimiter, recordsResultSize, 5);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

                Deque<Record[]> recordsQueue = new LinkedList<>();

                while (true) {
                    if (recordsQueue.size() > 1) {
                        Record[] records1 = recordsQueue.removeFirst();
                        Record[] records2 = recordsQueue.removeFirst();
                        Record[] mergedArray = mergeArrays(records1, records2);
                        recordsQueue.addLast(mergedArray);
                    } else if (parser.hasNext()) {
                        List<String[]> rows = parser.next();
                        Record[] records = Record.fromRaw(rows);
                        Arrays.sort(records, Comparator.nullsFirst(PRICE_DESCENDING_COMPARATOR));
                        recordsQueue.addLast(records);
                    } else {
                        break;
                    }
                }

                processedRecords.put(recordsQueue.removeFirst());

                processedActions.countDown();
                progressHandler.accept((actionsCount - processedActions.getCount()) * 100 / actionsCount);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }));

        Thread merger = new Thread(() -> {
            try {
                int mergesCount = csvFiles.size() - 1;
                for (int i = 0; i < mergesCount; i++) {
                    Record[] records1 = processedRecords.take();
                    Record[] records2 = processedRecords.take();

                    Record[] mergedArray = mergeArrays(records1, records2);
                    processedRecords.put(mergedArray);
                }
                processedActions.countDown();
                progressHandler.accept((actionsCount - processedActions.getCount()) * 100 / actionsCount);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });

        merger.start();

        try {
            processedActions.await();
            processors.shutdown();
            Record[] result = processedRecords.take();
            return Arrays.stream(result).filter(Objects::nonNull).sorted(PRICE_ASCENDING_COMPARATOR).collect(Collectors.toList());
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    private Record[] mergeArrays(Record[] firstArray, Record[] secondArray) {
        Map<Integer, Integer> productsCounter = new HashMap<>();
        Record[] result = new Record[Math.min(firstArray.length + secondArray.length, recordsResultSize)];

        int firstIndex = firstArray.length - 1;
        int secondIndex = secondArray.length - 1;
        int insertionIndex = result.length - 1;
        int maxCount = this.recordsMaxCountWithSameProduct;

        while (insertionIndex >= 0) {
            if (firstIndex >= 0 && firstArray[firstIndex] != null) {
                if (secondIndex >= 0 && secondArray[secondIndex] != null) {

                    Record firstRecord = firstArray[firstIndex];
                    Record secondRecord = secondArray[secondIndex];
                    if (PRICE_DESCENDING_COMPARATOR.compare(firstRecord, secondRecord) > 0) {
                        int productId = firstRecord.getProductId();
                        Integer count = productsCounter.computeIfAbsent(productId, key -> 0);
                        if (count < maxCount) {
                            result[insertionIndex--] = firstRecord;
                            productsCounter.put(productId, count + 1);
                        }
                        firstIndex--;
                    } else {
                        int productId = secondRecord.getProductId();
                        Integer count = productsCounter.computeIfAbsent(productId, key -> 0);
                        if (count < maxCount) {
                            result[insertionIndex--] = secondRecord;
                            productsCounter.put(productId, count + 1);
                        }
                        secondIndex--;
                    }
                } else {
                    for (int i = firstIndex; i >= 0 && firstArray[i] != null; i--) {
                        Record record = firstArray[i];
                        int productId = record.getProductId();
                        Integer count = productsCounter.computeIfAbsent(productId, key -> 0);
                        if (count < maxCount) {
                            result[insertionIndex--] = record;
                            productsCounter.put(productId, count + 1);
                        }
                    }
                    break;
                }
            } else {
                if (secondIndex >= 0 && secondArray[secondIndex] != null) {
                    for (int i = secondIndex; i >= 0 && secondArray[i] != null; i--) {
                        Record record = secondArray[i];
                        int productId = record.getProductId();
                        Integer count = productsCounter.computeIfAbsent(productId, key -> 0);
                        if (count < maxCount) {
                            result[insertionIndex--] = record;
                            productsCounter.put(productId, count + 1);
                        }
                    }
                }
                break;
            }
        }

        return result;
    }
}
