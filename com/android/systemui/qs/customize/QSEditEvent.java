// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.customize;

import com.android.internal.logging.UiEventLogger$UiEventEnum;

public enum QSEditEvent implements UiEventLogger$UiEventEnum
{
    QS_EDIT_ADD(211), 
    QS_EDIT_CLOSED(214), 
    QS_EDIT_MOVE(212), 
    QS_EDIT_OPEN(213), 
    QS_EDIT_REMOVE(210), 
    QS_EDIT_RESET(215);
    
    private final int _id;
    
    private QSEditEvent(final int id) {
        this._id = id;
    }
    
    public int getId() {
        return this._id;
    }
}
