// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.smartspace;

import android.app.PendingIntent;
import com.android.systemui.R$string;
import com.android.systemui.R$plurals;
import android.util.Log;
import com.android.systemui.R$dimen;
import android.graphics.BitmapFactory$Options;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import com.android.systemui.smartspace.nano.SmartspaceProto$CardWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.content.Context;
import com.android.systemui.smartspace.nano.SmartspaceProto$SmartspaceUpdate;

public class SmartSpaceCard
{
    private static int sRequestCode;
    private final SmartspaceProto$SmartspaceUpdate.SmartspaceCard mCard;
    private final Context mContext;
    private Bitmap mIcon;
    private boolean mIconProcessed;
    private final Intent mIntent;
    private final long mPublishTime;
    private int mRequestCode;
    
    public SmartSpaceCard(final Context context, final SmartspaceProto$SmartspaceUpdate.SmartspaceCard mCard, final Intent mIntent, final boolean b, final Bitmap mIcon, final boolean b2, final long mPublishTime) {
        this.mContext = context.getApplicationContext();
        this.mCard = mCard;
        this.mIntent = mIntent;
        this.mIcon = mIcon;
        this.mPublishTime = mPublishTime;
        if (++SmartSpaceCard.sRequestCode > 2147483646) {
            SmartSpaceCard.sRequestCode = 0;
        }
        this.mRequestCode = SmartSpaceCard.sRequestCode;
    }
    
    static SmartSpaceCard fromWrapper(final Context context, final SmartspaceProto$CardWrapper smartspaceProto$CardWrapper, final boolean b) {
        if (smartspaceProto$CardWrapper == null) {
            return null;
        }
        try {
            Intent uri;
            if (smartspaceProto$CardWrapper.card.tapAction != null && !TextUtils.isEmpty((CharSequence)smartspaceProto$CardWrapper.card.tapAction.intent)) {
                uri = Intent.parseUri(smartspaceProto$CardWrapper.card.tapAction.intent, 0);
            }
            else {
                uri = null;
            }
            Bitmap decodeByteArray;
            if (smartspaceProto$CardWrapper.icon != null) {
                decodeByteArray = BitmapFactory.decodeByteArray(smartspaceProto$CardWrapper.icon, 0, smartspaceProto$CardWrapper.icon.length, (BitmapFactory$Options)null);
            }
            else {
                decodeByteArray = null;
            }
            final int dimensionPixelSize = context.getResources().getDimensionPixelSize(R$dimen.header_icon_size);
            Bitmap scaledBitmap = decodeByteArray;
            if (decodeByteArray != null) {
                scaledBitmap = decodeByteArray;
                if (decodeByteArray.getHeight() > dimensionPixelSize) {
                    scaledBitmap = Bitmap.createScaledBitmap(decodeByteArray, (int)(decodeByteArray.getWidth() * (dimensionPixelSize / (float)decodeByteArray.getHeight())), dimensionPixelSize, true);
                }
            }
            return new SmartSpaceCard(context, smartspaceProto$CardWrapper.card, uri, b, scaledBitmap, smartspaceProto$CardWrapper.isIconGrayscale, smartspaceProto$CardWrapper.publishTime);
        }
        catch (Exception ex) {
            Log.e("SmartspaceCard", "from proto", (Throwable)ex);
            return null;
        }
    }
    
    private String getDurationText(final SmartspaceProto$SmartspaceUpdate.SmartspaceCard.Message.FormattedText.FormatParam formatParam) {
        final int minutesToEvent = this.getMinutesToEvent(formatParam);
        String s;
        if (minutesToEvent >= 60) {
            final int i = minutesToEvent / 60;
            final int j = minutesToEvent % 60;
            s = this.mContext.getResources().getQuantityString(R$plurals.smartspace_hours, i, new Object[] { i });
            if (j > 0) {
                s = this.mContext.getString(R$string.smartspace_hours_mins, new Object[] { s, this.mContext.getResources().getQuantityString(R$plurals.smartspace_minutes, j, new Object[] { j }) });
            }
        }
        else {
            s = this.mContext.getResources().getQuantityString(R$plurals.smartspace_minutes, minutesToEvent, new Object[] { minutesToEvent });
        }
        return s;
    }
    
