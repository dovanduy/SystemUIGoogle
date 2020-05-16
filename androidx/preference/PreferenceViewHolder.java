// 
// Decompiled by Procyon v0.5.36
// 

package androidx.preference;

import androidx.core.view.ViewCompat;
import android.widget.TextView;
import android.content.res.ColorStateList;
import android.view.View;
import android.util.SparseArray;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;

public class PreferenceViewHolder extends ViewHolder
{
    private Drawable mBackground;
    private final SparseArray<View> mCachedViews;
    private boolean mDividerAllowedAbove;
    private boolean mDividerAllowedBelow;
    private ColorStateList mTitleTextColors;
    
    PreferenceViewHolder(final View view) {
        super(view);
        this.mCachedViews = (SparseArray<View>)new SparseArray(4);
        final TextView textView = (TextView)view.findViewById(16908310);
        this.mCachedViews.put(16908310, (Object)textView);
        this.mCachedViews.put(16908304, (Object)view.findViewById(16908304));
        this.mCachedViews.put(16908294, (Object)view.findViewById(16908294));
        final SparseArray<View> mCachedViews = this.mCachedViews;
        final int icon_frame = R$id.icon_frame;
        mCachedViews.put(icon_frame, (Object)view.findViewById(icon_frame));
        this.mCachedViews.put(16908350, (Object)view.findViewById(16908350));
        this.mBackground = view.getBackground();
        if (textView != null) {
            this.mTitleTextColors = textView.getTextColors();
        }
    }
    
    public View findViewById(final int n) {
        final View view = (View)this.mCachedViews.get(n);
        if (view != null) {
            return view;
        }
        final View viewById = super.itemView.findViewById(n);
        if (viewById != null) {
            this.mCachedViews.put(n, (Object)viewById);
        }
        return viewById;
    }
    
    public boolean isDividerAllowedAbove() {
        return this.mDividerAllowedAbove;
    }
    
    public boolean isDividerAllowedBelow() {
        return this.mDividerAllowedBelow;
    }
    
    void resetState() {
        final Drawable background = super.itemView.getBackground();
        final Drawable mBackground = this.mBackground;
        if (background != mBackground) {
            ViewCompat.setBackground(super.itemView, mBackground);
        }
        final TextView textView = (TextView)this.findViewById(16908310);
        if (textView != null && this.mTitleTextColors != null && !textView.getTextColors().equals(this.mTitleTextColors)) {
            textView.setTextColor(this.mTitleTextColors);
        }
    }
    
    public void setDividerAllowedAbove(final boolean mDividerAllowedAbove) {
        this.mDividerAllowedAbove = mDividerAllowedAbove;
    }
    
    public void setDividerAllowedBelow(final boolean mDividerAllowedBelow) {
        this.mDividerAllowedBelow = mDividerAllowedBelow;
    }
}
