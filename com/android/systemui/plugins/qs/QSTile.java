// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.plugins.qs;

import java.util.Objects;
import java.util.function.Supplier;
import android.graphics.drawable.Drawable;
import android.metrics.LogMaker;
import android.content.Context;
import com.android.systemui.plugins.annotations.ProvidesInterface;
import com.android.systemui.plugins.annotations.DependsOn;
import com.android.systemui.plugins.annotations.Dependencies;

@Dependencies({ @DependsOn(target = QSIconView.class), @DependsOn(target = DetailAdapter.class), @DependsOn(target = Callback.class), @DependsOn(target = Icon.class), @DependsOn(target = State.class) })
@ProvidesInterface(version = 1)
public interface QSTile
{
    public static final int VERSION = 1;
    
    void addCallback(final Callback p0);
    
    @Deprecated
    default void clearState() {
    }
    
    void click();
    
    QSIconView createTileView(final Context p0);
    
    void destroy();
    
    DetailAdapter getDetailAdapter();
    
    int getMetricsCategory();
    
    State getState();
    
    CharSequence getTileLabel();
    
    String getTileSpec();
    
    boolean isAvailable();
    
    void longClick();
    
    default LogMaker populate(final LogMaker logMaker) {
        return logMaker;
    }
    
    void refreshState();
    
    void removeCallback(final Callback p0);
    
    void removeCallbacks();
    
    void secondaryClick();
    
    void setDetailListening(final boolean p0);
    
    void setListening(final Object p0, final boolean p1);
    
    void setTileSpec(final String p0);
    
    default boolean supportsDetailView() {
        return false;
    }
    
    void userSwitch(final int p0);
    
    @ProvidesInterface(version = 1)
    public static class BooleanState extends State
    {
        public static final int VERSION = 1;
        public boolean value;
        
        @Override
        public State copy() {
            final BooleanState booleanState = new BooleanState();
            this.copyTo(booleanState);
            return booleanState;
        }
        
        @Override
        public boolean copyTo(final State state) {
            final BooleanState booleanState = (BooleanState)state;
            final boolean b = super.copyTo(state) || booleanState.value != this.value;
            booleanState.value = this.value;
            return b;
        }
        
        @Override
        protected StringBuilder toStringBuilder() {
            final StringBuilder stringBuilder = super.toStringBuilder();
            final int length = stringBuilder.length();
            final StringBuilder sb = new StringBuilder();
            sb.append(",value=");
            sb.append(this.value);
            stringBuilder.insert(length - 1, sb.toString());
            return stringBuilder;
        }
    }
    
    @ProvidesInterface(version = 1)
    public interface Callback
    {
        public static final int VERSION = 1;
        
        void onAnnouncementRequested(final CharSequence p0);
        
        void onScanStateChanged(final boolean p0);
        
        void onShowDetail(final boolean p0);
        
        void onStateChanged(final State p0);
        
        void onToggleStateChanged(final boolean p0);
    }
    
    @ProvidesInterface(version = 1)
    public abstract static class Icon
    {
        public static final int VERSION = 1;
        
        public abstract Drawable getDrawable(final Context p0);
        
        public Drawable getInvisibleDrawable(final Context context) {
            return this.getDrawable(context);
        }
        
        public int getPadding() {
            return 0;
        }
        
        @Override
        public int hashCode() {
            return Icon.class.hashCode();
        }
        
        @Override
        public String toString() {
            return "Icon";
        }
    }
    
    @ProvidesInterface(version = 1)
    public static final class SignalState extends BooleanState
    {
        public static final int VERSION = 1;
        public boolean activityIn;
        public boolean activityOut;
        public boolean isOverlayIconWide;
        public int overlayIconId;
        
        @Override
        public State copy() {
            final SignalState signalState = new SignalState();
            this.copyTo(signalState);
            return signalState;
        }
        
        @Override
        public boolean copyTo(final State state) {
            final SignalState signalState = (SignalState)state;
            final boolean activityIn = signalState.activityIn;
            final boolean activityIn2 = this.activityIn;
            boolean b = false;
            final boolean b2 = activityIn != activityIn2 || signalState.activityOut != this.activityOut || signalState.isOverlayIconWide != this.isOverlayIconWide || signalState.overlayIconId != this.overlayIconId;
            signalState.activityIn = this.activityIn;
            signalState.activityOut = this.activityOut;
            signalState.isOverlayIconWide = this.isOverlayIconWide;
            signalState.overlayIconId = this.overlayIconId;
            if (super.copyTo(state) || b2) {
                b = true;
            }
            return b;
        }
        
        @Override
        protected StringBuilder toStringBuilder() {
            final StringBuilder stringBuilder = super.toStringBuilder();
            final int length = stringBuilder.length();
            final StringBuilder sb = new StringBuilder();
            sb.append(",activityIn=");
            sb.append(this.activityIn);
            stringBuilder.insert(length - 1, sb.toString());
            final int length2 = stringBuilder.length();
            final StringBuilder sb2 = new StringBuilder();
            sb2.append(",activityOut=");
            sb2.append(this.activityOut);
            stringBuilder.insert(length2 - 1, sb2.toString());
            return stringBuilder;
        }
    }
    
    @ProvidesInterface(version = 2)
    public static class SlashState
    {
        public static final int VERSION = 2;
        public boolean isSlashed;
        public float rotation;
        
