package linc.ps.tools.image.signature;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

import linc.ps.tools.R;
import linc.ps.tools.image.util.ScreenUtils;

/**
 * Created by Frank on 2017/5/18.
 */
public class SignaturePopupWindow extends PopupWindow {

    private CalligraphyView signature_draw;// 签名的view
    private TextView tv_hint;// 默认显示
    private Button sign_clean;// 清空
    private Button sign_confirm;// 确认

    public static String signaturePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;

    public SignaturePopupWindow(Context context, String image_name) {
        signaturePath = signaturePath + image_name;

        LayoutInflater lay = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = lay.inflate(R.layout.popupwindow_signature, null);
        // 设置边框
        v.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.popupwindow_signature_corners));

        // 初始化按钮
        sign_clean = (Button) v.findViewById(R.id.sign_clean);
        sign_clean.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                signature_draw.clear();
            }
        });
        sign_confirm = (Button) v.findViewById(R.id.sign_confirm);
        sign_confirm.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    signature_draw.save(signaturePath);
                    signature_draw.clear();
                    SignaturePopupWindow.this.dismiss();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        tv_hint = (TextView) v.findViewById(R.id.tv_hint);
        signature_draw = (CalligraphyView) v.findViewById(R.id.signature_draw);
        signature_draw.setOnSignatureReadyListener(new CalligraphyView.SignatureReadyListener() {
            @Override
            public void onSignatureReady(boolean ready) {
                if (!ready) {
                    tv_hint.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onStartSigningSignature(boolean startSigning) {
                tv_hint.setVisibility(View.GONE);
            }
        });
        Log.e("signature", "ScreenUtils.getScreenWidth(context)==" + ScreenUtils.getScreenWidth(context));
        Log.e("signature", "ScreenUtils.getScreenHeight(context)==" + ScreenUtils.getScreenHeight(context));
        int width, heigth;
        if (Const.width > Const.height) {
            width = (int) (ScreenUtils.getScreenWidth(context) * Const.window_scale);
            heigth = (int) (width / Const.scale);
            double d = (double) width / (double) Const.width;
            Log.e("signature", "d==" + d);
            Const.pop_scale = (int) Math.ceil(d);
        } else {
            heigth = (int) (ScreenUtils.getScreenHeight(context) * Const.window_scale);
            width = (int) (heigth * Const.scale);
            double d = (double) width / (double) Const.width;
            Log.e("signature", "d==" + d);
            Const.pop_scale = (int) Math.ceil(d);
        }
        Log.e("signature", "width==" + width + "  heigth==" + heigth);

        Log.e("signature", "Const.pop_scale==" + Const.pop_scale);

        int button_heigth = ScreenUtils.dip2px(context, 40);
        Log.e("signature", "button_heigth==" + button_heigth);

        this.setContentView(v); // 设置SignaturePopupWindow的View
        this.setWidth(width); // 设置SignaturePopupWindow弹出窗体的宽
        this.setHeight(heigth + button_heigth); // 设置SignaturePopupWindow弹出窗体的高
        this.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.popupwindow_signature_corners));// 设置整个popupwindow的样式。
        this.setFocusable(false); // 设置SignaturePopupWindow弹出窗体可点击
        this.setOutsideTouchable(false);
        this.update(); // 刷新状态
    }

    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            this.showAtLocation(parent, Gravity.CENTER, 0, 0);
        } else {
            this.dismiss();
        }
    }

}
