package io.dcloud.uniplugin;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.List;

import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.common.UniModule;
import io.dcloud.uniplugin.txtParse.format.DataBase;
import io.dcloud.uniplugin.txtParse.services.ParseService;
import io.dcloud.uniplugin.txtParse.utils.AdbUtil;


public class TestModule extends UniModule {
    String TAG = "TestModule";
    public static int REQUEST_CODE = 1000;


    @UniJSMethod(uiThread = true)
    public void testAsyncFunc(JSONObject options, UniJSCallback callback) {
        Log.e(TAG, "testAsyncFunc--"+options);
        if(callback != null) {
            JSONObject data = new JSONObject();
            data.put("success", false);
            callback.invoke(data);
        }
    }

    //run JS thread
    @UniJSMethod (uiThread = false)
    public JSONObject testSyncFunc() throws IOException {
        JSONObject data = new JSONObject();
        String path = "data/local/tmp/ecapture1";
        //AdbUtil.execShell("su");
        List<DataBase> dataBases = ParseService.ReadTxt(path + "/1.txt");
        //String result = AdbUtil.execShell("./" + path +"/ecapture");
        data.put("code", "success");
        data.put("data", dataBases);
        return data;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("TestModule", "原生页面返回----");
        super.onActivityResult(requestCode, resultCode, data);
    }

    @UniJSMethod (uiThread = true)
    public void gotoNativePage(JSONObject options, UniJSCallback callback){
        Log.e("TestModule", "原生页面");
        if(mUniSDKInstance != null && mUniSDKInstance.getContext() instanceof Activity) {
            Intent intent = new Intent(mUniSDKInstance.getContext(), NativePageActivity.class);
            ((Activity)mUniSDKInstance.getContext()).startActivityForResult(intent, REQUEST_CODE);
        }
    }
}
