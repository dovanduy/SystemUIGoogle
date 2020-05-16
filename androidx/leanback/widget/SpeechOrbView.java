// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import androidx.leanback.R$drawable;
import androidx.leanback.R$layout;
import android.content.res.Resources;
import androidx.leanback.R$color;
import androidx.leanback.R$fraction;
import android.util.AttributeSet;
import android.content.Context;

public class SpeechOrbView extends SearchOrbView
{
    private int mCurrentLevel;
    private boolean mListening;
    private Colors mListeningOrbColors;
    private Colors mNotListeningOrbColors;
    private final float mSoundLevelMaxZoom;
    
    public SpeechOrbView(final Context context) {
        this(context, null);
    }
    
    public SpeechOrbView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public SpeechOrbView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mCurrentLevel = 0;
        this.mListening = false;
        final Resources resources = context.getResources();
        this.mSoundLevelMaxZoom = resources.getFraction(R$fraction.lb_search_bar_speech_orb_max_level_zoom, 1, 1);
        this.mNotListeningOrbColors = new Colors(resources.getColor(R$color.lb_speech_orb_not_recording), resources.getColor(R$color.lb_speech_orb_not_recording_pulsed), resources.getColor(R$color.lb_speech_orb_not_recording_icon));
        this.mListeningOrbColors = new Colors(resources.getColor(R$color.lb_speech_orb_recording), resources.getColor(R$color.lb_speech_orb_recording), 0);
        this.showNotListening();
    }
    
    @Override
    int getLayoutResourceId() {
        return R$layout.lb_speech_orb;
    }
    
    public void setSoundLevel(final int n) {
        if (!this.mListening) {
            return;
        }
        final int mCurrentLevel = this.mCurrentLevel;
        if (n > mCurrentLevel) {
            this.mCurrentLevel = mCurrentLevel + (n - mCurrentLevel) / 2;
        }
        else {
            this.mCurrentLevel = (int)(mCurrentLevel * 0.7f);
        }
        this.scaleOrbViewOnly((this.mSoundLevelMaxZoom - this.getFocusedZoom()) * this.mCurrentLevel / 100.0f + 1.0f);
    }
    
    public void showListening() {
        this.setOrbColors(this.mListeningOrbColors);
        this.setOrbIcon(this.getResources().getDrawable(R$drawable.lb_ic_search_mic));
        this.animateOnFocus(true);
        this.enableOrbColorAnimation(false);
        this.scaleOrbViewOnly(1.0f);
        this.mCurrentLevel = 0;
        this.mListening = true;
    }
    
    public void showNotListening() {
        this.setOrbColors(this.mNotListeningOrbColors);
        this.setOrbIcon(this.getResources().getDrawable(R$drawable.lb_ic_search_mic_out));
        this.animateOnFocus(this.hasFocus());
        this.scaleOrbViewOnly(1.0f);
        this.mListening = false;
    }
}
