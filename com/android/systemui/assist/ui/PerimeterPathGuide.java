// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.assist.ui;

import androidx.core.math.MathUtils;
import android.util.Pair;
import android.util.Log;
import android.graphics.Matrix;
import android.content.Context;
import android.graphics.PathMeasure;
import android.graphics.Path;

public class PerimeterPathGuide
{
    private final int mBottomCornerRadiusPx;
    private final CornerPathRenderer mCornerPathRenderer;
    private final int mDeviceHeightPx;
    private final int mDeviceWidthPx;
    private final int mEdgeInset;
    private RegionAttributes[] mRegions;
    private int mRotation;
    private final Path mScratchPath;
    private final PathMeasure mScratchPathMeasure;
    private final int mTopCornerRadiusPx;
    
    public PerimeterPathGuide(final Context context, final CornerPathRenderer mCornerPathRenderer, int mEdgeInset, final int mDeviceWidthPx, final int mDeviceHeightPx) {
        this.mScratchPath = new Path();
        final Path mScratchPath = this.mScratchPath;
        final int n = 0;
        this.mScratchPathMeasure = new PathMeasure(mScratchPath, false);
        this.mRotation = 0;
        this.mCornerPathRenderer = mCornerPathRenderer;
        this.mDeviceWidthPx = mDeviceWidthPx;
        this.mDeviceHeightPx = mDeviceHeightPx;
        this.mTopCornerRadiusPx = DisplayUtils.getCornerRadiusTop(context);
        this.mBottomCornerRadiusPx = DisplayUtils.getCornerRadiusBottom(context);
        this.mEdgeInset = mEdgeInset;
        this.mRegions = new RegionAttributes[8];
        mEdgeInset = n;
        while (true) {
            final RegionAttributes[] mRegions = this.mRegions;
            if (mEdgeInset >= mRegions.length) {
                break;
            }
            mRegions[mEdgeInset] = new RegionAttributes();
            ++mEdgeInset;
        }
        this.computeRegions();
    }
    
