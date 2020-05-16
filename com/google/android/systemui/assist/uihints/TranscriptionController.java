// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.text.TextUtils;
import java.util.Iterator;
import android.view.View$OnTouchListener;
import android.view.View$OnClickListener;
import com.android.systemui.R$id;
import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import android.app.PendingIntent$CanceledException;
import android.util.Log;
import android.view.View;
import android.os.Bundle;
import java.util.List;
import android.graphics.Rect;
import android.graphics.Region;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;
import android.view.ViewGroup;
import android.app.PendingIntent;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.android.systemui.assist.uihints.input.TouchInsideRegion;
import com.google.android.systemui.assist.uihints.input.TouchActionRegion;
import com.android.systemui.statusbar.policy.ConfigurationController;

public class TranscriptionController implements CardInfoListener, TranscriptionInfoListener, GreetingInfoListener, ChipsInfoListener, ClearListener, ConfigurationListener, TouchActionRegion, TouchInsideRegion
{
    private State mCurrentState;
    private final TouchInsideHandler mDefaultOnTap;
    private final FlingVelocityWrapper mFlingVelocity;
    private boolean mHasAccurateBackground;
    private ListenableFuture<Void> mHideFuture;
    private TranscriptionSpaceListener mListener;
    private PendingIntent mOnGreetingTap;
    private PendingIntent mOnTranscriptionTap;
    private final ViewGroup mParent;
    private Runnable mQueuedCompletion;
    private State mQueuedState;
    private boolean mQueuedStateAnimates;
    private Map<State, TranscriptionSpaceView> mViewMap;
    
    TranscriptionController(final ViewGroup mParent, final TouchInsideHandler mDefaultOnTap, final FlingVelocityWrapper mFlingVelocity, final ConfigurationController configurationController) {
        this.mViewMap = new HashMap<State, TranscriptionSpaceView>();
        final State none = State.NONE;
        this.mCurrentState = none;
        this.mHasAccurateBackground = false;
        this.mQueuedStateAnimates = false;
        this.mQueuedState = none;
        this.mParent = mParent;
        this.mDefaultOnTap = mDefaultOnTap;
        this.mFlingVelocity = mFlingVelocity;
        this.setUpViews();
        configurationController.addCallback((ConfigurationController.ConfigurationListener)this);
    }
    
    private Optional<Region> getTouchRegion() {
        final TranscriptionSpaceView transcriptionSpaceView = this.mViewMap.get(this.mCurrentState);
        if (transcriptionSpaceView == null) {
            return Optional.empty();
        }
        final Rect rect = new Rect();
        transcriptionSpaceView.getHitRect(rect);
        return Optional.of(new Region(rect));
    }
    
    private boolean hasTapAction() {
        final int n = TranscriptionController$1.$SwitchMap$com$google$android$systemui$assist$uihints$TranscriptionController$State[this.mCurrentState.ordinal()];
        final boolean b = false;
        boolean b2 = false;
        if (n == 1) {
            boolean b3 = b;
            if (this.mOnTranscriptionTap != null) {
                b3 = true;
            }
            return b3;
        }
        if (n != 2) {
            return n == 3;
        }
        if (this.mOnGreetingTap != null) {
            b2 = true;
        }
        return b2;
    }
    
