package processing.android;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import androidx.core.app.ActivityCompat;

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

    protected void onStart() {
        super.onStart();
        this.resultReceiver = (ResultReceiver)this.getIntent().getParcelableExtra("resultReceiver");
        this.permissions = this.getIntent().getStringArrayExtra("permissions");
        this.requestCode = this.getIntent().getIntExtra("requestCode", 0);
        ActivityCompat.requestPermissions(this, this.permissions, this.requestCode);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Bundle resultData = new Bundle();
        resultData.putStringArray("permissions", permissions);
        resultData.putIntArray("grantResults", grantResults);
        this.resultReceiver.send(requestCode, resultData);
        this.finish();
    }
}
