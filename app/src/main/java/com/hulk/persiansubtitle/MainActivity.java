package com.hulk.persiansubtitle;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.graphics.Color;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
public class MainActivity extends Activity {
private TextView activeSubtitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 50, 30, 30);

        TextView title = new TextView(this);
        title.setText("زیرنویس فارسی هالک");
        title.setTextSize(24);
        title.setGravity(Gravity.CENTER);

        Button button = new Button(this);
        button.setText("نمایش زیرنویس آزمایشی");

        layout.addView(title);
layout.addView(button);
setContentView(layout);
startServer();





        button.setOnClickListener(v -> {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName())
                );
                startActivity(intent);
            } else {
                showSubtitle();
            }
        });
    }

    private void showSubtitle() {
        WindowManager wm =
            (WindowManager) getSystemService(WINDOW_SERVICE);

        TextView subtitle = new TextView(this);
activeSubtitle = subtitle;
        subtitle.setText("سلام! این یک زیرنویس فارسی آزمایشی است.");
        subtitle.setTextSize(22);
        subtitle.setTextColor(Color.WHITE);
        subtitle.setBackgroundColor(0xCC000000);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(16, 12, 16, 12);

        WindowManager.LayoutParams params =
            new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                android.graphics.PixelFormat.TRANSLUCENT
            );

        params.gravity = Gravity.BOTTOM;
        params.y = 60;

        wm.addView(subtitle, params);

        
subtitle.setOnClickListener(v -> {
wm.removeView(subtitle);
activeSubtitle = null;
        });


        //finish();
    }
private void updateSubtitle(String text) {
    runOnUiThread(() -> {
        if (activeSubtitle != null) {
            activeSubtitle.setText(text);
        }
  });
}

private void startServer() {
    new Thread(() -> {
        try (ServerSocket server = new ServerSocket(5050)) {
            while (true) {
                try (
                    Socket client = server.accept();
                    BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                            client.getInputStream(),
                            StandardCharsets.UTF_8
                        )
                    )
                ) {
                    String text;
                    while ((text = reader.readLine()) != null) {
                        updateSubtitle(text);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }).start();
}

}