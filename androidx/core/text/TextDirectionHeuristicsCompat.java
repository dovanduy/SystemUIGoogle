// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.text;

import java.util.Locale;

public final class TextDirectionHeuristicsCompat
{
    public static final TextDirectionHeuristicCompat FIRSTSTRONG_LTR;
    public static final TextDirectionHeuristicCompat LTR;
    public static final TextDirectionHeuristicCompat RTL;
    
    static {
        LTR = new TextDirectionHeuristicInternal(null, false);
        RTL = new TextDirectionHeuristicInternal(null, true);
        FIRSTSTRONG_LTR = new TextDirectionHeuristicInternal(FirstStrong.INSTANCE, false);
        final FirstStrong instance = FirstStrong.INSTANCE;
        final AnyStrong instance_RTL = AnyStrong.INSTANCE_RTL;
        final TextDirectionHeuristicLocale instance2 = TextDirectionHeuristicLocale.INSTANCE;
    }
    
    static int isRtlText(final int n) {
        if (n == 0) {
            return 1;
        }
        if (n != 1 && n != 2) {
            return 2;
        }
        return 0;
    }
    
    static int isRtlTextOrFormat(final int n) {
        if (n != 0) {
            if (n != 1 && n != 2) {
                switch (n) {
                    default: {
                        return 2;
                    }
                    case 16:
                    case 17: {
                        break;
                    }
                    case 14:
                    case 15: {
                        return 1;
                    }
                }
            }
            return 0;
        }
        return 1;
    }
    
    private static class AnyStrong implements TextDirectionAlgorithm
    {
        static final AnyStrong INSTANCE_RTL;
        private final boolean mLookForRtl;
        
        static {
            INSTANCE_RTL = new AnyStrong(true);
        }
        
        private AnyStrong(final boolean mLookForRtl) {
            this.mLookForRtl = mLookForRtl;
        }
        
        @Override
        public int checkRtl(final CharSequence charSequence, final int n, final int n2) {
            boolean b = false;
            for (int i = n; i < n2 + n; ++i) {
                final int rtlText = TextDirectionHeuristicsCompat.isRtlText(Character.getDirectionality(charSequence.charAt(i)));
                if (rtlText != 0) {
                    if (rtlText != 1) {
                        continue;
                    }
                    if (!this.mLookForRtl) {
                        return 1;
                    }
                }
                else if (this.mLookForRtl) {
                    return 0;
                }
                b = true;
            }
            if (b) {
                return this.mLookForRtl ? 1 : 0;
            }
            return 2;
        }
    }
    
    private static class FirstStrong implements TextDirectionAlgorithm
    {
        static final FirstStrong INSTANCE;
        
        static {
            INSTANCE = new FirstStrong();
        }
        
        @Override
        public int checkRtl(final CharSequence charSequence, final int n, final int n2) {
            int rtlTextOrFormat = 2;
            for (int n3 = n; n3 < n2 + n && rtlTextOrFormat == 2; rtlTextOrFormat = TextDirectionHeuristicsCompat.isRtlTextOrFormat(Character.getDirectionality(charSequence.charAt(n3))), ++n3) {}
            return rtlTextOrFormat;
        }
    }
    
    private interface TextDirectionAlgorithm
    {
        int checkRtl(final CharSequence p0, final int p1, final int p2);
    }
    
    private abstract static class TextDirectionHeuristicImpl implements TextDirectionHeuristicCompat
    {
        private final TextDirectionAlgorithm mAlgorithm;
        
        TextDirectionHeuristicImpl(final TextDirectionAlgorithm mAlgorithm) {
            this.mAlgorithm = mAlgorithm;
        }
        
        private boolean doCheck(final CharSequence charSequence, int checkRtl, final int n) {
            checkRtl = this.mAlgorithm.checkRtl(charSequence, checkRtl, n);
            return checkRtl == 0 || (checkRtl != 1 && this.defaultIsRtl());
        }
        
        protected abstract boolean defaultIsRtl();
        
        @Override
        public boolean isRtl(final CharSequence charSequence, final int n, final int n2) {
            if (charSequence == null || n < 0 || n2 < 0 || charSequence.length() - n2 < n) {
                throw new IllegalArgumentException();
            }
            if (this.mAlgorithm == null) {
                return this.defaultIsRtl();
            }
            return this.doCheck(charSequence, n, n2);
        }
    }
    
    private static class TextDirectionHeuristicInternal extends TextDirectionHeuristicImpl
    {
        private final boolean mDefaultIsRtl;
        
        TextDirectionHeuristicInternal(final TextDirectionAlgorithm textDirectionAlgorithm, final boolean mDefaultIsRtl) {
            super(textDirectionAlgorithm);
            this.mDefaultIsRtl = mDefaultIsRtl;
        }
        
        @Override
        protected boolean defaultIsRtl() {
            return this.mDefaultIsRtl;
        }
    }
    
    private static class TextDirectionHeuristicLocale extends TextDirectionHeuristicImpl
    {
        static final TextDirectionHeuristicLocale INSTANCE;
        
        static {
            INSTANCE = new TextDirectionHeuristicLocale();
        }
        
        TextDirectionHeuristicLocale() {
            super(null);
        }
        
        @Override
        protected boolean defaultIsRtl() {
            final int layoutDirectionFromLocale = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault());
            boolean b = true;
            if (layoutDirectionFromLocale != 1) {
                b = false;
            }
            return b;
        }
    }
}
