// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints.edgelights;

import com.google.android.systemui.assist.uihints.edgelights.mode.FullListening;
import android.util.Log;
import com.google.android.systemui.assist.uihints.edgelights.mode.FulfillBottom;
import com.google.android.systemui.assist.uihints.edgelights.mode.FulfillPerimeter;
import com.google.android.systemui.assist.uihints.edgelights.mode.Gone;
import com.android.systemui.R$id;
import android.view.ViewGroup;
import android.content.Context;
import com.google.android.systemui.assist.uihints.NgaMessageHandler;

public final class EdgeLightsController implements AudioInfoListener, EdgeLightsInfoListener
{
    private final Context mContext;
    private final EdgeLightsView mEdgeLightsView;
    private ModeChangeThrottler mThrottler;
    
    public EdgeLightsController(final Context mContext, final ViewGroup viewGroup) {
        this.mContext = mContext;
        this.mEdgeLightsView = (EdgeLightsView)viewGroup.findViewById(R$id.edge_lights);
    }
    
    public void addListener(final EdgeLightsListener edgeLightsListener) {
        this.mEdgeLightsView.addListener(edgeLightsListener);
    }
    
    public EdgeLightsView.Mode getMode() {
        return this.mEdgeLightsView.getMode();
    }
    
    @Override
    public void onAudioInfo(final float n, final float n2) {
        this.mEdgeLightsView.onAudioLevelUpdate(n2, n);
    }
    
    @Override
    public void onEdgeLightsInfo(final String str, final boolean b) {
        int n = 0;
        Label_0131: {
            switch (str.hashCode()) {
                case 1971150571: {
                    if (str.equals("FULL_LISTENING")) {
                        n = 0;
                        break Label_0131;
                    }
                    break;
                }
                case 1387022046: {
                    if (str.equals("FULFILL_PERIMETER")) {
                        n = 3;
                        break Label_0131;
                    }
                    break;
                }
                case 429932431: {
                    if (str.equals("HALF_LISTENING")) {
                        n = 1;
                        break Label_0131;
                    }
                    break;
                }
                case 2193567: {
                    if (str.equals("GONE")) {
                        n = 4;
                        break Label_0131;
                    }
                    break;
                }
                case -1911007510: {
                    if (str.equals("FULFILL_BOTTOM")) {
                        n = 2;
                        break Label_0131;
                    }
                    break;
                }
            }
            n = -1;
        }
        EdgeLightsView.Mode mode;
        if (n != 0) {
            if (n != 1) {
                if (n != 2) {
                    if (n != 3) {
                        if (n != 4) {
                            mode = null;
                        }
                        else {
                            mode = new Gone();
                        }
                    }
                    else {
                        mode = new FulfillPerimeter(this.mContext);
                    }
                }
                else {
                    mode = new FulfillBottom(this.mContext, b);
                }
            }
            else {
                Log.i("EdgeLightsController", "Rendering full instead of half listening for now.");
                mode = new FullListening(this.mContext, true);
            }
        }
        else {
            mode = new FullListening(this.mContext, false);
        }
        if (mode == null) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Invalid edge lights mode: ");
            sb.append(str);
            Log.e("EdgeLightsController", sb.toString());
            return;
        }
        final _$$Lambda$EdgeLightsController$BD70M70wHYhKAr30BKmAvcn_cLg $$Lambda$EdgeLightsController$BD70M70wHYhKAr30BKmAvcn_cLg = new _$$Lambda$EdgeLightsController$BD70M70wHYhKAr30BKmAvcn_cLg(this, mode);
        final ModeChangeThrottler mThrottler = this.mThrottler;
        if (mThrottler == null) {
            $$Lambda$EdgeLightsController$BD70M70wHYhKAr30BKmAvcn_cLg.run();
        }
        else {
            mThrottler.runWhenReady(str, $$Lambda$EdgeLightsController$BD70M70wHYhKAr30BKmAvcn_cLg);
        }
    }
    
    public void setFullListening() {
        this.getMode().onNewModeRequest(this.mEdgeLightsView, (EdgeLightsView.Mode)new FullListening(this.mContext, false));
    }
    
    public void setGone() {
        this.getMode().onNewModeRequest(this.mEdgeLightsView, (EdgeLightsView.Mode)new Gone());
    }
    
    public void setModeChangeThrottler(final ModeChangeThrottler mThrottler) {
        this.mThrottler = mThrottler;
    }
    
    public interface ModeChangeThrottler
    {
        void runWhenReady(final String p0, final Runnable p1);
    }
}
