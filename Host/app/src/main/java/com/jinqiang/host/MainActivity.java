package com.jinqiang.host;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.didi.virtualapk.PluginManager;

public class MainActivity extends AppCompatActivity {
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView)findViewById(R.id.click);
        startService(new Intent(this, PluginService.class));

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PluginManager.getInstance(MainActivity.this).getLoadedPlugin("com.jinqiang.dalimap") == null) {
                    Toast.makeText(MainActivity.this, "插件未加载,请尝试重启APP", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent11 = new Intent();
                intent11.setClassName("com.jinqiang.dalimap","com.jinqiang.dalimap.MapMainActivity");
                startActivity(intent11);
            }
        });
    }

}
