package com.mmgct.quitguide2.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.j256.ormlite.dao.GenericRawResults;
import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.Craving;
import com.mmgct.quitguide2.models.HistoryItem;
import com.mmgct.quitguide2.models.Mood;
import com.mmgct.quitguide2.models.Slip;
import com.mmgct.quitguide2.models.SmokeFreeDay;
import com.mmgct.quitguide2.utils.Common;
import com.mmgct.quitguide2.views.adapters.HistoryListAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 35527 on 12/14/2015.
 */
public class HistoryListFragment extends BaseFragment {

    private static final String TAG = "HistoryListFragment";
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_list_history, container, false);
        initialize();
        return rootView;
    }

    private void initialize() {
        List<HistoryItem> historyItems = DbManager.getInstance().getAllHistoryItems();
        List<ListItem> listItems = new ArrayList<ListItem>();
        for (HistoryItem item : historyItems) {
            switch (item.getType()) {
                case (HistoryItem.SMK_FREE_DAY):
                    listItems.add(new ListItem("დღე მოწევის გარეშე!",
                            null,
                            Common.formatTimestamp("MMMMM d, yyyy", item.getDate())));
                    break;
                case (HistoryItem.SLIP):
                    Slip slip = item.getSlip();
                    listItems.add(new ListItem("მოვწიე",
                            "გამომწვევი მიზეზი: " + (slip.getTrigger() == null ? "მიზეზი არაა არჩეული" : slip.getTrigger().getContentDescription()),
                            Common.formatTimestamp("MMMMM d, yyyy", item.getDate())));
                    break;
                case (HistoryItem.CRAVING):
                    Craving craving = item.getCraving();
                    listItems.add(new ListItem("მოწევის სურვილი: " + craving.getCravingIntensity()+ ",",
                            "გამომწვევი მიზეზი: " + (craving.getTrigger() == null ? "მიზეზი არაა არჩეული" : craving.getTrigger().getContentDescription()),
                            Common.formatTimestamp("MMMMM d, yyyy", item.getDate())));
                    break;
                case (HistoryItem.MOOD):
                    Mood mood = item.getMood();
                    listItems.add(new ListItem("განწყობა: "+mood.getType(), null, Common.formatTimestamp("MMMMM d, yyyy", item.getDate())));
                    break;
            }
        }
        ((ListView)rootView.findViewById(R.id.list_view_history)).setAdapter(new HistoryListAdapter(getActivity(), listItems));
    }

    public class ListItem {
        private String header, trigger, date;

        public ListItem() {}
        public ListItem(String header, String trigger, String date) {
            this.header = header;
            this.trigger = trigger;
            this.date = date;
        }

        public String getHeader() {
            return header;
        }

        public void setHeader(String header) {
            this.header = header;
        }

        public String getTrigger() {
            return trigger;
        }

        public void setTrigger(String trigger) {
            this.trigger = trigger;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }
}
