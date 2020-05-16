// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.tv.micdisclosure;

import android.animation.PropertyValuesHolder;
import android.view.ViewGroup$LayoutParams;
import android.view.WindowManager$LayoutParams;
import android.view.ViewTreeObserver$OnGlobalLayoutListener;
import com.android.systemui.R$id;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.view.WindowManager;
import java.util.Iterator;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager$NameNotFoundException;
import android.animation.Animator$AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.Animator;
import android.animation.AnimatorSet;
import com.android.systemui.R$string;
import java.util.Collection;
import java.util.Arrays;
import com.android.systemui.R$array;
import java.util.LinkedList;
import android.util.ArraySet;
import android.widget.TextView;
import java.util.Queue;
import java.util.Set;
import android.content.Context;
import android.view.View;

public class AudioRecordingDisclosureBar implements OnAudioActivityStateChangeListener
{
    private final AudioActivityObserver[] mAudioActivityObservers;
    private View mBgRight;
    private final Context mContext;
    private final Set<String> mExemptPackages;
    private View mIcon;
    private View mIconContainerBg;
    private View mIconTextsContainer;
    private View mIndicatorView;
    private final Queue<String> mPendingNotificationPackages;
    private final Set<String> mSessionNotifiedPackages;
    private int mState;
    private TextView mTextView;
    private View mTextsContainers;
    
    public AudioRecordingDisclosureBar(final Context mContext) {
        this.mState = 0;
        this.mSessionNotifiedPackages = (Set<String>)new ArraySet();
        this.mPendingNotificationPackages = new LinkedList<String>();
        this.mContext = mContext;
        this.mExemptPackages = (Set<String>)new ArraySet((Collection)Arrays.asList(this.mContext.getResources().getStringArray(R$array.audio_recording_disclosure_exempt_apps)));
        this.mAudioActivityObservers = new AudioActivityObserver[] { new RecordAudioAppOpObserver(this.mContext, this), new MicrophoneForegroundServicesObserver(this.mContext, this) };
    }
    
