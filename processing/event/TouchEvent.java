package processing.event;

public class TouchEvent extends Event {
    public static final int START = 1;
    public static final int END = 2;
    public static final int CANCEL = 3;
    public static final int MOVE = 4;
    protected int action;
    protected int button;
    protected int numPointers;
    protected int[] pointerId;
    protected float[] pointerX;
    protected float[] pointerY;
    protected float[] pointerArea;
    protected float[] pointerPressure;

    public TouchEvent(Object nativeObject, long millis, int action, int modifiers, int button) {
        super(nativeObject, millis, action, modifiers);
        this.flavor = 3;
        this.button = button;
    }

    public void setNumPointers(int n) {
        this.numPointers = n;
        this.pointerId = new int[n];
        this.pointerX = new float[n];
        this.pointerY = new float[n];
        this.pointerArea = new float[n];
        this.pointerPressure = new float[n];
    }

    public void setPointer(int idx, int id, float x, float y, float a, float p) {
        this.pointerId[idx] = id;
        this.pointerX[idx] = x;
        this.pointerY[idx] = y;
        this.pointerArea[idx] = a;
        this.pointerPressure[idx] = p;
    }

    public int getNumPointers() {
        return this.numPointers;
    }

    public TouchEvent.Pointer getPointer(int idx) {
        TouchEvent.Pointer pt = new TouchEvent.Pointer();
        pt.id = this.pointerId[idx];
        pt.x = this.pointerX[idx];
        pt.y = this.pointerY[idx];
        pt.area = this.pointerArea[idx];
        pt.pressure = this.pointerPressure[idx];
        return pt;
    }

    public int getPointerId(int idx) {
        return this.pointerId[idx];
    }

    public float getPointerX(int idx) {
        return this.pointerX[idx];
    }

    public float getPointerY(int idx) {
        return this.pointerY[idx];
    }

    public float getPointerArea(int idx) {
        return this.pointerArea[idx];
    }

    public float getPointerPressure(int idx) {
        return this.pointerPressure[idx];
    }

    public int getButton() {
        return this.button;
    }

    public TouchEvent.Pointer[] getTouches(TouchEvent.Pointer[] touches) {
        int idx;
        if (touches == null || touches.length != this.numPointers) {
            touches = new TouchEvent.Pointer[this.numPointers];

            for(idx = 0; idx < this.numPointers; ++idx) {
                touches[idx] = new TouchEvent.Pointer();
            }
        }

        for(idx = 0; idx < this.numPointers; ++idx) {
            touches[idx].id = this.pointerId[idx];
            touches[idx].x = this.pointerX[idx];
            touches[idx].y = this.pointerY[idx];
            touches[idx].area = this.pointerArea[idx];
            touches[idx].pressure = this.pointerPressure[idx];
        }

        return touches;
    }

    public class Pointer {
        public int id;
        public float x;
        public float y;
        public float area;
        public float pressure;

        public Pointer() {
        }
    }
}
