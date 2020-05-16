// 
// Decompiled by Procyon v0.5.36
// 

package androidx.constraintlayout.widget;

import java.util.Arrays;
import android.content.res.XmlResourceParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import android.util.Xml;
import java.util.Iterator;
import android.view.View;
import android.view.ViewGroup$LayoutParams;
import java.util.Collection;
import java.util.HashSet;
import android.os.Build$VERSION;
import android.util.Log;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.content.Context;
import android.support.constraint.R$styleable;
import java.util.HashMap;
import android.util.SparseIntArray;

public class ConstraintSet
{
    private static final int[] VISIBILITY_FLAGS;
    private static SparseIntArray mapToConstant;
    private HashMap<Integer, Constraint> mConstraints;
    
    static {
        VISIBILITY_FLAGS = new int[] { 0, 4, 8 };
        (ConstraintSet.mapToConstant = new SparseIntArray()).append(R$styleable.ConstraintSet_layout_constraintLeft_toLeftOf, 25);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_constraintLeft_toRightOf, 26);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_constraintRight_toLeftOf, 29);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_constraintRight_toRightOf, 30);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_constraintTop_toTopOf, 36);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_constraintTop_toBottomOf, 35);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_constraintBottom_toTopOf, 4);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_constraintBottom_toBottomOf, 3);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_constraintBaseline_toBaselineOf, 1);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_editor_absoluteX, 6);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_editor_absoluteY, 7);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_constraintGuide_begin, 17);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_constraintGuide_end, 18);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_constraintGuide_percent, 19);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_android_orientation, 27);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_constraintStart_toEndOf, 32);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_constraintStart_toStartOf, 33);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_constraintEnd_toStartOf, 10);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_constraintEnd_toEndOf, 9);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_goneMarginLeft, 13);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_goneMarginTop, 16);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_goneMarginRight, 14);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_goneMarginBottom, 11);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_goneMarginStart, 15);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_goneMarginEnd, 12);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_constraintVertical_weight, 40);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_constraintHorizontal_weight, 39);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_constraintHorizontal_chainStyle, 41);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_constraintVertical_chainStyle, 42);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_constraintHorizontal_bias, 20);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_constraintVertical_bias, 37);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_constraintDimensionRatio, 5);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_constraintLeft_creator, 64);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_constraintTop_creator, 64);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_constraintRight_creator, 64);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_constraintBottom_creator, 64);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_constraintBaseline_creator, 64);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_android_layout_marginLeft, 24);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_android_layout_marginRight, 28);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_android_layout_marginStart, 31);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_android_layout_marginEnd, 8);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_android_layout_marginTop, 34);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_android_layout_marginBottom, 2);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_android_layout_width, 23);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_android_layout_height, 21);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_android_visibility, 22);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_android_alpha, 43);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_android_elevation, 44);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_android_rotationX, 45);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_android_rotationY, 46);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_android_rotation, 60);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_android_scaleX, 47);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_android_scaleY, 48);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_android_transformPivotX, 49);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_android_transformPivotY, 50);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_android_translationX, 51);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_android_translationY, 52);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_android_translationZ, 53);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_constraintWidth_default, 54);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_constraintHeight_default, 55);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_constraintWidth_max, 56);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_constraintHeight_max, 57);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_constraintWidth_min, 58);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_constraintHeight_min, 59);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_constraintCircle, 61);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_constraintCircleRadius, 62);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_layout_constraintCircleAngle, 63);
        ConstraintSet.mapToConstant.append(R$styleable.ConstraintSet_android_id, 38);
    }
    
    public ConstraintSet() {
        this.mConstraints = new HashMap<Integer, Constraint>();
    }
    
    private Constraint fillFromAttributeList(final Context context, final AttributeSet set) {
        final Constraint constraint = new Constraint();
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.ConstraintSet);
        this.populateConstraint(constraint, obtainStyledAttributes);
        obtainStyledAttributes.recycle();
        return constraint;
    }
    
    private static int lookupID(final TypedArray typedArray, final int n, int n2) {
        if ((n2 = typedArray.getResourceId(n, n2)) == -1) {
            n2 = typedArray.getInt(n, -1);
        }
        return n2;
    }
    
    private void populateConstraint(final Constraint constraint, final TypedArray typedArray) {
        for (int indexCount = typedArray.getIndexCount(), i = 0; i < indexCount; ++i) {
            final int index = typedArray.getIndex(i);
            final int value = ConstraintSet.mapToConstant.get(index);
            switch (value) {
                default: {
                    switch (value) {
                        default: {
                            final StringBuilder sb = new StringBuilder();
                            sb.append("Unknown attribute 0x");
                            sb.append(Integer.toHexString(index));
                            sb.append("   ");
                            sb.append(ConstraintSet.mapToConstant.get(index));
                            Log.w("ConstraintSet", sb.toString());
                            continue;
                        }
                        case 64: {
                            final StringBuilder sb2 = new StringBuilder();
                            sb2.append("unused attribute 0x");
                            sb2.append(Integer.toHexString(index));
                            sb2.append("   ");
                            sb2.append(ConstraintSet.mapToConstant.get(index));
                            Log.w("ConstraintSet", sb2.toString());
                            continue;
                        }
                        case 63: {
                            constraint.circleAngle = typedArray.getFloat(index, constraint.circleAngle);
                            continue;
                        }
                        case 62: {
                            constraint.circleRadius = typedArray.getDimensionPixelSize(index, constraint.circleRadius);
                            continue;
                        }
                        case 61: {
                            constraint.circleConstraint = lookupID(typedArray, index, constraint.circleConstraint);
                            continue;
                        }
                        case 60: {
                            constraint.rotation = typedArray.getFloat(index, constraint.rotation);
                            continue;
                        }
                    }
                    break;
                }
                case 53: {
                    constraint.translationZ = typedArray.getDimension(index, constraint.translationZ);
                    break;
                }
                case 52: {
                    constraint.translationY = typedArray.getDimension(index, constraint.translationY);
                    break;
                }
                case 51: {
                    constraint.translationX = typedArray.getDimension(index, constraint.translationX);
                    break;
                }
                case 50: {
                    constraint.transformPivotY = typedArray.getFloat(index, constraint.transformPivotY);
                    break;
                }
                case 49: {
                    constraint.transformPivotX = typedArray.getFloat(index, constraint.transformPivotX);
                    break;
                }
                case 48: {
                    constraint.scaleY = typedArray.getFloat(index, constraint.scaleY);
                    break;
                }
                case 47: {
                    constraint.scaleX = typedArray.getFloat(index, constraint.scaleX);
                    break;
                }
                case 46: {
                    constraint.rotationY = typedArray.getFloat(index, constraint.rotationY);
                    break;
                }
                case 45: {
                    constraint.rotationX = typedArray.getFloat(index, constraint.rotationX);
                    break;
                }
                case 44: {
                    constraint.applyElevation = true;
                    constraint.elevation = typedArray.getDimension(index, constraint.elevation);
                    break;
                }
                case 43: {
                    constraint.alpha = typedArray.getFloat(index, constraint.alpha);
                    break;
                }
                case 42: {
                    constraint.verticalChainStyle = typedArray.getInt(index, constraint.verticalChainStyle);
                    break;
                }
                case 41: {
                    constraint.horizontalChainStyle = typedArray.getInt(index, constraint.horizontalChainStyle);
                    break;
                }
                case 40: {
                    constraint.verticalWeight = typedArray.getFloat(index, constraint.verticalWeight);
                    break;
                }
                case 39: {
                    constraint.horizontalWeight = typedArray.getFloat(index, constraint.horizontalWeight);
                    break;
                }
                case 38: {
                    constraint.mViewId = typedArray.getResourceId(index, constraint.mViewId);
                    break;
                }
                case 37: {
                    constraint.verticalBias = typedArray.getFloat(index, constraint.verticalBias);
                    break;
                }
                case 36: {
                    constraint.topToTop = lookupID(typedArray, index, constraint.topToTop);
                    break;
                }
                case 35: {
                    constraint.topToBottom = lookupID(typedArray, index, constraint.topToBottom);
                    break;
                }
                case 34: {
                    constraint.topMargin = typedArray.getDimensionPixelSize(index, constraint.topMargin);
                    break;
                }
                case 33: {
                    constraint.startToStart = lookupID(typedArray, index, constraint.startToStart);
                    break;
                }
                case 32: {
                    constraint.startToEnd = lookupID(typedArray, index, constraint.startToEnd);
                    break;
                }
                case 31: {
                    constraint.startMargin = typedArray.getDimensionPixelSize(index, constraint.startMargin);
                    break;
                }
                case 30: {
                    constraint.rightToRight = lookupID(typedArray, index, constraint.rightToRight);
                    break;
                }
                case 29: {
                    constraint.rightToLeft = lookupID(typedArray, index, constraint.rightToLeft);
                    break;
                }
                case 28: {
                    constraint.rightMargin = typedArray.getDimensionPixelSize(index, constraint.rightMargin);
                    break;
                }
                case 27: {
                    constraint.orientation = typedArray.getInt(index, constraint.orientation);
                    break;
                }
                case 26: {
                    constraint.leftToRight = lookupID(typedArray, index, constraint.leftToRight);
                    break;
                }
                case 25: {
                    constraint.leftToLeft = lookupID(typedArray, index, constraint.leftToLeft);
                    break;
                }
                case 24: {
                    constraint.leftMargin = typedArray.getDimensionPixelSize(index, constraint.leftMargin);
                    break;
                }
                case 23: {
                    constraint.mWidth = typedArray.getLayoutDimension(index, constraint.mWidth);
                    break;
                }
                case 22: {
                    final int int1 = typedArray.getInt(index, constraint.visibility);
                    constraint.visibility = int1;
                    constraint.visibility = ConstraintSet.VISIBILITY_FLAGS[int1];
                    break;
                }
                case 21: {
                    constraint.mHeight = typedArray.getLayoutDimension(index, constraint.mHeight);
                    break;
                }
                case 20: {
                    constraint.horizontalBias = typedArray.getFloat(index, constraint.horizontalBias);
                    break;
                }
                case 19: {
                    constraint.guidePercent = typedArray.getFloat(index, constraint.guidePercent);
                    break;
                }
                case 18: {
                    constraint.guideEnd = typedArray.getDimensionPixelOffset(index, constraint.guideEnd);
                    break;
                }
                case 17: {
                    constraint.guideBegin = typedArray.getDimensionPixelOffset(index, constraint.guideBegin);
                    break;
                }
                case 16: {
                    constraint.goneTopMargin = typedArray.getDimensionPixelSize(index, constraint.goneTopMargin);
                    break;
                }
                case 15: {
                    constraint.goneStartMargin = typedArray.getDimensionPixelSize(index, constraint.goneStartMargin);
                    break;
                }
                case 14: {
                    constraint.goneRightMargin = typedArray.getDimensionPixelSize(index, constraint.goneRightMargin);
                    break;
                }
                case 13: {
                    constraint.goneLeftMargin = typedArray.getDimensionPixelSize(index, constraint.goneLeftMargin);
                    break;
                }
                case 12: {
                    constraint.goneEndMargin = typedArray.getDimensionPixelSize(index, constraint.goneEndMargin);
                    break;
                }
                case 11: {
                    constraint.goneBottomMargin = typedArray.getDimensionPixelSize(index, constraint.goneBottomMargin);
                    break;
                }
                case 10: {
                    constraint.endToStart = lookupID(typedArray, index, constraint.endToStart);
                    break;
                }
                case 9: {
                    constraint.endToEnd = lookupID(typedArray, index, constraint.endToEnd);
                    break;
                }
                case 8: {
                    constraint.endMargin = typedArray.getDimensionPixelSize(index, constraint.endMargin);
                    break;
                }
                case 7: {
                    constraint.editorAbsoluteY = typedArray.getDimensionPixelOffset(index, constraint.editorAbsoluteY);
                    break;
                }
                case 6: {
                    constraint.editorAbsoluteX = typedArray.getDimensionPixelOffset(index, constraint.editorAbsoluteX);
                    break;
                }
                case 5: {
                    constraint.dimensionRatio = typedArray.getString(index);
                    break;
                }
                case 4: {
                    constraint.bottomToTop = lookupID(typedArray, index, constraint.bottomToTop);
                    break;
                }
                case 3: {
                    constraint.bottomToBottom = lookupID(typedArray, index, constraint.bottomToBottom);
                    break;
                }
                case 2: {
                    constraint.bottomMargin = typedArray.getDimensionPixelSize(index, constraint.bottomMargin);
                    break;
                }
                case 1: {
                    constraint.baselineToBaseline = lookupID(typedArray, index, constraint.baselineToBaseline);
                    break;
                }
            }
        }
    }
    
    void applyToInternal(final ConstraintLayout constraintLayout) {
        final int sdk_INT = Build$VERSION.SDK_INT;
        final int childCount = constraintLayout.getChildCount();
        final HashSet<Integer> set = new HashSet<Integer>(this.mConstraints.keySet());
        for (int i = 0; i < childCount; ++i) {
            final View child = constraintLayout.getChildAt(i);
            final int id = child.getId();
            if (id == -1) {
                throw new RuntimeException("All children of ConstraintLayout must have ids to use ConstraintSet");
            }
            if (this.mConstraints.containsKey(id)) {
                set.remove(id);
                final Constraint constraint = this.mConstraints.get(id);
                final int mHelperType = constraint.mHelperType;
                if (mHelperType != -1) {
                    if (mHelperType == 1) {
                        final Barrier barrier = (Barrier)child;
                        barrier.setId(id);
                        barrier.setReferencedIds(constraint.mReferenceIds);
                        barrier.setType(constraint.mBarrierDirection);
                        constraint.applyTo(constraintLayout.generateDefaultLayoutParams());
                    }
                }
                final ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)child.getLayoutParams();
                constraint.applyTo(layoutParams);
                child.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
                child.setVisibility(constraint.visibility);
                if (sdk_INT >= 17) {
                    child.setAlpha(constraint.alpha);
                    child.setRotation(constraint.rotation);
                    child.setRotationX(constraint.rotationX);
                    child.setRotationY(constraint.rotationY);
                    child.setScaleX(constraint.scaleX);
                    child.setScaleY(constraint.scaleY);
                    if (!Float.isNaN(constraint.transformPivotX)) {
                        child.setPivotX(constraint.transformPivotX);
                    }
                    if (!Float.isNaN(constraint.transformPivotY)) {
                        child.setPivotY(constraint.transformPivotY);
                    }
                    child.setTranslationX(constraint.translationX);
                    child.setTranslationY(constraint.translationY);
                    if (sdk_INT >= 21) {
                        child.setTranslationZ(constraint.translationZ);
                        if (constraint.applyElevation) {
                            child.setElevation(constraint.elevation);
                        }
                    }
                }
            }
        }
        for (final Integer key : set) {
            final Constraint constraint2 = this.mConstraints.get(key);
            final int mHelperType2 = constraint2.mHelperType;
            if (mHelperType2 != -1) {
                if (mHelperType2 == 1) {
                    final Barrier barrier2 = new Barrier(constraintLayout.getContext());
                    barrier2.setId((int)key);
                    barrier2.setReferencedIds(constraint2.mReferenceIds);
                    barrier2.setType(constraint2.mBarrierDirection);
                    final ConstraintLayout.LayoutParams generateDefaultLayoutParams = constraintLayout.generateDefaultLayoutParams();
                    constraint2.applyTo(generateDefaultLayoutParams);
                    constraintLayout.addView((View)barrier2, (ViewGroup$LayoutParams)generateDefaultLayoutParams);
                }
            }
            if (constraint2.mIsGuideline) {
                final Guideline guideline = new Guideline(constraintLayout.getContext());
                guideline.setId((int)key);
                final ConstraintLayout.LayoutParams generateDefaultLayoutParams2 = constraintLayout.generateDefaultLayoutParams();
                constraint2.applyTo(generateDefaultLayoutParams2);
                constraintLayout.addView((View)guideline, (ViewGroup$LayoutParams)generateDefaultLayoutParams2);
            }
        }
    }
    
    public void clone(final Constraints constraints) {
        final int childCount = constraints.getChildCount();
        this.mConstraints.clear();
        for (int i = 0; i < childCount; ++i) {
            final View child = constraints.getChildAt(i);
            final Constraints.LayoutParams layoutParams = (Constraints.LayoutParams)child.getLayoutParams();
            final int id = child.getId();
            if (id == -1) {
                throw new RuntimeException("All children of ConstraintLayout must have ids to use ConstraintSet");
            }
            if (!this.mConstraints.containsKey(id)) {
                this.mConstraints.put(id, new Constraint());
            }
            final Constraint constraint = this.mConstraints.get(id);
            if (child instanceof ConstraintHelper) {
                constraint.fillFromConstraints((ConstraintHelper)child, id, layoutParams);
            }
            constraint.fillFromConstraints(id, layoutParams);
        }
    }
    
    public void load(final Context context, int i) {
        final XmlResourceParser xml = context.getResources().getXml(i);
        try {
            String name;
            Constraint fillFromAttributeList;
            for (i = ((XmlPullParser)xml).getEventType(); i != 1; i = ((XmlPullParser)xml).next()) {
                if (i != 0) {
                    if (i == 2) {
                        name = ((XmlPullParser)xml).getName();
                        fillFromAttributeList = this.fillFromAttributeList(context, Xml.asAttributeSet((XmlPullParser)xml));
                        if (name.equalsIgnoreCase("Guideline")) {
                            fillFromAttributeList.mIsGuideline = true;
                        }
                        this.mConstraints.put(fillFromAttributeList.mViewId, fillFromAttributeList);
                    }
                }
                else {
                    ((XmlPullParser)xml).getName();
                }
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        catch (XmlPullParserException ex2) {
            ex2.printStackTrace();
        }
    }
    
    private static class Constraint
    {
        public float alpha;
        public boolean applyElevation;
        public int baselineToBaseline;
        public int bottomMargin;
        public int bottomToBottom;
        public int bottomToTop;
        public float circleAngle;
        public int circleConstraint;
        public int circleRadius;
        public boolean constrainedHeight;
        public boolean constrainedWidth;
        public String dimensionRatio;
        public int editorAbsoluteX;
        public int editorAbsoluteY;
        public float elevation;
        public int endMargin;
        public int endToEnd;
        public int endToStart;
        public int goneBottomMargin;
        public int goneEndMargin;
        public int goneLeftMargin;
        public int goneRightMargin;
        public int goneStartMargin;
        public int goneTopMargin;
        public int guideBegin;
        public int guideEnd;
        public float guidePercent;
        public int heightDefault;
        public int heightMax;
        public int heightMin;
        public float heightPercent;
        public float horizontalBias;
        public int horizontalChainStyle;
        public float horizontalWeight;
        public int leftMargin;
        public int leftToLeft;
        public int leftToRight;
        public int mBarrierDirection;
        public int mHeight;
        public int mHelperType;
        boolean mIsGuideline;
        public int[] mReferenceIds;
        int mViewId;
        public int mWidth;
        public int orientation;
        public int rightMargin;
        public int rightToLeft;
        public int rightToRight;
        public float rotation;
        public float rotationX;
        public float rotationY;
        public float scaleX;
        public float scaleY;
        public int startMargin;
        public int startToEnd;
        public int startToStart;
        public int topMargin;
        public int topToBottom;
        public int topToTop;
        public float transformPivotX;
        public float transformPivotY;
        public float translationX;
        public float translationY;
        public float translationZ;
        public float verticalBias;
        public int verticalChainStyle;
        public float verticalWeight;
        public int visibility;
        public int widthDefault;
        public int widthMax;
        public int widthMin;
        public float widthPercent;
        
        private Constraint() {
            this.mIsGuideline = false;
            this.guideBegin = -1;
            this.guideEnd = -1;
            this.guidePercent = -1.0f;
            this.leftToLeft = -1;
            this.leftToRight = -1;
            this.rightToLeft = -1;
            this.rightToRight = -1;
            this.topToTop = -1;
            this.topToBottom = -1;
            this.bottomToTop = -1;
            this.bottomToBottom = -1;
            this.baselineToBaseline = -1;
            this.startToEnd = -1;
            this.startToStart = -1;
            this.endToStart = -1;
            this.endToEnd = -1;
            this.horizontalBias = 0.5f;
            this.verticalBias = 0.5f;
            this.dimensionRatio = null;
            this.circleConstraint = -1;
            this.circleRadius = 0;
            this.circleAngle = 0.0f;
            this.editorAbsoluteX = -1;
            this.editorAbsoluteY = -1;
            this.orientation = -1;
            this.leftMargin = -1;
            this.rightMargin = -1;
            this.topMargin = -1;
            this.bottomMargin = -1;
            this.endMargin = -1;
            this.startMargin = -1;
            this.visibility = 0;
            this.goneLeftMargin = -1;
            this.goneTopMargin = -1;
            this.goneRightMargin = -1;
            this.goneBottomMargin = -1;
            this.goneEndMargin = -1;
            this.goneStartMargin = -1;
            this.verticalWeight = 0.0f;
            this.horizontalWeight = 0.0f;
            this.horizontalChainStyle = 0;
            this.verticalChainStyle = 0;
            this.alpha = 1.0f;
            this.applyElevation = false;
            this.elevation = 0.0f;
            this.rotation = 0.0f;
            this.rotationX = 0.0f;
            this.rotationY = 0.0f;
            this.scaleX = 1.0f;
            this.scaleY = 1.0f;
            this.transformPivotX = Float.NaN;
            this.transformPivotY = Float.NaN;
            this.translationX = 0.0f;
            this.translationY = 0.0f;
            this.translationZ = 0.0f;
            this.constrainedWidth = false;
            this.constrainedHeight = false;
            this.widthDefault = 0;
            this.heightDefault = 0;
            this.widthMax = -1;
            this.heightMax = -1;
            this.widthMin = -1;
            this.heightMin = -1;
            this.widthPercent = 1.0f;
            this.heightPercent = 1.0f;
            this.mBarrierDirection = -1;
            this.mHelperType = -1;
        }
        
        private void fillFrom(final int mViewId, final ConstraintLayout.LayoutParams layoutParams) {
            this.mViewId = mViewId;
            this.leftToLeft = layoutParams.leftToLeft;
            this.leftToRight = layoutParams.leftToRight;
            this.rightToLeft = layoutParams.rightToLeft;
            this.rightToRight = layoutParams.rightToRight;
            this.topToTop = layoutParams.topToTop;
            this.topToBottom = layoutParams.topToBottom;
            this.bottomToTop = layoutParams.bottomToTop;
            this.bottomToBottom = layoutParams.bottomToBottom;
            this.baselineToBaseline = layoutParams.baselineToBaseline;
            this.startToEnd = layoutParams.startToEnd;
            this.startToStart = layoutParams.startToStart;
            this.endToStart = layoutParams.endToStart;
            this.endToEnd = layoutParams.endToEnd;
            this.horizontalBias = layoutParams.horizontalBias;
            this.verticalBias = layoutParams.verticalBias;
            this.dimensionRatio = layoutParams.dimensionRatio;
            this.circleConstraint = layoutParams.circleConstraint;
            this.circleRadius = layoutParams.circleRadius;
            this.circleAngle = layoutParams.circleAngle;
            this.editorAbsoluteX = layoutParams.editorAbsoluteX;
            this.editorAbsoluteY = layoutParams.editorAbsoluteY;
            this.orientation = layoutParams.orientation;
            this.guidePercent = layoutParams.guidePercent;
            this.guideBegin = layoutParams.guideBegin;
            this.guideEnd = layoutParams.guideEnd;
            this.mWidth = layoutParams.width;
            this.mHeight = layoutParams.height;
            this.leftMargin = layoutParams.leftMargin;
            this.rightMargin = layoutParams.rightMargin;
            this.topMargin = layoutParams.topMargin;
            this.bottomMargin = layoutParams.bottomMargin;
            this.verticalWeight = layoutParams.verticalWeight;
            this.horizontalWeight = layoutParams.horizontalWeight;
            this.verticalChainStyle = layoutParams.verticalChainStyle;
            this.horizontalChainStyle = layoutParams.horizontalChainStyle;
            final boolean constrainedWidth = layoutParams.constrainedWidth;
            this.constrainedWidth = constrainedWidth;
            this.constrainedHeight = layoutParams.constrainedHeight;
            this.widthDefault = layoutParams.matchConstraintDefaultWidth;
            this.heightDefault = layoutParams.matchConstraintDefaultHeight;
            this.constrainedWidth = constrainedWidth;
            this.widthMax = layoutParams.matchConstraintMaxWidth;
            this.heightMax = layoutParams.matchConstraintMaxHeight;
            this.widthMin = layoutParams.matchConstraintMinWidth;
            this.heightMin = layoutParams.matchConstraintMinHeight;
            this.widthPercent = layoutParams.matchConstraintPercentWidth;
            this.heightPercent = layoutParams.matchConstraintPercentHeight;
            if (Build$VERSION.SDK_INT >= 17) {
                this.endMargin = layoutParams.getMarginEnd();
                this.startMargin = layoutParams.getMarginStart();
            }
        }
        
        private void fillFromConstraints(final int n, final Constraints.LayoutParams layoutParams) {
            this.fillFrom(n, layoutParams);
            this.alpha = layoutParams.alpha;
            this.rotation = layoutParams.rotation;
            this.rotationX = layoutParams.rotationX;
            this.rotationY = layoutParams.rotationY;
            this.scaleX = layoutParams.scaleX;
            this.scaleY = layoutParams.scaleY;
            this.transformPivotX = layoutParams.transformPivotX;
            this.transformPivotY = layoutParams.transformPivotY;
            this.translationX = layoutParams.translationX;
            this.translationY = layoutParams.translationY;
            this.translationZ = layoutParams.translationZ;
            this.elevation = layoutParams.elevation;
            this.applyElevation = layoutParams.applyElevation;
        }
        
        private void fillFromConstraints(final ConstraintHelper constraintHelper, final int n, final Constraints.LayoutParams layoutParams) {
            this.fillFromConstraints(n, layoutParams);
            if (constraintHelper instanceof Barrier) {
                this.mHelperType = 1;
                final Barrier barrier = (Barrier)constraintHelper;
                this.mBarrierDirection = barrier.getType();
                this.mReferenceIds = barrier.getReferencedIds();
            }
        }
        
        public void applyTo(final ConstraintLayout.LayoutParams layoutParams) {
            layoutParams.leftToLeft = this.leftToLeft;
            layoutParams.leftToRight = this.leftToRight;
            layoutParams.rightToLeft = this.rightToLeft;
            layoutParams.rightToRight = this.rightToRight;
            layoutParams.topToTop = this.topToTop;
            layoutParams.topToBottom = this.topToBottom;
            layoutParams.bottomToTop = this.bottomToTop;
            layoutParams.bottomToBottom = this.bottomToBottom;
            layoutParams.baselineToBaseline = this.baselineToBaseline;
            layoutParams.startToEnd = this.startToEnd;
            layoutParams.startToStart = this.startToStart;
            layoutParams.endToStart = this.endToStart;
            layoutParams.endToEnd = this.endToEnd;
            layoutParams.leftMargin = this.leftMargin;
            layoutParams.rightMargin = this.rightMargin;
            layoutParams.topMargin = this.topMargin;
            layoutParams.bottomMargin = this.bottomMargin;
            layoutParams.goneStartMargin = this.goneStartMargin;
            layoutParams.goneEndMargin = this.goneEndMargin;
            layoutParams.horizontalBias = this.horizontalBias;
            layoutParams.verticalBias = this.verticalBias;
            layoutParams.circleConstraint = this.circleConstraint;
            layoutParams.circleRadius = this.circleRadius;
            layoutParams.circleAngle = this.circleAngle;
            layoutParams.dimensionRatio = this.dimensionRatio;
            layoutParams.editorAbsoluteX = this.editorAbsoluteX;
            layoutParams.editorAbsoluteY = this.editorAbsoluteY;
            layoutParams.verticalWeight = this.verticalWeight;
            layoutParams.horizontalWeight = this.horizontalWeight;
            layoutParams.verticalChainStyle = this.verticalChainStyle;
            layoutParams.horizontalChainStyle = this.horizontalChainStyle;
            layoutParams.constrainedWidth = this.constrainedWidth;
            layoutParams.constrainedHeight = this.constrainedHeight;
            layoutParams.matchConstraintDefaultWidth = this.widthDefault;
            layoutParams.matchConstraintDefaultHeight = this.heightDefault;
            layoutParams.matchConstraintMaxWidth = this.widthMax;
            layoutParams.matchConstraintMaxHeight = this.heightMax;
            layoutParams.matchConstraintMinWidth = this.widthMin;
            layoutParams.matchConstraintMinHeight = this.heightMin;
            layoutParams.matchConstraintPercentWidth = this.widthPercent;
            layoutParams.matchConstraintPercentHeight = this.heightPercent;
            layoutParams.orientation = this.orientation;
            layoutParams.guidePercent = this.guidePercent;
            layoutParams.guideBegin = this.guideBegin;
            layoutParams.guideEnd = this.guideEnd;
            layoutParams.width = this.mWidth;
            layoutParams.height = this.mHeight;
            if (Build$VERSION.SDK_INT >= 17) {
                layoutParams.setMarginStart(this.startMargin);
                layoutParams.setMarginEnd(this.endMargin);
            }
            layoutParams.validate();
        }
        
        public Constraint clone() {
            final Constraint constraint = new Constraint();
            constraint.mIsGuideline = this.mIsGuideline;
            constraint.mWidth = this.mWidth;
            constraint.mHeight = this.mHeight;
            constraint.guideBegin = this.guideBegin;
            constraint.guideEnd = this.guideEnd;
            constraint.guidePercent = this.guidePercent;
            constraint.leftToLeft = this.leftToLeft;
            constraint.leftToRight = this.leftToRight;
            constraint.rightToLeft = this.rightToLeft;
            constraint.rightToRight = this.rightToRight;
            constraint.topToTop = this.topToTop;
            constraint.topToBottom = this.topToBottom;
            constraint.bottomToTop = this.bottomToTop;
            constraint.bottomToBottom = this.bottomToBottom;
            constraint.baselineToBaseline = this.baselineToBaseline;
            constraint.startToEnd = this.startToEnd;
            constraint.startToStart = this.startToStart;
            constraint.endToStart = this.endToStart;
            constraint.endToEnd = this.endToEnd;
            constraint.horizontalBias = this.horizontalBias;
            constraint.verticalBias = this.verticalBias;
            constraint.dimensionRatio = this.dimensionRatio;
            constraint.editorAbsoluteX = this.editorAbsoluteX;
            constraint.editorAbsoluteY = this.editorAbsoluteY;
            constraint.horizontalBias = this.horizontalBias;
            constraint.horizontalBias = this.horizontalBias;
            constraint.horizontalBias = this.horizontalBias;
            constraint.horizontalBias = this.horizontalBias;
            constraint.horizontalBias = this.horizontalBias;
            constraint.orientation = this.orientation;
            constraint.leftMargin = this.leftMargin;
            constraint.rightMargin = this.rightMargin;
            constraint.topMargin = this.topMargin;
            constraint.bottomMargin = this.bottomMargin;
            constraint.endMargin = this.endMargin;
            constraint.startMargin = this.startMargin;
            constraint.visibility = this.visibility;
            constraint.goneLeftMargin = this.goneLeftMargin;
            constraint.goneTopMargin = this.goneTopMargin;
            constraint.goneRightMargin = this.goneRightMargin;
            constraint.goneBottomMargin = this.goneBottomMargin;
            constraint.goneEndMargin = this.goneEndMargin;
            constraint.goneStartMargin = this.goneStartMargin;
            constraint.verticalWeight = this.verticalWeight;
            constraint.horizontalWeight = this.horizontalWeight;
            constraint.horizontalChainStyle = this.horizontalChainStyle;
            constraint.verticalChainStyle = this.verticalChainStyle;
            constraint.alpha = this.alpha;
            constraint.applyElevation = this.applyElevation;
            constraint.elevation = this.elevation;
            constraint.rotation = this.rotation;
            constraint.rotationX = this.rotationX;
            constraint.rotationY = this.rotationY;
            constraint.scaleX = this.scaleX;
            constraint.scaleY = this.scaleY;
            constraint.transformPivotX = this.transformPivotX;
            constraint.transformPivotY = this.transformPivotY;
            constraint.translationX = this.translationX;
            constraint.translationY = this.translationY;
            constraint.translationZ = this.translationZ;
            constraint.constrainedWidth = this.constrainedWidth;
            constraint.constrainedHeight = this.constrainedHeight;
            constraint.widthDefault = this.widthDefault;
            constraint.heightDefault = this.heightDefault;
            constraint.widthMax = this.widthMax;
            constraint.heightMax = this.heightMax;
            constraint.widthMin = this.widthMin;
            constraint.heightMin = this.heightMin;
            constraint.widthPercent = this.widthPercent;
            constraint.heightPercent = this.heightPercent;
            constraint.mBarrierDirection = this.mBarrierDirection;
            constraint.mHelperType = this.mHelperType;
            final int[] mReferenceIds = this.mReferenceIds;
            if (mReferenceIds != null) {
                constraint.mReferenceIds = Arrays.copyOf(mReferenceIds, mReferenceIds.length);
            }
            constraint.circleConstraint = this.circleConstraint;
            constraint.circleRadius = this.circleRadius;
            constraint.circleAngle = this.circleAngle;
            return constraint;
        }
    }
}
