package no.utgdev.nfpa.utils;

import no.utgdev.nfpa.model.Division;
import no.utgdev.nfpa.model.DivisionOption;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class CodesReader {
    public static List<Division> readFromFile(File file) {

        try {
            Document document = getDocument(file);
            XPath xpath = XPathFactory.newInstance().newXPath();
            XPathExpression divisionExpression = xpath.compile("//divisions/division");

            NodeList divisionNodes = ((NodeList) divisionExpression.evaluate(document, XPathConstants.NODESET));

            return processDivisionNodes(divisionNodes, xpath);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static List<Division> processDivisionNodes(NodeList divisionNodes, XPath xpath) throws XPathExpressionException {
        List<Division> divisionList = new ArrayList<>();

        for (int i = 0; i < divisionNodes.getLength(); i++) {
            divisionList.add(processDivisionNode(divisionNodes.item(i), xpath));
        }

        return unmodifiableList(divisionList);

    }

    private static Division processDivisionNode(Node divisionNode, XPath xpath) throws XPathExpressionException {
        XPathExpression optionsExpression = xpath.compile(".//options");
        XPathExpression optionExpression = xpath.compile(".//option");
        Node optionsNode = ((Node) optionsExpression.evaluate(divisionNode, XPathConstants.NODE));

        NodeList optionNodes = ((NodeList) optionExpression.evaluate(optionsNode, XPathConstants.NODESET));

        List<DivisionOption> options = processOptionsNodes(optionNodes);

        XPathExpression nameExpression = xpath.compile(".//name");
        XPathExpression descriptionExpression = xpath.compile(".//description");
        XPathExpression xExpression = xpath.compile(".//diagram-position/x");
        XPathExpression yExpression = xpath.compile(".//diagram-position/y");

        String name = ((Node) nameExpression.evaluate(divisionNode, XPathConstants.NODE)).getTextContent();
        String description = ((Node) descriptionExpression.evaluate(divisionNode, XPathConstants.NODE)).getTextContent();
        String xText = ((Node) xExpression.evaluate(divisionNode, XPathConstants.NODE)).getTextContent();
        String yText = ((Node) yExpression.evaluate(divisionNode, XPathConstants.NODE)).getTextContent();

        double x = toDouble(xText);
        double y = toDouble(yText);

        return new Division(name, description, options, x, y);
    }

    private static List<DivisionOption> processOptionsNodes(NodeList optionsNodes) {
        List<DivisionOption> divisionList = new ArrayList<>();

        for (int i = 0; i < optionsNodes.getLength(); i++) {
            divisionList.add(processOptionNode(optionsNodes.item(i)));
        }

        return unmodifiableList(divisionList);
    }

    private static DivisionOption processOptionNode(Node item) {
        return new DivisionOption(
                item.getAttributes().getNamedItem("code").getTextContent(),
                item.getTextContent()
        );
    }

    private static Document getDocument(File file) {
        try {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            return builder.parse(file);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static double toDouble(String text) {
        try {
            return Double.valueOf(text);
        } catch (Exception e) {
            return 0;
        }
    }
}
