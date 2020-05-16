// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.pip.phone;

import android.os.RemoteException;
import android.os.Bundle;
import android.view.MagnificationSpec;
import android.view.accessibility.IAccessibilityInteractionConnectionCallback;
import android.graphics.Region;
import android.view.accessibility.AccessibilityNodeInfo$AccessibilityAction;
import java.util.ArrayList;
import android.graphics.Rect;
import android.os.Handler;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.List;
import android.view.accessibility.IAccessibilityInteractionConnection$Stub;

public class PipAccessibilityInteractionConnection extends IAccessibilityInteractionConnection$Stub
{
    private List<AccessibilityNodeInfo> mAccessibilityNodeInfoList;
    private AccessibilityCallbacks mCallbacks;
    private Handler mHandler;
    private PipMotionHelper mMotionHelper;
    private Rect mTmpBounds;
    
    public PipAccessibilityInteractionConnection(final PipMotionHelper mMotionHelper, final AccessibilityCallbacks mCallbacks, final Handler mHandler) {
        this.mTmpBounds = new Rect();
        this.mHandler = mHandler;
        this.mMotionHelper = mMotionHelper;
        this.mCallbacks = mCallbacks;
    }
    
    private List<AccessibilityNodeInfo> getNodeList() {
        if (this.mAccessibilityNodeInfoList == null) {
            this.mAccessibilityNodeInfoList = new ArrayList<AccessibilityNodeInfo>(1);
        }
        final AccessibilityNodeInfo obtainRootAccessibilityNodeInfo = obtainRootAccessibilityNodeInfo();
        this.mAccessibilityNodeInfoList.clear();
        this.mAccessibilityNodeInfoList.add(obtainRootAccessibilityNodeInfo);
        return this.mAccessibilityNodeInfoList;
    }
    
    public static AccessibilityNodeInfo obtainRootAccessibilityNodeInfo() {
        final AccessibilityNodeInfo obtain = AccessibilityNodeInfo.obtain();
        obtain.setSourceNodeId(AccessibilityNodeInfo.ROOT_NODE_ID, -3);
        obtain.addAction(AccessibilityNodeInfo$AccessibilityAction.ACTION_CLICK);
        obtain.addAction(AccessibilityNodeInfo$AccessibilityAction.ACTION_DISMISS);
        obtain.addAction(AccessibilityNodeInfo$AccessibilityAction.ACTION_MOVE_WINDOW);
        obtain.addAction(AccessibilityNodeInfo$AccessibilityAction.ACTION_EXPAND);
        obtain.setImportantForAccessibility(true);
        obtain.setClickable(true);
        obtain.setVisibleToUser(true);
        return obtain;
    }
    
    public void clearAccessibilityFocus() {
    }
    
    public void findAccessibilityNodeInfoByAccessibilityId(final long n, final Region region, final int n2, final IAccessibilityInteractionConnectionCallback accessibilityInteractionConnectionCallback, final int n3, final int n4, final long n5, final MagnificationSpec magnificationSpec, final Bundle bundle) {
        try {
            List<AccessibilityNodeInfo> nodeList;
            if (n == AccessibilityNodeInfo.ROOT_NODE_ID) {
                nodeList = this.getNodeList();
            }
            else {
                nodeList = null;
            }
            accessibilityInteractionConnectionCallback.setFindAccessibilityNodeInfosResult((List)nodeList, n2);
        }
        catch (RemoteException ex) {}
    }
    
    public void findAccessibilityNodeInfosByText(final long n, final String s, final Region region, final int n2, final IAccessibilityInteractionConnectionCallback accessibilityInteractionConnectionCallback, final int n3, final int n4, final long n5, final MagnificationSpec magnificationSpec) {
        try {
            accessibilityInteractionConnectionCallback.setFindAccessibilityNodeInfoResult((AccessibilityNodeInfo)null, n2);
        }
        catch (RemoteException ex) {}
    }
    
    public void findAccessibilityNodeInfosByViewId(final long n, final String s, final Region region, final int n2, final IAccessibilityInteractionConnectionCallback accessibilityInteractionConnectionCallback, final int n3, final int n4, final long n5, final MagnificationSpec magnificationSpec) {
        try {
            accessibilityInteractionConnectionCallback.setFindAccessibilityNodeInfoResult((AccessibilityNodeInfo)null, n2);
        }
        catch (RemoteException ex) {}
    }
    
    public void findFocus(final long n, final int n2, final Region region, final int n3, final IAccessibilityInteractionConnectionCallback accessibilityInteractionConnectionCallback, final int n4, final int n5, final long n6, final MagnificationSpec magnificationSpec) {
        try {
            accessibilityInteractionConnectionCallback.setFindAccessibilityNodeInfoResult((AccessibilityNodeInfo)null, n3);
        }
        catch (RemoteException ex) {}
    }
    
    public void focusSearch(final long n, final int n2, final Region region, final int n3, final IAccessibilityInteractionConnectionCallback accessibilityInteractionConnectionCallback, final int n4, final int n5, final long n6, final MagnificationSpec magnificationSpec) {
        try {
            accessibilityInteractionConnectionCallback.setFindAccessibilityNodeInfoResult((AccessibilityNodeInfo)null, n3);
        }
        catch (RemoteException ex) {}
    }
    
    public void notifyOutsideTouch() {
    }
    
    public void performAccessibilityAction(final long n, int int1, final Bundle bundle, final int n2, final IAccessibilityInteractionConnectionCallback accessibilityInteractionConnectionCallback, int int2, final int n3, long root_NODE_ID) {
        root_NODE_ID = AccessibilityNodeInfo.ROOT_NODE_ID;
        boolean b = true;
        Label_0142: {
            if (n == root_NODE_ID) {
                if (int1 == 16) {
                    this.mHandler.post((Runnable)new _$$Lambda$PipAccessibilityInteractionConnection$yj5JMyeINsNwnRK777qXcVORJV0(this));
                    break Label_0142;
                }
                if (int1 == 262144) {
                    this.mMotionHelper.expandPip();
                    break Label_0142;
                }
                if (int1 == 1048576) {
                    this.mMotionHelper.dismissPip();
                    break Label_0142;
                }
                if (int1 == 16908354) {
                    int2 = bundle.getInt("ACTION_ARGUMENT_MOVE_WINDOW_X");
                    int1 = bundle.getInt("ACTION_ARGUMENT_MOVE_WINDOW_Y");
                    new Rect().set(this.mMotionHelper.getBounds());
                    this.mTmpBounds.offsetTo(int2, int1);
                    this.mMotionHelper.movePip(this.mTmpBounds);
                    break Label_0142;
                }
            }
            b = false;
            try {
                accessibilityInteractionConnectionCallback.setPerformAccessibilityActionResult(b, n2);
            }
            catch (RemoteException ex) {}
        }
    }
    
    public interface AccessibilityCallbacks
    {
        void onAccessibilityShowMenu();
    }
}
