package processing.data;

import java.io.PrintWriter;

public interface TableRow {
    String getString(int var1);

    String getString(String var1);

    int getInt(int var1);

    int getInt(String var1);

    long getLong(int var1);

    long getLong(String var1);

    float getFloat(int var1);

    float getFloat(String var1);

    double getDouble(int var1);

    double getDouble(String var1);

    void setString(int var1, String var2);

    void setString(String var1, String var2);

    void setInt(int var1, int var2);

    void setInt(String var1, int var2);

    void setLong(int var1, long var2);

    void setLong(String var1, long var2);

    void setFloat(int var1, float var2);

    void setFloat(String var1, float var2);

    void setDouble(int var1, double var2);

    void setDouble(String var1, double var2);

    int getColumnCount();

    int getColumnType(String var1);

    int getColumnType(int var1);

    int[] getColumnTypes();

    String getColumnTitle(int var1);

    String[] getColumnTitles();

    void write(PrintWriter var1);

    void print();
}
