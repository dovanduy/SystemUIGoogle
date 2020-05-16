// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.view.MotionEvent;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.graphics.Bitmap;
import com.android.systemui.qs.tiles.UserDetailItemView;
import android.graphics.drawable.LayerDrawable;
import com.android.systemui.R$drawable;
import com.android.systemui.R$color;
import android.view.View$OnClickListener;
import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.view.View$OnLayoutChangeListener;
import android.graphics.drawable.Drawable;
import android.view.View;
import com.android.systemui.Interpolators;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.phone.NotificationPanelViewController;
import android.view.ViewStub;
import android.content.Context;
import android.view.ViewGroup;
import com.android.systemui.statusbar.phone.KeyguardStatusBarView;
import android.database.DataSetObserver;
import android.animation.ObjectAnimator;
import com.android.settingslib.animation.AppearAnimationUtils;

public class KeyguardUserSwitcher
{
    private final Adapter mAdapter;
    private boolean mAnimating;
    private final AppearAnimationUtils mAppearAnimationUtils;
    private final KeyguardUserSwitcherScrim mBackground;
    private ObjectAnimator mBgAnimator;
    public final DataSetObserver mDataSetObserver;
    private final KeyguardStatusBarView mStatusBarView;
    private ViewGroup mUserSwitcher;
    private final Container mUserSwitcherContainer;
    private UserSwitcherController mUserSwitcherController;
    
    public KeyguardUserSwitcher(final Context context, final ViewStub viewStub, final KeyguardStatusBarView mStatusBarView, final NotificationPanelViewController notificationPanelViewController) {
        this.mDataSetObserver = new DataSetObserver() {
            public void onChanged() {
                KeyguardUserSwitcher.this.refresh();
            }
        };
        final boolean boolean1 = context.getResources().getBoolean(17891477);
        final UserSwitcherController mUserSwitcherController = Dependency.get(UserSwitcherController.class);
        if (mUserSwitcherController != null && boolean1) {
            this.mUserSwitcherContainer = (Container)viewStub.inflate();
            this.mBackground = new KeyguardUserSwitcherScrim(context);
            this.reinflateViews();
            (this.mStatusBarView = mStatusBarView).setKeyguardUserSwitcher(this);
            notificationPanelViewController.setKeyguardUserSwitcher(this);
            (this.mAdapter = new Adapter(context, mUserSwitcherController, this)).registerDataSetObserver(this.mDataSetObserver);
            this.mUserSwitcherController = mUserSwitcherController;
            this.mAppearAnimationUtils = new AppearAnimationUtils(context, 400L, -0.5f, 0.5f, Interpolators.FAST_OUT_SLOW_IN);
            this.mUserSwitcherContainer.setKeyguardUserSwitcher(this);
        }
        else {
            this.mUserSwitcherContainer = null;
            this.mStatusBarView = null;
            this.mAdapter = null;
            this.mAppearAnimationUtils = null;
            this.mBackground = null;
        }
    }
    
    private void cancelAnimations() {
        for (int childCount = this.mUserSwitcher.getChildCount(), i = 0; i < childCount; ++i) {
            this.mUserSwitcher.getChildAt(i).animate().cancel();
        }
        final ObjectAnimator mBgAnimator = this.mBgAnimator;
        if (mBgAnimator != null) {
            mBgAnimator.cancel();
        }
        this.mUserSwitcher.animate().cancel();
        this.mAnimating = false;
    }
    
    private boolean hide(final boolean b) {
        if (this.mUserSwitcher != null && this.mUserSwitcherContainer.getVisibility() == 0) {
            this.cancelAnimations();
            if (b) {
                this.startDisappearAnimation();
            }
            else {
                this.mUserSwitcherContainer.setVisibility(8);
            }
            this.mStatusBarView.setKeyguardUserSwitcherShowing(false, b);
            return true;
        }
        return false;
    }
    
    private void refresh() {
        final int childCount = this.mUserSwitcher.getChildCount();
        final int count = ((UserSwitcherController.BaseUserAdapter)this.mAdapter).getCount();
        for (int max = Math.max(childCount, count), i = 0; i < max; ++i) {
            if (i < count) {
                View child = null;
                if (i < childCount) {
                    child = this.mUserSwitcher.getChildAt(i);
                }
                final View view = this.mAdapter.getView(i, child, this.mUserSwitcher);
                if (child == null) {
                    this.mUserSwitcher.addView(view);
                }
                else if (child != view) {
                    this.mUserSwitcher.removeViewAt(i);
                    this.mUserSwitcher.addView(view, i);
                }
            }
            else {
                this.mUserSwitcher.removeViewAt(this.mUserSwitcher.getChildCount() - 1);
            }
        }
    }
    
