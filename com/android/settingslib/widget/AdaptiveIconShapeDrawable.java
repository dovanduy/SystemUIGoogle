// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.widget;

import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
import android.content.res.Resources$Theme;
import android.util.AttributeSet;
import org.xmlpull.v1.XmlPullParser;
import android.graphics.drawable.shapes.Shape;
import android.graphics.drawable.shapes.PathShape;
import android.graphics.Path;
import android.util.PathParser;
import android.content.res.Resources;
import android.graphics.drawable.ShapeDrawable;

public class AdaptiveIconShapeDrawable extends ShapeDrawable
{
    public AdaptiveIconShapeDrawable(final Resources resources) {
        this.init(resources);
    }
    
    private void init(final Resources resources) {
        this.setShape((Shape)new PathShape(new Path(PathParser.createPathFromPathData(resources.getString(17039911))), 100.0f, 100.0f));
    }
    
    public void inflate(final Resources resources, final XmlPullParser xmlPullParser, final AttributeSet set, final Resources$Theme resources$Theme) throws XmlPullParserException, IOException {
        super.inflate(resources, xmlPullParser, set, resources$Theme);
        this.init(resources);
    }
}
