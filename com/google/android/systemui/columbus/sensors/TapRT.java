// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.sensors;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ArrayDeque;
import android.content.res.AssetManager;
import java.util.Deque;

public class TapRT extends EventIMURT
{
    private Highpass1C _highpassKey;
    private Lowpass1C _lowpassKey;
    private PeakDetector _peakDetectorNegative;
    private PeakDetector _peakDetectorPositive;
    private int _result;
    private Deque<Long> _tBackTapTimestamps;
    private TfClassifier _tflite;
    private boolean _wasPeakApproaching;
    
    public TapRT(final long sizeWindowNs, final AssetManager assetManager, String s) {
        this._lowpassKey = new Lowpass1C();
        this._highpassKey = new Highpass1C();
        this._peakDetectorPositive = new PeakDetector();
        this._peakDetectorNegative = new PeakDetector();
        this._tBackTapTimestamps = new ArrayDeque<Long>();
        this._wasPeakApproaching = true;
        final int hashCode = s.hashCode();
        int n = 0;
        Label_0123: {
            if (hashCode != 1905086331) {
                if (hashCode == 1905116122) {
                    if (s.equals("Pixel 4 XL")) {
                        n = 1;
                        break Label_0123;
                    }
                }
            }
            else if (s.equals("Pixel 3 XL")) {
                n = 0;
                break Label_0123;
            }
            n = -1;
        }
        if (n != 0) {
            if (n != 1) {
                s = "tap7cls_pixel4.tflite";
            }
            else {
                s = "tap7cls_pixel4xl.tflite";
            }
        }
        else {
            s = "tap7cls_pixel3xl.tflite";
        }
        this._tflite = new TfClassifier(assetManager, s);
        super._sizeWindowNs = sizeWindowNs;
        super._sizeFeatureWindow = 50;
        super._numberFeature = 50 * 6;
        super._lowpassAcc.setPara(1.0f);
        super._lowpassGyro.setPara(1.0f);
        super._highpassAcc.setPara(0.05f);
        super._highpassGyro.setPara(0.05f);
        this._lowpassKey.setPara(0.2f);
        this._highpassKey.setPara(0.2f);
    }
    
    private void addToFeatureVector(final Deque<Float> deque, final int n, int n2) {
        final Iterator<Float> iterator = deque.iterator();
        final int n3 = 0;
        int index = n2;
        n2 = n3;
        while (iterator.hasNext()) {
            if (n2 < n) {
                iterator.next();
            }
            else {
                if (n2 >= super._sizeFeatureWindow + n) {
                    break;
                }
                super._fv.set(index, iterator.next());
                ++index;
            }
            ++n2;
        }
    }
    
    public int checkDoubleTapTiming(final long n) {
        final Iterator<Long> iterator = this._tBackTapTimestamps.iterator();
        while (iterator.hasNext()) {
            if (n - iterator.next() > 500000000L) {
                iterator.remove();
            }
        }
        if (this._tBackTapTimestamps.isEmpty()) {
            return 0;
        }
        final Iterator<Long> iterator2 = this._tBackTapTimestamps.iterator();
        while (iterator2.hasNext()) {
            if (this._tBackTapTimestamps.getLast() - iterator2.next() > 100000000L) {
                this._tBackTapTimestamps.clear();
                return 2;
            }
        }
        return 1;
    }
    
    public Highpass1C getHighpassKey() {
        return this._highpassKey;
    }
    
    public Lowpass1C getLowpassKey() {
        return this._lowpassKey;
    }
    
    public PeakDetector getNegativePeakDetection() {
        return this._peakDetectorNegative;
    }
    
    public PeakDetector getPositivePeakDetector() {
        return this._peakDetectorPositive;
    }
    
    public void processAccAndKeySignal() {
        final Point3f update = super._slopeAcc.update(super._resampleAcc.getResults().point, 2500000.0f / super._resampleAcc.getInterval());
        final Point3f update2 = super._highpassAcc.update(super._lowpassAcc.update(update));
        super._xsAcc.add(update2.x);
        super._ysAcc.add(update2.y);
        super._zsAcc.add(update2.z);
        while (super._xsAcc.size() > (int)(super._sizeWindowNs / super._resampleAcc.getInterval())) {
            super._xsAcc.removeFirst();
            super._ysAcc.removeFirst();
            super._zsAcc.removeFirst();
        }
        this._peakDetectorPositive.update(this._highpassKey.update(this._lowpassKey.update(update.z)));
    }
    
    public void processKeySignalHeursitic(final long l) {
        final float update = this._highpassKey.update(this._lowpassKey.update(super._slopeAcc.update(super._resampleAcc.getResults().point, 2500000.0f / super._resampleAcc.getInterval()).z));
        this._peakDetectorPositive.update(update);
        this._peakDetectorNegative.update(-update);
        super._zsAcc.add(update);
        final int n = (int)(super._sizeWindowNs / super._resampleAcc.getInterval());
        while (super._zsAcc.size() > n) {
            super._zsAcc.removeFirst();
        }
        if (super._zsAcc.size() == n) {
            this.recognizeTapHeursitic();
        }
        if (this._result == TapClass.Back.ordinal()) {
            this._tBackTapTimestamps.addLast(l);
        }
    }
    
