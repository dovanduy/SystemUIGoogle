// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.os.Handler;
import java.util.Set;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NgaMessageHandler_Factory implements Factory<NgaMessageHandler>
{
    private final Provider<AssistantPresenceHandler> assistantPresenceHandlerProvider;
    private final Provider<Set<NgaMessageHandler.AudioInfoListener>> audioInfoListenersProvider;
    private final Provider<Set<NgaMessageHandler.CardInfoListener>> cardInfoListenersProvider;
    private final Provider<Set<NgaMessageHandler.ChipsInfoListener>> chipsInfoListenersProvider;
    private final Provider<Set<NgaMessageHandler.ClearListener>> clearListenersProvider;
    private final Provider<Set<NgaMessageHandler.ConfigInfoListener>> configInfoListenersProvider;
    private final Provider<Set<NgaMessageHandler.EdgeLightsInfoListener>> edgeLightsInfoListenersProvider;
    private final Provider<Set<NgaMessageHandler.GoBackListener>> goBackListenersProvider;
    private final Provider<Set<NgaMessageHandler.GreetingInfoListener>> greetingInfoListenersProvider;
    private final Provider<Handler> handlerProvider;
    private final Provider<Set<NgaMessageHandler.KeepAliveListener>> keepAliveListenersProvider;
    private final Provider<Set<NgaMessageHandler.KeyboardInfoListener>> keyboardInfoListenersProvider;
    private final Provider<NgaUiController> ngaUiControllerProvider;
    private final Provider<Set<NgaMessageHandler.StartActivityInfoListener>> startActivityInfoListenersProvider;
    private final Provider<Set<NgaMessageHandler.TakeScreenshotListener>> takeScreenshotListenersProvider;
    private final Provider<Set<NgaMessageHandler.TranscriptionInfoListener>> transcriptionInfoListenersProvider;
    private final Provider<Set<NgaMessageHandler.WarmingListener>> warmingListenersProvider;
    private final Provider<Set<NgaMessageHandler.ZerostateInfoListener>> zerostateInfoListenersProvider;
    
    public NgaMessageHandler_Factory(final Provider<NgaUiController> ngaUiControllerProvider, final Provider<AssistantPresenceHandler> assistantPresenceHandlerProvider, final Provider<Set<NgaMessageHandler.KeepAliveListener>> keepAliveListenersProvider, final Provider<Set<NgaMessageHandler.AudioInfoListener>> audioInfoListenersProvider, final Provider<Set<NgaMessageHandler.CardInfoListener>> cardInfoListenersProvider, final Provider<Set<NgaMessageHandler.ConfigInfoListener>> configInfoListenersProvider, final Provider<Set<NgaMessageHandler.EdgeLightsInfoListener>> edgeLightsInfoListenersProvider, final Provider<Set<NgaMessageHandler.TranscriptionInfoListener>> transcriptionInfoListenersProvider, final Provider<Set<NgaMessageHandler.GreetingInfoListener>> greetingInfoListenersProvider, final Provider<Set<NgaMessageHandler.ChipsInfoListener>> chipsInfoListenersProvider, final Provider<Set<NgaMessageHandler.ClearListener>> clearListenersProvider, final Provider<Set<NgaMessageHandler.StartActivityInfoListener>> startActivityInfoListenersProvider, final Provider<Set<NgaMessageHandler.KeyboardInfoListener>> keyboardInfoListenersProvider, final Provider<Set<NgaMessageHandler.ZerostateInfoListener>> zerostateInfoListenersProvider, final Provider<Set<NgaMessageHandler.GoBackListener>> goBackListenersProvider, final Provider<Set<NgaMessageHandler.TakeScreenshotListener>> takeScreenshotListenersProvider, final Provider<Set<NgaMessageHandler.WarmingListener>> warmingListenersProvider, final Provider<Handler> handlerProvider) {
        this.ngaUiControllerProvider = ngaUiControllerProvider;
        this.assistantPresenceHandlerProvider = assistantPresenceHandlerProvider;
        this.keepAliveListenersProvider = keepAliveListenersProvider;
        this.audioInfoListenersProvider = audioInfoListenersProvider;
        this.cardInfoListenersProvider = cardInfoListenersProvider;
        this.configInfoListenersProvider = configInfoListenersProvider;
        this.edgeLightsInfoListenersProvider = edgeLightsInfoListenersProvider;
        this.transcriptionInfoListenersProvider = transcriptionInfoListenersProvider;
        this.greetingInfoListenersProvider = greetingInfoListenersProvider;
        this.chipsInfoListenersProvider = chipsInfoListenersProvider;
        this.clearListenersProvider = clearListenersProvider;
        this.startActivityInfoListenersProvider = startActivityInfoListenersProvider;
        this.keyboardInfoListenersProvider = keyboardInfoListenersProvider;
        this.zerostateInfoListenersProvider = zerostateInfoListenersProvider;
        this.goBackListenersProvider = goBackListenersProvider;
        this.takeScreenshotListenersProvider = takeScreenshotListenersProvider;
        this.warmingListenersProvider = warmingListenersProvider;
        this.handlerProvider = handlerProvider;
    }
    
    public static NgaMessageHandler_Factory create(final Provider<NgaUiController> provider, final Provider<AssistantPresenceHandler> provider2, final Provider<Set<NgaMessageHandler.KeepAliveListener>> provider3, final Provider<Set<NgaMessageHandler.AudioInfoListener>> provider4, final Provider<Set<NgaMessageHandler.CardInfoListener>> provider5, final Provider<Set<NgaMessageHandler.ConfigInfoListener>> provider6, final Provider<Set<NgaMessageHandler.EdgeLightsInfoListener>> provider7, final Provider<Set<NgaMessageHandler.TranscriptionInfoListener>> provider8, final Provider<Set<NgaMessageHandler.GreetingInfoListener>> provider9, final Provider<Set<NgaMessageHandler.ChipsInfoListener>> provider10, final Provider<Set<NgaMessageHandler.ClearListener>> provider11, final Provider<Set<NgaMessageHandler.StartActivityInfoListener>> provider12, final Provider<Set<NgaMessageHandler.KeyboardInfoListener>> provider13, final Provider<Set<NgaMessageHandler.ZerostateInfoListener>> provider14, final Provider<Set<NgaMessageHandler.GoBackListener>> provider15, final Provider<Set<NgaMessageHandler.TakeScreenshotListener>> provider16, final Provider<Set<NgaMessageHandler.WarmingListener>> provider17, final Provider<Handler> provider18) {
        return new NgaMessageHandler_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13, provider14, provider15, provider16, provider17, provider18);
    }
    
    public static NgaMessageHandler provideInstance(final Provider<NgaUiController> provider, final Provider<AssistantPresenceHandler> provider2, final Provider<Set<NgaMessageHandler.KeepAliveListener>> provider3, final Provider<Set<NgaMessageHandler.AudioInfoListener>> provider4, final Provider<Set<NgaMessageHandler.CardInfoListener>> provider5, final Provider<Set<NgaMessageHandler.ConfigInfoListener>> provider6, final Provider<Set<NgaMessageHandler.EdgeLightsInfoListener>> provider7, final Provider<Set<NgaMessageHandler.TranscriptionInfoListener>> provider8, final Provider<Set<NgaMessageHandler.GreetingInfoListener>> provider9, final Provider<Set<NgaMessageHandler.ChipsInfoListener>> provider10, final Provider<Set<NgaMessageHandler.ClearListener>> provider11, final Provider<Set<NgaMessageHandler.StartActivityInfoListener>> provider12, final Provider<Set<NgaMessageHandler.KeyboardInfoListener>> provider13, final Provider<Set<NgaMessageHandler.ZerostateInfoListener>> provider14, final Provider<Set<NgaMessageHandler.GoBackListener>> provider15, final Provider<Set<NgaMessageHandler.TakeScreenshotListener>> provider16, final Provider<Set<NgaMessageHandler.WarmingListener>> provider17, final Provider<Handler> provider18) {
        return new NgaMessageHandler(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get(), provider9.get(), provider10.get(), provider11.get(), provider12.get(), provider13.get(), provider14.get(), provider15.get(), provider16.get(), provider17.get(), provider18.get());
    }
    
    @Override
    public NgaMessageHandler get() {
        return provideInstance(this.ngaUiControllerProvider, this.assistantPresenceHandlerProvider, this.keepAliveListenersProvider, this.audioInfoListenersProvider, this.cardInfoListenersProvider, this.configInfoListenersProvider, this.edgeLightsInfoListenersProvider, this.transcriptionInfoListenersProvider, this.greetingInfoListenersProvider, this.chipsInfoListenersProvider, this.clearListenersProvider, this.startActivityInfoListenersProvider, this.keyboardInfoListenersProvider, this.zerostateInfoListenersProvider, this.goBackListenersProvider, this.takeScreenshotListenersProvider, this.warmingListenersProvider, this.handlerProvider);
    }
}
