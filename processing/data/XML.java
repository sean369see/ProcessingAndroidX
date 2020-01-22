package processing.data;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import processing.core.PApplet;

public class XML implements Serializable {
    protected Node node;
    protected XML parent;
    protected XML[] children;

    protected XML() {
    }

    public XML(File file) throws IOException, ParserConfigurationException, SAXException {
        this((File)file, (String)null);
    }

    public XML(File file, String options) throws IOException, ParserConfigurationException, SAXException {
        this((Reader)PApplet.createReader(file), (String)options);
    }

    public XML(InputStream input) throws IOException, ParserConfigurationException, SAXException {
        this((InputStream)input, (String)null);
    }

    public XML(InputStream input, String options) throws IOException, ParserConfigurationException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            factory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (IllegalArgumentException var6) {
        }

        factory.setExpandEntityReferences(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(input));
        this.node = document.getDocumentElement();
    }

    public XML(Reader reader) throws IOException, ParserConfigurationException, SAXException {
        this((Reader)reader, (String)null);
    }

    public XML(final Reader reader, String options) throws IOException, ParserConfigurationException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            factory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (IllegalArgumentException var6) {
        }

        factory.setExpandEntityReferences(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new Reader() {
            public int read(char[] cbuf, int off, int len) throws IOException {
                int count = reader.read(cbuf, off, len);

                for(int i = 0; i < count; ++i) {
                    if (cbuf[off + i] == 8232) {
                        cbuf[off + i] = '\n';
                    }
                }

                return count;
            }

            public void close() throws IOException {
                reader.close();
            }
        }));
        this.node = document.getDocumentElement();
    }

    public XML(String name) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            this.node = document.createElement(name);
            this.parent = null;
        } catch (ParserConfigurationException var5) {
            throw new RuntimeException(var5);
        }
    }

    protected XML(XML parent, Node node) {
        this.node = node;
        this.parent = parent;
        String[] var3 = parent.listAttributes();
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            String attr = var3[var5];
            if (attr.startsWith("xmlns") && node instanceof Element) {
                this.setString(attr, parent.getString(attr));
            }
        }

    }

    public static XML parse(String data) throws IOException, ParserConfigurationException, SAXException {
        return parse(data, (String)null);
    }

    public static XML parse(String data, String options) throws IOException, ParserConfigurationException, SAXException {
        return new XML(new StringReader(data), (String)null);
    }

    public boolean save(File file) {
        return this.save(file, (String)null);
    }

    public boolean save(File file, String options) {
        PrintWriter writer = PApplet.createWriter(file);
        boolean result = this.write(writer);
        writer.flush();
        writer.close();
        return result;
    }

    public boolean write(PrintWriter output) {
        output.print(this.format(2));
        output.flush();
        return true;
    }

    public XML getParent() {
        return this.parent;
    }

    protected Object getNative() {
        return this.node;
    }

    public String getName() {
        return this.node.getNodeName();
    }

    public void setName(String newName) {
        Document document = this.node.getOwnerDocument();
        this.node = document.renameNode(this.node, (String)null, newName);
    }

    public String getLocalName() {
        return this.node.getLocalName();
    }

    protected void checkChildren() {
        if (this.children == null) {
            NodeList kids = this.node.getChildNodes();
            int childCount = kids.getLength();
            this.children = new XML[childCount];

            for(int i = 0; i < childCount; ++i) {
                this.children[i] = new XML(this, kids.item(i));
            }
        }

    }

    public int getChildCount() {
        this.checkChildren();
        return this.children.length;
    }

    public boolean hasChildren() {
        this.checkChildren();
        return this.children.length > 0;
    }

    public String[] listChildren() {
        this.checkChildren();
        String[] outgoing = new String[this.children.length];

        for(int i = 0; i < this.children.length; ++i) {
            outgoing[i] = this.children[i].getName();
        }

        return outgoing;
    }

    public XML[] getChildren() {
        this.checkChildren();
        return this.children;
    }

    public XML getChild(int index) {
        this.checkChildren();
        return this.children[index];
    }

    public XML getChild(String name) {
        if (name.length() > 0 && name.charAt(0) == '/') {
            throw new IllegalArgumentException("getChild() should not begin with a slash");
        } else if (name.indexOf(47) != -1) {
            return this.getChildRecursive(PApplet.split(name, '/'), 0);
        } else {
            int childCount = this.getChildCount();

            for(int i = 0; i < childCount; ++i) {
                XML kid = this.getChild(i);
                String kidName = kid.getName();
                if (kidName != null && kidName.equals(name)) {
                    return kid;
                }
            }

            return null;
        }
    }

    protected XML getChildRecursive(String[] items, int offset) {
        if (Character.isDigit(items[offset].charAt(0))) {
            XML kid = this.getChild(Integer.parseInt(items[offset]));
            return offset == items.length - 1 ? kid : kid.getChildRecursive(items, offset + 1);
        } else {
            int childCount = this.getChildCount();

            for(int i = 0; i < childCount; ++i) {
                XML kid = this.getChild(i);
                String kidName = kid.getName();
                if (kidName != null && kidName.equals(items[offset])) {
                    if (offset == items.length - 1) {
                        return kid;
                    }

                    return kid.getChildRecursive(items, offset + 1);
                }
            }

            return null;
        }
    }

    public XML[] getChildren(String name) {
        if (name.length() > 0 && name.charAt(0) == '/') {
            throw new IllegalArgumentException("getChildren() should not begin with a slash");
        } else if (name.indexOf(47) != -1) {
            return this.getChildrenRecursive(PApplet.split(name, '/'), 0);
        } else if (Character.isDigit(name.charAt(0))) {
            return new XML[]{this.getChild(Integer.parseInt(name))};
        } else {
            int childCount = this.getChildCount();
            XML[] matches = new XML[childCount];
            int matchCount = 0;

            for(int i = 0; i < childCount; ++i) {
                XML kid = this.getChild(i);
                String kidName = kid.getName();
                if (kidName != null && kidName.equals(name)) {
                    matches[matchCount++] = kid;
                }
            }

            return (XML[])((XML[])PApplet.subset(matches, 0, matchCount));
        }
    }

    protected XML[] getChildrenRecursive(String[] items, int offset) {
        if (offset == items.length - 1) {
            return this.getChildren(items[offset]);
        } else {
            XML[] matches = this.getChildren(items[offset]);
            XML[] outgoing = new XML[0];

            for(int i = 0; i < matches.length; ++i) {
                XML[] kidMatches = matches[i].getChildrenRecursive(items, offset + 1);
                outgoing = (XML[])((XML[])PApplet.concat(outgoing, kidMatches));
            }

            return outgoing;
        }
    }

    public XML addChild(String tag) {
        Document document = this.node.getOwnerDocument();
        Node newChild = document.createElement(tag);
        return this.appendChild(newChild);
    }

    public XML addChild(XML child) {
        Document document = this.node.getOwnerDocument();
        Node newChild = document.importNode((Node)child.getNative(), true);
        return this.appendChild(newChild);
    }

    protected XML appendChild(Node newNode) {
        this.node.appendChild(newNode);
        XML newbie = new XML(this, newNode);
        if (this.children != null) {
            this.children = (XML[])((XML[])PApplet.concat(this.children, new XML[]{newbie}));
        }

        return newbie;
    }

    public void removeChild(XML kid) {
        this.node.removeChild(kid.node);
        this.children = null;
    }

    public void trim() {
        try {
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPathExpression xpathExp = xpathFactory.newXPath().compile("//text()[normalize-space(.) = '']");
            NodeList emptyTextNodes = (NodeList)xpathExp.evaluate(this.node, XPathConstants.NODESET);

            for(int i = 0; i < emptyTextNodes.getLength(); ++i) {
                Node emptyTextNode = emptyTextNodes.item(i);
                emptyTextNode.getParentNode().removeChild(emptyTextNode);
            }

        } catch (Exception var6) {
            throw new RuntimeException(var6);
        }
    }

    public int getAttributeCount() {
        return this.node.getAttributes().getLength();
    }

    public String[] listAttributes() {
        NamedNodeMap nnm = this.node.getAttributes();
        String[] outgoing = new String[nnm.getLength()];

        for(int i = 0; i < outgoing.length; ++i) {
            outgoing[i] = nnm.item(i).getNodeName();
        }

        return outgoing;
    }

    public boolean hasAttribute(String name) {
        return this.node.getAttributes().getNamedItem(name) != null;
    }

    public String getString(String name) {
        return this.getString(name, (String)null);
    }

    public String getString(String name, String defaultValue) {
        NamedNodeMap attrs = this.node.getAttributes();
        if (attrs != null) {
            Node attr = attrs.getNamedItem(name);
            if (attr != null) {
                return attr.getNodeValue();
            }
        }

        return defaultValue;
    }

    public void setString(String name, String value) {
        ((Element)this.node).setAttribute(name, value);
    }

    public int getInt(String name) {
        return this.getInt(name, 0);
    }

    public void setInt(String name, int value) {
        this.setString(name, String.valueOf(value));
    }

    public int getInt(String name, int defaultValue) {
        String value = this.getString(name);
        return value == null ? defaultValue : Integer.parseInt(value);
    }

    public void setLong(String name, long value) {
        this.setString(name, String.valueOf(value));
    }

    public long getLong(String name, long defaultValue) {
        String value = this.getString(name);
        return value == null ? defaultValue : Long.parseLong(value);
    }

    public float getFloat(String name) {
        return this.getFloat(name, 0.0F);
    }

    public float getFloat(String name, float defaultValue) {
        String value = this.getString(name);
        return value == null ? defaultValue : Float.parseFloat(value);
    }

    public void setFloat(String name, float value) {
        this.setString(name, String.valueOf(value));
    }

    public double getDouble(String name) {
        return this.getDouble(name, 0.0D);
    }

    public double getDouble(String name, double defaultValue) {
        String value = this.getString(name);
        return value == null ? defaultValue : Double.parseDouble(value);
    }

    public void setDouble(String name, double value) {
        this.setString(name, String.valueOf(value));
    }

    public String getContent() {
        return this.node.getTextContent();
    }

    public String getContent(String defaultValue) {
        String s = this.node.getTextContent();
        return s != null ? s : defaultValue;
    }

    public int getIntContent() {
        return this.getIntContent(0);
    }

    public int getIntContent(int defaultValue) {
        return PApplet.parseInt(this.node.getTextContent(), defaultValue);
    }

    public float getFloatContent() {
        return this.getFloatContent(0.0F);
    }

    public float getFloatContent(float defaultValue) {
        return PApplet.parseFloat(this.node.getTextContent(), defaultValue);
    }

    public long getLongContent() {
        return this.getLongContent(0L);
    }

    public long getLongContent(long defaultValue) {
        String c = this.node.getTextContent();
        if (c != null) {
            try {
                return Long.parseLong(c);
            } catch (NumberFormatException var5) {
            }
        }

        return defaultValue;
    }

    public double getDoubleContent() {
        return this.getDoubleContent(0.0D);
    }

    public double getDoubleContent(double defaultValue) {
        String c = this.node.getTextContent();
        if (c != null) {
            try {
                return Double.parseDouble(c);
            } catch (NumberFormatException var5) {
            }
        }

        return defaultValue;
    }

    public void setContent(String text) {
        this.node.setTextContent(text);
    }

    public void setIntContent(int value) {
        this.setContent(String.valueOf(value));
    }

    public void setFloatContent(float value) {
        this.setContent(String.valueOf(value));
    }

    public void setLongContent(long value) {
        this.setContent(String.valueOf(value));
    }

    public void setDoubleContent(double value) {
        this.setContent(String.valueOf(value));
    }

    public String format(int indent) {
        try {
            boolean useIndentAmount = false;
            TransformerFactory factory = TransformerFactory.newInstance();
            if (indent != -1) {
                try {
                    factory.setAttribute("indent-number", indent);
                } catch (IllegalArgumentException var17) {
                    useIndentAmount = true;
                }
            }

            Transformer transformer = factory.newTransformer();
            if (indent != -1 && this.parent != null) {
                transformer.setOutputProperty("omit-xml-declaration", "no");
            } else {
                transformer.setOutputProperty("omit-xml-declaration", "yes");
            }

            transformer.setOutputProperty("method", "xml");
            if (useIndentAmount) {
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(indent));
            }

            transformer.setOutputProperty("encoding", "UTF-8");
            transformer.setOutputProperty("indent", "yes");
            String decl = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
            String sep = System.getProperty("line.separator");
            StringWriter tempWriter = new StringWriter();
            StreamResult tempResult = new StreamResult(tempWriter);
            transformer.transform(new DOMSource(this.node), tempResult);
            String[] tempLines = PApplet.split(tempWriter.toString(), sep);
            if (tempLines[0].startsWith("<?xml")) {
                int declEnd = tempLines[0].indexOf("?>") + 2;
                if (tempLines[0].length() == declEnd) {
                    tempLines = PApplet.subset(tempLines, 1);
                } else {
                    tempLines[0] = tempLines[0].substring(declEnd);
                }
            }

            String singleLine = PApplet.join(PApplet.trim(tempLines), "");
            if (indent == -1) {
                return singleLine;
            } else if (singleLine.trim().length() == 0) {
                return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + sep + singleLine;
            } else {
                StringWriter stringWriter = new StringWriter();
                StreamResult xmlOutput = new StreamResult(stringWriter);
                Source source = new StreamSource(new StringReader(singleLine));
                transformer.transform(source, xmlOutput);
                String outgoing = stringWriter.toString();
                if (outgoing.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")) {
                    int declen = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>".length();
                    int seplen = sep.length();
                    return outgoing.length() > declen + seplen && !outgoing.substring(declen, declen + seplen).equals(sep) ? outgoing.substring(0, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>".length()) + sep + outgoing.substring("<?xml version=\"1.0\" encoding=\"UTF-8\"?>".length()) : outgoing;
                } else {
                    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + sep + outgoing;
                }
            }
        } catch (Exception var18) {
            var18.printStackTrace();
            return null;
        }
    }

    public void print() {
        PApplet.println(this.format(2));
    }

    public String toString() {
        return this.format(-1);
    }
}
