package com.company;

import doc.Generator;
import doc.Page;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Main {

    public static ArrayList<ArrayList<String>> tsvReader(String path) {
        ArrayList<ArrayList<String>> data = new ArrayList<>();
        StringTokenizer st;
        BufferedReader TSVFile = null;
        try {
            TSVFile = new BufferedReader(new InputStreamReader
                    (new FileInputStream(path), "UTF-16"));
            String dataRow = TSVFile.readLine(); // Read first line.
            while (dataRow != null) {
                st = new StringTokenizer(dataRow, "\t");
                List<String> dataArray = new ArrayList<String>();
                while (st.hasMoreElements()) {
                    dataArray.add(st.nextElement().toString());
                }
                ArrayList<String> row = new ArrayList<>();

                for (String item : dataArray) {
                    row.add(item);
                }
                data.add(row);

                dataRow = TSVFile.readLine();
            }
            TSVFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static Page xmlReader(String path) {
        String[] header = null;
        int[] column_width = null;
        int page_width = 0;
        int page_height = 0;
        final File xmlFile = new File(path);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = (Document) db.parse(xmlFile);
            doc.getDocumentElement().normalize();
            NodeList nodelist = doc.getElementsByTagName("page");
            for (int i = 0; i < nodelist.getLength(); i++) {
                Node node = nodelist.item(i);
                if (Node.ELEMENT_NODE == node.getNodeType()) {
                    Element element = (Element) node;
                    page_width = Integer.parseInt(element.getElementsByTagName("width").item(0).getTextContent());
                    page_height = Integer.parseInt(element.getElementsByTagName("height").item(0).getTextContent());
                }
            }
            nodelist = doc.getElementsByTagName("column");
            header = new String[nodelist.getLength()];
            column_width = new int[nodelist.getLength()];
            for (int i = 0; i < nodelist.getLength(); i++) {
                Node node = nodelist.item(i);
                if (Node.ELEMENT_NODE == node.getNodeType()) {
                    Element element = (Element) node;
                    header[i] = element.getElementsByTagName("title").item(0).getTextContent();
                    column_width[i] = Integer.parseInt(element.getElementsByTagName("width").item(0).getTextContent());
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Page(header, column_width, page_height, page_width);
    }

    public static void main(String[] args) {
        Page page = xmlReader("settings.xml");//put here your path
        ArrayList<ArrayList<String>> data = tsvReader("source-data.tsv");//and here too
        Generator genarator = new Generator(data,page);
        genarator.generateDoc();
    }
}
