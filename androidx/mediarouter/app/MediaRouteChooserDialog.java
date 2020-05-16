// 
// Decompiled by Procyon v0.5.36
// 

package androidx.mediarouter.app;

import android.widget.AdapterView;
import android.widget.ImageView;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.view.View;
import android.net.Uri;
import java.io.IOException;
import android.util.Log;
import android.content.res.TypedArray;
import androidx.mediarouter.R$attr;
import android.view.LayoutInflater;
import android.graphics.drawable.Drawable;
import android.widget.ArrayAdapter;
import android.os.SystemClock;
import java.util.Comparator;
import java.util.Collections;
import java.util.Collection;
import android.widget.AdapterView$OnItemClickListener;
import android.widget.ListAdapter;
import androidx.mediarouter.R$id;
import androidx.mediarouter.R$layout;
import android.os.Bundle;
import java.util.List;
import android.os.Message;
import android.content.Context;
import android.widget.TextView;
import androidx.mediarouter.media.MediaRouteSelector;
import java.util.ArrayList;
import androidx.mediarouter.media.MediaRouter;
import android.widget.ListView;
import android.os.Handler;
import androidx.appcompat.app.AppCompatDialog;

public class MediaRouteChooserDialog extends AppCompatDialog
{
    private RouteAdapter mAdapter;
    private boolean mAttachedToWindow;
    private final MediaRouterCallback mCallback;
    private final Handler mHandler;
    private long mLastUpdateTime;
    private ListView mListView;
    private final MediaRouter mRouter;
    private ArrayList<MediaRouter.RouteInfo> mRoutes;
    private MediaRouteSelector mSelector;
    private TextView mTitleView;
    
    public MediaRouteChooserDialog(final Context context) {
        this(context, 0);
    }
    
    public MediaRouteChooserDialog(Context themedDialogContext, final int n) {
        themedDialogContext = MediaRouterThemeHelper.createThemedDialogContext(themedDialogContext, n, false);
        super(themedDialogContext, MediaRouterThemeHelper.createThemedDialogStyle(themedDialogContext));
        this.mSelector = MediaRouteSelector.EMPTY;
        this.mHandler = new Handler() {
            public void handleMessage(final Message message) {
                if (message.what == 1) {
                    MediaRouteChooserDialog.this.updateRoutes((List<MediaRouter.RouteInfo>)message.obj);
                }
            }
        };
        this.mRouter = MediaRouter.getInstance(this.getContext());
        this.mCallback = new MediaRouterCallback();
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
        this.setContentView(R$layout.mr_chooser_dialog);
        this.mRoutes = new ArrayList<MediaRouter.RouteInfo>();
        this.mAdapter = new RouteAdapter(this.getContext(), this.mRoutes);
        (this.mListView = this.findViewById(R$id.mr_chooser_list)).setAdapter((ListAdapter)this.mAdapter);
        this.mListView.setOnItemClickListener((AdapterView$OnItemClickListener)this.mAdapter);
        this.mListView.setEmptyView(this.findViewById(16908292));
        this.mTitleView = this.findViewById(R$id.mr_chooser_title);
        this.updateLayout();
    }
    
