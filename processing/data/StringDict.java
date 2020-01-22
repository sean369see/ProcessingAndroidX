package processing.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import processing.core.PApplet;

public class StringDict {
    protected int count;
    protected String[] keys;
    protected String[] values;
    private HashMap<String, Integer> indices;

    public StringDict() {
        this.indices = new HashMap();
        this.count = 0;
        this.keys = new String[10];
        this.values = new String[10];
    }

    public StringDict(int length) {
        this.indices = new HashMap();
        this.count = 0;
        this.keys = new String[length];
        this.values = new String[length];
    }

    public StringDict(BufferedReader reader) {
        this.indices = new HashMap();
        String[] lines = PApplet.loadStrings(reader);
        this.keys = new String[lines.length];
        this.values = new String[lines.length];

        for(int i = 0; i < lines.length; ++i) {
            String[] pieces = PApplet.split(lines[i], '\t');
            if (pieces.length == 2) {
                this.keys[this.count] = pieces[0];
                this.values[this.count] = pieces[1];
                this.indices.put(this.keys[this.count], this.count);
                ++this.count;
            }
        }

    }

    public StringDict(String[] keys, String[] values) {
        this.indices = new HashMap();
        if (keys.length != values.length) {
            throw new IllegalArgumentException("key and value arrays must be the same length");
        } else {
            this.keys = keys;
            this.values = values;
            this.count = keys.length;

            for(int i = 0; i < this.count; ++i) {
                this.indices.put(keys[i], i);
            }

        }
    }

    public StringDict(String[][] pairs) {
        this.indices = new HashMap();
        this.count = pairs.length;
        this.keys = new String[this.count];
        this.values = new String[this.count];

        for(int i = 0; i < this.count; ++i) {
            this.keys[i] = pairs[i][0];
            this.values[i] = pairs[i][1];
            this.indices.put(this.keys[i], i);
        }

    }

    public StringDict(TableRow row) {
        this(row.getColumnCount());
        String[] titles = row.getColumnTitles();
        if (titles == null) {
            titles = (new StringList(new Object[]{IntList.fromRange(row.getColumnCount())})).array();
        }

        for(int col = 0; col < row.getColumnCount(); ++col) {
            this.set(titles[col], row.getString(col));
        }

        this.crop();
    }

    public int size() {
        return this.count;
    }

    public void resize(int length) {
        if (length > this.count) {
            throw new IllegalArgumentException("resize() can only be used to shrink the dictionary");
        } else if (length < 1) {
            throw new IllegalArgumentException("resize(" + length + ") is too small, use 1 or higher");
        } else {
            String[] newKeys = new String[length];
            String[] newValues = new String[length];
            PApplet.arrayCopy(this.keys, newKeys, length);
            PApplet.arrayCopy(this.values, newValues, length);
            this.keys = newKeys;
            this.values = newValues;
            this.count = length;
            this.resetIndices();
        }
    }

    public void clear() {
        this.count = 0;
        this.indices = new HashMap();
    }

    private void resetIndices() {
        this.indices = new HashMap(this.count);

        for(int i = 0; i < this.count; ++i) {
            this.indices.put(this.keys[i], i);
        }

    }

    public Iterable<StringDict.Entry> entries() {
        return new Iterable<StringDict.Entry>() {
            public Iterator<StringDict.Entry> iterator() {
                return StringDict.this.entryIterator();
            }
        };
    }

    public Iterator<StringDict.Entry> entryIterator() {
        return new Iterator<StringDict.Entry>() {
            int index = -1;

            public void remove() {
                StringDict.this.removeIndex(this.index);
                --this.index;
            }

            public StringDict.Entry next() {
                ++this.index;
                StringDict.Entry e = StringDict.this.new Entry(StringDict.this.keys[this.index], StringDict.this.values[this.index]);
                return e;
            }

            public boolean hasNext() {
                return this.index + 1 < StringDict.this.size();
            }
        };
    }

    public String key(int index) {
        return this.keys[index];
    }

    protected void crop() {
        if (this.count != this.keys.length) {
            this.keys = PApplet.subset(this.keys, 0, this.count);
            this.values = PApplet.subset(this.values, 0, this.count);
        }

    }

    public Iterable<String> keys() {
        return new Iterable<String>() {
            public Iterator<String> iterator() {
                return StringDict.this.keyIterator();
            }
        };
    }

    public Iterator<String> keyIterator() {
        return new Iterator<String>() {
            int index = -1;

            public void remove() {
                StringDict.this.removeIndex(this.index);
                --this.index;
            }

            public String next() {
                return StringDict.this.key(++this.index);
            }

            public boolean hasNext() {
                return this.index + 1 < StringDict.this.size();
            }
        };
    }

    public String[] keyArray() {
        this.crop();
        return this.keyArray((String[])null);
    }

    public String[] keyArray(String[] outgoing) {
        if (outgoing == null || outgoing.length != this.count) {
            outgoing = new String[this.count];
        }

        System.arraycopy(this.keys, 0, outgoing, 0, this.count);
        return outgoing;
    }

    public String value(int index) {
        return this.values[index];
    }

    public Iterable<String> values() {
        return new Iterable<String>() {
            public Iterator<String> iterator() {
                return StringDict.this.valueIterator();
            }
        };
    }

