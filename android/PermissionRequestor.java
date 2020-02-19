/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */

/*
  Part of the Processing project - http://processing.org

  Copyright (c) 2017 The Processing Foundation

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License version 2.1 as published by the Free Software Foundation.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
*/

/*
  AndroidX modification project by Xuan "Sean" Li
*/


package processing.android;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import androidx.core.app.ActivityCompat;

// A simple utility activity to request permissions in a service.
public class PermissionRequestor extends Activity {
    public static final String KEY_RESULT_RECEIVER = "resultReceiver";
    public static final String KEY_PERMISSIONS = "permissions";
    public static final String KEY_GRANT_RESULTS = "grantResults";
    public static final String KEY_REQUEST_CODE = "requestCode";

    ResultReceiver resultReceiver;
    String[] permissions;
    int requestCode;

    public PermissionRequestor() {
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.resultReceiver = (ResultReceiver)this.getIntent().getParcelableExtra(KEY_RESULT_RECEIVER);
        this.permissions = this.getIntent().getStringArrayExtra(KEY_PERMISSIONS);
        this.requestCode = this.getIntent().getIntExtra(KEY_REQUEST_CODE, 0);
        ActivityCompat.requestPermissions(this, this.permissions, this.requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Bundle resultData = new Bundle();
        resultData.putStringArray(KEY_PERMISSIONS, permissions);
        resultData.putIntArray(KEY_GRANT_RESULTS, grantResults);
        this.resultReceiver.send(requestCode, resultData);
        this.finish();
    }

}
