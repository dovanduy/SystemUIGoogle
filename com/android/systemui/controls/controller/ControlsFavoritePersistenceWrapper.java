// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.controller;

import java.io.FileNotFoundException;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import libcore.io.IoUtils;
import java.io.InputStream;
import android.util.Xml;
import com.android.systemui.backup.BackupHelper;
import java.io.FileInputStream;
import android.util.Log;
import kotlin.collections.CollectionsKt;
import android.content.ComponentName;
import java.util.ArrayList;
import kotlin.jvm.internal.Ref$IntRef;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import kotlin.jvm.internal.Intrinsics;
import java.io.File;
import java.util.concurrent.Executor;
import android.app.backup.BackupManager;

public final class ControlsFavoritePersistenceWrapper
{
    private BackupManager backupManager;
    private final Executor executor;
    private File file;
    
    public ControlsFavoritePersistenceWrapper(final File file, final Executor executor, final BackupManager backupManager) {
        Intrinsics.checkParameterIsNotNull(file, "file");
        Intrinsics.checkParameterIsNotNull(executor, "executor");
        this.file = file;
        this.executor = executor;
        this.backupManager = backupManager;
    }
    
    private final List<StructureInfo> parseXml(final XmlPullParser xmlPullParser) {
        final Ref$IntRef ref$IntRef = new Ref$IntRef();
        final ArrayList<StructureInfo> list = new ArrayList<StructureInfo>();
        final ArrayList<ControlInfo> list2 = new ArrayList<ControlInfo>();
        String attributeValue;
        Object unflattenFromString = attributeValue = null;
        while ((ref$IntRef.element = xmlPullParser.next()) != 1) {
            String name = xmlPullParser.getName();
            final String s = "";
            if (name == null) {
                name = "";
            }
            if (ref$IntRef.element == 2 && Intrinsics.areEqual(name, "structure")) {
                unflattenFromString = ComponentName.unflattenFromString(xmlPullParser.getAttributeValue((String)null, "component"));
                attributeValue = xmlPullParser.getAttributeValue((String)null, "structure");
                if (attributeValue != null) {
                    continue;
                }
                attributeValue = "";
            }
            else if (ref$IntRef.element == 2 && Intrinsics.areEqual(name, "control")) {
                final String attributeValue2 = xmlPullParser.getAttributeValue((String)null, "id");
                final String attributeValue3 = xmlPullParser.getAttributeValue((String)null, "title");
                final String attributeValue4 = xmlPullParser.getAttributeValue((String)null, "subtitle");
                String s2 = s;
                if (attributeValue4 != null) {
                    s2 = attributeValue4;
                }
                final String attributeValue5 = xmlPullParser.getAttributeValue((String)null, "type");
                Integer value;
                if (attributeValue5 != null) {
                    value = Integer.parseInt(attributeValue5);
                }
                else {
                    value = null;
                }
                if (attributeValue2 == null || attributeValue3 == null || value == null) {
                    continue;
                }
                list2.add(new ControlInfo(attributeValue2, attributeValue3, s2, value));
            }
            else {
                if (ref$IntRef.element != 3 || !Intrinsics.areEqual(name, "structure")) {
                    continue;
                }
                if (unflattenFromString == null) {
                    Intrinsics.throwNpe();
                    throw null;
                }
                if (attributeValue == null) {
                    Intrinsics.throwNpe();
                    throw null;
                }
                list.add(new StructureInfo((ComponentName)unflattenFromString, attributeValue, CollectionsKt.toList((Iterable<? extends ControlInfo>)list2)));
                list2.clear();
            }
        }
        return list;
    }
    
    public final void changeFileAndBackupManager(final File file, final BackupManager backupManager) {
        Intrinsics.checkParameterIsNotNull(file, "fileName");
        this.file = file;
        this.backupManager = backupManager;
    }
    
    public final void deleteFile() {
        this.file.delete();
    }
    
    public final boolean getFileExists() {
        return this.file.exists();
    }
    
    public final List<StructureInfo> readFavorites() {
        if (!this.file.exists()) {
            Log.d("ControlsFavoritePersistenceWrapper", "No favorites, returning empty list");
            return CollectionsKt.emptyList();
        }
        try {
            final FileInputStream fileInputStream = new FileInputStream(this.file);
            try {
                final StringBuilder sb = new StringBuilder();
                sb.append("Reading data from file: ");
                sb.append(this.file);
                Log.d("ControlsFavoritePersistenceWrapper", sb.toString());
                synchronized (BackupHelper.Companion.getControlsDataLock()) {
                    final XmlPullParser pullParser = Xml.newPullParser();
                    pullParser.setInput((InputStream)fileInputStream, (String)null);
                    Intrinsics.checkExpressionValueIsNotNull(pullParser, "parser");
                    final List<StructureInfo> xml = this.parseXml(pullParser);
                    // monitorexit(BackupHelper.Companion.getControlsDataLock())
                    IoUtils.closeQuietly((AutoCloseable)fileInputStream);
                    return xml;
                }
            }
            catch (IOException cause) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("Failed parsing favorites file: ");
                sb2.append(this.file);
                throw new IllegalStateException(sb2.toString(), cause);
            }
            catch (XmlPullParserException cause2) {
                final StringBuilder sb3 = new StringBuilder();
                sb3.append("Failed parsing favorites file: ");
                sb3.append(this.file);
                throw new IllegalStateException(sb3.toString(), (Throwable)cause2);
            }
            IoUtils.closeQuietly((AutoCloseable)fileInputStream);
        }
        catch (FileNotFoundException ex) {
            Log.i("ControlsFavoritePersistenceWrapper", "No file found");
            return CollectionsKt.emptyList();
        }
    }
    
    public final void storeFavorites(final List<StructureInfo> list) {
        Intrinsics.checkParameterIsNotNull(list, "structures");
        this.executor.execute((Runnable)new ControlsFavoritePersistenceWrapper$storeFavorites.ControlsFavoritePersistenceWrapper$storeFavorites$1(this, (List)list));
    }
}
