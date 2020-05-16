// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.sensors;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.ArrayList;

public class EventIMURT
{
    protected ArrayList<Float> _fv;
    protected boolean _gotAcc;
    protected boolean _gotGyro;
    protected Highpass3C _highpassAcc;
    protected Highpass3C _highpassGyro;
    protected Lowpass3C _lowpassAcc;
    protected Lowpass3C _lowpassGyro;
    protected int _numberFeature;
    protected Resample3C _resampleAcc;
    protected Resample3C _resampleGyro;
    protected int _sizeFeatureWindow;
    protected long _sizeWindowNs;
    protected Slope3C _slopeAcc;
    protected Slope3C _slopeGyro;
    protected long _syncTime;
    protected Deque<Float> _xsAcc;
    protected Deque<Float> _xsGyro;
    protected Deque<Float> _ysAcc;
    protected Deque<Float> _ysGyro;
    protected Deque<Float> _zsAcc;
    protected Deque<Float> _zsGyro;
    
    public EventIMURT() {
        this._fv = new ArrayList<Float>();
        this._xsAcc = new ArrayDeque<Float>();
        this._ysAcc = new ArrayDeque<Float>();
        this._zsAcc = new ArrayDeque<Float>();
        this._xsGyro = new ArrayDeque<Float>();
        this._ysGyro = new ArrayDeque<Float>();
        this._zsGyro = new ArrayDeque<Float>();
        this._gotAcc = false;
        this._gotGyro = false;
        this._syncTime = 0L;
        this._resampleAcc = new Resample3C();
        this._resampleGyro = new Resample3C();
        this._slopeAcc = new Slope3C();
        this._slopeGyro = new Slope3C();
        this._lowpassAcc = new Lowpass3C();
        this._lowpassGyro = new Lowpass3C();
        this._highpassAcc = new Highpass3C();
        this._highpassGyro = new Highpass3C();
    }
    
    public void processGyro() {
        final Point3f update = this._highpassGyro.update(this._lowpassGyro.update(this._slopeGyro.update(this._resampleGyro.getResults().point, 2500000.0f / this._resampleGyro.getInterval())));
        this._xsGyro.add(update.x);
        this._ysGyro.add(update.y);
        this._zsGyro.add(update.z);
        while (this._xsGyro.size() > (int)(this._sizeWindowNs / this._resampleGyro.getInterval())) {
            this._xsGyro.removeFirst();
            this._ysGyro.removeFirst();
            this._zsGyro.removeFirst();
        }
    }
    
    public void reset() {
        this._xsAcc.clear();
        this._ysAcc.clear();
        this._zsAcc.clear();
        this._xsGyro.clear();
        this._ysGyro.clear();
        this._zsGyro.clear();
        this._gotAcc = false;
        this._gotGyro = false;
        this._syncTime = 0L;
    }
    
    public ArrayList<Float> scaleGyroData(final ArrayList<Float> list, final float n) {
        for (int i = list.size() / 2; i < list.size(); ++i) {
            list.set(i, list.get(i) * n);
        }
        return list;
    }
}