    private void maybeSetState() {
        final State mCurrentState = this.mCurrentState;
        final State mQueuedState = this.mQueuedState;
        if (mCurrentState == mQueuedState) {
            final Runnable mQueuedCompletion = this.mQueuedCompletion;
            if (mQueuedCompletion != null) {
                mQueuedCompletion.run();
            }
            return;
        }
        if (!this.mHasAccurateBackground && mQueuedState != State.NONE) {
            return;
        }
        final ListenableFuture<Void> mHideFuture = this.mHideFuture;
        if (mHideFuture != null && !mHideFuture.isDone()) {
            return;
        }
        this.updateListener(this.mCurrentState, this.mQueuedState);
        if (State.NONE.equals(this.mCurrentState)) {
            this.mCurrentState = this.mQueuedState;
            final Runnable mQueuedCompletion2 = this.mQueuedCompletion;
            if (mQueuedCompletion2 != null) {
                mQueuedCompletion2.run();
            }
        }
        else {
            Futures.transform(this.mHideFuture = this.mViewMap.get(this.mCurrentState).hide(this.mQueuedStateAnimates), (Function<? super Void, ?>)new _$$Lambda$TranscriptionController$KP3rlvpiNKikrYxgZ_KAznCG_Sg(this), MoreExecutors.directExecutor());
        }
    }
    
    private void setQueuedState(final State mQueuedState, final boolean mQueuedStateAnimates, final Runnable mQueuedCompletion) {
        this.mQueuedState = mQueuedState;
        this.mQueuedStateAnimates = mQueuedStateAnimates;
        this.mQueuedCompletion = mQueuedCompletion;
    }
    
    private void setUpViews() {
        this.mViewMap = new HashMap<State, TranscriptionSpaceView>();
        final TranscriptionView transcriptionView = (TranscriptionView)this.mParent.findViewById(R$id.transcription);
        transcriptionView.setOnClickListener((View$OnClickListener)new _$$Lambda$TranscriptionController$0HYbuLdNbhl5gctqUMc9bywkpQ4(this));
        transcriptionView.setOnTouchListener((View$OnTouchListener)this.mDefaultOnTap);
        this.mViewMap.put(State.TRANSCRIPTION, (TranscriptionSpaceView)transcriptionView);
        final GreetingView greetingView = (GreetingView)this.mParent.findViewById(R$id.greeting);
        greetingView.setOnClickListener((View$OnClickListener)new _$$Lambda$TranscriptionController$h9qLIH9VBz86NiKHZhwB3DSlwns(this));
        greetingView.setOnTouchListener((View$OnTouchListener)this.mDefaultOnTap);
        this.mViewMap.put(State.GREETING, (TranscriptionSpaceView)greetingView);
        this.mViewMap.put(State.CHIPS, (TranscriptionSpaceView)this.mParent.findViewById(R$id.chips));
    }
    
    private void updateListener(final State state, final State state2) {
        final TranscriptionSpaceListener mListener = this.mListener;
        if (mListener != null) {
            mListener.onStateChanged(state, state2);
        }
    }
    
    @Override
    public Optional<Region> getTouchActionRegion() {
        Optional<Region> optional;
        if (this.hasTapAction()) {
            optional = this.getTouchRegion();
        }
        else {
            optional = Optional.empty();
        }
        return optional;
    }
    
    @Override
    public Optional<Region> getTouchInsideRegion() {
        Optional<Region> optional;
        if (this.hasTapAction()) {
            optional = Optional.empty();
        }
        else {
            optional = this.getTouchRegion();
        }
        return optional;
    }
    
    @Override
    public void onCardInfo(final boolean cardVisible, final int n, final boolean b, final boolean b2) {
        this.setCardVisible(cardVisible);
    }
    
    @Override
    public void onChipsInfo(final List<Bundle> list) {
        if (list != null && list.size() != 0) {
            final Optional<Float> consumeVelocity = this.mFlingVelocity.consumeVelocity();
            if (this.mCurrentState == State.NONE && consumeVelocity.isPresent()) {
                this.setQueuedState(State.CHIPS, false, new _$$Lambda$TranscriptionController$cv29P2HMbMTQn9IMOXR0PWH2ZSs(this, list, consumeVelocity));
            }
            else {
                final State mCurrentState = this.mCurrentState;
                if (mCurrentState != State.GREETING && mCurrentState != State.TRANSCRIPTION) {
                    this.setQueuedState(State.CHIPS, false, new _$$Lambda$TranscriptionController$j6WH0DCOjNclKqv26tfpcuFwBiw(this, list));
                }
                else {
                    this.setQueuedState(State.CHIPS, false, new _$$Lambda$TranscriptionController$C_lGCu3ECeXDDNmvs4Ou0eB7IHE(this, list));
                }
            }
            this.maybeSetState();
            return;
        }
        Log.e("TranscriptionController", "Null or empty chip list received; clearing transcription space");
        this.onClear(false);
    }
    
