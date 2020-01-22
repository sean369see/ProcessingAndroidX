package processing.data;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import processing.core.PApplet;

public class JSONObject {
    private static final int keyPoolSize = 100;
    private static HashMap<String, Object> keyPool = new HashMap(100);
    private final HashMap<String, Object> map;
    public static final Object NULL = new JSONObject.Null();

    public JSONObject() {
        this.map = new HashMap();
    }

    public JSONObject(Reader reader) {
        this(new JSONTokener(reader));
    }

    protected JSONObject(JSONTokener x) {
        this();
        if (x.nextClean() != '{') {
            throw new RuntimeException("A JSONObject text must begin with '{'");
        } else {
            while(true) {
                char c = x.nextClean();
                switch(c) {
                    case '\u0000':
                        throw new RuntimeException("A JSONObject text must end with '}'");
                    case '}':
                        return;
                    default:
                        x.back();
                        String key = x.nextValue().toString();
                        c = x.nextClean();
                        if (c == '=') {
                            if (x.next() != '>') {
                                x.back();
                            }
                        } else if (c != ':') {
                            throw new RuntimeException("Expected a ':' after a key");
                        }

                        this.putOnce(key, x.nextValue());
                        switch(x.nextClean()) {
                            case ',':
                            case ';':
                                if (x.nextClean() == '}') {
                                    return;
                                }

                                x.back();
                                break;
                            case '}':
                                return;
                            default:
                                throw new RuntimeException("Expected a ',' or '}'");
                        }
                }
            }
        }
    }

    protected JSONObject(HashMap<String, Object> map) {
        this.map = new HashMap();
        if (map != null) {
            Iterator i = map.entrySet().iterator();

            while(i.hasNext()) {
                Entry e = (Entry)i.next();
                Object value = e.getValue();
                if (value != null) {
                    map.put((String)e.getKey(), wrap(value));
                }
            }
        }

    }

    public JSONObject(IntDict dict) {
        this.map = new HashMap();

        for(int i = 0; i < dict.size(); ++i) {
            this.setInt(dict.key(i), dict.value(i));
        }

    }

    public JSONObject(FloatDict dict) {
        this.map = new HashMap();

        for(int i = 0; i < dict.size(); ++i) {
            this.setFloat(dict.key(i), dict.value(i));
        }

    }

    public JSONObject(StringDict dict) {
        this.map = new HashMap();

        for(int i = 0; i < dict.size(); ++i) {
            this.setString(dict.key(i), dict.value(i));
        }

    }

    protected JSONObject(Object bean) {
        this();
        this.populateMap(bean);
    }

    public static JSONObject parse(String source) {
        return new JSONObject(new JSONTokener(source));
    }

    protected static String doubleToString(double d) {
        if (!Double.isInfinite(d) && !Double.isNaN(d)) {
            String string = Double.toString(d);
            if (string.indexOf(46) > 0 && string.indexOf(101) < 0 && string.indexOf(69) < 0) {
                while(string.endsWith("0")) {
                    string = string.substring(0, string.length() - 1);
                }

                if (string.endsWith(".")) {
                    string = string.substring(0, string.length() - 1);
                }
            }

            return string;
        } else {
            return "null";
        }
    }

    public Object get(String key) {
        if (key == null) {
            throw new RuntimeException("JSONObject.get(null) called");
        } else {
            Object object = this.opt(key);
            if (object == null) {
                return null;
            } else if (object == null) {
                throw new RuntimeException("JSONObject[" + quote(key) + "] not found");
            } else {
                return object;
            }
        }
    }

    public String getString(String key) {
        Object object = this.get(key);
        if (object == null) {
            return null;
        } else if (object instanceof String) {
            return (String)object;
        } else {
            throw new RuntimeException("JSONObject[" + quote(key) + "] is not a string");
        }
    }

    public String getString(String key, String defaultValue) {
        Object object = this.opt(key);
        return NULL.equals(object) ? defaultValue : object.toString();
    }

    public int getInt(String key) {
        Object object = this.get(key);
        if (object == null) {
            throw new RuntimeException("JSONObject[" + quote(key) + "] not found");
        } else {
            try {
                return object instanceof Number ? ((Number)object).intValue() : Integer.parseInt((String)object);
            } catch (Exception var4) {
                throw new RuntimeException("JSONObject[" + quote(key) + "] is not an int.");
            }
        }
    }

    public int getInt(String key, int defaultValue) {
        try {
            return this.getInt(key);
        } catch (Exception var4) {
            return defaultValue;
        }
    }

