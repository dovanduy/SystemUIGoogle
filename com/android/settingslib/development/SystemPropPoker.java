// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.development;

import android.os.RemoteException;
import android.os.Parcel;
import android.util.Log;
import android.os.ServiceManager;
import android.os.IBinder;
import android.os.AsyncTask;

public class SystemPropPoker
{
    private static final SystemPropPoker sInstance;
    private boolean mBlockPokes;
    
    static {
        sInstance = new SystemPropPoker();
    }
    
    private SystemPropPoker() {
        this.mBlockPokes = false;
    }
    
    public static SystemPropPoker getInstance() {
        return SystemPropPoker.sInstance;
    }
    
    PokerTask createPokerTask() {
        return new PokerTask();
    }
    
    public void poke() {
        if (!this.mBlockPokes) {
            this.createPokerTask().execute((Object[])new Void[0]);
        }
    }
    
    public static class PokerTask extends AsyncTask<Void, Void, Void>
    {
        IBinder checkService(final String s) {
            return ServiceManager.checkService(s);
        }
        
        protected Void doInBackground(Void... listServices) {
            listServices = (Void[])this.listServices();
            if (listServices == null) {
                Log.e("SystemPropPoker", "There are no services, how odd");
                return null;
            }
            final int length = listServices.length;
            final int n = 0;
            if (n >= length) {
                goto Label_0128;
            }
            final Void str = listServices[n];
            final IBinder checkService = this.checkService((String)str);
            if (checkService == null) {
                goto Label_0122;
            }
            final Parcel obtain = Parcel.obtain();
            try {
                checkService.transact(1599295570, obtain, (Parcel)null, 0);
                goto Label_0117;
            }
            catch (Exception ex) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Someone wrote a bad service '");
                sb.append((String)str);
                sb.append("' that doesn't like to be poked");
                Log.i("SystemPropPoker", sb.toString(), (Throwable)ex);
            }
            catch (RemoteException ex2) {
                goto Label_0117;
            }
        }
        
        String[] listServices() {
            return ServiceManager.listServices();
        }
    }
}