    private void computeRegions() {
        final int mDeviceWidthPx = this.mDeviceWidthPx;
        int n = this.mDeviceHeightPx;
        final int mRotation = this.mRotation;
        final int n2 = 0;
        int n3;
        if (mRotation != 1) {
            if (mRotation != 2) {
                if (mRotation != 3) {
                    n3 = 0;
                }
                else {
                    n3 = -270;
                }
            }
            else {
                n3 = -180;
            }
        }
        else {
            n3 = -90;
        }
        final Matrix matrix = new Matrix();
        matrix.postRotate((float)n3, (float)(this.mDeviceWidthPx / 2), (float)(this.mDeviceHeightPx / 2));
        final int mRotation2 = this.mRotation;
        int mDeviceHeightPx = 0;
        Label_0134: {
            if (mRotation2 != 1) {
                mDeviceHeightPx = mDeviceWidthPx;
                if (mRotation2 != 3) {
                    break Label_0134;
                }
            }
            n = this.mDeviceWidthPx;
            mDeviceHeightPx = this.mDeviceHeightPx;
            matrix.postTranslate((float)((mDeviceHeightPx - n) / 2), (float)((n - mDeviceHeightPx) / 2));
        }
        final CornerPathRenderer.Corner rotatedCorner = this.getRotatedCorner(CornerPathRenderer.Corner.BOTTOM_LEFT);
        final CornerPathRenderer.Corner rotatedCorner2 = this.getRotatedCorner(CornerPathRenderer.Corner.BOTTOM_RIGHT);
        final CornerPathRenderer.Corner rotatedCorner3 = this.getRotatedCorner(CornerPathRenderer.Corner.TOP_LEFT);
        final CornerPathRenderer.Corner rotatedCorner4 = this.getRotatedCorner(CornerPathRenderer.Corner.TOP_RIGHT);
        this.mRegions[Region.BOTTOM_LEFT.ordinal()].path = this.mCornerPathRenderer.getInsetPath(rotatedCorner, (float)this.mEdgeInset);
        this.mRegions[Region.BOTTOM_RIGHT.ordinal()].path = this.mCornerPathRenderer.getInsetPath(rotatedCorner2, (float)this.mEdgeInset);
        this.mRegions[Region.TOP_RIGHT.ordinal()].path = this.mCornerPathRenderer.getInsetPath(rotatedCorner4, (float)this.mEdgeInset);
        this.mRegions[Region.TOP_LEFT.ordinal()].path = this.mCornerPathRenderer.getInsetPath(rotatedCorner3, (float)this.mEdgeInset);
        this.mRegions[Region.BOTTOM_LEFT.ordinal()].path.transform(matrix);
        this.mRegions[Region.BOTTOM_RIGHT.ordinal()].path.transform(matrix);
        this.mRegions[Region.TOP_RIGHT.ordinal()].path.transform(matrix);
        this.mRegions[Region.TOP_LEFT.ordinal()].path.transform(matrix);
        final Path path = new Path();
        path.moveTo((float)this.getPhysicalCornerRadius(rotatedCorner), (float)(n - this.mEdgeInset));
        path.lineTo((float)(mDeviceHeightPx - this.getPhysicalCornerRadius(rotatedCorner2)), (float)(n - this.mEdgeInset));
        this.mRegions[Region.BOTTOM.ordinal()].path = path;
        final Path path2 = new Path();
        path2.moveTo((float)(mDeviceHeightPx - this.getPhysicalCornerRadius(rotatedCorner4)), (float)this.mEdgeInset);
        path2.lineTo((float)this.getPhysicalCornerRadius(rotatedCorner3), (float)this.mEdgeInset);
        this.mRegions[Region.TOP.ordinal()].path = path2;
        final Path path3 = new Path();
        path3.moveTo((float)(mDeviceHeightPx - this.mEdgeInset), (float)(n - this.getPhysicalCornerRadius(rotatedCorner2)));
        path3.lineTo((float)(mDeviceHeightPx - this.mEdgeInset), (float)this.getPhysicalCornerRadius(rotatedCorner4));
        this.mRegions[Region.RIGHT.ordinal()].path = path3;
        final Path path4 = new Path();
        path4.moveTo((float)this.mEdgeInset, (float)this.getPhysicalCornerRadius(rotatedCorner3));
        path4.lineTo((float)this.mEdgeInset, (float)(n - this.getPhysicalCornerRadius(rotatedCorner)));
        this.mRegions[Region.LEFT.ordinal()].path = path4;
        final PathMeasure pathMeasure = new PathMeasure();
        final float n4 = 0.0f;
        float n5 = 0.0f;
        int n6 = 0;
        float n7;
        int n8;
        while (true) {
            final RegionAttributes[] mRegions = this.mRegions;
            n7 = n4;
            n8 = n2;
            if (n6 >= mRegions.length) {
                break;
            }
            pathMeasure.setPath(mRegions[n6].path, false);
            this.mRegions[n6].absoluteLength = pathMeasure.getLength();
            n5 += this.mRegions[n6].absoluteLength;
            ++n6;
        }
        RegionAttributes[] mRegions2;
        while (true) {
            mRegions2 = this.mRegions;
            if (n8 >= mRegions2.length) {
                break;
            }
            mRegions2[n8].normalizedLength = mRegions2[n8].absoluteLength / n5;
            n7 += mRegions2[n8].absoluteLength;
            mRegions2[n8].endCoordinate = n7 / n5;
            ++n8;
        }
        mRegions2[mRegions2.length - 1].endCoordinate = 1.0f;
    }
    
    private int getPhysicalCornerRadius(final CornerPathRenderer.Corner corner) {
        if (corner != CornerPathRenderer.Corner.BOTTOM_LEFT && corner != CornerPathRenderer.Corner.BOTTOM_RIGHT) {
            return this.mTopCornerRadiusPx;
        }
        return this.mBottomCornerRadiusPx;
    }
    
    private Region getRegionForPoint(final float n) {
        float n2 = 0.0f;
        Label_0022: {
            if (n >= 0.0f) {
                n2 = n;
                if (n <= 1.0f) {
                    break Label_0022;
                }
            }
            n2 = (n % 1.0f + 1.0f) % 1.0f;
        }
        for (final Region region : Region.values()) {
            if (n2 <= this.mRegions[region.ordinal()].endCoordinate) {
                return region;
            }
        }
        Log.e("PerimeterPathGuide", "Fell out of getRegionForPoint");
        return Region.BOTTOM;
    }
    
    private CornerPathRenderer.Corner getRotatedCorner(final CornerPathRenderer.Corner corner) {
        int ordinal = corner.ordinal();
        final int mRotation = this.mRotation;
        if (mRotation != 1) {
            if (mRotation != 2) {
                if (mRotation == 3) {
                    ++ordinal;
                }
            }
            else {
                ordinal += 2;
            }
        }
        else {
            ordinal += 3;
        }
        return CornerPathRenderer.Corner.values()[ordinal % 4];
    }
    
