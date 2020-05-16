// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.view.animation.Interpolator;
import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import java.util.function.Consumer;
import com.android.systemui.R$bool;
import com.android.systemui.R$id;
import java.text.NumberFormat;
import android.view.accessibility.AccessibilityEvent;
import android.graphics.Canvas;
import android.content.res.ColorStateList;
import android.view.View;
import com.android.systemui.plugins.DarkIconDispatcher;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.drawable.Icon;
import java.util.Arrays;
import android.widget.ImageView;
import android.graphics.ColorFilter;
import androidx.core.graphics.ColorUtils;
import com.android.internal.util.ContrastColorUtil;
import android.graphics.Color;
import android.content.res.Resources;
import com.android.systemui.statusbar.notification.NotificationUtils;
import com.android.systemui.R$dimen;
import android.util.TypedValue;
import android.os.Parcelable;
import com.android.systemui.R$string;
import android.text.TextUtils;
import android.content.pm.ApplicationInfo;
import android.util.Log;
import android.app.Notification$Builder;
import android.app.Notification;
import android.widget.ImageView$ScaleType;
import com.android.systemui.R$drawable;
import android.graphics.Paint$Align;
import android.util.AttributeSet;
import android.content.Context;
import android.util.FloatProperty;
import android.view.ViewDebug$ExportedProperty;
import android.graphics.drawable.Drawable;
import android.service.notification.StatusBarNotification;
import android.graphics.ColorMatrixColorFilter;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.systemui.statusbar.notification.NotificationIconDozeHelper;
import android.graphics.Paint;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.animation.ValueAnimator;
import android.util.Property;

public class StatusBarIconView extends AnimatedImageView implements StatusIconDisplayable
{
    private static final Property<StatusBarIconView, Float> DOT_APPEAR_AMOUNT;
    private static final Property<StatusBarIconView, Float> ICON_APPEAR_AMOUNT;
    private boolean mAlwaysScaleIcon;
    private int mAnimationStartColor;
    private final boolean mBlocked;
    private int mCachedContrastBackgroundColor;
    private ValueAnimator mColorAnimator;
    private final ValueAnimator$AnimatorUpdateListener mColorUpdater;
    private int mContrastedDrawableColor;
    private int mCurrentSetColor;
    private int mDecorColor;
    private int mDensity;
    private ObjectAnimator mDotAnimator;
    private float mDotAppearAmount;
    private final Paint mDotPaint;
    private float mDotRadius;
    private float mDozeAmount;
    private final NotificationIconDozeHelper mDozer;
    private int mDrawableColor;
    private StatusBarIcon mIcon;
    private float mIconAppearAmount;
    private ObjectAnimator mIconAppearAnimator;
    private int mIconColor;
    private float mIconScale;
    private boolean mIncreasedSize;
    private Runnable mLayoutRunnable;
    private float[] mMatrix;
    private ColorMatrixColorFilter mMatrixColorFilter;
    private boolean mNightMode;
    private StatusBarNotification mNotification;
    private Drawable mNumberBackground;
    private Paint mNumberPain;
    private String mNumberText;
    private int mNumberX;
    private int mNumberY;
    private Runnable mOnDismissListener;
    private OnVisibilityChangedListener mOnVisibilityChangedListener;
    private boolean mShowsConversation;
    @ViewDebug$ExportedProperty
    private String mSlot;
    private int mStaticDotRadius;
    private int mStatusBarIconDrawingSize;
    private int mStatusBarIconDrawingSizeIncreased;
    private int mStatusBarIconSize;
    private float mSystemIconDefaultScale;
    private float mSystemIconDesiredHeight;
    private float mSystemIconIntrinsicHeight;
    private int mVisibleState;
    
    static {
        ICON_APPEAR_AMOUNT = (Property)new FloatProperty<StatusBarIconView>() {
            public Float get(final StatusBarIconView statusBarIconView) {
                return statusBarIconView.getIconAppearAmount();
            }
            
            public void setValue(final StatusBarIconView statusBarIconView, final float iconAppearAmount) {
                statusBarIconView.setIconAppearAmount(iconAppearAmount);
            }
        };
        DOT_APPEAR_AMOUNT = (Property)new FloatProperty<StatusBarIconView>() {
            public Float get(final StatusBarIconView statusBarIconView) {
                return statusBarIconView.getDotAppearAmount();
            }
            
            public void setValue(final StatusBarIconView statusBarIconView, final float dotAppearAmount) {
                statusBarIconView.setDotAppearAmount(dotAppearAmount);
            }
        };
    }
    
