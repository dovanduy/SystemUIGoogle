// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier.brightline;

import java.util.Locale;
import java.util.Iterator;
import java.util.ArrayList;
import android.graphics.Point;
import java.util.List;
import android.view.MotionEvent;
import com.android.systemui.util.DeviceConfigProxy;

class ZigZagClassifier extends FalsingClassifier
{
    private float mLastDevianceX;
    private float mLastDevianceY;
    private float mLastMaxXDeviance;
    private float mLastMaxYDeviance;
    private final float mMaxXPrimaryDeviance;
    private final float mMaxXSecondaryDeviance;
    private final float mMaxYPrimaryDeviance;
    private final float mMaxYSecondaryDeviance;
    
    ZigZagClassifier(final FalsingDataProvider falsingDataProvider, final DeviceConfigProxy deviceConfigProxy) {
        super(falsingDataProvider);
        this.mMaxXPrimaryDeviance = deviceConfigProxy.getFloat("systemui", "brightline_falsing_zigzag_x_primary_deviance", 0.05f);
        this.mMaxYPrimaryDeviance = deviceConfigProxy.getFloat("systemui", "brightline_falsing_zigzag_y_primary_deviance", 0.15f);
        this.mMaxXSecondaryDeviance = deviceConfigProxy.getFloat("systemui", "brightline_falsing_zigzag_x_secondary_deviance", 0.4f);
        this.mMaxYSecondaryDeviance = deviceConfigProxy.getFloat("systemui", "brightline_falsing_zigzag_y_secondary_deviance", 0.3f);
    }
    
    private float getAtan2LastPoint() {
        final MotionEvent firstMotionEvent = this.getFirstMotionEvent();
        final MotionEvent lastMotionEvent = this.getLastMotionEvent();
        return (float)Math.atan2(lastMotionEvent.getY() - firstMotionEvent.getY(), lastMotionEvent.getX() - firstMotionEvent.getX());
    }
    
    private List<Point> rotateHorizontal() {
        final double d = this.getAtan2LastPoint();
        final StringBuilder sb = new StringBuilder();
        sb.append("Rotating to horizontal by: ");
        sb.append(d);
        FalsingClassifier.logDebug(sb.toString());
        return this.rotateMotionEvents(this.getRecentMotionEvents(), d);
    }
    
    private List<Point> rotateMotionEvents(final List<MotionEvent> list, double sin) {
        final ArrayList<Point> list2 = new ArrayList<Point>();
        final double cos = Math.cos(sin);
        sin = Math.sin(sin);
        final MotionEvent motionEvent = list.get(0);
        final float x = motionEvent.getX();
        final float y = motionEvent.getY();
        for (final MotionEvent motionEvent2 : list) {
            final float x2 = motionEvent2.getX();
            final float y2 = motionEvent2.getY();
            final double n = x2 - x;
            final double n2 = y2 - y;
            list2.add(new Point((int)(cos * n + sin * n2 + x), (int)(-sin * n + n2 * cos + y)));
        }
        final MotionEvent motionEvent3 = list.get(list.size() - 1);
        final Point point = list2.get(0);
        final Point point2 = list2.get(list2.size() - 1);
        final StringBuilder sb = new StringBuilder();
        sb.append("Before: (");
        sb.append(motionEvent.getX());
        sb.append(",");
        sb.append(motionEvent.getY());
        sb.append("), (");
        sb.append(motionEvent3.getX());
        sb.append(",");
        sb.append(motionEvent3.getY());
        sb.append(")");
        FalsingClassifier.logDebug(sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("After: (");
        sb2.append(point.x);
        sb2.append(",");
        sb2.append(point.y);
        sb2.append("), (");
        sb2.append(point2.x);
        sb2.append(",");
        sb2.append(point2.y);
        sb2.append(")");
        FalsingClassifier.logDebug(sb2.toString());
        return list2;
    }
    
    private List<Point> rotateVertical() {
        final double d = 1.5707963267948966 - this.getAtan2LastPoint();
        final StringBuilder sb = new StringBuilder();
        sb.append("Rotating to vertical by: ");
        sb.append(d);
        FalsingClassifier.logDebug(sb.toString());
        return this.rotateMotionEvents(this.getRecentMotionEvents(), -d);
    }
    
    @Override
    String getReason() {
        return String.format(null, "{devianceX=%f, maxDevianceX=%s, devianceY=%s, maxDevianceY=%s}", this.mLastDevianceX, this.mLastMaxXDeviance, this.mLastDevianceY, this.mLastMaxYDeviance);
    }
    
    @Override
    boolean isFalseTouch() {
        final int size = this.getRecentMotionEvents().size();
        boolean b = false;
        if (size < 3) {
            return false;
        }
        List<Point> list;
        if (this.isHorizontal()) {
            list = this.rotateHorizontal();
        }
        else {
            list = this.rotateVertical();
        }
        final float f = (float)Math.abs(list.get(0).x - list.get(list.size() - 1).x);
        final float f2 = (float)Math.abs(list.get(0).y - list.get(list.size() - 1).y);
        final StringBuilder sb = new StringBuilder();
        sb.append("Actual: (");
        sb.append(f);
        sb.append(",");
        sb.append(f2);
        sb.append(")");
        FalsingClassifier.logDebug(sb.toString());
        final Iterator<Point> iterator = list.iterator();
        float f3 = 0.0f;
        int n = 1;
        float f4 = 0.0f;
        float f6;
        float f5 = f6 = f4;
        while (iterator.hasNext()) {
            final Point point = iterator.next();
            if (n != 0) {
                f5 = (float)point.x;
                f6 = (float)point.y;
                n = 0;
            }
            else {
                f3 += Math.abs(point.x - f5);
                f4 += Math.abs(point.y - f6);
                f5 = (float)point.x;
                f6 = (float)point.y;
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("(x, y, runningAbsDx, runningAbsDy) - (");
                sb2.append(f5);
                sb2.append(", ");
                sb2.append(f6);
                sb2.append(", ");
                sb2.append(f3);
                sb2.append(", ");
                sb2.append(f4);
                sb2.append(")");
                FalsingClassifier.logDebug(sb2.toString());
            }
        }
        final float n2 = f3 - f;
        final float n3 = f4 - f2;
        final float n4 = f / this.getXdpi();
        final float n5 = f2 / this.getYdpi();
        final float n6 = (float)Math.sqrt(n4 * n4 + n5 * n5);
        float n7;
        float n8;
        float n9;
        if (f > f2) {
            n7 = this.mMaxXPrimaryDeviance * n6 * this.getXdpi();
            n8 = this.mMaxYSecondaryDeviance * n6;
            n9 = this.getYdpi();
        }
        else {
            n7 = this.mMaxXSecondaryDeviance * n6 * this.getXdpi();
            n8 = this.mMaxYPrimaryDeviance * n6;
            n9 = this.getYdpi();
        }
        final float n10 = n8 * n9;
        this.mLastDevianceX = n2;
        this.mLastDevianceY = n3;
        this.mLastMaxXDeviance = n7;
        this.mLastMaxYDeviance = n10;
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("Straightness Deviance: (");
        sb3.append(n2);
        sb3.append(",");
        sb3.append(n3);
        sb3.append(") vs (");
        sb3.append(n7);
        sb3.append(",");
        sb3.append(n10);
        sb3.append(")");
        FalsingClassifier.logDebug(sb3.toString());
        if (n2 > n7 || n3 > n10) {
            b = true;
        }
        return b;
    }
}
