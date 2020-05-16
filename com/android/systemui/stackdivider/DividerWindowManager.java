// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.stackdivider;

import android.os.IBinder;
import android.view.ViewGroup$LayoutParams;
import android.os.Binder;
import android.view.View;
import com.android.systemui.wm.SystemWindows;
import android.view.WindowManager$LayoutParams;

public class DividerWindowManager
{
    private WindowManager$LayoutParams mLp;
    final SystemWindows mSystemWindows;
    private View mView;
    
    public DividerWindowManager(final SystemWindows mSystemWindows) {
        this.mSystemWindows = mSystemWindows;
    }
    
    public void add(final View mView, final int n, final int n2, final int n3) {
        final WindowManager$LayoutParams mLp = new WindowManager$LayoutParams(n, n2, 2034, 545521704, -3);
        this.mLp = mLp;
        mLp.token = (IBinder)new Binder();
        this.mLp.setTitle((CharSequence)"DockedStackDivider");
        final WindowManager$LayoutParams mLp2 = this.mLp;
        mLp2.privateFlags |= 0x40;
        mLp2.layoutInDisplayCutoutMode = 3;
        mView.setSystemUiVisibility(1792);
        this.mSystemWindows.addView(mView, this.mLp, n3, 2034);
        this.mView = mView;
    }
    
    public void remove() {
        final View mView = this.mView;
        if (mView != null) {
            this.mSystemWindows.removeView(mView);
        }
        this.mView = null;
    }
    
    public void setSlippery(final boolean b) {
        boolean b2 = true;
        Label_0074: {
            if (b) {
                final WindowManager$LayoutParams mLp = this.mLp;
                final int flags = mLp.flags;
                if ((flags & 0x20000000) == 0x0) {
                    mLp.flags = (flags | 0x20000000);
                    break Label_0074;
                }
            }
            if (!b) {
                final WindowManager$LayoutParams mLp2 = this.mLp;
                final int flags2 = mLp2.flags;
                if ((0x20000000 & flags2) != 0x0) {
                    mLp2.flags = (0xDFFFFFFF & flags2);
                    break Label_0074;
                }
            }
            b2 = false;
        }
        if (b2) {
            this.mSystemWindows.updateViewLayout(this.mView, (ViewGroup$LayoutParams)this.mLp);
        }
    }
    
    public void setTouchable(final boolean b) {
        if (this.mView == null) {
            return;
        }
        final boolean b2 = false;
        int n = 0;
    Label_0089:
        while (true) {
            Label_0048: {
                if (b) {
                    break Label_0048;
                }
                final WindowManager$LayoutParams mLp = this.mLp;
                final int flags = mLp.flags;
                if ((flags & 0x10) != 0x0) {
                    break Label_0048;
                }
                mLp.flags = (flags | 0x10);
                n = 1;
                break Label_0089;
            }
            n = (b2 ? 1 : 0);
            if (b) {
                final WindowManager$LayoutParams mLp2 = this.mLp;
                final int flags2 = mLp2.flags;
                n = (b2 ? 1 : 0);
                if ((flags2 & 0x10) != 0x0) {
                    mLp2.flags = (flags2 & 0xFFFFFFEF);
                    continue;
                }
            }
            break;
        }
        if (n != 0) {
            this.mSystemWindows.updateViewLayout(this.mView, (ViewGroup$LayoutParams)this.mLp);
        }
    }
}
