// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice;

import android.graphics.Color;
import android.text.format.DateUtils;
import java.util.Calendar;
import android.app.RemoteInput;
import java.util.Arrays;
import android.os.Handler;
import android.app.PendingIntent$OnFinished;
import android.app.PendingIntent$CanceledException;
import android.content.Intent;
import android.content.Context;
import android.os.Parcelable;
import android.text.SpannableString;
import androidx.core.graphics.drawable.IconCompat;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.AlignmentSpan;
import java.util.List;
import android.os.Bundle;
import androidx.core.util.Pair;
import android.app.PendingIntent;
import androidx.versionedparcelable.CustomVersionedParcelable;

public final class SliceItem extends CustomVersionedParcelable
{
    String mFormat;
    protected String[] mHints;
    SliceItemHolder mHolder;
    Object mObj;
    CharSequence mSanitizedText;
    String mSubType;
    
    public SliceItem() {
        this.mHints = Slice.NO_HINTS;
        this.mFormat = "text";
        this.mSubType = null;
    }
    
    public SliceItem(final PendingIntent pendingIntent, final Slice slice, final String s, final String s2, final String[] array) {
        this(new Pair(pendingIntent, slice), s, s2, array);
    }
    
    public SliceItem(final Bundle bundle) {
        this.mHints = Slice.NO_HINTS;
        this.mFormat = "text";
        this.mSubType = null;
        this.mHints = bundle.getStringArray("hints");
        this.mFormat = bundle.getString("format");
        this.mSubType = bundle.getString("subtype");
        this.mObj = readObj(this.mFormat, bundle);
    }
    
    public SliceItem(final Object o, final String s, final String s2, final List<String> list) {
        this(o, s, s2, list.toArray(new String[list.size()]));
    }
    
    public SliceItem(final Object mObj, final String mFormat, final String mSubType, final String[] mHints) {
        this.mHints = Slice.NO_HINTS;
        this.mFormat = "text";
        this.mSubType = null;
        this.mHints = mHints;
        this.mFormat = mFormat;
        this.mSubType = mSubType;
        this.mObj = mObj;
    }
    
    private static boolean checkSpan(final Object o) {
        return o instanceof AlignmentSpan || o instanceof ForegroundColorSpan || o instanceof RelativeSizeSpan || o instanceof StyleSpan;
    }
    
    private static boolean checkSpannedText(final Spanned spanned) {
        final Object[] spans = spanned.getSpans(0, spanned.length(), (Class)Object.class);
        for (int length = spans.length, i = 0; i < length; ++i) {
            if (!checkSpan(spans[i])) {
                return false;
            }
        }
        return true;
    }
    
    private static Object fixSpan(Object o) {
        if (!checkSpan(o)) {
            o = null;
        }
        return o;
    }
    
    private static void fixSpannableText(final Spannable spannable) {
        final int length = spannable.length();
        int i = 0;
        for (Object[] spans = spannable.getSpans(0, length, (Class)Object.class); i < spans.length; ++i) {
            final Object o = spans[i];
            final Object fixSpan = fixSpan(o);
            if (fixSpan != o) {
                if (fixSpan != null) {
                    spannable.setSpan(fixSpan, spannable.getSpanStart(o), spannable.getSpanEnd(o), spannable.getSpanFlags(o));
                }
                spannable.removeSpan(o);
            }
        }
    }
    
    private static String layoutDirectionToString(final int i) {
        if (i == 0) {
            return "LTR";
        }
        if (i == 1) {
            return "RTL";
        }
        if (i == 2) {
            return "INHERIT";
        }
        if (i != 3) {
            return Integer.toString(i);
        }
        return "LOCALE";
    }
    
