package linc.ps.tools.image.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;
/**
 * Created by Frank on 2017/5/18.
 */

public class ScreenUtils {

    /**
     * 获取屏幕的宽度（单位：px）
     * @return 屏幕宽px
     */
    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();// 创建了一张白纸
        windowManager.getDefaultDisplay().getMetrics(dm);// 给白纸设置宽高
        return dm.widthPixels;
    }

    /**
     * 获取屏幕的高度（单位：px）
     * @return 屏幕高px
     */
    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();// 创建了一张白纸
        windowManager.getDefaultDisplay().getMetrics(dm);// 给白纸设置宽高
        return dm.heightPixels;
    }


    /**
     * dip转px
     *
     * @param context
     * @param value
     * @return
     */
    public static int dip2px(Context context, float value) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }

    /**
     * px转dip
     *
     * @param context
     * @param value
     * @return
     */
    public static int px2dip(Context context, float value) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (value / scale + 0.5f);
    }
}
