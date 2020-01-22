package processing.event;

public class Event {
    protected Object nativeObject;
    protected long millis;
    protected int action;
    public static final int SHIFT = 1;
    public static final int CTRL = 2;
    public static final int META = 4;
    public static final int ALT = 8;
    protected int modifiers;
    public static final int KEY = 1;
    public static final int MOUSE = 2;
    public static final int TOUCH = 3;
    protected int flavor;

    public Event(Object nativeObject, long millis, int action, int modifiers) {
        this.nativeObject = nativeObject;
        this.millis = millis;
        this.action = action;
        this.modifiers = modifiers;
    }

    public int getFlavor() {
        return this.flavor;
    }

    public Object getNative() {
        return this.nativeObject;
    }

    public long getMillis() {
        return this.millis;
    }

    public int getAction() {
        return this.action;
    }

    public int getModifiers() {
        return this.modifiers;
    }

    public boolean isShiftDown() {
        return (this.modifiers & 1) != 0;
    }

    public boolean isControlDown() {
        return (this.modifiers & 2) != 0;
    }

    public boolean isMetaDown() {
        return (this.modifiers & 4) != 0;
    }

    public boolean isAltDown() {
        return (this.modifiers & 8) != 0;
    }
}
