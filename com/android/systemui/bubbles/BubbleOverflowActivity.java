// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.bubbles;

import android.content.res.TypedArray;
import com.android.systemui.R$drawable;
import java.util.Collection;
import android.content.res.Resources;
import java.util.function.Consumer;
import java.util.Objects;
import com.android.systemui.R$dimen;
import android.util.DisplayMetrics;
import androidx.recyclerview.widget.GridLayoutManager;
import com.android.systemui.R$integer;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import android.os.Bundle;
import java.util.ArrayList;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.app.Activity;

public class BubbleOverflowActivity extends Activity
{
    private BubbleOverflowAdapter mAdapter;
    private BubbleController mBubbleController;
    private LinearLayout mEmptyState;
    private ImageView mEmptyStateImage;
    private List<Bubble> mOverflowBubbles;
    private RecyclerView mRecyclerView;
    
    public BubbleOverflowActivity(final BubbleController mBubbleController) {
        this.mOverflowBubbles = new ArrayList<Bubble>();
        this.mBubbleController = mBubbleController;
    }
    
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.setContentView(R$layout.bubble_overflow_activity);
        this.setBackgroundColor();
        this.mEmptyState = (LinearLayout)this.findViewById(R$id.bubble_overflow_empty_state);
        this.mRecyclerView = (RecyclerView)this.findViewById(R$id.bubble_overflow_recycler);
        this.mEmptyStateImage = (ImageView)this.findViewById(R$id.bubble_overflow_empty_state_image);
        final Resources resources = this.getResources();
        final int integer = resources.getInteger(R$integer.bubbles_overflow_columns);
        this.mRecyclerView.setLayoutManager((RecyclerView.LayoutManager)new GridLayoutManager(this.getApplicationContext(), integer));
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int n = (displayMetrics.widthPixels - resources.getDimensionPixelSize(R$dimen.bubble_overflow_padding)) / integer;
        final int n2 = (resources.getDimensionPixelSize(R$dimen.bubble_overflow_height) - resources.getDimensionPixelSize(R$dimen.bubble_overflow_padding)) / (int)Math.ceil(resources.getInteger(R$integer.bubbles_max_overflow) / (double)integer);
        final List<Bubble> mOverflowBubbles = this.mOverflowBubbles;
        final BubbleController mBubbleController = this.mBubbleController;
        Objects.requireNonNull(mBubbleController);
        final BubbleOverflowAdapter bubbleOverflowAdapter = new BubbleOverflowAdapter(mOverflowBubbles, new _$$Lambda$HcbZA8v8RHJPrNTsZB0H54PCimo(mBubbleController), n, n2);
        this.mAdapter = bubbleOverflowAdapter;
        this.mRecyclerView.setAdapter((RecyclerView.Adapter)bubbleOverflowAdapter);
        this.onDataChanged(this.mBubbleController.getOverflowBubbles());
        this.mBubbleController.setOverflowCallback(new _$$Lambda$BubbleOverflowActivity$bBXw1pgL9xyN0c4JMlrR5U428HM(this));
        this.onThemeChanged();
    }
    
    void onDataChanged(final List<Bubble> list) {
        this.mOverflowBubbles.clear();
        this.mOverflowBubbles.addAll(list);
        ((RecyclerView.Adapter)this.mAdapter).notifyDataSetChanged();
        if (this.mOverflowBubbles.isEmpty()) {
            this.mEmptyState.setVisibility(0);
        }
        else {
            this.mEmptyState.setVisibility(8);
        }
    }
    
    public void onDestroy() {
        super.onDestroy();
    }
    
    public void onPause() {
        super.onPause();
    }
    
    public void onRestart() {
        super.onRestart();
    }
    
    public void onResume() {
        super.onResume();
        this.onThemeChanged();
    }
    
    public void onStart() {
        super.onStart();
    }
    
    public void onStop() {
        super.onStop();
    }
    
    void onThemeChanged() {
        final int n = this.getResources().getConfiguration().uiMode & 0x30;
        if (n != 16) {
            if (n == 32) {
                this.mEmptyStateImage.setImageDrawable(this.getResources().getDrawable(R$drawable.ic_empty_bubble_overflow_dark));
            }
        }
        else {
            this.mEmptyStateImage.setImageDrawable(this.getResources().getDrawable(R$drawable.ic_empty_bubble_overflow_light));
        }
    }
    
    void setBackgroundColor() {
        final TypedArray obtainStyledAttributes = this.getApplicationContext().obtainStyledAttributes(new int[] { 16844002 });
        final int color = obtainStyledAttributes.getColor(0, -1);
        obtainStyledAttributes.recycle();
        this.findViewById(16908290).setBackgroundColor(color);
    }
}
