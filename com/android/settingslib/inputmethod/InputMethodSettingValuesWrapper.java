// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.inputmethod;

import java.util.Collection;
import android.content.Context;
import android.view.inputmethod.InputMethodInfo;
import java.util.ArrayList;
import android.view.inputmethod.InputMethodManager;

public class InputMethodSettingValuesWrapper
{
    private static final String TAG = "InputMethodSettingValuesWrapper";
    private static volatile InputMethodSettingValuesWrapper sInstance;
    private final InputMethodManager mImm;
    private final ArrayList<InputMethodInfo> mMethodList;
    
    private InputMethodSettingValuesWrapper(final Context context) {
        this.mMethodList = new ArrayList<InputMethodInfo>();
        context.getContentResolver();
        this.mImm = (InputMethodManager)context.getSystemService((Class)InputMethodManager.class);
        this.refreshAllInputMethodAndSubtypes();
    }
    
    public static InputMethodSettingValuesWrapper getInstance(final Context context) {
        if (InputMethodSettingValuesWrapper.sInstance == null) {
            synchronized (InputMethodSettingValuesWrapper.TAG) {
                if (InputMethodSettingValuesWrapper.sInstance == null) {
                    InputMethodSettingValuesWrapper.sInstance = new InputMethodSettingValuesWrapper(context);
                }
            }
        }
        return InputMethodSettingValuesWrapper.sInstance;
    }
    
    public void refreshAllInputMethodAndSubtypes() {
        this.mMethodList.clear();
        this.mMethodList.addAll(this.mImm.getInputMethodList());
    }
}
