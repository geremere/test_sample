package doc;

public class Page {
    int page_width;
    int page_height;
    Column[] header;

    public Page(String[] header, int[] column_width, int page_height, int page_width) {
        this.page_height = page_height;
        this.page_width = page_width;
        this.header = new Column[header.length];
        for (int i = 0; i < header.length; i++) {
            this.header[i] = new Column(header[i], column_width[i]);
        }
    }

    class Column {
        String name;
        int width;

        private Column(String name, int width) {
            this.name = name;
            this.width = width;
        }
    }
}
