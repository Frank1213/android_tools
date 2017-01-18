package linc.ps.tools.crash;
import android.os.Build;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Created by Frank on 2016/12/22.
 * 崩溃相关工具类
 */
public class CrashUtils implements Thread.UncaughtExceptionHandler {
    private volatile static CrashUtils mInstance;
    private Thread.UncaughtExceptionHandler mHandler;
    private boolean mInitialized;
    private CrashUtils() {

    }
    /**单例**/
    public static CrashUtils getInstance() {
        if (mInstance == null) {
            synchronized (CrashUtils.class) {
                if (mInstance == null) {
                    mInstance = new CrashUtils();
                }
            }
        }
        return mInstance;
    }
    /*** 初始化{true: 成功;false: 失败}**/
    public boolean init() {
        if (mInitialized) return true;
        mHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        return mInitialized = true;
    }
    @Override
    public void uncaughtException(Thread thread, final Throwable throwable) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                CrashModel crashModel = new CrashModel();
                crashModel.setDateNow(new SimpleDateFormat("yy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
                crashModel.setDeviceManufacturer(Build.MANUFACTURER);
                crashModel.setDeviceModel(Build.MODEL);
                crashModel.setAndroidVersion(Build.VERSION.RELEASE);
                crashModel.setAndroidSDK(Build.VERSION.SDK_INT);
                crashModel.setMessage(throwable.getMessage());
                // 接下来就是调用服务端的接口去保存闪退的信息
            }
        }).start();
        if (mHandler != null) {
            mHandler.uncaughtException(thread, throwable);
        }
    }
}