    private void expand(String applicationLabel) {
        applicationLabel = this.getApplicationLabel(applicationLabel);
        this.mTextView.setText((CharSequence)this.mContext.getString(R$string.app_accessed_mic, new Object[] { applicationLabel }));
        final AnimatorSet set = new AnimatorSet();
        set.playTogether(new Animator[] { (Animator)ObjectAnimator.ofFloat((Object)this.mIconTextsContainer, View.TRANSLATION_X, new float[] { 0.0f }), (Animator)ObjectAnimator.ofFloat((Object)this.mIconContainerBg, View.ALPHA, new float[] { 1.0f }), (Animator)ObjectAnimator.ofFloat((Object)this.mTextsContainers, View.ALPHA, new float[] { 1.0f }), (Animator)ObjectAnimator.ofFloat((Object)this.mBgRight, View.ALPHA, new float[] { 1.0f }) });
        set.setDuration(600L);
        set.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                AudioRecordingDisclosureBar.this.onExpanded();
            }
        });
        set.start();
        this.mState = 5;
    }
    
    private String getApplicationLabel(final String s) {
        final PackageManager packageManager = this.mContext.getPackageManager();
        try {
            return packageManager.getApplicationLabel(packageManager.getApplicationInfo(s, 0)).toString();
        }
        catch (PackageManager$NameNotFoundException ex) {
            return s;
        }
    }
    
    private void hide() {
        final int width = this.mIndicatorView.getWidth();
        final int n = (int)this.mIconTextsContainer.getTranslationX();
        final AnimatorSet set = new AnimatorSet();
        set.playTogether(new Animator[] { (Animator)ObjectAnimator.ofFloat((Object)this.mIndicatorView, View.TRANSLATION_X, new float[] { (float)(width - n) }), (Animator)ObjectAnimator.ofFloat((Object)this.mIcon, View.ALPHA, new float[] { 0.0f }) });
        set.setDuration(600L);
        set.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                AudioRecordingDisclosureBar.this.onHidden();
            }
        });
        set.start();
        this.mState = 6;
    }
    
    private void hideIndicatorIfNeeded() {
        if (this.mState != 4) {
            return;
        }
        for (int i = this.mAudioActivityObservers.length - 1; i >= 0; --i) {
            final Iterator<String> iterator = this.mAudioActivityObservers[i].getActivePackages().iterator();
            while (iterator.hasNext()) {
                if (this.mExemptPackages.contains(iterator.next())) {
                    continue;
                }
                return;
            }
        }
        this.mSessionNotifiedPackages.clear();
        this.hide();
    }
    
    private void minimize() {
        final int width = this.mTextsContainers.getWidth();
        final AnimatorSet set = new AnimatorSet();
        set.playTogether(new Animator[] { (Animator)ObjectAnimator.ofFloat((Object)this.mIconTextsContainer, View.TRANSLATION_X, new float[] { (float)width }), (Animator)ObjectAnimator.ofFloat((Object)this.mIconContainerBg, View.ALPHA, new float[] { 0.0f }), (Animator)ObjectAnimator.ofFloat((Object)this.mTextsContainers, View.ALPHA, new float[] { 0.0f }), (Animator)ObjectAnimator.ofFloat((Object)this.mBgRight, View.ALPHA, new float[] { 0.0f }) });
        set.setDuration(600L);
        set.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                AudioRecordingDisclosureBar.this.onMinimized();
            }
        });
        set.start();
        this.mState = 3;
    }
    
    private void onExpanded() {
        this.mState = 2;
        this.mIndicatorView.postDelayed((Runnable)new _$$Lambda$AudioRecordingDisclosureBar$kBNc6kAUIQ_xJwAjIaXKEey1BxA(this), 3000L);
    }
    
    private void onHidden() {
        ((WindowManager)this.mContext.getSystemService("window")).removeView(this.mIndicatorView);
        this.mIndicatorView = null;
        this.mIconTextsContainer = null;
        this.mIconContainerBg = null;
        this.mIcon = null;
        this.mTextsContainers = null;
        this.mTextView = null;
        this.mBgRight = null;
        this.mState = 0;
        if (!this.mPendingNotificationPackages.isEmpty()) {
            this.show(this.mPendingNotificationPackages.poll());
        }
    }
    
    private void onMinimized() {
        this.mState = 4;
        if (!this.mPendingNotificationPackages.isEmpty()) {
            this.expand(this.mPendingNotificationPackages.poll());
        }
        else {
            this.hideIndicatorIfNeeded();
        }
    }
    
    private void show(String applicationLabel) {
        applicationLabel = this.getApplicationLabel(applicationLabel);
        final View inflate = LayoutInflater.from(this.mContext).inflate(R$layout.tv_audio_recording_indicator, (ViewGroup)null);
        this.mIndicatorView = inflate;
        final View viewById = inflate.findViewById(R$id.icon_texts_container);
        this.mIconTextsContainer = viewById;
        this.mIconContainerBg = viewById.findViewById(R$id.icon_container_bg);
        this.mIcon = this.mIconTextsContainer.findViewById(R$id.icon_mic);
        final View viewById2 = this.mIconTextsContainer.findViewById(R$id.texts_container);
        this.mTextsContainers = viewById2;
        this.mTextView = (TextView)viewById2.findViewById(R$id.text);
        this.mBgRight = this.mIndicatorView.findViewById(R$id.bg_right);
        this.mTextView.setText((CharSequence)this.mContext.getString(R$string.app_accessed_mic, new Object[] { applicationLabel }));
        this.mIndicatorView.setVisibility(4);
        this.mIndicatorView.getViewTreeObserver().addOnGlobalLayoutListener((ViewTreeObserver$OnGlobalLayoutListener)new ViewTreeObserver$OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                AudioRecordingDisclosureBar.this.mIndicatorView.getViewTreeObserver().removeOnGlobalLayoutListener((ViewTreeObserver$OnGlobalLayoutListener)this);
                final int width = AudioRecordingDisclosureBar.this.mIndicatorView.getWidth();
                final AnimatorSet set = new AnimatorSet();
                set.setDuration(600L);
                set.playTogether(new Animator[] { (Animator)ObjectAnimator.ofFloat((Object)AudioRecordingDisclosureBar.this.mIndicatorView, View.TRANSLATION_X, new float[] { (float)width, 0.0f }), (Animator)ObjectAnimator.ofFloat((Object)AudioRecordingDisclosureBar.this.mIndicatorView, View.ALPHA, new float[] { 0.0f, 1.0f }) });
                set.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                    public void onAnimationEnd(final Animator animator) {
                        AudioRecordingDisclosureBar.this.startPulsatingAnimation();
                        AudioRecordingDisclosureBar.this.onExpanded();
                    }
                    
                    public void onAnimationStart(final Animator animator, final boolean b) {
                        AudioRecordingDisclosureBar.this.mIndicatorView.setVisibility(0);
                    }
                });
                set.start();
            }
        });
        final WindowManager$LayoutParams windowManager$LayoutParams = new WindowManager$LayoutParams(-2, -2, 2006, 8, -3);
        windowManager$LayoutParams.gravity = 53;
        windowManager$LayoutParams.setTitle((CharSequence)"MicrophoneCaptureIndicator");
        windowManager$LayoutParams.packageName = this.mContext.getPackageName();
        ((WindowManager)this.mContext.getSystemService("window")).addView(this.mIndicatorView, (ViewGroup$LayoutParams)windowManager$LayoutParams);
        this.mState = 1;
    }
    
    private void showIndicatorForPackageIfNeeded(final String s) {
        if (!this.mSessionNotifiedPackages.add(s)) {
            return;
        }
        switch (this.mState) {
            case 4: {
                this.expand(s);
                break;
            }
            case 1:
            case 2:
            case 3:
            case 5:
            case 6: {
                this.mPendingNotificationPackages.add(s);
                break;
            }
            case 0: {
                this.show(s);
                break;
            }
        }
    }
    
    private void startPulsatingAnimation() {
        final ObjectAnimator ofPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder((Object)this.mIconTextsContainer.findViewById(R$id.pulsating_circle), new PropertyValuesHolder[] { PropertyValuesHolder.ofFloat(View.SCALE_X, new float[] { 1.25f }), PropertyValuesHolder.ofFloat(View.SCALE_Y, new float[] { 1.25f }) });
        ofPropertyValuesHolder.setDuration(1000L);
        ofPropertyValuesHolder.setRepeatCount(-1);
        ofPropertyValuesHolder.setRepeatMode(2);
        ofPropertyValuesHolder.start();
    }
    
    @Override
    public void onAudioActivityStateChange(final boolean b, final String s) {
        if (this.mExemptPackages.contains(s)) {
            return;
        }
        if (b) {
            this.showIndicatorForPackageIfNeeded(s);
        }
        else {
            this.hideIndicatorIfNeeded();
        }
    }
}
