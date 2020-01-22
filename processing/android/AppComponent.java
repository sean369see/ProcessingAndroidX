//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package processing.android;

import android.content.Intent;
import processing.core.PApplet;
import processing.core.PConstants;

public interface AppComponent extends PConstants {
    int FRAGMENT = 0;
    int WALLPAPER = 1;
    int WATCHFACE = 2;

    void initDimensions();

    int getDisplayWidth();

    int getDisplayHeight();

    float getDisplayDensity();

    int getKind();

    void setSketch(PApplet var1);

    PApplet getSketch();

    boolean isService();

    ServiceEngine getEngine();

    void startActivity(Intent var1);

    void requestDraw();

    boolean canDraw();

    void dispose();
}
