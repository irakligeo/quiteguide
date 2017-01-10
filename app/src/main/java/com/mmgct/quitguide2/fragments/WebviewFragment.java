package com.mmgct.quitguide2.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.utils.Common;

/**
 * Created by 35527 on 11/5/2015.
 */
public class WebviewFragment extends BaseFragment {
    private static final String TAG = "WebviewFragment";
    public static final String URL_KEY = "url";
    private View rootView;
    private String mUrl;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_webview, null, false);

        WebView webView = (WebView) rootView.findViewById(R.id.plot_web_view);

        Bundle args = getArguments();
        if (args != null && args.containsKey(URL_KEY)) {
            mUrl = args.getString(URL_KEY);
        }

        // Settings
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setJavaScriptEnabled(true);
        if (mUrl != null) {
            String html = Common.assetFileToString(mUrl, getActivity());
            webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
        }
        webView.setBackgroundColor(getResources().getColor(R.color.transparent));

        return rootView;
    }
}
