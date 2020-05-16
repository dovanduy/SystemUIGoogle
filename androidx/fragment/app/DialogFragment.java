// 
// Decompiled by Procyon v0.5.36
// 

package androidx.fragment.app;

import android.view.Window;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.content.Context;
import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.util.Log;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import android.os.Handler;
import android.app.Dialog;
import android.content.DialogInterface$OnDismissListener;
import android.content.DialogInterface$OnCancelListener;

public class DialogFragment extends Fragment implements DialogInterface$OnCancelListener, DialogInterface$OnDismissListener
{
    private int mBackStackId;
    private boolean mCancelable;
    private boolean mCreatingDialog;
    private Dialog mDialog;
    private boolean mDialogCreated;
    private Runnable mDismissRunnable;
    private boolean mDismissed;
    private Handler mHandler;
    private Observer<LifecycleOwner> mObserver;
    private DialogInterface$OnCancelListener mOnCancelListener;
    private DialogInterface$OnDismissListener mOnDismissListener;
    private boolean mShownByMe;
    private boolean mShowsDialog;
    private int mStyle;
    private int mTheme;
    private boolean mViewDestroyed;
    
    public DialogFragment() {
        this.mDismissRunnable = new Runnable() {
            @SuppressLint({ "SyntheticAccessor" })
            @Override
            public void run() {
                DialogFragment.this.mOnDismissListener.onDismiss((DialogInterface)DialogFragment.this.mDialog);
            }
        };
        this.mOnCancelListener = (DialogInterface$OnCancelListener)new DialogInterface$OnCancelListener() {
            @SuppressLint({ "SyntheticAccessor" })
            public void onCancel(final DialogInterface dialogInterface) {
                if (DialogFragment.this.mDialog != null) {
                    final DialogFragment this$0 = DialogFragment.this;
                    this$0.onCancel((DialogInterface)this$0.mDialog);
                }
            }
        };
        this.mOnDismissListener = (DialogInterface$OnDismissListener)new DialogInterface$OnDismissListener() {
            @SuppressLint({ "SyntheticAccessor" })
            public void onDismiss(final DialogInterface dialogInterface) {
                if (DialogFragment.this.mDialog != null) {
                    final DialogFragment this$0 = DialogFragment.this;
                    this$0.onDismiss((DialogInterface)this$0.mDialog);
                }
            }
        };
        this.mStyle = 0;
        this.mTheme = 0;
        this.mCancelable = true;
        this.mShowsDialog = true;
        this.mBackStackId = -1;
        this.mObserver = new Observer<LifecycleOwner>() {
            @SuppressLint({ "SyntheticAccessor" })
            @Override
            public void onChanged(final LifecycleOwner lifecycleOwner) {
                if (lifecycleOwner != null && DialogFragment.this.mShowsDialog) {
                    final View requireView = DialogFragment.this.requireView();
                    if (requireView.getParent() != null) {
                        throw new IllegalStateException("DialogFragment can not be attached to a container view");
                    }
                    if (DialogFragment.this.mDialog != null) {
                        if (FragmentManager.isLoggingEnabled(3)) {
                            final StringBuilder sb = new StringBuilder();
                            sb.append("DialogFragment ");
                            sb.append(this);
                            sb.append(" setting the content view on ");
                            sb.append(DialogFragment.this.mDialog);
                            Log.d("FragmentManager", sb.toString());
                        }
                        DialogFragment.this.mDialog.setContentView(requireView);
                    }
                }
            }
        };
        this.mDialogCreated = false;
    }
    
