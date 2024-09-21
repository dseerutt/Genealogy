package Genealogy.GUI;

public class ComboItem {
    private String value;
    private String label;

    public ComboItem(String label, String value) {
        this.value = value;
        this.label = label;
    }

    public String getValue() {
        return this.value;
    }

    public String getLabel() {
        return this.label;
    }

    @Override
    public String toString() {
        return label;
    }
}
