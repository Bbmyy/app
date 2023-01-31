package io.dcloud.uniplugin;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

import java.util.ArrayList;
import java.util.List;

import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.common.UniModule;


public class PythonModule extends UniModule {
    String TAG = "PythonModule";
    public static int REQUEST_CODE = 1001;
    //public PyObject pyModule = null;

    //run JS thread
    @UniJSMethod (uiThread = false)
    public JSONObject initPython(){
        Log.e(TAG,"initpython");
        JSONObject data = new JSONObject();
        try {
            GlobalData.initPython();
        } catch (AppInnerException e) {
            Log.e(TAG,e.getMessage());
            data.put("success", false);
            return data;
        }
        Log.e(TAG,"SuccessGetPython");
        data.put("success", true);
        return data;
    }
    @UniJSMethod (uiThread = false)
    public JSONObject getModuleList(JSONObject options){
        Log.e(TAG,"getmodule");
        JSONObject data = new JSONObject();
        data.put("success", true);
        List<String> list = new ArrayList<>();
        list.add("AddrDetect");
        data.put("data",list);
        Log.e(TAG,list.toString());
        return data;
    }
    @UniJSMethod(uiThread = true)
    public void runPythonModule(JSONObject options, UniJSCallback callback) {
        Log.e(TAG,"runPythonModule");
        if(callback != null) {
            JSONObject data = new JSONObject();
            data.put("success", false);
            try {
                String modulename = (String) options.get("module");
                String arg = (String) options.get("arg");
                Log.e(TAG,options.toString());
                if(modulename == ""){
                    data.put("info","模块名不能为空");
                }else{
                    Python pythonInstance = GlobalData.getPython();
                    Log.e(TAG,"开始运行Python");

                    PyObject backend = pythonInstance.getModule(modulename);
                    if(backend == null){
                        data.put("info","打开模块"+modulename+"失败");
                    }else{
                        PyObject res = backend.callAttr("main", arg);
                        data.put("data",res.toString());
                        Log.e(TAG,res.toString());
                        data.put("success", true);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG,e.toString());
                data.put("info",e.toString());
            }
            callback.invoke(data);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        initPython();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @UniJSMethod (uiThread = true)
    public void initEnv(JSONObject options, UniJSCallback callback){
        Log.e(TAG,"initenv");
        if(mUniSDKInstance != null && mUniSDKInstance.getContext() instanceof Activity) {
            Intent intent = new Intent(mUniSDKInstance.getContext(), NativePagePythonInitActivity.class);
            ((Activity)mUniSDKInstance.getContext()).startActivityForResult(intent, REQUEST_CODE);
        }
    }
}
