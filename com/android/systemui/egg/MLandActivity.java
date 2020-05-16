// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.egg;

import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import android.os.Bundle;
import android.app.Activity;

public class MLandActivity extends Activity
{
    MLand mLand;
    
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.setContentView(R$layout.mland);
        (this.mLand = (MLand)this.findViewById(R$id.world)).setScoreFieldHolder((ViewGroup)this.findViewById(R$id.scores));
        this.mLand.setSplash(this.findViewById(R$id.welcome));
        final int size = this.mLand.getGameControllers().size();
        if (size > 0) {
            this.mLand.setupPlayers(size);
        }
    }
    
    public void onPause() {
        this.mLand.stop();
        super.onPause();
    }
    
    public void onResume() {
        super.onResume();
        this.mLand.onAttachedToWindow();
        this.updateSplashPlayers();
        this.mLand.showSplash();
    }
    
    public void playerMinus(final View view) {
        this.mLand.removePlayer();
        this.updateSplashPlayers();
    }
    
    public void playerPlus(final View view) {
        this.mLand.addPlayer();
        this.updateSplashPlayers();
    }
    
    public void startButtonPressed(final View view) {
        this.findViewById(R$id.player_minus_button).setVisibility(4);
        this.findViewById(R$id.player_plus_button).setVisibility(4);
        this.mLand.start(true);
    }
    
    public void updateSplashPlayers() {
        final int numPlayers = this.mLand.getNumPlayers();
        final View viewById = this.findViewById(R$id.player_minus_button);
        final View viewById2 = this.findViewById(R$id.player_plus_button);
        if (numPlayers == 1) {
            viewById.setVisibility(4);
            viewById2.setVisibility(0);
            viewById2.requestFocus();
        }
        else if (numPlayers == 6) {
            viewById.setVisibility(0);
            viewById2.setVisibility(4);
            viewById.requestFocus();
        }
        else {
            viewById.setVisibility(0);
            viewById2.setVisibility(0);
        }
    }
}
