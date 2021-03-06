package bsu.famcs.chat.storage.xml;

import bsu.famcs.chat.model.Message;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class XMLHistoryUtil {
    private static final String STORAGE_LOCATION = System.getProperty("user.home") +  File.separator + "history.xml";
    private static final String STORAGE_FILE_LOCATION = "file:///" + STORAGE_LOCATION;
    private static final String MESSAGES = "messages";
    private static final String MESSAGE = "message";
    private static final String ID = "id";
    private static final String AUTHOR = "author";
    private static final String TEXT = "text";
    private static final String STATUS = "status";
    private static final String DATE = "date";
    private static final String DATE_PATTERN = "dd-MM-yyyy HH:mm";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN);
    private static Logger logger = Logger.getLogger(XMLHistoryUtil.class);

    private XMLHistoryUtil() {
    }

    public static synchronized void createStorage() throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement(MESSAGES);
        doc.appendChild(rootElement);

        Transformer transformer = getTransformer();

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(STORAGE_LOCATION));
        transformer.transform(source, result);
    }

    public static synchronized void addData(Message message) throws ParserConfigurationException, SAXException, IOException, TransformerException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(STORAGE_FILE_LOCATION);
        document.getDocumentElement().normalize();

        Element root = document.getDocumentElement();

        Element messageElement = document.createElement(MESSAGE);
        root.appendChild(messageElement);

        messageElement.setAttribute(ID, message.getId());

        Element author = document.createElement(AUTHOR);
        author.appendChild(document.createTextNode(message.getAuthor()));
        messageElement.appendChild(author);

        Element text = document.createElement(TEXT);
        text.appendChild(document.createTextNode(message.getText()));
        messageElement.appendChild(text);

        Element status = document.createElement(STATUS);
        status.appendChild(document.createTextNode((new Long(message.getStatus())).toString()));
        messageElement.appendChild(status);

        Element date = document.createElement(DATE);
        date.appendChild(document.createTextNode(DATE_FORMAT.format(new Date())));
        messageElement.appendChild(date);

        DOMSource source = new DOMSource(document);

        Transformer transformer = getTransformer();

        StreamResult result = new StreamResult(STORAGE_FILE_LOCATION);
        transformer.transform(source, result);
    }

    public static synchronized void updateData(Message message) throws ParserConfigurationException, SAXException, IOException, TransformerException, XPathExpressionException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(new File(STORAGE_LOCATION));

        document.getDocumentElement().normalize();
        Node taskToUpdate = getNodeById(document, message.getId());

        if (taskToUpdate != null) {

            NodeList childNodes = taskToUpdate.getChildNodes();

            for (int i = 0; i < childNodes.getLength(); i++) {

                Node node = childNodes.item(i);

                if (AUTHOR.equals(node.getNodeName()) && message.getAuthor() != null) {
                    node.setTextContent(message.getAuthor());
                }

                if (TEXT.equals(node.getNodeName())) {
                    node.setTextContent(message.getText());
                }

                if (STATUS.equals(node.getNodeName())) {
                    node.setTextContent((new Long(message.getStatus())).toString());
                }

                if(DATE.equals(node.getNodeName())) {
                    node.setTextContent(DATE_FORMAT.format(new Date()));
                }

            }

            Transformer transformer = getTransformer();

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(STORAGE_LOCATION));
            transformer.transform(source, result);
        } else {
            throw new NullPointerException();
        }
    }

    public static synchronized boolean doesStorageExist() {
        File file = new File(STORAGE_LOCATION);
        return file.exists();
    }

    public static synchronized List<Message> getMessages() throws SAXException, IOException, ParserConfigurationException {
        return getSubMessagesByIndex(0);
    }

    public static synchronized List<Message> getSubMessagesByIndex(int index) throws ParserConfigurationException, SAXException, IOException {
        List<Message> messages = new ArrayList<>();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(STORAGE_FILE_LOCATION);
        document.getDocumentElement().normalize();
        Element root = document.getDocumentElement();
        NodeList messageList = root.getElementsByTagName(MESSAGE);
        for (int i = index; i < messageList.getLength(); i++) {
            Element messageElement = (Element)messageList.item(i);
            String author = messageElement.getElementsByTagName(AUTHOR).item(0).getTextContent();
            String text = messageElement.getElementsByTagName(TEXT).item(0).getTextContent();
            String id = messageElement.getAttribute(ID);
            String statusCode = messageElement.getElementsByTagName(STATUS).item(0).getTextContent();
            int status = Integer.valueOf(statusCode);
            messages.add(new Message(author, text, id, status));
        }
        return messages;
    }

    public static synchronized int getStorageSize() throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(STORAGE_FILE_LOCATION);
        document.getDocumentElement().normalize();
        Element root = document.getDocumentElement();
        return root.getElementsByTagName(MESSAGE).getLength();
    }

    private static Node getNodeById(Document doc, String id) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = xpath.compile("//" + MESSAGE + "[@id='" + id + "']");
        return (Node) expr.evaluate(doc, XPathConstants.NODE);
    }

    private static Transformer getTransformer() throws TransformerConfigurationException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        return transformer;
    }
}
