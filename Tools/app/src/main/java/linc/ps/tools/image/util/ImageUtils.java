package linc.ps.tools.image.util;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Created by Frank on 2017/5/18.
 */
public class ImageUtils {

    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {// API
            // 12
            return bitmap.getByteCount();
        }
        return bitmap.getRowBytes() * bitmap.getHeight(); // earlier version
    }

    /**
     * 缩放图片
     * @param src       源图片
     * @param newWidth  新宽度
     * @param newHeight 新高度
     * @param recycle   是否回收
     * @return 缩放后的图片的字节数组
     */
    public static byte[] scale(Bitmap src, int newWidth, int newHeight, boolean recycle) {
        Bitmap ret = Bitmap.createScaledBitmap(src, newWidth, newHeight, true);
        if (recycle && !src.isRecycled()) src.recycle();
        return compressImage(ret);
    }

    /**
     * 质量压缩方法
     * @param image
     * @return
     */
    public static byte[] compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;
        Log.e("signature", "baos.toByteArray().length压缩前==" + baos.toByteArray().length);
        while (baos.toByteArray().length / 1024 > 4) { // 循环判断如果压缩后图片是否大于5kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 5;// 每次都减少10
            if (options <= 0) {
                options = 0;
                break;
            }
            Log.e("signature", "options==" + options);
        }
        Log.e("signature", "baos.toByteArray().length压缩后==" + baos.toByteArray().length);
        return baos.toByteArray();
    }
}
