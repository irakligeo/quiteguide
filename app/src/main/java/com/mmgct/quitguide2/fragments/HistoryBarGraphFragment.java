package com.mmgct.quitguide2.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.utils.Common;
import com.mmgct.quitguide2.utils.Constants;

import java.util.List;

/**
 * Created by 35527 on 12/16/2015.
 */
public class HistoryBarGraphFragment extends BaseFragment {

    private static final String TAG = "HistoryChartFragment";
    public static final String URL_KEY = "url";
    public static final String LABEL_TOKEN = "#LABEL_TOKEN#"; // See html file, line.html
    public static final String DATA_TOKEN = "#DATA_TOKEN#";
    public static final String WIDTH_TOKEN = "#WIDTH_TOKEN#";
    public static final String HEIGHT_TOKEN = "#HEIGHT_TOKEN#";
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
            StringBuilder html = new StringBuilder(Common.assetFileToString(mUrl, getActivity()));

            List<DbManager.BarGraphItem> graphItems = DbManager.getInstance().getBarItems();
            StringBuilder labels = new StringBuilder();
            StringBuilder data = new StringBuilder();

            for (DbManager.BarGraphItem item : graphItems) {
                labels.append("\"");
                labels.append(item.getLabel());
                labels.append("\"");
                labels.append(",");
                data.append("\"");
                data.append(item.getValue());
                data.append("\"");
                data.append(",");
            }
            // Remove trailing comma
            if (labels.length() > 0 && data.length() > 0) {
                labels.deleteCharAt(labels.length()-1);
                data.deleteCharAt(data.length()-1);
            }

            DisplayMetrics dm = getResources().getDisplayMetrics();

            int substringStart;

            html.replace(substringStart = html.indexOf(WIDTH_TOKEN), substringStart + WIDTH_TOKEN.length(), String.valueOf(Math.floor(dm.widthPixels/dm.density-8*dm.density)));
            html.replace(substringStart = html.indexOf(HEIGHT_TOKEN), substringStart + HEIGHT_TOKEN.length(), String.valueOf(Math.floor(dm.widthPixels/dm.density-14*dm.density)));
            html.replace(substringStart = html.indexOf(LABEL_TOKEN), substringStart + LABEL_TOKEN.length(), labels.toString());
            html.replace(substringStart = html.indexOf(DATA_TOKEN), substringStart + DATA_TOKEN.length(), data.toString());

            webView.loadDataWithBaseURL("file:///android_asset/", html.toString(), "text/html", "UTF-8", null);
        }
        webView.setBackgroundColor(getResources().getColor(R.color.transparent));

        return rootView;
    }

}