    public StatusBarIconView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mSystemIconDesiredHeight = 15.0f;
        this.mSystemIconIntrinsicHeight = 17.0f;
        this.mSystemIconDefaultScale = 15.0f / 17.0f;
        this.mStatusBarIconDrawingSizeIncreased = 1;
        this.mStatusBarIconDrawingSize = 1;
        this.mStatusBarIconSize = 1;
        this.mIconScale = 1.0f;
        this.mDotPaint = new Paint(1);
        this.mVisibleState = 0;
        this.mIconAppearAmount = 1.0f;
        this.mCurrentSetColor = 0;
        this.mAnimationStartColor = 0;
        this.mColorUpdater = (ValueAnimator$AnimatorUpdateListener)new _$$Lambda$StatusBarIconView$nRA4PFzS_KIqshXSve3PBqKMX7Q(this);
        this.mCachedContrastBackgroundColor = 0;
        this.mDozer = new NotificationIconDozeHelper(context);
        this.mBlocked = false;
        this.mAlwaysScaleIcon = true;
        this.reloadDimens();
        this.maybeUpdateIconScaleDimens();
        this.mDensity = context.getResources().getDisplayMetrics().densityDpi;
    }
    
    public StatusBarIconView(final Context context, final String s, final StatusBarNotification statusBarNotification) {
        this(context, s, statusBarNotification, false);
    }
    
    public StatusBarIconView(final Context context, final String mSlot, final StatusBarNotification notification, final boolean mBlocked) {
        super(context);
        this.mSystemIconDesiredHeight = 15.0f;
        this.mSystemIconIntrinsicHeight = 17.0f;
        this.mSystemIconDefaultScale = 15.0f / 17.0f;
        final boolean b = true;
        this.mStatusBarIconDrawingSizeIncreased = 1;
        this.mStatusBarIconDrawingSize = 1;
        this.mStatusBarIconSize = 1;
        this.mIconScale = 1.0f;
        this.mDotPaint = new Paint(1);
        this.mVisibleState = 0;
        this.mIconAppearAmount = 1.0f;
        this.mCurrentSetColor = 0;
        this.mAnimationStartColor = 0;
        this.mColorUpdater = (ValueAnimator$AnimatorUpdateListener)new _$$Lambda$StatusBarIconView$nRA4PFzS_KIqshXSve3PBqKMX7Q(this);
        this.mCachedContrastBackgroundColor = 0;
        this.mDozer = new NotificationIconDozeHelper(context);
        this.mBlocked = mBlocked;
        this.mSlot = mSlot;
        (this.mNumberPain = new Paint()).setTextAlign(Paint$Align.CENTER);
        this.mNumberPain.setColor(context.getColor(R$drawable.notification_number_text_color));
        this.mNumberPain.setAntiAlias(true);
        this.setNotification(notification);
        this.setScaleType(ImageView$ScaleType.CENTER);
        this.mDensity = context.getResources().getDisplayMetrics().densityDpi;
        this.mNightMode = ((context.getResources().getConfiguration().uiMode & 0x30) == 0x20 && b);
        this.initializeDecorColor();
        this.reloadDimens();
        this.maybeUpdateIconScaleDimens();
    }
    
    public static String contentDescForNotification(final Context context, final Notification notification) {
        final String s = "";
        String s2;
        try {
            s2 = Notification$Builder.recoverBuilder(context, notification).loadHeaderAppName();
        }
        catch (RuntimeException ex) {
            Log.e("StatusBarIconView", "Unable to recover builder", (Throwable)ex);
            final Parcelable parcelable = notification.extras.getParcelable("android.appInfo");
            if (parcelable instanceof ApplicationInfo) {
                s2 = String.valueOf(((ApplicationInfo)parcelable).loadLabel(context.getPackageManager()));
            }
            else {
                s2 = "";
            }
        }
        final CharSequence charSequence = notification.extras.getCharSequence("android.title");
        final CharSequence charSequence2 = notification.extras.getCharSequence("android.text");
        final CharSequence tickerText = notification.tickerText;
        CharSequence charSequence3 = charSequence;
        if (TextUtils.equals(charSequence, (CharSequence)s2)) {
            charSequence3 = charSequence2;
        }
        if (TextUtils.isEmpty(charSequence3)) {
            charSequence3 = s;
            if (!TextUtils.isEmpty(tickerText)) {
                charSequence3 = tickerText;
            }
        }
        return context.getString(R$string.accessibility_desc_notification_icon, new Object[] { s2, charSequence3 });
    }
    
    public static Drawable getIcon(final Context context, final StatusBarIcon statusBarIcon) {
        int identifier;
        if ((identifier = statusBarIcon.user.getIdentifier()) == -1) {
            identifier = 0;
        }
        final Drawable loadDrawableAsUser = statusBarIcon.icon.loadDrawableAsUser(context, identifier);
        final TypedValue typedValue = new TypedValue();
        context.getResources().getValue(R$dimen.status_bar_icon_scale_factor, typedValue, true);
        final float float1 = typedValue.getFloat();
        if (float1 == 1.0f) {
            return loadDrawableAsUser;
        }
        return (Drawable)new ScalingDrawableWrapper(loadDrawableAsUser, float1);
    }
    
    private Drawable getIcon(final StatusBarIcon statusBarIcon) {
        return getIcon(this.getContext(), statusBarIcon);
    }
    
    private float getIconHeight() {
        if (this.getDrawable() != null) {
            return (float)this.getDrawable().getIntrinsicHeight();
        }
        return this.mSystemIconIntrinsicHeight;
    }
    
    private void initializeDecorColor() {
        if (this.mNotification != null) {
            final Context context = this.getContext();
            int n;
            if (this.mNightMode) {
                n = 17170885;
            }
            else {
                n = 17170886;
            }
            this.setDecorColor(context.getColor(n));
        }
    }
    
    private void maybeUpdateIconScaleDimens() {
        if (this.mNotification == null && !this.mAlwaysScaleIcon) {
            this.updateIconScaleForSystemIcons();
        }
        else {
            this.updateIconScaleForNotifications();
        }
    }
    
    private void reloadDimens() {
        final boolean b = this.mDotRadius == this.mStaticDotRadius;
        final Resources resources = this.getResources();
        this.mStaticDotRadius = resources.getDimensionPixelSize(R$dimen.overflow_dot_radius);
        this.mStatusBarIconSize = resources.getDimensionPixelSize(R$dimen.status_bar_icon_size);
        this.mStatusBarIconDrawingSizeIncreased = resources.getDimensionPixelSize(R$dimen.status_bar_icon_drawing_size_dark);
        this.mStatusBarIconDrawingSize = resources.getDimensionPixelSize(R$dimen.status_bar_icon_drawing_size);
        if (b) {
            this.mDotRadius = (float)this.mStaticDotRadius;
        }
        this.mSystemIconDesiredHeight = resources.getDimension(17105476);
        final float dimension = resources.getDimension(17105475);
        this.mSystemIconIntrinsicHeight = dimension;
        this.mSystemIconDefaultScale = this.mSystemIconDesiredHeight / dimension;
    }
    
    private void runRunnable(final Runnable runnable) {
        if (runnable != null) {
            runnable.run();
        }
    }
    
    private void setColorInternal(final int mCurrentSetColor) {
        this.mCurrentSetColor = mCurrentSetColor;
        this.updateIconColor();
    }
    
    private void setContentDescription(final Notification notification) {
        if (notification != null) {
            final String contentDescForNotification = contentDescForNotification(super.mContext, notification);
            if (!TextUtils.isEmpty((CharSequence)contentDescForNotification)) {
                this.setContentDescription((CharSequence)contentDescForNotification);
            }
        }
    }
    
    private void updateAllowAnimation() {
        final float mDozeAmount = this.mDozeAmount;
        if (mDozeAmount == 0.0f || mDozeAmount == 1.0f) {
            this.setAllowAnimation(this.mDozeAmount == 0.0f);
        }
    }
    
    private void updateContrastedStaticColor() {
        if (Color.alpha(this.mCachedContrastBackgroundColor) != 255) {
            this.mContrastedDrawableColor = this.mDrawableColor;
            return;
        }
        int mContrastedDrawableColor;
        int n = mContrastedDrawableColor = this.mDrawableColor;
        if (!ContrastColorUtil.satisfiesTextContrast(this.mCachedContrastBackgroundColor, n)) {
            final float[] array = new float[3];
            ColorUtils.colorToHSL(this.mDrawableColor, array);
            if (array[1] < 0.2f) {
                n = 0;
            }
            mContrastedDrawableColor = ContrastColorUtil.resolveContrastColor(super.mContext, n, this.mCachedContrastBackgroundColor, ContrastColorUtil.isColorLight(this.mCachedContrastBackgroundColor) ^ true);
        }
        this.mContrastedDrawableColor = mContrastedDrawableColor;
    }
    
    private void updateDecorColor() {
        final int interpolateColors = NotificationUtils.interpolateColors(this.mDecorColor, -1, this.mDozeAmount);
        if (this.mDotPaint.getColor() != interpolateColors) {
            this.mDotPaint.setColor(interpolateColors);
            if (this.mDotAppearAmount != 0.0f) {
                this.invalidate();
            }
        }
    }
    
    private boolean updateDrawable(final boolean b) {
        final StatusBarIcon mIcon = this.mIcon;
        if (mIcon == null) {
            return false;
        }
        try {
            final Drawable icon = this.getIcon(mIcon);
            if (icon == null) {
                final StringBuilder sb = new StringBuilder();
                sb.append("No icon for slot ");
                sb.append(this.mSlot);
                sb.append("; ");
                sb.append(this.mIcon.icon);
                Log.w("StatusBarIconView", sb.toString());
                return false;
            }
            if (b) {
                this.setImageDrawable(null);
            }
            this.setImageDrawable(icon);
            return true;
        }
        catch (OutOfMemoryError outOfMemoryError) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("OOM while inflating ");
            sb2.append(this.mIcon.icon);
            sb2.append(" for slot ");
            sb2.append(this.mSlot);
            Log.w("StatusBarIconView", sb2.toString());
            return false;
        }
    }
    
    private void updateIconColor() {
        if (this.mShowsConversation) {
            this.setColorFilter((ColorFilter)null);
            return;
        }
        if (this.mCurrentSetColor != 0) {
            if (this.mMatrixColorFilter == null) {
                this.mMatrix = new float[20];
                this.mMatrixColorFilter = new ColorMatrixColorFilter(this.mMatrix);
            }
            updateTintMatrix(this.mMatrix, NotificationUtils.interpolateColors(this.mCurrentSetColor, -1, this.mDozeAmount), this.mDozeAmount * 0.67f);
            this.mMatrixColorFilter.setColorMatrixArray(this.mMatrix);
            this.setColorFilter((ColorFilter)null);
            this.setColorFilter((ColorFilter)this.mMatrixColorFilter);
        }
        else {
            this.mDozer.updateGrayscale(this, this.mDozeAmount);
        }
    }
    
    private void updateIconScaleForNotifications() {
        int n;
        if (this.mIncreasedSize) {
            n = this.mStatusBarIconDrawingSizeIncreased;
        }
        else {
            n = this.mStatusBarIconDrawingSize;
        }
        this.mIconScale = n / (float)this.mStatusBarIconSize;
        this.updatePivot();
    }
    
    private void updateIconScaleForSystemIcons() {
        final float iconHeight = this.getIconHeight();
        if (iconHeight != 0.0f) {
            this.mIconScale = this.mSystemIconDesiredHeight / iconHeight;
        }
        else {
            this.mIconScale = this.mSystemIconDefaultScale;
        }
    }
    
    private void updatePivot() {
        if (this.isLayoutRtl()) {
            this.setPivotX((this.mIconScale + 1.0f) / 2.0f * this.getWidth());
        }
        else {
            this.setPivotX((1.0f - this.mIconScale) / 2.0f * this.getWidth());
        }
        this.setPivotY((this.getHeight() - this.mIconScale * this.getWidth()) / 2.0f);
    }
    
    private static void updateTintMatrix(final float[] a, final int n, final float n2) {
        Arrays.fill(a, 0.0f);
        a[4] = (float)Color.red(n);
        a[9] = (float)Color.green(n);
        a[14] = (float)Color.blue(n);
        a[18] = Color.alpha(n) / 255.0f + n2;
    }
    
    protected void debug(final int n) {
        super.debug(n);
        final StringBuilder sb = new StringBuilder();
        sb.append(ImageView.debugIndent(n));
        sb.append("slot=");
        sb.append(this.mSlot);
        Log.d("View", sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append(ImageView.debugIndent(n));
        sb2.append("icon=");
        sb2.append(this.mIcon);
        Log.d("View", sb2.toString());
    }
    
    public boolean equalIcons(final Icon icon, final Icon icon2) {
        boolean b = true;
        if (icon == icon2) {
            return true;
        }
        if (icon.getType() != icon2.getType()) {
            return false;
        }
        final int type = icon.getType();
        if (type != 2) {
            return (type == 4 || type == 6) && icon.getUriString().equals(icon2.getUriString());
        }
        if (!icon.getResPackage().equals(icon2.getResPackage()) || icon.getResId() != icon2.getResId()) {
            b = false;
        }
        return b;
    }
    
    public void executeOnLayout(final Runnable mLayoutRunnable) {
        this.mLayoutRunnable = mLayoutRunnable;
    }
    
    int getContrastedStaticDrawableColor(final int mCachedContrastBackgroundColor) {
        if (this.mCachedContrastBackgroundColor != mCachedContrastBackgroundColor) {
            this.mCachedContrastBackgroundColor = mCachedContrastBackgroundColor;
            this.updateContrastedStaticColor();
        }
        return this.mContrastedDrawableColor;
    }
    
    public float getDotAppearAmount() {
        return this.mDotAppearAmount;
    }
    
    public void getDrawingRect(final Rect rect) {
        super.getDrawingRect(rect);
        final float translationX = this.getTranslationX();
        final float translationY = this.getTranslationY();
        rect.left += (int)translationX;
        rect.right += (int)translationX;
        rect.top += (int)translationY;
        rect.bottom += (int)translationY;
    }
    
    public float getIconAppearAmount() {
        return this.mIconAppearAmount;
    }
    
    public float getIconScale() {
        return this.mIconScale;
    }
    
    public float getIconScaleIncreased() {
        return this.mStatusBarIconDrawingSizeIncreased / (float)this.mStatusBarIconDrawingSize;
    }
    
    public StatusBarNotification getNotification() {
        return this.mNotification;
    }
    
    @Override
    public String getSlot() {
        return this.mSlot;
    }
    
    public Icon getSourceIcon() {
        return this.mIcon.icon;
    }
    
    public int getStaticDrawableColor() {
        return this.mDrawableColor;
    }
    
    public StatusBarIcon getStatusBarIcon() {
        return this.mIcon;
    }
    
    @Override
    public int getVisibleState() {
        return this.mVisibleState;
    }
    
    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }
    
    @Override
    public boolean isIconBlocked() {
        return this.mBlocked;
    }
    
    @Override
    public boolean isIconVisible() {
        final StatusBarIcon mIcon = this.mIcon;
        return mIcon != null && mIcon.visible;
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        final int densityDpi = configuration.densityDpi;
        if (densityDpi != this.mDensity) {
            this.mDensity = densityDpi;
            this.reloadDimens();
            this.updateDrawable();
            this.maybeUpdateIconScaleDimens();
        }
        final boolean mNightMode = (configuration.uiMode & 0x30) == 0x20;
        if (mNightMode != this.mNightMode) {
            this.mNightMode = mNightMode;
            this.initializeDecorColor();
        }
    }
    
    public void onDarkChanged(final Rect rect, final float n, int tint) {
        tint = DarkIconDispatcher.getTint(rect, (View)this, tint);
        this.setImageTintList(ColorStateList.valueOf(tint));
        this.setDecorColor(tint);
    }
    
    protected void onDraw(final Canvas canvas) {
        if (this.mIconAppearAmount > 0.0f) {
            canvas.save();
            final float mIconScale = this.mIconScale;
            final float mIconAppearAmount = this.mIconAppearAmount;
            canvas.scale(mIconScale * mIconAppearAmount, mIconScale * mIconAppearAmount, (float)(this.getWidth() / 2), (float)(this.getHeight() / 2));
            super.onDraw(canvas);
            canvas.restore();
        }
        final Drawable mNumberBackground = this.mNumberBackground;
        if (mNumberBackground != null) {
            mNumberBackground.draw(canvas);
            canvas.drawText(this.mNumberText, (float)this.mNumberX, (float)this.mNumberY, this.mNumberPain);
        }
        if (this.mDotAppearAmount != 0.0f) {
            float n = Color.alpha(this.mDecorColor) / 255.0f;
            final float mDotAppearAmount = this.mDotAppearAmount;
            float interpolate;
            if (mDotAppearAmount <= 1.0f) {
                interpolate = this.mDotRadius * mDotAppearAmount;
            }
            else {
                final float n2 = mDotAppearAmount - 1.0f;
                n *= 1.0f - n2;
                interpolate = NotificationUtils.interpolate(this.mDotRadius, (float)(this.getWidth() / 4), n2);
            }
            this.mDotPaint.setAlpha((int)(n * 255.0f));
            canvas.drawCircle((float)(this.mStatusBarIconSize / 2), (float)(this.getHeight() / 2), interpolate, this.mDotPaint);
        }
    }
    
    public void onInitializeAccessibilityEvent(final AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        final StatusBarNotification mNotification = this.mNotification;
        if (mNotification != null) {
            accessibilityEvent.setParcelableData((Parcelable)mNotification.getNotification());
        }
    }
    
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        super.onLayout(b, n, n2, n3, n4);
        final Runnable mLayoutRunnable = this.mLayoutRunnable;
        if (mLayoutRunnable != null) {
            mLayoutRunnable.run();
            this.mLayoutRunnable = null;
        }
        this.updatePivot();
    }
    
    public void onRtlPropertiesChanged(final int n) {
        super.onRtlPropertiesChanged(n);
        this.updateDrawable();
    }
    
    protected void onSizeChanged(final int n, final int n2, final int n3, final int n4) {
        super.onSizeChanged(n, n2, n3, n4);
        if (this.mNumberBackground != null) {
            this.placeNumber();
        }
    }
    
    void placeNumber() {
        String mNumberText;
        if (this.mIcon.number > this.getContext().getResources().getInteger(17694723)) {
            mNumberText = this.getContext().getResources().getString(17039383);
        }
        else {
            mNumberText = NumberFormat.getIntegerInstance().format(this.mIcon.number);
        }
        this.mNumberText = mNumberText;
        final int width = this.getWidth();
        final int height = this.getHeight();
        final Rect rect = new Rect();
        this.mNumberPain.getTextBounds(mNumberText, 0, mNumberText.length(), rect);
        final int right = rect.right;
        final int left = rect.left;
        final int n = rect.bottom - rect.top;
        this.mNumberBackground.getPadding(rect);
        int minimumWidth;
        if ((minimumWidth = rect.left + (right - left) + rect.right) < this.mNumberBackground.getMinimumWidth()) {
            minimumWidth = this.mNumberBackground.getMinimumWidth();
        }
        final int right2 = rect.right;
        this.mNumberX = width - right2 - (minimumWidth - right2 - rect.left) / 2;
        int minimumWidth2;
        if ((minimumWidth2 = rect.top + n + rect.bottom) < this.mNumberBackground.getMinimumWidth()) {
            minimumWidth2 = this.mNumberBackground.getMinimumWidth();
        }
        final int bottom = rect.bottom;
        this.mNumberY = height - bottom - (minimumWidth2 - rect.top - n - bottom) / 2;
        this.mNumberBackground.setBounds(width - minimumWidth, height - minimumWidth2, width, height);
    }
    
    public boolean set(final StatusBarIcon statusBarIcon) {
        final StatusBarIcon mIcon = this.mIcon;
        final int n = 0;
        final boolean b = mIcon != null && this.equalIcons(mIcon.icon, statusBarIcon.icon);
        final boolean b2 = b && this.mIcon.iconLevel == statusBarIcon.iconLevel;
        final StatusBarIcon mIcon2 = this.mIcon;
        final boolean b3 = mIcon2 != null && mIcon2.visible == statusBarIcon.visible;
        final StatusBarIcon mIcon3 = this.mIcon;
        final boolean b4 = mIcon3 != null && mIcon3.number == statusBarIcon.number;
        this.mIcon = statusBarIcon.clone();
        this.setContentDescription(statusBarIcon.contentDescription);
        if (!b) {
            if (!this.updateDrawable(false)) {
                return false;
            }
            this.setTag(R$id.icon_is_grayscale, (Object)null);
            this.maybeUpdateIconScaleDimens();
        }
        if (!b2) {
            this.setImageLevel(statusBarIcon.iconLevel);
        }
        if (!b4) {
            if (statusBarIcon.number > 0 && this.getContext().getResources().getBoolean(R$bool.config_statusBarShowNumber)) {
                if (this.mNumberBackground == null) {
                    this.mNumberBackground = this.getContext().getResources().getDrawable(R$drawable.ic_notification_overlay);
                }
                this.placeNumber();
            }
            else {
                this.mNumberBackground = null;
                this.mNumberText = null;
            }
            this.invalidate();
        }
        if (!b3) {
            int visibility;
            if (statusBarIcon.visible && !this.mBlocked) {
                visibility = n;
            }
            else {
                visibility = 8;
            }
            this.setVisibility(visibility);
        }
        return true;
    }
    
    @Override
    public void setDecorColor(final int mDecorColor) {
        this.mDecorColor = mDecorColor;
        this.updateDecorColor();
    }
    
    public void setDismissed() {
        final Runnable mOnDismissListener = this.mOnDismissListener;
        if (mOnDismissListener != null) {
            mOnDismissListener.run();
        }
    }
    
    public void setDotAppearAmount(final float mDotAppearAmount) {
        if (this.mDotAppearAmount != mDotAppearAmount) {
            this.mDotAppearAmount = mDotAppearAmount;
            this.invalidate();
        }
    }
    
    public void setDozing(final boolean b, final boolean b2, final long n) {
        this.mDozer.setDozing(new _$$Lambda$StatusBarIconView$x3AGEt_5vRmE_DqrCK9ien5Lp2M(this), b, b2, n, (View)this);
    }
    
    public void setIconAppearAmount(final float mIconAppearAmount) {
        if (this.mIconAppearAmount != mIconAppearAmount) {
            this.mIconAppearAmount = mIconAppearAmount;
            this.invalidate();
        }
    }
    
    public void setIconColor(final int n, final boolean b) {
        if (this.mIconColor != n) {
            this.mIconColor = n;
            final ValueAnimator mColorAnimator = this.mColorAnimator;
            if (mColorAnimator != null) {
                mColorAnimator.cancel();
            }
            final int mCurrentSetColor = this.mCurrentSetColor;
            if (mCurrentSetColor == n) {
                return;
            }
            if (b && mCurrentSetColor != 0) {
                this.mAnimationStartColor = mCurrentSetColor;
                (this.mColorAnimator = ValueAnimator.ofFloat(new float[] { 0.0f, 1.0f })).setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_SLOW_IN);
                this.mColorAnimator.setDuration(100L);
                this.mColorAnimator.addUpdateListener(this.mColorUpdater);
                this.mColorAnimator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                    public void onAnimationEnd(final Animator animator) {
                        StatusBarIconView.this.mColorAnimator = null;
                        StatusBarIconView.this.mAnimationStartColor = 0;
                    }
                });
                this.mColorAnimator.start();
            }
            else {
                this.setColorInternal(n);
            }
        }
    }
    
    public void setIncreasedSize(final boolean mIncreasedSize) {
        this.mIncreasedSize = mIncreasedSize;
        this.maybeUpdateIconScaleDimens();
    }
    
    public void setIsInShelf(final boolean b) {
    }
    
    public void setNotification(final StatusBarNotification mNotification) {
        this.mNotification = mNotification;
        if (mNotification != null) {
            this.setContentDescription(mNotification.getNotification());
        }
        this.maybeUpdateIconScaleDimens();
    }
    
    public void setOnDismissListener(final Runnable mOnDismissListener) {
        this.mOnDismissListener = mOnDismissListener;
    }
    
    public void setOnVisibilityChangedListener(final OnVisibilityChangedListener mOnVisibilityChangedListener) {
        this.mOnVisibilityChangedListener = mOnVisibilityChangedListener;
    }
    
    public void setShowsConversation(final boolean mShowsConversation) {
        if (this.mShowsConversation != mShowsConversation) {
            this.mShowsConversation = mShowsConversation;
            this.updateIconColor();
        }
    }
    
    @Override
    public void setStaticDrawableColor(final int color) {
        this.setColorInternal(this.mDrawableColor = color);
        this.updateContrastedStaticColor();
        this.mIconColor = color;
        this.mDozer.setColor(color);
    }
    
    public void setVisibility(final int visibility) {
        super.setVisibility(visibility);
        final OnVisibilityChangedListener mOnVisibilityChangedListener = this.mOnVisibilityChangedListener;
        if (mOnVisibilityChangedListener != null) {
            mOnVisibilityChangedListener.onVisibilityChanged(visibility);
        }
    }
    
    @Override
    public void setVisibleState(final int n) {
        this.setVisibleState(n, true, null);
    }
    
    @Override
    public void setVisibleState(final int n, final boolean b) {
        this.setVisibleState(n, b, null);
    }
    
    public void setVisibleState(final int n, final boolean b, final Runnable runnable) {
        this.setVisibleState(n, b, runnable, 0L);
    }
    
    public void setVisibleState(final int mVisibleState, final boolean b, final Runnable runnable, long duration) {
        final int mVisibleState2 = this.mVisibleState;
        int n2;
        final int n = n2 = 0;
        if (mVisibleState != mVisibleState2) {
            this.mVisibleState = mVisibleState;
            final ObjectAnimator mIconAppearAnimator = this.mIconAppearAnimator;
            if (mIconAppearAnimator != null) {
                mIconAppearAnimator.cancel();
            }
            final ObjectAnimator mDotAnimator = this.mDotAnimator;
            if (mDotAnimator != null) {
                mDotAnimator.cancel();
            }
            if (b) {
                Interpolator interpolator = Interpolators.FAST_OUT_LINEAR_IN;
                float n3;
                if (mVisibleState == 0) {
                    interpolator = Interpolators.LINEAR_OUT_SLOW_IN;
                    n3 = 1.0f;
                }
                else {
                    n3 = 0.0f;
                }
                final float iconAppearAmount = this.getIconAppearAmount();
                final long n4 = 100L;
                if (n3 != iconAppearAmount) {
                    (this.mIconAppearAnimator = ObjectAnimator.ofFloat((Object)this, (Property)StatusBarIconView.ICON_APPEAR_AMOUNT, new float[] { iconAppearAmount, n3 })).setInterpolator((TimeInterpolator)interpolator);
                    final ObjectAnimator mIconAppearAnimator2 = this.mIconAppearAnimator;
                    long duration2;
                    if (duration == 0L) {
                        duration2 = 100L;
                    }
                    else {
                        duration2 = duration;
                    }
                    mIconAppearAnimator2.setDuration(duration2);
                    this.mIconAppearAnimator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                        public void onAnimationEnd(final Animator animator) {
                            StatusBarIconView.this.mIconAppearAnimator = null;
                            StatusBarIconView.this.runRunnable(runnable);
                        }
                    });
                    this.mIconAppearAnimator.start();
                    n2 = 1;
                }
                else {
                    n2 = 0;
                }
                float n5;
                if (mVisibleState == 0) {
                    n5 = 2.0f;
                }
                else {
                    n5 = 0.0f;
                }
                Interpolator interpolator2 = Interpolators.FAST_OUT_LINEAR_IN;
                if (mVisibleState == 1) {
                    interpolator2 = Interpolators.LINEAR_OUT_SLOW_IN;
                    n5 = 1.0f;
                }
                final float dotAppearAmount = this.getDotAppearAmount();
                if (n5 != dotAppearAmount) {
                    (this.mDotAnimator = ObjectAnimator.ofFloat((Object)this, (Property)StatusBarIconView.DOT_APPEAR_AMOUNT, new float[] { dotAppearAmount, n5 })).setInterpolator((TimeInterpolator)interpolator2);
                    final ObjectAnimator mDotAnimator2 = this.mDotAnimator;
                    if (duration == 0L) {
                        duration = n4;
                    }
                    mDotAnimator2.setDuration(duration);
                    this.mDotAnimator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                        final /* synthetic */ boolean val$runRunnable = n2 ^ 0x1;
                        
                        public void onAnimationEnd(final Animator animator) {
                            StatusBarIconView.this.mDotAnimator = null;
                            if (this.val$runRunnable) {
                                StatusBarIconView.this.runRunnable(runnable);
                            }
                        }
                    });
                    this.mDotAnimator.start();
                    n2 = 1;
                }
            }
            else {
                float iconAppearAmount2;
                if (mVisibleState == 0) {
                    iconAppearAmount2 = 1.0f;
                }
                else {
                    iconAppearAmount2 = 0.0f;
                }
                this.setIconAppearAmount(iconAppearAmount2);
                float dotAppearAmount2;
                if (mVisibleState == 1) {
                    dotAppearAmount2 = 1.0f;
                }
                else if (mVisibleState == 0) {
                    dotAppearAmount2 = 2.0f;
                }
                else {
                    dotAppearAmount2 = 0.0f;
                }
                this.setDotAppearAmount(dotAppearAmount2);
                n2 = n;
            }
        }
        if (n2 == 0) {
            this.runRunnable(runnable);
        }
    }
    
    public boolean showsConversation() {
        return this.mShowsConversation;
    }
    
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("StatusBarIconView(slot=");
        sb.append(this.mSlot);
        sb.append(" icon=");
        sb.append(this.mIcon);
        sb.append(" notification=");
        sb.append(this.mNotification);
        sb.append(")");
        return sb.toString();
    }
    
    public void updateDrawable() {
        this.updateDrawable(true);
    }
    
    public interface OnVisibilityChangedListener
    {
        void onVisibilityChanged(final int p0);
    }
}
