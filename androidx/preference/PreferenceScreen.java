// 
// Decompiled by Procyon v0.5.36
// 

package androidx.preference;

import androidx.core.content.res.TypedArrayUtils;
import android.util.AttributeSet;
import android.content.Context;

public final class PreferenceScreen extends PreferenceGroup
{
    private boolean mShouldUseGeneratedIds;
    
    public PreferenceScreen(final Context context, final AttributeSet set) {
        super(context, set, TypedArrayUtils.getAttr(context, R$attr.preferenceScreenStyle, 16842891));
        this.mShouldUseGeneratedIds = true;
    }
    
    @Override
    protected boolean isOnSameScreenAsChildren() {
        return false;
    }
    
    @Override
    protected void onClick() {
        if (this.getIntent() == null && this.getFragment() == null) {
            if (this.getPreferenceCount() != 0) {
                final PreferenceManager.OnNavigateToScreenListener onNavigateToScreenListener = this.getPreferenceManager().getOnNavigateToScreenListener();
                if (onNavigateToScreenListener != null) {
                    onNavigateToScreenListener.onNavigateToScreen(this);
                }
            }
        }
    }
    
    public boolean shouldUseGeneratedIds() {
        return this.mShouldUseGeneratedIds;
    }
}
