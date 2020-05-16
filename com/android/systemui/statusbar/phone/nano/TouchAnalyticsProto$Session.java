// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone.nano;

import com.google.protobuf.nano.InternalNano;
import java.io.IOException;
import com.google.protobuf.nano.WireFormatNano;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.MessageNano;

public final class TouchAnalyticsProto$Session extends MessageNano
{
    public String build;
    public String deviceId;
    public long durationMillis;
    public PhoneEvent[] phoneEvents;
    public int result;
    public SensorEvent[] sensorEvents;
    public long startTimestampMillis;
    public int touchAreaHeight;
    public int touchAreaWidth;
    public TouchEvent[] touchEvents;
    public int type;
    
    public TouchAnalyticsProto$Session() {
        this.clear();
    }
    
    public TouchAnalyticsProto$Session clear() {
        this.startTimestampMillis = 0L;
        this.durationMillis = 0L;
        this.build = "";
        this.result = 0;
        this.touchEvents = TouchEvent.emptyArray();
        this.sensorEvents = SensorEvent.emptyArray();
        this.touchAreaWidth = 0;
        this.touchAreaHeight = 0;
        this.type = 0;
        this.phoneEvents = PhoneEvent.emptyArray();
        this.deviceId = "";
        super.cachedSize = -1;
        return this;
    }
    
    @Override
    protected int computeSerializedSize() {
        final int computeSerializedSize = super.computeSerializedSize();
        final long startTimestampMillis = this.startTimestampMillis;
        int n = computeSerializedSize;
        if (startTimestampMillis != 0L) {
            n = computeSerializedSize + CodedOutputByteBufferNano.computeUInt64Size(1, startTimestampMillis);
        }
        final long durationMillis = this.durationMillis;
        int n2 = n;
        if (durationMillis != 0L) {
            n2 = n + CodedOutputByteBufferNano.computeUInt64Size(2, durationMillis);
        }
        int n3 = n2;
        if (!this.build.equals("")) {
            n3 = n2 + CodedOutputByteBufferNano.computeStringSize(3, this.build);
        }
        final int result = this.result;
        int n4 = n3;
        if (result != 0) {
            n4 = n3 + CodedOutputByteBufferNano.computeInt32Size(4, result);
        }
        final TouchEvent[] touchEvents = this.touchEvents;
        final int n5 = 0;
        int n6 = n4;
        if (touchEvents != null) {
            n6 = n4;
            if (touchEvents.length > 0) {
                int n7 = 0;
                while (true) {
                    final TouchEvent[] touchEvents2 = this.touchEvents;
                    n6 = n4;
                    if (n7 >= touchEvents2.length) {
                        break;
                    }
                    final TouchEvent touchEvent = touchEvents2[n7];
                    int n8 = n4;
                    if (touchEvent != null) {
                        n8 = n4 + CodedOutputByteBufferNano.computeMessageSize(5, touchEvent);
                    }
                    ++n7;
                    n4 = n8;
                }
            }
        }
        final SensorEvent[] sensorEvents = this.sensorEvents;
        int n9 = n6;
        if (sensorEvents != null) {
            n9 = n6;
            if (sensorEvents.length > 0) {
                int n10 = 0;
                while (true) {
                    final SensorEvent[] sensorEvents2 = this.sensorEvents;
                    n9 = n6;
                    if (n10 >= sensorEvents2.length) {
                        break;
                    }
                    final SensorEvent sensorEvent = sensorEvents2[n10];
                    int n11 = n6;
                    if (sensorEvent != null) {
                        n11 = n6 + CodedOutputByteBufferNano.computeMessageSize(6, sensorEvent);
                    }
                    ++n10;
                    n6 = n11;
                }
            }
        }
        final int touchAreaWidth = this.touchAreaWidth;
        int n12 = n9;
        if (touchAreaWidth != 0) {
            n12 = n9 + CodedOutputByteBufferNano.computeInt32Size(9, touchAreaWidth);
        }
        final int touchAreaHeight = this.touchAreaHeight;
        int n13 = n12;
        if (touchAreaHeight != 0) {
            n13 = n12 + CodedOutputByteBufferNano.computeInt32Size(10, touchAreaHeight);
        }
        final int type = this.type;
        int n14 = n13;
        if (type != 0) {
            n14 = n13 + CodedOutputByteBufferNano.computeInt32Size(11, type);
        }
        final PhoneEvent[] phoneEvents = this.phoneEvents;
        int n15 = n14;
        if (phoneEvents != null) {
            n15 = n14;
            if (phoneEvents.length > 0) {
                int n16 = n5;
                while (true) {
                    final PhoneEvent[] phoneEvents2 = this.phoneEvents;
                    n15 = n14;
                    if (n16 >= phoneEvents2.length) {
                        break;
                    }
                    final PhoneEvent phoneEvent = phoneEvents2[n16];
                    int n17 = n14;
                    if (phoneEvent != null) {
                        n17 = n14 + CodedOutputByteBufferNano.computeMessageSize(12, phoneEvent);
                    }
                    ++n16;
                    n14 = n17;
                }
            }
        }
        int n18 = n15;
        if (!this.deviceId.equals("")) {
            n18 = n15 + CodedOutputByteBufferNano.computeStringSize(13, this.deviceId);
        }
        return n18;
    }
    
