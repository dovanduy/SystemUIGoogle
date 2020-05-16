// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.text;

import android.text.TextDirectionHeuristics;
import androidx.core.util.ObjectsCompat;
import android.text.TextUtils;
import android.text.PrecomputedText$Params$Builder;
import android.text.PrecomputedText$Params;
import android.text.TextDirectionHeuristic;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import android.annotation.SuppressLint;
import android.os.Build$VERSION;
import android.text.PrecomputedText;
import android.text.Spannable;

public class PrecomputedTextCompat implements Spannable
{
    private final Params mParams;
    private final Spannable mText;
    private final PrecomputedText mWrapped;
    
    public char charAt(final int n) {
        return this.mText.charAt(n);
    }
    
    public Params getParams() {
        return this.mParams;
    }
    
    public PrecomputedText getPrecomputedText() {
        final Spannable mText = this.mText;
        if (mText instanceof PrecomputedText) {
            return (PrecomputedText)mText;
        }
        return null;
    }
    
    public int getSpanEnd(final Object o) {
        return this.mText.getSpanEnd(o);
    }
    
    public int getSpanFlags(final Object o) {
        return this.mText.getSpanFlags(o);
    }
    
    public int getSpanStart(final Object o) {
        return this.mText.getSpanStart(o);
    }
    
    @SuppressLint({ "NewApi" })
    public <T> T[] getSpans(final int n, final int n2, final Class<T> clazz) {
        if (Build$VERSION.SDK_INT >= 29) {
            return (T[])this.mWrapped.getSpans(n, n2, (Class)clazz);
        }
        return (T[])this.mText.getSpans(n, n2, (Class)clazz);
    }
    
    public int length() {
        return this.mText.length();
    }
    
    public int nextSpanTransition(final int n, final int n2, final Class clazz) {
        return this.mText.nextSpanTransition(n, n2, clazz);
    }
    
    @SuppressLint({ "NewApi" })
    public void removeSpan(final Object o) {
        if (!(o instanceof MetricAffectingSpan)) {
            if (Build$VERSION.SDK_INT >= 29) {
                this.mWrapped.removeSpan(o);
            }
            else {
                this.mText.removeSpan(o);
            }
            return;
        }
        throw new IllegalArgumentException("MetricAffectingSpan can not be removed from PrecomputedText.");
    }
    
    @SuppressLint({ "NewApi" })
    public void setSpan(final Object o, final int n, final int n2, final int n3) {
        if (!(o instanceof MetricAffectingSpan)) {
            if (Build$VERSION.SDK_INT >= 29) {
                this.mWrapped.setSpan(o, n, n2, n3);
            }
            else {
                this.mText.setSpan(o, n, n2, n3);
            }
            return;
        }
        throw new IllegalArgumentException("MetricAffectingSpan can not be set to PrecomputedText.");
    }
    
    public CharSequence subSequence(final int n, final int n2) {
        return this.mText.subSequence(n, n2);
    }
    
    @Override
    public String toString() {
        return this.mText.toString();
    }
    
    public static final class Params
    {
        private final int mBreakStrategy;
        private final int mHyphenationFrequency;
        private final TextPaint mPaint;
        private final TextDirectionHeuristic mTextDir;
        
        public Params(final PrecomputedText$Params precomputedText$Params) {
            this.mPaint = precomputedText$Params.getTextPaint();
            this.mTextDir = precomputedText$Params.getTextDirection();
            this.mBreakStrategy = precomputedText$Params.getBreakStrategy();
            this.mHyphenationFrequency = precomputedText$Params.getHyphenationFrequency();
            final int sdk_INT = Build$VERSION.SDK_INT;
        }
        
