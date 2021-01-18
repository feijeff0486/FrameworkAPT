package com.jeff.framework.apt.demo.mvvm;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jeff.framework.apt.annotation.ViewModelGenerator;
import com.jeff.framework.apt.demo.mvvm.databinding.ActivityMainBinding;

@ViewModelGenerator(value = {
        BookDataStore.class,
        UserDataStore.class
})
public class MainActivity extends AppCompatActivity {

    private MainActivityViewModel mainActivityViewModel=new MainActivityViewModel();
    private ActivityMainBinding mDataBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataBinding= DataBindingUtil.setContentView(this,R.layout.activity_main);
        mDataBinding.setBookStore(mainActivityViewModel.getBookDataStore());
        mDataBinding.setUserStore(mainActivityViewModel.getUserDataStore());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