    @Override
    public void onClear(final boolean b) {
        this.setQueuedState(State.NONE, b, null);
        this.maybeSetState();
    }
    
    @Override
    public void onDensityOrFontScaleChanged() {
        final Iterator<TranscriptionSpaceView> iterator = this.mViewMap.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().onFontSizeChanged();
        }
    }
    
    @Override
    public void onGreetingInfo(final String s, final PendingIntent mOnGreetingTap) {
        if (!TextUtils.isEmpty((CharSequence)s)) {
            this.mOnGreetingTap = mOnGreetingTap;
            final Optional<Float> consumeVelocity = this.mFlingVelocity.consumeVelocity();
            if (this.mCurrentState == State.NONE && consumeVelocity.isPresent()) {
                this.setQueuedState(State.GREETING, false, new _$$Lambda$TranscriptionController$kpGnEWtIcplKHiQGCmOtu2mhuLw(this, s, consumeVelocity));
            }
            else {
                this.setQueuedState(State.GREETING, false, new _$$Lambda$TranscriptionController$4uJ3cqqTFUlD8qUYtC9zYqi1VzQ(this, s));
            }
            this.maybeSetState();
        }
    }
    
    @Override
    public void onTranscriptionInfo(final String s, final PendingIntent pendingIntent, final int transcriptionColor) {
        this.setTranscription(s, pendingIntent);
        this.setTranscriptionColor(transcriptionColor);
    }
    
    public void setCardVisible(final boolean cardVisible) {
        final Iterator<TranscriptionSpaceView> iterator = this.mViewMap.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().setCardVisible(cardVisible);
        }
    }
    
    public void setHasAccurateBackground(final boolean mHasAccurateBackground) {
        if (this.mHasAccurateBackground != mHasAccurateBackground && (this.mHasAccurateBackground = mHasAccurateBackground)) {
            this.maybeSetState();
        }
    }
    
    public void setHasDarkBackground(final boolean hasDarkBackground) {
        final Iterator<TranscriptionSpaceView> iterator = this.mViewMap.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().setHasDarkBackground(hasDarkBackground);
        }
    }
    
    public void setListener(final TranscriptionSpaceListener mListener) {
        this.mListener = mListener;
        if (mListener != null) {
            mListener.onStateChanged(null, this.mCurrentState);
        }
    }
    
    public void setTranscription(final String s, final PendingIntent mOnTranscriptionTap) {
        this.mOnTranscriptionTap = mOnTranscriptionTap;
        this.setQueuedState(State.TRANSCRIPTION, false, new _$$Lambda$TranscriptionController$QHuNzzppBiu7sa8Sf9qG6b6CQOw(this, s));
        this.maybeSetState();
    }
    
    public void setTranscriptionColor(final int transcriptionColor) {
        ((TranscriptionView)this.mViewMap.get(State.TRANSCRIPTION)).setTranscriptionColor(transcriptionColor);
    }
    
    public enum State
    {
        CHIPS, 
        GREETING, 
        NONE, 
        TRANSCRIPTION;
    }
    
    public interface TranscriptionSpaceListener
    {
        void onStateChanged(final State p0, final State p1);
    }
    
    interface TranscriptionSpaceView
    {
        void getHitRect(final Rect p0);
        
        ListenableFuture<Void> hide(final boolean p0);
        
        void onFontSizeChanged();
        
        default void setCardVisible(final boolean b) {
        }
        
        void setHasDarkBackground(final boolean p0);
    }
}
