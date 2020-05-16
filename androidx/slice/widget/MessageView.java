// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice.widget;

import java.util.Iterator;
import android.graphics.drawable.Drawable;
import androidx.slice.SliceItem;
import android.text.SpannableStringBuilder;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.Bitmap$Config;
import android.util.TypedValue;
import androidx.slice.core.SliceQuery;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageView extends SliceChildView
{
    private TextView mDetails;
    private ImageView mIcon;
    
    public MessageView(final Context context) {
        super(context);
    }
    
    @Override
    public int getMode() {
        return 2;
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mDetails = (TextView)this.findViewById(16908304);
        this.mIcon = (ImageView)this.findViewById(16908294);
    }
    
    @Override
    public void resetView() {
    }
    
    @Override
    public void setSliceItem(final SliceContent sliceContent, final boolean b, int n, final int n2, final SliceView.OnSliceActionListener sliceActionListener) {
        final SliceItem sliceItem = sliceContent.getSliceItem();
        this.setSliceActionListener(sliceActionListener);
        final SliceItem subtype = SliceQuery.findSubtype(sliceItem, "image", "source");
        if (subtype != null && subtype.getIcon() != null) {
            final Drawable loadDrawable = subtype.getIcon().loadDrawable(this.getContext());
            if (loadDrawable != null) {
                n = (int)TypedValue.applyDimension(1, 24.0f, this.getContext().getResources().getDisplayMetrics());
                final Bitmap bitmap = Bitmap.createBitmap(n, n, Bitmap$Config.ARGB_8888);
                final Canvas canvas = new Canvas(bitmap);
                loadDrawable.setBounds(0, 0, n, n);
                loadDrawable.draw(canvas);
                this.mIcon.setImageBitmap(SliceViewUtil.getCircularBitmap(bitmap));
            }
        }
        final SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        for (final SliceItem sliceItem2 : SliceQuery.findAll(sliceItem, "text")) {
            if (spannableStringBuilder.length() != 0) {
                spannableStringBuilder.append('\n');
            }
            spannableStringBuilder.append(sliceItem2.getSanitizedText());
        }
        this.mDetails.setText((CharSequence)spannableStringBuilder.toString());
    }
}