    public void recognizeTapHeursitic() {
        final int idMajorPeak = this._peakDetectorPositive.getIdMajorPeak();
        final int n = this._peakDetectorNegative.getIdMajorPeak() - idMajorPeak;
        if (idMajorPeak == 4) {
            super._fv = new ArrayList<Float>(super._zsAcc);
            if (n > 0 && n < 3) {
                this._result = TapClass.Back.ordinal();
            }
            else {
                this._result = TapClass.Others.ordinal();
            }
        }
    }
    
    public void recognizeTapML() {
        final int n = (int)((super._resampleAcc.getResults().t - super._resampleGyro.getResults().t) / super._resampleAcc.getInterval());
        final int idMajorPeak = this._peakDetectorPositive.getIdMajorPeak();
        if (idMajorPeak > 12) {
            this._wasPeakApproaching = true;
        }
        final int n2 = idMajorPeak - 6;
        final int n3 = n2 - n;
        final int size = super._zsAcc.size();
        if (n2 >= 0 && n3 >= 0) {
            final int sizeFeatureWindow = super._sizeFeatureWindow;
            if (n2 + sizeFeatureWindow < size) {
                if (sizeFeatureWindow + n3 < size) {
                    if (this._wasPeakApproaching && idMajorPeak <= 12) {
                        this._wasPeakApproaching = false;
                        this.addToFeatureVector(super._xsAcc, n2, 0);
                        this.addToFeatureVector(super._ysAcc, n2, super._sizeFeatureWindow);
                        this.addToFeatureVector(super._zsAcc, n2, super._sizeFeatureWindow * 2);
                        this.addToFeatureVector(super._xsGyro, n3, super._sizeFeatureWindow * 3);
                        this.addToFeatureVector(super._ysGyro, n3, super._sizeFeatureWindow * 4);
                        this.addToFeatureVector(super._zsGyro, n3, super._sizeFeatureWindow * 5);
                        final ArrayList<Float> fv = (ArrayList<Float>)super._fv;
                        this.scaleGyroData(fv, 10.0f);
                        super._fv = fv;
                        this._result = Util.getMaxId(this._tflite.predict(fv, 7).get(0));
                    }
                }
            }
        }
    }
    
    public void reset(final boolean b) {
        super.reset();
        if (!b) {
            super._fv = new ArrayList<Float>(super._numberFeature);
            for (int i = 0; i < super._numberFeature; ++i) {
                super._fv.add(0.0f);
            }
        }
        else {
            super._fv.clear();
        }
    }
    
    public void updateData(final int n, final float n2, final float n3, final float n4, final long n5, final long n6, final boolean b) {
        this._result = TapClass.Others.ordinal();
        if (b) {
            this.updateHeuristic(n, n2, n3, n4, n5, n6);
        }
        else {
            this.updateML(n, n2, n3, n4, n5, n6);
        }
    }
    
    public void updateHeuristic(final int n, final float n2, final float n3, final float n4, final long syncTime, final long n5) {
        if (n == 4) {
            return;
        }
        if (0L == super._syncTime) {
            super._syncTime = syncTime;
            super._resampleAcc.init(n2, n3, n4, syncTime, n5);
            super._resampleAcc.setSyncTime(super._syncTime);
            super._slopeAcc.init(super._resampleAcc.getResults().point);
            this._lowpassKey.init(0.0f);
            this._highpassKey.init(0.0f);
            return;
        }
        while (super._resampleAcc.update(n2, n3, n4, syncTime)) {
            this.processKeySignalHeursitic(syncTime);
        }
    }
    
    public void updateML(final int n, final float n2, final float n3, final float n4, final long l, final long n5) {
        if (n == 1) {
            super._gotAcc = true;
            if (0L == super._syncTime) {
                super._resampleAcc.init(n2, n3, n4, l, n5);
            }
            if (!super._gotGyro) {
                return;
            }
        }
        else if (n == 4) {
            super._gotGyro = true;
            if (0L == super._syncTime) {
                super._resampleGyro.init(n2, n3, n4, l, n5);
            }
            if (!super._gotAcc) {
                return;
            }
        }
        if (0L == super._syncTime) {
            super._syncTime = l;
            super._resampleAcc.setSyncTime(l);
            super._resampleGyro.setSyncTime(super._syncTime);
            super._slopeAcc.init(super._resampleAcc.getResults().point);
            super._slopeGyro.init(super._resampleGyro.getResults().point);
            super._lowpassAcc.init(new Point3f(0.0f, 0.0f, 0.0f));
            super._lowpassGyro.init(new Point3f(0.0f, 0.0f, 0.0f));
            super._highpassAcc.init(new Point3f(0.0f, 0.0f, 0.0f));
            super._highpassGyro.init(new Point3f(0.0f, 0.0f, 0.0f));
            this._lowpassKey.init(0.0f);
            this._highpassKey.init(0.0f);
            return;
        }
        if (n == 1) {
            while (super._resampleAcc.update(n2, n3, n4, l)) {
                this.processAccAndKeySignal();
            }
        }
        else if (n == 4) {
            while (super._resampleGyro.update(n2, n3, n4, l)) {
                this.processGyro();
            }
        }
        this.recognizeTapML();
        if (this._result == TapClass.Back.ordinal()) {
            this._tBackTapTimestamps.addLast(l);
        }
    }
    
    public enum TapClass
    {
        Back, 
        Bottom, 
        Front, 
        Left, 
        Others, 
        Right, 
        Top;
    }
}
