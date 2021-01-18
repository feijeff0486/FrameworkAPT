package com.jeff.framework.apt.demo.mvvm;

import android.databinding.ObservableBoolean;
import android.util.Log;

/**
 * <p>
 *
 * @author Jeff
 * @date 2021/1/7
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public class UserDataStore {
    private static final String TAG = "UserDataStore";
    public final ObservableBoolean enableButton=new ObservableBoolean(false);

    public void switchEnableState(boolean state){
        enableButton.set(state);
        Log.d(TAG, "switchEnableState: "+state);
    }
}
