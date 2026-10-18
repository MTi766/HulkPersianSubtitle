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
        if (activeSubtitle == null) {
            if (!Settings.canDrawOverlays(this)) {
                android.widget.Toast.makeText(
                    this,
                    "مجوز نمایش روی برنامه‌های دیگر را فعال کنید",
                    android.widget.Toast.LENGTH_LONG
                ).show();
                return;
            }
            showSubtitle();
        }

        if (activeSubtitle != null) {
            activeSubtitle.setText(text);
        }
    });
}

private void startServer() {
    new Thread(() -> {
        try (ServerSocket server = new ServerSocket(5050)) {
            runOnUiThread(() -> {
    android.widget.Toast.makeText(
        MainActivity.this,
        "SERVER STARTED: 5050",
        android.widget.Toast.LENGTH_LONG
    ).show();
}
);while (true) {
                try (Socket client = server.accept();
                     BufferedReader reader = new BufferedReader(
                         new InputStreamReader(
                             client.getInputStream(),
                             StandardCharsets.UTF_8));
                     OutputStream out = client.getOutputStream()) {

                    String request = reader.readLine();
                    if (request == null) continue;

                    int contentLength = 0;
                    String line;

                    while ((line = reader.readLine()) != null
                            && !line.isEmpty()) {
                        if (line.toLowerCase().startsWith(
                                "content-length:")) {
                            contentLength = Integer.parseInt(
                                line.substring(line.indexOf(':') + 1)
                                    .trim());
                        }
                    }

                    String data = "";
                    if (request.startsWith("POST ")) {
                        StringBuilder body = new StringBuilder();
                        for (int i = 0; i < contentLength; i++) {
                            int ch = reader.read();
                            if (ch == -1) break;
                            body.append((char) ch);
                        }
                        data = body.toString();
                    } else {
                        String[] parts = request.split(" ");
                        if (parts.length > 1
                                && parts[1].contains("?")) {
                            data = parts[1].substring(
                                parts[1].indexOf('?') + 1);
                        }
                    }

                    String text = "";
                    for (String item : data.split("&")) {
                        if (item.startsWith("text=")) {
                            text = URLDecoder.decode(
                                item.substring(5), "UTF-8");
                            break;
                        }
                    }

                    if (!text.trim().isEmpty()) {
                        updateSubtitle(text);
                    }

                    String html =
                        "<!doctype html><html><meta charset='UTF-8'>"
                        + "<meta name='viewport' content='width=device-width,"
                        + " initial-scale=1'><body dir='rtl'>"
                        + "<h3>زیرنویس فارسی هالک</h3>"
                        + "<form method='POST'>"
                        + "<textarea name='text' rows='4' "
                        + "style='width:95%;font-size:20px' "
                        + "placeholder='متن فارسی را بنویس'></textarea>"
                        + "<br><button style='font-size:20px'>"
                        + "ارسال به تلویزیون</button></form>"
                        + "<p>متن ارسال شد؟ تصویر تلویزیون را بررسی کن.</p>"
                        + "</body></html>";

                    byte[] bytes = html.getBytes(
                        StandardCharsets.UTF_8);

                    String headers =
                        "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: text/html; charset=UTF-8\r\n"
                        + "Content-Length: " + bytes.length + "\r\n"
                        + "Connection: close\r\n\r\n";

                    out.write(headers.getBytes(
                        StandardCharsets.US_ASCII));
                    out.write(bytes);
                    out.flush();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
    e.printStackTrace();
    runOnUiThread(() -> {
        android.widget.Toast.makeText(
            MainActivity.this,
            "SERVER ERROR: " + e.toString(),
            android.widget.Toast.LENGTH_LONG
        ).show();
    });
}
    }).start();
}
}