        @SuppressLint({ "NewApi" })
        Params(final TextPaint mPaint, final TextDirectionHeuristic textDirectionHeuristic, final int n, final int n2) {
            if (Build$VERSION.SDK_INT >= 29) {
                new PrecomputedText$Params$Builder(mPaint).setBreakStrategy(n).setHyphenationFrequency(n2).setTextDirection(textDirectionHeuristic).build();
            }
            this.mPaint = mPaint;
            this.mTextDir = textDirectionHeuristic;
            this.mBreakStrategy = n;
            this.mHyphenationFrequency = n2;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Params)) {
                return false;
            }
            final Params params = (Params)o;
            return this.equalsWithoutTextDirection(params) && (Build$VERSION.SDK_INT < 18 || this.mTextDir == params.getTextDirection());
        }
        
        public boolean equalsWithoutTextDirection(final Params params) {
            final int sdk_INT = Build$VERSION.SDK_INT;
            if (sdk_INT >= 23) {
                if (this.mBreakStrategy != params.getBreakStrategy()) {
                    return false;
                }
                if (this.mHyphenationFrequency != params.getHyphenationFrequency()) {
                    return false;
                }
            }
            if (this.mPaint.getTextSize() != params.getTextPaint().getTextSize()) {
                return false;
            }
            if (this.mPaint.getTextScaleX() != params.getTextPaint().getTextScaleX()) {
                return false;
            }
            if (this.mPaint.getTextSkewX() != params.getTextPaint().getTextSkewX()) {
                return false;
            }
            if (sdk_INT >= 21) {
                if (this.mPaint.getLetterSpacing() != params.getTextPaint().getLetterSpacing()) {
                    return false;
                }
                if (!TextUtils.equals((CharSequence)this.mPaint.getFontFeatureSettings(), (CharSequence)params.getTextPaint().getFontFeatureSettings())) {
                    return false;
                }
            }
            if (this.mPaint.getFlags() != params.getTextPaint().getFlags()) {
                return false;
            }
            if (sdk_INT >= 24) {
                if (!this.mPaint.getTextLocales().equals((Object)params.getTextPaint().getTextLocales())) {
                    return false;
                }
            }
            else if (sdk_INT >= 17 && !this.mPaint.getTextLocale().equals(params.getTextPaint().getTextLocale())) {
                return false;
            }
            if (this.mPaint.getTypeface() == null) {
                if (params.getTextPaint().getTypeface() != null) {
                    return false;
                }
            }
            else if (!this.mPaint.getTypeface().equals((Object)params.getTextPaint().getTypeface())) {
                return false;
            }
            return true;
        }
        
        public int getBreakStrategy() {
            return this.mBreakStrategy;
        }
        
        public int getHyphenationFrequency() {
            return this.mHyphenationFrequency;
        }
        
        public TextDirectionHeuristic getTextDirection() {
            return this.mTextDir;
        }
        
        public TextPaint getTextPaint() {
            return this.mPaint;
        }
        
        @Override
        public int hashCode() {
            final int sdk_INT = Build$VERSION.SDK_INT;
            if (sdk_INT >= 24) {
                return ObjectsCompat.hash(this.mPaint.getTextSize(), this.mPaint.getTextScaleX(), this.mPaint.getTextSkewX(), this.mPaint.getLetterSpacing(), this.mPaint.getFlags(), this.mPaint.getTextLocales(), this.mPaint.getTypeface(), this.mPaint.isElegantTextHeight(), this.mTextDir, this.mBreakStrategy, this.mHyphenationFrequency);
            }
            if (sdk_INT >= 21) {
                return ObjectsCompat.hash(this.mPaint.getTextSize(), this.mPaint.getTextScaleX(), this.mPaint.getTextSkewX(), this.mPaint.getLetterSpacing(), this.mPaint.getFlags(), this.mPaint.getTextLocale(), this.mPaint.getTypeface(), this.mPaint.isElegantTextHeight(), this.mTextDir, this.mBreakStrategy, this.mHyphenationFrequency);
            }
            if (sdk_INT >= 18) {
                return ObjectsCompat.hash(this.mPaint.getTextSize(), this.mPaint.getTextScaleX(), this.mPaint.getTextSkewX(), this.mPaint.getFlags(), this.mPaint.getTextLocale(), this.mPaint.getTypeface(), this.mTextDir, this.mBreakStrategy, this.mHyphenationFrequency);
            }
            if (sdk_INT >= 17) {
                return ObjectsCompat.hash(this.mPaint.getTextSize(), this.mPaint.getTextScaleX(), this.mPaint.getTextSkewX(), this.mPaint.getFlags(), this.mPaint.getTextLocale(), this.mPaint.getTypeface(), this.mTextDir, this.mBreakStrategy, this.mHyphenationFrequency);
            }
            return ObjectsCompat.hash(this.mPaint.getTextSize(), this.mPaint.getTextScaleX(), this.mPaint.getTextSkewX(), this.mPaint.getFlags(), this.mPaint.getTypeface(), this.mTextDir, this.mBreakStrategy, this.mHyphenationFrequency);
        }
        
        @Override
        public String toString() {
            final int sdk_INT = Build$VERSION.SDK_INT;
            final StringBuilder sb = new StringBuilder("{");
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("textSize=");
            sb2.append(this.mPaint.getTextSize());
            sb.append(sb2.toString());
            final StringBuilder sb3 = new StringBuilder();
            sb3.append(", textScaleX=");
            sb3.append(this.mPaint.getTextScaleX());
            sb.append(sb3.toString());
            final StringBuilder sb4 = new StringBuilder();
            sb4.append(", textSkewX=");
            sb4.append(this.mPaint.getTextSkewX());
            sb.append(sb4.toString());
            if (sdk_INT >= 21) {
                final StringBuilder sb5 = new StringBuilder();
                sb5.append(", letterSpacing=");
                sb5.append(this.mPaint.getLetterSpacing());
                sb.append(sb5.toString());
                final StringBuilder sb6 = new StringBuilder();
                sb6.append(", elegantTextHeight=");
                sb6.append(this.mPaint.isElegantTextHeight());
                sb.append(sb6.toString());
            }
            if (sdk_INT >= 24) {
                final StringBuilder sb7 = new StringBuilder();
                sb7.append(", textLocale=");
                sb7.append(this.mPaint.getTextLocales());
                sb.append(sb7.toString());
            }
            else if (sdk_INT >= 17) {
                final StringBuilder sb8 = new StringBuilder();
                sb8.append(", textLocale=");
                sb8.append(this.mPaint.getTextLocale());
                sb.append(sb8.toString());
            }
            final StringBuilder sb9 = new StringBuilder();
            sb9.append(", typeface=");
            sb9.append(this.mPaint.getTypeface());
            sb.append(sb9.toString());
            if (sdk_INT >= 26) {
                final StringBuilder sb10 = new StringBuilder();
                sb10.append(", variationSettings=");
                sb10.append(this.mPaint.getFontVariationSettings());
                sb.append(sb10.toString());
            }
            final StringBuilder sb11 = new StringBuilder();
            sb11.append(", textDir=");
            sb11.append(this.mTextDir);
            sb.append(sb11.toString());
            final StringBuilder sb12 = new StringBuilder();
            sb12.append(", breakStrategy=");
            sb12.append(this.mBreakStrategy);
            sb.append(sb12.toString());
            final StringBuilder sb13 = new StringBuilder();
            sb13.append(", hyphenationFrequency=");
            sb13.append(this.mHyphenationFrequency);
            sb.append(sb13.toString());
            sb.append("}");
            return sb.toString();
        }
        
        public static class Builder
        {
            private int mBreakStrategy;
            private int mHyphenationFrequency;
            private final TextPaint mPaint;
            private TextDirectionHeuristic mTextDir;
            
            public Builder(final TextPaint mPaint) {
                final int sdk_INT = Build$VERSION.SDK_INT;
                this.mPaint = mPaint;
                if (sdk_INT >= 23) {
                    this.mBreakStrategy = 1;
                    this.mHyphenationFrequency = 1;
                }
                else {
                    this.mHyphenationFrequency = 0;
                    this.mBreakStrategy = 0;
                }
                if (sdk_INT >= 18) {
                    this.mTextDir = TextDirectionHeuristics.FIRSTSTRONG_LTR;
                }
                else {
                    this.mTextDir = null;
                }
            }
            
            public Params build() {
                return new Params(this.mPaint, this.mTextDir, this.mBreakStrategy, this.mHyphenationFrequency);
            }
            
            public Builder setBreakStrategy(final int mBreakStrategy) {
                this.mBreakStrategy = mBreakStrategy;
                return this;
            }
            
            public Builder setHyphenationFrequency(final int mHyphenationFrequency) {
                this.mHyphenationFrequency = mHyphenationFrequency;
                return this;
            }
            
            public Builder setTextDirection(final TextDirectionHeuristic mTextDir) {
                this.mTextDir = mTextDir;
                return this;
            }
        }
    }
}
