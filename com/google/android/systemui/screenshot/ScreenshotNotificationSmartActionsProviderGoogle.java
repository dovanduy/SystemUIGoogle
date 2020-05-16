// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.screenshot;

import com.google.android.apps.miphone.aiai.matchmaker.overview.ui.ContentSuggestionsServiceWrapper;
import android.os.SystemClock;
import android.util.Log;
import android.graphics.Bitmap$Config;
import android.content.ComponentName;
import android.graphics.Bitmap;
import java.util.Collections;
import android.app.Notification$Action;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import android.os.Bundle;
import android.os.Handler;
import java.util.concurrent.Executor;
import android.content.Context;
import com.google.android.apps.miphone.aiai.matchmaker.overview.ui.ContentSuggestionsServiceClient;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2.FeedbackParcelables$ScreenshotOpStatus;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2.FeedbackParcelables$ScreenshotOp;
import com.google.common.collect.ImmutableMap;
import com.android.systemui.screenshot.ScreenshotNotificationSmartActionsProvider;

public final class ScreenshotNotificationSmartActionsProviderGoogle extends ScreenshotNotificationSmartActionsProvider
{
    private static final ImmutableMap<ScreenshotOp, FeedbackParcelables$ScreenshotOp> SCREENSHOT_OP_MAP;
    private static final ImmutableMap<ScreenshotOpStatus, FeedbackParcelables$ScreenshotOpStatus> SCREENSHOT_OP_STATUS_MAP;
    private final ContentSuggestionsServiceClient mClient;
    
    static {
        SCREENSHOT_OP_MAP = ImmutableMap.builder().put(ScreenshotOp.RETRIEVE_SMART_ACTIONS, FeedbackParcelables$ScreenshotOp.RETRIEVE_SMART_ACTIONS).put(ScreenshotOp.REQUEST_SMART_ACTIONS, FeedbackParcelables$ScreenshotOp.REQUEST_SMART_ACTIONS).put(ScreenshotOp.WAIT_FOR_SMART_ACTIONS, FeedbackParcelables$ScreenshotOp.WAIT_FOR_SMART_ACTIONS).build();
        SCREENSHOT_OP_STATUS_MAP = ImmutableMap.builder().put(ScreenshotOpStatus.SUCCESS, FeedbackParcelables$ScreenshotOpStatus.SUCCESS).put(ScreenshotOpStatus.ERROR, FeedbackParcelables$ScreenshotOpStatus.ERROR).put(ScreenshotOpStatus.TIMEOUT, FeedbackParcelables$ScreenshotOpStatus.TIMEOUT).build();
    }
    
    public ScreenshotNotificationSmartActionsProviderGoogle(final Context context, final Executor executor, final Handler handler) {
        this.mClient = new ContentSuggestionsServiceClient(context, executor, handler);
    }
    
    void completeFuture(final Bundle bundle, final CompletableFuture<List<Notification$Action>> completableFuture) {
        if (bundle.containsKey("ScreenshotNotificationActions")) {
            completableFuture.complete(bundle.getParcelableArrayList("ScreenshotNotificationActions"));
        }
        else {
            completableFuture.complete(Collections.emptyList());
        }
    }
    
    @Override
    public CompletableFuture<List<Notification$Action>> getActions(final String s, final String s2, final Bitmap bitmap, final ComponentName componentName, final boolean b) {
        final CompletableFuture<List<Notification$Action>> completableFuture = new CompletableFuture<List<Notification$Action>>();
        if (bitmap.getConfig() != Bitmap$Config.HARDWARE) {
            Log.e("ScreenshotActionsGoogle", String.format("Bitmap expected: Hardware, Bitmap found: %s. Returning empty list.", bitmap.getConfig()));
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
        final long uptimeMillis = SystemClock.uptimeMillis();
        Log.d("ScreenshotActionsGoogle", "Calling AiAi to obtain screenshot notification smart actions.");
        this.mClient.provideScreenshotActions(bitmap, s2, componentName.getPackageName(), componentName.getClassName(), b, new ContentSuggestionsServiceWrapper.BundleCallback() {
            @Override
            public void onResult(final Bundle bundle) {
                ScreenshotNotificationSmartActionsProviderGoogle.this.completeFuture(bundle, completableFuture);
                final long l = SystemClock.uptimeMillis() - uptimeMillis;
                Log.d("ScreenshotActionsGoogle", String.format("Total time taken to get smart actions: %d ms", l));
                ScreenshotNotificationSmartActionsProviderGoogle.this.notifyOp(s, ScreenshotOp.RETRIEVE_SMART_ACTIONS, ScreenshotOpStatus.SUCCESS, l);
            }
        });
        return completableFuture;
    }
    
    @Override
    public void notifyAction(final String s, final String s2, final boolean b) {
        this.mClient.notifyAction(s, s2, b);
    }
    
    @Override
    public void notifyOp(final String s, final ScreenshotOp screenshotOp, final ScreenshotOpStatus screenshotOpStatus, final long n) {
        this.mClient.notifyOp(s, ScreenshotNotificationSmartActionsProviderGoogle.SCREENSHOT_OP_MAP.getOrDefault(screenshotOp, FeedbackParcelables$ScreenshotOp.OP_UNKNOWN), ScreenshotNotificationSmartActionsProviderGoogle.SCREENSHOT_OP_STATUS_MAP.getOrDefault(screenshotOpStatus, FeedbackParcelables$ScreenshotOpStatus.OP_STATUS_UNKNOWN), n);
    }
}
