// 
// Decompiled by Procyon v0.5.36
// 

package androidx.recyclerview.widget;

public class BatchingListUpdateCallback implements ListUpdateCallback
{
    int mLastEventCount;
    Object mLastEventPayload;
    int mLastEventPosition;
    int mLastEventType;
    final ListUpdateCallback mWrapped;
    
    public BatchingListUpdateCallback(final ListUpdateCallback mWrapped) {
        this.mLastEventType = 0;
        this.mLastEventPosition = -1;
        this.mLastEventCount = -1;
        this.mLastEventPayload = null;
        this.mWrapped = mWrapped;
    }
    
    public void dispatchLastEvent() {
        final int mLastEventType = this.mLastEventType;
        if (mLastEventType == 0) {
            return;
        }
        if (mLastEventType != 1) {
            if (mLastEventType != 2) {
                if (mLastEventType == 3) {
                    this.mWrapped.onChanged(this.mLastEventPosition, this.mLastEventCount, this.mLastEventPayload);
                }
            }
            else {
                this.mWrapped.onRemoved(this.mLastEventPosition, this.mLastEventCount);
            }
        }
        else {
            this.mWrapped.onInserted(this.mLastEventPosition, this.mLastEventCount);
        }
        this.mLastEventPayload = null;
        this.mLastEventType = 0;
    }
    
    @Override
    public void onChanged(final int n, final int mLastEventCount, final Object mLastEventPayload) {
        if (this.mLastEventType == 3) {
            final int mLastEventPosition = this.mLastEventPosition;
            final int mLastEventCount2 = this.mLastEventCount;
            if (n <= mLastEventPosition + mLastEventCount2) {
                final int b = n + mLastEventCount;
                if (b >= mLastEventPosition && this.mLastEventPayload == mLastEventPayload) {
                    this.mLastEventPosition = Math.min(n, mLastEventPosition);
                    this.mLastEventCount = Math.max(mLastEventCount2 + mLastEventPosition, b) - this.mLastEventPosition;
                    return;
                }
            }
        }
        this.dispatchLastEvent();
        this.mLastEventPosition = n;
        this.mLastEventCount = mLastEventCount;
        this.mLastEventPayload = mLastEventPayload;
        this.mLastEventType = 3;
    }
    
    @Override
    public void onInserted(final int n, final int mLastEventCount) {
        if (this.mLastEventType == 1) {
            final int mLastEventPosition = this.mLastEventPosition;
            if (n >= mLastEventPosition) {
                final int mLastEventCount2 = this.mLastEventCount;
                if (n <= mLastEventPosition + mLastEventCount2) {
                    this.mLastEventCount = mLastEventCount2 + mLastEventCount;
                    this.mLastEventPosition = Math.min(n, mLastEventPosition);
                    return;
                }
            }
        }
        this.dispatchLastEvent();
        this.mLastEventPosition = n;
        this.mLastEventCount = mLastEventCount;
        this.mLastEventType = 1;
    }
    
    @Override
    public void onMoved(final int n, final int n2) {
        this.dispatchLastEvent();
        this.mWrapped.onMoved(n, n2);
    }
    
    @Override
    public void onRemoved(final int n, final int mLastEventCount) {
        if (this.mLastEventType == 2) {
            final int mLastEventPosition = this.mLastEventPosition;
            if (mLastEventPosition >= n && mLastEventPosition <= n + mLastEventCount) {
                this.mLastEventCount += mLastEventCount;
                this.mLastEventPosition = n;
                return;
            }
        }
        this.dispatchLastEvent();
        this.mLastEventPosition = n;
        this.mLastEventCount = mLastEventCount;
        this.mLastEventType = 2;
    }
}
