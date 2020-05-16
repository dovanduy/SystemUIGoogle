// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.sensors;

public class PeakDetector
{
    private float _amplitudeMajorPeak;
    private float _amplitudeReference;
    private int _idMajorPeak;
    private float _minNoiseTolerate;
    private float _noiseTolerate;
    private int _windowSize;
    
    public PeakDetector() {
        this._idMajorPeak = -1;
        this._amplitudeMajorPeak = 0.0f;
        this._windowSize = 0;
        this._amplitudeReference = 0.0f;
        this._minNoiseTolerate = 0.0f;
    }
    
    public int getIdMajorPeak() {
        return this._idMajorPeak;
    }
    
    public void setMinNoiseTolerate(final float minNoiseTolerate) {
        this._minNoiseTolerate = minNoiseTolerate;
    }
    
    public void setWindowSize(final int windowSize) {
        this._windowSize = windowSize;
    }
    
    public void update(final float amplitudeReference) {
        final int idMajorPeak = this._idMajorPeak - 1;
        this._idMajorPeak = idMajorPeak;
        if (idMajorPeak < 0) {
            this._amplitudeMajorPeak = 0.0f;
        }
        final float minNoiseTolerate = this._minNoiseTolerate;
        this._noiseTolerate = minNoiseTolerate;
        final float amplitudeMajorPeak = this._amplitudeMajorPeak;
        if (amplitudeMajorPeak / 5.0f > minNoiseTolerate) {
            this._noiseTolerate = amplitudeMajorPeak / 5.0f;
        }
        final float n = this._amplitudeReference - amplitudeReference;
        final float noiseTolerate = this._noiseTolerate;
        if (n < noiseTolerate) {
            if (n < 0.0f && amplitudeReference > noiseTolerate) {
                this._amplitudeReference = amplitudeReference;
                if (amplitudeReference > this._amplitudeMajorPeak) {
                    this._idMajorPeak = this._windowSize - 1;
                    this._amplitudeMajorPeak = amplitudeReference;
                }
            }
        }
        else {
            this._amplitudeReference = amplitudeReference;
        }
    }
}
