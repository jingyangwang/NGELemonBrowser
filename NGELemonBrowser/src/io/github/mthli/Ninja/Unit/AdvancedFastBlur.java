package io.github.mthli.Ninja.Unit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by changqing on 15/9/7.
 */
public class AdvancedFastBlur {

    public static Bitmap blur(Context context, Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        float radius = 6;
        float scaleFactor = 8;

        Bitmap overlay = Bitmap.createBitmap((int) (bitmap.getWidth() / scaleFactor), (int) (bitmap.getHeight() / scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(0, 0);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return FastBlur.doBlur(overlay, (int) radius, true);
    }
}
