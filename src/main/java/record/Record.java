package record;

import java.util.List;
import java.util.Objects;

public final class Record {

    private final int productId;

    private final String name;

    private final String condition;

    private final String state;

    private final double price;

    public Record(int productId, String name, String condition, String state, double price) {
        this.productId = productId;
        this.name = name;
        this.condition = condition;
        this.state = state;
        this.price = price;
    }

    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getCondition() {
        return condition;
    }

    public String getState() {
        return state;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Record record = (Record) o;
        return productId == record.productId
                && Double.compare(record.price, price) == 0
                && Objects.equals(name, record.name)
                && Objects.equals(condition, record.condition)
                && Objects.equals(state, record.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, name, condition, state, price);
    }

    /**
     * @throws IllegalArgumentException if data is incorrect
     */
    public static Record fromRaw(String[] data) {
        if (data.length != 5) {
            throw new IllegalArgumentException("data length must be 5");
        }
        int productId;
        try {
            productId = Integer.parseInt(data[0]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(e);
        }
        String name = data[1];
        String condition = data[2];
        String state = data[3];
        double price;
        try {
            price = Double.parseDouble(data[4]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(e);
        }

        return new Record(productId, name, condition, state, price);
    }

    public static Record[] fromRaw(List<String[]> data) {
        Record[] records = new Record[data.size()];
        for (int i = 0; i < data.size(); i++) {
            records[i] = fromRaw(data.get(i));
        }
        return records;
    }

    public String[] toRaw() {
        String[] data = new String[5];
        data[0] = String.valueOf(this.productId);
        data[1] = this.name;
        data[2] = this.condition;
        data[3] = this.state;
        data[4] = String.valueOf(this.price);
        return data;
    }
}
