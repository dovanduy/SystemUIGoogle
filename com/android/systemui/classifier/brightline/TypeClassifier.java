// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier.brightline;

public class TypeClassifier extends FalsingClassifier
{
    TypeClassifier(final FalsingDataProvider falsingDataProvider) {
        super(falsingDataProvider);
    }
    
    @Override
    String getReason() {
        return String.format("{vertical=%s, up=%s, right=%s}", this.isVertical(), this.isUp(), this.isRight());
    }
    
    public boolean isFalseTouch() {
        final boolean vertical = this.isVertical();
        final boolean up = this.isUp();
        final boolean right = this.isRight();
        final int interactionType = this.getInteractionType();
        final boolean b = false;
        final boolean b2 = false;
        final boolean b3 = false;
        boolean b4 = false;
        Label_0137: {
            if (interactionType != 0) {
                if (interactionType == 1) {
                    return vertical;
                }
                if (interactionType != 2) {
                    if (interactionType != 4) {
                        if (interactionType == 5) {
                            if (right) {
                                final boolean b5 = b;
                                if (up) {
                                    return b5;
                                }
                            }
                            return true;
                        }
                        if (interactionType == 6) {
                            if (right || !up) {
                                b4 = true;
                            }
                            return b4;
                        }
                        if (interactionType != 8) {
                            if (interactionType != 9) {
                                return true;
                            }
                            break Label_0137;
                        }
                    }
                    if (vertical) {
                        final boolean b6 = b2;
                        if (up) {
                            return b6;
                        }
                    }
                    return true;
                }
            }
        }
        if (vertical) {
            final boolean b7 = b3;
            if (!up) {
                return b7;
            }
        }
        return true;
    }
}
