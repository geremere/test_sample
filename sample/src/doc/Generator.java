package doc;


import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Generator {
    ArrayList<ArrayList<String>> data;
    Page page;

    public Generator(ArrayList<ArrayList<String>> data, Page page) {
        this.data = data;
        this.page = page;
    }

    private void printEmptyRow(int index, FileWriter wr) throws IOException {
        for (int j = 0; j < page.header[index].width; j++) {
            wr.write(" ");
        }
        wr.write(" | ");

    }

    private void printSeparator(FileWriter wr) throws IOException {
        wr.write("\n");
        for (int j = 0; j < page.page_width; j++) {
            wr.write("-");
        }
        wr.write("\n");
    }

    private boolean checkBuf(String[] buf) {
        for (int i = 0; i < buf.length; i++) {
            if (buf[i] != null)
                return true;
        }
        return false;
    }

    private void printVerticalSeparator(FileWriter wr, int i) throws IOException {
        if (i == page.header.length - 1)
            wr.write(" |");
        else
            wr.write(" | ");
    }

    private int printFromBuffer(String[] buf, FileWriter wr, int height) throws IOException {
        while (checkBuf(buf)) {
            wr.write("\n");
            for (int j = 0; j < buf.length; j++) {
                if (j == 0)
                    wr.write("| ");

                if (buf[j] == null) {
                    printEmptyRow(j, wr);
                    wr.flush();
                } else {
                    buf = printLongWord(buf[j], j, wr, buf);
                    buf[j] = buf[j] == "" ? null : buf[j];
                    wr.flush();
                }

            }
            height++;
            if (height >= page.page_height) {
                height-=page.page_height;
                wr.write("~\n");
                printHeader(wr);
            }
        }
        return height;
    }

    private String[] printLongWord(String item, int i, FileWriter wr, String[] buf) throws IOException {
        String[] split_item = item.split(GetSeparator(item));
        int cursize = 0;
        String line = "";
        for (int j = 0; j < split_item.length; j++) {
            if (split_item[j].length() <= page.header[i].width - cursize) {
                for (int s = 0; s < split_item[j].length(); s++) {
                    wr.write(split_item[j].charAt(s));
                    cursize++;
                }
                if (cursize < page.header[i].width && j != split_item.length - 1) {
                    wr.write(GetSeparator(item));
                    cursize++;
                }
            } else if (split_item[j].length() < page.header[i].width || cursize >= page.header[i].width) {
                line += split_item[j];
                if (j != split_item.length - 1)
                    line += " ";
            } else {
                for (int s = 0; s < split_item[j].length(); s++) {
                    if (cursize++ < page.header[i].width)
                        wr.write(split_item[j].charAt(s));
                    else
                        line += split_item[j].charAt(s);
                }
                if (j != split_item.length - 1)
                    line += " ";
            }
        }
        for (int j = cursize; j < page.header[i].width; j++) {
            wr.write(" ");
        }
        buf[i] = line;
        printVerticalSeparator(wr, i);
        wr.flush();
        return buf;
    }

    private String GetSeparator(String item) {
        return item.split(" ").length == 1 ? "/" : " ";
    }

    private int printRow(ArrayList<String> row, FileWriter wr, int currentHeight) throws IOException {
        String[] buf = new String[row.size()];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = null;
        }
        wr.write("| ");
        int height = 0;
        int i = 0;
        for (String item : row) {
            if (item.length() < page.header[i].width) {
                for (int j = 0; j < page.header[i].width; j++) {
                    if (j < item.length())
                        wr.write(item.charAt(j));
                    else
                        wr.write(" ");
                }
                printVerticalSeparator(wr, i);
                height += 2;
            } else {
                buf = printLongWord(item, i, wr, buf);
                height++;
                if (height >= page.page_height) {
                    wr.write("~\n");
                    printHeader(wr);
                    height-=page.page_height;
                }
            }
            i++;
        }
        wr.flush();
        height = printFromBuffer(buf, wr, height);
        wr.flush();

        printSeparator(wr);
        return height + currentHeight;
    }

    private void printHeader(FileWriter wr) throws IOException {
        wr.write("| ");
        for (int i = 0; i < page.header.length; i++) {
            for (int j = 0; j < page.header[i].width; j++) {
                if (j < page.header[i].name.length())
                    wr.write(page.header[i].name.charAt(j));
                else
                    wr.write(" ");
            }
            printVerticalSeparator(wr, i);
        }
        printSeparator(wr);
    }

    public void generateDoc() {
        try {
            FileWriter wr = new FileWriter("report.txt");
            int currentHeight = 2;
            printHeader(wr);
            for (ArrayList<String> row : data) {
                if (currentHeight >= page.page_height) {
                    wr.write("~\n");
                    currentHeight-=page.page_height;
                    printHeader(wr);
                    currentHeight += 2;
                } else {
                    currentHeight = printRow(row, wr, currentHeight);
                }
            }
            wr.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
