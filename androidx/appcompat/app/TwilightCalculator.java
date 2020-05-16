// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.app;

class TwilightCalculator
{
    private static TwilightCalculator sInstance;
    public int state;
    public long sunrise;
    public long sunset;
    
    static TwilightCalculator getInstance() {
        if (TwilightCalculator.sInstance == null) {
            TwilightCalculator.sInstance = new TwilightCalculator();
        }
        return TwilightCalculator.sInstance;
    }
    
    public void calculateTwilight(final long n, double a, double n2) {
        final float n3 = (n - 946728000000L) / 8.64E7f;
        final float n4 = 0.01720197f * n3 + 6.24006f;
        final double n5 = n4;
        final double a2 = Math.sin(n5) * 0.03341960161924362 + n5 + Math.sin(2.0f * n4) * 3.4906598739326E-4 + Math.sin(n4 * 3.0f) * 5.236000106378924E-6 + 1.796593063 + 3.141592653589793;
        n2 = -n2 / 360.0;
        n2 = Math.round(n3 - 9.0E-4f - n2) + 9.0E-4f + n2 + Math.sin(n5) * 0.0053 + Math.sin(2.0 * a2) * -0.0069;
        final double asin = Math.asin(Math.sin(a2) * Math.sin(0.4092797040939331));
        a *= 0.01745329238474369;
        a = (Math.sin(-0.10471975803375244) - Math.sin(a) * Math.sin(asin)) / (Math.cos(a) * Math.cos(asin));
        if (a >= 1.0) {
            this.state = 1;
            this.sunset = -1L;
            this.sunrise = -1L;
            return;
        }
        if (a <= -1.0) {
            this.state = 0;
            this.sunset = -1L;
            this.sunrise = -1L;
            return;
        }
        a = (float)(Math.acos(a) / 6.283185307179586);
        this.sunset = Math.round((n2 + a) * 8.64E7) + 946728000000L;
        final long sunrise = Math.round((n2 - a) * 8.64E7) + 946728000000L;
        this.sunrise = sunrise;
        if (sunrise < n && this.sunset > n) {
            this.state = 0;
        }
        else {
            this.state = 1;
        }
    }
}
