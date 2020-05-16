// 
// Decompiled by Procyon v0.5.36
// 

package androidx.mediarouter.app;

import android.widget.ProgressBar;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Iterator;
import androidx.mediarouter.R$string;
import android.view.ViewGroup;
import android.net.Uri;
import java.io.IOException;
import android.util.Log;
import android.view.LayoutInflater;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import java.util.Comparator;
import java.util.Collections;
import java.util.Collection;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;
import android.view.View$OnClickListener;
import androidx.mediarouter.R$id;
import java.util.ArrayList;
import android.app.Dialog;
import androidx.mediarouter.R$layout;
import android.os.Bundle;
import androidx.mediarouter.R$integer;
import android.os.Message;
import androidx.mediarouter.media.MediaRouteSelector;
import java.util.List;
import androidx.mediarouter.media.MediaRouter;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Handler;
import android.content.Context;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatDialog;

public class MediaRouteDynamicChooserDialog extends AppCompatDialog
{
    private RecyclerAdapter mAdapter;
    private boolean mAttachedToWindow;
    private final MediaRouterCallback mCallback;
    private ImageButton mCloseButton;
    Context mContext;
    private final Handler mHandler;
    private long mLastUpdateTime;
    private RecyclerView mRecyclerView;
    final MediaRouter mRouter;
    List<MediaRouter.RouteInfo> mRoutes;
    private MediaRouteSelector mSelector;
    private long mUpdateRoutesDelayMs;
    
    public MediaRouteDynamicChooserDialog(final Context context) {
        this(context, 0);
    }
    
