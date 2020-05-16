// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.stackdivider;

import android.app.ActivityManager$TaskDescription;
import android.view.MotionEvent;
import android.view.View;
import android.view.KeyEvent;
import com.android.systemui.R$string;
import android.widget.TextView;
import com.android.systemui.R$layout;
import android.os.Bundle;
import com.android.systemui.R$anim;
import android.view.View$OnTouchListener;
import android.app.Activity;

public class ForcedResizableInfoActivity extends Activity implements View$OnTouchListener
{
    private final Runnable mFinishRunnable;
    
    public ForcedResizableInfoActivity() {
        this.mFinishRunnable = new Runnable() {
            @Override
            public void run() {
                ForcedResizableInfoActivity.this.finish();
            }
        };
    }
    
    public void finish() {
        super.finish();
        this.overridePendingTransition(0, R$anim.forced_resizable_exit);
    }
    
    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.setContentView(R$layout.forced_resizable_activity);
        final TextView textView = (TextView)this.findViewById(16908299);
        final int intExtra = this.getIntent().getIntExtra("extra_forced_resizeable_reason", -1);
        String s;
        if (intExtra != 1) {
            if (intExtra != 2) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Unexpected forced resizeable reason: ");
                sb.append(intExtra);
                throw new IllegalArgumentException(sb.toString());
            }
            s = this.getString(R$string.forced_resizable_secondary_display);
        }
        else {
            s = this.getString(R$string.dock_forced_resizable);
        }
        textView.setText((CharSequence)s);
        this.getWindow().setTitle((CharSequence)s);
        this.getWindow().getDecorView().setOnTouchListener((View$OnTouchListener)this);
    }
    
    public boolean onKeyDown(final int n, final KeyEvent keyEvent) {
        this.finish();
        return true;
    }
    
    protected void onStart() {
        super.onStart();
        this.getWindow().getDecorView().postDelayed(this.mFinishRunnable, 2500L);
    }
    
    protected void onStop() {
        super.onStop();
        this.finish();
    }
    
    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        this.finish();
        return true;
    }
    
    public void setTaskDescription(final ActivityManager$TaskDescription activityManager$TaskDescription) {
    }
}
