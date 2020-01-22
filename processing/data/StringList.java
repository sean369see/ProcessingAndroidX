package processing.data;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import processing.core.PApplet;

public class StringList implements Iterable<String> {
    int count;
    String[] data;

    public StringList() {
        this(10);
    }

    public StringList(int length) {
        this.data = new String[length];
    }

    public StringList(String[] list) {
        this.count = list.length;
        this.data = new String[this.count];
        System.arraycopy(list, 0, this.data, 0, this.count);
    }

    public StringList(Object... items) {
        this.count = items.length;
        this.data = new String[this.count];
        int index = 0;
        Object[] var3 = items;
        int var4 = items.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Object o = var3[var5];
            if (o != null) {
                this.data[index] = o.toString();
            }

            ++index;
        }

    }

    public StringList(Iterable<String> iter) {
        this(10);
        Iterator var2 = iter.iterator();

        while(var2.hasNext()) {
            String s = (String)var2.next();
            this.append(s);
        }

    }

    private void crop() {
        if (this.count != this.data.length) {
            this.data = PApplet.subset(this.data, 0, this.count);
        }

    }

    public int size() {
        return this.count;
    }

    public void resize(int length) {
        if (length > this.data.length) {
            String[] temp = new String[length];
            System.arraycopy(this.data, 0, temp, 0, this.count);
            this.data = temp;
        } else if (length > this.count) {
            Arrays.fill(this.data, this.count, length, 0);
        }

        this.count = length;
    }

    public void clear() {
        this.count = 0;
    }

    public String get(int index) {
        if (index >= this.count) {
            throw new ArrayIndexOutOfBoundsException(index);
        } else {
            return this.data[index];
        }
    }

    public void set(int index, String what) {
        if (index >= this.count) {
            this.data = PApplet.expand(this.data, index + 1);

            for(int i = this.count; i < index; ++i) {
                this.data[i] = null;
            }

            this.count = index + 1;
        }

        this.data[index] = what;
    }

    public void push(String value) {
        this.append(value);
    }

    public String pop() {
        if (this.count == 0) {
            throw new RuntimeException("Can't call pop() on an empty list");
        } else {
            String value = this.get(this.count - 1);
            this.data[--this.count] = null;
            return value;
        }
    }

    public String remove(int index) {
        if (index >= 0 && index < this.count) {
            String entry = this.data[index];

            for(int i = index; i < this.count - 1; ++i) {
                this.data[i] = this.data[i + 1];
            }

            --this.count;
            return entry;
        } else {
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    public int removeValue(String value) {
        int i;
        if (value == null) {
            for(i = 0; i < this.count; ++i) {
                if (this.data[i] == null) {
                    this.remove(i);
                    return i;
                }
            }
        } else {
            i = this.index(value);
            if (i != -1) {
                this.remove(i);
                return i;
            }
        }

        return -1;
    }

    public int removeValues(String value) {
        int ii = 0;
        int i;
        if (value == null) {
            for(i = 0; i < this.count; ++i) {
                if (this.data[i] != null) {
                    this.data[ii++] = this.data[i];
                }
            }
        } else {
            for(i = 0; i < this.count; ++i) {
                if (!value.equals(this.data[i])) {
                    this.data[ii++] = this.data[i];
                }
            }
        }

        i = this.count - ii;
        this.count = ii;
        return i;
    }

    public int replaceValue(String value, String newValue) {
        int i;
        if (value == null) {
            for(i = 0; i < this.count; ++i) {
                if (this.data[i] == null) {
                    this.data[i] = newValue;
                    return i;
                }
            }
        } else {
            for(i = 0; i < this.count; ++i) {
                if (value.equals(this.data[i])) {
                    this.data[i] = newValue;
                    return i;
                }
            }
        }

        return -1;
    }

    public int replaceValues(String value, String newValue) {
        int changed = 0;
        int i;
        if (value == null) {
            for(i = 0; i < this.count; ++i) {
                if (this.data[i] == null) {
                    this.data[i] = newValue;
                    ++changed;
                }
            }
        } else {
            for(i = 0; i < this.count; ++i) {
                if (value.equals(this.data[i])) {
                    this.data[i] = newValue;
                    ++changed;
                }
            }
        }

        return changed;
    }

    public void append(String value) {
        if (this.count == this.data.length) {
            this.data = PApplet.expand(this.data);
        }

        this.data[this.count++] = value;
    }

    public void append(String[] values) {
        String[] var2 = values;
        int var3 = values.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String v = var2[var4];
            this.append(v);
        }

    }

    public void append(StringList list) {
        String[] var2 = list.values();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String v = var2[var4];
            this.append(v);
        }

    }

    public void appendUnique(String value) {
        if (!this.hasValue(value)) {
            this.append(value);
        }

    }

    public void insert(int index, String value) {
        this.insert(index, new String[]{value});
    }

    public void insert(int index, String[] values) {
        if (index < 0) {
            throw new IllegalArgumentException("insert() index cannot be negative: it was " + index);
        } else if (index >= this.data.length) {
            throw new IllegalArgumentException("insert() index " + index + " is past the end of this list");
        } else {
            String[] temp = new String[this.count + values.length];
            System.arraycopy(this.data, 0, temp, 0, Math.min(this.count, index));
            System.arraycopy(values, 0, temp, index, values.length);
            System.arraycopy(this.data, index, temp, index + values.length, this.count - index);
            this.count += values.length;
            this.data = temp;
        }
    }

    public void insert(int index, StringList list) {
        this.insert(index, list.values());
    }

    public int index(String what) {
        int i;
        if (what == null) {
            for(i = 0; i < this.count; ++i) {
                if (this.data[i] == null) {
                    return i;
                }
            }
        } else {
            for(i = 0; i < this.count; ++i) {
                if (what.equals(this.data[i])) {
                    return i;
                }
            }
        }

        return -1;
    }

    public boolean hasValue(String value) {
        int i;
        if (value == null) {
            for(i = 0; i < this.count; ++i) {
                if (this.data[i] == null) {
                    return true;
                }
            }
        } else {
            for(i = 0; i < this.count; ++i) {
                if (value.equals(this.data[i])) {
                    return true;
                }
            }
        }

        return false;
    }

    public void sort() {
        this.sortImpl(false);
    }

    public void sortReverse() {
        this.sortImpl(true);
    }

    private void sortImpl(final boolean reverse) {
        (new Sort() {
            public int size() {
                return StringList.this.count;
            }

            public int compare(int a, int b) {
                int diff = StringList.this.data[a].compareToIgnoreCase(StringList.this.data[b]);
                return reverse ? -diff : diff;
            }

            public void swap(int a, int b) {
                String temp = StringList.this.data[a];
                StringList.this.data[a] = StringList.this.data[b];
                StringList.this.data[b] = temp;
            }
        }).run();
    }

    public void reverse() {
        int ii = this.count - 1;

        for(int i = 0; i < this.count / 2; ++i) {
            String t = this.data[i];
            this.data[i] = this.data[ii];
            this.data[ii] = t;
            --ii;
        }

    }

    public void shuffle() {
        Random r = new Random();

        int value;
        String temp;
        for(int num = this.count; num > 1; this.data[value] = temp) {
            value = r.nextInt(num);
            --num;
            temp = this.data[num];
            this.data[num] = this.data[value];
        }

    }

    public void shuffle(PApplet sketch) {
        int value;
        String temp;
        for(int num = this.count; num > 1; this.data[value] = temp) {
            value = (int)sketch.random((float)num);
            --num;
            temp = this.data[num];
            this.data[num] = this.data[value];
        }

    }

    public void lower() {
        for(int i = 0; i < this.count; ++i) {
            if (this.data[i] != null) {
                this.data[i] = this.data[i].toLowerCase();
            }
        }

    }

    public void upper() {
        for(int i = 0; i < this.count; ++i) {
            if (this.data[i] != null) {
                this.data[i] = this.data[i].toUpperCase();
            }
        }

    }

    public StringList copy() {
        StringList outgoing = new StringList(this.data);
        outgoing.count = this.count;
        return outgoing;
    }

    public String[] values() {
        this.crop();
        return this.data;
    }

    public Iterator<String> iterator() {
        return new Iterator<String>() {
            int index = -1;

            public void remove() {
                StringList.this.remove(this.index);
                --this.index;
            }

            public String next() {
                return StringList.this.data[++this.index];
            }

            public boolean hasNext() {
                return this.index + 1 < StringList.this.count;
            }
        };
    }

    public String[] array() {
        return this.array((String[])null);
    }

    public String[] array(String[] array) {
        if (array == null || array.length != this.count) {
            array = new String[this.count];
        }

        System.arraycopy(this.data, 0, array, 0, this.count);
        return array;
    }

    public StringList getSubset(int start) {
        return this.getSubset(start, this.count - start);
    }

    public StringList getSubset(int start, int num) {
        String[] subset = new String[num];
        System.arraycopy(this.data, start, subset, 0, num);
        return new StringList(subset);
    }

    public String[] getUnique() {
        return this.getTally().keyArray();
    }

    public IntDict getTally() {
        IntDict outgoing = new IntDict();

        for(int i = 0; i < this.count; ++i) {
            outgoing.increment(this.data[i]);
        }

        return outgoing;
    }

    public IntDict getOrder() {
        IntDict outgoing = new IntDict();

        for(int i = 0; i < this.count; ++i) {
            outgoing.set(this.data[i], i);
        }

        return outgoing;
    }

    public String join(String separator) {
        if (this.count == 0) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(this.data[0]);

            for(int i = 1; i < this.count; ++i) {
                sb.append(separator);
                sb.append(this.data[i]);
            }

            return sb.toString();
        }
    }

    public void print() {
        for(int i = 0; i < this.count; ++i) {
            System.out.format("[%d] %s%n", i, this.data[i]);
        }

    }

    public void save(File file) {
        PrintWriter writer = PApplet.createWriter(file);
        this.write(writer);
        writer.close();
    }

    public void write(PrintWriter writer) {
        for(int i = 0; i < this.count; ++i) {
            writer.println(this.data[i]);
        }

        writer.flush();
    }

    public String toJSON() {
        StringList temp = new StringList();
        Iterator var2 = this.iterator();

        while(var2.hasNext()) {
            String item = (String)var2.next();
            temp.append(JSONObject.quote(item));
        }

        return "[ " + temp.join(", ") + " ]";
    }

    public String toString() {
        return this.getClass().getSimpleName() + " size=" + this.size() + " " + this.toJSON();
    }
}
