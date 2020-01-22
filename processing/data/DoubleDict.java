package processing.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import processing.core.PApplet;

public class DoubleDict {
    protected int count;
    protected String[] keys;
    protected double[] values;
    private HashMap<String, Integer> indices = new HashMap();

    public DoubleDict() {
        this.count = 0;
        this.keys = new String[10];
        this.values = new double[10];
    }

    public DoubleDict(int length) {
        this.count = 0;
        this.keys = new String[length];
        this.values = new double[length];
    }

    public DoubleDict(BufferedReader reader) {
        String[] lines = PApplet.loadStrings(reader);
        this.keys = new String[lines.length];
        this.values = new double[lines.length];

        for(int i = 0; i < lines.length; ++i) {
            String[] pieces = PApplet.split(lines[i], '\t');
            if (pieces.length == 2) {
                this.keys[this.count] = pieces[0];
                this.values[this.count] = (double)PApplet.parseFloat(pieces[1]);
                this.indices.put(pieces[0], this.count);
                ++this.count;
            }
        }

    }

    public DoubleDict(String[] keys, double[] values) {
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

    public DoubleDict(Object[][] pairs) {
        this.count = pairs.length;
        this.keys = new String[this.count];
        this.values = new double[this.count];

        for(int i = 0; i < this.count; ++i) {
            this.keys[i] = (String)pairs[i][0];
            this.values[i] = (double)(Float)pairs[i][1];
            this.indices.put(this.keys[i], i);
        }

    }

    public DoubleDict(Map<String, Double> incoming) {
        this.count = incoming.size();
        this.keys = new String[this.count];
        this.values = new double[this.count];
        int index = 0;

        for(Iterator var3 = incoming.entrySet().iterator(); var3.hasNext(); ++index) {
            java.util.Map.Entry<String, Double> e = (java.util.Map.Entry)var3.next();
            this.keys[index] = (String)e.getKey();
            this.values[index] = (Double)e.getValue();
            this.indices.put(this.keys[index], index);
        }

    }

    public int size() {
        return this.count;
    }

    public void resize(int length) {
        if (length != this.count) {
            if (length > this.count) {
                throw new IllegalArgumentException("resize() can only be used to shrink the dictionary");
            } else if (length < 1) {
                throw new IllegalArgumentException("resize(" + length + ") is too small, use 1 or higher");
            } else {
                String[] newKeys = new String[length];
                double[] newValues = new double[length];
                PApplet.arrayCopy(this.keys, newKeys, length);
                PApplet.arrayCopy(this.values, newValues, length);
                this.keys = newKeys;
                this.values = newValues;
                this.count = length;
                this.resetIndices();
            }
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

    public Iterable<DoubleDict.Entry> entries() {
        return new Iterable<DoubleDict.Entry>() {
            public Iterator<DoubleDict.Entry> iterator() {
                return DoubleDict.this.entryIterator();
            }
        };
    }

    public Iterator<DoubleDict.Entry> entryIterator() {
        return new Iterator<DoubleDict.Entry>() {
            int index = -1;

            public void remove() {
                DoubleDict.this.removeIndex(this.index);
                --this.index;
            }

            public DoubleDict.Entry next() {
                ++this.index;
                DoubleDict.Entry e = DoubleDict.this.new Entry(DoubleDict.this.keys[this.index], DoubleDict.this.values[this.index]);
                return e;
            }

            public boolean hasNext() {
                return this.index + 1 < DoubleDict.this.size();
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
                return DoubleDict.this.keyIterator();
            }
        };
    }

    public Iterator<String> keyIterator() {
        return new Iterator<String>() {
            int index = -1;

            public void remove() {
                DoubleDict.this.removeIndex(this.index);
                --this.index;
            }

            public String next() {
                return DoubleDict.this.key(++this.index);
            }

            public boolean hasNext() {
                return this.index + 1 < DoubleDict.this.size();
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

    public double value(int index) {
        return this.values[index];
    }

    public Iterable<Double> values() {
        return new Iterable<Double>() {
            public Iterator<Double> iterator() {
                return DoubleDict.this.valueIterator();
            }
        };
    }

    public Iterator<Double> valueIterator() {
        return new Iterator<Double>() {
            int index = -1;

            public void remove() {
                DoubleDict.this.removeIndex(this.index);
                --this.index;
            }

            public Double next() {
                return DoubleDict.this.value(++this.index);
            }

            public boolean hasNext() {
                return this.index + 1 < DoubleDict.this.size();
            }
        };
    }

    public double[] valueArray() {
        this.crop();
        return this.valueArray((double[])null);
    }

    public double[] valueArray(double[] array) {
        if (array == null || array.length != this.size()) {
            array = new double[this.count];
        }

        System.arraycopy(this.values, 0, array, 0, this.count);
        return array;
    }

    public double get(String key) {
        int index = this.index(key);
        if (index == -1) {
            throw new IllegalArgumentException("No key named '" + key + "'");
        } else {
            return this.values[index];
        }
    }

    public double get(String key, double alternate) {
        int index = this.index(key);
        return index == -1 ? alternate : this.values[index];
    }

    public void set(String key, double amount) {
        int index = this.index(key);
        if (index == -1) {
            this.create(key, amount);
        } else {
            this.values[index] = amount;
        }

    }

    public void setIndex(int index, String key, double value) {
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

    public void add(String key, double amount) {
        int index = this.index(key);
        if (index == -1) {
            this.create(key, amount);
        } else {
            double[] var10000 = this.values;
            var10000[index] += amount;
        }

    }

    public void sub(String key, double amount) {
        this.add(key, -amount);
    }

    public void mult(String key, double amount) {
        int index = this.index(key);
        if (index != -1) {
            double[] var10000 = this.values;
            var10000[index] *= amount;
        }

    }

    public void div(String key, double amount) {
        int index = this.index(key);
        if (index != -1) {
            double[] var10000 = this.values;
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
            double m = 0.0D / 0.0;
            int mi = -1;

            for(int i = 0; i < this.count; ++i) {
                if (this.values[i] == this.values[i]) {
                    m = this.values[i];
                    mi = i;

                    for(int j = i + 1; j < this.count; ++j) {
                        double d = this.values[j];
                        if (d == d && d < m) {
                            m = this.values[j];
                            mi = j;
                        }
                    }

                    return mi;
                }
            }

            return mi;
        }
    }

    public String minKey() {
        this.checkMinMax("minKey");
        int index = this.minIndex();
        return index == -1 ? null : this.keys[index];
    }

    public double minValue() {
        this.checkMinMax("minValue");
        int index = this.minIndex();
        return index == -1 ? 0.0D / 0.0 : this.values[index];
    }

    public int maxIndex() {
        if (this.count == 0) {
            return -1;
        } else {
            double m = 0.0D / 0.0;
            int mi = -1;

            for(int i = 0; i < this.count; ++i) {
                if (this.values[i] == this.values[i]) {
                    m = this.values[i];
                    mi = i;

                    for(int j = i + 1; j < this.count; ++j) {
                        double d = this.values[j];
                        if (!Double.isNaN(d) && d > m) {
                            m = this.values[j];
                            mi = j;
                        }
                    }

                    return mi;
                }
            }

            return mi;
        }
    }

    public String maxKey() {
        int index = this.maxIndex();
        return index == -1 ? null : this.keys[index];
    }

    public double maxValue() {
        int index = this.maxIndex();
        return index == -1 ? 0.0D / 0.0 : this.values[index];
    }

    public double sum() {
        double sum = 0.0D;

        for(int i = 0; i < this.count; ++i) {
            sum += this.values[i];
        }

        return sum;
    }

    public int index(String what) {
        Integer found = (Integer)this.indices.get(what);
        return found == null ? -1 : found;
    }

    protected void create(String what, double much) {
        if (this.count == this.keys.length) {
            this.keys = PApplet.expand(this.keys);
            this.values = PApplet.expand(this.values);
        }

        this.indices.put(what, this.count);
        this.keys[this.count] = what;
        this.values[this.count] = much;
        ++this.count;
    }

    public double remove(String key) {
        int index = this.index(key);
        if (index == -1) {
            throw new NoSuchElementException("'" + key + "' not found");
        } else {
            double value = this.values[index];
            this.removeIndex(index);
            return value;
        }
    }

    public double removeIndex(int index) {
        if (index >= 0 && index < this.count) {
            double value = this.values[index];
            this.indices.remove(this.keys[index]);

            for(int i = index; i < this.count - 1; ++i) {
                this.keys[i] = this.keys[i + 1];
                this.values[i] = this.values[i + 1];
                this.indices.put(this.keys[i], i);
            }

            --this.count;
            this.keys[this.count] = null;
            this.values[this.count] = 0.0D;
            return value;
        } else {
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    public void swap(int a, int b) {
        String tkey = this.keys[a];
        double tvalue = this.values[a];
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
                if (useKeys) {
                    return DoubleDict.this.count;
                } else if (DoubleDict.this.count == 0) {
                    return 0;
                } else {
                    int right = DoubleDict.this.count - 1;

                    do {
                        if (DoubleDict.this.values[right] == DoubleDict.this.values[right]) {
                            for(int i = right; i >= 0; --i) {
                                if (Double.isNaN(DoubleDict.this.values[i])) {
                                    this.swap(i, right);
                                    --right;
                                }
                            }

                            return right + 1;
                        }

                        --right;
                    } while(right != -1);

                    return 0;
                }
            }

            public int compare(int a, int b) {
                double diff = 0.0D;
                if (useKeys) {
                    diff = (double)DoubleDict.this.keys[a].compareToIgnoreCase(DoubleDict.this.keys[b]);
                    if (diff == 0.0D) {
                        diff = DoubleDict.this.values[a] - DoubleDict.this.values[b];
                    }
                } else {
                    diff = DoubleDict.this.values[a] - DoubleDict.this.values[b];
                    if (diff == 0.0D && stable) {
                        diff = (double)DoubleDict.this.keys[a].compareToIgnoreCase(DoubleDict.this.keys[b]);
                    }
                }

                if (diff == 0.0D) {
                    return 0;
                } else if (reverse) {
                    return diff < 0.0D ? 1 : -1;
                } else {
                    return diff < 0.0D ? -1 : 1;
                }
            }

            public void swap(int a, int b) {
                DoubleDict.this.swap(a, b);
            }
        };
        s.run();
        this.resetIndices();
    }

    public DoubleDict getPercent() {
        double sum = this.sum();
        DoubleDict outgoing = new DoubleDict();

        for(int i = 0; i < this.size(); ++i) {
            double percent = this.value(i) / sum;
            outgoing.set(this.key(i), percent);
        }

        return outgoing;
    }

    public DoubleDict copy() {
        DoubleDict outgoing = new DoubleDict(this.count);
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
        public double value;

        Entry(String key, double value) {
            this.key = key;
            this.value = value;
        }
    }
}
