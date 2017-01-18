package linc.ps.tools.base;

import android.app.Application;
import android.content.Context;

import linc.ps.tools.crash.CrashUtils;

/**
 * Created by Frank on 2016/12/22.
 * 1.全局异常捕获
 * 2.讯飞语音初始化
 * 3.数据库
 */

public class MyApplication extends Application {
    public static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        // 全局闪退异常记录
        CrashUtils crashUtils = CrashUtils.getInstance();
        crashUtils.init();
        // 讯飞语音注册,初始化语音对象
//        SpeechUtility.createUtility(context, SpeechConstant.APPID+"=5876d59c");
//        AudioUtils.getInstance().init(context);
    }

    /**
     * 获取全局上下文*/
    public static Context getContext() {
        return context;
    }
}
