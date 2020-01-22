package processing.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

class JSONTokener {
    private long character;
    private boolean eof;
    private long index;
    private long line;
    private char previous;
    private Reader reader;
    private boolean usePrevious;

    public JSONTokener(Reader reader) {
        this.reader = (Reader)(reader.markSupported() ? reader : new BufferedReader(reader));
        this.eof = false;
        this.usePrevious = false;
        this.previous = 0;
        this.index = 0L;
        this.character = 1L;
        this.line = 1L;
    }

    public JSONTokener(InputStream inputStream) {
        this((Reader)(new InputStreamReader(inputStream)));
    }

    public JSONTokener(String s) {
        this((Reader)(new StringReader(s)));
    }

    public void back() {
        if (!this.usePrevious && this.index > 0L) {
            --this.index;
            --this.character;
            this.usePrevious = true;
            this.eof = false;
        } else {
            throw new RuntimeException("Stepping back two steps is not supported");
        }
    }

    public static int dehexchar(char c) {
        if (c >= '0' && c <= '9') {
            return c - 48;
        } else if (c >= 'A' && c <= 'F') {
            return c - 55;
        } else {
            return c >= 'a' && c <= 'f' ? c - 87 : -1;
        }
    }

    public boolean end() {
        return this.eof && !this.usePrevious;
    }

    public boolean more() {
        this.next();
        if (this.end()) {
            return false;
        } else {
            this.back();
            return true;
        }
    }

    public char next() {
        int c;
        if (this.usePrevious) {
            this.usePrevious = false;
            c = this.previous;
        } else {
            try {
                c = this.reader.read();
            } catch (IOException var3) {
                throw new RuntimeException(var3);
            }

            if (c <= 0) {
                this.eof = true;
                c = 0;
            }
        }

        ++this.index;
        if (this.previous == '\r') {
            ++this.line;
            this.character = c == 10 ? 0L : 1L;
        } else if (c == 10) {
            ++this.line;
            this.character = 0L;
        } else {
            ++this.character;
        }

        this.previous = (char)c;
        return this.previous;
    }

    public char next(char c) {
        char n = this.next();
        if (n != c) {
            throw new RuntimeException("Expected '" + c + "' and instead saw '" + n + "'");
        } else {
            return n;
        }
    }

    public String next(int n) {
        if (n == 0) {
            return "";
        } else {
            char[] chars = new char[n];

            for(int pos = 0; pos < n; ++pos) {
                chars[pos] = this.next();
                if (this.end()) {
                    throw new RuntimeException("Substring bounds error");
                }
            }

            return new String(chars);
        }
    }

    public char nextClean() {
        char c;
        do {
            c = this.next();
        } while(c != 0 && c <= ' ');

        return c;
    }

    public String nextString(char quote) {
        StringBuilder sb = new StringBuilder();

        while(true) {
            char c = this.next();
            switch(c) {
                case '\u0000':
                case '\n':
                case '\r':
                    throw new RuntimeException("Unterminated string");
                case '\\':
                    c = this.next();
                    switch(c) {
                        case '"':
                        case '\'':
                        case '/':
                        case '\\':
                            sb.append(c);
                            continue;
                        case 'b':
                            sb.append('\b');
                            continue;
                        case 'f':
                            sb.append('\f');
                            continue;
                        case 'n':
                            sb.append('\n');
                            continue;
                        case 'r':
                            sb.append('\r');
                            continue;
                        case 't':
                            sb.append('\t');
                            continue;
                        case 'u':
                            sb.append((char)Integer.parseInt(this.next((int)4), 16));
                            continue;
                        default:
                            throw new RuntimeException("Illegal escape.");
                    }
                default:
                    if (c == quote) {
                        return sb.toString();
                    }

                    sb.append(c);
            }
        }
    }

    public String nextTo(char delimiter) {
        StringBuilder sb = new StringBuilder();

        while(true) {
            char c = this.next();
            if (c == delimiter || c == 0 || c == '\n' || c == '\r') {
                if (c != 0) {
                    this.back();
                }

                return sb.toString().trim();
            }

            sb.append(c);
        }
    }

    public String nextTo(String delimiters) {
        StringBuilder sb = new StringBuilder();

        while(true) {
            char c = this.next();
            if (delimiters.indexOf(c) >= 0 || c == 0 || c == '\n' || c == '\r') {
                if (c != 0) {
                    this.back();
                }

                return sb.toString().trim();
            }

            sb.append(c);
        }
    }

    public Object nextValue() {
        char c = this.nextClean();
        switch(c) {
            case '"':
            case '\'':
                return this.nextString(c);
            case '[':
                this.back();
                return new JSONArray(this);
            case '{':
                this.back();
                return new JSONObject(this);
            default:
                StringBuilder sb;
                for(sb = new StringBuilder(); c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0; c = this.next()) {
                    sb.append(c);
                }

                this.back();
                String string = sb.toString().trim();
                if ("".equals(string)) {
                    throw new RuntimeException("Missing value");
                } else {
                    return JSONObject.stringToValue(string);
                }
        }
    }

    public char skipTo(char to) {
        char c;
        try {
            long startIndex = this.index;
            long startCharacter = this.character;
            long startLine = this.line;
            this.reader.mark(1000000);

            do {
                c = this.next();
                if (c == 0) {
                    this.reader.reset();
                    this.index = startIndex;
                    this.character = startCharacter;
                    this.line = startLine;
                    return c;
                }
            } while(c != to);
        } catch (IOException var9) {
            throw new RuntimeException(var9);
        }

        this.back();
        return c;
    }

    public String toString() {
        return " at " + this.index + " [character " + this.character + " line " + this.line + "]";
    }
}
