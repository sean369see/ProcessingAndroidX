package processing.event;

public class MouseEvent extends Event {
    public static final int PRESS = 1;
    public static final int RELEASE = 2;
    public static final int CLICK = 3;
    public static final int DRAG = 4;
    public static final int MOVE = 5;
    public static final int ENTER = 6;
    public static final int EXIT = 7;
    protected int x;
    protected int y;
    protected int button;
    protected int clickCount;

    public MouseEvent(Object nativeObject, long millis, int action, int modifiers, int x, int y, int button, int clickCount) {
        super(nativeObject, millis, action, modifiers);
        this.flavor = 2;
        this.x = x;
        this.y = y;
        this.button = button;
        this.clickCount = clickCount;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getButton() {
        return this.button;
    }

    public int getClickCount() {
        return this.clickCount;
    }
}
