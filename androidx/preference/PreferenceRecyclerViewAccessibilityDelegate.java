// 
// Decompiled by Procyon v0.5.36
// 

package androidx.preference;

import android.os.Bundle;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.recyclerview.widget.RecyclerViewAccessibilityDelegate;

@Deprecated
public class PreferenceRecyclerViewAccessibilityDelegate extends RecyclerViewAccessibilityDelegate
{
    final AccessibilityDelegateCompat mDefaultItemDelegate;
    final AccessibilityDelegateCompat mItemDelegate;
    final RecyclerView mRecyclerView;
    
    public PreferenceRecyclerViewAccessibilityDelegate(final RecyclerView mRecyclerView) {
        super(mRecyclerView);
        this.mDefaultItemDelegate = super.getItemDelegate();
        this.mItemDelegate = new AccessibilityDelegateCompat() {
            @Override
            public void onInitializeAccessibilityNodeInfo(final View view, final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                PreferenceRecyclerViewAccessibilityDelegate.this.mDefaultItemDelegate.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
                final int childAdapterPosition = PreferenceRecyclerViewAccessibilityDelegate.this.mRecyclerView.getChildAdapterPosition(view);
                final RecyclerView.Adapter adapter = PreferenceRecyclerViewAccessibilityDelegate.this.mRecyclerView.getAdapter();
                if (!(adapter instanceof PreferenceGroupAdapter)) {
                    return;
                }
                final Preference item = ((PreferenceGroupAdapter)adapter).getItem(childAdapterPosition);
                if (item == null) {
                    return;
                }
                item.onInitializeAccessibilityNodeInfo(accessibilityNodeInfoCompat);
            }
            
            @Override
            public boolean performAccessibilityAction(final View view, final int n, final Bundle bundle) {
                return PreferenceRecyclerViewAccessibilityDelegate.this.mDefaultItemDelegate.performAccessibilityAction(view, n, bundle);
            }
        };
        this.mRecyclerView = mRecyclerView;
    }
    
    @Override
    public AccessibilityDelegateCompat getItemDelegate() {
        return this.mItemDelegate;
    }
}
