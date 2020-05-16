// 
// Decompiled by Procyon v0.5.36
// 

package androidx.mediarouter.media;

import android.os.Message;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Executor;
import androidx.core.util.ObjectsCompat;
import android.os.Handler;
import android.content.ComponentName;
import android.content.Context;

public abstract class MediaRouteProvider
{
    private Callback mCallback;
    private final Context mContext;
    private MediaRouteProviderDescriptor mDescriptor;
    private MediaRouteDiscoveryRequest mDiscoveryRequest;
    private final ProviderHandler mHandler;
    private final ProviderMetadata mMetadata;
    private boolean mPendingDescriptorChange;
    private boolean mPendingDiscoveryRequestChange;
    
    MediaRouteProvider(final Context mContext, final ProviderMetadata mMetadata) {
        this.mHandler = new ProviderHandler();
        if (mContext != null) {
            this.mContext = mContext;
            if (mMetadata == null) {
                this.mMetadata = new ProviderMetadata(new ComponentName(mContext, (Class)this.getClass()));
            }
            else {
                this.mMetadata = mMetadata;
            }
            return;
        }
        throw new IllegalArgumentException("context must not be null");
    }
    
    void deliverDescriptorChanged() {
        this.mPendingDescriptorChange = false;
        final Callback mCallback = this.mCallback;
        if (mCallback != null) {
            mCallback.onDescriptorChanged(this, this.mDescriptor);
        }
    }
    
    void deliverDiscoveryRequestChanged() {
        this.mPendingDiscoveryRequestChange = false;
        this.onDiscoveryRequestChanged(this.mDiscoveryRequest);
    }
    
    public final Context getContext() {
        return this.mContext;
    }
    
    public final MediaRouteProviderDescriptor getDescriptor() {
        return this.mDescriptor;
    }
    
    public final MediaRouteDiscoveryRequest getDiscoveryRequest() {
        return this.mDiscoveryRequest;
    }
    
    public final Handler getHandler() {
        return this.mHandler;
    }
    
    public final ProviderMetadata getMetadata() {
        return this.mMetadata;
    }
    
    public DynamicGroupRouteController onCreateDynamicGroupRouteController(final String s) {
        if (s != null) {
            return null;
        }
        throw new IllegalArgumentException("initialMemberRouteId cannot be null.");
    }
    
    public RouteController onCreateRouteController(final String s) {
        if (s != null) {
            return null;
        }
        throw new IllegalArgumentException("routeId cannot be null");
    }
    
    public RouteController onCreateRouteController(final String s, final String s2) {
        if (s == null) {
            throw new IllegalArgumentException("routeId cannot be null");
        }
        if (s2 != null) {
            return this.onCreateRouteController(s);
        }
        throw new IllegalArgumentException("routeGroupId cannot be null");
    }
    
    public void onDiscoveryRequestChanged(final MediaRouteDiscoveryRequest mediaRouteDiscoveryRequest) {
    }
    
    public final void setCallback(final Callback mCallback) {
        MediaRouter.checkCallingThread();
        this.mCallback = mCallback;
    }
    
    public final void setDescriptor(final MediaRouteProviderDescriptor mDescriptor) {
        MediaRouter.checkCallingThread();
        if (this.mDescriptor != mDescriptor) {
            this.mDescriptor = mDescriptor;
            if (!this.mPendingDescriptorChange) {
                this.mPendingDescriptorChange = true;
                this.mHandler.sendEmptyMessage(1);
            }
        }
    }
    
    public final void setDiscoveryRequest(final MediaRouteDiscoveryRequest mDiscoveryRequest) {
        MediaRouter.checkCallingThread();
        if (ObjectsCompat.equals(this.mDiscoveryRequest, mDiscoveryRequest)) {
            return;
        }
        this.mDiscoveryRequest = mDiscoveryRequest;
        if (!this.mPendingDiscoveryRequestChange) {
            this.mPendingDiscoveryRequestChange = true;
            this.mHandler.sendEmptyMessage(2);
        }
    }
    
    public abstract static class Callback
    {
        public abstract void onDescriptorChanged(final MediaRouteProvider p0, final MediaRouteProviderDescriptor p1);
    }
    
    public abstract static class DynamicGroupRouteController extends RouteController
    {
        Executor mExecutor;
        OnDynamicRoutesChangedListener mListener;
        private final Object mLock;
        Collection<DynamicRouteDescriptor> mPendingRoutes;
        
        public DynamicGroupRouteController() {
            this.mLock = new Object();
        }
        
        public String getGroupableSelectionTitle() {
            return null;
        }
        
        public String getTransferableSectionTitle() {
            return null;
        }
        
