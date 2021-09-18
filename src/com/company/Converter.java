package com.company;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Converter {

    public static void main(String[] args) throws IOException {
        Scanner input = new Scanner(new File("audit.audit"));
        String xmlFilePath = "audit.xml";

        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();

            List<String> lines = new ArrayList<>();
            List<String> result = new ArrayList<>();

            while (input.hasNextLine()) {
                String currentLine = input.nextLine();
                if(!currentLine.trim().isEmpty()){
                    lines.add(currentLine);
                }
            }

            boolean inside = false;
            for (String el : lines){
                if (el.contains("<custom_item>")){
                    inside = true;
                    result.add(el);
                    continue;
                }
                if (inside) {
                    if (el.charAt(0) == ' ' && el.charAt(1) == ' '){
                        result.add(el);
                    }
                }
                if (el.contains("</custom_item>") ){
                    inside = false;
                }
            }

            Element root = document.createElement("audit");
            document.appendChild(root);
            Element custom_item = null;

            for (String line : result) {
                if (line.contains("custom_item") && !line.contains("/")) {
                    custom_item = document.createElement("custom_item");
                    root.appendChild(custom_item);
                }
                if (!line.trim().isEmpty() && !line.contains("custom_item")) {
                    int index = line.indexOf(":");
                    String type = line.substring(0, index).trim();
                    String contains = line.substring(index + 1).trim();
                    Element field = document.createElement(type);
                    custom_item.appendChild(field);
                    field.appendChild(document.createTextNode(contains));
                }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(xmlFilePath));
            transformer.transform(domSource, streamResult);

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }
}