    private void dismissInternal(final boolean b, final boolean b2) {
        if (this.mDismissed) {
            return;
        }
        this.mDismissed = true;
        this.mShownByMe = false;
        final Dialog mDialog = this.mDialog;
        if (mDialog != null) {
            mDialog.setOnDismissListener((DialogInterface$OnDismissListener)null);
            this.mDialog.dismiss();
            if (!b2) {
                if (Looper.myLooper() == this.mHandler.getLooper()) {
                    this.onDismiss((DialogInterface)this.mDialog);
                }
                else {
                    this.mHandler.post(this.mDismissRunnable);
                }
            }
        }
        this.mViewDestroyed = true;
        if (this.mBackStackId >= 0) {
            this.getParentFragmentManager().popBackStack(this.mBackStackId, 1);
            this.mBackStackId = -1;
        }
        else {
            final FragmentTransaction beginTransaction = this.getParentFragmentManager().beginTransaction();
            beginTransaction.remove(this);
            if (b) {
                beginTransaction.commitAllowingStateLoss();
            }
            else {
                beginTransaction.commit();
            }
        }
    }
    
    private void prepareDialog(final Bundle bundle) {
        if (!this.mShowsDialog) {
            return;
        }
        if (!this.mDialogCreated) {
            try {
                this.mCreatingDialog = true;
                this.setupDialog(this.mDialog = this.onCreateDialog(bundle), this.mStyle);
                final FragmentActivity activity = this.getActivity();
                if (activity != null) {
                    this.mDialog.setOwnerActivity((Activity)activity);
                }
                this.mDialog.setCancelable(this.mCancelable);
                this.mDialog.setOnCancelListener(this.mOnCancelListener);
                this.mDialog.setOnDismissListener(this.mOnDismissListener);
            }
            finally {
                this.mCreatingDialog = false;
            }
        }
        this.mDialogCreated = true;
    }
    
    public Dialog getDialog() {
        return this.mDialog;
    }
    
