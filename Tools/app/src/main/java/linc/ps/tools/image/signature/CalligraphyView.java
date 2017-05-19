package linc.ps.tools.image.signature;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import linc.ps.tools.image.util.ImageUtils;

/**
 * Created by Frank on 2017/5/18.
 * 签名类
 */
public class CalligraphyView extends View {

    private static final float VELOCITY_FILTER_WEIGHT = 0.28f;
    private static final long DELAY_START_TIME_FACTOR = 130;
    private static final String TAG = "CalligraphyView";

    private Paint mPaint;
    private Canvas mCanvas;
    private Bitmap mCache;
    private boolean isDown;
    private boolean isMoved;
    private SignatureReadyListener signatureReadyListener;
    private float minStroke;
    private float maxStroke;
    private float vMax;
    private List<Point> mPoint;
    private float lastVelocity;
    private float lastWidth;
    private Bezier lastBezier;
    private float lastX;
    private float lastY;
    private float wFactor;
    private long sTime;
    private int width;
    private int height;
    private Handler mHandler;
    private AtomicInteger buildCount = new AtomicInteger();
    private boolean isRecycled;
    private boolean bitmapReady;

    public CalligraphyView(Context context) {
        super(context);
        init();

    }

    public CalligraphyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public CalligraphyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setOnSignatureReadyListener(SignatureReadyListener signatureReadyListener) {
        this.signatureReadyListener = signatureReadyListener;
    }

    private void init() {
        mHandler = new Handler();
        mPaint = new Paint(Paint.DITHER_FLAG);
        mPaint.setStrokeWidth(5.0f);
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        isDown = false;
        setStrokeRange(1, 15);// 15是设置字体的粗细，越小越细
        mPoint = new ArrayList<Point>();
        lastVelocity = 0;
        buildCount.set(0);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        if (w != 0 && h != 0) {
            buildBitmap(w, h);
        }
    }

