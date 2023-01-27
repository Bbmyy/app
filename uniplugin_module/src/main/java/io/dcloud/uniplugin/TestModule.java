package io.dcloud.uniplugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.common.UniModule;


public class TestModule extends UniModule {
    String TAG = "TestModule";
    public static int REQUEST_CODE = 1000;
    Python pythonInstance = null;
    //run JS thread
    @UniJSMethod (uiThread = false)
    public JSONObject init(){

        JSONObject data = new JSONObject();
        if(GlobalData.appContext == null){
            data.put("success", false);
            return data;
        }
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(GlobalData.appContext));
        }
        pythonInstance = Python.getInstance();
        Log.e("Test","SuccessGetPython");
        data.put("success", true);
        return data;
    }

    @UniJSMethod(uiThread = true)
    public void testAsyncFunc(JSONObject options, UniJSCallback callback) {
        Log.e(TAG, "testAsyncFunc--"+options);
        if(callback != null) {
            JSONObject data = new JSONObject();
            data.put("success", false);
            if(pythonInstance == null){
                Log.e("Test","Python尚未初始化");
                data.put("info", "Python环境尚未初始化，请先调用init函数。");
            }else{
                Log.e("Test","开始运行Python");
                try{
                    PyObject backend = pythonInstance.getModule("testpy123");
                    if(backend == null){
                        data.put("info","打开文件失败");
                    }else{
                        PyObject res = backend.callAttr("test_function", 10);
                        data.put("data",res.toString());
                        Log.e("Test",res.toString());
                        data.put("success", true);
                    }
                }catch (Exception e){
                    Log.e("Test",e.toString());
                    data.put("info",e.toString());
                    //e.printStackTrace();
                }
            }

            callback.invoke(data);
            //callback.invokeAndKeepAlive(data);
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
        /*if(requestCode == REQUEST_CODE && data.hasExtra("respond")) {
            Log.e("TestModule", "原生页面返回----"+data.getStringExtra("respond"));
        } else {
        */
        //}
        init();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @UniJSMethod (uiThread = true)
    public void gotoNativePage(JSONObject options, UniJSCallback callback){
        if(mUniSDKInstance != null && mUniSDKInstance.getContext() instanceof Activity) {
            Intent intent = new Intent(mUniSDKInstance.getContext(), NativePageActivity.class);
            ((Activity)mUniSDKInstance.getContext()).startActivityForResult(intent, REQUEST_CODE);
        }

    }
}
