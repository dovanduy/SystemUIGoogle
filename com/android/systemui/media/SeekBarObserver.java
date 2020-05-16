// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.media;

import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;
import android.content.res.ColorStateList;
import com.android.systemui.R$id;
import kotlin.jvm.internal.Intrinsics;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.lifecycle.Observer;

public final class SeekBarObserver implements Observer<SeekBarViewModel.Progress>
{
    private final TextView elapsedTimeView;
    private final SeekBar seekBarView;
    private final TextView totalTimeView;
    
    public SeekBarObserver(View viewById) {
        Intrinsics.checkParameterIsNotNull(viewById, "view");
        final View viewById2 = viewById.findViewById(R$id.media_progress_bar);
        Intrinsics.checkExpressionValueIsNotNull(viewById2, "view.findViewById(R.id.media_progress_bar)");
        this.seekBarView = (SeekBar)viewById2;
        final View viewById3 = viewById.findViewById(R$id.media_elapsed_time);
        Intrinsics.checkExpressionValueIsNotNull(viewById3, "view.findViewById(R.id.media_elapsed_time)");
        this.elapsedTimeView = (TextView)viewById3;
        viewById = viewById.findViewById(R$id.media_total_time);
        Intrinsics.checkExpressionValueIsNotNull(viewById, "view.findViewById(R.id.media_total_time)");
        this.totalTimeView = (TextView)viewById;
    }
    
    @Override
    public void onChanged(final SeekBarViewModel.Progress progress) {
        Intrinsics.checkParameterIsNotNull(progress, "data");
        final Integer color = progress.getColor();
        if (color != null) {
            final int intValue = color.intValue();
            final ColorStateList value = ColorStateList.valueOf(intValue);
            this.seekBarView.setThumbTintList(value);
            final ColorStateList withAlpha = value.withAlpha(192);
            this.seekBarView.setProgressTintList(withAlpha);
            this.seekBarView.setProgressBackgroundTintList(withAlpha.withAlpha(128));
            this.elapsedTimeView.setTextColor(intValue);
            this.totalTimeView.setTextColor(intValue);
        }
        final boolean enabled = progress.getEnabled();
        int alpha = 0;
        if (!enabled) {
            this.seekBarView.setEnabled(false);
            this.seekBarView.getThumb().setAlpha(0);
            this.elapsedTimeView.setText((CharSequence)"");
            this.totalTimeView.setText((CharSequence)"");
            return;
        }
        final Drawable thumb = this.seekBarView.getThumb();
        if (progress.getSeekAvailable()) {
            alpha = 255;
        }
        thumb.setAlpha(alpha);
        this.seekBarView.setEnabled(progress.getSeekAvailable());
        final Integer elapsedTime = progress.getElapsedTime();
        if (elapsedTime != null) {
            final int intValue2 = elapsedTime.intValue();
            this.seekBarView.setProgress(intValue2);
            this.elapsedTimeView.setText((CharSequence)DateUtils.formatElapsedTime(intValue2 / 1000L));
        }
        final Integer duration = progress.getDuration();
        if (duration != null) {
            final int intValue3 = duration.intValue();
            this.seekBarView.setMax(intValue3);
            this.totalTimeView.setText((CharSequence)DateUtils.formatElapsedTime(intValue3 / 1000L));
        }
    }
}
