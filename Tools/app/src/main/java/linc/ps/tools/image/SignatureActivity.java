package linc.ps.tools.image;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import java.io.File;

import linc.ps.tools.R;
import linc.ps.tools.image.signature.SignaturePopupWindow;

/**
 * Created by Frank on 2017/5/18.
 * 签名类使用示例
 */
public class SignatureActivity extends Activity {
    private Button btn_show;
    private SignaturePopupWindow sPopupWindow;

    public static String signaturePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "signature2.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);

        sPopupWindow = new SignaturePopupWindow(this, "signature2.png");

        btn_show = (Button) findViewById(R.id.activity_signature_btn_show);
        btn_show.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sPopupWindow.showPopupWindow(v);
            }
        });

    }
}