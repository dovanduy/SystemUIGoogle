// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.keyguard;

import android.graphics.Canvas;
import android.graphics.Bitmap$Config;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter$Blur;
import com.android.systemui.R$dimen;
import java.lang.ref.WeakReference;
import android.os.AsyncTask;
import com.android.systemui.SystemUIFactory;
import com.google.android.systemui.dagger.SystemUIGoogleRootComponent;
import android.app.PendingIntent;
import androidx.slice.builders.SliceAction;
import android.text.TextUtils;
import android.os.Trace;
import androidx.slice.Slice;
import android.graphics.Bitmap;
import com.google.android.systemui.smartspace.SmartSpaceCard;
import android.graphics.PorterDuff$Mode;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.builders.ListBuilder;
import android.util.Log;
import com.google.android.systemui.smartspace.SmartSpaceData;
import com.google.android.systemui.smartspace.SmartSpaceController;
import android.net.Uri;
import com.google.android.systemui.smartspace.SmartSpaceUpdateListener;
import com.android.systemui.keyguard.KeyguardSliceProvider;

public class KeyguardSliceProviderGoogle extends KeyguardSliceProvider implements SmartSpaceUpdateListener
{
    private static final boolean DEBUG;
    private final Uri mCalendarUri;
    private boolean mHideSensitiveContent;
    private boolean mHideWorkContent;
    public SmartSpaceController mSmartSpaceController;
    private SmartSpaceData mSmartSpaceData;
    private final Uri mWeatherUri;
    
    static {
        DEBUG = Log.isLoggable("KeyguardSliceProvider", 3);
    }
    
    public KeyguardSliceProviderGoogle() {
        this.mHideWorkContent = true;
        this.mWeatherUri = Uri.parse("content://com.android.systemui.keyguard/smartSpace/weather");
        this.mCalendarUri = Uri.parse("content://com.android.systemui.keyguard/smartSpace/calendar");
    }
    
    private void addWeather(final ListBuilder listBuilder) {
        final SmartSpaceCard weatherCard = this.mSmartSpaceData.getWeatherCard();
        if (weatherCard != null && !weatherCard.isExpired()) {
            final ListBuilder.RowBuilder rowBuilder = new ListBuilder.RowBuilder(this.mWeatherUri);
            rowBuilder.setTitle(weatherCard.getTitle());
            final Bitmap icon = weatherCard.getIcon();
            if (icon != null) {
                final IconCompat withBitmap = IconCompat.createWithBitmap(icon);
                withBitmap.setTintMode(PorterDuff$Mode.DST);
                rowBuilder.addEndItem(withBitmap, 1);
            }
            listBuilder.addRow(rowBuilder);
        }
    }
    
    @Override
    public Slice onBindSlice(final Uri uri) {
        Trace.beginSection("KeyguardSliceProviderGoogle#onBindSlice");
        final ListBuilder listBuilder = new ListBuilder(this.getContext(), super.mSliceUri, -1L);
        synchronized (this) {
            final SmartSpaceCard currentCard = this.mSmartSpaceData.getCurrentCard();
            int n2;
            final int n = n2 = 0;
            Label_0154: {
                if (currentCard != null) {
                    n2 = n;
                    if (!currentCard.isExpired()) {
                        n2 = n;
                        if (!TextUtils.isEmpty((CharSequence)currentCard.getTitle())) {
                            final boolean sensitive = currentCard.isSensitive();
                            final boolean b = sensitive && !this.mHideSensitiveContent && !currentCard.isWorkProfile();
                            final boolean b2 = sensitive && !this.mHideWorkContent && currentCard.isWorkProfile();
                            if (sensitive && !b) {
                                n2 = n;
                                if (!b2) {
                                    break Label_0154;
                                }
                            }
                            n2 = 1;
                        }
                    }
                }
            }
            if (n2 != 0) {
                final Bitmap icon = currentCard.getIcon();
                final SliceAction sliceAction = null;
                IconCompat withBitmap;
                if (icon == null) {
                    withBitmap = null;
                }
                else {
                    withBitmap = IconCompat.createWithBitmap(icon);
                }
                final PendingIntent pendingIntent = currentCard.getPendingIntent();
                SliceAction create = sliceAction;
                if (withBitmap != null) {
                    if (pendingIntent == null) {
                        create = sliceAction;
                    }
                    else {
                        create = SliceAction.create(pendingIntent, withBitmap, 1, currentCard.getTitle());
                    }
                }
                final ListBuilder.HeaderBuilder header = new ListBuilder.HeaderBuilder(super.mHeaderUri);
                header.setTitle(currentCard.getFormattedTitle());
                if (create != null) {
                    header.setPrimaryAction(create);
                }
                listBuilder.setHeader(header);
                final String subtitle = currentCard.getSubtitle();
                if (subtitle != null) {
                    final ListBuilder.RowBuilder rowBuilder = new ListBuilder.RowBuilder(this.mCalendarUri);
                    rowBuilder.setTitle(subtitle);
                    if (withBitmap != null) {
                        rowBuilder.addEndItem(withBitmap, 1);
                    }
                    if (create != null) {
                        rowBuilder.setPrimaryAction(create);
                    }
                    listBuilder.addRow(rowBuilder);
                }
                this.addWeather(listBuilder);
                this.addZenModeLocked(listBuilder);
                this.addPrimaryActionLocked(listBuilder);
                Trace.endSection();
                return listBuilder.build();
            }
            if (this.needsMediaLocked()) {
                this.addMediaLocked(listBuilder);
            }
            else {
                final ListBuilder.RowBuilder rowBuilder2 = new ListBuilder.RowBuilder(super.mDateUri);
                rowBuilder2.setTitle(this.getFormattedDateLocked());
                listBuilder.addRow(rowBuilder2);
            }
            this.addWeather(listBuilder);
            this.addNextAlarmLocked(listBuilder);
            this.addZenModeLocked(listBuilder);
            this.addPrimaryActionLocked(listBuilder);
            // monitorexit(this)
            final Slice build = listBuilder.build();
            if (KeyguardSliceProviderGoogle.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Binding slice: ");
                sb.append(build);
                Log.d("KeyguardSliceProvider", sb.toString());
            }
            Trace.endSection();
            return build;
        }
    }
    