    private void reinflateViews() {
        final ViewGroup mUserSwitcher = this.mUserSwitcher;
        if (mUserSwitcher != null) {
            mUserSwitcher.setBackground((Drawable)null);
            this.mUserSwitcher.removeOnLayoutChangeListener((View$OnLayoutChangeListener)this.mBackground);
        }
        this.mUserSwitcherContainer.removeAllViews();
        LayoutInflater.from(this.mUserSwitcherContainer.getContext()).inflate(R$layout.keyguard_user_switcher_inner, (ViewGroup)this.mUserSwitcherContainer);
        (this.mUserSwitcher = (ViewGroup)this.mUserSwitcherContainer.findViewById(R$id.keyguard_user_switcher_inner)).addOnLayoutChangeListener((View$OnLayoutChangeListener)this.mBackground);
        this.mUserSwitcher.setBackground((Drawable)this.mBackground);
    }
    
    private boolean shouldExpandByDefault() {
        final UserSwitcherController mUserSwitcherController = this.mUserSwitcherController;
        return mUserSwitcherController != null && mUserSwitcherController.isSimpleUserSwitcher();
    }
    
    private void startAppearAnimation() {
        final int childCount = this.mUserSwitcher.getChildCount();
        final View[] array = new View[childCount];
        for (int i = 0; i < childCount; ++i) {
            array[i] = this.mUserSwitcher.getChildAt(i);
        }
        this.mUserSwitcher.setClipChildren(false);
        this.mUserSwitcher.setClipToPadding(false);
        this.mAppearAnimationUtils.startAnimation(array, new Runnable() {
            @Override
            public void run() {
                KeyguardUserSwitcher.this.mUserSwitcher.setClipChildren(true);
                KeyguardUserSwitcher.this.mUserSwitcher.setClipToPadding(true);
            }
        });
        this.mAnimating = true;
        (this.mBgAnimator = ObjectAnimator.ofInt((Object)this.mBackground, "alpha", new int[] { 0, 255 })).setDuration(400L);
        this.mBgAnimator.setInterpolator((TimeInterpolator)Interpolators.ALPHA_IN);
        this.mBgAnimator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                KeyguardUserSwitcher.this.mBgAnimator = null;
                KeyguardUserSwitcher.this.mAnimating = false;
            }
        });
        this.mBgAnimator.start();
    }
    
    private void startDisappearAnimation() {
        this.mAnimating = true;
        this.mUserSwitcher.animate().alpha(0.0f).setDuration(300L).setInterpolator((TimeInterpolator)Interpolators.ALPHA_OUT).withEndAction((Runnable)new Runnable() {
            @Override
            public void run() {
                KeyguardUserSwitcher.this.mUserSwitcherContainer.setVisibility(8);
                KeyguardUserSwitcher.this.mUserSwitcher.setAlpha(1.0f);
                KeyguardUserSwitcher.this.mAnimating = false;
            }
        });
    }
    
    public boolean hideIfNotSimple(final boolean b) {
        return this.mUserSwitcherContainer != null && !this.mUserSwitcherController.isSimpleUserSwitcher() && this.hide(b);
    }
    
    boolean isAnimating() {
        return this.mAnimating;
    }
    
    public void onDensityOrFontScaleChanged() {
        if (this.mUserSwitcherContainer != null) {
            this.reinflateViews();
            this.refresh();
        }
    }
    
    public void setKeyguard(final boolean b, final boolean b2) {
        if (this.mUserSwitcher != null) {
            if (b && this.shouldExpandByDefault()) {
                this.show(b2);
            }
            else {
                this.hide(b2);
            }
        }
    }
    
    public void show(final boolean b) {
        if (this.mUserSwitcher != null && this.mUserSwitcherContainer.getVisibility() != 0) {
            this.cancelAnimations();
            ((UserSwitcherController.BaseUserAdapter)this.mAdapter).refresh();
            this.mUserSwitcherContainer.setVisibility(0);
            this.mStatusBarView.setKeyguardUserSwitcherShowing(true, b);
            if (b) {
                this.startAppearAnimation();
            }
        }
    }
    
    public static class Adapter extends BaseUserAdapter implements View$OnClickListener
    {
        private Context mContext;
        private View mCurrentUserView;
        private KeyguardUserSwitcher mKeyguardUserSwitcher;
        
        public Adapter(final Context mContext, final UserSwitcherController userSwitcherController, final KeyguardUserSwitcher mKeyguardUserSwitcher) {
            super(userSwitcherController);
            this.mContext = mContext;
            this.mKeyguardUserSwitcher = mKeyguardUserSwitcher;
        }
        
        private static Drawable getDrawable(final Context context, final UserRecord userRecord) {
            final Drawable iconDrawable = UserSwitcherController.BaseUserAdapter.getIconDrawable(context, userRecord);
            int n;
            if (userRecord.isCurrent) {
                n = R$color.kg_user_switcher_selected_avatar_icon_color;
            }
            else {
                n = R$color.kg_user_switcher_avatar_icon_color;
            }
            iconDrawable.setTint(context.getResources().getColor(n, context.getTheme()));
            Object o = iconDrawable;
            if (userRecord.isCurrent) {
                o = new LayerDrawable(new Drawable[] { context.getDrawable(R$drawable.bg_avatar_selected), iconDrawable });
            }
            return (Drawable)o;
        }
        
        public View getView(final int n, final View view, final ViewGroup viewGroup) {
            final UserRecord item = ((UserSwitcherController.BaseUserAdapter)this).getItem(n);
            View inflate = null;
            Label_0050: {
                if (view instanceof UserDetailItemView) {
                    inflate = view;
                    if (view.getTag() instanceof UserRecord) {
                        break Label_0050;
                    }
                }
                inflate = LayoutInflater.from(this.mContext).inflate(R$layout.keyguard_user_switcher_item, viewGroup, false);
                inflate.setOnClickListener((View$OnClickListener)this);
            }
            final UserDetailItemView userDetailItemView = (UserDetailItemView)inflate;
            final String name = ((UserSwitcherController.BaseUserAdapter)this).getName(this.mContext, item);
            final Bitmap picture = item.picture;
            if (picture == null) {
                userDetailItemView.bind(name, getDrawable(this.mContext, item).mutate(), item.resolveId());
            }
            else {
                userDetailItemView.bind(name, picture, item.info.id);
            }
            userDetailItemView.setAvatarEnabled(item.isSwitchToEnabled);
            inflate.setActivated(item.isCurrent);
            if (item.isCurrent) {
                this.mCurrentUserView = inflate;
            }
            inflate.setTag((Object)item);
            return inflate;
        }
        
        public void onClick(final View view) {
            final UserRecord userRecord = (UserRecord)view.getTag();
            if (userRecord.isCurrent && !userRecord.isGuest) {
                this.mKeyguardUserSwitcher.hideIfNotSimple(true);
            }
            else if (userRecord.isSwitchToEnabled) {
                if (!userRecord.isAddUser && !userRecord.isRestricted && !userRecord.isDisabledByAdmin) {
                    final View mCurrentUserView = this.mCurrentUserView;
                    if (mCurrentUserView != null) {
                        mCurrentUserView.setActivated(false);
                    }
                    view.setActivated(true);
                }
                ((UserSwitcherController.BaseUserAdapter)this).switchTo(userRecord);
            }
        }
    }
    
    public static class Container extends FrameLayout
    {
        private KeyguardUserSwitcher mKeyguardUserSwitcher;
        
        public Container(final Context context, final AttributeSet set) {
            super(context, set);
            this.setClipChildren(false);
        }
        
        public boolean onTouchEvent(final MotionEvent motionEvent) {
            final KeyguardUserSwitcher mKeyguardUserSwitcher = this.mKeyguardUserSwitcher;
            if (mKeyguardUserSwitcher != null && !mKeyguardUserSwitcher.isAnimating()) {
                this.mKeyguardUserSwitcher.hideIfNotSimple(true);
            }
            return false;
        }
        
        public void setKeyguardUserSwitcher(final KeyguardUserSwitcher mKeyguardUserSwitcher) {
            this.mKeyguardUserSwitcher = mKeyguardUserSwitcher;
        }
    }
}
