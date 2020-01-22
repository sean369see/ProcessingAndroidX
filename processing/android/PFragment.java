package processing.android;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import processing.core.PApplet;

public class PFragment extends Fragment implements AppComponent {
    private DisplayMetrics metrics;
    private Point size;
    private PApplet sketch;
    @LayoutRes
    private int layout = -1;

    public PFragment() {
    }

    public PFragment(PApplet sketch) {
        this.setSketch(sketch);
    }

    public void initDimensions() {
        this.metrics = new DisplayMetrics();
        this.size = new Point();
        WindowManager wm = this.getActivity().getWindowManager();
        Display display = wm.getDefaultDisplay();
        CompatUtils.getDisplayParams(display, this.metrics, this.size);
    }

    public int getDisplayWidth() {
        return this.size.x;
    }

    public int getDisplayHeight() {
        return this.size.y;
    }

    public float getDisplayDensity() {
        return this.metrics.density;
    }

    public int getKind() {
        return 0;
    }

    public void setSketch(PApplet sketch) {
        this.sketch = sketch;
        if (this.layout != -1) {
            sketch.parentLayout = this.layout;
        }

    }

    public PApplet getSketch() {
        return this.sketch;
    }

    public void setLayout(@LayoutRes int layout, @IdRes int id, FragmentActivity activity) {
        this.layout = layout;
        if (this.sketch != null) {
            this.sketch.parentLayout = layout;
        }

        FragmentManager manager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(id, this);
        transaction.commit();
    }

    public void setView(View view, FragmentActivity activity) {
        FragmentManager manager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(view.getId(), this);
        transaction.commit();
    }

    public boolean isService() {
        return false;
    }

    public ServiceEngine getEngine() {
        return null;
    }

    public void dispose() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.sketch != null) {
            this.sketch.initSurface(inflater, container, savedInstanceState, this, (SurfaceHolder)null);
            this.sketch.onCreate(savedInstanceState);
            return this.sketch.getSurface().getRootView();
        } else {
            return null;
        }
    }

    public void onStart() {
        super.onStart();
        if (this.sketch != null) {
            this.sketch.onStart();
        }

    }

    public void onResume() {
        super.onResume();
        if (this.sketch != null) {
            this.sketch.onResume();
        }

    }

    public void onPause() {
        super.onPause();
        if (this.sketch != null) {
            this.sketch.onPause();
        }

    }

    public void onStop() {
        super.onStop();
        if (this.sketch != null) {
            this.sketch.onStop();
        }

    }

    public void onDestroy() {
        super.onDestroy();
        if (this.sketch != null) {
            this.sketch.onDestroy();
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (this.sketch != null) {
            this.sketch.onActivityResult(requestCode, resultCode, data);
        }

    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (this.sketch != null) {
            this.sketch.onCreateOptionsMenu(menu, inflater);
        }

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return this.sketch != null ? this.sketch.onOptionsItemSelected(item) : super.onOptionsItemSelected(item);
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        if (this.sketch != null) {
            this.sketch.onCreateContextMenu(menu, v, menuInfo);
        }

    }

    public boolean onContextItemSelected(MenuItem item) {
        return this.sketch != null ? this.sketch.onContextItemSelected(item) : super.onContextItemSelected(item);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void setOrientation(int which) {
        if (which == 1) {
            this.getActivity().setRequestedOrientation(1);
        } else if (which == 2) {
            this.getActivity().setRequestedOrientation(0);
        }

    }

    public void requestDraw() {
    }

    public boolean canDraw() {
        return this.sketch != null && this.sketch.isLooping();
    }
}