    public int getTheme() {
        return this.mTheme;
    }
    
    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        this.getViewLifecycleOwnerLiveData().observeForever(this.mObserver);
        if (!this.mShownByMe) {
            this.mDismissed = false;
        }
    }
    
    public void onCancel(final DialogInterface dialogInterface) {
    }
    
    @Override
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.mHandler = new Handler();
        this.mShowsDialog = (super.mContainerId == 0);
        if (bundle != null) {
            this.mStyle = bundle.getInt("android:style", 0);
            this.mTheme = bundle.getInt("android:theme", 0);
            this.mCancelable = bundle.getBoolean("android:cancelable", true);
            this.mShowsDialog = bundle.getBoolean("android:showsDialog", this.mShowsDialog);
            this.mBackStackId = bundle.getInt("android:backStackId", -1);
        }
    }
    
    public Dialog onCreateDialog(final Bundle bundle) {
        if (FragmentManager.isLoggingEnabled(3)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("onCreateDialog called for DialogFragment ");
            sb.append(this);
            Log.d("FragmentManager", sb.toString());
        }
        return new Dialog(this.requireContext(), this.getTheme());
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        final Dialog mDialog = this.mDialog;
        if (mDialog != null) {
            this.mViewDestroyed = true;
            mDialog.setOnDismissListener((DialogInterface$OnDismissListener)null);
            this.mDialog.dismiss();
            if (!this.mDismissed) {
                this.onDismiss((DialogInterface)this.mDialog);
            }
            this.mDialog = null;
            this.mDialogCreated = false;
        }
    }
    
    @Override
    public void onDetach() {
        super.onDetach();
        if (!this.mShownByMe && !this.mDismissed) {
            this.mDismissed = true;
        }
        this.getViewLifecycleOwnerLiveData().removeObserver(this.mObserver);
    }
    
    public void onDismiss(final DialogInterface dialogInterface) {
        if (!this.mViewDestroyed) {
            if (FragmentManager.isLoggingEnabled(3)) {
                final StringBuilder sb = new StringBuilder();
                sb.append("onDismiss called for DialogFragment ");
                sb.append(this);
                Log.d("FragmentManager", sb.toString());
            }
            this.dismissInternal(true, true);
        }
    }
    
    @Override
    public LayoutInflater onGetLayoutInflater(final Bundle bundle) {
        final LayoutInflater onGetLayoutInflater = super.onGetLayoutInflater(bundle);
        if (this.mShowsDialog && !this.mCreatingDialog) {
            this.prepareDialog(bundle);
            if (FragmentManager.isLoggingEnabled(2)) {
                final StringBuilder sb = new StringBuilder();
                sb.append("get layout inflater for DialogFragment ");
                sb.append(this);
                sb.append(" from dialog context");
                Log.d("FragmentManager", sb.toString());
            }
            return onGetLayoutInflater.cloneInContext(this.requireDialog().getContext());
        }
        if (FragmentManager.isLoggingEnabled(2)) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("getting layout inflater for DialogFragment ");
            sb2.append(this);
            final String string = sb2.toString();
            if (!this.mShowsDialog) {
                final StringBuilder sb3 = new StringBuilder();
                sb3.append("mShowsDialog = false: ");
                sb3.append(string);
                Log.d("FragmentManager", sb3.toString());
            }
            else {
                final StringBuilder sb4 = new StringBuilder();
                sb4.append("mCreatingDialog = true: ");
                sb4.append(string);
                Log.d("FragmentManager", sb4.toString());
            }
        }
        return onGetLayoutInflater;
    }
    
    @Override
    public void onSaveInstanceState(final Bundle bundle) {
        super.onSaveInstanceState(bundle);
        final Dialog mDialog = this.mDialog;
        if (mDialog != null) {
            bundle.putBundle("android:savedDialogState", mDialog.onSaveInstanceState());
        }
        final int mStyle = this.mStyle;
        if (mStyle != 0) {
            bundle.putInt("android:style", mStyle);
        }
        final int mTheme = this.mTheme;
        if (mTheme != 0) {
            bundle.putInt("android:theme", mTheme);
        }
        final boolean mCancelable = this.mCancelable;
        if (!mCancelable) {
            bundle.putBoolean("android:cancelable", mCancelable);
        }
        final boolean mShowsDialog = this.mShowsDialog;
        if (!mShowsDialog) {
            bundle.putBoolean("android:showsDialog", mShowsDialog);
        }
        final int mBackStackId = this.mBackStackId;
        if (mBackStackId != -1) {
            bundle.putInt("android:backStackId", mBackStackId);
        }
    }
    
    @Override
    public void onStart() {
        super.onStart();
        final Dialog mDialog = this.mDialog;
        if (mDialog != null) {
            this.mViewDestroyed = false;
            mDialog.show();
        }
    }
    
    @Override
    public void onStop() {
        super.onStop();
        final Dialog mDialog = this.mDialog;
        if (mDialog != null) {
            mDialog.hide();
        }
    }
    
    @Override
    public void onViewStateRestored(Bundle bundle) {
        super.onViewStateRestored(bundle);
        if (this.mDialog != null && bundle != null) {
            bundle = bundle.getBundle("android:savedDialogState");
            if (bundle != null) {
                this.mDialog.onRestoreInstanceState(bundle);
            }
        }
    }
    
    @Override
    void performCreateView(final LayoutInflater layoutInflater, final ViewGroup viewGroup, final Bundle bundle) {
        super.performCreateView(layoutInflater, viewGroup, bundle);
        if (super.mView == null && this.mDialog != null && bundle != null) {
            final Bundle bundle2 = bundle.getBundle("android:savedDialogState");
            if (bundle2 != null) {
                this.mDialog.onRestoreInstanceState(bundle2);
            }
        }
    }
    
    public final Dialog requireDialog() {
        final Dialog dialog = this.getDialog();
        if (dialog != null) {
            return dialog;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("DialogFragment ");
        sb.append(this);
        sb.append(" does not have a Dialog.");
        throw new IllegalStateException(sb.toString());
    }
    
    public void setCancelable(final boolean b) {
        this.mCancelable = b;
        final Dialog mDialog = this.mDialog;
        if (mDialog != null) {
            mDialog.setCancelable(b);
        }
    }
    
    public void setupDialog(final Dialog dialog, final int n) {
        if (n != 1 && n != 2) {
            if (n != 3) {
                return;
            }
            final Window window = dialog.getWindow();
            if (window != null) {
                window.addFlags(24);
            }
        }
        dialog.requestWindowFeature(1);
    }
}