        public SlashState copy() {
            final SlashState slashState = new SlashState();
            slashState.rotation = this.rotation;
            slashState.isSlashed = this.isSlashed;
            return slashState;
        }
        
        @Override
        public boolean equals(final Object o) {
            final boolean b = false;
            if (o == null) {
                return false;
            }
            boolean b2 = b;
            try {
                if (((SlashState)o).rotation == this.rotation) {
                    final boolean isSlashed = ((SlashState)o).isSlashed;
                    final boolean isSlashed2 = this.isSlashed;
                    b2 = b;
                    if (isSlashed == isSlashed2) {
                        b2 = true;
                    }
                }
                return b2;
            }
            catch (ClassCastException ex) {
                b2 = b;
                return b2;
            }
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("isSlashed=");
            sb.append(this.isSlashed);
            sb.append(",rotation=");
            sb.append(this.rotation);
            return sb.toString();
        }
    }
    
    @ProvidesInterface(version = 1)
    public static class State
    {
        public static final int VERSION = 1;
        public CharSequence contentDescription;
        public boolean disabledByPolicy;
        public CharSequence dualLabelContentDescription;
        public boolean dualTarget;
        public String expandedAccessibilityClassName;
        public boolean handlesLongClick;
        public Icon icon;
        public Supplier<Icon> iconSupplier;
        public boolean isTransient;
        public CharSequence label;
        public CharSequence secondaryLabel;
        public boolean showRippleEffect;
        public SlashState slash;
        public int state;
        public CharSequence stateDescription;
        
        public State() {
            this.state = 2;
            this.dualTarget = false;
            this.isTransient = false;
            this.handlesLongClick = true;
            this.showRippleEffect = true;
        }
        
        public State copy() {
            final State state = new State();
            this.copyTo(state);
            return state;
        }
        
        public boolean copyTo(final State state) {
            if (state == null) {
                throw new IllegalArgumentException();
            }
            if (state.getClass().equals(this.getClass())) {
                final boolean b = !Objects.equals(state.icon, this.icon) || !Objects.equals(state.iconSupplier, this.iconSupplier) || !Objects.equals(state.label, this.label) || !Objects.equals(state.secondaryLabel, this.secondaryLabel) || !Objects.equals(state.contentDescription, this.contentDescription) || !Objects.equals(state.stateDescription, this.stateDescription) || !Objects.equals(state.dualLabelContentDescription, this.dualLabelContentDescription) || !Objects.equals(state.expandedAccessibilityClassName, this.expandedAccessibilityClassName) || !Objects.equals(state.disabledByPolicy, this.disabledByPolicy) || !Objects.equals(state.state, this.state) || !Objects.equals(state.isTransient, this.isTransient) || !Objects.equals(state.dualTarget, this.dualTarget) || !Objects.equals(state.slash, this.slash) || !Objects.equals(state.handlesLongClick, this.handlesLongClick) || !Objects.equals(state.showRippleEffect, this.showRippleEffect);
                state.icon = this.icon;
                state.iconSupplier = this.iconSupplier;
                state.label = this.label;
                state.secondaryLabel = this.secondaryLabel;
                state.contentDescription = this.contentDescription;
                state.stateDescription = this.stateDescription;
                state.dualLabelContentDescription = this.dualLabelContentDescription;
                state.expandedAccessibilityClassName = this.expandedAccessibilityClassName;
                state.disabledByPolicy = this.disabledByPolicy;
                state.state = this.state;
                state.dualTarget = this.dualTarget;
                state.isTransient = this.isTransient;
                final SlashState slash = this.slash;
                Object copy;
                if (slash != null) {
                    copy = slash.copy();
                }
                else {
                    copy = null;
                }
                state.slash = (SlashState)copy;
                state.handlesLongClick = this.handlesLongClick;
                state.showRippleEffect = this.showRippleEffect;
                return b;
            }
            throw new IllegalArgumentException();
        }
        
        @Override
        public String toString() {
            return this.toStringBuilder().toString();
        }
        
        protected StringBuilder toStringBuilder() {
            final StringBuilder sb = new StringBuilder(this.getClass().getSimpleName());
            sb.append('[');
            sb.append(",icon=");
            sb.append(this.icon);
            sb.append(",iconSupplier=");
            sb.append(this.iconSupplier);
            sb.append(",label=");
            sb.append(this.label);
            sb.append(",secondaryLabel=");
            sb.append(this.secondaryLabel);
            sb.append(",contentDescription=");
            sb.append(this.contentDescription);
            sb.append(",stateDescription=");
            sb.append(this.stateDescription);
            sb.append(",dualLabelContentDescription=");
            sb.append(this.dualLabelContentDescription);
            sb.append(",expandedAccessibilityClassName=");
            sb.append(this.expandedAccessibilityClassName);
            sb.append(",disabledByPolicy=");
            sb.append(this.disabledByPolicy);
            sb.append(",dualTarget=");
            sb.append(this.dualTarget);
            sb.append(",isTransient=");
            sb.append(this.isTransient);
            sb.append(",state=");
            sb.append(this.state);
            sb.append(",slash=\"");
            sb.append(this.slash);
            sb.append("\"");
            sb.append(']');
            return sb;
        }
    }
}
