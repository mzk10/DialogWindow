package com.meng.dialogwindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import java.util.Map;

import butterknife.ButterKnife;

public abstract class BaseHolder<T> {

    private Map<String, Object> data;
    //private U data;
    private PopWindowUtil.OnWindowClickListener<T> listener;
    private final Context context;
    private View view;

    public View getRoot() {
        return view;
    }

    public BaseHolder(Context context) {
        this.context = context;
        view = LayoutInflater.from(context).inflate(bindLayout(), null, false);
        ButterKnife.bind(this, view);
        startAction();
    }

    public BaseHolder(Context context, int layout) {
        this.context = context;
        view = LayoutInflater.from(context).inflate(layout, null, false);
        ButterKnife.bind(this, view);
        startAction();
    }

    public BaseHolder(Context context, Map<String, Object> data) {
        this.context = context;
        this.data = data;
        view = LayoutInflater.from(context).inflate(bindLayout(), null, false);
        ButterKnife.bind(this, view);
        startAction();
    }

    public BaseHolder(Context context, Map<String, Object> data, int layout) {
        this.context = context;
        this.data = data;
        view = LayoutInflater.from(context).inflate(layout, null, false);
        ButterKnife.bind(this, view);
        startAction();
    }

    public PopWindowUtil.OnWindowClickListener<T> getListener() {
        return listener;
    }

    public void setListener(PopWindowUtil.OnWindowClickListener<T> listener) {
        this.listener = listener;
    }

    public abstract int bindLayout();

    public abstract void startAction();

    public Context getContext() {
        return context;
    }

    public Map<String, Object> getData() {
        return data;
    }

}
