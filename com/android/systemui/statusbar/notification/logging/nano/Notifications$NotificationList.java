// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.logging.nano;

import java.io.IOException;
import com.google.protobuf.nano.WireFormatNano;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.MessageNano;

public final class Notifications$NotificationList extends MessageNano
{
    public Notifications$Notification[] notifications;
    
    public Notifications$NotificationList() {
        this.clear();
    }
    
    public Notifications$NotificationList clear() {
        this.notifications = Notifications$Notification.emptyArray();
        super.cachedSize = -1;
        return this;
    }
    
    @Override
    protected int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        final Notifications$Notification[] notifications = this.notifications;
        int n = computeSerializedSize;
        if (notifications != null) {
            n = computeSerializedSize;
            if (notifications.length > 0) {
                int n2 = 0;
                while (true) {
                    final Notifications$Notification[] notifications2 = this.notifications;
                    n = computeSerializedSize;
                    if (n2 >= notifications2.length) {
                        break;
                    }
                    final Notifications$Notification notifications$Notification = notifications2[n2];
                    int n3 = computeSerializedSize;
                    if (notifications$Notification != null) {
                        n3 = computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(1, notifications$Notification);
                    }
                    ++n2;
                    computeSerializedSize = n3;
                }
            }
        }
        return n;
    }
    
    @Override
    public Notifications$NotificationList mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            final int tag = codedInputByteBufferNano.readTag();
            if (tag == 0) {
                return this;
            }
            if (tag != 10) {
                if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                    return this;
                }
                continue;
            }
            else {
                final int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 10);
                final Notifications$Notification[] notifications = this.notifications;
                int length;
                if (notifications == null) {
                    length = 0;
                }
                else {
                    length = notifications.length;
                }
                final int n = repeatedFieldArrayLength + length;
                final Notifications$Notification[] notifications2 = new Notifications$Notification[n];
                int i = length;
                if (length != 0) {
                    System.arraycopy(this.notifications, 0, notifications2, 0, length);
                    i = length;
                }
                while (i < n - 1) {
                    codedInputByteBufferNano.readMessage(notifications2[i] = new Notifications$Notification());
                    codedInputByteBufferNano.readTag();
                    ++i;
                }
                codedInputByteBufferNano.readMessage(notifications2[i] = new Notifications$Notification());
                this.notifications = notifications2;
            }
        }
    }
    
    @Override
    public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        final Notifications$Notification[] notifications = this.notifications;
        if (notifications != null && notifications.length > 0) {
            int n = 0;
            while (true) {
                final Notifications$Notification[] notifications2 = this.notifications;
                if (n >= notifications2.length) {
                    break;
                }
                final Notifications$Notification notifications$Notification = notifications2[n];
                if (notifications$Notification != null) {
                    codedOutputByteBufferNano.writeMessage(1, notifications$Notification);
                }
                ++n;
            }
        }
        super.writeTo(codedOutputByteBufferNano);
    }
}