    private void buildBitmap(int w, int h) {
        if (width == w && height == h)
            return;
        width = w;
        height = h;
        if (width > 0 && height > 0 && !isRecycled) {
            new Thread() {
                private int p;

                public void run() {
                    p = buildCount.incrementAndGet();
                    // costing time, FIXME try?
                    final Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    // end time
                    if (buildCount.get() != p)
                        return;
                    mCache = bm;
                    mCanvas = new Canvas(mCache);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (signatureReadyListener != null) {
                                signatureReadyListener.onSignatureReady(false);
                                bitmapReady = true;
                            }
                        }
                    });
                }
            }.start();
        }
    }

    public void setStrokeRange(float min, float max) {
        if (min < 1 || max < 1 || max < min)
            throw new IllegalArgumentException("error stroke range");
        minStroke = min;
        maxStroke = max;
        maxStroke = dp2px(max);
        minStroke = dp2px(min);
        // vMax = (max - min) * 0.1f + min;
        vMax = (maxStroke - minStroke) * 0.1f + min;
        minStroke = min;
    }

    /**
     * @param dpValue
     * @return int px
     */
    public int dp2px(float dpValue) {
        float v = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
        return (int) (v + 0.5f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mCache != null && !mCache.isRecycled()) {
            canvas.drawBitmap(mCache, 0, 0, mPaint);
        }
    }

    private void touch_start(MotionEvent event) {
        mPoint.clear();
        addPoint(event);
        isMoved = false;
    }

    private void touch_move(MotionEvent event) {
        int len = event.getHistorySize();
        if (len > 0) {
            for (int i = 0; i < len; i++) {
                addPoint(Point.from(event, i));
            }
        } else {
            addPoint(Point.from(event));
        }
        if (!isMoved && mPoint.size() > 1) {
            final Point finalP = mPoint.get(mPoint.size() - 1);
            final Point firstP = mPoint.get(0);
            if (finalP.distanceFrom(firstP) > maxStroke) {
                isMoved = true;
            }
        }
    }

    private void touch_up(MotionEvent event) {
        if (isMoved) {
            addPoint(event);
        }
        finalPoint(event);
        if (mCanvas != null) {
            mPoint.clear();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!bitmapReady)
            return super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDown = true;
                touch_start(event);
                if (!isMoved && signatureReadyListener != null && mCanvas != null) {
                    signatureReadyListener.onStartSigningSignature(true);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isDown) {
                    touch_move(event);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isDown) {
                    touch_up(event);
                    invalidate();
                    if (isMoved && signatureReadyListener != null && mCanvas != null) {
                        signatureReadyListener.onSignatureReady(true);
                    }
                }
                isDown = false;
                break;
        }
        return true;
    }

    public void clear() {
        if (mCanvas != null) {
            mCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
            invalidate();
            if (signatureReadyListener != null) {
                signatureReadyListener.onSignatureReady(false);
            }
        }
    }

    public Bitmap getBitmap() {
        return mCache;
    }

    public interface SignatureReadyListener {
        void onSignatureReady(boolean ready);

        void onStartSigningSignature(boolean startSigning);
    }

    public void recycle() {
        isRecycled = true;
        if (mCache != null) {
            mCache.recycle();
        }
        mCanvas = null;
        if (signatureReadyListener != null) {
            signatureReadyListener.onSignatureReady(false);
        }
    }

    private void addPoint(MotionEvent e) {
        Point p = Point.from(e);
        if (mPoint.size() == 0) {
            mPoint.add(p);
            lastWidth = 0;
            wFactor = 0;
            sTime = e.getEventTime();
            lastBezier = null;
            p.width = lastWidth;
        } else {
            addPoint(p);
        }
    }

    private void addPoint(Point p) {
        Point lp = mPoint.get(mPoint.size() - 1);
        mPoint.add(p);
        Bezier bezier = new Bezier(lp, p);
        float velocity = p.velocityFrom(lp);
        velocity = VELOCITY_FILTER_WEIGHT * velocity + (1 - VELOCITY_FILTER_WEIGHT) * lastVelocity;

        float percent = 1 - velocity / vMax;
        if (percent > 1)
            percent = 1;
        else if (percent < 0)
            percent = 0;
        if (wFactor < 1)
            wFactor = (p.time - sTime) / (float) DELAY_START_TIME_FACTOR;
        if (wFactor > 1)
            wFactor = 1;
        p.width = (maxStroke - minStroke) * percent * p.width * wFactor + minStroke;
        addBezier(bezier, lastWidth, p.width);
        lastVelocity = velocity;
        lastWidth = p.width;
    }

    private void finalPoint(MotionEvent e) {
        int len = mPoint.size();
        if (len > 2 && lastBezier != null) {
            Point p = mPoint.get(len - 1);
            float x = (p.x - lastBezier.startX) * 3 + p.x;
            float y = (p.y - lastBezier.startY) * 3 + p.y;
            Point pe = new Point(x, y, p.time + 1, 0);
            Bezier bezier = new Bezier(p, pe);
            addBezier(bezier, lastWidth, 0);
        }
    }

    private void addBezier(Bezier curve, float startWidth, float endWidth) {
        if (mCanvas == null) {
            return;
        }
        curve.draw(mCanvas, mPaint, startWidth, endWidth);
    }

    private static class Point {
        public final float x, y;
        public final long time;
        public float width;

        public Point(float x, float y, long time, float pressure) {
            this.x = x;
            this.y = y;
            this.time = time;
            width = pressure;
        }

        public float distanceFrom(Point p) {
            float dx = p.x - x;
            float dy = p.y - y;
            float d = dx * dx + dy * dy;
            d = (float) Math.sqrt(d);
            return d;
        }

        public float velocityFrom(Point p) {
            return distanceFrom(p) / (time - p.time);
        }

        public static Point from(MotionEvent e) {
            return new Point(e.getX(), e.getY(), e.getEventTime(), e.getPressure());
        }

        public static Point from(MotionEvent e, int pos) {
            return new Point(e.getHistoricalX(pos), e.getHistoricalY(pos), e.getHistoricalEventTime(pos),
                    e.getHistoricalPressure(pos));
        }
    }

    private class Bezier {
        private Point startPoint, endPoint;
        private float startX, startY;

        public Bezier(Point lp, Point p) {
            startPoint = lp;
            endPoint = p;
        }

        public void draw(Canvas canvas, Paint paint, float startWidth, float endWidth) {
            float originalWidth = paint.getStrokeWidth();
            float widthDelta = endWidth - startWidth;
            int roundDelta = (int) Math.ceil(Math.abs(widthDelta));
            int drawSteps = roundDelta > 0 ? roundDelta * 10 : 10;
            if (lastBezier == null) {
                startX = (startPoint.x + endPoint.x) / 2;
                startY = (startPoint.y + endPoint.y) / 2;
                float lastX = startPoint.x;
                float lastY = startPoint.y;
                for (int i = 1; i < drawSteps; i++) {
                    float t = ((float) i) / drawSteps;
                    float x = startPoint.x + (startX - startPoint.x) * t;
                    float y = startPoint.y + (startY - startPoint.y) * t;

                    paint.setStrokeWidth(startWidth + t * widthDelta);
                    canvas.drawLine(lastX, lastY, x, y, paint);
                    // canvas.drawPoint(x, y, paint);
                    lastX = x;
                    lastY = y;
                }
                CalligraphyView.this.lastX = startX;
                CalligraphyView.this.lastY = startY;
                lastBezier = this;
            } else {
                float lastX = lastBezier.startX;
                float lastY = lastBezier.startY;
                float cx = startPoint.x;
                float cy = startPoint.y;
                startX = (startPoint.x + endPoint.x) / 2;
                startY = (startPoint.y + endPoint.y) / 2;
                for (int i = 0; i < drawSteps; i++) {
                    float t = ((float) i) / drawSteps;
                    float tt = t * t;
                    float x = lastX + 2 * (cx - lastX) * t + (startX - 2 * cx + lastX) * tt;
                    float y = lastY + 2 * (cy - lastY) * t + (startY - 2 * cy + lastY) * tt;
                    paint.setStrokeWidth(startWidth + t * widthDelta);
                    canvas.drawLine(CalligraphyView.this.lastX, CalligraphyView.this.lastY, x, y, paint);
                    CalligraphyView.this.lastX = x;
                    CalligraphyView.this.lastY = y;
                }
                lastBezier = this;
            }
            paint.setStrokeWidth(originalWidth);
        }
    }

    /**
     * 保存画板
     *
     * @param path 保存的路径
     */
    public void save(String path) throws IOException {
        save(path, false, 0);
    }

    /**
     * 保存画板
     *
     * @param path       保存到路劲
     * @param clearBlank 是否清楚空白区域
     * @param blank      边缘空白区域
     */
    public void save(String path, boolean clearBlank, int blank) throws IOException {
        Bitmap bitmap = mCache;
        if (clearBlank) {
            bitmap = clearBlank(mCache, blank);
        }
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Log.e("signature", "newBitmap压缩前==" + newBitmap.getWidth() + "  " + newBitmap.getHeight() + "  大小==" + ImageUtils.getBitmapSize(newBitmap) / 1024 + "kb");

        //Canvas要写在压缩之前，不然压缩后的图片就会想截图一样的结果
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvas.drawBitmap(bitmap, 0, 0, null);

        //压缩（压缩后的图片宽高确定，图片大小小于5kb）
        byte[] bs = ImageUtils.scale(newBitmap, (int) Const.width, (int) Const.height, false);// 按给定的宽高缩放压缩后再质量压缩
//		byte[] bs=ImageUtils.compressScale(newBitmap);//按比例大小压缩后，在质量压缩


        Log.e("signature", "newBitmap压缩后字节==" + bs.length);

        byte[] buffer = bs;
        if (buffer != null) {
            File file = new File(path);
            if (file.exists()) {
                if (file.delete()) {
                    Log.e("signature", "file delete success!");
                } else {
                    Log.e("signature", "file delete fail!");
                }
            }
            file = new File(path);
            OutputStream outputStream = new FileOutputStream(file);
            outputStream.write(buffer);
            outputStream.close();
            scanMediaFile(file);
            // 释放Bitmap资源
            if (newBitmap != null && !newBitmap.isRecycled()) {
                newBitmap.recycle();
            }
        }
    }

    /**
     * 逐行扫描 清楚边界空白。
     *
     * @param bp
     * @param blank 边距留多少个像素
     * @return
     */
    private Bitmap clearBlank(Bitmap bp, int blank) {
        int HEIGHT = bp.getHeight();
        int WIDTH = bp.getWidth();
        int top = 0, left = 0, right = 0, bottom = 0;
        int[] pixs = new int[WIDTH];
        boolean isStop;
        for (int y = 0; y < HEIGHT; y++) {
            bp.getPixels(pixs, 0, WIDTH, 0, y, WIDTH, 1);
            isStop = false;
            for (int pix : pixs) {
                if (pix != Color.TRANSPARENT) {
                    top = y;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        for (int y = HEIGHT - 1; y >= 0; y--) {
            bp.getPixels(pixs, 0, WIDTH, 0, y, WIDTH, 1);
            isStop = false;
            for (int pix : pixs) {
                if (pix != Color.TRANSPARENT) {
                    bottom = y;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        pixs = new int[HEIGHT];
        for (int x = 0; x < WIDTH; x++) {
            bp.getPixels(pixs, 0, 1, x, 0, 1, HEIGHT);
            isStop = false;
            for (int pix : pixs) {
                if (pix != Color.TRANSPARENT) {
                    left = x;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        for (int x = WIDTH - 1; x > 0; x--) {
            bp.getPixels(pixs, 0, 1, x, 0, 1, HEIGHT);
            isStop = false;
            for (int pix : pixs) {
                if (pix != Color.TRANSPARENT) {
                    right = x;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        if (blank < 0) {
            blank = 0;
        }
        left = left - blank > 0 ? left - blank : 0;
        top = top - blank > 0 ? top - blank : 0;
        right = right + blank > WIDTH - 1 ? WIDTH - 1 : right + blank;
        bottom = bottom + blank > HEIGHT - 1 ? HEIGHT - 1 : bottom + blank;
        return Bitmap.createBitmap(bp, left, top, right - left, bottom - top);
    }

    /**
     * 保存到本地
     *
     * @param photo
     */
    private void scanMediaFile(File photo) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photo);
        mediaScanIntent.setData(contentUri);
        getContext().sendBroadcast(mediaScanIntent);
    }

}
