// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.text;

import android.text.SpannableStringBuilder;
import java.util.Locale;

public final class BidiFormatter
{
    static final BidiFormatter DEFAULT_LTR_INSTANCE;
    static final BidiFormatter DEFAULT_RTL_INSTANCE;
    static final TextDirectionHeuristicCompat DEFAULT_TEXT_DIRECTION_HEURISTIC;
    private static final String LRM_STRING;
    private static final String RLM_STRING;
    private final TextDirectionHeuristicCompat mDefaultTextDirectionHeuristicCompat;
    private final int mFlags;
    private final boolean mIsRtlContext;
    
    static {
        DEFAULT_TEXT_DIRECTION_HEURISTIC = TextDirectionHeuristicsCompat.FIRSTSTRONG_LTR;
        LRM_STRING = Character.toString('\u200e');
        RLM_STRING = Character.toString('\u200f');
        DEFAULT_LTR_INSTANCE = new BidiFormatter(false, 2, BidiFormatter.DEFAULT_TEXT_DIRECTION_HEURISTIC);
        DEFAULT_RTL_INSTANCE = new BidiFormatter(true, 2, BidiFormatter.DEFAULT_TEXT_DIRECTION_HEURISTIC);
    }
    
    BidiFormatter(final boolean mIsRtlContext, final int mFlags, final TextDirectionHeuristicCompat mDefaultTextDirectionHeuristicCompat) {
        this.mIsRtlContext = mIsRtlContext;
        this.mFlags = mFlags;
        this.mDefaultTextDirectionHeuristicCompat = mDefaultTextDirectionHeuristicCompat;
    }
    
    private static int getEntryDir(final CharSequence charSequence) {
        return new DirectionalityEstimator(charSequence, false).getEntryDir();
    }
    
    private static int getExitDir(final CharSequence charSequence) {
        return new DirectionalityEstimator(charSequence, false).getExitDir();
    }
    
    public static BidiFormatter getInstance() {
        return new Builder().build();
    }
    
    static boolean isRtlLocale(final Locale locale) {
        final int layoutDirectionFromLocale = TextUtilsCompat.getLayoutDirectionFromLocale(locale);
        boolean b = true;
        if (layoutDirectionFromLocale != 1) {
            b = false;
        }
        return b;
    }
    
    private String markAfter(final CharSequence charSequence, final TextDirectionHeuristicCompat textDirectionHeuristicCompat) {
        final boolean rtl = textDirectionHeuristicCompat.isRtl(charSequence, 0, charSequence.length());
        if (!this.mIsRtlContext && (rtl || getExitDir(charSequence) == 1)) {
            return BidiFormatter.LRM_STRING;
        }
        if (this.mIsRtlContext && (!rtl || getExitDir(charSequence) == -1)) {
            return BidiFormatter.RLM_STRING;
        }
        return "";
    }
    
    private String markBefore(final CharSequence charSequence, final TextDirectionHeuristicCompat textDirectionHeuristicCompat) {
        final boolean rtl = textDirectionHeuristicCompat.isRtl(charSequence, 0, charSequence.length());
        if (!this.mIsRtlContext && (rtl || getEntryDir(charSequence) == 1)) {
            return BidiFormatter.LRM_STRING;
        }
        if (this.mIsRtlContext && (!rtl || getEntryDir(charSequence) == -1)) {
            return BidiFormatter.RLM_STRING;
        }
        return "";
    }
    
    public boolean getStereoReset() {
        return (this.mFlags & 0x2) != 0x0;
    }
    
    public CharSequence unicodeWrap(final CharSequence charSequence, TextDirectionHeuristicCompat textDirectionHeuristicCompat, final boolean b) {
        if (charSequence == null) {
            return null;
        }
        final boolean rtl = textDirectionHeuristicCompat.isRtl(charSequence, 0, charSequence.length());
        final SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        if (this.getStereoReset() && b) {
            if (rtl) {
                textDirectionHeuristicCompat = TextDirectionHeuristicsCompat.RTL;
            }
            else {
                textDirectionHeuristicCompat = TextDirectionHeuristicsCompat.LTR;
            }
            spannableStringBuilder.append((CharSequence)this.markBefore(charSequence, textDirectionHeuristicCompat));
        }
        if (rtl != this.mIsRtlContext) {
            char c;
            if (rtl) {
                c = '\u202b';
            }
            else {
                c = '\u202a';
            }
            spannableStringBuilder.append(c);
            spannableStringBuilder.append(charSequence);
            spannableStringBuilder.append('\u202c');
        }
        else {
            spannableStringBuilder.append(charSequence);
        }
        if (b) {
            if (rtl) {
                textDirectionHeuristicCompat = TextDirectionHeuristicsCompat.RTL;
            }
            else {
                textDirectionHeuristicCompat = TextDirectionHeuristicsCompat.LTR;
            }
            spannableStringBuilder.append((CharSequence)this.markAfter(charSequence, textDirectionHeuristicCompat));
        }
        return (CharSequence)spannableStringBuilder;
    }
    
