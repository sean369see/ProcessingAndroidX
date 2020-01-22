package processing.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import processing.core.PApplet;

public class LongDict {
    protected int count;
    protected String[] keys;
    protected long[] values;
    private HashMap<String, Integer> indices = new HashMap();

    public LongDict() {
        this.count = 0;
        this.keys = new String[10];
        this.values = new long[10];
    }

    public LongDict(int length) {
        this.count = 0;
        this.keys = new String[length];
        this.values = new long[length];
    }

    public LongDict(BufferedReader reader) {
        String[] lines = PApplet.loadStrings(reader);
        this.keys = new String[lines.length];
        this.values = new long[lines.length];

        for(int i = 0; i < lines.length; ++i) {
            String[] pieces = PApplet.split(lines[i], '\t');
            if (pieces.length == 2) {
                this.keys[this.count] = pieces[0];
                this.values[this.count] = (long)PApplet.parseInt(pieces[1]);
                this.indices.put(pieces[0], this.count);
                ++this.count;
            }
        }

    }

    public LongDict(String[] keys, long[] values) {
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

    public LongDict(Object[][] pairs) {
        this.count = pairs.length;
        this.keys = new String[this.count];
        this.values = new long[this.count];

        for(int i = 0; i < this.count; ++i) {
            this.keys[i] = (String)pairs[i][0];
            this.values[i] = (long)(Integer)pairs[i][1];
            this.indices.put(this.keys[i], i);
        }

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
            long[] newValues = new long[length];
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

    public Iterable<LongDict.Entry> entries() {
        return new Iterable<LongDict.Entry>() {
            public Iterator<LongDict.Entry> iterator() {
                return LongDict.this.entryIterator();
            }
        };
    }

    public Iterator<LongDict.Entry> entryIterator() {
        return new Iterator<LongDict.Entry>() {
            int index = -1;

            public void remove() {
                LongDict.this.removeIndex(this.index);
                --this.index;
            }

            public LongDict.Entry next() {
                ++this.index;
                LongDict.Entry e = LongDict.this.new Entry(LongDict.this.keys[this.index], LongDict.this.values[this.index]);
                return e;
            }

            public boolean hasNext() {
                return this.index + 1 < LongDict.this.size();
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
                return LongDict.this.keyIterator();
            }
        };
    }

    public Iterator<String> keyIterator() {
        return new Iterator<String>() {
            int index = -1;

            public void remove() {
                LongDict.this.removeIndex(this.index);
                --this.index;
            }

            public String next() {
                return LongDict.this.key(++this.index);
            }

            public boolean hasNext() {
                return this.index + 1 < LongDict.this.size();
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

    public long value(int index) {
        return this.values[index];
    }

    public Iterable<Long> values() {
        return new Iterable<Long>() {
            public Iterator<Long> iterator() {
                return LongDict.this.valueIterator();
            }
        };
    }

    public Iterator<Long> valueIterator() {
        return new Iterator<Long>() {
            int index = -1;

            public void remove() {
                LongDict.this.removeIndex(this.index);
                --this.index;
            }

            public Long next() {
                return LongDict.this.value(++this.index);
            }

            public boolean hasNext() {
                return this.index + 1 < LongDict.this.size();
            }
        };
    }

    public int[] valueArray() {
        this.crop();
        return this.valueArray((int[])null);
    }

    public int[] valueArray(int[] array) {
        if (array == null || array.length != this.size()) {
            array = new int[this.count];
        }

        System.arraycopy(this.values, 0, array, 0, this.count);
        return array;
    }

    public long get(String key) {
        int index = this.index(key);
        if (index == -1) {
            throw new IllegalArgumentException("No key named '" + key + "'");
        } else {
            return this.values[index];
        }
    }

    public long get(String key, long alternate) {
        int index = this.index(key);
        return index == -1 ? alternate : this.values[index];
    }

    public void set(String key, long amount) {
        int index = this.index(key);
        if (index == -1) {
            this.create(key, amount);
        } else {
            this.values[index] = amount;
        }

    }

    public void setIndex(int index, String key, long value) {
        if (index >= 0 && index < this.count) {
            this.keys[index] = key;
            this.values[index] = value;
        } else {
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    public boolean hasKey(String key) {
        return this.index(key) != -1;
    }

    public void increment(String key) {
        this.add(key, 1L);
    }

    public void increment(LongDict dict) {
        for(int i = 0; i < dict.count; ++i) {
            this.add(dict.key(i), dict.value(i));
        }

    }

    public void add(String key, long amount) {
        int index = this.index(key);
        if (index == -1) {
            this.create(key, amount);
        } else {
            long[] var10000 = this.values;
            var10000[index] += amount;
        }

    }

    public void sub(String key, long amount) {
        this.add(key, -amount);
    }

    public void mult(String key, long amount) {
        int index = this.index(key);
        if (index != -1) {
            long[] var10000 = this.values;
            var10000[index] *= amount;
        }

    }

    public void div(String key, long amount) {
        int index = this.index(key);
        if (index != -1) {
            long[] var10000 = this.values;
            var10000[index] /= amount;
        }

    }

    private void checkMinMax(String functionName) {
        if (this.count == 0) {
            String msg = String.format("Cannot use %s() on an empty %s.", functionName, this.getClass().getSimpleName());
            throw new RuntimeException(msg);
        }
    }

    public int minIndex() {
        if (this.count == 0) {
            return -1;
        } else {
            int index = 0;
            long value = this.values[0];

            for(int i = 1; i < this.count; ++i) {
                if (this.values[i] < value) {
                    index = i;
                    value = this.values[i];
                }
            }

            return index;
        }
    }

    public String minKey() {
        this.checkMinMax("minKey");
        int index = this.minIndex();
        return index == -1 ? null : this.keys[index];
    }

    public long minValue() {
        this.checkMinMax("minValue");
        return this.values[this.minIndex()];
    }

    public int maxIndex() {
        if (this.count == 0) {
            return -1;
        } else {
            int index = 0;
            long value = this.values[0];

            for(int i = 1; i < this.count; ++i) {
                if (this.values[i] > value) {
                    index = i;
                    value = this.values[i];
                }
            }

            return index;
        }
    }

    public String maxKey() {
        int index = this.maxIndex();
        return index == -1 ? null : this.keys[index];
    }

    public long maxValue() {
        this.checkMinMax("maxIndex");
        return this.values[this.maxIndex()];
    }

    public long sum() {
        long sum = 0L;

        for(int i = 0; i < this.count; ++i) {
            sum += this.values[i];
        }

        return sum;
    }

    public int index(String what) {
        Integer found = (Integer)this.indices.get(what);
        return found == null ? -1 : found;
    }

    protected void create(String what, long much) {
        if (this.count == this.keys.length) {
            this.keys = PApplet.expand(this.keys);
            this.values = PApplet.expand(this.values);
        }

        this.indices.put(what, this.count);
        this.keys[this.count] = what;
        this.values[this.count] = much;
        ++this.count;
    }

    public long remove(String key) {
        int index = this.index(key);
        if (index == -1) {
            throw new NoSuchElementException("'" + key + "' not found");
        } else {
            long value = this.values[index];
            this.removeIndex(index);
            return value;
        }
    }

    public long removeIndex(int index) {
        if (index >= 0 && index < this.count) {
            long value = this.values[index];
            this.indices.remove(this.keys[index]);

            for(int i = index; i < this.count - 1; ++i) {
                this.keys[i] = this.keys[i + 1];
                this.values[i] = this.values[i + 1];
                this.indices.put(this.keys[i], i);
            }

            --this.count;
            this.keys[this.count] = null;
            this.values[this.count] = 0L;
            return value;
        } else {
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    public void swap(int a, int b) {
        String tkey = this.keys[a];
        long tvalue = this.values[a];
        this.keys[a] = this.keys[b];
        this.values[a] = this.values[b];
        this.keys[b] = tkey;
        this.values[b] = tvalue;
    }

    public void sortKeys() {
        this.sortImpl(true, false, true);
    }

    public void sortKeysReverse() {
        this.sortImpl(true, true, true);
    }

    public void sortValues() {
        this.sortValues(true);
    }

    public void sortValues(boolean stable) {
        this.sortImpl(false, false, stable);
    }

    public void sortValuesReverse() {
        this.sortValuesReverse(true);
    }

    public void sortValuesReverse(boolean stable) {
        this.sortImpl(false, true, stable);
    }

    protected void sortImpl(final boolean useKeys, final boolean reverse, final boolean stable) {
        Sort s = new Sort() {
            public int size() {
                return LongDict.this.count;
            }

            public int compare(int a, int b) {
                long diff = 0L;
                if (useKeys) {
                    diff = (long)LongDict.this.keys[a].compareToIgnoreCase(LongDict.this.keys[b]);
                    if (diff == 0L) {
                        diff = LongDict.this.values[a] - LongDict.this.values[b];
                    }
                } else {
                    diff = LongDict.this.values[a] - LongDict.this.values[b];
                    if (diff == 0L && stable) {
                        diff = (long)LongDict.this.keys[a].compareToIgnoreCase(LongDict.this.keys[b]);
                    }
                }

                if (diff == 0L) {
                    return 0;
                } else if (reverse) {
                    return diff < 0L ? 1 : -1;
                } else {
                    return diff < 0L ? -1 : 1;
                }
            }

            public void swap(int a, int b) {
                LongDict.this.swap(a, b);
            }
        };
        s.run();
        this.resetIndices();
    }

    public FloatDict getPercent() {
        double sum = (double)this.sum();
        FloatDict outgoing = new FloatDict();

        for(int i = 0; i < this.size(); ++i) {
            double percent = (double)this.value(i) / sum;
            outgoing.set(this.key(i), (float)percent);
        }

        return outgoing;
    }

    public LongDict copy() {
        LongDict outgoing = new LongDict(this.count);
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
            items.append(JSONObject.quote(this.keys[i]) + ": " + this.values[i]);
        }

        return "{ " + items.join(", ") + " }";
    }

    public String toString() {
        return this.getClass().getSimpleName() + " size=" + this.size() + " " + this.toJSON();
    }

    public class Entry {
        public String key;
        public long value;

        Entry(String key, long value) {
            this.key = key;
            this.value = value;
        }
    }
}
