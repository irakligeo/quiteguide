package com.mmgct.quitguide2.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.utils.Common;
import com.mmgct.quitguide2.utils.Constants;

/**
 * Created by 35527 on 11/5/2015.
 */
public class HistoryLineChartFragment extends BaseFragment {
    private static final String TAG = "HistoryChartFragment";
    public static final String URL_KEY = "url";
    public static final String LABEL_TOKEN = "#LABEL_TOKEN#"; // See html file, line.html
    public static final String DATA_TOKEN = "#DATA_TOKEN#";
    private View rootView;
    private String mUrl;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_chart_line_history, null, false);

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
            html = html.replace(LABEL_TOKEN, Constants.PLOT_LABELS);
            html = html.replace(DATA_TOKEN, DbManager.getInstance().getPlotValues());

            webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
        }
        webView.setBackgroundColor(getResources().getColor(R.color.transparent));

        return rootView;
    }
}