    public String unicodeWrap(final String s) {
        return this.unicodeWrap(s, this.mDefaultTextDirectionHeuristicCompat, true);
    }
    
    public String unicodeWrap(final String s, final TextDirectionHeuristicCompat textDirectionHeuristicCompat, final boolean b) {
        if (s == null) {
            return null;
        }
        return this.unicodeWrap((CharSequence)s, textDirectionHeuristicCompat, b).toString();
    }
    
    public static final class Builder
    {
        private int mFlags;
        private boolean mIsRtlContext;
        private TextDirectionHeuristicCompat mTextDirectionHeuristicCompat;
        
        public Builder() {
            this.initialize(BidiFormatter.isRtlLocale(Locale.getDefault()));
        }
        
        private static BidiFormatter getDefaultInstanceFromContext(final boolean b) {
            BidiFormatter bidiFormatter;
            if (b) {
                bidiFormatter = BidiFormatter.DEFAULT_RTL_INSTANCE;
            }
            else {
                bidiFormatter = BidiFormatter.DEFAULT_LTR_INSTANCE;
            }
            return bidiFormatter;
        }
        
        private void initialize(final boolean mIsRtlContext) {
            this.mIsRtlContext = mIsRtlContext;
            this.mTextDirectionHeuristicCompat = BidiFormatter.DEFAULT_TEXT_DIRECTION_HEURISTIC;
            this.mFlags = 2;
        }
        
        public BidiFormatter build() {
            if (this.mFlags == 2 && this.mTextDirectionHeuristicCompat == BidiFormatter.DEFAULT_TEXT_DIRECTION_HEURISTIC) {
                return getDefaultInstanceFromContext(this.mIsRtlContext);
            }
            return new BidiFormatter(this.mIsRtlContext, this.mFlags, this.mTextDirectionHeuristicCompat);
        }
    }
    
    private static class DirectionalityEstimator
    {
        private static final byte[] DIR_TYPE_CACHE;
        private int charIndex;
        private final boolean isHtml;
        private char lastChar;
        private final int length;
        private final CharSequence text;
        
        static {
            DIR_TYPE_CACHE = new byte[1792];
            for (int i = 0; i < 1792; ++i) {
                DirectionalityEstimator.DIR_TYPE_CACHE[i] = Character.getDirectionality(i);
            }
        }
        
        DirectionalityEstimator(final CharSequence text, final boolean isHtml) {
            this.text = text;
            this.isHtml = isHtml;
            this.length = text.length();
        }
        
        private static byte getCachedDirectionality(final char ch) {
            byte directionality;
            if (ch < '\u0700') {
                directionality = DirectionalityEstimator.DIR_TYPE_CACHE[ch];
            }
            else {
                directionality = Character.getDirectionality(ch);
            }
            return directionality;
        }
        
        private byte skipEntityBackward() {
            final int charIndex = this.charIndex;
            char char1;
            do {
                int charIndex2 = this.charIndex;
                if (charIndex2 <= 0) {
                    break;
                }
                final CharSequence text = this.text;
                --charIndex2;
                this.charIndex = charIndex2;
                char1 = text.charAt(charIndex2);
                this.lastChar = char1;
                if (char1 == '&') {
                    return 12;
                }
            } while (char1 != ';');
            this.charIndex = charIndex;
            this.lastChar = 59;
            return 13;
        }
        
        private byte skipEntityForward() {
            char char1;
            do {
                final int charIndex = this.charIndex;
                if (charIndex >= this.length) {
                    break;
                }
                final CharSequence text = this.text;
                this.charIndex = charIndex + 1;
                char1 = text.charAt(charIndex);
                this.lastChar = char1;
            } while (char1 != ';');
            return 12;
        }
        
        private byte skipTagBackward() {
            final int charIndex = this.charIndex;
            while (true) {
                int charIndex2 = this.charIndex;
                if (charIndex2 <= 0) {
                    break;
                }
                final CharSequence text = this.text;
                --charIndex2;
                this.charIndex = charIndex2;
                final char char1 = text.charAt(charIndex2);
                this.lastChar = char1;
                if (char1 == '<') {
                    return 12;
                }
                if (char1 == '>') {
                    break;
                }
                if (char1 != '\"' && char1 != '\'') {
                    continue;
                }
                final char lastChar = this.lastChar;
                char char2;
                do {
                    int charIndex3 = this.charIndex;
                    if (charIndex3 <= 0) {
                        break;
                    }
                    final CharSequence text2 = this.text;
                    --charIndex3;
                    this.charIndex = charIndex3;
                    char2 = text2.charAt(charIndex3);
                    this.lastChar = char2;
                } while (char2 != lastChar);
            }
            this.charIndex = charIndex;
            this.lastChar = 62;
            return 13;
        }
        
