package io.dcloud.uniplugin;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;

import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.common.UniModule;


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
    public JSONObject testSyncFunc(){
        JSONObject data = new JSONObject();
        data.put("code", "success");
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
