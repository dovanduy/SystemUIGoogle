// 
// Decompiled by Procyon v0.5.36
// 

package androidx.mediarouter.media;

abstract class RemoteControlClientCompat
{
    public abstract void setPlaybackInfo(final PlaybackInfo p0);
    
    public static final class PlaybackInfo
    {
        public int playbackStream;
        public int playbackType;
        public int volume;
        public int volumeHandling;
        public int volumeMax;
        
        public PlaybackInfo() {
            this.volumeHandling = 0;
        }
    }
}