    private static Object readObj(final String str, final Bundle bundle) {
        int n = 0;
        Label_0176: {
            switch (str.hashCode()) {
                case 109526418: {
                    if (str.equals("slice")) {
                        n = 2;
                        break Label_0176;
                    }
                    break;
                }
                case 100358090: {
                    if (str.equals("input")) {
                        n = 1;
                        break Label_0176;
                    }
                    break;
                }
                case 100313435: {
                    if (str.equals("image")) {
                        n = 0;
                        break Label_0176;
                    }
                    break;
                }
                case 3556653: {
                    if (str.equals("text")) {
                        n = 3;
                        break Label_0176;
                    }
                    break;
                }
                case 3327612: {
                    if (str.equals("long")) {
                        n = 6;
                        break Label_0176;
                    }
                    break;
                }
                case 104431: {
                    if (str.equals("int")) {
                        n = 5;
                        break Label_0176;
                    }
                    break;
                }
                case -1422950858: {
                    if (str.equals("action")) {
                        n = 4;
                        break Label_0176;
                    }
                    break;
                }
            }
            n = -1;
        }
        switch (n) {
            default: {
                final StringBuilder sb = new StringBuilder();
                sb.append("Unsupported type ");
                sb.append(str);
                throw new RuntimeException(sb.toString());
            }
            case 6: {
                return bundle.getLong("obj");
            }
            case 5: {
                return bundle.getInt("obj");
            }
            case 4: {
                return new Pair(bundle.getParcelable("obj"), new Slice(bundle.getBundle("obj_2")));
            }
            case 3: {
                return bundle.getCharSequence("obj");
            }
            case 2: {
                return new Slice(bundle.getBundle("obj"));
            }
            case 1: {
                return bundle.getParcelable("obj");
            }
            case 0: {
                return IconCompat.createFromBundle(bundle.getBundle("obj"));
            }
        }
    }
    
    private static CharSequence sanitizeText(final CharSequence charSequence) {
        if (charSequence instanceof Spannable) {
            fixSpannableText((Spannable)charSequence);
            return charSequence;
        }
        if (!(charSequence instanceof Spanned)) {
            return charSequence;
        }
        if (checkSpannedText((Spanned)charSequence)) {
            return charSequence;
        }
        final SpannableString spannableString = new SpannableString(charSequence);
        fixSpannableText((Spannable)spannableString);
        return (CharSequence)spannableString;
    }
    
    public static String typeToString(final String str) {
        int n = 0;
        Label_0176: {
            switch (str.hashCode()) {
                case 109526418: {
                    if (str.equals("slice")) {
                        n = 0;
                        break Label_0176;
                    }
                    break;
                }
                case 100358090: {
                    if (str.equals("input")) {
                        n = 6;
                        break Label_0176;
                    }
                    break;
                }
                case 100313435: {
                    if (str.equals("image")) {
                        n = 2;
                        break Label_0176;
                    }
                    break;
                }
                case 3556653: {
                    if (str.equals("text")) {
                        n = 1;
                        break Label_0176;
                    }
                    break;
                }
                case 3327612: {
                    if (str.equals("long")) {
                        n = 5;
                        break Label_0176;
                    }
                    break;
                }
                case 104431: {
                    if (str.equals("int")) {
                        n = 4;
                        break Label_0176;
                    }
                    break;
                }
                case -1422950858: {
                    if (str.equals("action")) {
                        n = 3;
                        break Label_0176;
                    }
                    break;
                }
            }
            n = -1;
        }
        switch (n) {
            default: {
                final StringBuilder sb = new StringBuilder();
                sb.append("Unrecognized format: ");
                sb.append(str);
                return sb.toString();
            }
            case 6: {
                return "RemoteInput";
            }
            case 5: {
                return "Long";
            }
            case 4: {
                return "Int";
            }
            case 3: {
                return "Action";
            }
            case 2: {
                return "Image";
            }
            case 1: {
                return "Text";
            }
            case 0: {
                return "Slice";
            }
        }
    }
    
