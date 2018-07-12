package cn.com.codeteenager.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import cn.com.codeteenager.wxrecordbutton.WXRecordButton;

public class MainActivity extends AppCompatActivity {
    private WXRecordButton wxRecordButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wxRecordButton = findViewById(R.id.wx_record_button);
        wxRecordButton.setMax(4000);
        wxRecordButton.setGestureListener(new WXRecordButton.GestureListener() {
            @Override
            public void onClick() {

            }

            @Override
            public void onLongPressStart() {

            }

            @Override
            public void onLongPressEnd() {
                Toast.makeText(MainActivity.this, "录制停止", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongPressForceOver() {
                Toast.makeText(MainActivity.this, "录制停止", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
