package processing.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import processing.core.PApplet;

public class Table {
    protected int rowCount;
    protected int allocCount;
    protected String missingString;
    protected int missingInt;
    protected long missingLong;
    protected float missingFloat;
    protected double missingDouble;
    protected int missingCategory;
    String[] columnTitles;
    Table.HashMapBlows[] columnCategories;
    HashMap<String, Integer> columnIndices;
    protected Object[] columns;
    public static final int STRING = 0;
    public static final int INT = 1;
    public static final int LONG = 2;
    public static final int FLOAT = 3;
    public static final int DOUBLE = 4;
    public static final int CATEGORY = 5;
    int[] columnTypes;
    protected Table.RowIterator rowIterator;
    protected int expandIncrement;
    static final String[] loadExtensions = new String[]{"csv", "tsv", "ods", "bin"};
    static final String[] saveExtensions = new String[]{"csv", "tsv", "ods", "bin", "html"};
    Table.CommaSeparatedLine csl;
    static Charset utf8;

    public Table() {
        this.missingString = null;
        this.missingInt = 0;
        this.missingLong = 0L;
        this.missingFloat = 0.0F / 0.0;
        this.missingDouble = 0.0D / 0.0;
        this.missingCategory = -1;
        this.init();
    }

    public Table(File file) throws IOException {
        this((File)file, (String)null);
    }

    public Table(File file, String options) throws IOException {
        this.missingString = null;
        this.missingInt = 0;
        this.missingLong = 0L;
        this.missingFloat = 0.0F / 0.0;
        this.missingDouble = 0.0D / 0.0;
        this.missingCategory = -1;
        this.init();
        this.parse(PApplet.createInput(file), extensionOptions(true, file.getName(), options));
    }

    public Table(InputStream input) throws IOException {
        this((InputStream)input, (String)null);
    }

    public Table(InputStream input, String options) throws IOException {
        this.missingString = null;
        this.missingInt = 0;
        this.missingLong = 0L;
        this.missingFloat = 0.0F / 0.0;
        this.missingDouble = 0.0D / 0.0;
        this.missingCategory = -1;
        this.init();
        this.parse(input, options);
    }

    public Table(Iterable<TableRow> rows) {
        this.missingString = null;
        this.missingInt = 0;
        this.missingLong = 0L;
        this.missingFloat = 0.0F / 0.0;
        this.missingDouble = 0.0D / 0.0;
        this.missingCategory = -1;
        this.init();
        int row = 0;
        int alloc = 10;

        TableRow incoming;
        for(Iterator var4 = rows.iterator(); var4.hasNext(); this.setRow(row++, incoming)) {
            incoming = (TableRow)var4.next();
            if (row == 0) {
                this.setColumnTypes(incoming.getColumnTypes());
                this.setColumnTitles(incoming.getColumnTitles());
                this.setRowCount(alloc);
                this.setColumnCount(incoming.getColumnCount());
            } else if (row == alloc) {
                alloc *= 2;
                this.setRowCount(alloc);
            }
        }

        if (row != alloc) {
            this.setRowCount(row);
        }

    }