    public long getLong(String key) {
        Object object = this.get(key);

        try {
            return object instanceof Number ? ((Number)object).longValue() : Long.parseLong((String)object);
        } catch (Exception var4) {
            throw new RuntimeException("JSONObject[" + quote(key) + "] is not a long.", var4);
        }
    }

    public long getLong(String key, long defaultValue) {
        try {
            return this.getLong(key);
        } catch (Exception var5) {
            return defaultValue;
        }
    }

    public float getFloat(String key) {
        return (float)this.getDouble(key);
    }

    public float getFloat(String key, float defaultValue) {
        try {
            return this.getFloat(key);
        } catch (Exception var4) {
            return defaultValue;
        }
    }

    public double getDouble(String key) {
        Object object = this.get(key);

        try {
            return object instanceof Number ? ((Number)object).doubleValue() : Double.parseDouble((String)object);
        } catch (Exception var4) {
            throw new RuntimeException("JSONObject[" + quote(key) + "] is not a number.");
        }
    }

    public double getDouble(String key, double defaultValue) {
        try {
            return this.getDouble(key);
        } catch (Exception var5) {
            return defaultValue;
        }
    }

    public boolean getBoolean(String key) {
        Object object = this.get(key);
        if (!object.equals(Boolean.FALSE) && (!(object instanceof String) || !((String)object).equalsIgnoreCase("false"))) {
            if (!object.equals(Boolean.TRUE) && (!(object instanceof String) || !((String)object).equalsIgnoreCase("true"))) {
                throw new RuntimeException("JSONObject[" + quote(key) + "] is not a Boolean.");
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        try {
            return this.getBoolean(key);
        } catch (Exception var4) {
            return defaultValue;
        }
    }

    public JSONArray getJSONArray(String key) {
        Object object = this.get(key);
        if (object == null) {
            return null;
        } else if (object instanceof JSONArray) {
            return (JSONArray)object;
        } else {
            throw new RuntimeException("JSONObject[" + quote(key) + "] is not a JSONArray.");
        }
    }

    public JSONObject getJSONObject(String key) {
        Object object = this.get(key);
        if (object == null) {
            return null;
        } else if (object instanceof JSONObject) {
            return (JSONObject)object;
        } else {
            throw new RuntimeException("JSONObject[" + quote(key) + "] is not a JSONObject.");
        }
    }

    public boolean hasKey(String key) {
        return this.map.containsKey(key);
    }

    public boolean isNull(String key) {
        return NULL.equals(this.opt(key));
    }

    public Iterator keyIterator() {
        return this.map.keySet().iterator();
    }

    public Set keys() {
        return this.map.keySet();
    }

    public int size() {
        return this.map.size();
    }

    private static String numberToString(Number number) {
        if (number == null) {
            throw new RuntimeException("Null pointer");
        } else {
            testValidity(number);
            String string = number.toString();
            if (string.indexOf(46) > 0 && string.indexOf(101) < 0 && string.indexOf(69) < 0) {
                while(string.endsWith("0")) {
                    string = string.substring(0, string.length() - 1);
                }

                if (string.endsWith(".")) {
                    string = string.substring(0, string.length() - 1);
                }
            }

            return string;
        }
    }

    private Object opt(String key) {
        return key == null ? null : this.map.get(key);
    }

    private void populateMap(Object bean) {
        Class klass = bean.getClass();
        boolean includeSuperClass = klass.getClassLoader() != null;
        Method[] methods = includeSuperClass ? klass.getMethods() : klass.getDeclaredMethods();

        for(int i = 0; i < methods.length; ++i) {
            try {
                Method method = methods[i];
                if (Modifier.isPublic(method.getModifiers())) {
                    String name = method.getName();
                    String key = "";
                    if (name.startsWith("get")) {
                        if (!"getClass".equals(name) && !"getDeclaringClass".equals(name)) {
                            key = name.substring(3);
                        } else {
                            key = "";
                        }
                    } else if (name.startsWith("is")) {
                        key = name.substring(2);
                    }

                    if (key.length() > 0 && Character.isUpperCase(key.charAt(0)) && method.getParameterTypes().length == 0) {
                        if (key.length() == 1) {
                            key = key.toLowerCase();
                        } else if (!Character.isUpperCase(key.charAt(1))) {
                            key = key.substring(0, 1).toLowerCase() + key.substring(1);
                        }

                        Object result = method.invoke(bean, (Object[])null);
                        if (result != null) {
                            this.map.put(key, wrap(result));
                        }
                    }
                }
            } catch (Exception var10) {
            }
        }

    }

    public JSONObject setString(String key, String value) {
        return this.put(key, value);
    }

    public JSONObject setInt(String key, int value) {
        this.put(key, value);
        return this;
    }

    public JSONObject setLong(String key, long value) {
        this.put(key, value);
        return this;
    }

    public JSONObject setFloat(String key, float value) {
        this.put(key, (double)value);
        return this;
    }

    public JSONObject setDouble(String key, double value) {
        this.put(key, value);
        return this;
    }

    public JSONObject setBoolean(String key, boolean value) {
        this.put(key, value ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }

    public JSONObject setJSONObject(String key, JSONObject value) {
        return this.put(key, value);
    }

    public JSONObject setJSONArray(String key, JSONArray value) {
        return this.put(key, value);
    }

    public JSONObject put(String key, Object value) {
        if (key == null) {
            throw new RuntimeException("Null key.");
        } else {
            if (value != null) {
                testValidity(value);
                String pooled = (String)keyPool.get(key);
                if (pooled == null) {
                    if (keyPool.size() >= 100) {
                        keyPool = new HashMap(100);
                    }

                    keyPool.put(key, key);
                } else {
                    key = pooled;
                }

                this.map.put(key, value);
            } else {
                this.remove(key);
            }

            return this;
        }
    }

    private JSONObject putOnce(String key, Object value) {
        if (key != null && value != null) {
            if (this.opt(key) != null) {
                throw new RuntimeException("Duplicate key \"" + key + "\"");
            }

            this.put(key, value);
        }

        return this;
    }

    public static String quote(String string) {
        StringWriter sw = new StringWriter();
        synchronized(sw.getBuffer()) {
            String var10000;
            try {
                var10000 = quote(string, sw).toString();
            } catch (IOException var5) {
                return "";
            }

            return var10000;
        }
    }

    public static Writer quote(String string, Writer w) throws IOException {
        if (string != null && string.length() != 0) {
            char c = 0;
            int len = string.length();
            w.write(34);

            for(int i = 0; i < len; ++i) {
                char b = c;
                c = string.charAt(i);
                switch(c) {
                    case '\b':
                        w.write("\\b");
                        continue;
                    case '\t':
                        w.write("\\t");
                        continue;
                    case '\n':
                        w.write("\\n");
                        continue;
                    case '\f':
                        w.write("\\f");
                        continue;
                    case '\r':
                        w.write("\\r");
                        continue;
                    case '"':
                    case '\\':
                        w.write(92);
                        w.write(c);
                        continue;
                    case '/':
                        if (b == '<') {
                            w.write(92);
                        }

                        w.write(c);
                        continue;
                }

                if (c >= ' ' && (c < 128 || c >= 160) && (c < 8192 || c >= 8448)) {
                    w.write(c);
                } else {
                    w.write("\\u");
                    String hhhh = Integer.toHexString(c);
                    w.write("0000", 0, 4 - hhhh.length());
                    w.write(hhhh);
                }
            }

            w.write(34);
            return w;
        } else {
            w.write("\"\"");
            return w;
        }
    }

    public Object remove(String key) {
        return this.map.remove(key);
    }

    protected static Object stringToValue(String string) {
        if (string.equals("")) {
            return string;
        } else if (string.equalsIgnoreCase("true")) {
            return Boolean.TRUE;
        } else if (string.equalsIgnoreCase("false")) {
            return Boolean.FALSE;
        } else if (string.equalsIgnoreCase("null")) {
            return NULL;
        } else {
            char b = string.charAt(0);
            if (b >= '0' && b <= '9' || b == '.' || b == '-' || b == '+') {
                try {
                    if (string.indexOf(46) <= -1 && string.indexOf(101) <= -1 && string.indexOf(69) <= -1) {
                        Long myLong = Long.valueOf(string);
                        if (myLong == (long)myLong.intValue()) {
                            return myLong.intValue();
                        }

                        return myLong;
                    }

                    Double d = Double.valueOf(string);
                    if (!d.isInfinite() && !d.isNaN()) {
                        return d;
                    }
                } catch (Exception var4) {
                }
            }

            return string;
        }
    }

    protected static void testValidity(Object o) {
        if (o != null) {
            if (o instanceof Double) {
                if (((Double)o).isInfinite() || ((Double)o).isNaN()) {
                    throw new RuntimeException("JSON does not allow non-finite numbers.");
                }
            } else if (o instanceof Float && (((Float)o).isInfinite() || ((Float)o).isNaN())) {
                throw new RuntimeException("JSON does not allow non-finite numbers.");
            }
        }

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
        StringWriter w = new StringWriter();
        synchronized(w.getBuffer()) {
            return this.writeInternal(w, indentFactor, 0).toString();
        }
    }

    protected static String valueToString(Object value) {
        if (value != null && !value.equals((Object)null)) {
            if (value instanceof Number) {
                return numberToString((Number)value);
            } else if (!(value instanceof Boolean) && !(value instanceof JSONObject) && !(value instanceof JSONArray)) {
                if (value instanceof Map) {
                    return (new JSONObject(value)).toString();
                } else if (value instanceof Collection) {
                    return (new JSONArray(value)).toString();
                } else {
                    return value.getClass().isArray() ? (new JSONArray(value)).toString() : quote(value.toString());
                }
            } else {
                return value.toString();
            }
        } else {
            return "null";
        }
    }

    protected static Object wrap(Object object) {
        try {
            if (object == null) {
                return NULL;
            } else if (!(object instanceof JSONObject) && !(object instanceof JSONArray) && !NULL.equals(object) && !(object instanceof Byte) && !(object instanceof Character) && !(object instanceof Short) && !(object instanceof Integer) && !(object instanceof Long) && !(object instanceof Boolean) && !(object instanceof Float) && !(object instanceof Double) && !(object instanceof String)) {
                if (object instanceof Collection) {
                    return new JSONArray(object);
                } else if (object.getClass().isArray()) {
                    return new JSONArray(object);
                } else if (object instanceof Map) {
                    return new JSONObject(object);
                } else {
                    Package objectPackage = object.getClass().getPackage();
                    String objectPackageName = objectPackage != null ? objectPackage.getName() : "";
                    return !objectPackageName.startsWith("java.") && !objectPackageName.startsWith("javax.") && object.getClass().getClassLoader() != null ? new JSONObject(object) : object.toString();
                }
            } else {
                return object;
            }
        } catch (Exception var3) {
            return null;
        }
    }

    static final Writer writeValue(Writer writer, Object value, int indentFactor, int indent) throws IOException {
        if (value != null && !value.equals((Object)null)) {
            if (value instanceof JSONObject) {
                ((JSONObject)value).writeInternal(writer, indentFactor, indent);
            } else if (value instanceof JSONArray) {
                ((JSONArray)value).writeInternal(writer, indentFactor, indent);
            } else if (value instanceof Map) {
                (new JSONObject(value)).writeInternal(writer, indentFactor, indent);
            } else if (value instanceof Collection) {
                (new JSONArray(value)).writeInternal(writer, indentFactor, indent);
            } else if (value.getClass().isArray()) {
                (new JSONArray(value)).writeInternal(writer, indentFactor, indent);
            } else if (value instanceof Number) {
                writer.write(numberToString((Number)value));
            } else if (value instanceof Boolean) {
                writer.write(value.toString());
            } else {
                quote(value.toString(), writer);
            }
        } else {
            writer.write("null");
        }

        return writer;
    }

    static final void indent(Writer writer, int indent) throws IOException {
        for(int i = 0; i < indent; ++i) {
            writer.write(32);
        }

    }

    protected Writer writeInternal(Writer writer, int indentFactor, int indent) {
        try {
            boolean commanate = false;
            int length = this.size();
            Iterator keys = this.keyIterator();
            writer.write(123);
            int actualFactor = indentFactor == -1 ? 0 : indentFactor;
            if (length == 1) {
                Object key = keys.next();
                writer.write(quote(key.toString()));
                writer.write(58);
                if (actualFactor > 0) {
                    writer.write(32);
                }

                writeValue(writer, this.map.get(key), indentFactor, indent);
            } else if (length != 0) {
                for(int newIndent = indent + actualFactor; keys.hasNext(); commanate = true) {
                    Object key = keys.next();
                    if (commanate) {
                        writer.write(44);
                    }

                    if (indentFactor != -1) {
                        writer.write(10);
                    }

                    indent(writer, newIndent);
                    writer.write(quote(key.toString()));
                    writer.write(58);
                    if (actualFactor > 0) {
                        writer.write(32);
                    }

                    writeValue(writer, this.map.get(key), indentFactor, newIndent);
                }

                if (indentFactor != -1) {
                    writer.write(10);
                }

                indent(writer, indent);
            }

            writer.write(125);
            return writer;
        } catch (IOException var10) {
            throw new RuntimeException(var10);
        }
    }

    private static final class Null {
        private Null() {
        }

        protected final Object clone() {
            return this;
        }

        public boolean equals(Object object) {
            return object == null || object == this;
        }

        public String toString() {
            return "null";
        }

        public int hashCode() {
            return super.hashCode();
        }
    }
}
