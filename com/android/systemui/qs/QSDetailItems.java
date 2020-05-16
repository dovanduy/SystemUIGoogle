// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import android.os.Message;
import android.os.Looper;
import android.os.Handler;
import android.graphics.drawable.Drawable;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.R$drawable;
import android.view.View$OnClickListener;
import android.text.TextUtils;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import com.android.systemui.FontSizeUtils;
import android.content.res.Configuration;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.android.systemui.R$dimen;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.View;
import android.content.Context;
import android.widget.FrameLayout;

public class QSDetailItems extends FrameLayout
{
    private static final boolean DEBUG;
    private final Adapter mAdapter;
    private Callback mCallback;
    private final Context mContext;
    private View mEmpty;
    private ImageView mEmptyIcon;
    private TextView mEmptyText;
    private final H mHandler;
    private AutoSizingList mItemList;
    private Item[] mItems;
    private boolean mItemsVisible;
    private final int mQsDetailIconOverlaySize;
    private String mTag;
    
    static {
        DEBUG = Log.isLoggable("QSDetailItems", 3);
    }
    
    public QSDetailItems(final Context mContext, final AttributeSet set) {
        super(mContext, set);
        this.mHandler = new H();
        this.mAdapter = new Adapter();
        this.mItemsVisible = true;
        this.mContext = mContext;
        this.mTag = "QSDetailItems";
        this.mQsDetailIconOverlaySize = (int)this.getResources().getDimension(R$dimen.qs_detail_icon_overlay_size);
    }
    
    public static QSDetailItems convertOrInflate(final Context context, final View view, final ViewGroup viewGroup) {
        if (view instanceof QSDetailItems) {
            return (QSDetailItems)view;
        }
        return (QSDetailItems)LayoutInflater.from(context).inflate(R$layout.qs_detail_items, viewGroup, false);
    }
    
    private void handleSetCallback(final Callback mCallback) {
        this.mCallback = mCallback;
    }
    
    private void handleSetItems(final Item[] mItems) {
        final int n = 0;
        int length;
        if (mItems != null) {
            length = mItems.length;
        }
        else {
            length = 0;
        }
        final View mEmpty = this.mEmpty;
        int visibility;
        if (length == 0) {
            visibility = 0;
        }
        else {
            visibility = 8;
        }
        mEmpty.setVisibility(visibility);
        final AutoSizingList mItemList = this.mItemList;
        int visibility2 = n;
        if (length == 0) {
            visibility2 = 8;
        }
        mItemList.setVisibility(visibility2);
        this.mItems = mItems;
        this.mAdapter.notifyDataSetChanged();
    }
    
    private void handleSetItemsVisible(final boolean mItemsVisible) {
        if (this.mItemsVisible == mItemsVisible) {
            return;
        }
        this.mItemsVisible = mItemsVisible;
        for (int i = 0; i < this.mItemList.getChildCount(); ++i) {
            final View child = this.mItemList.getChildAt(i);
            int visibility;
            if (this.mItemsVisible) {
                visibility = 0;
            }
            else {
                visibility = 4;
            }
            child.setVisibility(visibility);
        }
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (QSDetailItems.DEBUG) {
            Log.d(this.mTag, "onAttachedToWindow");
        }
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        FontSizeUtils.updateFontSize(this.mEmptyText, R$dimen.qs_detail_empty_text_size);
        for (int childCount = this.mItemList.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.mItemList.getChildAt(i);
            FontSizeUtils.updateFontSize(child, 16908310, R$dimen.qs_detail_item_primary_text_size);
            FontSizeUtils.updateFontSize(child, 16908304, R$dimen.qs_detail_item_secondary_text_size);
        }
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (QSDetailItems.DEBUG) {
            Log.d(this.mTag, "onDetachedFromWindow");
        }
        this.mCallback = null;
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        (this.mItemList = (AutoSizingList)this.findViewById(16908298)).setVisibility(8);
        this.mItemList.setAdapter((ListAdapter)this.mAdapter);
        (this.mEmpty = this.findViewById(16908292)).setVisibility(8);
        this.mEmptyText = (TextView)this.mEmpty.findViewById(16908310);
        this.mEmptyIcon = (ImageView)this.mEmpty.findViewById(16908294);
    }
    
    public void setCallback(final Callback callback) {
        this.mHandler.removeMessages(2);
        this.mHandler.obtainMessage(2, (Object)callback).sendToTarget();
    }
    
    public void setEmptyState(final int n, final int n2) {
        this.mEmptyIcon.post((Runnable)new _$$Lambda$QSDetailItems$8UkcDK0xyJROkQ0Pv0OF8HNZO94(this, n, n2));
    }
    
    public void setItems(final Item[] array) {
        this.mHandler.removeMessages(1);
        this.mHandler.obtainMessage(1, (Object)array).sendToTarget();
    }
    