    public Iterator<String> valueIterator() {
        return new Iterator<String>() {
            int index = -1;

            public void remove() {
                StringDict.this.removeIndex(this.index);
                --this.index;
            }

            public String next() {
                return StringDict.this.value(++this.index);
            }

            public boolean hasNext() {
                return this.index + 1 < StringDict.this.size();
            }
        };
    }

    public String[] valueArray() {
        this.crop();
        return this.valueArray((String[])null);
    }

    public String[] valueArray(String[] array) {
        if (array == null || array.length != this.size()) {
            array = new String[this.count];
        }

        System.arraycopy(this.values, 0, array, 0, this.count);
        return array;
    }

    public String get(String key) {
        int index = this.index(key);
        return index == -1 ? null : this.values[index];
    }

    public String get(String key, String alternate) {
        int index = this.index(key);
        return index == -1 ? alternate : this.values[index];
    }

    public void set(String key, String value) {
        int index = this.index(key);
        if (index == -1) {
            this.create(key, value);
        } else {
            this.values[index] = value;
        }

    }

    public void setIndex(int index, String key, String value) {
        if (index >= 0 && index < this.count) {
            this.keys[index] = key;
            this.values[index] = value;
        } else {
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    public int index(String what) {
        Integer found = (Integer)this.indices.get(what);
        return found == null ? -1 : found;
    }

    public boolean hasKey(String key) {
        return this.index(key) != -1;
    }

    protected void create(String key, String value) {
        if (this.count == this.keys.length) {
            this.keys = PApplet.expand(this.keys);
            this.values = PApplet.expand(this.values);
        }

        this.indices.put(key, this.count);
        this.keys[this.count] = key;
        this.values[this.count] = value;
        ++this.count;
    }

    public String remove(String key) {
        int index = this.index(key);
        if (index == -1) {
            throw new NoSuchElementException("'" + key + "' not found");
        } else {
            String value = this.values[index];
            this.removeIndex(index);
            return value;
        }
    }

    public String removeIndex(int index) {
        if (index >= 0 && index < this.count) {
            String value = this.values[index];
            this.indices.remove(this.keys[index]);

            for(int i = index; i < this.count - 1; ++i) {
                this.keys[i] = this.keys[i + 1];
                this.values[i] = this.values[i + 1];
                this.indices.put(this.keys[i], i);
            }

            --this.count;
            this.keys[this.count] = null;
            this.values[this.count] = null;
            return value;
        } else {
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    public void swap(int a, int b) {
        String tkey = this.keys[a];
        String tvalue = this.values[a];
        this.keys[a] = this.keys[b];
        this.values[a] = this.values[b];
        this.keys[b] = tkey;
        this.values[b] = tvalue;
    }

    public void sortKeys() {
        this.sortImpl(true, false);
    }

    public void sortKeysReverse() {
        this.sortImpl(true, true);
    }

    public void sortValues() {
        this.sortImpl(false, false);
    }

    public void sortValuesReverse() {
        this.sortImpl(false, true);
    }

    protected void sortImpl(final boolean useKeys, final boolean reverse) {
        Sort s = new Sort() {
            public int size() {
                return StringDict.this.count;
            }

            public int compare(int a, int b) {
                int diff = false;
                int diffx;
                if (useKeys) {
                    diffx = StringDict.this.keys[a].compareToIgnoreCase(StringDict.this.keys[b]);
                    if (diffx == 0) {
                        diffx = StringDict.this.values[a].compareToIgnoreCase(StringDict.this.values[b]);
                    }
                } else {
                    diffx = StringDict.this.values[a].compareToIgnoreCase(StringDict.this.values[b]);
                    if (diffx == 0) {
                        diffx = StringDict.this.keys[a].compareToIgnoreCase(StringDict.this.keys[b]);
                    }
                }

                return reverse ? -diffx : diffx;
            }

            public void swap(int a, int b) {
                StringDict.this.swap(a, b);
            }
        };
        s.run();
        this.resetIndices();
    }

    public StringDict copy() {
        StringDict outgoing = new StringDict(this.count);
        System.arraycopy(this.keys, 0, outgoing.keys, 0, this.count);
        System.arraycopy(this.values, 0, outgoing.values, 0, this.count);

        for(int i = 0; i < this.count; ++i) {
            outgoing.indices.put(this.keys[i], i);
        }

        outgoing.count = this.count;
        return outgoing;
    }

    public void print() {
        for(int i = 0; i < this.size(); ++i) {
            System.out.println(this.keys[i] + " = " + this.values[i]);
        }

    }

    public void save(File file) {
        PrintWriter writer = PApplet.createWriter(file);
        this.write(writer);
        writer.close();
    }

    public void write(PrintWriter writer) {
        for(int i = 0; i < this.count; ++i) {
            writer.println(this.keys[i] + "\t" + this.values[i]);
        }

        writer.flush();
    }

    public String toJSON() {
        StringList items = new StringList();

        for(int i = 0; i < this.count; ++i) {
            items.append(JSONObject.quote(this.keys[i]) + ": " + JSONObject.quote(this.values[i]));
        }

        return "{ " + items.join(", ") + " }";
    }

    public String toString() {
        return this.getClass().getSimpleName() + " size=" + this.size() + " " + this.toJSON();
    }

    public class Entry {
        public String key;
        public String value;

        Entry(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
