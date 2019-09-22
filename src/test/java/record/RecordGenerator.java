package record;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class RecordGenerator {

    private static final String[] BEGINNING = {"Kr", "Ca", "Ra", "Mrok", "Cru",
            "Ray", "Bre", "Zed", "Drak", "Mor", "Jag", "Mer", "Jar", "Mjol",
            "Zork", "Mad", "Cry", "Zur", "Creo", "Azak", "Azur", "Rei", "Cro",
            "Mar", "Luk"};
    private static final String[] MIDDLE = {"air", "ir", "mi", "sor", "mee", "clo",
            "red", "cra", "ark", "arc", "miri", "lori", "cres", "mur", "zer",
            "marac", "zoir", "slamar", "salmar", "urak"};
    private static final String[] END = {"d", "ed", "ark", "arc", "es", "er", "der",
            "tron", "med", "ure", "zur", "cred", "mur"};

    private static final double MIN_PRICE = 1;
    private static final double MAX_PRICE = 1000;

    private final int rowsCount;
    private final Random rand = new Random();

    public RecordGenerator(int count) {
        this.rowsCount = count;
    }

    public List<Record> generate() {
        List<Record> records = new ArrayList<>();

        for (int i = 0; i < rowsCount; i++) {
            records.add(new Record(generateId(), generateName(), "Condition", "State", generatePrice()));
        }

        return records;
    }

    private int generateId() {
        return 1 + (int) (Math.random() * (rowsCount / 5));
    }

    private String generateName() {
        return BEGINNING[rand.nextInt(BEGINNING.length)] +
                MIDDLE[rand.nextInt(MIDDLE.length)] +
                END[rand.nextInt(END.length)];
    }

    private double generatePrice() {
        double random = new Random().nextDouble();
        return ((int) ((MIN_PRICE + (random * (MAX_PRICE - MIN_PRICE))) * 1000)) / 1000D;
    }
}
