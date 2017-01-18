package linc.ps.tools.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import linc.ps.tools.R;
import linc.ps.tools.sound.AudioUtils;
import linc.ps.tools.sound.SoundPoolUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_one;
    private LinearLayout activity_main;
    private Button btn_two;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        btn_one = (Button) findViewById(R.id.btn_one);
        activity_main = (LinearLayout) findViewById(R.id.activity_main);

        btn_one.setOnClickListener(this);
        btn_two = (Button) findViewById(R.id.btn_two);
        btn_two.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_one:
                AudioUtils.getInstance().speakText("十七点整");
                break;
            case R.id.btn_two:
                SoundPoolUtils.getInstance().play(1,0);
                break;
        }
    }
}
