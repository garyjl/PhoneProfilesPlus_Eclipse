package sk.henrichg.phoneprofilesplus;

public class Contact {
    private String name = "";
    private boolean checked = false;

    public Contact() {
    }

    public Contact(String name) {
        this.name = name;
    }

    public Contact(String name, boolean checked) {
        this.name = name;
        this.checked = checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String toString() {
        return name;
    }

    public void toggleChecked() {
        checked = !checked;
    }
}