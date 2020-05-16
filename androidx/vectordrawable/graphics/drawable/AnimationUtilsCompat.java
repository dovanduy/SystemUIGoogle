// 
// Decompiled by Procyon v0.5.36
// 

package androidx.vectordrawable.graphics.drawable;

import android.content.res.XmlResourceParser;
import android.content.res.Resources$NotFoundException;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;
import android.view.animation.AnimationUtils;
import android.os.Build$VERSION;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
import android.util.AttributeSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.util.Xml;
import android.view.animation.Interpolator;
import org.xmlpull.v1.XmlPullParser;
import android.content.Context;

public class AnimationUtilsCompat
{
    private static Interpolator createInterpolatorFromXml(final Context context, final XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        final int depth = xmlPullParser.getDepth();
        Object o = null;
        while (true) {
            final int next = xmlPullParser.next();
            if ((next == 3 && xmlPullParser.getDepth() <= depth) || next == 1) {
                return (Interpolator)o;
            }
            if (next != 2) {
                continue;
            }
            final AttributeSet attributeSet = Xml.asAttributeSet(xmlPullParser);
            final String name = xmlPullParser.getName();
            if (name.equals("linearInterpolator")) {
                o = new LinearInterpolator();
            }
            else if (name.equals("accelerateInterpolator")) {
                o = new AccelerateInterpolator(context, attributeSet);
            }
            else if (name.equals("decelerateInterpolator")) {
                o = new DecelerateInterpolator(context, attributeSet);
            }
            else if (name.equals("accelerateDecelerateInterpolator")) {
                o = new AccelerateDecelerateInterpolator();
            }
            else if (name.equals("cycleInterpolator")) {
                o = new CycleInterpolator(context, attributeSet);
            }
            else if (name.equals("anticipateInterpolator")) {
                o = new AnticipateInterpolator(context, attributeSet);
            }
            else if (name.equals("overshootInterpolator")) {
                o = new OvershootInterpolator(context, attributeSet);
            }
            else if (name.equals("anticipateOvershootInterpolator")) {
                o = new AnticipateOvershootInterpolator(context, attributeSet);
            }
            else if (name.equals("bounceInterpolator")) {
                o = new BounceInterpolator();
            }
            else {
                if (!name.equals("pathInterpolator")) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Unknown interpolator name: ");
                    sb.append(xmlPullParser.getName());
                    throw new RuntimeException(sb.toString());
                }
                o = new PathInterpolatorCompat(context, attributeSet, xmlPullParser);
            }
        }
    }
    
    public static Interpolator loadInterpolator(final Context context, final int i) throws Resources$NotFoundException {
        if (Build$VERSION.SDK_INT >= 21) {
            return AnimationUtils.loadInterpolator(context, i);
        }
        final XmlResourceParser xmlResourceParser = null;
        Label_0047: {
            if (i != 17563663) {
                break Label_0047;
            }
            try {
                try {
                    return (Interpolator)new FastOutLinearInInterpolator();
                }
                finally {
                    if (xmlResourceParser != null) {
                        xmlResourceParser.close();
                    }
                    // iftrue(Label_0061:, i != 17563661)
                    return (Interpolator)new FastOutSlowInInterpolator();
                    Label_0114: {
                        return;
                    }
                    Label_0075:
                    final XmlResourceParser animation = context.getResources().getAnimation(i);
                    final Interpolator interpolatorFromXml = createInterpolatorFromXml(context, (XmlPullParser)animation);
                    // iftrue(Label_0114:, animation == null)
                    animation.close();
                    return interpolatorFromXml;
                    Label_0061:
                    // iftrue(Label_0075:, i != 17563662)
                    return (Interpolator)new LinearOutSlowInInterpolator();
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Can't load animation resource ID #0x");
                    sb.append(Integer.toHexString(i));
                    final Resources$NotFoundException ex = new Resources$NotFoundException(sb.toString());
                    final IOException ex2;
                    ex.initCause((Throwable)ex2);
                    throw ex;
                }
            }
            catch (IOException ex3) {}
            catch (XmlPullParserException ex4) {}
        }
    }
}
