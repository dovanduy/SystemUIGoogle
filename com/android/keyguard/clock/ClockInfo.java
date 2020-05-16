// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard.clock;

import android.graphics.Bitmap;
import java.util.function.Supplier;

final class ClockInfo
{
    private final String mId;
    private final String mName;
    private final Supplier<Bitmap> mPreview;
    private final Supplier<Bitmap> mThumbnail;
    private final Supplier<String> mTitle;
    
    private ClockInfo(final String mName, final Supplier<String> mTitle, final String mId, final Supplier<Bitmap> mThumbnail, final Supplier<Bitmap> mPreview) {
        this.mName = mName;
        this.mTitle = mTitle;
        this.mId = mId;
        this.mThumbnail = mThumbnail;
        this.mPreview = mPreview;
    }
    
    static Builder builder() {
        return new Builder();
    }
    
    String getId() {
        return this.mId;
    }
    
    String getName() {
        return this.mName;
    }
    
    Bitmap getPreview() {
        return this.mPreview.get();
    }
    
    Bitmap getThumbnail() {
        return this.mThumbnail.get();
    }
    
    String getTitle() {
        return this.mTitle.get();
    }
    
    static class Builder
    {
        private String mId;
        private String mName;
        private Supplier<Bitmap> mPreview;
        private Supplier<Bitmap> mThumbnail;
        private Supplier<String> mTitle;
        
        public ClockInfo build() {
            return new ClockInfo(this.mName, this.mTitle, this.mId, this.mThumbnail, this.mPreview, null);
        }
        
        public Builder setId(final String mId) {
            this.mId = mId;
            return this;
        }
        
        public Builder setName(final String mName) {
            this.mName = mName;
            return this;
        }
        
        public Builder setPreview(final Supplier<Bitmap> mPreview) {
            this.mPreview = mPreview;
            return this;
        }
        
        public Builder setThumbnail(final Supplier<Bitmap> mThumbnail) {
            this.mThumbnail = mThumbnail;
            return this;
        }
        
        public Builder setTitle(final Supplier<String> mTitle) {
            this.mTitle = mTitle;
            return this;
        }
    }
}
