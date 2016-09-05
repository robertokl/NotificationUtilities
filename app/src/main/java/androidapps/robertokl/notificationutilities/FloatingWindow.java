package androidapps.robertokl.notificationutilities;

import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by klein on 8/30/16.
 */
public class FloatingWindow extends Service {
    private WindowManager windowManager;
    private View utilitiesWindow;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(this, MainActivity.class);
            // TODO
            return;
        }

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        final WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        layoutParams.gravity = Gravity.CENTER | Gravity.CENTER;
        layoutParams.x = 0;
        layoutParams.y = 0;

        utilitiesWindow = LayoutInflater.from(this).inflate(R.layout.activity_utilities_window, null);
        UtilitiesWindow.onCreate(utilitiesWindow, this);
        windowManager.addView(utilitiesWindow, layoutParams);

        utilitiesWindow.setOnTouchListener(new View.OnTouchListener() {
            WindowManager.LayoutParams updatedParameters = layoutParams;
            double y;
            double pressedY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_OUTSIDE:
                        hide();
                        break;
                    case MotionEvent.ACTION_DOWN:
                        y = updatedParameters.y;
                        pressedY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        updatedParameters.y = (int) (y + (event.getRawY() - pressedY));
                        windowManager.updateViewLayout(v, updatedParameters);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onDestroy() {
        hide();
        super.onDestroy();
    }

    public void hide() {
        stopSelf();
        windowManager.removeView(utilitiesWindow);
    }

    public ClipboardManager getClipboardService() {
        return (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
    }
}
