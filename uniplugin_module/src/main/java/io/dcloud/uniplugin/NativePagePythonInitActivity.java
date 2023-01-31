package io.dcloud.uniplugin;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class NativePagePythonInitActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalData.setAppContext(getApplicationContext());
        Log.e("PythonInit","设置AppContext");
        finish();
    }

}
