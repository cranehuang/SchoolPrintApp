package com.xiaoyintong.app.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

public class CustomDialog extends Dialog { 
    private static int default_width = 160; //默认宽度
    private static int default_height = 120;//默认高度
    public CustomDialog(Context context, int layout, int style) { 
        this(context, default_width, default_height, layout, style); 
    }
    public CustomDialog(Context context, int width, int height, int layout, int style) {
        super(context, style);
        //set content
       setContentView(layout);
        //set window params
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        //set width,height by density and gravity
        float density = getDensity(context);
        params.width = (int) (width*density);
        params.height = (int) (height*density);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }
    private float getDensity(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
       return dm.density;
    }
}
