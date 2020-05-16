// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.ui;

import android.support.annotation.WorkerThread;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2.SuggestParcelables$InteractionType;
import android.os.Parcelable;
import java.util.Iterator;
import java.util.ArrayList;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2.SuggestParcelables$Entities;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2.ParserParcelables$ParsedViewHierarchy;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2.InteractionContextParcelables$InteractionContext;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2.FeedbackParcelables$ScreenshotOpStatus;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2.FeedbackParcelables$ScreenshotOp;
import android.content.pm.PackageManager$NameNotFoundException;
import android.support.annotation.Nullable;
import android.app.PendingIntent;
import android.graphics.Bitmap;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2.SuggestParcelables$Action;
import android.text.TextUtils;
import com.google.android.apps.miphone.aiai.matchmaker.overview.ui.utils.LogUtils;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2.SuggestParcelables$ActionGroup;
import java.util.List;
import com.google.android.apps.miphone.aiai.matchmaker.overview.ui.utils.Utils;
import android.graphics.drawable.Icon;
import android.app.Notification$Action$Builder;
import android.os.Bundle;
import android.app.RemoteAction;
import android.app.Notification$Action;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2.EntitiesData;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2.SuggestParcelables$Entity;
import android.os.Handler;
import java.util.concurrent.Executor;
import android.content.Context;
import com.google.android.apps.miphone.aiai.matchmaker.overview.common.BundleUtils;
import java.util.Random;

public class ContentSuggestionsServiceClient
{
    private static final Random random;
    private final BundleUtils bundleUtils;
    private final boolean isAiAiVersionSupported;
    private final ContentSuggestionsServiceWrapper serviceWrapper;
    
    static {
        random = new Random();
    }
    
    public ContentSuggestionsServiceClient(final Context context, final Executor executor, final Handler handler) {
        this.serviceWrapper = SuggestController.create(context, context, executor, handler).getWrapper();
        this.isAiAiVersionSupported = isVersionCodeSupported(context);
        this.bundleUtils = BundleUtils.createWithBackwardsCompatVersion();
    }
    
    private static Notification$Action createNotificationActionFromRemoteAction(final RemoteAction remoteAction, final String s, final float n) {
        Icon icon;
        if (remoteAction.shouldShowIcon()) {
            icon = remoteAction.getIcon();
        }
        else {
            icon = null;
        }
        final Bundle bundle = new Bundle();
        bundle.putString("action_type", s);
        bundle.putFloat("action_score", n);
        return new Notification$Action$Builder(icon, remoteAction.getTitle(), remoteAction.getActionIntent()).setContextual(true).addExtras(bundle).build();
    }
    
    @Nullable
    private static Notification$Action generateNotificationAction(final SuggestParcelables$Entity suggestParcelables$Entity, EntitiesData entitiesData) {
        if (suggestParcelables$Entity.getActions() != null) {
            final List<SuggestParcelables$ActionGroup> actions = suggestParcelables$Entity.getActions();
            Utils.checkNotNull(actions);
            if (!actions.isEmpty()) {
                final List<SuggestParcelables$ActionGroup> actions2 = suggestParcelables$Entity.getActions();
                Utils.checkNotNull(actions2);
                final SuggestParcelables$Action mainAction = actions2.get(0).getMainAction();
                if (mainAction != null && mainAction.getId() != null) {
                    final String id = mainAction.getId();
                    Utils.checkNotNull(id);
                    final Bitmap bitmap = entitiesData.getBitmap(id);
                    Utils.checkNotNull(entitiesData);
                    entitiesData = entitiesData;
                    final String id2 = mainAction.getId();
                    Utils.checkNotNull(id2);
                    final PendingIntent pendingIntent = entitiesData.getPendingIntent(id2);
                    if (pendingIntent == null || bitmap == null) {
                        LogUtils.d("Malformed EntitiesData: Expected icon bitmap and intent");
                        return null;
                    }
                    final String displayName = mainAction.getDisplayName();
                    Utils.checkNotNull(displayName);
                    final String s = displayName;
                    final String fullDisplayName = mainAction.getFullDisplayName();
                    Utils.checkNotNull(fullDisplayName);
                    final String s2 = fullDisplayName;
                    final String searchQueryHint = suggestParcelables$Entity.getSearchQueryHint();
                    Utils.checkNotNull(searchQueryHint);
                    final String firstNonEmptyString = getFirstNonEmptyString(s, s2, searchQueryHint);
                    if (firstNonEmptyString == null) {
                        LogUtils.d("Title expected.");
                        return null;
                    }
                    final RemoteAction remoteAction = new RemoteAction(Icon.createWithBitmap(bitmap), (CharSequence)firstNonEmptyString, (CharSequence)firstNonEmptyString, pendingIntent);
                    String searchQueryHint2;
                    if (TextUtils.isEmpty((CharSequence)suggestParcelables$Entity.getSearchQueryHint())) {
                        searchQueryHint2 = "Smart Action";
                    }
                    else {
                        searchQueryHint2 = suggestParcelables$Entity.getSearchQueryHint();
                    }
                    Utils.checkNotNull(searchQueryHint2);
                    return createNotificationActionFromRemoteAction(remoteAction, searchQueryHint2, 1.0f);
                }
                else {
                    LogUtils.d("Malformed mainAction: Expected id");
                }
            }
        }
        return null;
    }
    
