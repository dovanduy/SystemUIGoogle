// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

class WindowAlignment
{
    public final Axis horizontal;
    private Axis mMainAxis;
    private int mOrientation;
    private Axis mSecondAxis;
    public final Axis vertical;
    
    WindowAlignment() {
        this.mOrientation = 0;
        this.vertical = new Axis("vertical");
        final Axis axis = new Axis("horizontal");
        this.horizontal = axis;
        this.mMainAxis = axis;
        this.mSecondAxis = this.vertical;
    }
    
    public final Axis mainAxis() {
        return this.mMainAxis;
    }
    
    public final void reset() {
        this.mainAxis().reset();
    }
    
    public final Axis secondAxis() {
        return this.mSecondAxis;
    }
    
    public final void setOrientation(final int mOrientation) {
        this.mOrientation = mOrientation;
        if (mOrientation == 0) {
            this.mMainAxis = this.horizontal;
            this.mSecondAxis = this.vertical;
        }
        else {
            this.mMainAxis = this.vertical;
            this.mSecondAxis = this.horizontal;
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("horizontal=");
        sb.append(this.horizontal);
        sb.append("; vertical=");
        sb.append(this.vertical);
        return sb.toString();
    }
    
    public static class Axis
    {
        private int mMaxEdge;
        private int mMaxScroll;
        private int mMinEdge;
        private int mMinScroll;
        private int mPaddingMax;
        private int mPaddingMin;
        private int mPreferredKeyLine;
        private boolean mReversedFlow;
        private int mSize;
        private int mWindowAlignment;
        private int mWindowAlignmentOffset;
        private float mWindowAlignmentOffsetPercent;
        
        public Axis(final String s) {
            this.mPreferredKeyLine = 2;
            this.mWindowAlignment = 3;
            this.mWindowAlignmentOffset = 0;
            this.mWindowAlignmentOffsetPercent = 50.0f;
            this.reset();
        }
        
        final int calculateKeyline() {
            int n;
            if (!this.mReversedFlow) {
                int mWindowAlignmentOffset = this.mWindowAlignmentOffset;
                if (mWindowAlignmentOffset < 0) {
                    mWindowAlignmentOffset += this.mSize;
                }
                final float mWindowAlignmentOffsetPercent = this.mWindowAlignmentOffsetPercent;
                n = mWindowAlignmentOffset;
                if (mWindowAlignmentOffsetPercent != -1.0f) {
                    n = mWindowAlignmentOffset + (int)(this.mSize * mWindowAlignmentOffsetPercent / 100.0f);
                }
            }
            else {
                final int mWindowAlignmentOffset2 = this.mWindowAlignmentOffset;
                int n2;
                if (mWindowAlignmentOffset2 >= 0) {
                    n2 = this.mSize - mWindowAlignmentOffset2;
                }
                else {
                    n2 = -mWindowAlignmentOffset2;
                }
                final float mWindowAlignmentOffsetPercent2 = this.mWindowAlignmentOffsetPercent;
                n = n2;
                if (mWindowAlignmentOffsetPercent2 != -1.0f) {
                    n = n2 - (int)(this.mSize * mWindowAlignmentOffsetPercent2 / 100.0f);
                }
            }
            return n;
        }
        
        final int calculateScrollToKeyLine(final int n, final int n2) {
            return n - n2;
        }
        
        public final int getClientSize() {
            return this.mSize - this.mPaddingMin - this.mPaddingMax;
        }
        
        public final int getMaxScroll() {
            return this.mMaxScroll;
        }
        
        public final int getMinScroll() {
            return this.mMinScroll;
        }
        
        public final int getPaddingMax() {
            return this.mPaddingMax;
        }
        
        public final int getPaddingMin() {
            return this.mPaddingMin;
        }
        
        public final int getScroll(int n) {
            final int size = this.getSize();
            final int calculateKeyline = this.calculateKeyline();
            final boolean minUnknown = this.isMinUnknown();
            final boolean maxUnknown = this.isMaxUnknown();
            Label_0109: {
                if (!minUnknown) {
                    final int mPaddingMin = this.mPaddingMin;
                    if (!this.mReversedFlow) {
                        if ((this.mWindowAlignment & 0x1) == 0x0) {
                            break Label_0109;
                        }
                    }
                    else if ((this.mWindowAlignment & 0x2) == 0x0) {
                        break Label_0109;
                    }
                    final int mMinEdge = this.mMinEdge;
                    if (n - mMinEdge <= calculateKeyline - mPaddingMin) {
                        final int n2 = n = mMinEdge - this.mPaddingMin;
                        if (!maxUnknown) {
                            final int mMaxScroll = this.mMaxScroll;
                            if ((n = n2) > mMaxScroll) {
                                n = mMaxScroll;
                            }
                        }
                        return n;
                    }
                }
            }
            if (!maxUnknown) {
                final int mPaddingMax = this.mPaddingMax;
                if (!this.mReversedFlow) {
                    if ((this.mWindowAlignment & 0x2) == 0x0) {
                        return this.calculateScrollToKeyLine(n, calculateKeyline);
                    }
                }
                else if ((this.mWindowAlignment & 0x1) == 0x0) {
                    return this.calculateScrollToKeyLine(n, calculateKeyline);
                }
                final int mMaxEdge = this.mMaxEdge;
                if (mMaxEdge - n <= size - calculateKeyline - mPaddingMax) {
                    final int n3 = n = mMaxEdge - (size - this.mPaddingMax);
                    if (!minUnknown) {
                        final int mMinScroll = this.mMinScroll;
                        if ((n = n3) < mMinScroll) {
                            n = mMinScroll;
                        }
                    }
                    return n;
                }
            }
            return this.calculateScrollToKeyLine(n, calculateKeyline);
        }
        
        public final int getSize() {
            return this.mSize;
        }
        
        public final void invalidateScrollMax() {
            this.mMaxEdge = Integer.MAX_VALUE;
            this.mMaxScroll = Integer.MAX_VALUE;
        }
        
        public final void invalidateScrollMin() {
            this.mMinEdge = Integer.MIN_VALUE;
            this.mMinScroll = Integer.MIN_VALUE;
        }
        
        public final boolean isMaxUnknown() {
            return this.mMaxEdge == Integer.MAX_VALUE;
        }
        
        public final boolean isMinUnknown() {
            return this.mMinEdge == Integer.MIN_VALUE;
        }
        
        final boolean isPreferKeylineOverHighEdge() {
            return (this.mPreferredKeyLine & 0x2) != 0x0;
        }
        
        final boolean isPreferKeylineOverLowEdge() {
            final int mPreferredKeyLine = this.mPreferredKeyLine;
            boolean b = true;
            if ((mPreferredKeyLine & 0x1) == 0x0) {
                b = false;
            }
            return b;
        }
        
        void reset() {
            this.mMinEdge = Integer.MIN_VALUE;
            this.mMaxEdge = Integer.MAX_VALUE;
        }
        
        public final void setPadding(final int mPaddingMin, final int mPaddingMax) {
            this.mPaddingMin = mPaddingMin;
            this.mPaddingMax = mPaddingMax;
        }
        
        public final void setReversedFlow(final boolean mReversedFlow) {
            this.mReversedFlow = mReversedFlow;
        }
        
        public final void setSize(final int mSize) {
            this.mSize = mSize;
        }
        
        public final void setWindowAlignment(final int mWindowAlignment) {
            this.mWindowAlignment = mWindowAlignment;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append(" min:");
            sb.append(this.mMinEdge);
            sb.append(" ");
            sb.append(this.mMinScroll);
            sb.append(" max:");
            sb.append(this.mMaxEdge);
            sb.append(" ");
            sb.append(this.mMaxScroll);
            return sb.toString();
        }
        
        public final void updateMinMax(int calculateKeyline, int mMaxEdge, final int n, final int n2) {
            this.mMinEdge = calculateKeyline;
            this.mMaxEdge = mMaxEdge;
            mMaxEdge = this.getClientSize();
            calculateKeyline = this.calculateKeyline();
            final boolean minUnknown = this.isMinUnknown();
            final boolean maxUnknown = this.isMaxUnknown();
            Label_0091: {
                if (!minUnknown) {
                    Label_0081: {
                        if (!this.mReversedFlow) {
                            if ((this.mWindowAlignment & 0x1) == 0x0) {
                                break Label_0081;
                            }
                        }
                        else if ((this.mWindowAlignment & 0x2) == 0x0) {
                            break Label_0081;
                        }
                        this.mMinScroll = this.mMinEdge - this.mPaddingMin;
                        break Label_0091;
                    }
                    this.mMinScroll = this.calculateScrollToKeyLine(n, calculateKeyline);
                }
            }
            Label_0153: {
                if (!maxUnknown) {
                    Label_0142: {
                        if (!this.mReversedFlow) {
                            if ((this.mWindowAlignment & 0x2) == 0x0) {
                                break Label_0142;
                            }
                        }
                        else if ((this.mWindowAlignment & 0x1) == 0x0) {
                            break Label_0142;
                        }
                        this.mMaxScroll = this.mMaxEdge - this.mPaddingMin - mMaxEdge;
                        break Label_0153;
                    }
                    this.mMaxScroll = this.calculateScrollToKeyLine(n2, calculateKeyline);
                }
            }
            if (!maxUnknown && !minUnknown) {
                if (!this.mReversedFlow) {
                    mMaxEdge = this.mWindowAlignment;
                    if ((mMaxEdge & 0x1) != 0x0) {
                        if (this.isPreferKeylineOverLowEdge()) {
                            this.mMinScroll = Math.min(this.mMinScroll, this.calculateScrollToKeyLine(n2, calculateKeyline));
                        }
                        this.mMaxScroll = Math.max(this.mMinScroll, this.mMaxScroll);
                    }
                    else if ((mMaxEdge & 0x2) != 0x0) {
                        if (this.isPreferKeylineOverHighEdge()) {
                            this.mMaxScroll = Math.max(this.mMaxScroll, this.calculateScrollToKeyLine(n, calculateKeyline));
                        }
                        this.mMinScroll = Math.min(this.mMinScroll, this.mMaxScroll);
                    }
                }
                else {
                    mMaxEdge = this.mWindowAlignment;
                    if ((mMaxEdge & 0x1) != 0x0) {
                        if (this.isPreferKeylineOverLowEdge()) {
                            this.mMaxScroll = Math.max(this.mMaxScroll, this.calculateScrollToKeyLine(n, calculateKeyline));
                        }
                        this.mMinScroll = Math.min(this.mMinScroll, this.mMaxScroll);
                    }
                    else if ((mMaxEdge & 0x2) != 0x0) {
                        if (this.isPreferKeylineOverHighEdge()) {
                            this.mMinScroll = Math.min(this.mMinScroll, this.calculateScrollToKeyLine(n2, calculateKeyline));
                        }
                        this.mMaxScroll = Math.max(this.mMinScroll, this.mMaxScroll);
                    }
                }
            }
        }
    }
}