    private SmartspaceProto$SmartspaceUpdate.SmartspaceCard.Message.FormattedText getFormattedText(final boolean b) {
        final SmartspaceProto$SmartspaceUpdate.SmartspaceCard.Message message = this.getMessage();
        if (message != null) {
            SmartspaceProto$SmartspaceUpdate.SmartspaceCard.Message.FormattedText formattedText;
            if (b) {
                formattedText = message.title;
            }
            else {
                formattedText = message.subtitle;
            }
            return formattedText;
        }
        return null;
    }
    
    private SmartspaceProto$SmartspaceUpdate.SmartspaceCard.Message getMessage() {
        final long currentTimeMillis = System.currentTimeMillis();
        final SmartspaceProto$SmartspaceUpdate.SmartspaceCard mCard = this.mCard;
        final long eventTimeMillis = mCard.eventTimeMillis;
        final long eventDurationMillis = mCard.eventDurationMillis;
        if (currentTimeMillis < eventTimeMillis) {
            final SmartspaceProto$SmartspaceUpdate.SmartspaceCard.Message preEvent = mCard.preEvent;
            if (preEvent != null) {
                return preEvent;
            }
        }
        if (currentTimeMillis > eventDurationMillis + eventTimeMillis) {
            final SmartspaceProto$SmartspaceUpdate.SmartspaceCard.Message postEvent = this.mCard.postEvent;
            if (postEvent != null) {
                return postEvent;
            }
        }
        final SmartspaceProto$SmartspaceUpdate.SmartspaceCard.Message duringEvent = this.mCard.duringEvent;
        if (duringEvent != null) {
            return duringEvent;
        }
        return null;
    }
    
    private int getMinutesToEvent(final SmartspaceProto$SmartspaceUpdate.SmartspaceCard.Message.FormattedText.FormatParam formatParam) {
        return (int)Math.ceil(this.getMillisToEvent(formatParam) / 60000.0);
    }
    
    private String[] getTextArgs(final SmartspaceProto$SmartspaceUpdate.SmartspaceCard.Message.FormattedText.FormatParam[] array, final String s) {
        final int length = array.length;
        final String[] array2 = new String[length];
        for (int i = 0; i < length; ++i) {
            final int formatParamArgs = array[i].formatParamArgs;
            if (formatParamArgs != 1 && formatParamArgs != 2) {
                String text = "";
                if (formatParamArgs != 3) {
                    array2[i] = "";
                }
                else if (s != null && array[i].truncateLocation != 0) {
                    array2[i] = s;
                }
                else {
                    if (array[i].text != null) {
                        text = array[i].text;
                    }
                    array2[i] = text;
                }
            }
            else {
                array2[i] = this.getDurationText(array[i]);
            }
        }
        return array2;
    }
    
    private boolean hasParams(final SmartspaceProto$SmartspaceUpdate.SmartspaceCard.Message.FormattedText formattedText) {
        if (formattedText != null && formattedText.text != null) {
            final SmartspaceProto$SmartspaceUpdate.SmartspaceCard.Message.FormattedText.FormatParam[] formatParam = formattedText.formatParam;
            if (formatParam != null && formatParam.length > 0) {
                return true;
            }
        }
        return false;
    }
    
    private String substitute(final boolean b) {
        return this.substitute(b, null);
    }
    
    private String substitute(final boolean b, final String s) {
        final SmartspaceProto$SmartspaceUpdate.SmartspaceCard.Message.FormattedText formattedText = this.getFormattedText(b);
        if (formattedText != null) {
            final String text = formattedText.text;
            if (text != null) {
                if (this.hasParams(formattedText)) {
                    return String.format(text, (Object[])this.getTextArgs(formattedText.formatParam, s));
                }
                return text;
            }
        }
        return "";
    }
    
    public long getExpiration() {
        final SmartspaceProto$SmartspaceUpdate.SmartspaceCard mCard = this.mCard;
        if (mCard != null) {
            final SmartspaceProto$SmartspaceUpdate.SmartspaceCard.ExpiryCriteria expiryCriteria = mCard.expiryCriteria;
            if (expiryCriteria != null) {
                return expiryCriteria.expirationTimeMillis;
            }
        }
        return 0L;
    }
    
