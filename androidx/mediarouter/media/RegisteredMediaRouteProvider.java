// 
// Decompiled by Procyon v0.5.36
// 

package androidx.mediarouter.media;

import java.util.Collection;
import java.lang.ref.WeakReference;
import android.os.DeadObjectException;
import android.os.RemoteException;
import android.os.Message;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseArray;
import android.os.IBinder$DeathRecipient;
import android.os.Messenger;
import android.os.IBinder;
import java.util.Iterator;
import java.util.List;
import android.content.Intent;
import android.content.Context;
import android.util.Log;
import java.util.ArrayList;
import android.content.ComponentName;
import android.content.ServiceConnection;

final class RegisteredMediaRouteProvider extends MediaRouteProvider implements ServiceConnection
{
    static final boolean DEBUG;
    private Connection mActiveConnection;
    private boolean mBound;
    private final ComponentName mComponentName;
    private boolean mConnectionReady;
    private final ArrayList<ControllerConnection> mControllerConnections;
    final PrivateHandler mPrivateHandler;
    private boolean mStarted;
    
    static {
        DEBUG = Log.isLoggable("MediaRouteProviderProxy", 3);
    }
    
    public RegisteredMediaRouteProvider(final Context context, final ComponentName mComponentName) {
        super(context, new ProviderMetadata(mComponentName));
        this.mControllerConnections = new ArrayList<ControllerConnection>();
        this.mComponentName = mComponentName;
        this.mPrivateHandler = new PrivateHandler();
    }
    
    private void attachControllersToConnection() {
        for (int size = this.mControllerConnections.size(), i = 0; i < size; ++i) {
            this.mControllerConnections.get(i).attachConnection(this.mActiveConnection);
        }
    }
    
    private void bind() {
        if (!this.mBound) {
            if (RegisteredMediaRouteProvider.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append(this);
                sb.append(": Binding");
                Log.d("MediaRouteProviderProxy", sb.toString());
            }
            final Intent intent = new Intent("android.media.MediaRouteProviderService");
            intent.setComponent(this.mComponentName);
            try {
                final boolean bindService = this.getContext().bindService(intent, (ServiceConnection)this, 1);
                this.mBound = bindService;
                if (!bindService && RegisteredMediaRouteProvider.DEBUG) {
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append(this);
                    sb2.append(": Bind failed");
                    Log.d("MediaRouteProviderProxy", sb2.toString());
                }
            }
            catch (SecurityException ex) {
                if (RegisteredMediaRouteProvider.DEBUG) {
                    final StringBuilder sb3 = new StringBuilder();
                    sb3.append(this);
                    sb3.append(": Bind failed");
                    Log.d("MediaRouteProviderProxy", sb3.toString(), (Throwable)ex);
                }
            }
        }
    }
    
    private DynamicGroupRouteController createDynamicGroupRouteController(final String anObject) {
        final MediaRouteProviderDescriptor descriptor = this.getDescriptor();
        if (descriptor != null) {
            final List<MediaRouteDescriptor> routes = descriptor.getRoutes();
            for (int size = routes.size(), i = 0; i < size; ++i) {
                if (routes.get(i).getId().equals(anObject)) {
                    final RegisteredDynamicController e = new RegisteredDynamicController(anObject);
                    this.mControllerConnections.add((ControllerConnection)e);
                    if (this.mConnectionReady) {
                        ((ControllerConnection)e).attachConnection(this.mActiveConnection);
                    }
                    this.updateBinding();
                    return e;
                }
            }
        }
        return null;
    }
    
    private RouteController createRouteController(final String anObject, final String s) {
        final MediaRouteProviderDescriptor descriptor = this.getDescriptor();
        if (descriptor != null) {
            final List<MediaRouteDescriptor> routes = descriptor.getRoutes();
            for (int size = routes.size(), i = 0; i < size; ++i) {
                if (routes.get(i).getId().equals(anObject)) {
                    final RegisteredRouteController e = new RegisteredRouteController(anObject, s);
                    this.mControllerConnections.add((ControllerConnection)e);
                    if (this.mConnectionReady) {
                        ((ControllerConnection)e).attachConnection(this.mActiveConnection);
                    }
                    this.updateBinding();
                    return e;
                }
            }
        }
        return null;
    }
    
