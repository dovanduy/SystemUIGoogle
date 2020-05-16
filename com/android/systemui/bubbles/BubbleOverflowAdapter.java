// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.bubbles;

import com.android.systemui.R$id;
import android.widget.TextView;
import android.view.ViewGroup$LayoutParams;
import android.widget.LinearLayout$LayoutParams;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.view.ViewGroup;
import android.view.View$OnClickListener;
import android.view.View;
import java.util.function.Consumer;
import java.util.List;
import androidx.recyclerview.widget.RecyclerView;

class BubbleOverflowAdapter extends Adapter<ViewHolder>
{
    private List<Bubble> mBubbles;
    private int mHeight;
    private Consumer<Bubble> mPromoteBubbleFromOverflow;
    private int mWidth;
    
    public BubbleOverflowAdapter(final List<Bubble> mBubbles, final Consumer<Bubble> mPromoteBubbleFromOverflow, final int mWidth, final int mHeight) {
        this.mBubbles = mBubbles;
        this.mPromoteBubbleFromOverflow = mPromoteBubbleFromOverflow;
        this.mWidth = mWidth;
        this.mHeight = mHeight;
    }
    
    @Override
    public int getItemCount() {
        return this.mBubbles.size();
    }
    
    public void onBindViewHolder(final ViewHolder viewHolder, final int n) {
        final Bubble renderedBubble = this.mBubbles.get(n);
        viewHolder.iconView.setRenderedBubble(renderedBubble);
        viewHolder.iconView.setOnClickListener((View$OnClickListener)new _$$Lambda$BubbleOverflowAdapter$MgnimWNCDitXqbPJN2vzJpXXigU(this, renderedBubble));
        final Bubble.FlyoutMessage flyoutMessage = renderedBubble.getFlyoutMessage();
        if (flyoutMessage != null) {
            final CharSequence senderName = flyoutMessage.senderName;
            if (senderName != null) {
                viewHolder.textView.setText(senderName);
                return;
            }
        }
        viewHolder.textView.setText((CharSequence)renderedBubble.getAppName());
    }
    
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int n) {
        final LinearLayout linearLayout = (LinearLayout)LayoutInflater.from(viewGroup.getContext()).inflate(R$layout.bubble_overflow_view, viewGroup, false);
        final LinearLayout$LayoutParams layoutParams = new LinearLayout$LayoutParams(-2, -2);
        layoutParams.width = this.mWidth;
        layoutParams.height = this.mHeight;
        linearLayout.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
        return new ViewHolder(linearLayout);
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public BadgedImageView iconView;
        public TextView textView;
        
        public ViewHolder(final LinearLayout linearLayout) {
            super((View)linearLayout);
            this.iconView = (BadgedImageView)linearLayout.findViewById(R$id.bubble_view);
            this.textView = (TextView)linearLayout.findViewById(R$id.bubble_view_name);
        }
    }
}