    private void writeObj(final Bundle bundle, final Object o, final String s) {
        int n = 0;
        Label_0184: {
            switch (s.hashCode()) {
                case 109526418: {
                    if (s.equals("slice")) {
                        n = 2;
                        break Label_0184;
                    }
                    break;
                }
                case 100358090: {
                    if (s.equals("input")) {
                        n = 1;
                        break Label_0184;
                    }
                    break;
                }
                case 100313435: {
                    if (s.equals("image")) {
                        n = 0;
                        break Label_0184;
                    }
                    break;
                }
                case 3556653: {
                    if (s.equals("text")) {
                        n = 4;
                        break Label_0184;
                    }
                    break;
                }
                case 3327612: {
                    if (s.equals("long")) {
                        n = 6;
                        break Label_0184;
                    }
                    break;
                }
                case 104431: {
                    if (s.equals("int")) {
                        n = 5;
                        break Label_0184;
                    }
                    break;
                }
                case -1422950858: {
                    if (s.equals("action")) {
                        n = 3;
                        break Label_0184;
                    }
                    break;
                }
            }
            n = -1;
        }
        switch (n) {
            case 6: {
                bundle.putLong("obj", (long)this.mObj);
                break;
            }
            case 5: {
                bundle.putInt("obj", (int)this.mObj);
                break;
            }
            case 4: {
                bundle.putCharSequence("obj", (CharSequence)o);
                break;
            }
            case 3: {
                final Pair pair = (Pair)o;
                bundle.putParcelable("obj", (Parcelable)pair.first);
                bundle.putBundle("obj_2", ((Slice)pair.second).toBundle());
                break;
            }
            case 2: {
                bundle.putParcelable("obj", (Parcelable)((Slice)o).toBundle());
                break;
            }
            case 1: {
                bundle.putParcelable("obj", (Parcelable)o);
                break;
            }
            case 0: {
                bundle.putBundle("obj", ((IconCompat)o).toBundle());
                break;
            }
        }
    }
    
    public void addHint(final String s) {
        this.mHints = ArrayUtils.appendElement(String.class, this.mHints, s);
    }
    
    public void fireAction(final Context context, final Intent intent) throws PendingIntent$CanceledException {
        this.fireActionInternal(context, intent);
    }
    
    public boolean fireActionInternal(final Context context, final Intent intent) throws PendingIntent$CanceledException {
        final F first = ((Pair)this.mObj).first;
        if (first instanceof PendingIntent) {
            ((PendingIntent)first).send(context, 0, intent, (PendingIntent$OnFinished)null, (Handler)null);
            return false;
        }
        ((ActionHandler)first).onAction(this, context, intent);
        return true;
    }
    
    public PendingIntent getAction() {
        final F first = ((Pair)this.mObj).first;
        if (first instanceof PendingIntent) {
            return (PendingIntent)first;
        }
        return null;
    }
    
    public String getFormat() {
        return this.mFormat;
    }
    
    public List<String> getHints() {
        return Arrays.asList(this.mHints);
    }
    
    public IconCompat getIcon() {
        return (IconCompat)this.mObj;
    }
    
    public int getInt() {
        return (int)this.mObj;
    }
    
    public long getLong() {
        return (long)this.mObj;
    }
    
    public RemoteInput getRemoteInput() {
        return (RemoteInput)this.mObj;
    }
    
    public CharSequence getSanitizedText() {
        if (this.mSanitizedText == null) {
            this.mSanitizedText = sanitizeText(this.getText());
        }
        return this.mSanitizedText;
    }
    
    public Slice getSlice() {
        if ("action".equals(this.getFormat())) {
            return (Slice)((Pair)this.mObj).second;
        }
        return (Slice)this.mObj;
    }
    
    public String getSubType() {
        return this.mSubType;
    }
    
    public CharSequence getText() {
        return (CharSequence)this.mObj;
    }
    