    @Nullable
    private static String getFirstNonEmptyString(@Nullable final String... array) {
        if (array != null) {
            for (final String s : array) {
                if (!TextUtils.isEmpty((CharSequence)s)) {
                    return s;
                }
            }
        }
        return null;
    }
    
    private static boolean isVersionCodeSupported(final Context context) {
        boolean b = false;
        try {
            if (context.getPackageManager().getPackageInfo("com.google.android.as", 0).getLongVersionCode() >= 660780L) {
                b = true;
            }
        }
        catch (PackageManager$NameNotFoundException ex) {
            LogUtils.e("Error obtaining package info: ", (Throwable)ex);
        }
        return b;
    }
    
    public void notifyAction(final String s, final String s2, final boolean b) {
        this.serviceWrapper.connectAndRunAsync(new ContentSuggestionsServiceClient$$Lambda$2(this, s, s2, b));
    }
    
    public void notifyOp(final String s, final FeedbackParcelables$ScreenshotOp feedbackParcelables$ScreenshotOp, final FeedbackParcelables$ScreenshotOpStatus feedbackParcelables$ScreenshotOpStatus, final long n) {
        this.serviceWrapper.connectAndRunAsync(new ContentSuggestionsServiceClient$$Lambda$1(this, s, feedbackParcelables$ScreenshotOp, feedbackParcelables$ScreenshotOpStatus, n));
    }
    
    @WorkerThread
    public void provideScreenshotActions(final Bitmap bitmap, final String s, final String s2, final String s3, final boolean b, final ContentSuggestionsServiceWrapper.BundleCallback bundleCallback) {
        if (!this.isAiAiVersionSupported) {
            bundleCallback.onResult(Bundle.EMPTY);
            return;
        }
        final int nextInt = ContentSuggestionsServiceClient.random.nextInt();
        final long currentTimeMillis = System.currentTimeMillis();
        final Bundle obtainScreenshotContextImageBundle = this.bundleUtils.obtainScreenshotContextImageBundle(true, s, s2, s3, currentTimeMillis);
        obtainScreenshotContextImageBundle.putParcelable("android.contentsuggestions.extra.BITMAP", (Parcelable)bitmap);
        final InteractionContextParcelables$InteractionContext create = InteractionContextParcelables$InteractionContext.create();
        create.setInteractionType(SuggestParcelables$InteractionType.SCREENSHOT_NOTIFICATION);
        create.setDisallowCopyPaste(false);
        create.setVersionCode(1);
        create.setIsPrimaryTask(true);
        this.serviceWrapper.connectAndRunAsync(new ContentSuggestionsServiceClient$$Lambda$0(this, nextInt, obtainScreenshotContextImageBundle, s2, s3, currentTimeMillis, create, b, bundleCallback));
    }
}