        private byte skipTagForward() {
            final int charIndex = this.charIndex;
            while (true) {
                final int charIndex2 = this.charIndex;
                if (charIndex2 >= this.length) {
                    this.charIndex = charIndex;
                    this.lastChar = 60;
                    return 13;
                }
                final CharSequence text = this.text;
                this.charIndex = charIndex2 + 1;
                final char char1 = text.charAt(charIndex2);
                this.lastChar = char1;
                if (char1 == '>') {
                    return 12;
                }
                if (char1 != '\"' && char1 != '\'') {
                    continue;
                }
                final char lastChar = this.lastChar;
                char char2;
                do {
                    final int charIndex3 = this.charIndex;
                    if (charIndex3 >= this.length) {
                        break;
                    }
                    final CharSequence text2 = this.text;
                    this.charIndex = charIndex3 + 1;
                    char2 = text2.charAt(charIndex3);
                    this.lastChar = char2;
                } while (char2 != lastChar);
            }
        }
        
        byte dirTypeBackward() {
            final char char1 = this.text.charAt(this.charIndex - 1);
            this.lastChar = char1;
            if (Character.isLowSurrogate(char1)) {
                final int codePointBefore = Character.codePointBefore(this.text, this.charIndex);
                this.charIndex -= Character.charCount(codePointBefore);
                return Character.getDirectionality(codePointBefore);
            }
            --this.charIndex;
            byte b = getCachedDirectionality(this.lastChar);
            if (this.isHtml) {
                final char lastChar = this.lastChar;
                if (lastChar == '>') {
                    b = this.skipTagBackward();
                }
                else {
                    b = b;
                    if (lastChar == ';') {
                        b = this.skipEntityBackward();
                    }
                }
            }
            return b;
        }
        
        byte dirTypeForward() {
            final char char1 = this.text.charAt(this.charIndex);
            this.lastChar = char1;
            if (Character.isHighSurrogate(char1)) {
                final int codePoint = Character.codePointAt(this.text, this.charIndex);
                this.charIndex += Character.charCount(codePoint);
                return Character.getDirectionality(codePoint);
            }
            ++this.charIndex;
            byte b = getCachedDirectionality(this.lastChar);
            if (this.isHtml) {
                final char lastChar = this.lastChar;
                if (lastChar == '<') {
                    b = this.skipTagForward();
                }
                else {
                    b = b;
                    if (lastChar == '&') {
                        b = this.skipEntityForward();
                    }
                }
            }
            return b;
        }
        
        int getEntryDir() {
            this.charIndex = 0;
            int n = 0;
            int n3;
            int n2 = n3 = n;
            while (this.charIndex < this.length && n == 0) {
                final byte dirTypeForward = this.dirTypeForward();
                if (dirTypeForward != 0) {
                    if (dirTypeForward != 1 && dirTypeForward != 2) {
                        if (dirTypeForward == 9) {
                            continue;
                        }
                        switch (dirTypeForward) {
                            case 18: {
                                --n3;
                                n2 = 0;
                                continue;
                            }
                            case 16:
                            case 17: {
                                ++n3;
                                n2 = 1;
                                continue;
                            }
                            case 14:
                            case 15: {
                                ++n3;
                                n2 = -1;
                                continue;
                            }
                        }
                    }
                    else if (n3 == 0) {
                        return 1;
                    }
                }
                else if (n3 == 0) {
                    return -1;
                }
                n = n3;
            }
            if (n == 0) {
                return 0;
            }
            if (n2 != 0) {
                return n2;
            }
            while (this.charIndex > 0) {
                switch (this.dirTypeBackward()) {
                    default: {
                        continue;
                    }
                    case 18: {
                        ++n3;
                        continue;
                    }
                    case 16:
                    case 17: {
                        if (n == n3) {
                            return 1;
                        }
                        break;
                    }
                    case 14:
                    case 15: {
                        if (n == n3) {
                            return -1;
                        }
                        break;
                    }
                }
                --n3;
            }
            return 0;
        }
        
        int getExitDir() {
            throw new RuntimeException("d2j fail translate: java.lang.RuntimeException: fail exe a8 = a1\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:31)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.Ir2JRegAssignTransformer.transform(Ir2JRegAssignTransformer.java:182)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:167)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:442)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:40)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:132)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:575)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:434)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:450)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:175)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:275)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:112)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:290)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:33)\nCaused by: java.lang.NullPointerException\n\tat com.googlecode.dex2jar.ir.ts.an.SimpleLiveAnalyze.onUseLocal(SimpleLiveAnalyze.java:89)\n\tat com.googlecode.dex2jar.ir.ts.an.SimpleLiveAnalyze.onUseLocal(SimpleLiveAnalyze.java:27)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.onUse(BaseAnalyze.java:166)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.onUse(BaseAnalyze.java:31)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.travel(Cfg.java:331)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.travel(Cfg.java:387)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:90)\n\t... 17 more\n");
        }
    }
}
