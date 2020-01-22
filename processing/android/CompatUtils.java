package processing.android;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

public class CompatUtils {
    private static final AtomicInteger nextId = new AtomicInteger(15000000);

    public CompatUtils() {
    }

    public static void getDisplayParams(Display display, DisplayMetrics metrics, Point size) {
        if (17 <= VERSION.SDK_INT) {
            display.getRealMetrics(metrics);
            display.getRealSize(size);
        }

        if (14 <= VERSION.SDK_INT) {
            display.getMetrics(metrics);

            try {
                size.x = (Integer)Display.class.getMethod("getRawWidth").invoke(display);
                size.y = (Integer)Display.class.getMethod("getRawHeight").invoke(display);
            } catch (Exception var4) {
                display.getSize(size);
            }
        } else {
            display.getMetrics(metrics);
            display.getSize(size);
        }

    }

    @SuppressLint({"NewApi"})
    public static int getUniqueViewId() {
        if (17 <= VERSION.SDK_INT) {
            return View.generateViewId();
        } else {
            int result;
            int newValue;
            do {
                result = nextId.get();
                newValue = result + 1;
                if (newValue > 16777215) {
                    newValue = 1;
                }
            } while(!nextId.compareAndSet(result, newValue));

            return result;
        }
    }

    @SuppressLint({"NewApi"})
    public static Charset getCharsetUTF8() {
        return 19 <= VERSION.SDK_INT ? StandardCharsets.UTF_8 : Charset.forName("UTF-8");
    }
}