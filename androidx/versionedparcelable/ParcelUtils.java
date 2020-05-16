// 
// Decompiled by Procyon v0.5.36
// 

package androidx.versionedparcelable;

import android.os.Bundle;
import android.os.Parcelable;

public class ParcelUtils
{
    private ParcelUtils() {
    }
    
    public static <T extends VersionedParcelable> T fromParcelable(final Parcelable parcelable) {
        if (parcelable instanceof ParcelImpl) {
            return ((ParcelImpl)parcelable).getVersionedParcel();
        }
        throw new IllegalArgumentException("Invalid parcel");
    }
    
    public static <T extends VersionedParcelable> T getVersionedParcelable(Bundle bundle, final String s) {
        try {
            bundle = (Bundle)bundle.getParcelable(s);
            if (bundle == null) {
                return null;
            }
            bundle.setClassLoader(ParcelUtils.class.getClassLoader());
            return fromParcelable(bundle.getParcelable("a"));
        }
        catch (RuntimeException ex) {
            return null;
        }
    }
    
    public static Parcelable toParcelable(final VersionedParcelable versionedParcelable) {
        return (Parcelable)new ParcelImpl(versionedParcelable);
    }
}
