package com.technifysoft.docstudent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeActivity extends Fragment {
    private WebView mWebView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mWebView = view.findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true); // enable JavaScript
        mWebView.loadUrl("https://www.studocu.com/en-us"); // load the website

        return view;
    }
}