    private void detachControllersFromConnection() {
        for (int size = this.mControllerConnections.size(), i = 0; i < size; ++i) {
            this.mControllerConnections.get(i).detachConnection();
        }
    }
    
    private void disconnect() {
        if (this.mActiveConnection != null) {
            this.setDescriptor(null);
            this.mConnectionReady = false;
            this.detachControllersFromConnection();
            this.mActiveConnection.dispose();
            this.mActiveConnection = null;
        }
    }
    
    private ControllerConnection findControllerById(final int n) {
        for (final ControllerConnection controllerConnection : this.mControllerConnections) {
            if (controllerConnection.getControllerId() == n) {
                return controllerConnection;
            }
        }
        return null;
    }
    
    private boolean shouldBind() {
        if (this.mStarted) {
            if (this.getDiscoveryRequest() != null) {
                return true;
            }
            if (!this.mControllerConnections.isEmpty()) {
                return true;
            }
        }
        return false;
    }
    
    private void unbind() {
        if (this.mBound) {
            if (RegisteredMediaRouteProvider.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append(this);
                sb.append(": Unbinding");
                Log.d("MediaRouteProviderProxy", sb.toString());
            }
            this.mBound = false;
            this.disconnect();
            try {
                this.getContext().unbindService((ServiceConnection)this);
            }
            catch (IllegalArgumentException ex) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append(this);
                sb2.append(": unbindService failed");
                Log.e("MediaRouteProviderProxy", sb2.toString(), (Throwable)ex);
            }
        }
    }
    
    private void updateBinding() {
        if (this.shouldBind()) {
            this.bind();
        }
        else {
            this.unbind();
        }
    }
    
    public boolean hasComponentName(final String anObject, final String anObject2) {
        return this.mComponentName.getPackageName().equals(anObject) && this.mComponentName.getClassName().equals(anObject2);
    }
    
    void onConnectionDescriptorChanged(final Connection connection, final MediaRouteProviderDescriptor mediaRouteProviderDescriptor) {
        if (this.mActiveConnection == connection) {
            if (RegisteredMediaRouteProvider.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append(this);
                sb.append(": Descriptor changed, descriptor=");
                sb.append(mediaRouteProviderDescriptor);
                Log.d("MediaRouteProviderProxy", sb.toString());
            }
            this.setDescriptor(mediaRouteProviderDescriptor);
        }
    }
    
    void onConnectionDied(final Connection connection) {
        if (this.mActiveConnection == connection) {
            if (RegisteredMediaRouteProvider.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append(this);
                sb.append(": Service connection died");
                Log.d("MediaRouteProviderProxy", sb.toString());
            }
            this.disconnect();
        }
    }
    
    void onConnectionError(final Connection connection, final String str) {
        if (this.mActiveConnection == connection) {
            if (RegisteredMediaRouteProvider.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append(this);
                sb.append(": Service connection error - ");
                sb.append(str);
                Log.d("MediaRouteProviderProxy", sb.toString());
            }
            this.unbind();
        }
    }
    
    void onConnectionReady(final Connection connection) {
        if (this.mActiveConnection == connection) {
            this.mConnectionReady = true;
            this.attachControllersToConnection();
            final MediaRouteDiscoveryRequest discoveryRequest = this.getDiscoveryRequest();
            if (discoveryRequest != null) {
                this.mActiveConnection.setDiscoveryRequest(discoveryRequest);
            }
        }
    }
    
    void onControllerReleased(final ControllerConnection o) {
        this.mControllerConnections.remove(o);
        o.detachConnection();
        this.updateBinding();
    }
    
    @Override
    public DynamicGroupRouteController onCreateDynamicGroupRouteController(final String s) {
        if (s != null) {
            return this.createDynamicGroupRouteController(s);
        }
        throw new IllegalArgumentException("initialMemberRouteId cannot be null.");
    }
    
    @Override
    public RouteController onCreateRouteController(final String s) {
        if (s != null) {
            return this.createRouteController(s, null);
        }
        throw new IllegalArgumentException("routeId cannot be null");
    }
    
    @Override
    public RouteController onCreateRouteController(final String s, final String s2) {
        if (s == null) {
            throw new IllegalArgumentException("routeId cannot be null");
        }
        if (s2 != null) {
            return this.createRouteController(s, s2);
        }
        throw new IllegalArgumentException("routeGroupId cannot be null");
    }
    
    @Override
    public void onDiscoveryRequestChanged(final MediaRouteDiscoveryRequest discoveryRequest) {
        if (this.mConnectionReady) {
            this.mActiveConnection.setDiscoveryRequest(discoveryRequest);
        }
        this.updateBinding();
    }
    
    void onDynamicRouteDescriptorChanged(final Connection connection, final int n, final List<DynamicRouteDescriptor> obj) {
        if (this.mActiveConnection == connection) {
            if (RegisteredMediaRouteProvider.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append(this);
                sb.append(": DynamicRouteDescriptors changed, descriptors=");
                sb.append(obj);
                Log.d("MediaRouteProviderProxy", sb.toString());
            }
            final ControllerConnection controllerById = this.findControllerById(n);
            if (controllerById instanceof RegisteredDynamicController) {
                ((RegisteredDynamicController)controllerById).onDynamicRoutesChanged(obj);
            }
        }
    }
    
    public void onServiceConnected(final ComponentName componentName, final IBinder binder) {
        if (RegisteredMediaRouteProvider.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append(this);
            sb.append(": Connected");
            Log.d("MediaRouteProviderProxy", sb.toString());
        }
        if (this.mBound) {
            this.disconnect();
            Messenger messenger;
            if (binder != null) {
                messenger = new Messenger(binder);
            }
            else {
                messenger = null;
            }
            if (MediaRouteProviderProtocol.isValidRemoteMessenger(messenger)) {
                final Connection mActiveConnection = new Connection(messenger);
                if (mActiveConnection.register()) {
                    this.mActiveConnection = mActiveConnection;
                }
                else if (RegisteredMediaRouteProvider.DEBUG) {
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append(this);
                    sb2.append(": Registration failed");
                    Log.d("MediaRouteProviderProxy", sb2.toString());
                }
            }
            else {
                final StringBuilder sb3 = new StringBuilder();
                sb3.append(this);
                sb3.append(": Service returned invalid messenger binder");
                Log.e("MediaRouteProviderProxy", sb3.toString());
            }
        }
    }
    
    public void onServiceDisconnected(final ComponentName componentName) {
        if (RegisteredMediaRouteProvider.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append(this);
            sb.append(": Service disconnected");
            Log.d("MediaRouteProviderProxy", sb.toString());
        }
        this.disconnect();
    }
    
    public void rebindIfDisconnected() {
        if (this.mActiveConnection == null && this.shouldBind()) {
            this.unbind();
            this.bind();
        }
    }
    
    public void start() {
        if (!this.mStarted) {
            if (RegisteredMediaRouteProvider.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append(this);
                sb.append(": Starting");
                Log.d("MediaRouteProviderProxy", sb.toString());
            }
            this.mStarted = true;
            this.updateBinding();
        }
    }
    
    public void stop() {
        if (this.mStarted) {
            if (RegisteredMediaRouteProvider.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append(this);
                sb.append(": Stopping");
                Log.d("MediaRouteProviderProxy", sb.toString());
            }
            this.mStarted = false;
            this.updateBinding();
        }
    }
    
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Service connection ");
        sb.append(this.mComponentName.flattenToShortString());
        return sb.toString();
    }
    
    private final class Connection implements IBinder$DeathRecipient
    {
        private int mNextControllerId;
        private int mNextRequestId;
        private final SparseArray<MediaRouter.ControlRequestCallback> mPendingCallbacks;
        private int mPendingRegisterRequestId;
        private final ReceiveHandler mReceiveHandler;
        private final Messenger mReceiveMessenger;
        private final Messenger mServiceMessenger;
        private int mServiceVersion;
        final /* synthetic */ RegisteredMediaRouteProvider this$0;
        
        public Connection(final Messenger mServiceMessenger) {
            this.mNextRequestId = 1;
            this.mNextControllerId = 1;
            this.mPendingCallbacks = (SparseArray<MediaRouter.ControlRequestCallback>)new SparseArray();
            this.mServiceMessenger = mServiceMessenger;
            this.mReceiveHandler = new ReceiveHandler(this);
            this.mReceiveMessenger = new Messenger((Handler)this.mReceiveHandler);
        }
        
        private boolean sendRequest(final int what, final int arg1, final int arg2, final Object obj, final Bundle data) {
            final Message obtain = Message.obtain();
            obtain.what = what;
            obtain.arg1 = arg1;
            obtain.arg2 = arg2;
            obtain.obj = obj;
            obtain.setData(data);
            obtain.replyTo = this.mReceiveMessenger;
            try {
                this.mServiceMessenger.send(obtain);
                return true;
            }
            catch (RemoteException ex) {
                if (what == 2) {
                    goto Label_0074;
                }
                Log.e("MediaRouteProviderProxy", "Could not send message to service.", (Throwable)ex);
            }
            catch (DeadObjectException ex2) {
                goto Label_0074;
            }
        }
        
        public void addMemberRoute(final int n, final String s) {
            final Bundle bundle = new Bundle();
            bundle.putString("memberRouteId", s);
            this.sendRequest(12, this.mNextRequestId++, n, null, bundle);
        }
        
        public void binderDied() {
            RegisteredMediaRouteProvider.this.mPrivateHandler.post((Runnable)new Runnable() {
                @Override
                public void run() {
                    final Connection this$1 = Connection.this;
                    this$1.this$0.onConnectionDied(this$1);
                }
            });
        }
        
        public int createDynamicGroupRouteController(final String s, final MediaRouter.ControlRequestCallback controlRequestCallback) {
            final int n = this.mNextControllerId++;
            final int n2 = this.mNextRequestId++;
            final Bundle bundle = new Bundle();
            bundle.putString("memberRouteId", s);
            this.sendRequest(11, n2, n, null, bundle);
            this.mPendingCallbacks.put(n2, (Object)controlRequestCallback);
            return n;
        }
        
        public int createRouteController(final String s, final String s2) {
            final int n = this.mNextControllerId++;
            final Bundle bundle = new Bundle();
            bundle.putString("routeId", s);
            bundle.putString("routeGroupId", s2);
            this.sendRequest(3, this.mNextRequestId++, n, null, bundle);
            return n;
        }
        
        public void dispose() {
            this.sendRequest(2, 0, 0, null, null);
            this.mReceiveHandler.dispose();
            this.mServiceMessenger.getBinder().unlinkToDeath((IBinder$DeathRecipient)this, 0);
            RegisteredMediaRouteProvider.this.mPrivateHandler.post((Runnable)new Runnable() {
                @Override
                public void run() {
                    Connection.this.failPendingCallbacks();
                }
            });
        }
        
        void failPendingCallbacks() {
            for (int size = this.mPendingCallbacks.size(), i = 0; i < size; ++i) {
                ((MediaRouter.ControlRequestCallback)this.mPendingCallbacks.valueAt(i)).onError(null, null);
            }
            this.mPendingCallbacks.clear();
        }
        
        public boolean onControlRequestFailed(final int n, final String s, final Bundle bundle) {
            final MediaRouter.ControlRequestCallback controlRequestCallback = (MediaRouter.ControlRequestCallback)this.mPendingCallbacks.get(n);
            if (controlRequestCallback != null) {
                this.mPendingCallbacks.remove(n);
                controlRequestCallback.onError(s, bundle);
                return true;
            }
            return false;
        }
        
        public boolean onControlRequestSucceeded(final int n, final Bundle bundle) {
            final MediaRouter.ControlRequestCallback controlRequestCallback = (MediaRouter.ControlRequestCallback)this.mPendingCallbacks.get(n);
            if (controlRequestCallback != null) {
                this.mPendingCallbacks.remove(n);
                controlRequestCallback.onResult(bundle);
                return true;
            }
            return false;
        }
        
        public boolean onDescriptorChanged(final Bundle bundle) {
            if (this.mServiceVersion != 0) {
                RegisteredMediaRouteProvider.this.onConnectionDescriptorChanged(this, MediaRouteProviderDescriptor.fromBundle(bundle));
                return true;
            }
            return false;
        }
        
        public void onDynamicGroupRouteControllerCreated(final int n, final Bundle bundle) {
            final MediaRouter.ControlRequestCallback controlRequestCallback = (MediaRouter.ControlRequestCallback)this.mPendingCallbacks.get(n);
            if (bundle != null && bundle.containsKey("routeId")) {
                this.mPendingCallbacks.remove(n);
                controlRequestCallback.onResult(bundle);
            }
            else {
                controlRequestCallback.onError("DynamicGroupRouteController is created without valid route id.", bundle);
            }
        }
        
        public boolean onDynamicRouteDescriptorsChanged(final int n, final Bundle bundle) {
            if (this.mServiceVersion != 0) {
                final ArrayList parcelableArrayList = bundle.getParcelableArrayList("dynamicRoutes");
                final ArrayList<DynamicGroupRouteController.DynamicRouteDescriptor> list = new ArrayList<DynamicGroupRouteController.DynamicRouteDescriptor>();
                final Iterator<Bundle> iterator = parcelableArrayList.iterator();
                while (iterator.hasNext()) {
                    list.add(DynamicGroupRouteController.DynamicRouteDescriptor.fromBundle(iterator.next()));
                }
                RegisteredMediaRouteProvider.this.onDynamicRouteDescriptorChanged(this, n, list);
                return true;
            }
            return false;
        }
        
        public boolean onGenericFailure(final int n) {
            if (n == this.mPendingRegisterRequestId) {
                this.mPendingRegisterRequestId = 0;
                RegisteredMediaRouteProvider.this.onConnectionError(this, "Registration failed");
            }
            final MediaRouter.ControlRequestCallback controlRequestCallback = (MediaRouter.ControlRequestCallback)this.mPendingCallbacks.get(n);
            if (controlRequestCallback != null) {
                this.mPendingCallbacks.remove(n);
                controlRequestCallback.onError(null, null);
            }
            return true;
        }
        
        public boolean onGenericSuccess(final int n) {
            return true;
        }
        
        public boolean onRegistered(final int n, final int mServiceVersion, final Bundle bundle) {
            if (this.mServiceVersion == 0 && n == this.mPendingRegisterRequestId && mServiceVersion >= 1) {
                this.mPendingRegisterRequestId = 0;
                this.mServiceVersion = mServiceVersion;
                RegisteredMediaRouteProvider.this.onConnectionDescriptorChanged(this, MediaRouteProviderDescriptor.fromBundle(bundle));
                RegisteredMediaRouteProvider.this.onConnectionReady(this);
                return true;
            }
            return false;
        }
        
        public boolean register() {
            final int mPendingRegisterRequestId = this.mNextRequestId++;
            this.mPendingRegisterRequestId = mPendingRegisterRequestId;
            if (!this.sendRequest(1, mPendingRegisterRequestId, 3, null, null)) {
                return false;
            }
            try {
                this.mServiceMessenger.getBinder().linkToDeath((IBinder$DeathRecipient)this, 0);
                return true;
            }
            catch (RemoteException ex) {
                this.binderDied();
                return false;
            }
        }
        
        public void releaseRouteController(final int n) {
            this.sendRequest(4, this.mNextRequestId++, n, null, null);
        }
        
        public void removeMemberRoute(final int n, final String s) {
            final Bundle bundle = new Bundle();
            bundle.putString("memberRouteId", s);
            this.sendRequest(13, this.mNextRequestId++, n, null, bundle);
        }
        
        public void selectRoute(final int n) {
            this.sendRequest(5, this.mNextRequestId++, n, null, null);
        }
        
        public void setDiscoveryRequest(final MediaRouteDiscoveryRequest mediaRouteDiscoveryRequest) {
            final int n = this.mNextRequestId++;
            Bundle bundle;
            if (mediaRouteDiscoveryRequest != null) {
                bundle = mediaRouteDiscoveryRequest.asBundle();
            }
            else {
                bundle = null;
            }
            this.sendRequest(10, n, 0, bundle, null);
        }
        
        public void setVolume(final int n, int n2) {
            final Bundle bundle = new Bundle();
            bundle.putInt("volume", n2);
            n2 = this.mNextRequestId++;
            this.sendRequest(7, n2, n, null, bundle);
        }
        
        public void unselectRoute(final int n, int n2) {
            final Bundle bundle = new Bundle();
            bundle.putInt("unselectReason", n2);
            n2 = this.mNextRequestId++;
            this.sendRequest(6, n2, n, null, bundle);
        }
        
        public void updateVolume(final int n, int n2) {
            final Bundle bundle = new Bundle();
            bundle.putInt("volume", n2);
            n2 = this.mNextRequestId++;
            this.sendRequest(8, n2, n, null, bundle);
        }
    }
    
    interface ControllerConnection
    {
        void attachConnection(final Connection p0);
        
        void detachConnection();
        
        int getControllerId();
    }
    
    private static final class PrivateHandler extends Handler
    {
        PrivateHandler() {
        }
    }
    
    private static final class ReceiveHandler extends Handler
    {
        private final WeakReference<Connection> mConnectionRef;
        
        public ReceiveHandler(final Connection referent) {
            this.mConnectionRef = new WeakReference<Connection>(referent);
        }
        
        private boolean processMessage(final Connection connection, final int n, final int n2, final int n3, final Object o, final Bundle bundle) {
            switch (n) {
                case 7: {
                    if (o == null || o instanceof Bundle) {
                        return connection.onDynamicRouteDescriptorsChanged(n3, (Bundle)o);
                    }
                    break;
                }
                case 6: {
                    if (o instanceof Bundle) {
                        connection.onDynamicGroupRouteControllerCreated(n2, (Bundle)o);
                        break;
                    }
                    Log.w("MediaRouteProviderProxy", "No further information on the dynamic group controller");
                    break;
                }
                case 5: {
                    if (o == null || o instanceof Bundle) {
                        return connection.onDescriptorChanged((Bundle)o);
                    }
                    break;
                }
                case 4: {
                    if (o == null || o instanceof Bundle) {
                        String string;
                        if (bundle == null) {
                            string = null;
                        }
                        else {
                            string = bundle.getString("error");
                        }
                        return connection.onControlRequestFailed(n2, string, (Bundle)o);
                    }
                    break;
                }
                case 3: {
                    if (o == null || o instanceof Bundle) {
                        return connection.onControlRequestSucceeded(n2, (Bundle)o);
                    }
                    break;
                }
                case 2: {
                    if (o == null || o instanceof Bundle) {
                        return connection.onRegistered(n2, n3, (Bundle)o);
                    }
                    break;
                }
                case 1: {
                    connection.onGenericSuccess(n2);
                    return true;
                }
                case 0: {
                    connection.onGenericFailure(n2);
                    return true;
                }
            }
            return false;
        }
        
        public void dispose() {
            this.mConnectionRef.clear();
        }
        
        public void handleMessage(final Message obj) {
            final Connection connection = this.mConnectionRef.get();
            if (connection != null && !this.processMessage(connection, obj.what, obj.arg1, obj.arg2, obj.obj, obj.peekData()) && RegisteredMediaRouteProvider.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Unhandled message from server: ");
                sb.append(obj);
                Log.d("MediaRouteProviderProxy", sb.toString());
            }
        }
    }
    
    private final class RegisteredDynamicController extends DynamicGroupRouteController implements ControllerConnection
    {
        private Connection mConnection;
        private int mControllerId;
        String mGroupableSectionTitle;
        private final String mInitialMemberRouteId;
        private int mPendingSetVolume;
        private int mPendingUpdateVolumeDelta;
        private boolean mSelected;
        String mTransferableSectionTitle;
        
        RegisteredDynamicController(final String mInitialMemberRouteId) {
            this.mPendingSetVolume = -1;
            this.mControllerId = -1;
            this.mInitialMemberRouteId = mInitialMemberRouteId;
        }
        
        @Override
        public void attachConnection(final Connection mConnection) {
            final MediaRouter.ControlRequestCallback controlRequestCallback = new MediaRouter.ControlRequestCallback() {
                @Override
                public void onError(final String str, final Bundle obj) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Error: ");
                    sb.append(str);
                    sb.append(", data: ");
                    sb.append(obj);
                    Log.d("MediaRouteProviderProxy", sb.toString());
                }
                
                @Override
                public void onResult(final Bundle bundle) {
                    RegisteredDynamicController.this.mGroupableSectionTitle = bundle.getString("groupableTitle");
                    RegisteredDynamicController.this.mTransferableSectionTitle = bundle.getString("transferableTitle");
                }
            };
            this.mConnection = mConnection;
            final int dynamicGroupRouteController = mConnection.createDynamicGroupRouteController(this.mInitialMemberRouteId, controlRequestCallback);
            this.mControllerId = dynamicGroupRouteController;
            if (this.mSelected) {
                mConnection.selectRoute(dynamicGroupRouteController);
                final int mPendingSetVolume = this.mPendingSetVolume;
                if (mPendingSetVolume >= 0) {
                    mConnection.setVolume(this.mControllerId, mPendingSetVolume);
                    this.mPendingSetVolume = -1;
                }
                final int mPendingUpdateVolumeDelta = this.mPendingUpdateVolumeDelta;
                if (mPendingUpdateVolumeDelta != 0) {
                    mConnection.updateVolume(this.mControllerId, mPendingUpdateVolumeDelta);
                    this.mPendingUpdateVolumeDelta = 0;
                }
            }
        }
        
        @Override
        public void detachConnection() {
            final Connection mConnection = this.mConnection;
            if (mConnection != null) {
                mConnection.releaseRouteController(this.mControllerId);
                this.mConnection = null;
                this.mControllerId = 0;
            }
        }
        
        @Override
        public int getControllerId() {
            return this.mControllerId;
        }
        
        @Override
        public String getGroupableSelectionTitle() {
            return this.mGroupableSectionTitle;
        }
        
        @Override
        public String getTransferableSectionTitle() {
            return this.mTransferableSectionTitle;
        }
        
        @Override
        public void onAddMemberRoute(final String s) {
            final Connection mConnection = this.mConnection;
            if (mConnection != null) {
                mConnection.addMemberRoute(this.mControllerId, s);
            }
        }
        
        void onDynamicRoutesChanged(final List<DynamicRouteDescriptor> list) {
            ((DynamicGroupRouteController)this).notifyDynamicRoutesChanged(list);
        }
        
        @Override
        public void onRelease() {
            RegisteredMediaRouteProvider.this.onControllerReleased((ControllerConnection)this);
        }
        
        @Override
        public void onRemoveMemberRoute(final String s) {
            final Connection mConnection = this.mConnection;
            if (mConnection != null) {
                mConnection.removeMemberRoute(this.mControllerId, s);
            }
        }
        
        @Override
        public void onSelect() {
            this.mSelected = true;
            final Connection mConnection = this.mConnection;
            if (mConnection != null) {
                mConnection.selectRoute(this.mControllerId);
            }
        }
        
        @Override
        public void onSetVolume(final int mPendingSetVolume) {
            final Connection mConnection = this.mConnection;
            if (mConnection != null) {
                mConnection.setVolume(this.mControllerId, mPendingSetVolume);
            }
            else {
                this.mPendingSetVolume = mPendingSetVolume;
                this.mPendingUpdateVolumeDelta = 0;
            }
        }
        
        @Override
        public void onUnselect() {
            this.onUnselect(0);
        }
        
        @Override
        public void onUnselect(final int n) {
            this.mSelected = false;
            final Connection mConnection = this.mConnection;
            if (mConnection != null) {
                mConnection.unselectRoute(this.mControllerId, n);
            }
        }
        
        @Override
        public void onUpdateVolume(final int n) {
            final Connection mConnection = this.mConnection;
            if (mConnection != null) {
                mConnection.updateVolume(this.mControllerId, n);
            }
            else {
                this.mPendingUpdateVolumeDelta += n;
            }
        }
    }
    
    private final class RegisteredRouteController extends RouteController implements ControllerConnection
    {
        private Connection mConnection;
        private int mControllerId;
        private int mPendingSetVolume;
        private int mPendingUpdateVolumeDelta;
        private final String mRouteGroupId;
        private final String mRouteId;
        private boolean mSelected;
        
        RegisteredRouteController(final String mRouteId, final String mRouteGroupId) {
            this.mPendingSetVolume = -1;
            this.mRouteId = mRouteId;
            this.mRouteGroupId = mRouteGroupId;
        }
        
        @Override
        public void attachConnection(final Connection mConnection) {
            this.mConnection = mConnection;
            final int routeController = mConnection.createRouteController(this.mRouteId, this.mRouteGroupId);
            this.mControllerId = routeController;
            if (this.mSelected) {
                mConnection.selectRoute(routeController);
                final int mPendingSetVolume = this.mPendingSetVolume;
                if (mPendingSetVolume >= 0) {
                    mConnection.setVolume(this.mControllerId, mPendingSetVolume);
                    this.mPendingSetVolume = -1;
                }
                final int mPendingUpdateVolumeDelta = this.mPendingUpdateVolumeDelta;
                if (mPendingUpdateVolumeDelta != 0) {
                    mConnection.updateVolume(this.mControllerId, mPendingUpdateVolumeDelta);
                    this.mPendingUpdateVolumeDelta = 0;
                }
            }
        }
        
        @Override
        public void detachConnection() {
            final Connection mConnection = this.mConnection;
            if (mConnection != null) {
                mConnection.releaseRouteController(this.mControllerId);
                this.mConnection = null;
                this.mControllerId = 0;
            }
        }
        
        @Override
        public int getControllerId() {
            return this.mControllerId;
        }
        
        @Override
        public void onRelease() {
            RegisteredMediaRouteProvider.this.onControllerReleased((ControllerConnection)this);
        }
        
        @Override
        public void onSelect() {
            this.mSelected = true;
            final Connection mConnection = this.mConnection;
            if (mConnection != null) {
                mConnection.selectRoute(this.mControllerId);
            }
        }
        
        @Override
        public void onSetVolume(final int mPendingSetVolume) {
            final Connection mConnection = this.mConnection;
            if (mConnection != null) {
                mConnection.setVolume(this.mControllerId, mPendingSetVolume);
            }
            else {
                this.mPendingSetVolume = mPendingSetVolume;
                this.mPendingUpdateVolumeDelta = 0;
            }
        }
        
        @Override
        public void onUnselect() {
            this.onUnselect(0);
        }
        
        @Override
        public void onUnselect(final int n) {
            this.mSelected = false;
            final Connection mConnection = this.mConnection;
            if (mConnection != null) {
                mConnection.unselectRoute(this.mControllerId, n);
            }
        }
        
        @Override
        public void onUpdateVolume(final int n) {
            final Connection mConnection = this.mConnection;
            if (mConnection != null) {
                mConnection.updateVolume(this.mControllerId, n);
            }
            else {
                this.mPendingUpdateVolumeDelta += n;
            }
        }
    }
}
