package processing.event;

public class KeyEvent extends Event {
    public static final int PRESS = 1;
    public static final int RELEASE = 2;
    public static final int TYPE = 3;
    char key;
    int keyCode;
    boolean isAutoRepeat;

    public KeyEvent(Object nativeObject, long millis, int action, int modifiers, char key, int keyCode) {
        super(nativeObject, millis, action, modifiers);
        this.flavor = 1;
        this.key = key;
        this.keyCode = keyCode;
    }

    public KeyEvent(Object nativeObject, long millis, int action, int modifiers, char key, int keyCode, boolean isAutoRepeat) {
        super(nativeObject, millis, action, modifiers);
        this.flavor = 1;
        this.key = key;
        this.keyCode = keyCode;
        this.isAutoRepeat = isAutoRepeat;
    }

    public char getKey() {
        return this.key;
    }

    public int getKeyCode() {
        return this.keyCode;
    }

    public boolean isAutoRepeat() {
        return this.isAutoRepeat;
    }
}