        public final void notifyDynamicRoutesChanged(final Collection<DynamicRouteDescriptor> c) {
            synchronized (this.mLock) {
                if (this.mExecutor != null) {
                    this.mExecutor.execute(new Runnable() {
                        final /* synthetic */ OnDynamicRoutesChangedListener val$listener = DynamicGroupRouteController.this.mListener;
                        
                        @Override
                        public void run() {
                            this.val$listener.onRoutesChanged(DynamicGroupRouteController.this, c);
                        }
                    });
                }
                else {
                    this.mPendingRoutes = new ArrayList<DynamicRouteDescriptor>(c);
                }
            }
        }
        
        public abstract void onAddMemberRoute(final String p0);
        
        public abstract void onRemoveMemberRoute(final String p0);
        
        void setOnDynamicRoutesChangedListener(Executor mExecutor, final OnDynamicRoutesChangedListener mListener) {
            final Object mLock = this.mLock;
            // monitorenter(mLock)
            Label_0097: {
                if (mExecutor == null) {
                    break Label_0097;
                }
                Label_0085: {
                    if (mListener == null) {
                        break Label_0085;
                    }
                    try {
                        this.mExecutor = mExecutor;
                        this.mListener = mListener;
                        if (this.mPendingRoutes != null && !this.mPendingRoutes.isEmpty()) {
                            final Collection<DynamicRouteDescriptor> mPendingRoutes = this.mPendingRoutes;
                            this.mPendingRoutes = null;
                            mExecutor = this.mExecutor;
                            mExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    mListener.onRoutesChanged(DynamicGroupRouteController.this, mPendingRoutes);
                                }
                            });
                        }
                        return;
                        throw new NullPointerException("Listener shouldn't be null");
                        throw new NullPointerException("Executor shouldn't be null");
                    }
                    finally {
                    }
                    // monitorexit(mLock)
                }
            }
        }
        
        public static final class DynamicRouteDescriptor
        {
            final boolean mIsGroupable;
            final boolean mIsTransferable;
            final boolean mIsUnselectable;
            final MediaRouteDescriptor mMediaRouteDescriptor;
            final int mSelectionState;
            
            DynamicRouteDescriptor(final MediaRouteDescriptor mMediaRouteDescriptor, final int mSelectionState, final boolean mIsUnselectable, final boolean mIsGroupable, final boolean mIsTransferable) {
                this.mMediaRouteDescriptor = mMediaRouteDescriptor;
                this.mSelectionState = mSelectionState;
                this.mIsUnselectable = mIsUnselectable;
                this.mIsGroupable = mIsGroupable;
                this.mIsTransferable = mIsTransferable;
            }
            
            static DynamicRouteDescriptor fromBundle(final Bundle bundle) {
                if (bundle == null) {
                    return null;
                }
                return new DynamicRouteDescriptor(MediaRouteDescriptor.fromBundle(bundle.getBundle("mrDescriptor")), bundle.getInt("selectionState", 1), bundle.getBoolean("isUnselectable", false), bundle.getBoolean("isGroupable", false), bundle.getBoolean("isTransferable", false));
            }
            
            public MediaRouteDescriptor getRouteDescriptor() {
                return this.mMediaRouteDescriptor;
            }
            
            public int getSelectionState() {
                return this.mSelectionState;
            }
            
            public boolean isGroupable() {
                return this.mIsGroupable;
            }
            
            public boolean isTransferable() {
                return this.mIsTransferable;
            }
            
            public boolean isUnselectable() {
                return this.mIsUnselectable;
            }
        }
        
        interface OnDynamicRoutesChangedListener
        {
            void onRoutesChanged(final DynamicGroupRouteController p0, final Collection<DynamicRouteDescriptor> p1);
        }
    }
    
    private final class ProviderHandler extends Handler
    {
        ProviderHandler() {
        }
        
        public void handleMessage(final Message message) {
            final int what = message.what;
            if (what != 1) {
                if (what == 2) {
                    MediaRouteProvider.this.deliverDiscoveryRequestChanged();
                }
            }
            else {
                MediaRouteProvider.this.deliverDescriptorChanged();
            }
        }
    }
    
    public static final class ProviderMetadata
    {
        private final ComponentName mComponentName;
        
        ProviderMetadata(final ComponentName mComponentName) {
            if (mComponentName != null) {
                this.mComponentName = mComponentName;
                return;
            }
            throw new IllegalArgumentException("componentName must not be null");
        }
        
        public ComponentName getComponentName() {
            return this.mComponentName;
        }
        
        public String getPackageName() {
            return this.mComponentName.getPackageName();
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("ProviderMetadata{ componentName=");
            sb.append(this.mComponentName.flattenToShortString());
            sb.append(" }");
            return sb.toString();
        }
    }
    
    public abstract static class RouteController
    {
        public void onRelease() {
        }
        
        public void onSelect() {
        }
        
        public void onSetVolume(final int n) {
        }
        
        public void onUnselect() {
        }
        
        public void onUnselect(final int n) {
            this.onUnselect();
        }
        
        public void onUpdateVolume(final int n) {
        }
    }
}
