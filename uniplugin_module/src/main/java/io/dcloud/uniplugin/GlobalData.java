package io.dcloud.uniplugin;

import android.content.Context;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

public class GlobalData {
    private static Context appContext = null;
    private static Python pythonInstance = null;

    public static void setAppContext(Context appContext) {
        GlobalData.appContext = appContext;
    }

    public static Python initPython() throws AppInnerException{
        if(appContext == null){
            throw new AppInnerException("Python环境尚未初始化");
        }
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(appContext));
        }
        if(pythonInstance == null)  pythonInstance = Python.getInstance();
        return pythonInstance;
    }
    public static Python getPython() throws AppInnerException {
        if(pythonInstance == null) throw new AppInnerException("Python尚未初始化");
        return pythonInstance;
    }
}