    public static float makeClockwise(final float n) {
        return n - 1.0f;
    }
    
    private Pair<Region, Float> placePoint(final float n) {
        float n2 = 0.0f;
        Label_0022: {
            if (0.0f <= n) {
                n2 = n;
                if (n <= 1.0f) {
                    break Label_0022;
                }
            }
            n2 = (n % 1.0f + 1.0f) % 1.0f;
        }
        final Region regionForPoint = this.getRegionForPoint(n2);
        if (regionForPoint.equals(Region.BOTTOM)) {
            return (Pair<Region, Float>)Pair.create((Object)regionForPoint, (Object)(n2 / this.mRegions[regionForPoint.ordinal()].normalizedLength));
        }
        return (Pair<Region, Float>)Pair.create((Object)regionForPoint, (Object)((n2 - this.mRegions[regionForPoint.ordinal() - 1].endCoordinate) / this.mRegions[regionForPoint.ordinal()].normalizedLength));
    }
    
    private void strokeRegion(final Path path, final Region region, final float n, final float n2) {
        if (n == n2) {
            return;
        }
        this.mScratchPathMeasure.setPath(this.mRegions[region.ordinal()].path, false);
        final PathMeasure mScratchPathMeasure = this.mScratchPathMeasure;
        mScratchPathMeasure.getSegment(n * mScratchPathMeasure.getLength(), n2 * this.mScratchPathMeasure.getLength(), path, true);
    }
    
    private void strokeSegmentInternal(final Path path, final float n, final float n2) {
        final Pair<Region, Float> placePoint = this.placePoint(n);
        final Pair<Region, Float> placePoint2 = this.placePoint(n2);
        if (((Region)placePoint.first).equals(placePoint2.first)) {
            this.strokeRegion(path, (Region)placePoint.first, (float)placePoint.second, (float)placePoint2.second);
        }
        else {
            this.strokeRegion(path, (Region)placePoint.first, (float)placePoint.second, 1.0f);
            final Region[] values = Region.values();
            final int length = values.length;
            int i = 0;
            int n3 = 0;
            while (i < length) {
                final Region region = values[i];
                int n4;
                if (region.equals(placePoint.first)) {
                    n4 = 1;
                }
                else if ((n4 = n3) != 0) {
                    if (region.equals(placePoint2.first)) {
                        this.strokeRegion(path, region, 0.0f, (float)placePoint2.second);
                        break;
                    }
                    this.strokeRegion(path, region, 0.0f, 1.0f);
                    n4 = n3;
                }
                ++i;
                n3 = n4;
            }
        }
    }
    
    public float getCoord(final Region region, float clamp) {
        final RegionAttributes regionAttributes = this.mRegions[region.ordinal()];
        clamp = MathUtils.clamp(clamp, 0.0f, 1.0f);
        return regionAttributes.endCoordinate - (1.0f - clamp) * regionAttributes.normalizedLength;
    }
    
    public float getRegionCenter(final Region region) {
        return this.getCoord(region, 0.5f);
    }
    
    public float getRegionWidth(final Region region) {
        return this.mRegions[region.ordinal()].normalizedLength;
    }
    
    public void setRotation(final int n) {
        if (n != this.mRotation) {
            if (n != 0 && n != 1 && n != 2 && n != 3) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Invalid rotation provided: ");
                sb.append(n);
                Log.e("PerimeterPathGuide", sb.toString());
            }
            else {
                this.mRotation = n;
                this.computeRegions();
            }
        }
    }
    
    public void strokeSegment(final Path path, float n, float n2) {
        path.reset();
        final float n3 = (n % 1.0f + 1.0f) % 1.0f;
        n2 = (n2 % 1.0f + 1.0f) % 1.0f;
        final boolean b = n3 > n2;
        n = n3;
        if (b) {
            this.strokeSegmentInternal(path, n3, 1.0f);
            n = 0.0f;
        }
        this.strokeSegmentInternal(path, n, n2);
    }
    
    public enum Region
    {
        BOTTOM, 
        BOTTOM_LEFT, 
        BOTTOM_RIGHT, 
        LEFT, 
        RIGHT, 
        TOP, 
        TOP_LEFT, 
        TOP_RIGHT;
    }
    
    private class RegionAttributes
    {
        public float absoluteLength;
        public float endCoordinate;
        public float normalizedLength;
        public Path path;
        
        private RegionAttributes(final PerimeterPathGuide perimeterPathGuide) {
        }
    }
}