    public void setItemsVisible(final boolean b) {
        this.mHandler.removeMessages(3);
        this.mHandler.obtainMessage(3, (int)(b ? 1 : 0), 0).sendToTarget();
    }
    
    public void setTagSuffix(final String str) {
        final StringBuilder sb = new StringBuilder();
        sb.append("QSDetailItems.");
        sb.append(str);
        this.mTag = sb.toString();
    }
    
    private class Adapter extends BaseAdapter
    {
        public int getCount() {
            int length;
            if (QSDetailItems.this.mItems != null) {
                length = QSDetailItems.this.mItems.length;
            }
            else {
                length = 0;
            }
            return length;
        }
        
        public Object getItem(final int n) {
            return QSDetailItems.this.mItems[n];
        }
        
        public long getItemId(final int n) {
            return 0L;
        }
        
        public View getView(int visibility, final View view, final ViewGroup viewGroup) {
            final Item item = QSDetailItems.this.mItems[visibility];
            View inflate = view;
            if (view == null) {
                inflate = LayoutInflater.from(QSDetailItems.this.mContext).inflate(R$layout.qs_detail_item, viewGroup, false);
            }
            if (QSDetailItems.this.mItemsVisible) {
                visibility = 0;
            }
            else {
                visibility = 4;
            }
            inflate.setVisibility(visibility);
            final ImageView imageView = (ImageView)inflate.findViewById(16908294);
            final QSTile.Icon icon = item.icon;
            if (icon != null) {
                imageView.setImageDrawable(icon.getDrawable(imageView.getContext()));
            }
            else {
                imageView.setImageResource(item.iconResId);
            }
            imageView.getOverlay().clear();
            final Drawable overlay = item.overlay;
            if (overlay != null) {
                overlay.setBounds(0, 0, QSDetailItems.this.mQsDetailIconOverlaySize, QSDetailItems.this.mQsDetailIconOverlaySize);
                imageView.getOverlay().add(item.overlay);
            }
            final TextView textView = (TextView)inflate.findViewById(16908310);
            textView.setText(item.line1);
            final TextView textView2 = (TextView)inflate.findViewById(16908304);
            final boolean b = TextUtils.isEmpty(item.line2) ^ true;
            if (b) {
                visibility = 1;
            }
            else {
                visibility = 2;
            }
            textView.setMaxLines(visibility);
            if (b) {
                visibility = 0;
            }
            else {
                visibility = 8;
            }
            textView2.setVisibility(visibility);
            CharSequence line2;
            if (b) {
                line2 = item.line2;
            }
            else {
                line2 = null;
            }
            textView2.setText(line2);
            inflate.setOnClickListener((View$OnClickListener)new View$OnClickListener() {
                public void onClick(final View view) {
                    if (QSDetailItems.this.mCallback != null) {
                        QSDetailItems.this.mCallback.onDetailItemClick(item);
                    }
                }
            });
            final ImageView imageView2 = (ImageView)inflate.findViewById(16908296);
            if (item.canDisconnect) {
                imageView2.setImageResource(R$drawable.ic_qs_cancel);
                imageView2.setVisibility(0);
                imageView2.setClickable(true);
                imageView2.setOnClickListener((View$OnClickListener)new View$OnClickListener() {
                    public void onClick(final View view) {
                        if (QSDetailItems.this.mCallback != null) {
                            QSDetailItems.this.mCallback.onDetailItemDisconnect(item);
                        }
                    }
                });
            }
            else if (item.icon2 != -1) {
                imageView2.setVisibility(0);
                imageView2.setImageResource(item.icon2);
                imageView2.setClickable(false);
            }
            else {
                imageView2.setVisibility(8);
            }
            return inflate;
        }
    }
    
    public interface Callback
    {
        void onDetailItemClick(final Item p0);
        
        void onDetailItemDisconnect(final Item p0);
    }
    
    private class H extends Handler
    {
        public H() {
            super(Looper.getMainLooper());
        }
        
        public void handleMessage(final Message message) {
            final int what = message.what;
            boolean b = true;
            if (what == 1) {
                QSDetailItems.this.handleSetItems((Item[])message.obj);
            }
            else if (what == 2) {
                QSDetailItems.this.handleSetCallback((Callback)message.obj);
            }
            else if (what == 3) {
                final QSDetailItems this$0 = QSDetailItems.this;
                if (message.arg1 == 0) {
                    b = false;
                }
                this$0.handleSetItemsVisible(b);
            }
        }
    }
    
    public static class Item
    {
        public boolean canDisconnect;
        public QSTile.Icon icon;
        public int icon2;
        public int iconResId;
        public CharSequence line1;
        public CharSequence line2;
        public Drawable overlay;
        public Object tag;
        
        public Item() {
            this.icon2 = -1;
        }
    }
}
