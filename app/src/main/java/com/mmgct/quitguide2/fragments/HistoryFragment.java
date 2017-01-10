package com.mmgct.quitguide2.fragments;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.mmgct.quitguide2.R;

/**
 * Created by 35527 on 12/9/2015.
 */
public class HistoryFragment extends BaseFragment implements View.OnClickListener {

    public static final String SHOW_CLOSE_BTN = "show_close_btn";
    private View rootView;
    private ImageButton mBtnCal, mBtnList, mBtnPlot, mBtnGraph;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_history, container, false);
        initialize();
        bindListeners();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // If this fragment was navigated to from smoke free button pop all backstack
        if (getArguments() != null && getArguments().getBoolean(SHOW_CLOSE_BTN, false)) {
            getActivity().getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    private void initialize() {
        getChildFragmentManager().beginTransaction()
                    .replace(R.id.frag_container_history, new HistoryCalendarFragment()).commit();

        ((ImageView)rootView.findViewById(R.id.btn_history_calendar)).setImageResource(R.drawable.btn_calendar_active);

        if (getArguments() != null && getArguments().getBoolean(SHOW_CLOSE_BTN, false)) {
            rootView.findViewById(R.id.btn_close).setVisibility(View.VISIBLE);
        } else {
            rootView.findViewById(R.id.btn_close).setVisibility(View.GONE);
        }
    }

    private void bindListeners() {

        mBtnCal = (ImageButton) rootView.findViewById(R.id.btn_history_calendar);
        mBtnList = (ImageButton) rootView.findViewById(R.id.btn_history_list);
        mBtnPlot = (ImageButton) rootView.findViewById(R.id.btn_history_plot);
        mBtnGraph = (ImageButton) rootView.findViewById(R.id.btn_history_graph);

        mBtnCal.setOnClickListener(this);
        mBtnList.setOnClickListener(this);
        mBtnPlot.setOnClickListener(this);
        mBtnGraph.setOnClickListener(this);
        rootView.findViewById(R.id.btn_close).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case(R.id.btn_history_calendar):
                resetButtons();
                ((ImageButton)v).setImageResource(R.drawable.btn_calendar_active);
                mTrackerHost.send(getString(R.string.category_history), getString(R.string.action_calendar_selected));
                mTrackerHost.send(getString(R.string.category_history)+"|"+getString(R.string.action_calendar_selected));
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.frag_container_history, new HistoryCalendarFragment()).commit();
                break;
            case(R.id.btn_history_list):
                resetButtons();
                ((ImageButton)v).setImageResource(R.drawable.btn_history_list_button_active);
                mTrackerHost.send(getString(R.string.category_history), getString(R.string.action_table_selected));
                mTrackerHost.send(getString(R.string.category_history)+"|"+getString(R.string.action_table_selected));
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.frag_container_history, new HistoryListFragment()).commit();
                break;
            case(R.id.btn_history_plot):
                resetButtons();
                ((ImageButton)v).setImageResource(R.drawable.btn_mood_plot_graph_active);
                Bundle args = new Bundle();
                args.putString(HistoryLineChartFragment.URL_KEY, "line.html");
                HistoryLineChartFragment historyLineChartFragment = new HistoryLineChartFragment();
                historyLineChartFragment.setArguments(args);
                mTrackerHost.send(getString(R.string.category_history), getString(R.string.action_line_graph_selected));
                mTrackerHost.send(getString(R.string.category_history)+"|"+getString(R.string.action_line_graph_selected));
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.frag_container_history, historyLineChartFragment).commit();
                break;
            case(R.id.btn_history_graph):
                resetButtons();
                ((ImageButton)v).setImageResource(R.drawable.btn_triggers_bar_graph_active);
                Bundle args2 = new Bundle();
                args2.putString(HistoryLineChartFragment.URL_KEY, "bar.html");
                HistoryBarGraphFragment historyBarGraphFragment = new HistoryBarGraphFragment();
                historyBarGraphFragment.setArguments(args2);
                mTrackerHost.send(getString(R.string.category_history), getString(R.string.action_bar_chart_selected));
                mTrackerHost.send(getString(R.string.category_history)+"|"+getString(R.string.action_bar_chart_selected));
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.frag_container_history, historyBarGraphFragment).commit();
                break;
            case(R.id.btn_close):
                mCallbacks.popBackStack();
                break;
        }
    }

    private void resetButtons() {
        if (mBtnCal == null || mBtnList == null
                || mBtnPlot == null || mBtnGraph == null) {
            return;
        } else {
            mBtnCal.setImageResource(R.drawable.btn_calendar);
            mBtnList.setImageResource(R.drawable.btn_history_list);
            mBtnPlot.setImageResource(R.drawable.btn_mood_plot_graph);
            mBtnGraph.setImageResource(R.drawable.btn_triggers_bar_graph);
        }
    }
}
