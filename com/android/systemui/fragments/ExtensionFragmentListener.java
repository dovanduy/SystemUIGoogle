// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.fragments;

import android.util.Log;
import android.app.Fragment;
import android.view.View;
import com.android.systemui.statusbar.policy.ExtensionController;
import java.util.function.Consumer;
import com.android.systemui.plugins.FragmentBase;

public class ExtensionFragmentListener<T extends FragmentBase> implements Consumer<T>
{
    private final ExtensionController.Extension<T> mExtension;
    private final FragmentHostManager mFragmentHostManager;
    private final int mId;
    private String mOldClass;
    private final String mTag;
    
    private ExtensionFragmentListener(final View view, final String mTag, final int mId, final ExtensionController.Extension<T> mExtension) {
        this.mTag = mTag;
        final FragmentHostManager value = FragmentHostManager.get(view);
        this.mFragmentHostManager = value;
        this.mExtension = mExtension;
        this.mId = mId;
        value.getFragmentManager().beginTransaction().replace(mId, (Fragment)this.mExtension.get(), this.mTag).commit();
        this.mExtension.clearItem(false);
    }
    
    public static <T> void attachExtensonToFragment(final View view, final String s, final int n, final ExtensionController.Extension<T> extension) {
        extension.addCallback(new ExtensionFragmentListener<T>(view, s, n, extension));
    }
    
    @Override
    public void accept(final T obj) {
        try {
            Fragment.class.cast(obj);
            this.mFragmentHostManager.getExtensionManager().setCurrentExtension(this.mId, this.mTag, this.mOldClass, obj.getClass().getName(), this.mExtension.getContext());
            this.mOldClass = obj.getClass().getName();
        }
        catch (ClassCastException ex) {
            final StringBuilder sb = new StringBuilder();
            sb.append(obj.getClass().getName());
            sb.append(" must be a Fragment");
            Log.e("ExtensionFragmentListener", sb.toString(), (Throwable)ex);
        }
        this.mExtension.clearItem(true);
    }
}