    public void onDetachedFromWindow() {
        this.mAttachedToWindow = false;
        this.mRouter.removeCallback((MediaRouter.Callback)this.mCallback);
        this.mHandler.removeMessages(1);
        super.onDetachedFromWindow();
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
            if (SystemClock.uptimeMillis() - this.mLastUpdateTime >= 300L) {
                this.updateRoutes((List<MediaRouter.RouteInfo>)list);
            }
            else {
                this.mHandler.removeMessages(1);
                final Handler mHandler = this.mHandler;
                mHandler.sendMessageAtTime(mHandler.obtainMessage(1, (Object)list), this.mLastUpdateTime + 300L);
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
    
    @Override
    public void setTitle(final int text) {
        this.mTitleView.setText(text);
    }
    
    @Override
    public void setTitle(final CharSequence text) {
        this.mTitleView.setText(text);
    }
    
    void updateLayout() {
        this.getWindow().setLayout(MediaRouteDialogHelper.getDialogWidth(this.getContext()), -2);
    }
    
    void updateRoutes(final List<MediaRouter.RouteInfo> c) {
        this.mLastUpdateTime = SystemClock.uptimeMillis();
        this.mRoutes.clear();
        this.mRoutes.addAll(c);
        this.mAdapter.notifyDataSetChanged();
    }
    
    private final class MediaRouterCallback extends Callback
    {
        MediaRouterCallback() {
        }
        
        @Override
        public void onRouteAdded(final MediaRouter mediaRouter, final RouteInfo routeInfo) {
            MediaRouteChooserDialog.this.refreshRoutes();
        }
        
        @Override
        public void onRouteChanged(final MediaRouter mediaRouter, final RouteInfo routeInfo) {
            MediaRouteChooserDialog.this.refreshRoutes();
        }
        
        @Override
        public void onRouteRemoved(final MediaRouter mediaRouter, final RouteInfo routeInfo) {
            MediaRouteChooserDialog.this.refreshRoutes();
        }
        
        @Override
        public void onRouteSelected(final MediaRouter mediaRouter, final RouteInfo routeInfo) {
            MediaRouteChooserDialog.this.dismiss();
        }
    }
    
    private final class RouteAdapter extends ArrayAdapter<MediaRouter.RouteInfo> implements AdapterView$OnItemClickListener
    {
        private final Drawable mDefaultIcon;
        private final LayoutInflater mInflater;
        private final Drawable mSpeakerGroupIcon;
        private final Drawable mSpeakerIcon;
        private final Drawable mTvIcon;
        
        public RouteAdapter(final Context context, final List<MediaRouter.RouteInfo> list) {
            super(context, 0, (List)list);
            this.mInflater = LayoutInflater.from(context);
            final TypedArray obtainStyledAttributes = this.getContext().obtainStyledAttributes(new int[] { R$attr.mediaRouteDefaultIconDrawable, R$attr.mediaRouteTvIconDrawable, R$attr.mediaRouteSpeakerIconDrawable, R$attr.mediaRouteSpeakerGroupIconDrawable });
            this.mDefaultIcon = obtainStyledAttributes.getDrawable(0);
            this.mTvIcon = obtainStyledAttributes.getDrawable(1);
            this.mSpeakerIcon = obtainStyledAttributes.getDrawable(2);
            this.mSpeakerGroupIcon = obtainStyledAttributes.getDrawable(3);
            obtainStyledAttributes.recycle();
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
        
        private Drawable getIconDrawable(final MediaRouter.RouteInfo routeInfo) {
            final Uri iconUri = routeInfo.getIconUri();
            if (iconUri != null) {
                try {
                    final Drawable fromStream = Drawable.createFromStream(this.getContext().getContentResolver().openInputStream(iconUri), (String)null);
                    if (fromStream != null) {
                        return fromStream;
                    }
                }
                catch (IOException ex) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Failed to load ");
                    sb.append(iconUri);
                    Log.w("MediaRouteChooserDialog", sb.toString(), (Throwable)ex);
                }
            }
            return this.getDefaultIconDrawable(routeInfo);
        }
        
        public boolean areAllItemsEnabled() {
            return false;
        }
        
        public View getView(int n, final View view, final ViewGroup viewGroup) {
            View inflate = view;
            if (view == null) {
                inflate = this.mInflater.inflate(R$layout.mr_chooser_list_item, viewGroup, false);
            }
            final MediaRouter.RouteInfo routeInfo = (MediaRouter.RouteInfo)this.getItem(n);
            final TextView textView = (TextView)inflate.findViewById(R$id.mr_chooser_route_name);
            final TextView textView2 = (TextView)inflate.findViewById(R$id.mr_chooser_route_desc);
            textView.setText((CharSequence)routeInfo.getName());
            final String description = routeInfo.getDescription();
            final int connectionState = routeInfo.getConnectionState();
            final int n2 = n = 1;
            if (connectionState != 2) {
                if (routeInfo.getConnectionState() == 1) {
                    n = n2;
                }
                else {
                    n = 0;
                }
            }
            if (n != 0 && !TextUtils.isEmpty((CharSequence)description)) {
                textView.setGravity(80);
                textView2.setVisibility(0);
                textView2.setText((CharSequence)description);
            }
            else {
                textView.setGravity(16);
                textView2.setVisibility(8);
                textView2.setText((CharSequence)"");
            }
            inflate.setEnabled(routeInfo.isEnabled());
            final ImageView imageView = (ImageView)inflate.findViewById(R$id.mr_chooser_route_icon);
            if (imageView != null) {
                imageView.setImageDrawable(this.getIconDrawable(routeInfo));
            }
            return inflate;
        }
        
        public boolean isEnabled(final int n) {
            return ((MediaRouter.RouteInfo)this.getItem(n)).isEnabled();
        }
        
        public void onItemClick(final AdapterView<?> adapterView, final View view, final int n, final long n2) {
            final MediaRouter.RouteInfo routeInfo = (MediaRouter.RouteInfo)this.getItem(n);
            if (routeInfo.isEnabled()) {
                routeInfo.select();
                MediaRouteChooserDialog.this.dismiss();
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
