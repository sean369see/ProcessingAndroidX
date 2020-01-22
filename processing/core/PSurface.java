package processing.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import java.io.File;
import java.io.InputStream;
import processing.android.AppComponent;
import processing.android.ServiceEngine;

public interface PSurface {
    int REQUEST_PERMISSIONS = 1;

    AppComponent getComponent();

    Context getContext();

    Activity getActivity();

    ServiceEngine getEngine();

    void dispose();

    String getName();

    View getResource(int var1);

    Rect getVisibleFrame();

    SurfaceView getSurfaceView();

    SurfaceHolder getSurfaceHolder();

    View getRootView();

    void setRootView(View var1);

    void initView(int var1, int var2);

    void initView(int var1, int var2, boolean var3, LayoutInflater var4, ViewGroup var5, Bundle var6);

    void startActivity(Intent var1);

    void runOnUiThread(Runnable var1);

    void setOrientation(int var1);

    void setHasOptionsMenu(boolean var1);

    File getFilesDir();

    File getFileStreamPath(String var1);

    InputStream openFileInput(String var1);

    AssetManager getAssets();

    void setSystemUiVisibility(int var1);

    void startThread();

    void pauseThread();

    void resumeThread();

    boolean stopThread();

    boolean isStopped();

    void finish();

    void setFrameRate(float var1);

    boolean hasPermission(String var1);

    void requestPermissions(String[] var1);
}
