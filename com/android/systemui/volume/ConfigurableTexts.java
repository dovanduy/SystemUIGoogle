// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.volume;

import android.content.res.Resources;
import android.view.View;
import android.view.View$OnAttachStateChangeListener;
import android.content.res.Resources$NotFoundException;
import com.android.settingslib.volume.Util;
import android.widget.TextView;
import android.util.ArrayMap;
import android.content.Context;

public class ConfigurableTexts
{
    private final Context mContext;
    private final ArrayMap<TextView, Integer> mTextLabels;
    private final ArrayMap<TextView, Integer> mTexts;
    private final Runnable mUpdateAll;
    
    public ConfigurableTexts(final Context mContext) {
        this.mTexts = (ArrayMap<TextView, Integer>)new ArrayMap();
        this.mTextLabels = (ArrayMap<TextView, Integer>)new ArrayMap();
        this.mUpdateAll = new Runnable() {
            @Override
            public void run() {
                final int n = 0;
                int n2 = 0;
                int i;
                while (true) {
                    i = n;
                    if (n2 >= ConfigurableTexts.this.mTexts.size()) {
                        break;
                    }
                    final ConfigurableTexts this$0 = ConfigurableTexts.this;
                    this$0.setTextSizeH((TextView)this$0.mTexts.keyAt(n2), (int)ConfigurableTexts.this.mTexts.valueAt(n2));
                    ++n2;
                }
                while (i < ConfigurableTexts.this.mTextLabels.size()) {
                    final ConfigurableTexts this$2 = ConfigurableTexts.this;
                    this$2.setTextLabelH((TextView)this$2.mTextLabels.keyAt(i), (int)ConfigurableTexts.this.mTextLabels.valueAt(i));
                    ++i;
                }
            }
        };
        this.mContext = mContext;
    }
    
    private void setTextLabelH(final TextView textView, final int n) {
        if (n < 0) {
            return;
        }
        try {
            Util.setText(textView, this.mContext.getString(n));
        }
        catch (Resources$NotFoundException ex) {}
    }
    
    private void setTextSizeH(final TextView textView, final int n) {
        textView.setTextSize(2, (float)n);
    }
    
    public int add(final TextView textView) {
        return this.add(textView, -1);
    }
    
    public int add(final TextView textView, final int i) {
        if (textView == null) {
            return 0;
        }
        if (this.mTexts.containsKey((Object)textView)) {
            return (int)this.mTexts.get((Object)textView);
        }
        final Resources resources = this.mContext.getResources();
        final int j = (int)(textView.getTextSize() / resources.getConfiguration().fontScale / resources.getDisplayMetrics().density);
        this.mTexts.put((Object)textView, (Object)j);
        textView.addOnAttachStateChangeListener((View$OnAttachStateChangeListener)new View$OnAttachStateChangeListener() {
            public void onViewAttachedToWindow(final View view) {
                ConfigurableTexts.this.setTextSizeH(textView, j);
            }
            
            public void onViewDetachedFromWindow(final View view) {
            }
        });
        this.mTextLabels.put((Object)textView, (Object)i);
        return j;
    }
    
    public void remove(final TextView textView) {
        this.mTexts.remove((Object)textView);
        this.mTextLabels.remove((Object)textView);
    }
    
    public void update() {
        if (this.mTexts.isEmpty()) {
            return;
        }
        ((TextView)this.mTexts.keyAt(0)).post(this.mUpdateAll);
    }
}
