// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard.clock;

import android.graphics.Paint$Style;
import java.util.TimeZone;
import com.android.systemui.R$string;
import android.graphics.BitmapFactory;
import com.android.systemui.R$drawable;
import com.android.internal.colorextraction.ColorExtractor$GradientColors;
import android.graphics.Bitmap;
import com.android.systemui.R$id;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.view.View;
import android.widget.TextView;
import android.content.res.Resources;
import android.view.LayoutInflater;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.plugins.ClockPlugin;

public class DefaultClockController implements ClockPlugin
{
    private final SysuiColorExtractor mColorExtractor;
    private final LayoutInflater mLayoutInflater;
    private final ViewPreviewer mRenderer;
    private final Resources mResources;
    private TextView mTextDate;
    private TextView mTextTime;
    private View mView;
    
    public DefaultClockController(final Resources mResources, final LayoutInflater mLayoutInflater, final SysuiColorExtractor mColorExtractor) {
        this.mRenderer = new ViewPreviewer();
        this.mResources = mResources;
        this.mLayoutInflater = mLayoutInflater;
        this.mColorExtractor = mColorExtractor;
    }
    
    private void createViews() {
        final View inflate = this.mLayoutInflater.inflate(R$layout.default_clock_preview, (ViewGroup)null);
        this.mView = inflate;
        this.mTextTime = (TextView)inflate.findViewById(R$id.time);
        this.mTextDate = (TextView)this.mView.findViewById(R$id.date);
    }
    
    @Override
    public View getBigClockView() {
        if (this.mView == null) {
            this.createViews();
        }
        return this.mView;
    }
    
    @Override
    public String getName() {
        return "default";
    }
    
    @Override
    public int getPreferredY(final int n) {
        return n / 2;
    }
    
    @Override
    public Bitmap getPreview(final int n, final int n2) {
        final View bigClockView = this.getBigClockView();
        this.setDarkAmount(1.0f);
        this.setTextColor(-1);
        final ColorExtractor$GradientColors colors = this.mColorExtractor.getColors(2);
        this.setColorPalette(colors.supportsDarkText(), colors.getColorPalette());
        this.onTimeTick();
        return this.mRenderer.createPreview(bigClockView, n, n2);
    }
    
    @Override
    public Bitmap getThumbnail() {
        return BitmapFactory.decodeResource(this.mResources, R$drawable.default_thumbnail);
    }
    
    @Override
    public String getTitle() {
        return this.mResources.getString(R$string.clock_title_default);
    }
    
    @Override
    public View getView() {
        return null;
    }
    
    @Override
    public void onDestroyView() {
        this.mView = null;
        this.mTextTime = null;
        this.mTextDate = null;
    }
    
    @Override
    public void onTimeTick() {
    }
    
    @Override
    public void onTimeZoneChanged(final TimeZone timeZone) {
    }
    
    @Override
    public void setColorPalette(final boolean b, final int[] array) {
    }
    
    @Override
    public void setDarkAmount(final float n) {
    }
    
    @Override
    public void setStyle(final Paint$Style paint$Style) {
    }
    
    @Override
    public void setTextColor(final int n) {
        this.mTextTime.setTextColor(n);
        this.mTextDate.setTextColor(n);
    }
    
    @Override
    public boolean shouldShowStatusArea() {
        return true;
    }
}
