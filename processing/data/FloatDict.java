package processing.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import processing.core.PApplet;

public class FloatDict {
    protected int count;
    protected String[] keys;
    protected float[] values;
    private HashMap<String, Integer> indices = new HashMap();

    public FloatDict() {
        this.count = 0;
        this.keys = new String[10];
        this.values = new float[10];
    }

    public FloatDict(int length) {
        this.count = 0;
        this.keys = new String[length];
        this.values = new float[length];
    }

    public FloatDict(BufferedReader reader) {
        String[] lines = PApplet.loadStrings(reader);
        this.keys = new String[lines.length];
        this.values = new float[lines.length];

        for(int i = 0; i < lines.length; ++i) {
            String[] pieces = PApplet.split(lines[i], '\t');
            if (pieces.length == 2) {
                this.keys[this.count] = pieces[0];
                this.values[this.count] = PApplet.parseFloat(pieces[1]);
                this.indices.put(pieces[0], this.count);
                ++this.count;
            }
        }

    }

    public FloatDict(String[] keys, float[] values) {
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

    public FloatDict(Object[][] pairs) {
        this.count = pairs.length;
        this.keys = new String[this.count];
        this.values = new float[this.count];

        for(int i = 0; i < this.count; ++i) {
            this.keys[i] = (String)pairs[i][0];
            this.values[i] = (Float)pairs[i][1];
            this.indices.put(this.keys[i], i);
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
                float[] newValues = new float[length];
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

    public Iterable<FloatDict.Entry> entries() {
        return new Iterable<FloatDict.Entry>() {
            public Iterator<FloatDict.Entry> iterator() {
                return FloatDict.this.entryIterator();
            }
        };
    }

    public Iterator<FloatDict.Entry> entryIterator() {
        return new Iterator<FloatDict.Entry>() {
            int index = -1;

            public void remove() {
                FloatDict.this.removeIndex(this.index);
                --this.index;
            }

            public FloatDict.Entry next() {
                ++this.index;
                FloatDict.Entry e = FloatDict.this.new Entry(FloatDict.this.keys[this.index], FloatDict.this.values[this.index]);
                return e;
            }

            public boolean hasNext() {
                return this.index + 1 < FloatDict.this.size();
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
                return FloatDict.this.keyIterator();
            }
        };
    }

    public Iterator<String> keyIterator() {
        return new Iterator<String>() {
            int index = -1;

            public void remove() {
                FloatDict.this.removeIndex(this.index);
                --this.index;
            }

            public String next() {
                return FloatDict.this.key(++this.index);
            }

            public boolean hasNext() {
                return this.index + 1 < FloatDict.this.size();
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

    public float value(int index) {
        return this.values[index];
    }

    public Iterable<Float> values() {
        return new Iterable<Float>() {
            public Iterator<Float> iterator() {
                return FloatDict.this.valueIterator();
            }
        };
    }

    public Iterator<Float> valueIterator() {
        return new Iterator<Float>() {
            int index = -1;

            public void remove() {
                FloatDict.this.removeIndex(this.index);
                --this.index;
            }

            public Float next() {
                return FloatDict.this.value(++this.index);
            }

            public boolean hasNext() {
                return this.index + 1 < FloatDict.this.size();
            }
        };
    }

    public float[] valueArray() {
        this.crop();
        return this.valueArray((float[])null);
    }

    public float[] valueArray(float[] array) {
        if (array == null || array.length != this.size()) {
            array = new float[this.count];
        }

        System.arraycopy(this.values, 0, array, 0, this.count);
        return array;
    }

    public float get(String key) {
        int index = this.index(key);
        if (index == -1) {
            throw new IllegalArgumentException("No key named '" + key + "'");
        } else {
            return this.values[index];
        }
    }

    public float get(String key, float alternate) {
        int index = this.index(key);
        return index == -1 ? alternate : this.values[index];
    }

    public void set(String key, float amount) {
        int index = this.index(key);
        if (index == -1) {
            this.create(key, amount);
        } else {
            this.values[index] = amount;
        }

    }

    public void setIndex(int index, String key, float value) {
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

    public void add(String key, float amount) {
        int index = this.index(key);
        if (index == -1) {
            this.create(key, amount);
        } else {
            float[] var10000 = this.values;
            var10000[index] += amount;
        }

    }

    public void sub(String key, float amount) {
        this.add(key, -amount);
    }

    public void mult(String key, float amount) {
        int index = this.index(key);
        if (index != -1) {
            float[] var10000 = this.values;
            var10000[index] *= amount;
        }

    }

    public void div(String key, float amount) {
        int index = this.index(key);
        if (index != -1) {
            float[] var10000 = this.values;
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
            float m = 0.0F / 0.0;
            int mi = -1;

            for(int i = 0; i < this.count; ++i) {
                if (this.values[i] == this.values[i]) {
                    m = this.values[i];
                    mi = i;

                    for(int j = i + 1; j < this.count; ++j) {
                        float d = this.values[j];
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

    public float minValue() {
        this.checkMinMax("minValue");
        int index = this.minIndex();
        return index == -1 ? 0.0F / 0.0 : this.values[index];
    }

    public int maxIndex() {
        if (this.count == 0) {
            return -1;
        } else {
            float m = 0.0F / 0.0;
            int mi = -1;

            for(int i = 0; i < this.count; ++i) {
                if (this.values[i] == this.values[i]) {
                    m = this.values[i];
                    mi = i;

                    for(int j = i + 1; j < this.count; ++j) {
                        float d = this.values[j];
                        if (!Float.isNaN(d) && d > m) {
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

    public float maxValue() {
        int index = this.maxIndex();
        return index == -1 ? 0.0F / 0.0 : this.values[index];
    }

    public float sum() {
        double amount = this.sumDouble();
        if (amount > 3.4028234663852886E38D) {
            throw new RuntimeException("sum() exceeds 3.4028235E38, use sumDouble()");
        } else if (amount < -3.4028234663852886E38D) {
            throw new RuntimeException("sum() lower than -3.4028235E38, use sumDouble()");
        } else {
            return (float)amount;
        }
    }

    public double sumDouble() {
        double sum = 0.0D;

        for(int i = 0; i < this.count; ++i) {
            sum += (double)this.values[i];
        }

        return sum;
    }

    public int index(String what) {
        Integer found = (Integer)this.indices.get(what);
        return found == null ? -1 : found;
    }

    protected void create(String what, float much) {
        if (this.count == this.keys.length) {
            this.keys = PApplet.expand(this.keys);
            this.values = PApplet.expand(this.values);
        }

        this.indices.put(what, this.count);
        this.keys[this.count] = what;
        this.values[this.count] = much;
        ++this.count;
    }

    public float remove(String key) {
        int index = this.index(key);
        if (index == -1) {
            throw new NoSuchElementException("'" + key + "' not found");
        } else {
            float value = this.values[index];
            this.removeIndex(index);
            return value;
        }
    }

    public float removeIndex(int index) {
        if (index >= 0 && index < this.count) {
            float value = this.values[index];
            this.indices.remove(this.keys[index]);

            for(int i = index; i < this.count - 1; ++i) {
                this.keys[i] = this.keys[i + 1];
                this.values[i] = this.values[i + 1];
                this.indices.put(this.keys[i], i);
            }

            --this.count;
            this.keys[this.count] = null;
            this.values[this.count] = 0.0F;
            return value;
        } else {
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    public void swap(int a, int b) {
        String tkey = this.keys[a];
        float tvalue = this.values[a];
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
                    return FloatDict.this.count;
                } else if (FloatDict.this.count == 0) {
                    return 0;
                } else {
                    int right = FloatDict.this.count - 1;

                    do {
                        if (FloatDict.this.values[right] == FloatDict.this.values[right]) {
                            for(int i = right; i >= 0; --i) {
                                if (Float.isNaN(FloatDict.this.values[i])) {
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
                float diff = 0.0F;
                if (useKeys) {
                    diff = (float)FloatDict.this.keys[a].compareToIgnoreCase(FloatDict.this.keys[b]);
                    if (diff == 0.0F) {
                        diff = FloatDict.this.values[a] - FloatDict.this.values[b];
                    }
                } else {
                    diff = FloatDict.this.values[a] - FloatDict.this.values[b];
                    if (diff == 0.0F && stable) {
                        diff = (float)FloatDict.this.keys[a].compareToIgnoreCase(FloatDict.this.keys[b]);
                    }
                }

                if (diff == 0.0F) {
                    return 0;
                } else if (reverse) {
                    return diff < 0.0F ? 1 : -1;
                } else {
                    return diff < 0.0F ? -1 : 1;
                }
            }

            public void swap(int a, int b) {
                FloatDict.this.swap(a, b);
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

    public FloatDict copy() {
        FloatDict outgoing = new FloatDict(this.count);
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
        public float value;

        Entry(String key, float value) {
            this.key = key;
            this.value = value;
        }
    }
}
