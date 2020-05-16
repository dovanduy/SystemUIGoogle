// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.inputmethod;

import android.view.inputmethod.InputMethodSubtype;
import android.view.inputmethod.InputMethodInfo;
import android.text.TextUtils$SimpleStringSplitter;

public class InputMethodAndSubtypeUtil
{
    static {
        new TextUtils$SimpleStringSplitter(':');
        new TextUtils$SimpleStringSplitter(';');
    }
    
    public static boolean isValidNonAuxAsciiCapableIme(final InputMethodInfo inputMethodInfo) {
        if (inputMethodInfo.isAuxiliaryIme()) {
            return false;
        }
        for (int subtypeCount = inputMethodInfo.getSubtypeCount(), i = 0; i < subtypeCount; ++i) {
            final InputMethodSubtype subtype = inputMethodInfo.getSubtypeAt(i);
            if ("keyboard".equalsIgnoreCase(subtype.getMode()) && subtype.isAsciiCapable()) {
                return true;
            }
        }
        return false;
    }
}
