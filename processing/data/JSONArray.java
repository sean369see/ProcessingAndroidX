package processing.data;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import processing.core.PApplet;

public class JSONArray {
    private final ArrayList<Object> myArrayList;

    public JSONArray() {
        this.myArrayList = new ArrayList();
    }

    public JSONArray(Reader reader) {
        this(new JSONTokener(reader));
    }

    protected JSONArray(JSONTokener x) {
        this();
        if (x.nextClean() != '[') {
            throw new RuntimeException("A JSONArray text must start with '['");
        } else if (x.nextClean() != ']') {
            x.back();

            while(true) {
                if (x.nextClean() == ',') {
                    x.back();
                    this.myArrayList.add(JSONObject.NULL);
                } else {
                    x.back();
                    this.myArrayList.add(x.nextValue());
                }

                switch(x.nextClean()) {
                    case ',':
                    case ';':
                        if (x.nextClean() == ']') {
                            return;
                        }

                        x.back();
                        break;
                    case ']':
                        return;
                    default:
                        throw new RuntimeException("Expected a ',' or ']'");
                }
            }
        }
    }

    public JSONArray(IntList list) {
        this.myArrayList = new ArrayList();
        int[] var2 = list.values();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            int item = var2[var4];
            this.myArrayList.add(item);
        }

    }

    public JSONArray(FloatList list) {
        this.myArrayList = new ArrayList();
        float[] var2 = list.values();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            float item = var2[var4];
            this.myArrayList.add(item);
        }

    }

    public JSONArray(StringList list) {
        this.myArrayList = new ArrayList();
        String[] var2 = list.values();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String item = var2[var4];
            this.myArrayList.add(item);
        }

    }

    public static JSONArray parse(String source) {
        try {
            return new JSONArray(new JSONTokener(source));
        } catch (Exception var2) {
            return null;
        }
    }

    protected JSONArray(Object array) {
        this();
        if (!array.getClass().isArray()) {
            throw new RuntimeException("JSONArray initial value should be a string or collection or array.");
        } else {
            int length = Array.getLength(array);

            for(int i = 0; i < length; ++i) {
                this.append(JSONObject.wrap(Array.get(array, i)));
            }

        }
    }

    private Object opt(int index) {
        return index >= 0 && index < this.size() ? this.myArrayList.get(index) : null;
    }

    public Object get(int index) {
        Object object = this.opt(index);
        if (object == null) {
            throw new RuntimeException("JSONArray[" + index + "] not found.");
        } else {
            return object;
        }
    }

    public String getString(int index) {
        Object object = this.get(index);
        if (object instanceof String) {
            return (String)object;
        } else {
            throw new RuntimeException("JSONArray[" + index + "] not a string.");
        }
    }

    public String getString(int index, String defaultValue) {
        Object object = this.opt(index);
        return JSONObject.NULL.equals(object) ? defaultValue : object.toString();
    }

    public int getInt(int index) {
        Object object = this.get(index);

        try {
            return object instanceof Number ? ((Number)object).intValue() : Integer.parseInt((String)object);
        } catch (Exception var4) {
            throw new RuntimeException("JSONArray[" + index + "] is not a number.");
        }
    }

    public int getInt(int index, int defaultValue) {
        try {
            return this.getInt(index);
        } catch (Exception var4) {
            return defaultValue;
        }
    }

    public long getLong(int index) {
        Object object = this.get(index);

        try {
            return object instanceof Number ? ((Number)object).longValue() : Long.parseLong((String)object);
        } catch (Exception var4) {
            throw new RuntimeException("JSONArray[" + index + "] is not a number.");
        }
    }

    public long getLong(int index, long defaultValue) {
        try {
            return this.getLong(index);
        } catch (Exception var5) {
            return defaultValue;
        }
    }

    public float getFloat(int index) {
        return (float)this.getDouble(index);
    }

    public float getFloat(int index, float defaultValue) {
        try {
            return this.getFloat(index);
        } catch (Exception var4) {
            return defaultValue;
        }
    }

    public double getDouble(int index) {
        Object object = this.get(index);

        try {
            return object instanceof Number ? ((Number)object).doubleValue() : Double.parseDouble((String)object);
        } catch (Exception var4) {
            throw new RuntimeException("JSONArray[" + index + "] is not a number.");
        }
    }

    public double getDouble(int index, double defaultValue) {
        try {
            return this.getDouble(index);
        } catch (Exception var5) {
            return defaultValue;
        }
    }

    public boolean getBoolean(int index) {
        Object object = this.get(index);
        if (!object.equals(Boolean.FALSE) && (!(object instanceof String) || !((String)object).equalsIgnoreCase("false"))) {
            if (!object.equals(Boolean.TRUE) && (!(object instanceof String) || !((String)object).equalsIgnoreCase("true"))) {
                throw new RuntimeException("JSONArray[" + index + "] is not a boolean.");
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public boolean getBoolean(int index, boolean defaultValue) {
        try {
            return this.getBoolean(index);
        } catch (Exception var4) {
            return defaultValue;
        }
    }

    public JSONArray getJSONArray(int index) {
        Object object = this.get(index);
        if (object instanceof JSONArray) {
            return (JSONArray)object;
        } else {
            throw new RuntimeException("JSONArray[" + index + "] is not a JSONArray.");
        }
    }

    public JSONArray getJSONArray(int index, JSONArray defaultValue) {
        try {
            return this.getJSONArray(index);
        } catch (Exception var4) {
            return defaultValue;
        }
    }

    public JSONObject getJSONObject(int index) {
        Object object = this.get(index);
        if (object instanceof JSONObject) {
            return (JSONObject)object;
        } else {
            throw new RuntimeException("JSONArray[" + index + "] is not a JSONObject.");
        }
    }

    public JSONObject getJSONObject(int index, JSONObject defaultValue) {
        try {
            return this.getJSONObject(index);
        } catch (Exception var4) {
            return defaultValue;
        }
    }

    public String[] getStringArray() {
        String[] outgoing = new String[this.size()];

        for(int i = 0; i < this.size(); ++i) {
            outgoing[i] = this.getString(i);
        }

        return outgoing;
    }

    public int[] getIntArray() {
        int[] outgoing = new int[this.size()];

        for(int i = 0; i < this.size(); ++i) {
            outgoing[i] = this.getInt(i);
        }

        return outgoing;
    }

    public long[] getLongArray() {
        long[] outgoing = new long[this.size()];

        for(int i = 0; i < this.size(); ++i) {
            outgoing[i] = this.getLong(i);
        }

        return outgoing;
    }

    public float[] getFloatArray() {
        float[] outgoing = new float[this.size()];

        for(int i = 0; i < this.size(); ++i) {
            outgoing[i] = this.getFloat(i);
        }

        return outgoing;
    }

    public double[] getDoubleArray() {
        double[] outgoing = new double[this.size()];

        for(int i = 0; i < this.size(); ++i) {
            outgoing[i] = this.getDouble(i);
        }

        return outgoing;
    }

    public boolean[] getBooleanArray() {
        boolean[] outgoing = new boolean[this.size()];

        for(int i = 0; i < this.size(); ++i) {
            outgoing[i] = this.getBoolean(i);
        }

        return outgoing;
    }

    public JSONArray append(String value) {
        this.append((Object)value);
        return this;
    }

    public JSONArray append(int value) {
        this.append((Object)value);
        return this;
    }

    public JSONArray append(long value) {
        this.append((Object)value);
        return this;
    }

    public JSONArray append(float value) {
        return this.append((double)value);
    }

    public JSONArray append(double value) {
        Double d = value;
        JSONObject.testValidity(d);
        this.append((Object)d);
        return this;
    }

    public JSONArray append(boolean value) {
        this.append((Object)(value ? Boolean.TRUE : Boolean.FALSE));
        return this;
    }

    public JSONArray append(JSONArray value) {
        this.myArrayList.add(value);
        return this;
    }

    public JSONArray append(JSONObject value) {
        this.myArrayList.add(value);
        return this;
    }

    protected JSONArray append(Object value) {
        this.myArrayList.add(value);
        return this;
    }

    public JSONArray setString(int index, String value) {
        this.set(index, value);
        return this;
    }

    public JSONArray setInt(int index, int value) {
        this.set(index, value);
        return this;
    }

    public JSONArray setLong(int index, long value) {
        return this.set(index, value);
    }

    public JSONArray setFloat(int index, float value) {
        return this.setDouble(index, (double)value);
    }

    public JSONArray setDouble(int index, double value) {
        return this.set(index, value);
    }

    public JSONArray setBoolean(int index, boolean value) {
        return this.set(index, value ? Boolean.TRUE : Boolean.FALSE);
    }

    public JSONArray setJSONArray(int index, JSONArray value) {
        this.set(index, value);
        return this;
    }

    public JSONArray setJSONObject(int index, JSONObject value) {
        this.set(index, value);
        return this;
    }

    private JSONArray set(int index, Object value) {
        JSONObject.testValidity(value);
        if (index < 0) {
            throw new RuntimeException("JSONArray[" + index + "] not found.");
        } else {
            if (index < this.size()) {
                this.myArrayList.set(index, value);
            } else {
                while(index != this.size()) {
                    this.append(JSONObject.NULL);
                }

                this.append(value);
            }

            return this;
        }
    }

    public int size() {
        return this.myArrayList.size();
    }

    public boolean isNull(int index) {
        return JSONObject.NULL.equals(this.opt(index));
    }

    public Object remove(int index) {
        Object o = this.opt(index);
        this.myArrayList.remove(index);
        return o;
    }

    public boolean save(File file, String options) {
        PrintWriter writer = PApplet.createWriter(file);
        boolean success = this.write(writer, options);
        writer.close();
        return success;
    }

    public boolean write(PrintWriter output) {
        return this.write(output, (String)null);
    }

    public boolean write(PrintWriter output, String options) {
        int indentFactor = 2;
        if (options != null) {
            String[] opts = PApplet.split(options, ',');
            String[] var5 = opts;
            int var6 = opts.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                String opt = var5[var7];
                if (opt.equals("compact")) {
                    indentFactor = -1;
                } else if (opt.startsWith("indent=")) {
                    indentFactor = PApplet.parseInt(opt.substring(7), -2);
                    if (indentFactor == -2) {
                        throw new IllegalArgumentException("Could not read a number from " + opt);
                    }
                } else {
                    System.err.println("Ignoring " + opt);
                }
            }
        }

        output.print(this.format(indentFactor));
        output.flush();
        return true;
    }

    public String toString() {
        try {
            return this.format(2);
        } catch (Exception var2) {
            return null;
        }
    }

    public String format(int indentFactor) {
        StringWriter sw = new StringWriter();
        synchronized(sw.getBuffer()) {
            return this.writeInternal(sw, indentFactor, 0).toString();
        }
    }

    protected Writer writeInternal(Writer writer, int indentFactor, int indent) {
        try {
            boolean commanate = false;
            int length = this.size();
            writer.write(91);
            int thisFactor = indentFactor == -1 ? 0 : indentFactor;
            if (length == 1) {
                JSONObject.writeValue(writer, this.myArrayList.get(0), indentFactor, indent);
            } else if (length != 0) {
                int newIndent = indent + thisFactor;

                for(int i = 0; i < length; ++i) {
                    if (commanate) {
                        writer.write(44);
                    }

                    if (indentFactor != -1) {
                        writer.write(10);
                    }

                    JSONObject.indent(writer, newIndent);
                    JSONObject.writeValue(writer, this.myArrayList.get(i), indentFactor, newIndent);
                    commanate = true;
                }

                if (indentFactor != -1) {
                    writer.write(10);
                }

                JSONObject.indent(writer, indent);
            }

            writer.write(93);
            return writer;
        } catch (IOException var9) {
            throw new RuntimeException(var9);
        }
    }

    public String join(String separator) {
        int len = this.size();
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < len; ++i) {
            if (i > 0) {
                sb.append(separator);
            }

            sb.append(JSONObject.valueToString(this.myArrayList.get(i)));
        }

        return sb.toString();
    }
}
