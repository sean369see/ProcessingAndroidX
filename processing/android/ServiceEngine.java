package processing.android;

import android.graphics.Rect;
import processing.core.PConstants;

public interface ServiceEngine extends PConstants {
    boolean isPreview();

    float getXOffset();

    float getYOffset();

    float getXOffsetStep();

    float getYOffsetStep();

    int getXPixelOffset();

    int getYPixelOffset();

    boolean isInAmbientMode();

    boolean isRound();

    Rect getInsets();

    boolean useLowBitAmbient();

    boolean requireBurnInProtection();

    void onRequestPermissionsResult(int var1, String[] var2, int[] var3);
}