    public MediaRouteDynamicChooserDialog(Context mContext, final int n) {
        mContext = MediaRouterThemeHelper.createThemedDialogContext(mContext, n, false);
        super(mContext, MediaRouterThemeHelper.createThemedDialogStyle(mContext));
        this.mSelector = MediaRouteSelector.EMPTY;
        this.mHandler = new Handler() {
            public void handleMessage(final Message message) {
                if (message.what == 1) {
                    MediaRouteDynamicChooserDialog.this.updateRoutes((List<MediaRouter.RouteInfo>)message.obj);
                }
            }
        };
        mContext = this.getContext();
        this.mRouter = MediaRouter.getInstance(mContext);
        this.mCallback = new MediaRouterCallback();
        this.mContext = mContext;
        this.mUpdateRoutesDelayMs = mContext.getResources().getInteger(R$integer.mr_update_routes_delay_ms);
    }
    
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mAttachedToWindow = true;
        this.mRouter.addCallback(this.mSelector, (MediaRouter.Callback)this.mCallback, 1);
        this.refreshRoutes();
    }
    
    @Override
    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.setContentView(R$layout.mr_picker_dialog);
        MediaRouterThemeHelper.setDialogBackgroundColor(this.mContext, this);
        this.mRoutes = new ArrayList<MediaRouter.RouteInfo>();
        (this.mCloseButton = this.findViewById(R$id.mr_picker_close_button)).setOnClickListener((View$OnClickListener)new View$OnClickListener() {
            public void onClick(final View view) {
                MediaRouteDynamicChooserDialog.this.dismiss();
            }
        });
        this.mAdapter = new RecyclerAdapter();
        (this.mRecyclerView = this.findViewById(R$id.mr_picker_list)).setAdapter((RecyclerView.Adapter)this.mAdapter);
        this.mRecyclerView.setLayoutManager((RecyclerView.LayoutManager)new LinearLayoutManager(this.mContext));
        this.updateLayout();
    }
    
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mAttachedToWindow = false;
        this.mRouter.removeCallback((MediaRouter.Callback)this.mCallback);
        this.mHandler.removeMessages(1);
    }
    
    public boolean onFilterRoute(final MediaRouter.RouteInfo routeInfo) {
        return !routeInfo.isDefaultOrBluetooth() && routeInfo.isEnabled() && routeInfo.matchesSelector(this.mSelector);
    }
    
    public void onFilterRoutes(final List<MediaRouter.RouteInfo> list) {
        int size = list.size();
        while (true) {
            final int n = size - 1;
            if (size <= 0) {
                break;
            }
            if (!this.onFilterRoute(list.get(n))) {
                list.remove(n);
            }
            size = n;
        }
    }
    
    public void refreshRoutes() {
        if (this.mAttachedToWindow) {
            final ArrayList<Object> list = new ArrayList<Object>(this.mRouter.getRoutes());
            this.onFilterRoutes((List<MediaRouter.RouteInfo>)list);
            Collections.sort(list, (Comparator<? super Object>)RouteComparator.sInstance);
            if (SystemClock.uptimeMillis() - this.mLastUpdateTime >= this.mUpdateRoutesDelayMs) {
                this.updateRoutes((List<MediaRouter.RouteInfo>)list);
            }
            else {
                this.mHandler.removeMessages(1);
                final Handler mHandler = this.mHandler;
                mHandler.sendMessageAtTime(mHandler.obtainMessage(1, (Object)list), this.mLastUpdateTime + this.mUpdateRoutesDelayMs);
            }
        }
    }
    
    public void setRouteSelector(final MediaRouteSelector mSelector) {
        if (mSelector != null) {
            if (!this.mSelector.equals(mSelector)) {
                this.mSelector = mSelector;
                if (this.mAttachedToWindow) {
                    this.mRouter.removeCallback((MediaRouter.Callback)this.mCallback);
                    this.mRouter.addCallback(mSelector, (MediaRouter.Callback)this.mCallback, 1);
                }
                this.refreshRoutes();
            }
            return;
        }
        throw new IllegalArgumentException("selector must not be null");
    }
    
    void updateLayout() {
        this.getWindow().setLayout(MediaRouteDialogHelper.getDialogWidthForDynamicGroup(this.mContext), MediaRouteDialogHelper.getDialogHeight(this.mContext));
    }
    
    void updateRoutes(final List<MediaRouter.RouteInfo> list) {
        this.mLastUpdateTime = SystemClock.uptimeMillis();
        this.mRoutes.clear();
        this.mRoutes.addAll(list);
        this.mAdapter.rebuildItems();
    }
    
    private final class MediaRouterCallback extends Callback
    {
        MediaRouterCallback() {
        }
        
        @Override
        public void onRouteAdded(final MediaRouter mediaRouter, final RouteInfo routeInfo) {
            MediaRouteDynamicChooserDialog.this.refreshRoutes();
        }
        
        @Override
        public void onRouteChanged(final MediaRouter mediaRouter, final RouteInfo routeInfo) {
            MediaRouteDynamicChooserDialog.this.refreshRoutes();
        }
        
        @Override
        public void onRouteRemoved(final MediaRouter mediaRouter, final RouteInfo routeInfo) {
            MediaRouteDynamicChooserDialog.this.refreshRoutes();
        }
        
        @Override
        public void onRouteSelected(final MediaRouter mediaRouter, final RouteInfo routeInfo) {
            MediaRouteDynamicChooserDialog.this.dismiss();
        }
    }
    
    private final class RecyclerAdapter extends Adapter<ViewHolder>
    {
        private final Drawable mDefaultIcon;
        private final LayoutInflater mInflater;
        private final ArrayList<Item> mItems;
        private final Drawable mSpeakerGroupIcon;
        private final Drawable mSpeakerIcon;
        private final Drawable mTvIcon;
        final /* synthetic */ MediaRouteDynamicChooserDialog this$0;
        
        RecyclerAdapter() {
            this.mItems = new ArrayList<Item>();
            this.mInflater = LayoutInflater.from(MediaRouteDynamicChooserDialog.this.mContext);
            this.mDefaultIcon = MediaRouterThemeHelper.getDefaultDrawableIcon(MediaRouteDynamicChooserDialog.this.mContext);
            this.mTvIcon = MediaRouterThemeHelper.getTvDrawableIcon(MediaRouteDynamicChooserDialog.this.mContext);
            this.mSpeakerIcon = MediaRouterThemeHelper.getSpeakerDrawableIcon(MediaRouteDynamicChooserDialog.this.mContext);
            this.mSpeakerGroupIcon = MediaRouterThemeHelper.getSpeakerGroupDrawableIcon(MediaRouteDynamicChooserDialog.this.mContext);
            this.rebuildItems();
        }
        
        private Drawable getDefaultIconDrawable(final MediaRouter.RouteInfo routeInfo) {
            final int deviceType = routeInfo.getDeviceType();
            if (deviceType == 1) {
                return this.mTvIcon;
            }
            if (deviceType == 2) {
                return this.mSpeakerIcon;
            }
            if (routeInfo.isGroup()) {
                return this.mSpeakerGroupIcon;
            }
            return this.mDefaultIcon;
        }
        
        Drawable getIconDrawable(final MediaRouter.RouteInfo routeInfo) {
            final Uri iconUri = routeInfo.getIconUri();
            if (iconUri != null) {
                try {
                    final Drawable fromStream = Drawable.createFromStream(MediaRouteDynamicChooserDialog.this.mContext.getContentResolver().openInputStream(iconUri), (String)null);
                    if (fromStream != null) {
                        return fromStream;
                    }
                }
                catch (IOException ex) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Failed to load ");
                    sb.append(iconUri);
                    Log.w("RecyclerAdapter", sb.toString(), (Throwable)ex);
                }
            }
            return this.getDefaultIconDrawable(routeInfo);
        }
        
        public Item getItem(final int index) {
            return this.mItems.get(index);
        }
        
        @Override
        public int getItemCount() {
            return this.mItems.size();
        }
        
        @Override
        public int getItemViewType(final int index) {
            return this.mItems.get(index).getType();
        }
        
        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int n) {
            final int itemViewType = this.getItemViewType(n);
            final Item item = this.getItem(n);
            if (itemViewType != 1) {
                if (itemViewType != 2) {
                    Log.w("RecyclerAdapter", "Cannot bind item to ViewHolder because of wrong view type");
                }
                else {
                    ((RouteViewHolder)viewHolder).bindRouteView(item);
                }
            }
            else {
                ((HeaderViewHolder)viewHolder).bindHeaderView(item);
            }
        }
        
        @Override
        public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int n) {
            if (n == 1) {
                return new HeaderViewHolder(this.mInflater.inflate(R$layout.mr_picker_header_item, viewGroup, false));
            }
            if (n != 2) {
                Log.w("RecyclerAdapter", "Cannot create ViewHolder because of wrong view type");
                return null;
            }
            return new RouteViewHolder(this.mInflater.inflate(R$layout.mr_picker_route_item, viewGroup, false));
        }
        
        void rebuildItems() {
            this.mItems.clear();
            this.mItems.add(new Item(MediaRouteDynamicChooserDialog.this.mContext.getString(R$string.mr_chooser_title)));
            final Iterator<MediaRouter.RouteInfo> iterator = MediaRouteDynamicChooserDialog.this.mRoutes.iterator();
            while (iterator.hasNext()) {
                this.mItems.add(new Item(iterator.next()));
            }
            ((RecyclerView.Adapter)this).notifyDataSetChanged();
        }
        
        private class HeaderViewHolder extends ViewHolder
        {
            TextView mTextView;
            
            HeaderViewHolder(final RecyclerAdapter recyclerAdapter, final View view) {
                super(view);
                this.mTextView = (TextView)view.findViewById(R$id.mr_picker_header_name);
            }
            
            public void bindHeaderView(final Item item) {
                this.mTextView.setText((CharSequence)item.getData().toString());
            }
        }
        
        private class Item
        {
            private final Object mData;
            private final int mType;
            
            Item(final RecyclerAdapter recyclerAdapter, final Object mData) {
                this.mData = mData;
                if (mData instanceof String) {
                    this.mType = 1;
                }
                else if (mData instanceof MediaRouter.RouteInfo) {
                    this.mType = 2;
                }
                else {
                    this.mType = 0;
                    Log.w("RecyclerAdapter", "Wrong type of data passed to Item constructor");
                }
            }
            
            public Object getData() {
                return this.mData;
            }
            
            public int getType() {
                return this.mType;
            }
        }
        
        private class RouteViewHolder extends ViewHolder
        {
            final ImageView mImageView;
            final View mItemView;
            final ProgressBar mProgressBar;
            final TextView mTextView;
            
            RouteViewHolder(final View mItemView) {
                super(mItemView);
                this.mItemView = mItemView;
                this.mImageView = (ImageView)mItemView.findViewById(R$id.mr_picker_route_icon);
                this.mProgressBar = (ProgressBar)mItemView.findViewById(R$id.mr_picker_route_progress_bar);
                this.mTextView = (TextView)mItemView.findViewById(R$id.mr_picker_route_name);
                MediaRouterThemeHelper.setIndeterminateProgressBarColor(RecyclerAdapter.this.this$0.mContext, this.mProgressBar);
            }
            
            public void bindRouteView(final Item item) {
                final MediaRouter.RouteInfo routeInfo = (MediaRouter.RouteInfo)item.getData();
                this.mItemView.setVisibility(0);
                this.mProgressBar.setVisibility(4);
                this.mItemView.setOnClickListener((View$OnClickListener)new View$OnClickListener() {
                    public void onClick(final View view) {
                        routeInfo.select();
                        RouteViewHolder.this.mImageView.setVisibility(4);
                        RouteViewHolder.this.mProgressBar.setVisibility(0);
                    }
                });
                this.mTextView.setText((CharSequence)routeInfo.getName());
                this.mImageView.setImageDrawable(RecyclerAdapter.this.getIconDrawable(routeInfo));
            }
        }
    }
    
    static final class RouteComparator implements Comparator<MediaRouter.RouteInfo>
    {
        public static final RouteComparator sInstance;
        
        static {
            sInstance = new RouteComparator();
        }
        
        @Override
        public int compare(final MediaRouter.RouteInfo routeInfo, final MediaRouter.RouteInfo routeInfo2) {
            return routeInfo.getName().compareToIgnoreCase(routeInfo2.getName());
        }
    }
}
