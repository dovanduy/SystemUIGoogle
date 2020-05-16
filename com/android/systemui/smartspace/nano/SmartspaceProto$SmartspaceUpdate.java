// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.smartspace.nano;

import com.google.protobuf.nano.InternalNano;
import java.io.IOException;
import com.google.protobuf.nano.WireFormatNano;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.MessageNano;

public final class SmartspaceProto$SmartspaceUpdate extends MessageNano
{
    public SmartspaceCard[] card;
    
    public SmartspaceProto$SmartspaceUpdate() {
        this.clear();
    }
    
    public SmartspaceProto$SmartspaceUpdate clear() {
        this.card = SmartspaceCard.emptyArray();
        super.cachedSize = -1;
        return this;
    }
    
    @Override
    protected int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        final SmartspaceCard[] card = this.card;
        int n = computeSerializedSize;
        if (card != null) {
            n = computeSerializedSize;
            if (card.length > 0) {
                int n2 = 0;
                while (true) {
                    final SmartspaceCard[] card2 = this.card;
                    n = computeSerializedSize;
                    if (n2 >= card2.length) {
                        break;
                    }
                    final SmartspaceCard smartspaceCard = card2[n2];
                    int n3 = computeSerializedSize;
                    if (smartspaceCard != null) {
                        n3 = computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(1, smartspaceCard);
                    }
                    ++n2;
                    computeSerializedSize = n3;
                }
            }
        }
        return n;
    }
    
    @Override
    public SmartspaceProto$SmartspaceUpdate mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                final SmartspaceCard[] card = this.card;
                int length;
                if (card == null) {
                    length = 0;
                }
                else {
                    length = card.length;
                }
                final int n = repeatedFieldArrayLength + length;
                final SmartspaceCard[] card2 = new SmartspaceCard[n];
                int i = length;
                if (length != 0) {
                    System.arraycopy(this.card, 0, card2, 0, length);
                    i = length;
                }
                while (i < n - 1) {
                    codedInputByteBufferNano.readMessage(card2[i] = new SmartspaceCard());
                    codedInputByteBufferNano.readTag();
                    ++i;
                }
                codedInputByteBufferNano.readMessage(card2[i] = new SmartspaceCard());
                this.card = card2;
            }
        }
    }
    
    @Override
    public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        final SmartspaceCard[] card = this.card;
        if (card != null && card.length > 0) {
            int n = 0;
            while (true) {
                final SmartspaceCard[] card2 = this.card;
                if (n >= card2.length) {
                    break;
                }
                final SmartspaceCard smartspaceCard = card2[n];
                if (smartspaceCard != null) {
                    codedOutputByteBufferNano.writeMessage(1, smartspaceCard);
                }
                ++n;
            }
        }
        super.writeTo(codedOutputByteBufferNano);
    }
    
    public static final class SmartspaceCard extends MessageNano
    {
        private static volatile SmartspaceCard[] _emptyArray;
        public int cardId;
        public int cardPriority;
        public int cardType;
        public Message duringEvent;
        public long eventDurationMillis;
        public long eventTimeMillis;
        public ExpiryCriteria expiryCriteria;
        public Image icon;
        public boolean isSensitive;
        public boolean isWorkProfile;
        public Message postEvent;
        public Message preEvent;
        public boolean shouldDiscard;
        public TapAction tapAction;
        public long updateTimeMillis;
        
        public SmartspaceCard() {
            this.clear();
        }
        
        public static SmartspaceCard[] emptyArray() {
            if (SmartspaceCard._emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (SmartspaceCard._emptyArray == null) {
                        SmartspaceCard._emptyArray = new SmartspaceCard[0];
                    }
                }
            }
            return SmartspaceCard._emptyArray;
        }
        
        public SmartspaceCard clear() {
            this.shouldDiscard = false;
            this.cardPriority = 0;
            this.cardId = 0;
            this.preEvent = null;
            this.duringEvent = null;
            this.postEvent = null;
            this.icon = null;
            this.cardType = 0;
            this.tapAction = null;
            this.updateTimeMillis = 0L;
            this.eventTimeMillis = 0L;
            this.eventDurationMillis = 0L;
            this.expiryCriteria = null;
            this.isSensitive = false;
            this.isWorkProfile = false;
            super.cachedSize = -1;
            return this;
        }
        
        @Override
        protected int computeSerializedSize() {
            final int computeSerializedSize = super.computeSerializedSize();
            final boolean shouldDiscard = this.shouldDiscard;
            int n = computeSerializedSize;
            if (shouldDiscard) {
                n = computeSerializedSize + CodedOutputByteBufferNano.computeBoolSize(1, shouldDiscard);
            }
            final int cardId = this.cardId;
            int n2 = n;
            if (cardId != 0) {
                n2 = n + CodedOutputByteBufferNano.computeInt32Size(2, cardId);
            }
            final Message preEvent = this.preEvent;
            int n3 = n2;
            if (preEvent != null) {
                n3 = n2 + CodedOutputByteBufferNano.computeMessageSize(3, preEvent);
            }
            final Message duringEvent = this.duringEvent;
            int n4 = n3;
            if (duringEvent != null) {
                n4 = n3 + CodedOutputByteBufferNano.computeMessageSize(4, duringEvent);
            }
            final Message postEvent = this.postEvent;
            int n5 = n4;
            if (postEvent != null) {
                n5 = n4 + CodedOutputByteBufferNano.computeMessageSize(5, postEvent);
            }
            final Image icon = this.icon;
            int n6 = n5;
            if (icon != null) {
                n6 = n5 + CodedOutputByteBufferNano.computeMessageSize(6, icon);
            }
            final int cardType = this.cardType;
            int n7 = n6;
            if (cardType != 0) {
                n7 = n6 + CodedOutputByteBufferNano.computeInt32Size(7, cardType);
            }
            final TapAction tapAction = this.tapAction;
            int n8 = n7;
            if (tapAction != null) {
                n8 = n7 + CodedOutputByteBufferNano.computeMessageSize(8, tapAction);
            }
            final long updateTimeMillis = this.updateTimeMillis;
            int n9 = n8;
            if (updateTimeMillis != 0L) {
                n9 = n8 + CodedOutputByteBufferNano.computeInt64Size(9, updateTimeMillis);
            }
            final long eventTimeMillis = this.eventTimeMillis;
            int n10 = n9;
            if (eventTimeMillis != 0L) {
                n10 = n9 + CodedOutputByteBufferNano.computeInt64Size(10, eventTimeMillis);
            }
            final long eventDurationMillis = this.eventDurationMillis;
            int n11 = n10;
            if (eventDurationMillis != 0L) {
                n11 = n10 + CodedOutputByteBufferNano.computeInt64Size(11, eventDurationMillis);
            }
            final ExpiryCriteria expiryCriteria = this.expiryCriteria;
            int n12 = n11;
            if (expiryCriteria != null) {
                n12 = n11 + CodedOutputByteBufferNano.computeMessageSize(12, expiryCriteria);
            }
            final int cardPriority = this.cardPriority;
            int n13 = n12;
            if (cardPriority != 0) {
                n13 = n12 + CodedOutputByteBufferNano.computeInt32Size(13, cardPriority);
            }
            final boolean isSensitive = this.isSensitive;
            int n14 = n13;
            if (isSensitive) {
                n14 = n13 + CodedOutputByteBufferNano.computeBoolSize(17, isSensitive);
            }
            final boolean isWorkProfile = this.isWorkProfile;
            int n15 = n14;
            if (isWorkProfile) {
                n15 = n14 + CodedOutputByteBufferNano.computeBoolSize(18, isWorkProfile);
            }
            return n15;
        }
        
        @Override
        public SmartspaceCard mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                final int tag = codedInputByteBufferNano.readTag();
                switch (tag) {
                    default: {
                        if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                            return this;
                        }
                        continue;
                    }
                    case 144: {
                        this.isWorkProfile = codedInputByteBufferNano.readBool();
                        continue;
                    }
                    case 136: {
                        this.isSensitive = codedInputByteBufferNano.readBool();
                        continue;
                    }
                    case 104: {
                        final int int32 = codedInputByteBufferNano.readInt32();
                        if (int32 != 0 && int32 != 1 && int32 != 2) {
                            continue;
                        }
                        this.cardPriority = int32;
                        continue;
                    }
                    case 98: {
                        if (this.expiryCriteria == null) {
                            this.expiryCriteria = new ExpiryCriteria();
                        }
                        codedInputByteBufferNano.readMessage(this.expiryCriteria);
                        continue;
                    }
                    case 88: {
                        this.eventDurationMillis = codedInputByteBufferNano.readInt64();
                        continue;
                    }
                    case 80: {
                        this.eventTimeMillis = codedInputByteBufferNano.readInt64();
                        continue;
                    }
                    case 72: {
                        this.updateTimeMillis = codedInputByteBufferNano.readInt64();
                        continue;
                    }
                    case 66: {
                        if (this.tapAction == null) {
                            this.tapAction = new TapAction();
                        }
                        codedInputByteBufferNano.readMessage(this.tapAction);
                        continue;
                    }
                    case 56: {
                        final int int33 = codedInputByteBufferNano.readInt32();
                        switch (int33) {
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
                                this.cardType = int33;
                                continue;
                            }
                        }
                        break;
                    }
                    case 50: {
                        if (this.icon == null) {
                            this.icon = new Image();
                        }
                        codedInputByteBufferNano.readMessage(this.icon);
                        continue;
                    }
                    case 42: {
                        if (this.postEvent == null) {
                            this.postEvent = new Message();
                        }
                        codedInputByteBufferNano.readMessage(this.postEvent);
                        continue;
                    }
                    case 34: {
                        if (this.duringEvent == null) {
                            this.duringEvent = new Message();
                        }
                        codedInputByteBufferNano.readMessage(this.duringEvent);
                        continue;
                    }
                    case 26: {
                        if (this.preEvent == null) {
                            this.preEvent = new Message();
                        }
                        codedInputByteBufferNano.readMessage(this.preEvent);
                        continue;
                    }
                    case 16: {
                        this.cardId = codedInputByteBufferNano.readInt32();
                        continue;
                    }
                    case 8: {
                        this.shouldDiscard = codedInputByteBufferNano.readBool();
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
            final boolean shouldDiscard = this.shouldDiscard;
            if (shouldDiscard) {
                codedOutputByteBufferNano.writeBool(1, shouldDiscard);
            }
            final int cardId = this.cardId;
            if (cardId != 0) {
                codedOutputByteBufferNano.writeInt32(2, cardId);
            }
            final Message preEvent = this.preEvent;
            if (preEvent != null) {
                codedOutputByteBufferNano.writeMessage(3, preEvent);
            }
            final Message duringEvent = this.duringEvent;
            if (duringEvent != null) {
                codedOutputByteBufferNano.writeMessage(4, duringEvent);
            }
            final Message postEvent = this.postEvent;
            if (postEvent != null) {
                codedOutputByteBufferNano.writeMessage(5, postEvent);
            }
            final Image icon = this.icon;
            if (icon != null) {
                codedOutputByteBufferNano.writeMessage(6, icon);
            }
            final int cardType = this.cardType;
            if (cardType != 0) {
                codedOutputByteBufferNano.writeInt32(7, cardType);
            }
            final TapAction tapAction = this.tapAction;
            if (tapAction != null) {
                codedOutputByteBufferNano.writeMessage(8, tapAction);
            }
            final long updateTimeMillis = this.updateTimeMillis;
            if (updateTimeMillis != 0L) {
                codedOutputByteBufferNano.writeInt64(9, updateTimeMillis);
            }
            final long eventTimeMillis = this.eventTimeMillis;
            if (eventTimeMillis != 0L) {
                codedOutputByteBufferNano.writeInt64(10, eventTimeMillis);
            }
            final long eventDurationMillis = this.eventDurationMillis;
            if (eventDurationMillis != 0L) {
                codedOutputByteBufferNano.writeInt64(11, eventDurationMillis);
            }
            final ExpiryCriteria expiryCriteria = this.expiryCriteria;
            if (expiryCriteria != null) {
                codedOutputByteBufferNano.writeMessage(12, expiryCriteria);
            }
            final int cardPriority = this.cardPriority;
            if (cardPriority != 0) {
                codedOutputByteBufferNano.writeInt32(13, cardPriority);
            }
            final boolean isSensitive = this.isSensitive;
            if (isSensitive) {
                codedOutputByteBufferNano.writeBool(17, isSensitive);
            }
            final boolean isWorkProfile = this.isWorkProfile;
            if (isWorkProfile) {
                codedOutputByteBufferNano.writeBool(18, isWorkProfile);
            }
            super.writeTo(codedOutputByteBufferNano);
        }
        
        public static final class ExpiryCriteria extends MessageNano
        {
            public long expirationTimeMillis;
            public int maxImpressions;
            
            public ExpiryCriteria() {
                this.clear();
            }
            
            public ExpiryCriteria clear() {
                this.expirationTimeMillis = 0L;
                this.maxImpressions = 0;
                super.cachedSize = -1;
                return this;
            }
            
            @Override
            protected int computeSerializedSize() {
                final int computeSerializedSize = super.computeSerializedSize();
                final long expirationTimeMillis = this.expirationTimeMillis;
                int n = computeSerializedSize;
                if (expirationTimeMillis != 0L) {
                    n = computeSerializedSize + CodedOutputByteBufferNano.computeInt64Size(1, expirationTimeMillis);
                }
                final int maxImpressions = this.maxImpressions;
                int n2 = n;
                if (maxImpressions != 0) {
                    n2 = n + CodedOutputByteBufferNano.computeInt32Size(2, maxImpressions);
                }
                return n2;
            }
            
            @Override
            public ExpiryCriteria mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                            this.maxImpressions = codedInputByteBufferNano.readInt32();
                        }
                    }
                    else {
                        this.expirationTimeMillis = codedInputByteBufferNano.readInt64();
                    }
                }
            }
            
            @Override
            public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                final long expirationTimeMillis = this.expirationTimeMillis;
                if (expirationTimeMillis != 0L) {
                    codedOutputByteBufferNano.writeInt64(1, expirationTimeMillis);
                }
                final int maxImpressions = this.maxImpressions;
                if (maxImpressions != 0) {
                    codedOutputByteBufferNano.writeInt32(2, maxImpressions);
                }
                super.writeTo(codedOutputByteBufferNano);
            }
        }
        
        public static final class Image extends MessageNano
        {
            public String gsaResourceName;
            public String key;
            public String uri;
            
            public Image() {
                this.clear();
            }
            
            public Image clear() {
                this.key = "";
                this.gsaResourceName = "";
                this.uri = "";
                super.cachedSize = -1;
                return this;
            }
            
            @Override
            protected int computeSerializedSize() {
                int computeSerializedSize;
                final int n = computeSerializedSize = super.computeSerializedSize();
                if (!this.key.equals("")) {
                    computeSerializedSize = n + CodedOutputByteBufferNano.computeStringSize(1, this.key);
                }
                int n2 = computeSerializedSize;
                if (!this.gsaResourceName.equals("")) {
                    n2 = computeSerializedSize + CodedOutputByteBufferNano.computeStringSize(2, this.gsaResourceName);
                }
                int n3 = n2;
                if (!this.uri.equals("")) {
                    n3 = n2 + CodedOutputByteBufferNano.computeStringSize(3, this.uri);
                }
                return n3;
            }
            
            @Override
            public Image mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    final int tag = codedInputByteBufferNano.readTag();
                    if (tag == 0) {
                        return this;
                    }
                    if (tag != 10) {
                        if (tag != 18) {
                            if (tag != 26) {
                                if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                                    return this;
                                }
                                continue;
                            }
                            else {
                                this.uri = codedInputByteBufferNano.readString();
                            }
                        }
                        else {
                            this.gsaResourceName = codedInputByteBufferNano.readString();
                        }
                    }
                    else {
                        this.key = codedInputByteBufferNano.readString();
                    }
                }
            }
            
            @Override
            public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (!this.key.equals("")) {
                    codedOutputByteBufferNano.writeString(1, this.key);
                }
                if (!this.gsaResourceName.equals("")) {
                    codedOutputByteBufferNano.writeString(2, this.gsaResourceName);
                }
                if (!this.uri.equals("")) {
                    codedOutputByteBufferNano.writeString(3, this.uri);
                }
                super.writeTo(codedOutputByteBufferNano);
            }
        }
        
        public static final class Message extends MessageNano
        {
            public FormattedText subtitle;
            public FormattedText title;
            
            public Message() {
                this.clear();
            }
            
            public Message clear() {
                this.title = null;
                this.subtitle = null;
                super.cachedSize = -1;
                return this;
            }
            
            @Override
            protected int computeSerializedSize() {
                final int computeSerializedSize = super.computeSerializedSize();
                final FormattedText title = this.title;
                int n = computeSerializedSize;
                if (title != null) {
                    n = computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(1, title);
                }
                final FormattedText subtitle = this.subtitle;
                int n2 = n;
                if (subtitle != null) {
                    n2 = n + CodedOutputByteBufferNano.computeMessageSize(2, subtitle);
                }
                return n2;
            }
            
            @Override
            public Message mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    final int tag = codedInputByteBufferNano.readTag();
                    if (tag == 0) {
                        return this;
                    }
                    if (tag != 10) {
                        if (tag != 18) {
                            if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                                return this;
                            }
                            continue;
                        }
                        else {
                            if (this.subtitle == null) {
                                this.subtitle = new FormattedText();
                            }
                            codedInputByteBufferNano.readMessage(this.subtitle);
                        }
                    }
                    else {
                        if (this.title == null) {
                            this.title = new FormattedText();
                        }
                        codedInputByteBufferNano.readMessage(this.title);
                    }
                }
            }
            
            @Override
            public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                final FormattedText title = this.title;
                if (title != null) {
                    codedOutputByteBufferNano.writeMessage(1, title);
                }
                final FormattedText subtitle = this.subtitle;
                if (subtitle != null) {
                    codedOutputByteBufferNano.writeMessage(2, subtitle);
                }
                super.writeTo(codedOutputByteBufferNano);
            }
            
            public static final class FormattedText extends MessageNano
            {
                public FormatParam[] formatParam;
                public String text;
                public int truncateLocation;
                
                public FormattedText() {
                    this.clear();
                }
                
                public FormattedText clear() {
                    this.text = "";
                    this.truncateLocation = 0;
                    this.formatParam = FormatParam.emptyArray();
                    super.cachedSize = -1;
                    return this;
                }
                
                @Override
                protected int computeSerializedSize() {
                    int computeSerializedSize;
                    final int n = computeSerializedSize = super.computeSerializedSize();
                    if (!this.text.equals("")) {
                        computeSerializedSize = n + CodedOutputByteBufferNano.computeStringSize(1, this.text);
                    }
                    final int truncateLocation = this.truncateLocation;
                    int n2 = computeSerializedSize;
                    if (truncateLocation != 0) {
                        n2 = computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(2, truncateLocation);
                    }
                    final FormatParam[] formatParam = this.formatParam;
                    int n3 = n2;
                    if (formatParam != null) {
                        n3 = n2;
                        if (formatParam.length > 0) {
                            int n4 = 0;
                            while (true) {
                                final FormatParam[] formatParam2 = this.formatParam;
                                n3 = n2;
                                if (n4 >= formatParam2.length) {
                                    break;
                                }
                                final FormatParam formatParam3 = formatParam2[n4];
                                int n5 = n2;
                                if (formatParam3 != null) {
                                    n5 = n2 + CodedOutputByteBufferNano.computeMessageSize(3, formatParam3);
                                }
                                ++n4;
                                n2 = n5;
                            }
                        }
                    }
                    return n3;
                }
                
                @Override
                public FormattedText mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        final int tag = codedInputByteBufferNano.readTag();
                        if (tag == 0) {
                            return this;
                        }
                        if (tag != 10) {
                            if (tag != 16) {
                                if (tag != 26) {
                                    if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                                        return this;
                                    }
                                    continue;
                                }
                                else {
                                    final int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 26);
                                    final FormatParam[] formatParam = this.formatParam;
                                    int length;
                                    if (formatParam == null) {
                                        length = 0;
                                    }
                                    else {
                                        length = formatParam.length;
                                    }
                                    final int n = repeatedFieldArrayLength + length;
                                    final FormatParam[] formatParam2 = new FormatParam[n];
                                    int i = length;
                                    if (length != 0) {
                                        System.arraycopy(this.formatParam, 0, formatParam2, 0, length);
                                        i = length;
                                    }
                                    while (i < n - 1) {
                                        codedInputByteBufferNano.readMessage(formatParam2[i] = new FormatParam());
                                        codedInputByteBufferNano.readTag();
                                        ++i;
                                    }
                                    codedInputByteBufferNano.readMessage(formatParam2[i] = new FormatParam());
                                    this.formatParam = formatParam2;
                                }
                            }
                            else {
                                final int int32 = codedInputByteBufferNano.readInt32();
                                if (int32 != 0 && int32 != 1 && int32 != 2 && int32 != 3) {
                                    continue;
                                }
                                this.truncateLocation = int32;
                            }
                        }
                        else {
                            this.text = codedInputByteBufferNano.readString();
                        }
                    }
                }
                
                @Override
                public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    if (!this.text.equals("")) {
                        codedOutputByteBufferNano.writeString(1, this.text);
                    }
                    final int truncateLocation = this.truncateLocation;
                    if (truncateLocation != 0) {
                        codedOutputByteBufferNano.writeInt32(2, truncateLocation);
                    }
                    final FormatParam[] formatParam = this.formatParam;
                    if (formatParam != null && formatParam.length > 0) {
                        int n = 0;
                        while (true) {
                            final FormatParam[] formatParam2 = this.formatParam;
                            if (n >= formatParam2.length) {
                                break;
                            }
                            final FormatParam formatParam3 = formatParam2[n];
                            if (formatParam3 != null) {
                                codedOutputByteBufferNano.writeMessage(3, formatParam3);
                            }
                            ++n;
                        }
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }
                
                public static final class FormatParam extends MessageNano
                {
                    private static volatile FormatParam[] _emptyArray;
                    public int formatParamArgs;
                    public String text;
                    public int truncateLocation;
                    public boolean updateTimeLocally;
                    
                    public FormatParam() {
                        this.clear();
                    }
                    
                    public static FormatParam[] emptyArray() {
                        if (FormatParam._emptyArray == null) {
                            synchronized (InternalNano.LAZY_INIT_LOCK) {
                                if (FormatParam._emptyArray == null) {
                                    FormatParam._emptyArray = new FormatParam[0];
                                }
                            }
                        }
                        return FormatParam._emptyArray;
                    }
                    
                    public FormatParam clear() {
                        this.text = "";
                        this.truncateLocation = 0;
                        this.formatParamArgs = 0;
                        this.updateTimeLocally = false;
                        super.cachedSize = -1;
                        return this;
                    }
                    
                    @Override
                    protected int computeSerializedSize() {
                        int computeSerializedSize;
                        final int n = computeSerializedSize = super.computeSerializedSize();
                        if (!this.text.equals("")) {
                            computeSerializedSize = n + CodedOutputByteBufferNano.computeStringSize(1, this.text);
                        }
                        final int truncateLocation = this.truncateLocation;
                        int n2 = computeSerializedSize;
                        if (truncateLocation != 0) {
                            n2 = computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(2, truncateLocation);
                        }
                        final int formatParamArgs = this.formatParamArgs;
                        int n3 = n2;
                        if (formatParamArgs != 0) {
                            n3 = n2 + CodedOutputByteBufferNano.computeInt32Size(3, formatParamArgs);
                        }
                        final boolean updateTimeLocally = this.updateTimeLocally;
                        int n4 = n3;
                        if (updateTimeLocally) {
                            n4 = n3 + CodedOutputByteBufferNano.computeBoolSize(4, updateTimeLocally);
                        }
                        return n4;
                    }
                    
                    @Override
                    public FormatParam mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                        while (true) {
                            final int tag = codedInputByteBufferNano.readTag();
                            if (tag == 0) {
                                return this;
                            }
                            if (tag != 10) {
                                if (tag != 16) {
                                    if (tag != 24) {
                                        if (tag != 32) {
                                            if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                                                return this;
                                            }
                                            continue;
                                        }
                                        else {
                                            this.updateTimeLocally = codedInputByteBufferNano.readBool();
                                        }
                                    }
                                    else {
                                        final int int32 = codedInputByteBufferNano.readInt32();
                                        if (int32 != 0 && int32 != 1 && int32 != 2 && int32 != 3) {
                                            continue;
                                        }
                                        this.formatParamArgs = int32;
                                    }
                                }
                                else {
                                    final int int33 = codedInputByteBufferNano.readInt32();
                                    if (int33 != 0 && int33 != 1 && int33 != 2 && int33 != 3) {
                                        continue;
                                    }
                                    this.truncateLocation = int33;
                                }
                            }
                            else {
                                this.text = codedInputByteBufferNano.readString();
                            }
                        }
                    }
                    
                    @Override
                    public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                        if (!this.text.equals("")) {
                            codedOutputByteBufferNano.writeString(1, this.text);
                        }
                        final int truncateLocation = this.truncateLocation;
                        if (truncateLocation != 0) {
                            codedOutputByteBufferNano.writeInt32(2, truncateLocation);
                        }
                        final int formatParamArgs = this.formatParamArgs;
                        if (formatParamArgs != 0) {
                            codedOutputByteBufferNano.writeInt32(3, formatParamArgs);
                        }
                        final boolean updateTimeLocally = this.updateTimeLocally;
                        if (updateTimeLocally) {
                            codedOutputByteBufferNano.writeBool(4, updateTimeLocally);
                        }
                        super.writeTo(codedOutputByteBufferNano);
                    }
                }
            }
        }
        
        public static final class TapAction extends MessageNano
        {
            public int actionType;
            public String intent;
            
            public TapAction() {
                this.clear();
            }
            
            public TapAction clear() {
                this.actionType = 0;
                this.intent = "";
                super.cachedSize = -1;
                return this;
            }
            
            @Override
            protected int computeSerializedSize() {
                final int computeSerializedSize = super.computeSerializedSize();
                final int actionType = this.actionType;
                int n = computeSerializedSize;
                if (actionType != 0) {
                    n = computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(1, actionType);
                }
                int n2 = n;
                if (!this.intent.equals("")) {
                    n2 = n + CodedOutputByteBufferNano.computeStringSize(2, this.intent);
                }
                return n2;
            }
            
            @Override
            public TapAction mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    final int tag = codedInputByteBufferNano.readTag();
                    if (tag == 0) {
                        return this;
                    }
                    if (tag != 8) {
                        if (tag != 18) {
                            if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                                return this;
                            }
                            continue;
                        }
                        else {
                            this.intent = codedInputByteBufferNano.readString();
                        }
                    }
                    else {
                        final int int32 = codedInputByteBufferNano.readInt32();
                        if (int32 != 0 && int32 != 1 && int32 != 2) {
                            continue;
                        }
                        this.actionType = int32;
                    }
                }
            }
            
            @Override
            public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                final int actionType = this.actionType;
                if (actionType != 0) {
                    codedOutputByteBufferNano.writeInt32(1, actionType);
                }
                if (!this.intent.equals("")) {
                    codedOutputByteBufferNano.writeString(2, this.intent);
                }
                super.writeTo(codedOutputByteBufferNano);
            }
        }
    }
}
