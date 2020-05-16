// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.plugins;

import android.graphics.drawable.Drawable;
import android.content.Intent;
import com.android.systemui.plugins.annotations.ProvidesInterface;

@ProvidesInterface(version = 1)
public interface IntentButtonProvider extends Plugin
{
    public static final int VERSION = 1;
    
    IntentButton getIntentButton();
    
    public interface IntentButton
    {
        IconState getIcon();
        
        Intent getIntent();
        
        public static class IconState
        {
            public CharSequence contentDescription;
            public Drawable drawable;
            public boolean isVisible;
            public boolean tint;
            
            public IconState() {
                this.isVisible = true;
                this.contentDescription = null;
                this.tint = true;
            }
        }
    }
}
