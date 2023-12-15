package unipi.it.mircv.indexing.dataStructures;

public class Doc {
    private int id;
    private String[] text;

    public Doc(int id, String[] text) {
        this.id = id;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public String[] getText() {
        return text;
    }

    @Override
    public String toString() {
        return this.id + "    " + String.join(", ", this.text);
    }
}
