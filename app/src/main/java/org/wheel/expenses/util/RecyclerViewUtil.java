package org.wheel.expenses.util;

import android.support.v7.widget.RecyclerView;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

public class RecyclerViewUtil {
    public static void Setup(RecyclerView rv) {
        rv.setItemAnimator(new SlideInLeftAnimator());
        rv.getItemAnimator().setAddDuration(500);
        rv.getItemAnimator().setRemoveDuration(500);
        rv.getItemAnimator().setMoveDuration(500);
        rv.getItemAnimator().setChangeDuration(500);
    }
}