    public Table(ResultSet rs) {
        this.missingString = null;
        this.missingInt = 0;
        this.missingLong = 0L;
        this.missingFloat = 0.0F / 0.0;
        this.missingDouble = 0.0D / 0.0;
        this.missingCategory = -1;
        this.init();

        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            this.setColumnCount(columnCount);

            int row;
            int col;
            for(row = 0; row < columnCount; ++row) {
                this.setColumnTitle(row, rsmd.getColumnName(row + 1));
                col = rsmd.getColumnType(row + 1);
                switch(col) {
                    case -6:
                    case 4:
                    case 5:
                        this.setColumnType(row, 1);
                        break;
                    case -5:
                        this.setColumnType(row, 2);
                    case -4:
                    case -3:
                    case -2:
                    case -1:
                    case 0:
                    case 1:
                    case 2:
                    default:
                        break;
                    case 3:
                    case 7:
                    case 8:
                        this.setColumnType(row, 4);
                        break;
                    case 6:
                        this.setColumnType(row, 3);
                }
            }

            for(row = 0; rs.next(); ++row) {
                for(col = 0; col < columnCount; ++col) {
                    switch(this.columnTypes[col]) {
                        case 0:
                            this.setString(row, col, rs.getString(col + 1));
                            break;
                        case 1:
                            this.setInt(row, col, rs.getInt(col + 1));
                            break;
                        case 2:
                            this.setLong(row, col, rs.getLong(col + 1));
                            break;
                        case 3:
                            this.setFloat(row, col, rs.getFloat(col + 1));
                            break;
                        case 4:
                            this.setDouble(row, col, rs.getDouble(col + 1));
                            break;
                        default:
                            throw new IllegalArgumentException("column type " + this.columnTypes[col] + " not supported.");
                    }
                }
            }

        } catch (SQLException var6) {
            throw new RuntimeException(var6);
        }
    }

    public Table typedParse(InputStream input, String options) throws IOException {
        Table table = new Table();
        table.setColumnTypes(this);
        table.parse(input, options);
        return table;
    }

    protected void init() {
        this.columns = new Object[0];
        this.columnTypes = new int[0];
        this.columnCategories = new Table.HashMapBlows[0];
    }

    public static String extensionOptions(boolean loading, String filename, String options) {
        String extension = PApplet.checkExtension(filename);
        if (extension != null) {
            String[] var4 = loading ? loadExtensions : saveExtensions;
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                String possible = var4[var6];
                if (extension.equals(possible)) {
                    if (options == null) {
                        return extension;
                    }

                    return extension + "," + options;
                }
            }
        }

        return options;
    }

    protected void parse(InputStream input, String options) throws IOException {
        boolean header = false;
        String extension = null;
        boolean binary = false;
        String encoding = "UTF-8";
        String worksheet = null;
        String sheetParam = "worksheet=";
        String[] opts = null;
        int c;
        if (options != null) {
            opts = PApplet.trim(PApplet.split(options, ','));
            String[] var10 = opts;
            int var11 = opts.length;

            for(c = 0; c < var11; ++c) {
                String opt = var10[c];
                if (opt.equals("tsv")) {
                    extension = "tsv";
                } else if (opt.equals("csv")) {
                    extension = "csv";
                } else if (opt.equals("ods")) {
                    extension = "ods";
                } else {
                    if (opt.equals("newlines")) {
                        throw new IllegalArgumentException("The 'newlines' option is no longer necessary.");
                    }

                    if (opt.equals("bin")) {
                        binary = true;
                        extension = "bin";
                    } else if (opt.equals("header")) {
                        header = true;
                    } else if (opt.startsWith("worksheet=")) {
                        worksheet = opt.substring("worksheet=".length());
                    } else if (!opt.startsWith("dictionary=")) {
                        if (!opt.startsWith("encoding=")) {
                            throw new IllegalArgumentException("'" + opt + "' is not a valid option for loading a Table");
                        }

                        encoding = opt.substring(9);
                    }
                }
            }
        }

        if (extension == null) {
            throw new IllegalArgumentException("No extension specified for this Table");
        } else {
            if (binary) {
                this.loadBinary(input);
            } else if (extension.equals("ods")) {
                this.odsParse(input, worksheet, header);
            } else {
                InputStreamReader isr = new InputStreamReader(input, encoding);
                BufferedReader reader = new BufferedReader(isr);
                reader.mark(1);
                c = reader.read();
                if (c != 65279) {
                    reader.reset();
                }

                this.parseBasic(reader, header, "tsv".equals(extension));
            }

        }
    }

    protected void parseBasic(BufferedReader reader, boolean header, boolean tsv) throws IOException {
        String line = null;
        int row = 0;
        if (this.rowCount == 0) {
            this.setRowCount(10);
        }

        try {
            while((line = reader.readLine()) != null) {
                if (row == this.getRowCount()) {
                    this.setRowCount(row << 1);
                }

                if (row == 0 && header) {
                    this.setColumnTitles(tsv ? PApplet.split(line, '\t') : this.splitLineCSV(line, reader));
                    header = false;
                } else {
                    this.setRow(row, (Object[])(tsv ? PApplet.split(line, '\t') : this.splitLineCSV(line, reader)));
                    ++row;
                }

                if (row % 10000 == 0) {
                    try {
                        Thread.sleep(10L);
                    } catch (InterruptedException var7) {
                        var7.printStackTrace();
                    }
                }
            }
        } catch (Exception var8) {
            throw new RuntimeException("Error reading table on line " + row, var8);
        }

        if (row != this.getRowCount()) {
            this.setRowCount(row);
        }

    }

    protected String[] splitLineCSV(String line, BufferedReader reader) throws IOException {
        if (this.csl == null) {
            this.csl = new Table.CommaSeparatedLine();
        }

        return this.csl.handle(line, reader);
    }

    private InputStream odsFindContentXML(InputStream input) {
        ZipInputStream zis = new ZipInputStream(input);
        ZipEntry entry = null;

        try {
            while((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals("content.xml")) {
                    return zis;
                }
            }
        } catch (IOException var5) {
            var5.printStackTrace();
        }

        return null;
    }

    protected void odsParse(InputStream input, String worksheet, boolean header) {
        try {
            InputStream contentStream = this.odsFindContentXML(input);
            XML xml = new XML(contentStream);
            XML[] sheets = xml.getChildren("office:body/office:spreadsheet/table:table");
            boolean found = false;
            XML[] var8 = sheets;
            int var9 = sheets.length;

            for(int var10 = 0; var10 < var9; ++var10) {
                XML sheet = var8[var10];
                if (worksheet == null || worksheet.equals(sheet.getString("table:name"))) {
                    this.odsParseSheet(sheet, header);
                    found = true;
                    if (worksheet == null) {
                        break;
                    }
                }
            }

            if (!found) {
                if (worksheet == null) {
                    throw new RuntimeException("No worksheets found in the ODS file.");
                }

                throw new RuntimeException("No worksheet named " + worksheet + " found in the ODS file.");
            }
        } catch (UnsupportedEncodingException var12) {
            var12.printStackTrace();
        } catch (IOException var13) {
            var13.printStackTrace();
        } catch (ParserConfigurationException var14) {
            var14.printStackTrace();
        } catch (SAXException var15) {
            var15.printStackTrace();
        }

    }

    private void odsParseSheet(XML sheet, boolean header) {
        boolean ignoreTags = true;
        XML[] rows = sheet.getChildren("table:table-row");
        int rowIndex = 0;
        XML[] var6 = rows;
        int var7 = rows.length;

        for(int var8 = 0; var8 < var7; ++var8) {
            XML row = var6[var8];
            int rowRepeat = row.getInt("table:number-rows-repeated", 1);
            boolean rowNotNull = false;
            XML[] cells = row.getChildren();
            int columnIndex = 0;
            XML[] var14 = cells;
            int r = cells.length;

            for(int var16 = 0; var16 < r; ++var16) {
                XML cell = var14[var16];
                int cellRepeat = cell.getInt("table:number-columns-repeated", 1);
                String cellData = cell.getString("office:value");
                int r;
                if (cellData == null) {
                    r = cell.getChildCount();
                    if (r != 0) {
                        XML[] paragraphElements = cell.getChildren("text:p");
                        if (paragraphElements.length != 1) {
                            XML[] var34 = paragraphElements;
                            int var35 = paragraphElements.length;

                            for(int var36 = 0; var36 < var35; ++var36) {
                                XML el = var34[var36];
                                System.err.println(el.toString());
                            }

                            throw new RuntimeException("found more than one text:p element");
                        }

                        XML textp = paragraphElements[0];
                        String textpContent = textp.getContent();
                        if (textpContent != null) {
                            cellData = textpContent;
                        } else {
                            XML[] textpKids = textp.getChildren();
                            StringBuilder cellBuffer = new StringBuilder();
                            XML[] var26 = textpKids;
                            int var27 = textpKids.length;
                            int var28 = 0;

                            while(true) {
                                if (var28 >= var27) {
                                    cellData = cellBuffer.toString();
                                    break;
                                }

                                XML kid = var26[var28];
                                String kidName = kid.getName();
                                if (kidName == null) {
                                    this.odsAppendNotNull(kid, cellBuffer);
                                } else if (kidName.equals("text:s")) {
                                    int spaceCount = kid.getInt("text:c", 1);

                                    for(int space = 0; space < spaceCount; ++space) {
                                        cellBuffer.append(' ');
                                    }
                                } else if (kidName.equals("text:span")) {
                                    this.odsAppendNotNull(kid, cellBuffer);
                                } else if (kidName.equals("text:a")) {
                                    cellBuffer.append(kid.getString("xlink:href"));
                                } else {
                                    this.odsAppendNotNull(kid, cellBuffer);
                                    System.err.println(this.getClass().getName() + ": don't understand: " + kid);
                                }

                                ++var28;
                            }
                        }
                    }
                }

                for(r = 0; r < cellRepeat; ++r) {
                    if (cellData != null) {
                        this.setString(rowIndex, columnIndex, cellData);
                    }

                    ++columnIndex;
                    if (cellData != null) {
                        rowNotNull = true;
                    }
                }
            }

            if (header) {
                this.removeTitleRow();
                header = false;
            } else {
                if (rowNotNull && rowRepeat > 1) {
                    String[] rowStrings = this.getStringRow(rowIndex);

                    for(r = 1; r < rowRepeat; ++r) {
                        this.addRow((Object[])rowStrings);
                    }
                }

                rowIndex += rowRepeat;
            }
        }

    }

    private void odsAppendNotNull(XML kid, StringBuilder buffer) {
        String content = kid.getContent();
        if (content != null) {
            buffer.append(content);
        }

    }

    public void parseInto(Object enclosingObject, String fieldName) {
        Class<?> target = null;
        Object outgoing = null;
        Field targetField = null;

        Class enclosingClass;
        try {
            enclosingClass = enclosingObject.getClass();
            targetField = enclosingClass.getDeclaredField(fieldName);
            Class<?> targetArray = targetField.getType();
            if (targetArray.isArray()) {
                target = targetArray.getComponentType();
                outgoing = Array.newInstance(target, this.getRowCount());
            }
        } catch (NoSuchFieldException var20) {
            var20.printStackTrace();
        } catch (SecurityException var21) {
            var21.printStackTrace();
        }

        enclosingClass = target.getEnclosingClass();
        Constructor con = null;

        try {
            if (enclosingClass == null) {
                con = target.getDeclaredConstructor();
            } else {
                con = target.getDeclaredConstructor(enclosingClass);
            }

            if (!con.isAccessible()) {
                con.setAccessible(true);
            }
        } catch (SecurityException var18) {
            var18.printStackTrace();
        } catch (NoSuchMethodException var19) {
            var19.printStackTrace();
        }

        Field[] fields = target.getDeclaredFields();
        ArrayList<Field> inuse = new ArrayList();
        Field[] var10 = fields;
        int var11 = fields.length;

        Field item;
        for(int var12 = 0; var12 < var11; ++var12) {
            item = var10[var12];
            String name = item.getName();
            if (this.getColumnIndex(name, false) != -1) {
                if (!item.isAccessible()) {
                    item.setAccessible(true);
                }

                inuse.add(item);
            }
        }

        int var27 = 0;

        try {
            Object item;
            label125:
            for(Iterator var28 = this.rows().iterator(); var28.hasNext(); Array.set(outgoing, var27++, item)) {
                TableRow row = (TableRow)var28.next();
                item = null;
                if (enclosingClass == null) {
                    item = con.newInstance();
                } else {
                    item = con.newInstance(enclosingObject);
                }

                Iterator var31 = inuse.iterator();

                while(true) {
                    while(true) {
                        if (!var31.hasNext()) {
                            continue label125;
                        }

                        Field field = (Field)var31.next();
                        String name = field.getName();
                        if (field.getType() == String.class) {
                            field.set(item, row.getString(name));
                        } else if (field.getType() == Integer.TYPE) {
                            field.setInt(item, row.getInt(name));
                        } else if (field.getType() == Long.TYPE) {
                            field.setLong(item, row.getLong(name));
                        } else if (field.getType() == Float.TYPE) {
                            field.setFloat(item, row.getFloat(name));
                        } else if (field.getType() == Double.TYPE) {
                            field.setDouble(item, row.getDouble(name));
                        } else {
                            String content;
                            if (field.getType() == Boolean.TYPE) {
                                content = row.getString(name);
                                if (content != null && (content.toLowerCase().equals("true") || content.equals("1"))) {
                                    field.setBoolean(item, true);
                                }
                            } else if (field.getType() == Character.TYPE) {
                                content = row.getString(name);
                                if (content != null && content.length() > 0) {
                                    field.setChar(item, content.charAt(0));
                                }
                            }
                        }
                    }
                }
            }

            if (!targetField.isAccessible()) {
                targetField.setAccessible(true);
            }

            targetField.set(enclosingObject, outgoing);
        } catch (InstantiationException var22) {
            var22.printStackTrace();
        } catch (IllegalAccessException var23) {
            var23.printStackTrace();
        } catch (IllegalArgumentException var24) {
            var24.printStackTrace();
        } catch (InvocationTargetException var25) {
            var25.printStackTrace();
        }

    }

    public boolean save(File file, String options) throws IOException {
        return this.save(PApplet.createOutput(file), extensionOptions(false, file.getName(), options));
    }

    public boolean save(OutputStream output, String options) {
        PrintWriter writer = PApplet.createWriter(output);
        String extension = null;
        if (options == null) {
            throw new IllegalArgumentException("No extension specified for saving this Table");
        } else {
            String[] opts = PApplet.trim(PApplet.split(options, ','));
            extension = opts[opts.length - 1];
            boolean found = false;
            String[] var7 = saveExtensions;
            int var8 = var7.length;

            for(int var9 = 0; var9 < var8; ++var9) {
                String ext = var7[var9];
                if (extension.equals(ext)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                throw new IllegalArgumentException("'" + extension + "' not available for Table");
            } else {
                if (extension.equals("csv")) {
                    this.writeCSV(writer);
                } else if (extension.equals("tsv")) {
                    this.writeTSV(writer);
                } else if (extension.equals("ods")) {
                    try {
                        this.saveODS(output);
                    } catch (IOException var12) {
                        var12.printStackTrace();
                        return false;
                    }
                } else if (extension.equals("html")) {
                    this.writeHTML(writer);
                } else if (extension.equals("bin")) {
                    try {
                        this.saveBinary(output);
                    } catch (IOException var11) {
                        var11.printStackTrace();
                        return false;
                    }
                }

                writer.flush();
                writer.close();
                return true;
            }
        }
    }

    protected void writeTSV(PrintWriter writer) {
        int row;
        if (this.columnTitles != null) {
            for(row = 0; row < this.columns.length; ++row) {
                if (row != 0) {
                    writer.print('\t');
                }

                if (this.columnTitles[row] != null) {
                    writer.print(this.columnTitles[row]);
                }
            }

            writer.println();
        }

        for(row = 0; row < this.rowCount; ++row) {
            for(int col = 0; col < this.getColumnCount(); ++col) {
                if (col != 0) {
                    writer.print('\t');
                }

                String entry = this.getString(row, col);
                if (entry != null) {
                    writer.print(entry);
                }
            }

            writer.println();
        }

        writer.flush();
    }

    protected void writeCSV(PrintWriter writer) {
        int col;
        if (this.columnTitles != null) {
            for(col = 0; col < this.getColumnCount(); ++col) {
                if (col != 0) {
                    writer.print(',');
                }

                try {
                    if (this.columnTitles[col] != null) {
                        this.writeEntryCSV(writer, this.columnTitles[col]);
                    }
                } catch (ArrayIndexOutOfBoundsException var5) {
                    PApplet.printArray(this.columnTitles);
                    PApplet.printArray(this.columns);
                    throw var5;
                }
            }

            writer.println();
        }

        for(col = 0; col < this.rowCount; ++col) {
            for(int col = 0; col < this.getColumnCount(); ++col) {
                if (col != 0) {
                    writer.print(',');
                }

                String entry = this.getString(col, col);
                if (entry != null) {
                    this.writeEntryCSV(writer, entry);
                }
            }

            writer.println();
        }

        writer.flush();
    }

    protected void writeEntryCSV(PrintWriter writer, String entry) {
        if (entry != null) {
            if (entry.indexOf(34) != -1) {
                char[] c = entry.toCharArray();
                writer.print('"');

                for(int i = 0; i < c.length; ++i) {
                    if (c[i] == '"') {
                        writer.print("\"\"");
                    } else {
                        writer.print(c[i]);
                    }
                }

                writer.print('"');
            } else if (entry.indexOf(44) == -1 && entry.indexOf(10) == -1 && entry.indexOf(13) == -1) {
                if (entry.length() <= 0 || entry.charAt(0) != ' ' && entry.charAt(entry.length() - 1) != ' ') {
                    writer.print(entry);
                } else {
                    writer.print('"');
                    writer.print(entry);
                    writer.print('"');
                }
            } else {
                writer.print('"');
                writer.print(entry);
                writer.print('"');
            }
        }

    }

    protected void writeHTML(PrintWriter writer) {
        writer.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2//EN\">");
        writer.println("<html>");
        writer.println("<head>");
        writer.println("  <meta http-equiv=\"content-type\" content=\"text/html;charset=utf-8\" />");
        writer.println("</head>");
        writer.println("<body>");
        writer.println("  <table>");
        int col;
        if (this.hasColumnTitles()) {
            writer.println("  <tr>");
            String[] var2 = this.getColumnTitles();
            col = var2.length;

            for(int var4 = 0; var4 < col; ++var4) {
                String entry = var2[var4];
                writer.print("      <th>");
                if (entry != null) {
                    this.writeEntryHTML(writer, entry);
                }

                writer.println("</th>");
            }

            writer.println("  </tr>");
        }

        for(int row = 0; row < this.getRowCount(); ++row) {
            writer.println("    <tr>");

            for(col = 0; col < this.getColumnCount(); ++col) {
                String entry = this.getString(row, col);
                writer.print("      <td>");
                if (entry != null) {
                    this.writeEntryHTML(writer, entry);
                }

                writer.println("</td>");
            }

            writer.println("    </tr>");
        }

        writer.println("  </table>");
        writer.println("</body>");
        writer.println("</html>");
        writer.flush();
    }

    protected void writeEntryHTML(PrintWriter writer, String entry) {
        char[] var3 = entry.toCharArray();
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            char c = var3[var5];
            if (c == '<') {
                writer.print("&lt;");
            } else if (c == '>') {
                writer.print("&gt;");
            } else if (c == '&') {
                writer.print("&amp;");
            } else if (c == '"') {
                writer.print("&quot;");
            } else if (c >= ' ' && c <= 127) {
                writer.print(c);
            } else {
                writer.print("&#");
                writer.print(c);
                writer.print(';');
            }
        }

    }

    protected void saveODS(OutputStream os) throws IOException {
        ZipOutputStream zos = new ZipOutputStream(os);
        String xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        ZipEntry entry = new ZipEntry("META-INF/manifest.xml");
        String[] lines = new String[]{"<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "<manifest:manifest xmlns:manifest=\"urn:oasis:names:tc:opendocument:xmlns:manifest:1.0\">", "  <manifest:file-entry manifest:media-type=\"application/vnd.oasis.opendocument.spreadsheet\" manifest:version=\"1.2\" manifest:full-path=\"/\"/>", "  <manifest:file-entry manifest:media-type=\"text/xml\" manifest:full-path=\"content.xml\"/>", "  <manifest:file-entry manifest:media-type=\"text/xml\" manifest:full-path=\"styles.xml\"/>", "  <manifest:file-entry manifest:media-type=\"text/xml\" manifest:full-path=\"meta.xml\"/>", "  <manifest:file-entry manifest:media-type=\"text/xml\" manifest:full-path=\"settings.xml\"/>", "</manifest:manifest>"};
        zos.putNextEntry(entry);
        zos.write(PApplet.join(lines, "\n").getBytes());
        zos.closeEntry();
        String[] dummyFiles = new String[]{"meta.xml", "settings.xml", "styles.xml"};
        lines = new String[]{"<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "<office:document-meta office:version=\"1.0\" xmlns:office=\"urn:oasis:names:tc:opendocument:xmlns:office:1.0\" />"};
        byte[] dummyBytes = PApplet.join(lines, "\n").getBytes();
        String[] var8 = dummyFiles;
        int var9 = dummyFiles.length;

        int i;
        for(i = 0; i < var9; ++i) {
            String filename = var8[i];
            entry = new ZipEntry(filename);
            zos.putNextEntry(entry);
            zos.write(dummyBytes);
            zos.closeEntry();
        }

        entry = new ZipEntry("mimetype");
        zos.putNextEntry(entry);
        zos.write("application/vnd.oasis.opendocument.spreadsheet".getBytes());
        zos.closeEntry();
        entry = new ZipEntry("content.xml");
        zos.putNextEntry(entry);
        writeUTF(zos, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "<office:document-content xmlns:office=\"urn:oasis:names:tc:opendocument:xmlns:office:1.0\" xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" xmlns:table=\"urn:oasis:names:tc:opendocument:xmlns:table:1.0\" office:version=\"1.2\">", "  <office:body>", "    <office:spreadsheet>", "      <table:table table:name=\"Sheet1\" table:print=\"false\">");
        byte[] rowStart = "        <table:table-row>\n".getBytes();
        byte[] rowStop = "        </table:table-row>\n".getBytes();
        if (this.hasColumnTitles()) {
            zos.write(rowStart);

            for(i = 0; i < this.getColumnCount(); ++i) {
                this.saveStringODS(zos, this.columnTitles[i]);
            }

            zos.write(rowStop);
        }

        Iterator var15 = this.rows().iterator();

        while(var15.hasNext()) {
            TableRow row = (TableRow)var15.next();
            zos.write(rowStart);

            for(int i = 0; i < this.getColumnCount(); ++i) {
                if (this.columnTypes[i] != 0 && this.columnTypes[i] != 5) {
                    this.saveNumberODS(zos, row.getString(i));
                } else {
                    this.saveStringODS(zos, row.getString(i));
                }
            }

            zos.write(rowStop);
        }

        writeUTF(zos, "      </table:table>", "    </office:spreadsheet>", "  </office:body>", "</office:document-content>");
        zos.closeEntry();
        zos.flush();
        zos.close();
    }

    void saveStringODS(OutputStream output, String text) throws IOException {
        StringBuilder sanitized = new StringBuilder();
        if (text != null) {
            char[] array = text.toCharArray();
            char[] var5 = array;
            int var6 = array.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                char c = var5[var7];
                if (c == '&') {
                    sanitized.append("&amp;");
                } else if (c == '\'') {
                    sanitized.append("&apos;");
                } else if (c == '"') {
                    sanitized.append("&quot;");
                } else if (c == '<') {
                    sanitized.append("&lt;");
                } else if (c == '>') {
                    sanitized.append("&rt;");
                } else if (c >= ' ' && c <= 127) {
                    sanitized.append(c);
                } else {
                    sanitized.append("&#" + c + ";");
                }
            }
        }

        writeUTF(output, "          <table:table-cell office:value-type=\"string\">", "            <text:p>" + sanitized + "</text:p>", "          </table:table-cell>");
    }

    void saveNumberODS(OutputStream output, String text) throws IOException {
        writeUTF(output, "          <table:table-cell office:value-type=\"float\" office:value=\"" + text + "\">", "            <text:p>" + text + "</text:p>", "          </table:table-cell>");
    }

    static void writeUTF(OutputStream output, String... lines) throws IOException {
        if (utf8 == null) {
            utf8 = Charset.forName("UTF-8");
        }

        String[] var2 = lines;
        int var3 = lines.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String str = var2[var4];
            output.write(str.getBytes(utf8));
            output.write(10);
        }

    }

    protected void saveBinary(OutputStream os) throws IOException {
        DataOutputStream output = new DataOutputStream(new BufferedOutputStream(os));
        output.writeInt(-1878545634);
        output.writeInt(this.getRowCount());
        output.writeInt(this.getColumnCount());
        int col;
        String str;
        if (this.columnTitles != null) {
            output.writeBoolean(true);
            String[] var3 = this.columnTitles;
            int var4 = var3.length;

            for(col = 0; col < var4; ++col) {
                str = var3[col];
                output.writeUTF(str);
            }
        } else {
            output.writeBoolean(false);
        }

        int i;
        for(i = 0; i < this.getColumnCount(); ++i) {
            output.writeInt(this.columnTypes[i]);
        }

        for(i = 0; i < this.getColumnCount(); ++i) {
            if (this.columnTypes[i] == 5) {
                this.columnCategories[i].write(output);
            }
        }

        if (this.missingString == null) {
            output.writeBoolean(false);
        } else {
            output.writeBoolean(true);
            output.writeUTF(this.missingString);
        }

        output.writeInt(this.missingInt);
        output.writeLong(this.missingLong);
        output.writeFloat(this.missingFloat);
        output.writeDouble(this.missingDouble);
        output.writeInt(this.missingCategory);
        Iterator var10 = this.rows().iterator();

        while(var10.hasNext()) {
            TableRow row = (TableRow)var10.next();

            for(col = 0; col < this.getColumnCount(); ++col) {
                switch(this.columnTypes[col]) {
                    case 0:
                        str = row.getString(col);
                        if (str == null) {
                            output.writeBoolean(false);
                        } else {
                            output.writeBoolean(true);
                            output.writeUTF(str);
                        }
                        break;
                    case 1:
                        output.writeInt(row.getInt(col));
                        break;
                    case 2:
                        output.writeLong(row.getLong(col));
                        break;
                    case 3:
                        output.writeFloat(row.getFloat(col));
                        break;
                    case 4:
                        output.writeDouble(row.getDouble(col));
                        break;
                    case 5:
                        String peace = row.getString(col);
                        if (peace.equals(this.missingString)) {
                            output.writeInt(this.missingCategory);
                        } else {
                            output.writeInt(this.columnCategories[col].index(peace));
                        }
                }
            }
        }

        output.flush();
        output.close();
    }

    protected void loadBinary(InputStream is) throws IOException {
        DataInputStream input = new DataInputStream(new BufferedInputStream(is));
        int magic = input.readInt();
        if (magic != -1878545634) {
            throw new IOException("Not a compatible binary table (magic was " + PApplet.hex(magic) + ")");
        } else {
            int rowCount = input.readInt();
            this.setRowCount(rowCount);
            int columnCount = input.readInt();
            this.setColumnCount(columnCount);
            boolean hasTitles = input.readBoolean();
            int row;
            if (hasTitles) {
                this.columnTitles = new String[this.getColumnCount()];

                for(row = 0; row < columnCount; ++row) {
                    this.setColumnTitle(row, input.readUTF());
                }
            }

            int col;
            for(row = 0; row < columnCount; ++row) {
                col = input.readInt();
                this.columnTypes[row] = col;
                switch(col) {
                    case 0:
                        this.columns[row] = new String[rowCount];
                        break;
                    case 1:
                        this.columns[row] = new int[rowCount];
                        break;
                    case 2:
                        this.columns[row] = new long[rowCount];
                        break;
                    case 3:
                        this.columns[row] = new float[rowCount];
                        break;
                    case 4:
                        this.columns[row] = new double[rowCount];
                        break;
                    case 5:
                        this.columns[row] = new int[rowCount];
                        break;
                    default:
                        throw new IllegalArgumentException(col + " is not a valid column type.");
                }
            }

            for(row = 0; row < columnCount; ++row) {
                if (this.columnTypes[row] == 5) {
                    this.columnCategories[row] = new Table.HashMapBlows(input);
                }
            }

            if (input.readBoolean()) {
                this.missingString = input.readUTF();
            } else {
                this.missingString = null;
            }

            this.missingInt = input.readInt();
            this.missingLong = input.readLong();
            this.missingFloat = input.readFloat();
            this.missingDouble = input.readDouble();
            this.missingCategory = input.readInt();

            for(row = 0; row < rowCount; ++row) {
                for(col = 0; col < columnCount; ++col) {
                    switch(this.columnTypes[col]) {
                        case 0:
                            String str = null;
                            if (input.readBoolean()) {
                                str = input.readUTF();
                            }

                            this.setString(row, col, str);
                            break;
                        case 1:
                            this.setInt(row, col, input.readInt());
                            break;
                        case 2:
                            this.setLong(row, col, input.readLong());
                            break;
                        case 3:
                            this.setFloat(row, col, input.readFloat());
                            break;
                        case 4:
                            this.setDouble(row, col, input.readDouble());
                            break;
                        case 5:
                            int index = input.readInt();
                            this.setInt(row, col, index);
                    }
                }
            }

            input.close();
        }
    }

    public void addColumn() {
        this.addColumn((String)null, 0);
    }

    public void addColumn(String title) {
        this.addColumn(title, 0);
    }

    public void addColumn(String title, int type) {
        this.insertColumn(this.columns.length, title, type);
    }

    public void insertColumn(int index) {
        this.insertColumn(index, (String)null, 0);
    }

    public void insertColumn(int index, String title) {
        this.insertColumn(index, title, 0);
    }

    public void insertColumn(int index, String title, int type) {
        if (title != null && this.columnTitles == null) {
            this.columnTitles = new String[this.columns.length];
        }

        if (this.columnTitles != null) {
            this.columnTitles = PApplet.splice(this.columnTitles, title, index);
            this.columnIndices = null;
        }

        this.columnTypes = PApplet.splice(this.columnTypes, type, index);
        Table.HashMapBlows[] catTemp = new Table.HashMapBlows[this.columns.length + 1];

        int i;
        for(i = 0; i < index; ++i) {
            catTemp[i] = this.columnCategories[i];
        }

        catTemp[index] = new Table.HashMapBlows();

        for(i = index; i < this.columns.length; ++i) {
            catTemp[i + 1] = this.columnCategories[i];
        }

        this.columnCategories = catTemp;
        Object[] temp = new Object[this.columns.length + 1];
        System.arraycopy(this.columns, 0, temp, 0, index);
        System.arraycopy(this.columns, index, temp, index + 1, this.columns.length - index);
        this.columns = temp;
        switch(type) {
            case 0:
                this.columns[index] = new String[this.rowCount];
                break;
            case 1:
                this.columns[index] = new int[this.rowCount];
                break;
            case 2:
                this.columns[index] = new long[this.rowCount];
                break;
            case 3:
                this.columns[index] = new float[this.rowCount];
                break;
            case 4:
                this.columns[index] = new double[this.rowCount];
                break;
            case 5:
                this.columns[index] = new int[this.rowCount];
        }

    }

    public void removeColumn(String columnName) {
        this.removeColumn(this.getColumnIndex(columnName));
    }

    public void removeColumn(int column) {
        int newCount = this.columns.length - 1;
        Object[] columnsTemp = new Object[newCount];
        Table.HashMapBlows[] catTemp = new Table.HashMapBlows[newCount];

        int i;
        for(i = 0; i < column; ++i) {
            columnsTemp[i] = this.columns[i];
            catTemp[i] = this.columnCategories[i];
        }

        for(i = column; i < newCount; ++i) {
            columnsTemp[i] = this.columns[i + 1];
            catTemp[i] = this.columnCategories[i + 1];
        }

        this.columns = columnsTemp;
        this.columnCategories = catTemp;
        if (this.columnTitles != null) {
            String[] titlesTemp = new String[newCount];

            int i;
            for(i = 0; i < column; ++i) {
                titlesTemp[i] = this.columnTitles[i];
            }

            for(i = column; i < newCount; ++i) {
                titlesTemp[i] = this.columnTitles[i + 1];
            }

            this.columnTitles = titlesTemp;
            this.columnIndices = null;
        }

    }

    public int getColumnCount() {
        return this.columns.length;
    }

    public void setColumnCount(int newCount) {
        int oldCount = this.columns.length;
        if (oldCount != newCount) {
            this.columns = (Object[])((Object[])PApplet.expand(this.columns, newCount));

            for(int c = oldCount; c < newCount; ++c) {
                this.columns[c] = new String[this.rowCount];
            }

            if (this.columnTitles != null) {
                this.columnTitles = PApplet.expand(this.columnTitles, newCount);
            }

            this.columnTypes = PApplet.expand(this.columnTypes, newCount);
            this.columnCategories = (Table.HashMapBlows[])((Table.HashMapBlows[])PApplet.expand(this.columnCategories, newCount));
        }

    }

    public void setColumnType(String columnName, String columnType) {
        this.setColumnType(this.checkColumnIndex(columnName), columnType);
    }

    static int parseColumnType(String columnType) {
        columnType = columnType.toLowerCase();
        int type = true;
        byte type;
        if (columnType.equals("string")) {
            type = 0;
        } else if (columnType.equals("int")) {
            type = 1;
        } else if (columnType.equals("long")) {
            type = 2;
        } else if (columnType.equals("float")) {
            type = 3;
        } else if (columnType.equals("double")) {
            type = 4;
        } else {
            if (!columnType.equals("category")) {
                throw new IllegalArgumentException("'" + columnType + "' is not a valid column type.");
            }

            type = 5;
        }

        return type;
    }

    public void setColumnType(int column, String columnType) {
        this.setColumnType(column, parseColumnType(columnType));
    }

    public void setColumnType(String columnName, int newType) {
        this.setColumnType(this.checkColumnIndex(columnName), newType);
    }

    public void setColumnType(int column, int newType) {
        int[] indexData;
        int row;
        String s;
        switch(newType) {
            case 0:
                if (this.columnTypes[column] != 0) {
                    String[] stringData = new String[this.rowCount];

                    for(row = 0; row < this.rowCount; ++row) {
                        stringData[row] = this.getString(row, column);
                    }

                    this.columns[column] = stringData;
                }
                break;
            case 1:
                indexData = new int[this.rowCount];

                for(row = 0; row < this.rowCount; ++row) {
                    s = this.getString(row, column);
                    indexData[row] = s == null ? this.missingInt : PApplet.parseInt(s, this.missingInt);
                }

                this.columns[column] = indexData;
                break;
            case 2:
                long[] longData = new long[this.rowCount];

                for(row = 0; row < this.rowCount; ++row) {
                    s = this.getString(row, column);

                    try {
                        longData[row] = s == null ? this.missingLong : Long.parseLong(s);
                    } catch (NumberFormatException var8) {
                        longData[row] = this.missingLong;
                    }
                }

                this.columns[column] = longData;
                break;
            case 3:
                float[] floatData = new float[this.rowCount];

                for(row = 0; row < this.rowCount; ++row) {
                    s = this.getString(row, column);
                    floatData[row] = s == null ? this.missingFloat : PApplet.parseFloat(s, this.missingFloat);
                }

                this.columns[column] = floatData;
                break;
            case 4:
                double[] doubleData = new double[this.rowCount];

                for(row = 0; row < this.rowCount; ++row) {
                    s = this.getString(row, column);

                    try {
                        doubleData[row] = s == null ? this.missingDouble : Double.parseDouble(s);
                    } catch (NumberFormatException var7) {
                        doubleData[row] = this.missingDouble;
                    }
                }

                this.columns[column] = doubleData;
                break;
            case 5:
                indexData = new int[this.rowCount];
                Table.HashMapBlows categories = new Table.HashMapBlows();

                for(int row = 0; row < this.rowCount; ++row) {
                    String s = this.getString(row, column);
                    indexData[row] = categories.index(s);
                }

                this.columnCategories[column] = categories;
                this.columns[column] = indexData;
                break;
            default:
                throw new IllegalArgumentException("That's not a valid column type.");
        }

        this.columnTypes[column] = newType;
    }

    public void setTableType(String type) {
        for(int col = 0; col < this.getColumnCount(); ++col) {
            this.setColumnType(col, type);
        }

    }

    public void setColumnTypes(int[] types) {
        this.ensureColumn(types.length - 1);

        for(int col = 0; col < types.length; ++col) {
            this.setColumnType(col, types[col]);
        }

    }

    public void setColumnTypes(Table dictionary) {
        this.ensureColumn(dictionary.getRowCount() - 1);
        int titleCol = 0;
        int typeCol = 1;
        if (dictionary.hasColumnTitles()) {
            titleCol = dictionary.getColumnIndex("title", true);
            typeCol = dictionary.getColumnIndex("type", true);
        }

        this.setColumnTitles(dictionary.getStringColumn(titleCol));
        final String[] typeNames = dictionary.getStringColumn(typeCol);
        if (dictionary.getColumnCount() > 1) {
            int col;
            if (this.getRowCount() > 1000) {
                col = Runtime.getRuntime().availableProcessors();
                ExecutorService pool = Executors.newFixedThreadPool(col / 2);

                for(final int i = 0; i < dictionary.getRowCount(); ++i) {
                    pool.execute(new Runnable() {
                        public void run() {
                            Table.this.setColumnType(i, typeNames[i]);
                        }
                    });
                }

                pool.shutdown();

                while(!pool.isTerminated()) {
                    Thread.yield();
                }
            } else {
                for(col = 0; col < dictionary.getRowCount(); ++col) {
                    this.setColumnType(col, typeNames[col]);
                }
            }
        }

    }

    public int getColumnType(String columnName) {
        return this.getColumnType(this.getColumnIndex(columnName));
    }

    public int getColumnType(int column) {
        return this.columnTypes[column];
    }

    public int[] getColumnTypes() {
        return this.columnTypes;
    }

    /** @deprecated */
    @Deprecated
    public String[] removeTitleRow() {
        String[] titles = this.getStringRow(0);
        this.removeRow(0);
        this.setColumnTitles(titles);
        return titles;
    }

    public void setColumnTitles(String[] titles) {
        if (titles != null) {
            this.ensureColumn(titles.length - 1);
        }

        this.columnTitles = titles;
        this.columnIndices = null;
    }

    public void setColumnTitle(int column, String title) {
        this.ensureColumn(column);
        if (this.columnTitles == null) {
            this.columnTitles = new String[this.getColumnCount()];
        }

        this.columnTitles[column] = title;
        this.columnIndices = null;
    }

    public boolean hasColumnTitles() {
        return this.columnTitles != null;
    }

    public String[] getColumnTitles() {
        return this.columnTitles;
    }

    public String getColumnTitle(int col) {
        return this.columnTitles == null ? null : this.columnTitles[col];
    }

    public int getColumnIndex(String columnName) {
        return this.getColumnIndex(columnName, true);
    }

    protected int getColumnIndex(String name, boolean report) {
        if (this.columnTitles == null) {
            if (report) {
                throw new IllegalArgumentException("This table has no header, so no column titles are set.");
            } else {
                return -1;
            }
        } else {
            if (this.columnIndices == null) {
                this.columnIndices = new HashMap();

                for(int col = 0; col < this.columns.length; ++col) {
                    this.columnIndices.put(this.columnTitles[col], col);
                }
            }

            Integer index = (Integer)this.columnIndices.get(name);
            if (index == null) {
                if (report) {
                    throw new IllegalArgumentException("This table has no column named '" + name + "'");
                } else {
                    return -1;
                }
            } else {
                return index;
            }
        }
    }

    public int checkColumnIndex(String title) {
        int index = this.getColumnIndex(title, false);
        if (index != -1) {
            return index;
        } else {
            this.addColumn(title);
            return this.getColumnCount() - 1;
        }
    }

    public int getRowCount() {
        return this.rowCount;
    }

    public int lastRowIndex() {
        return this.getRowCount() - 1;
    }

    public void clearRows() {
        this.setRowCount(0);
    }

    public void setRowCount(int newCount) {
        if (newCount != this.rowCount) {
            if (newCount > 1000000) {
                System.out.print("Note: setting maximum row count to " + PApplet.nfc(newCount));
            }

            long t = System.currentTimeMillis();

            int ms;
            for(ms = 0; ms < this.columns.length; ++ms) {
                switch(this.columnTypes[ms]) {
                    case 0:
                        this.columns[ms] = PApplet.expand((String[])((String[])this.columns[ms]), newCount);
                        break;
                    case 1:
                        this.columns[ms] = PApplet.expand((int[])((int[])this.columns[ms]), newCount);
                        break;
                    case 2:
                        this.columns[ms] = PApplet.expand((long[])((long[])this.columns[ms]), newCount);
                        break;
                    case 3:
                        this.columns[ms] = PApplet.expand((float[])((float[])this.columns[ms]), newCount);
                        break;
                    case 4:
                        this.columns[ms] = PApplet.expand((double[])((double[])this.columns[ms]), newCount);
                        break;
                    case 5:
                        this.columns[ms] = PApplet.expand((int[])((int[])this.columns[ms]), newCount);
                }

                if (newCount > 1000000) {
                    try {
                        Thread.sleep(10L);
                    } catch (InterruptedException var6) {
                        var6.printStackTrace();
                    }
                }
            }

            if (newCount > 1000000) {
                ms = (int)(System.currentTimeMillis() - t);
                System.out.println(" (resize took " + PApplet.nfc(ms) + " ms)");
            }
        }

        this.rowCount = newCount;
    }

    public TableRow addRow() {
        this.setRowCount(this.rowCount + 1);
        return new Table.RowPointer(this, this.rowCount - 1);
    }

    public TableRow addRow(TableRow source) {
        return this.setRow(this.rowCount, source);
    }

    public TableRow setRow(int row, TableRow source) {
        this.ensureBounds(row, source.getColumnCount() - 1);

        for(int col = 0; col < Math.min(source.getColumnCount(), this.columns.length); ++col) {
            switch(this.columnTypes[col]) {
                case 0:
                    this.setString(row, col, source.getString(col));
                    break;
                case 1:
                    this.setInt(row, col, source.getInt(col));
                    break;
                case 2:
                    this.setLong(row, col, source.getLong(col));
                    break;
                case 3:
                    this.setFloat(row, col, source.getFloat(col));
                    break;
                case 4:
                    this.setDouble(row, col, source.getDouble(col));
                    break;
                case 5:
                    int index = source.getInt(col);
                    this.setInt(row, col, index);
                    if (!this.columnCategories[col].hasCategory(index)) {
                        this.columnCategories[col].setCategory(index, source.getString(col));
                    }
                    break;
                default:
                    throw new RuntimeException("no types");
            }
        }

        return new Table.RowPointer(this, row);
    }

    public TableRow addRow(Object[] columnData) {
        this.setRow(this.getRowCount(), columnData);
        return new Table.RowPointer(this, this.rowCount - 1);
    }

    public void addRows(Table source) {
        int index = this.getRowCount();
        this.setRowCount(index + source.getRowCount());
        Iterator var3 = source.rows().iterator();

        while(var3.hasNext()) {
            TableRow row = (TableRow)var3.next();
            this.setRow(index++, row);
        }

    }

    public void insertRow(int insert, Object[] columnData) {
        for(int col = 0; col < this.columns.length; ++col) {
            switch(this.columnTypes[col]) {
                case 0:
                    String[] stringTemp = new String[this.rowCount + 1];
                    System.arraycopy(this.columns[col], 0, stringTemp, 0, insert);
                    System.arraycopy(this.columns[col], insert, stringTemp, insert + 1, this.rowCount - insert);
                    this.columns[col] = stringTemp;
                    break;
                case 1:
                case 5:
                    int[] intTemp = new int[this.rowCount + 1];
                    System.arraycopy(this.columns[col], 0, intTemp, 0, insert);
                    System.arraycopy(this.columns[col], insert, intTemp, insert + 1, this.rowCount - insert);
                    this.columns[col] = intTemp;
                    break;
                case 2:
                    long[] longTemp = new long[this.rowCount + 1];
                    System.arraycopy(this.columns[col], 0, longTemp, 0, insert);
                    System.arraycopy(this.columns[col], insert, longTemp, insert + 1, this.rowCount - insert);
                    this.columns[col] = longTemp;
                    break;
                case 3:
                    float[] floatTemp = new float[this.rowCount + 1];
                    System.arraycopy(this.columns[col], 0, floatTemp, 0, insert);
                    System.arraycopy(this.columns[col], insert, floatTemp, insert + 1, this.rowCount - insert);
                    this.columns[col] = floatTemp;
                    break;
                case 4:
                    double[] doubleTemp = new double[this.rowCount + 1];
                    System.arraycopy(this.columns[col], 0, doubleTemp, 0, insert);
                    System.arraycopy(this.columns[col], insert, doubleTemp, insert + 1, this.rowCount - insert);
                    this.columns[col] = doubleTemp;
            }
        }

        ++this.rowCount;
        this.setRow(insert, columnData);
    }

    public void removeRow(int row) {
        for(int col = 0; col < this.columns.length; ++col) {
            switch(this.columnTypes[col]) {
                case 0:
                    String[] stringTemp = new String[this.rowCount - 1];
                    System.arraycopy(this.columns[col], 0, stringTemp, 0, row);
                    System.arraycopy(this.columns[col], row + 1, stringTemp, row, this.rowCount - row - 1);
                    this.columns[col] = stringTemp;
                    break;
                case 1:
                case 5:
                    int[] intTemp = new int[this.rowCount - 1];
                    System.arraycopy(this.columns[col], 0, intTemp, 0, row);
                    System.arraycopy(this.columns[col], row + 1, intTemp, row, this.rowCount - row - 1);
                    this.columns[col] = intTemp;
                    break;
                case 2:
                    long[] longTemp = new long[this.rowCount - 1];
                    System.arraycopy(this.columns[col], 0, longTemp, 0, row);
                    System.arraycopy(this.columns[col], row + 1, longTemp, row, this.rowCount - row - 1);
                    this.columns[col] = longTemp;
                    break;
                case 3:
                    float[] floatTemp = new float[this.rowCount - 1];
                    System.arraycopy(this.columns[col], 0, floatTemp, 0, row);
                    System.arraycopy(this.columns[col], row + 1, floatTemp, row, this.rowCount - row - 1);
                    this.columns[col] = floatTemp;
                    break;
                case 4:
                    double[] doubleTemp = new double[this.rowCount - 1];
                    System.arraycopy(this.columns[col], 0, doubleTemp, 0, row);
                    System.arraycopy(this.columns[col], row + 1, doubleTemp, row, this.rowCount - row - 1);
                    this.columns[col] = doubleTemp;
            }
        }

        --this.rowCount;
    }

    public void setRow(int row, Object[] pieces) {
        this.ensureBounds(row, pieces.length - 1);

        for(int col = 0; col < pieces.length; ++col) {
            this.setRowCol(row, col, pieces[col]);
        }

    }

    protected void setRowCol(int row, int col, Object piece) {
        switch(this.columnTypes[col]) {
            case 0:
                String[] stringData = (String[])((String[])this.columns[col]);
                if (piece == null) {
                    stringData[row] = null;
                } else {
                    stringData[row] = String.valueOf(piece);
                }
                break;
            case 1:
                int[] intData = (int[])((int[])this.columns[col]);
                if (piece == null) {
                    intData[row] = this.missingInt;
                } else if (piece instanceof Integer) {
                    intData[row] = (Integer)piece;
                } else {
                    intData[row] = PApplet.parseInt(String.valueOf(piece), this.missingInt);
                }
                break;
            case 2:
                long[] longData = (long[])((long[])this.columns[col]);
                if (piece == null) {
                    longData[row] = this.missingLong;
                } else if (piece instanceof Long) {
                    longData[row] = (Long)piece;
                } else {
                    try {
                        longData[row] = Long.parseLong(String.valueOf(piece));
                    } catch (NumberFormatException var12) {
                        longData[row] = this.missingLong;
                    }
                }
                break;
            case 3:
                float[] floatData = (float[])((float[])this.columns[col]);
                if (piece == null) {
                    floatData[row] = this.missingFloat;
                } else if (piece instanceof Float) {
                    floatData[row] = (Float)piece;
                } else {
                    floatData[row] = PApplet.parseFloat(String.valueOf(piece), this.missingFloat);
                }
                break;
            case 4:
                double[] doubleData = (double[])((double[])this.columns[col]);
                if (piece == null) {
                    doubleData[row] = this.missingDouble;
                } else if (piece instanceof Double) {
                    doubleData[row] = (Double)piece;
                } else {
                    try {
                        doubleData[row] = Double.parseDouble(String.valueOf(piece));
                    } catch (NumberFormatException var11) {
                        doubleData[row] = this.missingDouble;
                    }
                }
                break;
            case 5:
                int[] indexData = (int[])((int[])this.columns[col]);
                if (piece == null) {
                    indexData[row] = this.missingCategory;
                } else {
                    String peace = String.valueOf(piece);
                    if (peace.equals(this.missingString)) {
                        indexData[row] = this.missingCategory;
                    } else {
                        indexData[row] = this.columnCategories[col].index(peace);
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("That's not a valid column type.");
        }

    }

    public TableRow getRow(int row) {
        return new Table.RowPointer(this, row);
    }

    public Iterable<TableRow> rows() {
        return new Iterable<TableRow>() {
            public Iterator<TableRow> iterator() {
                if (Table.this.rowIterator == null) {
                    Table.this.rowIterator = new Table.RowIterator(Table.this);
                } else {
                    Table.this.rowIterator.reset();
                }

                return Table.this.rowIterator;
            }
        };
    }

    public Iterable<TableRow> rows(final int[] indices) {
        return new Iterable<TableRow>() {
            public Iterator<TableRow> iterator() {
                return new Table.RowIndexIterator(Table.this, indices);
            }
        };
    }

    public int getInt(int row, int column) {
        this.checkBounds(row, column);
        if (this.columnTypes[column] != 1 && this.columnTypes[column] != 5) {
            String str = this.getString(row, column);
            return str != null && !str.equals(this.missingString) ? PApplet.parseInt(str, this.missingInt) : this.missingInt;
        } else {
            int[] intData = (int[])((int[])this.columns[column]);
            return intData[row];
        }
    }

    public int getInt(int row, String columnName) {
        return this.getInt(row, this.getColumnIndex(columnName));
    }

    public void setMissingInt(int value) {
        this.missingInt = value;
    }

    public void setInt(int row, int column, int value) {
        if (this.columnTypes[column] == 0) {
            this.setString(row, column, String.valueOf(value));
        } else {
            this.ensureBounds(row, column);
            if (this.columnTypes[column] != 1 && this.columnTypes[column] != 5) {
                throw new IllegalArgumentException("Column " + column + " is not an int column.");
            }

            int[] intData = (int[])((int[])this.columns[column]);
            intData[row] = value;
        }

    }

    public void setInt(int row, String columnName, int value) {
        this.setInt(row, this.getColumnIndex(columnName), value);
    }

    public int[] getIntColumn(String name) {
        int col = this.getColumnIndex(name);
        return col == -1 ? null : this.getIntColumn(col);
    }

    public int[] getIntColumn(int col) {
        int[] outgoing = new int[this.rowCount];

        for(int row = 0; row < this.rowCount; ++row) {
            outgoing[row] = this.getInt(row, col);
        }

        return outgoing;
    }

    public int[] getIntRow(int row) {
        int[] outgoing = new int[this.columns.length];

        for(int col = 0; col < this.columns.length; ++col) {
            outgoing[col] = this.getInt(row, col);
        }

        return outgoing;
    }

    public long getLong(int row, int column) {
        this.checkBounds(row, column);
        if (this.columnTypes[column] == 2) {
            long[] longData = (long[])((long[])this.columns[column]);
            return longData[row];
        } else {
            String str = this.getString(row, column);
            if (str != null && !str.equals(this.missingString)) {
                try {
                    return Long.parseLong(str);
                } catch (NumberFormatException var5) {
                    return this.missingLong;
                }
            } else {
                return this.missingLong;
            }
        }
    }

    public long getLong(int row, String columnName) {
        return this.getLong(row, this.getColumnIndex(columnName));
    }

    public void setMissingLong(long value) {
        this.missingLong = value;
    }

    public void setLong(int row, int column, long value) {
        if (this.columnTypes[column] == 0) {
            this.setString(row, column, String.valueOf(value));
        } else {
            this.ensureBounds(row, column);
            if (this.columnTypes[column] != 2) {
                throw new IllegalArgumentException("Column " + column + " is not a 'long' column.");
            }

            long[] longData = (long[])((long[])this.columns[column]);
            longData[row] = value;
        }

    }

    public void setLong(int row, String columnName, long value) {
        this.setLong(row, this.getColumnIndex(columnName), value);
    }

    public long[] getLongColumn(String name) {
        int col = this.getColumnIndex(name);
        return col == -1 ? null : this.getLongColumn(col);
    }

    public long[] getLongColumn(int col) {
        long[] outgoing = new long[this.rowCount];

        for(int row = 0; row < this.rowCount; ++row) {
            outgoing[row] = this.getLong(row, col);
        }

        return outgoing;
    }

    public long[] getLongRow(int row) {
        long[] outgoing = new long[this.columns.length];

        for(int col = 0; col < this.columns.length; ++col) {
            outgoing[col] = this.getLong(row, col);
        }

        return outgoing;
    }

    public float getFloat(int row, int column) {
        this.checkBounds(row, column);
        if (this.columnTypes[column] == 3) {
            float[] floatData = (float[])((float[])this.columns[column]);
            return floatData[row];
        } else {
            String str = this.getString(row, column);
            return str != null && !str.equals(this.missingString) ? PApplet.parseFloat(str, this.missingFloat) : this.missingFloat;
        }
    }

    public float getFloat(int row, String columnName) {
        return this.getFloat(row, this.getColumnIndex(columnName));
    }

    public void setMissingFloat(float value) {
        this.missingFloat = value;
    }

    public void setFloat(int row, int column, float value) {
        if (this.columnTypes[column] == 0) {
            this.setString(row, column, String.valueOf(value));
        } else {
            this.ensureBounds(row, column);
            if (this.columnTypes[column] != 3) {
                throw new IllegalArgumentException("Column " + column + " is not a float column.");
            }

            float[] longData = (float[])((float[])this.columns[column]);
            longData[row] = value;
        }

    }

    public void setFloat(int row, String columnName, float value) {
        this.setFloat(row, this.getColumnIndex(columnName), value);
    }

    public float[] getFloatColumn(String name) {
        int col = this.getColumnIndex(name);
        return col == -1 ? null : this.getFloatColumn(col);
    }

    public float[] getFloatColumn(int col) {
        float[] outgoing = new float[this.rowCount];

        for(int row = 0; row < this.rowCount; ++row) {
            outgoing[row] = this.getFloat(row, col);
        }

        return outgoing;
    }

    public float[] getFloatRow(int row) {
        float[] outgoing = new float[this.columns.length];

        for(int col = 0; col < this.columns.length; ++col) {
            outgoing[col] = this.getFloat(row, col);
        }

        return outgoing;
    }

    public double getDouble(int row, int column) {
        this.checkBounds(row, column);
        if (this.columnTypes[column] == 4) {
            double[] doubleData = (double[])((double[])this.columns[column]);
            return doubleData[row];
        } else {
            String str = this.getString(row, column);
            if (str != null && !str.equals(this.missingString)) {
                try {
                    return Double.parseDouble(str);
                } catch (NumberFormatException var5) {
                    return this.missingDouble;
                }
            } else {
                return this.missingDouble;
            }
        }
    }

    public double getDouble(int row, String columnName) {
        return this.getDouble(row, this.getColumnIndex(columnName));
    }

    public void setMissingDouble(double value) {
        this.missingDouble = value;
    }

    public void setDouble(int row, int column, double value) {
        if (this.columnTypes[column] == 0) {
            this.setString(row, column, String.valueOf(value));
        } else {
            this.ensureBounds(row, column);
            if (this.columnTypes[column] != 4) {
                throw new IllegalArgumentException("Column " + column + " is not a 'double' column.");
            }

            double[] doubleData = (double[])((double[])this.columns[column]);
            doubleData[row] = value;
        }

    }

    public void setDouble(int row, String columnName, double value) {
        this.setDouble(row, this.getColumnIndex(columnName), value);
    }

    public double[] getDoubleColumn(String name) {
        int col = this.getColumnIndex(name);
        return col == -1 ? null : this.getDoubleColumn(col);
    }

    public double[] getDoubleColumn(int col) {
        double[] outgoing = new double[this.rowCount];

        for(int row = 0; row < this.rowCount; ++row) {
            outgoing[row] = this.getDouble(row, col);
        }

        return outgoing;
    }

    public double[] getDoubleRow(int row) {
        double[] outgoing = new double[this.columns.length];

        for(int col = 0; col < this.columns.length; ++col) {
            outgoing[col] = this.getDouble(row, col);
        }

        return outgoing;
    }

    public String getString(int row, int column) {
        this.checkBounds(row, column);
        if (this.columnTypes[column] == 0) {
            String[] stringData = (String[])((String[])this.columns[column]);
            return stringData[row];
        } else if (this.columnTypes[column] == 5) {
            int cat = this.getInt(row, column);
            return cat == this.missingCategory ? this.missingString : this.columnCategories[column].key(cat);
        } else {
            if (this.columnTypes[column] == 3) {
                if (Float.isNaN(this.getFloat(row, column))) {
                    return null;
                }
            } else if (this.columnTypes[column] == 4 && Double.isNaN((double)this.getFloat(row, column))) {
                return null;
            }

            return String.valueOf(Array.get(this.columns[column], row));
        }
    }

    public String getString(int row, String columnName) {
        return this.getString(row, this.getColumnIndex(columnName));
    }

    public void setMissingString(String value) {
        this.missingString = value;
    }

    public void setString(int row, int column, String value) {
        this.ensureBounds(row, column);
        if (this.columnTypes[column] != 0) {
            throw new IllegalArgumentException("Column " + column + " is not a String column.");
        } else {
            String[] stringData = (String[])((String[])this.columns[column]);
            stringData[row] = value;
        }
    }

    public void setString(int row, String columnName, String value) {
        int column = this.checkColumnIndex(columnName);
        this.setString(row, column, value);
    }

    public String[] getStringColumn(String columnName) {
        int col = this.getColumnIndex(columnName);
        return col == -1 ? null : this.getStringColumn(col);
    }

    public String[] getStringColumn(int column) {
        String[] outgoing = new String[this.rowCount];

        for(int i = 0; i < this.rowCount; ++i) {
            outgoing[i] = this.getString(i, column);
        }

        return outgoing;
    }

    public String[] getStringRow(int row) {
        String[] outgoing = new String[this.columns.length];

        for(int col = 0; col < this.columns.length; ++col) {
            outgoing[col] = this.getString(row, col);
        }

        return outgoing;
    }

    public int findRowIndex(String value, int column) {
        this.checkColumn(column);
        if (this.columnTypes[column] == 0) {
            String[] stringData = (String[])((String[])this.columns[column]);
            int row;
            if (value == null) {
                for(row = 0; row < this.rowCount; ++row) {
                    if (stringData[row] == null) {
                        return row;
                    }
                }
            } else {
                for(row = 0; row < this.rowCount; ++row) {
                    if (stringData[row] != null && stringData[row].equals(value)) {
                        return row;
                    }
                }
            }
        } else {
            for(int row = 0; row < this.rowCount; ++row) {
                String str = this.getString(row, column);
                if (str == null) {
                    if (value == null) {
                        return row;
                    }
                } else if (str.equals(value)) {
                    return row;
                }
            }
        }

        return -1;
    }

    public int findRowIndex(String value, String columnName) {
        return this.findRowIndex(value, this.getColumnIndex(columnName));
    }

    public int[] findRowIndices(String value, int column) {
        int[] outgoing = new int[this.rowCount];
        int count = 0;
        this.checkColumn(column);
        if (this.columnTypes[column] == 0) {
            String[] stringData = (String[])((String[])this.columns[column]);
            int row;
            if (value == null) {
                for(row = 0; row < this.rowCount; ++row) {
                    if (stringData[row] == null) {
                        outgoing[count++] = row;
                    }
                }
            } else {
                for(row = 0; row < this.rowCount; ++row) {
                    if (stringData[row] != null && stringData[row].equals(value)) {
                        outgoing[count++] = row;
                    }
                }
            }
        } else {
            for(int row = 0; row < this.rowCount; ++row) {
                String str = this.getString(row, column);
                if (str == null) {
                    if (value == null) {
                        outgoing[count++] = row;
                    }
                } else if (str.equals(value)) {
                    outgoing[count++] = row;
                }
            }
        }

        return PApplet.subset(outgoing, 0, count);
    }

    public int[] findRowIndices(String value, String columnName) {
        return this.findRowIndices(value, this.getColumnIndex(columnName));
    }

    public TableRow findRow(String value, int column) {
        int row = this.findRowIndex(value, column);
        return row == -1 ? null : new Table.RowPointer(this, row);
    }

    public TableRow findRow(String value, String columnName) {
        return this.findRow(value, this.getColumnIndex(columnName));
    }

    public Iterable<TableRow> findRows(final String value, final int column) {
        return new Iterable<TableRow>() {
            public Iterator<TableRow> iterator() {
                return Table.this.findRowIterator(value, column);
            }
        };
    }

    public Iterable<TableRow> findRows(String value, String columnName) {
        return this.findRows(value, this.getColumnIndex(columnName));
    }

    public Iterator<TableRow> findRowIterator(String value, int column) {
        return new Table.RowIndexIterator(this, this.findRowIndices(value, column));
    }

    public Iterator<TableRow> findRowIterator(String value, String columnName) {
        return this.findRowIterator(value, this.getColumnIndex(columnName));
    }

    public int matchRowIndex(String regexp, int column) {
        this.checkColumn(column);
        if (this.columnTypes[column] == 0) {
            String[] stringData = (String[])((String[])this.columns[column]);

            for(int row = 0; row < this.rowCount; ++row) {
                if (stringData[row] != null && PApplet.match(stringData[row], regexp) != null) {
                    return row;
                }
            }
        } else {
            for(int row = 0; row < this.rowCount; ++row) {
                String str = this.getString(row, column);
                if (str != null && PApplet.match(str, regexp) != null) {
                    return row;
                }
            }
        }

        return -1;
    }

    public int matchRowIndex(String what, String columnName) {
        return this.matchRowIndex(what, this.getColumnIndex(columnName));
    }

    public int[] matchRowIndices(String regexp, int column) {
        int[] outgoing = new int[this.rowCount];
        int count = 0;
        this.checkColumn(column);
        if (this.columnTypes[column] == 0) {
            String[] stringData = (String[])((String[])this.columns[column]);

            for(int row = 0; row < this.rowCount; ++row) {
                if (stringData[row] != null && PApplet.match(stringData[row], regexp) != null) {
                    outgoing[count++] = row;
                }
            }
        } else {
            for(int row = 0; row < this.rowCount; ++row) {
                String str = this.getString(row, column);
                if (str != null && PApplet.match(str, regexp) != null) {
                    outgoing[count++] = row;
                }
            }
        }

        return PApplet.subset(outgoing, 0, count);
    }

    public int[] matchRowIndices(String what, String columnName) {
        return this.matchRowIndices(what, this.getColumnIndex(columnName));
    }

    public TableRow matchRow(String regexp, int column) {
        int row = this.matchRowIndex(regexp, column);
        return row == -1 ? null : new Table.RowPointer(this, row);
    }

    public TableRow matchRow(String regexp, String columnName) {
        return this.matchRow(regexp, this.getColumnIndex(columnName));
    }

    public Iterable<TableRow> matchRows(final String regexp, final int column) {
        return new Iterable<TableRow>() {
            public Iterator<TableRow> iterator() {
                return Table.this.matchRowIterator(regexp, column);
            }
        };
    }

    public Iterable<TableRow> matchRows(String regexp, String columnName) {
        return this.matchRows(regexp, this.getColumnIndex(columnName));
    }

    public Iterator<TableRow> matchRowIterator(String value, int column) {
        return new Table.RowIndexIterator(this, this.matchRowIndices(value, column));
    }

    public Iterator<TableRow> matchRowIterator(String value, String columnName) {
        return this.matchRowIterator(value, this.getColumnIndex(columnName));
    }

    public void replace(String orig, String replacement) {
        for(int col = 0; col < this.columns.length; ++col) {
            this.replace(orig, replacement, col);
        }

    }

    public void replace(String orig, String replacement, int col) {
        if (this.columnTypes[col] == 0) {
            String[] stringData = (String[])((String[])this.columns[col]);
            int row;
            if (orig != null) {
                for(row = 0; row < this.rowCount; ++row) {
                    if (orig.equals(stringData[row])) {
                        stringData[row] = replacement;
                    }
                }
            } else {
                for(row = 0; row < this.rowCount; ++row) {
                    if (stringData[row] == null) {
                        stringData[row] = replacement;
                    }
                }
            }
        }

    }

    public void replace(String orig, String replacement, String colName) {
        this.replace(orig, replacement, this.getColumnIndex(colName));
    }

    public void replaceAll(String regex, String replacement) {
        for(int col = 0; col < this.columns.length; ++col) {
            this.replaceAll(regex, replacement, col);
        }

    }

    public void replaceAll(String regex, String replacement, int column) {
        this.checkColumn(column);
        if (this.columnTypes[column] == 0) {
            String[] stringData = (String[])((String[])this.columns[column]);

            for(int row = 0; row < this.rowCount; ++row) {
                if (stringData[row] != null) {
                    stringData[row] = stringData[row].replaceAll(regex, replacement);
                }
            }

        } else {
            throw new IllegalArgumentException("replaceAll() can only be used on String columns");
        }
    }

    public void replaceAll(String regex, String replacement, String columnName) {
        this.replaceAll(regex, replacement, this.getColumnIndex(columnName));
    }

    public void removeTokens(String tokens) {
        for(int col = 0; col < this.getColumnCount(); ++col) {
            this.removeTokens(tokens, col);
        }

    }

    public void removeTokens(String tokens, int column) {
        for(int row = 0; row < this.rowCount; ++row) {
            String s = this.getString(row, column);
            if (s != null) {
                char[] c = s.toCharArray();
                int index = 0;

                for(int j = 0; j < c.length; ++j) {
                    if (tokens.indexOf(c[j]) == -1) {
                        if (index != j) {
                            c[index] = c[j];
                        }

                        ++index;
                    }
                }

                if (index != c.length) {
                    this.setString(row, column, new String(c, 0, index));
                }
            }
        }

    }

    public void removeTokens(String tokens, String columnName) {
        this.removeTokens(tokens, this.getColumnIndex(columnName));
    }

    public void trim() {
        this.columnTitles = PApplet.trim(this.columnTitles);

        int lastColumn;
        for(lastColumn = 0; lastColumn < this.getColumnCount(); ++lastColumn) {
            this.trim(lastColumn);
        }

        for(lastColumn = this.getColumnCount() - 1; this.isEmptyArray(this.getStringColumn(lastColumn)) && lastColumn >= 0; --lastColumn) {
        }

        this.setColumnCount(lastColumn + 1);

        while(this.getColumnCount() > 0 && this.isEmptyArray(this.getStringColumn(0))) {
            this.removeColumn(0);
        }

        int lastRow;
        for(lastRow = this.lastRowIndex(); this.isEmptyArray(this.getStringRow(lastRow)) && lastRow >= 0; --lastRow) {
        }

        this.setRowCount(lastRow + 1);

        while(this.getRowCount() > 0 && this.isEmptyArray(this.getStringRow(0))) {
            this.removeRow(0);
        }

    }

    protected boolean isEmptyArray(String[] contents) {
        String[] var2 = contents;
        int var3 = contents.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String entry = var2[var4];
            if (entry != null && entry.length() > 0) {
                return false;
            }
        }

        return true;
    }

    public void trim(int column) {
        if (this.columnTypes[column] == 0) {
            String[] stringData = (String[])((String[])this.columns[column]);

            for(int row = 0; row < this.rowCount; ++row) {
                if (stringData[row] != null) {
                    stringData[row] = PApplet.trim(stringData[row]);
                }
            }
        }

    }

    public void trim(String columnName) {
        this.trim(this.getColumnIndex(columnName));
    }

    protected void ensureColumn(int col) {
        if (col >= this.columns.length) {
            this.setColumnCount(col + 1);
        }

    }

    protected void ensureRow(int row) {
        if (row >= this.rowCount) {
            this.setRowCount(row + 1);
        }

    }

    protected void ensureBounds(int row, int col) {
        this.ensureRow(row);
        this.ensureColumn(col);
    }

    protected void checkRow(int row) {
        if (row < 0 || row >= this.rowCount) {
            throw new ArrayIndexOutOfBoundsException("Row " + row + " does not exist.");
        }
    }

    protected void checkColumn(int column) {
        if (column < 0 || column >= this.columns.length) {
            throw new ArrayIndexOutOfBoundsException("Column " + column + " does not exist.");
        }
    }

    protected void checkBounds(int row, int column) {
        this.checkRow(row);
        this.checkColumn(column);
    }

    public void sort(String columnName) {
        this.sort(this.getColumnIndex(columnName), false);
    }

    public void sort(int column) {
        this.sort(column, false);
    }

    public void sortReverse(String columnName) {
        this.sort(this.getColumnIndex(columnName), true);
    }

    public void sortReverse(int column) {
        this.sort(column, true);
    }

    protected void sort(final int column, final boolean reverse) {
        final int[] order = IntList.fromRange(this.getRowCount()).array();
        Sort s = new Sort() {
            public int size() {
                return Table.this.getRowCount();
            }

            public int compare(int index1, int index2) {
                int a = reverse ? order[index2] : order[index1];
                int b = reverse ? order[index1] : order[index2];
                switch(Table.this.getColumnType(column)) {
                    case 0:
                        String string1 = Table.this.getString(a, column);
                        if (string1 == null) {
                            string1 = "";
                        }

                        String string2 = Table.this.getString(b, column);
                        if (string2 == null) {
                            string2 = "";
                        }

                        return string1.compareToIgnoreCase(string2);
                    case 1:
                        return Table.this.getInt(a, column) - Table.this.getInt(b, column);
                    case 2:
                        long diffl = Table.this.getLong(a, column) - Table.this.getLong(b, column);
                        return diffl == 0L ? 0 : (diffl < 0L ? -1 : 1);
                    case 3:
                        float difff = Table.this.getFloat(a, column) - Table.this.getFloat(b, column);
                        return difff == 0.0F ? 0 : (difff < 0.0F ? -1 : 1);
                    case 4:
                        double diffd = Table.this.getDouble(a, column) - Table.this.getDouble(b, column);
                        return diffd == 0.0D ? 0 : (diffd < 0.0D ? -1 : 1);
                    case 5:
                        return Table.this.getInt(a, column) - Table.this.getInt(b, column);
                    default:
                        throw new IllegalArgumentException("Invalid column type: " + Table.this.getColumnType(column));
                }
            }

            public void swap(int a, int b) {
                int temp = order[a];
                order[a] = order[b];
                order[b] = temp;
            }
        };
        s.run();

        for(int col = 0; col < this.getColumnCount(); ++col) {
            switch(this.getColumnType(col)) {
                case 0:
                    String[] oldString = (String[])((String[])this.columns[col]);
                    String[] newString = new String[this.rowCount];

                    for(int row = 0; row < this.getRowCount(); ++row) {
                        newString[row] = oldString[order[row]];
                    }

                    this.columns[col] = newString;
                    break;
                case 1:
                case 5:
                    int[] oldInt = (int[])((int[])this.columns[col]);
                    int[] newInt = new int[this.rowCount];

                    for(int row = 0; row < this.getRowCount(); ++row) {
                        newInt[row] = oldInt[order[row]];
                    }

                    this.columns[col] = newInt;
                    break;
                case 2:
                    long[] oldLong = (long[])((long[])this.columns[col]);
                    long[] newLong = new long[this.rowCount];

                    for(int row = 0; row < this.getRowCount(); ++row) {
                        newLong[row] = oldLong[order[row]];
                    }

                    this.columns[col] = newLong;
                    break;
                case 3:
                    float[] oldFloat = (float[])((float[])this.columns[col]);
                    float[] newFloat = new float[this.rowCount];

                    for(int row = 0; row < this.getRowCount(); ++row) {
                        newFloat[row] = oldFloat[order[row]];
                    }

                    this.columns[col] = newFloat;
                    break;
                case 4:
                    double[] oldDouble = (double[])((double[])this.columns[col]);
                    double[] newDouble = new double[this.rowCount];

                    for(int row = 0; row < this.getRowCount(); ++row) {
                        newDouble[row] = oldDouble[order[row]];
                    }

                    this.columns[col] = newDouble;
            }
        }

    }

    public String[] getUnique(String columnName) {
        return this.getUnique(this.getColumnIndex(columnName));
    }

    public String[] getUnique(int column) {
        StringList list = new StringList(this.getStringColumn(column));
        return list.getUnique();
    }

    public IntDict getTally(String columnName) {
        return this.getTally(this.getColumnIndex(columnName));
    }

    public IntDict getTally(int column) {
        StringList list = new StringList(this.getStringColumn(column));
        return list.getTally();
    }

    public IntDict getOrder(String columnName) {
        return this.getOrder(this.getColumnIndex(columnName));
    }

    public IntDict getOrder(int column) {
        StringList list = new StringList(this.getStringColumn(column));
        return list.getOrder();
    }

    public IntList getIntList(String columnName) {
        return new IntList(this.getIntColumn(columnName));
    }

    public IntList getIntList(int column) {
        return new IntList(this.getIntColumn(column));
    }

    public FloatList getFloatList(String columnName) {
        return new FloatList(this.getFloatColumn(columnName));
    }

    public FloatList getFloatList(int column) {
        return new FloatList(this.getFloatColumn(column));
    }

    public StringList getStringList(String columnName) {
        return new StringList(this.getStringColumn(columnName));
    }

    public StringList getStringList(int column) {
        return new StringList(this.getStringColumn(column));
    }

    public IntDict getIntDict(String keyColumnName, String valueColumnName) {
        return new IntDict(this.getStringColumn(keyColumnName), this.getIntColumn(valueColumnName));
    }

    public IntDict getIntDict(int keyColumn, int valueColumn) {
        return new IntDict(this.getStringColumn(keyColumn), this.getIntColumn(valueColumn));
    }

    public FloatDict getFloatDict(String keyColumnName, String valueColumnName) {
        return new FloatDict(this.getStringColumn(keyColumnName), this.getFloatColumn(valueColumnName));
    }

    public FloatDict getFloatDict(int keyColumn, int valueColumn) {
        return new FloatDict(this.getStringColumn(keyColumn), this.getFloatColumn(valueColumn));
    }

    public StringDict getStringDict(String keyColumnName, String valueColumnName) {
        return new StringDict(this.getStringColumn(keyColumnName), this.getStringColumn(valueColumnName));
    }

    public StringDict getStringDict(int keyColumn, int valueColumn) {
        return new StringDict(this.getStringColumn(keyColumn), this.getStringColumn(valueColumn));
    }

    public Map<String, TableRow> getRowMap(String columnName) {
        int col = this.getColumnIndex(columnName);
        return col == -1 ? null : this.getRowMap(col);
    }

    public Map<String, TableRow> getRowMap(int column) {
        Map<String, TableRow> outgoing = new HashMap();

        for(int row = 0; row < this.getRowCount(); ++row) {
            String id = this.getString(row, column);
            outgoing.put(id, new Table.RowPointer(this, row));
        }

        return outgoing;
    }

    protected Table createSubset(int[] rowSubset) {
        Table newbie = new Table();
        newbie.setColumnTitles(this.columnTitles);
        newbie.columnTypes = this.columnTypes;
        newbie.setRowCount(rowSubset.length);

        for(int i = 0; i < rowSubset.length; ++i) {
            int row = rowSubset[i];

            for(int col = 0; col < this.columns.length; ++col) {
                switch(this.columnTypes[col]) {
                    case 0:
                        newbie.setString(i, col, this.getString(row, col));
                        break;
                    case 1:
                        newbie.setInt(i, col, this.getInt(row, col));
                        break;
                    case 2:
                        newbie.setLong(i, col, this.getLong(row, col));
                        break;
                    case 3:
                        newbie.setFloat(i, col, this.getFloat(row, col));
                        break;
                    case 4:
                        newbie.setDouble(i, col, this.getDouble(row, col));
                }
            }
        }

        return newbie;
    }

    protected float getMaxFloat() {
        boolean found = false;
        float max = -3.4028235E38F;

        for(int row = 0; row < this.getRowCount(); ++row) {
            for(int col = 0; col < this.getColumnCount(); ++col) {
                float value = this.getFloat(row, col);
                if (!Float.isNaN(value)) {
                    if (!found) {
                        max = value;
                        found = true;
                    } else if (value > max) {
                        max = value;
                    }
                }
            }
        }

        return found ? max : this.missingFloat;
    }

    protected void convertBasic(BufferedReader reader, boolean tsv, File outputFile) throws IOException {
        FileOutputStream fos = new FileOutputStream(outputFile);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 16384);
        DataOutputStream output = new DataOutputStream(bos);
        output.writeInt(0);
        output.writeInt(this.getColumnCount());
        String[] line;
        int prev;
        int row;
        if (this.columnTitles != null) {
            output.writeBoolean(true);
            line = this.columnTitles;
            prev = line.length;

            for(row = 0; row < prev; ++row) {
                String title = line[row];
                output.writeUTF(title);
            }
        } else {
            output.writeBoolean(false);
        }

        int[] var15 = this.columnTypes;
        prev = var15.length;

        int col;
        for(row = 0; row < prev; ++row) {
            col = var15[row];
            output.writeInt(col);
        }

        line = null;
        prev = -1;
        row = 0;

        String line;
        while((line = reader.readLine()) != null) {
            this.convertRow(output, tsv ? PApplet.split(line, '\t') : this.splitLineCSV(line, reader));
            ++row;
            if (row % 10000 == 0 && row < this.rowCount) {
                col = 100 * row / this.rowCount;
                if (col != prev) {
                    System.out.println(col + "%");
                    prev = col;
                }
            }
        }

        col = 0;
        Table.HashMapBlows[] var11 = this.columnCategories;
        int var12 = var11.length;

        for(int var13 = 0; var13 < var12; ++var13) {
            Table.HashMapBlows hmb = var11[var13];
            if (hmb == null) {
                output.writeInt(0);
            } else {
                hmb.write(output);
                hmb.writeln(PApplet.createWriter(new File(this.columnTitles[col] + ".categories")));
            }

            ++col;
        }

        output.flush();
        output.close();
        RandomAccessFile raf = new RandomAccessFile(outputFile, "rw");
        raf.writeInt(this.rowCount);
        raf.close();
    }

    protected void convertRow(DataOutputStream output, String[] pieces) throws IOException {
        if (pieces.length > this.getColumnCount()) {
            throw new IllegalArgumentException("Row with too many columns: " + PApplet.join(pieces, ","));
        } else {
            int col;
            for(col = 0; col < pieces.length; ++col) {
                switch(this.columnTypes[col]) {
                    case 0:
                        output.writeUTF(pieces[col]);
                        break;
                    case 1:
                        output.writeInt(PApplet.parseInt(pieces[col], this.missingInt));
                        break;
                    case 2:
                        try {
                            output.writeLong(Long.parseLong(pieces[col]));
                        } catch (NumberFormatException var6) {
                            output.writeLong(this.missingLong);
                        }
                        break;
                    case 3:
                        output.writeFloat(PApplet.parseFloat(pieces[col], this.missingFloat));
                        break;
                    case 4:
                        try {
                            output.writeDouble(Double.parseDouble(pieces[col]));
                        } catch (NumberFormatException var5) {
                            output.writeDouble(this.missingDouble);
                        }
                        break;
                    case 5:
                        String peace = pieces[col];
                        if (peace.equals(this.missingString)) {
                            output.writeInt(this.missingCategory);
                        } else {
                            output.writeInt(this.columnCategories[col].index(peace));
                        }
                }
            }

            for(col = pieces.length; col < this.getColumnCount(); ++col) {
                switch(this.columnTypes[col]) {
                    case 0:
                        output.writeUTF("");
                        break;
                    case 1:
                        output.writeInt(this.missingInt);
                        break;
                    case 2:
                        output.writeLong(this.missingLong);
                        break;
                    case 3:
                        output.writeFloat(this.missingFloat);
                        break;
                    case 4:
                        output.writeDouble(this.missingDouble);
                        break;
                    case 5:
                        output.writeInt(this.missingCategory);
                }
            }

        }
    }

    public Table copy() {
        return new Table(this.rows());
    }

    public void write(PrintWriter writer) {
        this.writeTSV(writer);
    }

    public void print() {
        this.writeTSV(new PrintWriter(System.out));
    }

    static class HashMapBlows {
        HashMap<String, Integer> dataToIndex = new HashMap();
        ArrayList<String> indexToData = new ArrayList();

        HashMapBlows() {
        }

        HashMapBlows(DataInputStream input) throws IOException {
            this.read(input);
        }

        int index(String key) {
            Integer value = (Integer)this.dataToIndex.get(key);
            if (value != null) {
                return value;
            } else {
                int v = this.dataToIndex.size();
                this.dataToIndex.put(key, v);
                this.indexToData.add(key);
                return v;
            }
        }

        String key(int index) {
            return (String)this.indexToData.get(index);
        }

        boolean hasCategory(int index) {
            return index < this.size() && this.indexToData.get(index) != null;
        }

        void setCategory(int index, String name) {
            while(this.indexToData.size() <= index) {
                this.indexToData.add((Object)null);
            }

            this.indexToData.set(index, name);
            this.dataToIndex.put(name, index);
        }

        int size() {
            return this.dataToIndex.size();
        }

        void write(DataOutputStream output) throws IOException {
            output.writeInt(this.size());
            Iterator var2 = this.indexToData.iterator();

            while(var2.hasNext()) {
                String str = (String)var2.next();
                output.writeUTF(str);
            }

        }

        private void writeln(PrintWriter writer) throws IOException {
            Iterator var2 = this.indexToData.iterator();

            while(var2.hasNext()) {
                String str = (String)var2.next();
                writer.println(str);
            }

            writer.flush();
            writer.close();
        }

        void read(DataInputStream input) throws IOException {
            int count = input.readInt();
            this.dataToIndex = new HashMap(count);

            for(int i = 0; i < count; ++i) {
                String str = input.readUTF();
                this.dataToIndex.put(str, i);
                this.indexToData.add(str);
            }

        }
    }

    static class RowIndexIterator implements Iterator<TableRow> {
        Table table;
        Table.RowPointer rp;
        int[] indices;
        int index;

        public RowIndexIterator(Table table, int[] indices) {
            this.table = table;
            this.indices = indices;
            this.index = -1;
            this.rp = new Table.RowPointer(table, -1);
        }

        public void remove() {
            this.table.removeRow(this.indices[this.index]);
        }

        public TableRow next() {
            this.rp.setRow(this.indices[++this.index]);
            return this.rp;
        }

        public boolean hasNext() {
            return this.index + 1 < this.indices.length;
        }

        public void reset() {
            this.index = -1;
        }
    }

    static class RowIterator implements Iterator<TableRow> {
        Table table;
        Table.RowPointer rp;
        int row;

        public RowIterator(Table table) {
            this.table = table;
            this.row = -1;
            this.rp = new Table.RowPointer(table, this.row);
        }

        public void remove() {
            this.table.removeRow(this.row);
        }

        public TableRow next() {
            this.rp.setRow(++this.row);
            return this.rp;
        }

        public boolean hasNext() {
            return this.row + 1 < this.table.getRowCount();
        }

        public void reset() {
            this.row = -1;
        }
    }

    static class RowPointer implements TableRow {
        Table table;
        int row;

        public RowPointer(Table table, int row) {
            this.table = table;
            this.row = row;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public String getString(int column) {
            return this.table.getString(this.row, column);
        }

        public String getString(String columnName) {
            return this.table.getString(this.row, columnName);
        }

        public int getInt(int column) {
            return this.table.getInt(this.row, column);
        }

        public int getInt(String columnName) {
            return this.table.getInt(this.row, columnName);
        }

        public long getLong(int column) {
            return this.table.getLong(this.row, column);
        }

        public long getLong(String columnName) {
            return this.table.getLong(this.row, columnName);
        }

        public float getFloat(int column) {
            return this.table.getFloat(this.row, column);
        }

        public float getFloat(String columnName) {
            return this.table.getFloat(this.row, columnName);
        }

        public double getDouble(int column) {
            return this.table.getDouble(this.row, column);
        }

        public double getDouble(String columnName) {
            return this.table.getDouble(this.row, columnName);
        }

        public void setString(int column, String value) {
            this.table.setString(this.row, column, value);
        }

        public void setString(String columnName, String value) {
            this.table.setString(this.row, columnName, value);
        }

        public void setInt(int column, int value) {
            this.table.setInt(this.row, column, value);
        }

        public void setInt(String columnName, int value) {
            this.table.setInt(this.row, columnName, value);
        }

        public void setLong(int column, long value) {
            this.table.setLong(this.row, column, value);
        }

        public void setLong(String columnName, long value) {
            this.table.setLong(this.row, columnName, value);
        }

        public void setFloat(int column, float value) {
            this.table.setFloat(this.row, column, value);
        }

        public void setFloat(String columnName, float value) {
            this.table.setFloat(this.row, columnName, value);
        }

        public void setDouble(int column, double value) {
            this.table.setDouble(this.row, column, value);
        }

        public void setDouble(String columnName, double value) {
            this.table.setDouble(this.row, columnName, value);
        }

        public int getColumnCount() {
            return this.table.getColumnCount();
        }

        public int getColumnType(String columnName) {
            return this.table.getColumnType(columnName);
        }

        public int getColumnType(int column) {
            return this.table.getColumnType(column);
        }

        public int[] getColumnTypes() {
            return this.table.getColumnTypes();
        }

        public String getColumnTitle(int column) {
            return this.table.getColumnTitle(column);
        }

        public String[] getColumnTitles() {
            return this.table.getColumnTitles();
        }

        public void print() {
            this.write(new PrintWriter(System.out));
        }

        public void write(PrintWriter writer) {
            for(int i = 0; i < this.getColumnCount(); ++i) {
                if (i != 0) {
                    writer.print('\t');
                }

                writer.print(this.getString(i));
            }

        }
    }

    static class CommaSeparatedLine {
        char[] c;
        String[] pieces;
        int pieceCount;
        int start;

        CommaSeparatedLine() {
        }

        String[] handle(String line, BufferedReader reader) throws IOException {
            this.start = 0;
            this.pieceCount = 0;
            this.c = line.toCharArray();
            int cols = 1;
            boolean quote = false;

            int i;
            for(i = 0; i < this.c.length; ++i) {
                if (!quote && this.c[i] == ',') {
                    ++cols;
                } else if (this.c[i] == '"') {
                    quote = !quote;
                }
            }

            this.pieces = new String[cols];

            while(this.start < this.c.length) {
                boolean enough = this.ingest();
                if (!enough) {
                    String nextLine = reader.readLine();
                    if (nextLine == null) {
                        throw new IOException("Found a quoted line that wasn't terminated properly.");
                    }

                    char[] temp = new char[this.c.length + 1 + nextLine.length()];
                    PApplet.arrayCopy(this.c, temp, this.c.length);
                    temp[this.c.length] = '\n';
                    nextLine.getChars(0, nextLine.length(), temp, this.c.length + 1);
                    return this.handle(new String(temp), reader);
                }
            }

            for(i = this.pieceCount; i < this.pieces.length; ++i) {
                this.pieces[i] = "";
            }

            return this.pieces;
        }

        protected void addPiece(int start, int stop, boolean quotes) {
            if (quotes) {
                int dest = start;

                for(int i = start; i < stop; ++i) {
                    if (this.c[i] == '"') {
                        ++i;
                    }

                    if (i != dest) {
                        this.c[dest] = this.c[i];
                    }

                    ++dest;
                }

                this.pieces[this.pieceCount++] = new String(this.c, start, dest - start);
            } else {
                this.pieces[this.pieceCount++] = new String(this.c, start, stop - start);
            }

        }

        protected boolean ingest() {
            boolean hasEscapedQuotes = false;
            boolean quoted = this.c[this.start] == '"';
            if (quoted) {
                ++this.start;
            }

            int i = this.start;

            while(i < this.c.length) {
                if (this.c[i] == '"') {
                    if (quoted) {
                        if (i == this.c.length - 1) {
                            this.addPiece(this.start, i, hasEscapedQuotes);
                            this.start = this.c.length;
                            return true;
                        }

                        if (this.c[i + 1] == '"') {
                            hasEscapedQuotes = true;
                            i += 2;
                        } else {
                            if (this.c[i + 1] == ',') {
                                this.addPiece(this.start, i, hasEscapedQuotes);
                                this.start = i + 2;
                                return true;
                            }

                            ++i;
                        }
                    } else {
                        if (i == this.c.length - 1) {
                            throw new RuntimeException("Unterminated quote at end of line");
                        }

                        if (this.c[i + 1] != '"') {
                            throw new RuntimeException("Unterminated quoted field mid-line");
                        }

                        hasEscapedQuotes = true;
                        i += 2;
                    }
                } else {
                    if (!quoted && this.c[i] == ',') {
                        this.addPiece(this.start, i, hasEscapedQuotes);
                        this.start = i + 1;
                        return true;
                    }

                    if (!quoted && i == this.c.length - 1) {
                        this.addPiece(this.start, this.c.length, hasEscapedQuotes);
                        this.start = this.c.length;
                        return true;
                    }

                    ++i;
                }
            }

            if (quoted) {
                return false;
            } else {
                throw new RuntimeException("not sure how...");
            }
        }
    }
}