    @Override
    public TouchAnalyticsProto$Session mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            final int tag = codedInputByteBufferNano.readTag();
            switch (tag) {
                default: {
                    if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                        return this;
                    }
                    continue;
                }
                case 106: {
                    this.deviceId = codedInputByteBufferNano.readString();
                    continue;
                }
                case 98: {
                    final int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 98);
                    final PhoneEvent[] phoneEvents = this.phoneEvents;
                    int length;
                    if (phoneEvents == null) {
                        length = 0;
                    }
                    else {
                        length = phoneEvents.length;
                    }
                    final int n = repeatedFieldArrayLength + length;
                    final PhoneEvent[] phoneEvents2 = new PhoneEvent[n];
                    int i = length;
                    if (length != 0) {
                        System.arraycopy(this.phoneEvents, 0, phoneEvents2, 0, length);
                        i = length;
                    }
                    while (i < n - 1) {
                        codedInputByteBufferNano.readMessage(phoneEvents2[i] = new PhoneEvent());
                        codedInputByteBufferNano.readTag();
                        ++i;
                    }
                    codedInputByteBufferNano.readMessage(phoneEvents2[i] = new PhoneEvent());
                    this.phoneEvents = phoneEvents2;
                    continue;
                }
                case 88: {
                    final int int32 = codedInputByteBufferNano.readInt32();
                    if (int32 != 0 && int32 != 1 && int32 != 2 && int32 != 3 && int32 != 4) {
                        continue;
                    }
                    this.type = int32;
                    continue;
                }
                case 80: {
                    this.touchAreaHeight = codedInputByteBufferNano.readInt32();
                    continue;
                }
                case 72: {
                    this.touchAreaWidth = codedInputByteBufferNano.readInt32();
                    continue;
                }
                case 50: {
                    final int repeatedFieldArrayLength2 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 50);
                    final SensorEvent[] sensorEvents = this.sensorEvents;
                    int length2;
                    if (sensorEvents == null) {
                        length2 = 0;
                    }
                    else {
                        length2 = sensorEvents.length;
                    }
                    final int n2 = repeatedFieldArrayLength2 + length2;
                    final SensorEvent[] sensorEvents2 = new SensorEvent[n2];
                    int j = length2;
                    if (length2 != 0) {
                        System.arraycopy(this.sensorEvents, 0, sensorEvents2, 0, length2);
                        j = length2;
                    }
                    while (j < n2 - 1) {
                        codedInputByteBufferNano.readMessage(sensorEvents2[j] = new SensorEvent());
                        codedInputByteBufferNano.readTag();
                        ++j;
                    }
                    codedInputByteBufferNano.readMessage(sensorEvents2[j] = new SensorEvent());
                    this.sensorEvents = sensorEvents2;
                    continue;
                }
                case 42: {
                    final int repeatedFieldArrayLength3 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 42);
                    final TouchEvent[] touchEvents = this.touchEvents;
                    int length3;
                    if (touchEvents == null) {
                        length3 = 0;
                    }
                    else {
                        length3 = touchEvents.length;
                    }
                    final int n3 = repeatedFieldArrayLength3 + length3;
                    final TouchEvent[] touchEvents2 = new TouchEvent[n3];
                    int k = length3;
                    if (length3 != 0) {
                        System.arraycopy(this.touchEvents, 0, touchEvents2, 0, length3);
                        k = length3;
                    }
                    while (k < n3 - 1) {
                        codedInputByteBufferNano.readMessage(touchEvents2[k] = new TouchEvent());
                        codedInputByteBufferNano.readTag();
                        ++k;
                    }
                    codedInputByteBufferNano.readMessage(touchEvents2[k] = new TouchEvent());
                    this.touchEvents = touchEvents2;
                    continue;
                }
                case 32: {
                    final int int33 = codedInputByteBufferNano.readInt32();
                    if (int33 != 0 && int33 != 1 && int33 != 2) {
                        continue;
                    }
                    this.result = int33;
                    continue;
                }
                case 26: {
                    this.build = codedInputByteBufferNano.readString();
                    continue;
                }
                case 16: {
                    this.durationMillis = codedInputByteBufferNano.readUInt64();
                    continue;
                }
                case 8: {
                    this.startTimestampMillis = codedInputByteBufferNano.readUInt64();
                    continue;
                }
                case 0: {
                    return this;
                }
            }
        }
    }
    
    @Override
    public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        final long startTimestampMillis = this.startTimestampMillis;
        if (startTimestampMillis != 0L) {
            codedOutputByteBufferNano.writeUInt64(1, startTimestampMillis);
        }
        final long durationMillis = this.durationMillis;
        if (durationMillis != 0L) {
            codedOutputByteBufferNano.writeUInt64(2, durationMillis);
        }
        if (!this.build.equals("")) {
            codedOutputByteBufferNano.writeString(3, this.build);
        }
        final int result = this.result;
        if (result != 0) {
            codedOutputByteBufferNano.writeInt32(4, result);
        }
        final TouchEvent[] touchEvents = this.touchEvents;
        final int n = 0;
        if (touchEvents != null && touchEvents.length > 0) {
            int n2 = 0;
            while (true) {
                final TouchEvent[] touchEvents2 = this.touchEvents;
                if (n2 >= touchEvents2.length) {
                    break;
                }
                final TouchEvent touchEvent = touchEvents2[n2];
                if (touchEvent != null) {
                    codedOutputByteBufferNano.writeMessage(5, touchEvent);
                }
                ++n2;
            }
        }
        final SensorEvent[] sensorEvents = this.sensorEvents;
        if (sensorEvents != null && sensorEvents.length > 0) {
            int n3 = 0;
            while (true) {
                final SensorEvent[] sensorEvents2 = this.sensorEvents;
                if (n3 >= sensorEvents2.length) {
                    break;
                }
                final SensorEvent sensorEvent = sensorEvents2[n3];
                if (sensorEvent != null) {
                    codedOutputByteBufferNano.writeMessage(6, sensorEvent);
                }
                ++n3;
            }
        }
        final int touchAreaWidth = this.touchAreaWidth;
        if (touchAreaWidth != 0) {
            codedOutputByteBufferNano.writeInt32(9, touchAreaWidth);
        }
        final int touchAreaHeight = this.touchAreaHeight;
        if (touchAreaHeight != 0) {
            codedOutputByteBufferNano.writeInt32(10, touchAreaHeight);
        }
        final int type = this.type;
        if (type != 0) {
            codedOutputByteBufferNano.writeInt32(11, type);
        }
        final PhoneEvent[] phoneEvents = this.phoneEvents;
        if (phoneEvents != null && phoneEvents.length > 0) {
            int n4 = n;
            while (true) {
                final PhoneEvent[] phoneEvents2 = this.phoneEvents;
                if (n4 >= phoneEvents2.length) {
                    break;
                }
                final PhoneEvent phoneEvent = phoneEvents2[n4];
                if (phoneEvent != null) {
                    codedOutputByteBufferNano.writeMessage(12, phoneEvent);
                }
                ++n4;
            }
        }
        if (!this.deviceId.equals("")) {
            codedOutputByteBufferNano.writeString(13, this.deviceId);
        }
        super.writeTo(codedOutputByteBufferNano);
    }
    
    public static final class PhoneEvent extends MessageNano
    {
        private static volatile PhoneEvent[] _emptyArray;
        public long timeOffsetNanos;
        public int type;
        
        public PhoneEvent() {
            this.clear();
        }
        
        public static PhoneEvent[] emptyArray() {
            if (PhoneEvent._emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (PhoneEvent._emptyArray == null) {
                        PhoneEvent._emptyArray = new PhoneEvent[0];
                    }
                }
            }
            return PhoneEvent._emptyArray;
        }
        
        public PhoneEvent clear() {
            this.type = 0;
            this.timeOffsetNanos = 0L;
            super.cachedSize = -1;
            return this;
        }
        
        @Override
        protected int computeSerializedSize() {
            final int computeSerializedSize = super.computeSerializedSize();
            final int type = this.type;
            int n = computeSerializedSize;
            if (type != 0) {
                n = computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(1, type);
            }
            final long timeOffsetNanos = this.timeOffsetNanos;
            int n2 = n;
            if (timeOffsetNanos != 0L) {
                n2 = n + CodedOutputByteBufferNano.computeUInt64Size(2, timeOffsetNanos);
            }
            return n2;
        }
        
        @Override
        public PhoneEvent mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                final int tag = codedInputByteBufferNano.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag != 8) {
                    if (tag != 16) {
                        if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                            return this;
                        }
                        continue;
                    }
                    else {
                        this.timeOffsetNanos = codedInputByteBufferNano.readUInt64();
                    }
                }
                else {
                    final int int32 = codedInputByteBufferNano.readInt32();
                    switch (int32) {
                        default: {
                            continue;
                        }
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                        case 10:
                        case 11:
                        case 12:
                        case 13:
                        case 14:
                        case 15:
                        case 16:
                        case 17:
                        case 18:
                        case 19:
                        case 20:
                        case 21:
                        case 22:
                        case 23:
                        case 24:
                        case 25:
                        case 26:
                        case 27:
                        case 28: {
                            this.type = int32;
                            continue;
                        }
                    }
                }
            }
        }
        
        @Override
        public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            final int type = this.type;
            if (type != 0) {
                codedOutputByteBufferNano.writeInt32(1, type);
            }
            final long timeOffsetNanos = this.timeOffsetNanos;
            if (timeOffsetNanos != 0L) {
                codedOutputByteBufferNano.writeUInt64(2, timeOffsetNanos);
            }
            super.writeTo(codedOutputByteBufferNano);
        }
    }
    
    public static final class SensorEvent extends MessageNano
    {
        private static volatile SensorEvent[] _emptyArray;
        public long timeOffsetNanos;
        public long timestamp;
        public int type;
        public float[] values;
        
        public SensorEvent() {
            this.clear();
        }
        
        public static SensorEvent[] emptyArray() {
            if (SensorEvent._emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (SensorEvent._emptyArray == null) {
                        SensorEvent._emptyArray = new SensorEvent[0];
                    }
                }
            }
            return SensorEvent._emptyArray;
        }
        
        public SensorEvent clear() {
            this.type = 1;
            this.timeOffsetNanos = 0L;
            this.values = WireFormatNano.EMPTY_FLOAT_ARRAY;
            this.timestamp = 0L;
            super.cachedSize = -1;
            return this;
        }
        
        @Override
        protected int computeSerializedSize() {
            final int computeSerializedSize = super.computeSerializedSize();
            final int type = this.type;
            int n = computeSerializedSize;
            if (type != 1) {
                n = computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(1, type);
            }
            final long timeOffsetNanos = this.timeOffsetNanos;
            int n2 = n;
            if (timeOffsetNanos != 0L) {
                n2 = n + CodedOutputByteBufferNano.computeUInt64Size(2, timeOffsetNanos);
            }
            final float[] values = this.values;
            int n3 = n2;
            if (values != null) {
                n3 = n2;
                if (values.length > 0) {
                    n3 = n2 + values.length * 4 + values.length * 1;
                }
            }
            final long timestamp = this.timestamp;
            int n4 = n3;
            if (timestamp != 0L) {
                n4 = n3 + CodedOutputByteBufferNano.computeUInt64Size(4, timestamp);
            }
            return n4;
        }
        
        @Override
        public SensorEvent mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                final int tag = codedInputByteBufferNano.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag != 8) {
                    if (tag != 16) {
                        if (tag != 26) {
                            if (tag != 29) {
                                if (tag != 32) {
                                    if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                                        return this;
                                    }
                                    continue;
                                }
                                else {
                                    this.timestamp = codedInputByteBufferNano.readUInt64();
                                }
                            }
                            else {
                                final int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 29);
                                final float[] values = this.values;
                                int length;
                                if (values == null) {
                                    length = 0;
                                }
                                else {
                                    length = values.length;
                                }
                                final int n = repeatedFieldArrayLength + length;
                                final float[] values2 = new float[n];
                                int i = length;
                                if (length != 0) {
                                    System.arraycopy(this.values, 0, values2, 0, length);
                                    i = length;
                                }
                                while (i < n - 1) {
                                    values2[i] = codedInputByteBufferNano.readFloat();
                                    codedInputByteBufferNano.readTag();
                                    ++i;
                                }
                                values2[i] = codedInputByteBufferNano.readFloat();
                                this.values = values2;
                            }
                        }
                        else {
                            final int rawVarint32 = codedInputByteBufferNano.readRawVarint32();
                            final int pushLimit = codedInputByteBufferNano.pushLimit(rawVarint32);
                            final int n2 = rawVarint32 / 4;
                            final float[] values3 = this.values;
                            int length2;
                            if (values3 == null) {
                                length2 = 0;
                            }
                            else {
                                length2 = values3.length;
                            }
                            final int n3 = n2 + length2;
                            final float[] values4 = new float[n3];
                            int j = length2;
                            if (length2 != 0) {
                                System.arraycopy(this.values, 0, values4, 0, length2);
                                j = length2;
                            }
                            while (j < n3) {
                                values4[j] = codedInputByteBufferNano.readFloat();
                                ++j;
                            }
                            this.values = values4;
                            codedInputByteBufferNano.popLimit(pushLimit);
                        }
                    }
                    else {
                        this.timeOffsetNanos = codedInputByteBufferNano.readUInt64();
                    }
                }
                else {
                    final int int32 = codedInputByteBufferNano.readInt32();
                    if (int32 != 1 && int32 != 8 && int32 != 11 && int32 != 4 && int32 != 5) {
                        continue;
                    }
                    this.type = int32;
                }
            }
        }
        
        @Override
        public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            final int type = this.type;
            if (type != 1) {
                codedOutputByteBufferNano.writeInt32(1, type);
            }
            final long timeOffsetNanos = this.timeOffsetNanos;
            if (timeOffsetNanos != 0L) {
                codedOutputByteBufferNano.writeUInt64(2, timeOffsetNanos);
            }
            final float[] values = this.values;
            if (values != null && values.length > 0) {
                int n = 0;
                while (true) {
                    final float[] values2 = this.values;
                    if (n >= values2.length) {
                        break;
                    }
                    codedOutputByteBufferNano.writeFloat(3, values2[n]);
                    ++n;
                }
            }
            final long timestamp = this.timestamp;
            if (timestamp != 0L) {
                codedOutputByteBufferNano.writeUInt64(4, timestamp);
            }
            super.writeTo(codedOutputByteBufferNano);
        }
    }
    
    public static final class TouchEvent extends MessageNano
    {
        private static volatile TouchEvent[] _emptyArray;
        public int action;
        public int actionIndex;
        public Pointer[] pointers;
        public BoundingBox removedBoundingBox;
        public boolean removedRedacted;
        public long timeOffsetNanos;
        
        public TouchEvent() {
            this.clear();
        }
        
        public static TouchEvent[] emptyArray() {
            if (TouchEvent._emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (TouchEvent._emptyArray == null) {
                        TouchEvent._emptyArray = new TouchEvent[0];
                    }
                }
            }
            return TouchEvent._emptyArray;
        }
        
        public TouchEvent clear() {
            this.timeOffsetNanos = 0L;
            this.action = 0;
            this.actionIndex = 0;
            this.pointers = Pointer.emptyArray();
            this.removedRedacted = false;
            this.removedBoundingBox = null;
            super.cachedSize = -1;
            return this;
        }
        
        @Override
        protected int computeSerializedSize() {
            final int computeSerializedSize = super.computeSerializedSize();
            final long timeOffsetNanos = this.timeOffsetNanos;
            int n = computeSerializedSize;
            if (timeOffsetNanos != 0L) {
                n = computeSerializedSize + CodedOutputByteBufferNano.computeUInt64Size(1, timeOffsetNanos);
            }
            final int action = this.action;
            int n2 = n;
            if (action != 0) {
                n2 = n + CodedOutputByteBufferNano.computeInt32Size(2, action);
            }
            final int actionIndex = this.actionIndex;
            int n3 = n2;
            if (actionIndex != 0) {
                n3 = n2 + CodedOutputByteBufferNano.computeInt32Size(3, actionIndex);
            }
            final Pointer[] pointers = this.pointers;
            int n4 = n3;
            if (pointers != null) {
                n4 = n3;
                if (pointers.length > 0) {
                    int n5 = 0;
                    while (true) {
                        final Pointer[] pointers2 = this.pointers;
                        n4 = n3;
                        if (n5 >= pointers2.length) {
                            break;
                        }
                        final Pointer pointer = pointers2[n5];
                        int n6 = n3;
                        if (pointer != null) {
                            n6 = n3 + CodedOutputByteBufferNano.computeMessageSize(4, pointer);
                        }
                        ++n5;
                        n3 = n6;
                    }
                }
            }
            final boolean removedRedacted = this.removedRedacted;
            int n7 = n4;
            if (removedRedacted) {
                n7 = n4 + CodedOutputByteBufferNano.computeBoolSize(5, removedRedacted);
            }
            final BoundingBox removedBoundingBox = this.removedBoundingBox;
            int n8 = n7;
            if (removedBoundingBox != null) {
                n8 = n7 + CodedOutputByteBufferNano.computeMessageSize(6, removedBoundingBox);
            }
            return n8;
        }
        
        @Override
        public TouchEvent mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                final int tag = codedInputByteBufferNano.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag != 8) {
                    if (tag != 16) {
                        if (tag != 24) {
                            if (tag != 34) {
                                if (tag != 40) {
                                    if (tag != 50) {
                                        if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                                            return this;
                                        }
                                        continue;
                                    }
                                    else {
                                        if (this.removedBoundingBox == null) {
                                            this.removedBoundingBox = new BoundingBox();
                                        }
                                        codedInputByteBufferNano.readMessage(this.removedBoundingBox);
                                    }
                                }
                                else {
                                    this.removedRedacted = codedInputByteBufferNano.readBool();
                                }
                            }
                            else {
                                final int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 34);
                                final Pointer[] pointers = this.pointers;
                                int length;
                                if (pointers == null) {
                                    length = 0;
                                }
                                else {
                                    length = pointers.length;
                                }
                                final int n = repeatedFieldArrayLength + length;
                                final Pointer[] pointers2 = new Pointer[n];
                                int i = length;
                                if (length != 0) {
                                    System.arraycopy(this.pointers, 0, pointers2, 0, length);
                                    i = length;
                                }
                                while (i < n - 1) {
                                    codedInputByteBufferNano.readMessage(pointers2[i] = new Pointer());
                                    codedInputByteBufferNano.readTag();
                                    ++i;
                                }
                                codedInputByteBufferNano.readMessage(pointers2[i] = new Pointer());
                                this.pointers = pointers2;
                            }
                        }
                        else {
                            this.actionIndex = codedInputByteBufferNano.readInt32();
                        }
                    }
                    else {
                        final int int32 = codedInputByteBufferNano.readInt32();
                        switch (int32) {
                            default: {
                                continue;
                            }
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6: {
                                this.action = int32;
                                continue;
                            }
                        }
                    }
                }
                else {
                    this.timeOffsetNanos = codedInputByteBufferNano.readUInt64();
                }
            }
        }
        
        @Override
        public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            final long timeOffsetNanos = this.timeOffsetNanos;
            if (timeOffsetNanos != 0L) {
                codedOutputByteBufferNano.writeUInt64(1, timeOffsetNanos);
            }
            final int action = this.action;
            if (action != 0) {
                codedOutputByteBufferNano.writeInt32(2, action);
            }
            final int actionIndex = this.actionIndex;
            if (actionIndex != 0) {
                codedOutputByteBufferNano.writeInt32(3, actionIndex);
            }
            final Pointer[] pointers = this.pointers;
            if (pointers != null && pointers.length > 0) {
                int n = 0;
                while (true) {
                    final Pointer[] pointers2 = this.pointers;
                    if (n >= pointers2.length) {
                        break;
                    }
                    final Pointer pointer = pointers2[n];
                    if (pointer != null) {
                        codedOutputByteBufferNano.writeMessage(4, pointer);
                    }
                    ++n;
                }
            }
            final boolean removedRedacted = this.removedRedacted;
            if (removedRedacted) {
                codedOutputByteBufferNano.writeBool(5, removedRedacted);
            }
            final BoundingBox removedBoundingBox = this.removedBoundingBox;
            if (removedBoundingBox != null) {
                codedOutputByteBufferNano.writeMessage(6, removedBoundingBox);
            }
            super.writeTo(codedOutputByteBufferNano);
        }
        
        public static final class BoundingBox extends MessageNano
        {
            public float height;
            public float width;
            
            public BoundingBox() {
                this.clear();
            }
            
            public BoundingBox clear() {
                this.width = 0.0f;
                this.height = 0.0f;
                super.cachedSize = -1;
                return this;
            }
            
            @Override
            protected int computeSerializedSize() {
                int computeSerializedSize;
                final int n = computeSerializedSize = super.computeSerializedSize();
                if (Float.floatToIntBits(this.width) != Float.floatToIntBits(0.0f)) {
                    computeSerializedSize = n + CodedOutputByteBufferNano.computeFloatSize(1, this.width);
                }
                int n2 = computeSerializedSize;
                if (Float.floatToIntBits(this.height) != Float.floatToIntBits(0.0f)) {
                    n2 = computeSerializedSize + CodedOutputByteBufferNano.computeFloatSize(2, this.height);
                }
                return n2;
            }
            
            @Override
            public BoundingBox mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    final int tag = codedInputByteBufferNano.readTag();
                    if (tag == 0) {
                        return this;
                    }
                    if (tag != 13) {
                        if (tag != 21) {
                            if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                                return this;
                            }
                            continue;
                        }
                        else {
                            this.height = codedInputByteBufferNano.readFloat();
                        }
                    }
                    else {
                        this.width = codedInputByteBufferNano.readFloat();
                    }
                }
            }
            
            @Override
            public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (Float.floatToIntBits(this.width) != Float.floatToIntBits(0.0f)) {
                    codedOutputByteBufferNano.writeFloat(1, this.width);
                }
                if (Float.floatToIntBits(this.height) != Float.floatToIntBits(0.0f)) {
                    codedOutputByteBufferNano.writeFloat(2, this.height);
                }
                super.writeTo(codedOutputByteBufferNano);
            }
        }
        
        public static final class Pointer extends MessageNano
        {
            private static volatile Pointer[] _emptyArray;
            public int id;
            public float pressure;
            public BoundingBox removedBoundingBox;
            public float removedLength;
            public float size;
            public float x;
            public float y;
            
            public Pointer() {
                this.clear();
            }
            
            public static Pointer[] emptyArray() {
                if (Pointer._emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (Pointer._emptyArray == null) {
                            Pointer._emptyArray = new Pointer[0];
                        }
                    }
                }
                return Pointer._emptyArray;
            }
            
            public Pointer clear() {
                this.x = 0.0f;
                this.y = 0.0f;
                this.size = 0.0f;
                this.pressure = 0.0f;
                this.id = 0;
                this.removedLength = 0.0f;
                this.removedBoundingBox = null;
                super.cachedSize = -1;
                return this;
            }
            
            @Override
            protected int computeSerializedSize() {
                int computeSerializedSize;
                final int n = computeSerializedSize = super.computeSerializedSize();
                if (Float.floatToIntBits(this.x) != Float.floatToIntBits(0.0f)) {
                    computeSerializedSize = n + CodedOutputByteBufferNano.computeFloatSize(1, this.x);
                }
                int n2 = computeSerializedSize;
                if (Float.floatToIntBits(this.y) != Float.floatToIntBits(0.0f)) {
                    n2 = computeSerializedSize + CodedOutputByteBufferNano.computeFloatSize(2, this.y);
                }
                int n3 = n2;
                if (Float.floatToIntBits(this.size) != Float.floatToIntBits(0.0f)) {
                    n3 = n2 + CodedOutputByteBufferNano.computeFloatSize(3, this.size);
                }
                int n4 = n3;
                if (Float.floatToIntBits(this.pressure) != Float.floatToIntBits(0.0f)) {
                    n4 = n3 + CodedOutputByteBufferNano.computeFloatSize(4, this.pressure);
                }
                final int id = this.id;
                int n5 = n4;
                if (id != 0) {
                    n5 = n4 + CodedOutputByteBufferNano.computeInt32Size(5, id);
                }
                int n6 = n5;
                if (Float.floatToIntBits(this.removedLength) != Float.floatToIntBits(0.0f)) {
                    n6 = n5 + CodedOutputByteBufferNano.computeFloatSize(6, this.removedLength);
                }
                final BoundingBox removedBoundingBox = this.removedBoundingBox;
                int n7 = n6;
                if (removedBoundingBox != null) {
                    n7 = n6 + CodedOutputByteBufferNano.computeMessageSize(7, removedBoundingBox);
                }
                return n7;
            }
            
            @Override
            public Pointer mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    final int tag = codedInputByteBufferNano.readTag();
                    if (tag == 0) {
                        return this;
                    }
                    if (tag != 13) {
                        if (tag != 21) {
                            if (tag != 29) {
                                if (tag != 37) {
                                    if (tag != 40) {
                                        if (tag != 53) {
                                            if (tag != 58) {
                                                if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                                                    return this;
                                                }
                                                continue;
                                            }
                                            else {
                                                if (this.removedBoundingBox == null) {
                                                    this.removedBoundingBox = new BoundingBox();
                                                }
                                                codedInputByteBufferNano.readMessage(this.removedBoundingBox);
                                            }
                                        }
                                        else {
                                            this.removedLength = codedInputByteBufferNano.readFloat();
                                        }
                                    }
                                    else {
                                        this.id = codedInputByteBufferNano.readInt32();
                                    }
                                }
                                else {
                                    this.pressure = codedInputByteBufferNano.readFloat();
                                }
                            }
                            else {
                                this.size = codedInputByteBufferNano.readFloat();
                            }
                        }
                        else {
                            this.y = codedInputByteBufferNano.readFloat();
                        }
                    }
                    else {
                        this.x = codedInputByteBufferNano.readFloat();
                    }
                }
            }
            
            @Override
            public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (Float.floatToIntBits(this.x) != Float.floatToIntBits(0.0f)) {
                    codedOutputByteBufferNano.writeFloat(1, this.x);
                }
                if (Float.floatToIntBits(this.y) != Float.floatToIntBits(0.0f)) {
                    codedOutputByteBufferNano.writeFloat(2, this.y);
                }
                if (Float.floatToIntBits(this.size) != Float.floatToIntBits(0.0f)) {
                    codedOutputByteBufferNano.writeFloat(3, this.size);
                }
                if (Float.floatToIntBits(this.pressure) != Float.floatToIntBits(0.0f)) {
                    codedOutputByteBufferNano.writeFloat(4, this.pressure);
                }
                final int id = this.id;
                if (id != 0) {
                    codedOutputByteBufferNano.writeInt32(5, id);
                }
                if (Float.floatToIntBits(this.removedLength) != Float.floatToIntBits(0.0f)) {
                    codedOutputByteBufferNano.writeFloat(6, this.removedLength);
                }
                final BoundingBox removedBoundingBox = this.removedBoundingBox;
                if (removedBoundingBox != null) {
                    codedOutputByteBufferNano.writeMessage(7, removedBoundingBox);
                }
                super.writeTo(codedOutputByteBufferNano);
            }
        }
    }
}