    @Override
    public boolean onCreateSliceProvider() {
        final boolean onCreateSliceProvider = super.onCreateSliceProvider();
        ((SystemUIGoogleRootComponent)SystemUIFactory.getInstance().getRootComponent()).inject(this);
        this.mSmartSpaceData = new SmartSpaceData();
        this.mSmartSpaceController.addListener(this);
        return onCreateSliceProvider;
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.mSmartSpaceController.removeListener(this);
    }
    
    @Override
    public void onSensitiveModeChanged(final boolean b, final boolean b2) {
        synchronized (this) {
            final boolean mHideSensitiveContent = this.mHideSensitiveContent;
            final boolean b3 = true;
            int n;
            if (mHideSensitiveContent != b) {
                this.mHideSensitiveContent = b;
                if (KeyguardSliceProviderGoogle.DEBUG) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Public mode changed, hide data: ");
                    sb.append(b);
                    Log.d("KeyguardSliceProvider", sb.toString());
                }
                n = 1;
            }
            else {
                n = 0;
            }
            if (this.mHideWorkContent != b2) {
                this.mHideWorkContent = b2;
                n = (b3 ? 1 : 0);
                if (KeyguardSliceProviderGoogle.DEBUG) {
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append("Public work mode changed, hide data: ");
                    sb2.append(b2);
                    Log.d("KeyguardSliceProvider", sb2.toString());
                    n = (b3 ? 1 : 0);
                }
            }
            // monitorexit(this)
            if (n != 0) {
                this.notifyChange();
            }
        }
    }
    
    @Override
    public void onSmartSpaceUpdated(final SmartSpaceData mSmartSpaceData) {
        synchronized (this) {
            this.mSmartSpaceData = mSmartSpaceData;
            // monitorexit(this)
            final SmartSpaceCard weatherCard = mSmartSpaceData.getWeatherCard();
            if (weatherCard != null && weatherCard.getIcon() != null && !weatherCard.isIconProcessed()) {
                weatherCard.setIconProcessed(true);
                new AddShadowTask(this, weatherCard).execute((Object[])new Bitmap[] { weatherCard.getIcon() });
            }
            else {
                this.notifyChange();
            }
        }
    }
    
    @Override
    protected void updateClockLocked() {
        this.notifyChange();
    }
    
    private static class AddShadowTask extends AsyncTask<Bitmap, Void, Bitmap>
    {
        private final float mBlurRadius;
        private final WeakReference<KeyguardSliceProviderGoogle> mProviderReference;
        private final SmartSpaceCard mWeatherCard;
        
        AddShadowTask(final KeyguardSliceProviderGoogle referent, final SmartSpaceCard mWeatherCard) {
            this.mProviderReference = new WeakReference<KeyguardSliceProviderGoogle>(referent);
            this.mWeatherCard = mWeatherCard;
            this.mBlurRadius = referent.getContext().getResources().getDimension(R$dimen.smartspace_icon_shadow);
        }
        
        private Bitmap applyShadow(final Bitmap bitmap) {
            final BlurMaskFilter maskFilter = new BlurMaskFilter(this.mBlurRadius, BlurMaskFilter$Blur.NORMAL);
            final Paint paint = new Paint();
            paint.setMaskFilter((MaskFilter)maskFilter);
            final int[] array = new int[2];
            final Bitmap alpha = bitmap.extractAlpha(paint, array);
            final Bitmap bitmap2 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap$Config.ARGB_8888);
            final Canvas canvas = new Canvas(bitmap2);
            final Paint paint2 = new Paint();
            paint2.setAlpha(70);
            canvas.drawBitmap(alpha, (float)array[0], array[1] + this.mBlurRadius / 2.0f, paint2);
            alpha.recycle();
            paint2.setAlpha(255);
            canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint2);
            return bitmap2;
        }
        
        protected Bitmap doInBackground(final Bitmap... array) {
            return this.applyShadow(array[0]);
        }
        
        protected void onPostExecute(final Bitmap icon) {
            synchronized (this) {
                this.mWeatherCard.setIcon(icon);
                final KeyguardSliceProviderGoogle keyguardSliceProviderGoogle = this.mProviderReference.get();
                // monitorexit(this)
                if (keyguardSliceProviderGoogle != null) {
                    keyguardSliceProviderGoogle.notifyChange();
                }
            }
        }
    }
}
