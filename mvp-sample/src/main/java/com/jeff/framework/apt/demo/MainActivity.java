package com.jeff.framework.apt.demo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.jeff.framework.apt.annotation.PresenterGenerator;
import com.jeff.framework.core.AbstractCompatActivity;

@PresenterGenerator({MainPresenter.class, BookPresenter.class})
public class MainActivity extends AbstractCompatActivity implements MainView, BookView {
    private static final String TAG = "MainActivity";
    private MainActivityPresenterProvider mainActivityPresenterProvider;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        super.init(savedInstanceState);
        mainActivityPresenterProvider = MainActivityPresenterProvider.attach(this, savedInstanceState);
        mainActivityPresenterProvider.setLogging(true);
    }

    @Override
    protected void initView() {
        findViewById(R.id.btn_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: mainTest");
//                ((MainPresenter)mainActivityPresenterProvider.getPresenter("com.jeff.framework.apt.demo.MainPresenter")).mainTest();
                mainActivityPresenterProvider.getPresenter(MainPresenter.class).mainTest();
            }
        });
        findViewById(R.id.btn_book).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: bookTest");
                mainActivityPresenterProvider.getBookPresenter().bookTest();
            }
        });
    }

    @Override
    protected void handleExtraParams(int from) {

    }

    @Override
    public void mainTest() {
        Log.d(TAG, "mainTest: ");
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void bookTest() {
        Log.d(TAG, "bookTest: ");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mainActivityPresenterProvider.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        mainActivityPresenterProvider.detach();
        super.onDestroy();
    }
}
