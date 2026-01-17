package support.masking;

public class MaskedString {
    private final String value;

    public MaskedString(String value) {
        this.value = value;
    }

    public String get() {
        return value;
    }

    public int getAsInt() {
        return Integer.parseInt(value);
    }

    @Override
    public String toString() {
        return "****";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MaskedString masked) {
            return value.equals(masked.value);
        }
        if (obj instanceof String str) {
            return value.equals(str);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    public boolean isEmpty() {
        return value.isEmpty();
    }
}