    public CharSequence getFormattedTitle() {
        final SmartspaceProto$SmartspaceUpdate.SmartspaceCard.Message message = this.getMessage();
        if (message == null) {
            return "";
        }
        final SmartspaceProto$SmartspaceUpdate.SmartspaceCard.Message.FormattedText title = message.title;
        if (title != null) {
            final String text = title.text;
            if (text != null) {
                if (!this.hasParams(title)) {
                    return text;
                }
                String s = null;
                String s2 = null;
                int n = 0;
                SmartspaceProto$SmartspaceUpdate.SmartspaceCard.Message.FormattedText.FormatParam[] formatParam;
                while (true) {
                    formatParam = title.formatParam;
                    if (n >= formatParam.length) {
                        break;
                    }
                    final SmartspaceProto$SmartspaceUpdate.SmartspaceCard.Message.FormattedText.FormatParam formatParam2 = formatParam[n];
                    String text2 = s;
                    String durationText = s2;
                    if (formatParam2 != null) {
                        final int formatParamArgs = formatParam2.formatParamArgs;
                        if (formatParamArgs != 1 && formatParamArgs != 2) {
                            if (formatParamArgs != 3) {
                                text2 = s;
                                durationText = s2;
                            }
                            else {
                                text2 = formatParam2.text;
                                durationText = s2;
                            }
                        }
                        else {
                            durationText = this.getDurationText(formatParam2);
                            text2 = s;
                        }
                    }
                    ++n;
                    s = text2;
                    s2 = durationText;
                }
                String text3 = s;
                String text4 = s2;
                if (this.mCard.cardType == 3) {
                    text3 = s;
                    text4 = s2;
                    if (formatParam.length == 2) {
                        text4 = formatParam[0].text;
                        text3 = formatParam[1].text;
                    }
                }
                if (text3 == null) {
                    return "";
                }
                String string;
                if ((string = text4) == null) {
                    if (message != this.mCard.duringEvent) {
                        return text;
                    }
                    string = this.mContext.getString(R$string.smartspace_now);
                }
                return this.mContext.getString(R$string.smartspace_pill_text_format, new Object[] { string, text3 });
            }
        }
        return "";
    }
    
    public Bitmap getIcon() {
        return this.mIcon;
    }
    
    public Intent getIntent() {
        return this.mIntent;
    }
    
    long getMillisToEvent(final SmartspaceProto$SmartspaceUpdate.SmartspaceCard.Message.FormattedText.FormatParam formatParam) {
        long eventTimeMillis;
        if (formatParam.formatParamArgs == 2) {
            final SmartspaceProto$SmartspaceUpdate.SmartspaceCard mCard = this.mCard;
            eventTimeMillis = mCard.eventTimeMillis + mCard.eventDurationMillis;
        }
        else {
            eventTimeMillis = this.mCard.eventTimeMillis;
        }
        return Math.abs(System.currentTimeMillis() - eventTimeMillis);
    }
    
    public PendingIntent getPendingIntent() {
        if (this.mCard.tapAction == null) {
            return null;
        }
        final Intent intent = new Intent(this.getIntent());
        final int actionType = this.mCard.tapAction.actionType;
        if (actionType == 1) {
            intent.addFlags(268435456);
            intent.setPackage("com.google.android.googlequicksearchbox");
            return PendingIntent.getBroadcast(this.mContext, this.mRequestCode, intent, 0);
        }
        if (actionType != 2) {
            return null;
        }
        return PendingIntent.getActivity(this.mContext, this.mRequestCode, intent, 0);
    }
    
    public String getSubtitle() {
        return this.substitute(false);
    }
    
    public String getTitle() {
        return this.substitute(true);
    }
    
    public boolean isExpired() {
        return System.currentTimeMillis() > this.getExpiration();
    }
    
    public boolean isIconProcessed() {
        return this.mIconProcessed;
    }
    
    public boolean isSensitive() {
        return this.mCard.isSensitive;
    }
    
    public boolean isWorkProfile() {
        return this.mCard.isWorkProfile;
    }
    
    public void setIcon(final Bitmap mIcon) {
        this.mIcon = mIcon;
    }
    
    public void setIconProcessed(final boolean mIconProcessed) {
        this.mIconProcessed = mIconProcessed;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("title:");
        sb.append(this.getTitle());
        sb.append(" subtitle:");
        sb.append(this.getSubtitle());
        sb.append(" expires:");
        sb.append(this.getExpiration());
        sb.append(" published:");
        sb.append(this.mPublishTime);
        return sb.toString();
    }
}
