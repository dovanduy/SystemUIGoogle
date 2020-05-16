// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import android.os.Parcel;
import android.os.Parcelable$Creator;
import android.view.View$BaseSavedState;
import android.graphics.Rect;
import android.os.Parcelable;
import android.view.ViewGroup;
import android.view.View;
import java.util.ArrayList;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.FrameLayout;

class PersistentFocusWrapper extends FrameLayout
{
    private boolean mPersistFocusVertical;
    private int mSelectedPosition;
    
    public PersistentFocusWrapper(final Context context, final AttributeSet set) {
        super(context, set);
        this.mSelectedPosition = -1;
        this.mPersistFocusVertical = true;
    }
    
    private boolean shouldPersistFocusFromDirection(final int n) {
        return (this.mPersistFocusVertical && (n == 33 || n == 130)) || (!this.mPersistFocusVertical && (n == 17 || n == 66));
    }
    
    public void addFocusables(final ArrayList<View> list, final int n, final int n2) {
        if (!this.hasFocus() && this.getGrandChildCount() != 0 && this.shouldPersistFocusFromDirection(n)) {
            list.add((View)this);
        }
        else {
            super.addFocusables((ArrayList)list, n, n2);
        }
    }
    
    int getGrandChildCount() {
        int childCount = 0;
        final ViewGroup viewGroup = (ViewGroup)this.getChildAt(0);
        if (viewGroup != null) {
            childCount = viewGroup.getChildCount();
        }
        return childCount;
    }
    
    protected void onRestoreInstanceState(final Parcelable parcelable) {
        if (!(parcelable instanceof SavedState)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        final SavedState savedState = (SavedState)parcelable;
        this.mSelectedPosition = savedState.mSelectedPosition;
        super.onRestoreInstanceState(savedState.getSuperState());
    }
    
    protected Parcelable onSaveInstanceState() {
        final SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.mSelectedPosition = this.mSelectedPosition;
        return (Parcelable)savedState;
    }
    
    public void requestChildFocus(final View view, View view2) {
        super.requestChildFocus(view, view2);
        while (view2 != null && view2.getParent() != view) {
            view2 = (View)view2.getParent();
        }
        int indexOfChild;
        if (view2 == null) {
            indexOfChild = -1;
        }
        else {
            indexOfChild = ((ViewGroup)view).indexOfChild(view2);
        }
        this.mSelectedPosition = indexOfChild;
    }
    
    public boolean requestFocus(final int n, final Rect rect) {
        final ViewGroup viewGroup = (ViewGroup)this.getChildAt(0);
        if (viewGroup != null) {
            final int mSelectedPosition = this.mSelectedPosition;
            if (mSelectedPosition >= 0 && mSelectedPosition < this.getGrandChildCount() && viewGroup.getChildAt(this.mSelectedPosition).requestFocus(n, rect)) {
                return true;
            }
        }
        return super.requestFocus(n, rect);
    }
    
    static class SavedState extends View$BaseSavedState
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        int mSelectedPosition;
        
        static {
            CREATOR = (Parcelable$Creator)new Parcelable$Creator<SavedState>() {
                public SavedState createFromParcel(final Parcel parcel) {
                    return new SavedState(parcel);
                }
                
                public SavedState[] newArray(final int n) {
                    return new SavedState[n];
                }
            };
        }
        
        SavedState(final Parcel parcel) {
            super(parcel);
            this.mSelectedPosition = parcel.readInt();
        }
        
        SavedState(final Parcelable parcelable) {
            super(parcelable);
        }
        
        public void writeToParcel(final Parcel parcel, final int n) {
            super.writeToParcel(parcel, n);
            parcel.writeInt(this.mSelectedPosition);
        }
    }
}
