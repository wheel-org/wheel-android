package org.wheel.expenses.util;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import org.wheel.expenses.R;

public class DrawableTinter {
    public static Drawable tintDrawable(int resourceID, Context context) {
        Drawable drawable = ContextCompat.getDrawable(context, resourceID);
        drawable.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary),
                PorterDuff.Mode.MULTIPLY);
        return drawable;
    }
}
