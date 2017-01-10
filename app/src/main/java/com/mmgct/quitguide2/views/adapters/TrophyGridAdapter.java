package com.mmgct.quitguide2.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.Award;

import java.util.List;

/**
 * Adapter for the trophy section
 * Created by 35527 on 11/5/2015.
 */
public class TrophyGridAdapter extends BaseAdapter {

    private List<Award> mAwards;
    private Context mContext;

    public TrophyGridAdapter(Context ctx, List<Award> awards){
        mContext = ctx;
        mAwards = awards;
    }

    @Override
    public void notifyDataSetChanged() {
        mAwards = DbManager.getInstance().getAwards();
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mAwards.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.view_trophy, null, false);
            holder = new Holder();
            holder.trophyIcon = (ImageView) convertView.findViewById(R.id.ic_trophy);
            holder.trophyDesc = (TextView) convertView.findViewById(R.id.lbl_trophy);
            convertView.setTag(holder);
        } else  {
            holder = (Holder) convertView.getTag();
        }

        // Get item
        Award award = mAwards.get(position);

        holder.trophyIcon.setImageResource(award.isAwarded() ? R.drawable.ic_trophy_gold
                : R.drawable.ic_trophy);
        // Uncomment for different awarded/unawarded description
        /*holder.trophyDesc.setText(award.isAwarded() ? award.getAwardDesc()
                : award.getReqDesc());*/
        holder.trophyDesc.setText(award.getAwardDesc());

        return convertView;
    }

    private class Holder {
        ImageView trophyIcon;
        TextView trophyDesc;
    }
}