    public boolean hasAnyHints(final String... array) {
        if (array == null) {
            return false;
        }
        for (int length = array.length, i = 0; i < length; ++i) {
            if (ArrayUtils.contains(this.mHints, array[i])) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasHint(final String s) {
        return ArrayUtils.contains(this.mHints, s);
    }
    
    public void onPostParceling() {
        final SliceItemHolder mHolder = this.mHolder;
        if (mHolder != null) {
            this.mObj = mHolder.getObj(this.mFormat);
            this.mHolder.release();
        }
        else {
            this.mObj = null;
        }
        this.mHolder = null;
    }
    
    public void onPreParceling(final boolean b) {
        this.mHolder = new SliceItemHolder(this.mFormat, this.mObj, b);
    }
    
    public Bundle toBundle() {
        final Bundle bundle = new Bundle();
        bundle.putStringArray("hints", this.mHints);
        bundle.putString("format", this.mFormat);
        bundle.putString("subtype", this.mSubType);
        this.writeObj(bundle, this.mObj, this.mFormat);
        return bundle;
    }
    
    @Override
    public String toString() {
        return this.toString("");
    }
    
    public String toString(final String s) {
        final StringBuilder sb = new StringBuilder();
        sb.append(s);
        sb.append(this.getFormat());
        if (this.getSubType() != null) {
            sb.append('<');
            sb.append(this.getSubType());
            sb.append('>');
        }
        sb.append(' ');
        final String[] mHints = this.mHints;
        if (mHints.length > 0) {
            Slice.appendHints(sb, mHints);
            sb.append(' ');
        }
        final StringBuilder sb2 = new StringBuilder();
        sb2.append(s);
        sb2.append("  ");
        final String string = sb2.toString();
        final String format = this.getFormat();
        int n = -1;
        switch (format.hashCode()) {
            case 109526418: {
                if (format.equals("slice")) {
                    n = 0;
                    break;
                }
                break;
            }
            case 100313435: {
                if (format.equals("image")) {
                    n = 3;
                    break;
                }
                break;
            }
            case 3556653: {
                if (format.equals("text")) {
                    n = 2;
                    break;
                }
                break;
            }
            case 3327612: {
                if (format.equals("long")) {
                    n = 5;
                    break;
                }
                break;
            }
            case 104431: {
                if (format.equals("int")) {
                    n = 4;
                    break;
                }
                break;
            }
            case -1422950858: {
                if (format.equals("action")) {
                    n = 1;
                    break;
                }
                break;
            }
        }
        if (n != 0) {
            if (n != 1) {
                if (n != 2) {
                    if (n != 3) {
                        if (n != 4) {
                            if (n != 5) {
                                sb.append(typeToString(this.getFormat()));
                            }
                            else if ("millis".equals(this.getSubType())) {
                                if (this.getLong() == -1L) {
                                    sb.append("INFINITY");
                                }
                                else {
                                    sb.append(DateUtils.getRelativeTimeSpanString(this.getLong(), Calendar.getInstance().getTimeInMillis(), 1000L, 262144));
                                }
                            }
                            else {
                                sb.append(this.getLong());
                                sb.append('L');
                            }
                        }
                        else if ("color".equals(this.getSubType())) {
                            final int int1 = this.getInt();
                            sb.append(String.format("a=0x%02x r=0x%02x g=0x%02x b=0x%02x", Color.alpha(int1), Color.red(int1), Color.green(int1), Color.blue(int1)));
                        }
                        else if ("layout_direction".equals(this.getSubType())) {
                            sb.append(layoutDirectionToString(this.getInt()));
                        }
                        else {
                            sb.append(this.getInt());
                        }
                    }
                    else {
                        sb.append(this.getIcon());
                    }
                }
                else {
                    sb.append('\"');
                    sb.append(this.getText());
                    sb.append('\"');
                }
            }
            else {
                final F first = ((Pair)this.mObj).first;
                sb.append('[');
                sb.append(first);
                sb.append("] ");
                sb.append("{\n");
                sb.append(this.getSlice().toString(string));
                sb.append('\n');
                sb.append(s);
                sb.append('}');
            }
        }
        else {
            sb.append("{\n");
            sb.append(this.getSlice().toString(string));
            sb.append('\n');
            sb.append(s);
            sb.append('}');
        }
        sb.append("\n");
        return sb.toString();
    }
    
    public interface ActionHandler
    {
        void onAction(final SliceItem p0, final Context p1, final Intent p2);
    }
}
