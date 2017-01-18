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
    private boolean                  mInitialized;
    private String                   crashDir;
    private String                   versionName;
    private int                      versionCode;

    private CrashUtils() {
    }

    /**
     * 获取单例
     * <p>在Application中初始化{@code CrashUtils.getInstance().init(this);}</p>
     *
     * @return 单例
     */
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

    /**
     * 初始化
     *
     * @return {@code true}: 成功<br>{@code false}: 失败
     */
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
                Log.e("test", "--->into uncaughtException run :");
                // 接下来就是调用服务端的接口去保存闪退的信息
            }
        }).start();
        if (mHandler != null) {
            mHandler.uncaughtException(thread, throwable);
        }
    }

    /**
     * 获取崩溃头
     *
     * @return 崩溃头
     */
    private String getCrashModel() {
        return "\n************* Crash Log Head ****************" +
                "\nDevice Manufacturer: " + Build.MANUFACTURER +// 设备厂商
                "\nDevice Model       : " + Build.MODEL +// 设备型号
                "\nAndroid Version    : " + Build.VERSION.RELEASE +// 系统版本
                "\nAndroid SDK        : " + Build.VERSION.SDK_INT +// SDK版本
                "\nApp VersionName    : " + versionName +
                "\nApp VersionCode    : " + versionCode +
                "\n************* Crash Log Head ****************\n\n";
    }
}
