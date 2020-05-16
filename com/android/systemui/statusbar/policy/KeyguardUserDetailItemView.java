// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import com.android.systemui.R$dimen;
import android.util.AttributeSet;
import android.content.Context;
import com.android.systemui.qs.tiles.UserDetailItemView;

public class KeyguardUserDetailItemView extends UserDetailItemView
{
    public KeyguardUserDetailItemView(final Context context) {
        this(context, null);
    }
    
    public KeyguardUserDetailItemView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public KeyguardUserDetailItemView(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public KeyguardUserDetailItemView(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
    }
    
    @Override
    protected int getFontSizeDimen() {
        return R$dimen.kg_user_switcher_text_size;
    }
}
