package com.mmgct.quitguide2.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.emilsjolander.components.StickyScrollViewItems.StickyScrollView;
import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.Journal;
import com.mmgct.quitguide2.utils.Common;
import com.mmgct.quitguide2.utils.Constants;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 35527 on 11/30/2015.
 */
public class JournalFragment extends BaseFlipInFragment implements View.OnClickListener {

    public static final String EXISTING_JOURNAL_KEY = "journal_key";
    public static final String SHOW_CLOSE_BTN = "show_close_key";
    private View rootView;
    private StickyScrollView mStickyScroll;
    private LinearLayout mScrollContainer;
    private String mAnimationType;
    private boolean isOpeningItem;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_journal, container, false);
        // Adding a stickyscrollview
        addStickyScrollView();

        // addHeaderComponents(inflater);
        // populateJournals(inflater);
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        populateJournals((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        bindListeners();

        Bundle args = getArguments();
        if (args != null && args.getBoolean(SHOW_CLOSE_BTN, false)) {
            mAnimationType = Constants.ANIM_FLIP_RIGHT_OUT;
            rootView.findViewById(R.id.header).setVisibility(View.VISIBLE);
        } else {
            rootView.findViewById(R.id.btn_close).setVisibility(View.GONE);
        }
    }

    private void bindListeners() {
        ImageButton btnClose = (ImageButton) rootView.findViewById(R.id.btn_close);
        btnClose.bringToFront();
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAnimationType = Constants.ANIM_SLIDE_DOWN;
                disableTargetAnimation();
                mCallbacks.popBackStack();
                mCallbacks.popBackStack();
                mCallbacks.popBackStack();
            }
        });
    }

    /**
     * This method retrieves all journal entries then for each, sets their content, and adds to the
     * {@link:StickyScrollView}. The tag for these Views is their related {@link:Journal} object.
     *
     * @param inflater - used to inflate template xml for journal entries
     */
    protected void populateJournals(LayoutInflater inflater) {

        // Remove children first
        mScrollContainer.removeAllViews();
        addHeaderComponents(inflater);

        List<Journal> journals = DbManager.getInstance().getAllJournals();

        for (int i = 0; i < journals.size(); i++) {
            Journal j = journals.get(i);
            // Using SwipeLayout library -- https://github.com/daimajia/AndroidSwipeLayout/wiki/usage
            SwipeLayout item = (SwipeLayout) inflater.inflate(R.layout.item_journal_entry, (ViewGroup)rootView, false);
            TextView header = (TextView) item.findViewById(R.id.header_journal_entry);
            TextView date = (TextView) item.findViewById(R.id.timestamp_journal_entry);
            View btnDelete = item.findViewById(R.id.btn_delete_journal_entry);
            header.setText(j.getTitle());
            date.setText(Common.formatTimestamp("MMMM d, yyyy h:mm a", j.getDate()));
            item.setTag(j);
            item.getSurfaceView().setOnClickListener(this);
            btnDelete.setOnClickListener(this);
            mScrollContainer.addView(item);
        }
    }

    private void addHeaderComponents(LayoutInflater inflater) {
        // Add a new entry
        TextView addNewEntry = (TextView) inflater.inflate(R.layout.template_tv_white_header, (ViewGroup)rootView, false);
        addNewEntry.setText(getResources().getString(R.string.btn_add_new_entry));
        final Fragment target = this;
        addNewEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public synchronized void onClick(View v) {
                closeAllSlideViews();
                if (isOpeningItem)
                    return;
                isOpeningItem = true;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isOpeningItem = false;
                    }
                }, 300);
                EditJournalFragment f = new EditJournalFragment();
                if (getArguments() != null && getArguments().getBoolean(SHOW_CLOSE_BTN, false)) {
                    f.setShowHeader(true);
                }
                f.setTargetFragment(target, 0);
                Animator animator = AnimatorInflater.loadAnimator(getActivity(), R.animator.oa_slide_left_out);
                animator.setTarget(rootView);
                animator.start();
                mCallbacks.onAddNavigationAction(f, "", true);
            }
        });

        // Sticky header
        TextView header= (TextView) inflater.inflate(R.layout.template_tv_white_bg_black_txt, (ViewGroup)rootView, false);
        header.setText(getResources().getString(R.string.header_saved_journals));
        header.setTag("sticky");

        mScrollContainer.addView(addNewEntry);
        mScrollContainer.addView(header);
    }

    private void addStickyScrollView() {
        mStickyScroll = new StickyScrollView(getActivity());
        mStickyScroll.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mStickyScroll.setBackgroundResource(R.color.transparent);
        mScrollContainer = new LinearLayout(getActivity());
        mScrollContainer.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mScrollContainer.setBackgroundResource(R.color.transparent);
        mScrollContainer.setOrientation(LinearLayout.VERTICAL);
        mStickyScroll.addView(mScrollContainer);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.BELOW, R.id.header);
        mStickyScroll.setLayoutParams(lp);
        ((ViewGroup) rootView).addView(mStickyScroll);
    }

    private void closeAllSlideViews() {
        for (int i = 0; i < mScrollContainer.getChildCount(); i++) {
            View v = mScrollContainer.getChildAt(i);
            if (v instanceof SwipeLayout) {
                ((SwipeLayout)v).close();
            }
        }
    }

    @Override
    public synchronized void onClick(View v) {
        if (v.getId() == R.id.surface_view_journal_entry) {
            closeAllSlideViews();
            if (isOpeningItem)
                return;
            isOpeningItem = true;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isOpeningItem = false;
                }
            }, 300);
            v.setBackgroundResource(R.color.chalk);
            EditJournalFragment editJournalFragment = new EditJournalFragment();
            if (getArguments() != null && getArguments().getBoolean(SHOW_CLOSE_BTN, false)) {
                editJournalFragment.setShowHeader(true);
            }
            Bundle args = new Bundle();
            args.putSerializable(EXISTING_JOURNAL_KEY, (Serializable) ((View)v.getParent()).getTag());
            editJournalFragment.setArguments(args);
            editJournalFragment.setTargetFragment(this, 0);
            Animator animator = AnimatorInflater.loadAnimator(getActivity(), R.animator.oa_slide_left_out);
            animator.setTarget(rootView);
            animator.start();
            mCallbacks.onAddNavigationAction(editJournalFragment, "", true);
        } else if(v.getId() == R.id.btn_delete_journal_entry) {
            Journal j = (Journal) ((View)v.getParent()).getTag();
            DbManager.getInstance().deleteJournal(j);
            mScrollContainer.removeView((View)v.getParent());
        }
    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        Animator animator = null;
        if (mAnimationType == null) {
            animator = null;
        } else if (mAnimationType.equals(Constants.ANIM_SLIDE_LEFT_OUT)) {
            animator = AnimatorInflater.loadAnimator(getActivity(), R.animator.oa_slide_left_out);
        } else if (mAnimationType.equals(Constants.ANIM_SLIDE_RIGHT_IN)) {
            animator = AnimatorInflater.loadAnimator(getActivity(), R.animator.oa_slide_right_in);
        } else if (mAnimationType.equals(Constants.ANIM_FLIP_RIGHT_IN)) {
            animator = AnimatorInflater.loadAnimator(getActivity(), R.animator.card_flip_right_in);
        } else if (mAnimationType.equals(Constants.ANIM_FLIP_RIGHT_OUT)) {
            animator = AnimatorInflater.loadAnimator(getActivity(), R.animator.card_flip_right_out);
        } else if (mAnimationType.equals(Constants.ANIM_FLIP_LEFT_IN)) {
            animator = AnimatorInflater.loadAnimator(getActivity(), R.animator.card_flip_left_in);
        } else if (mAnimationType.equals(Constants.ANIM_SLIDE_DOWN)) {
            animator = AnimatorInflater.loadAnimator(getActivity(), R.animator.oa_slide_down);
        }
        return animator;
    }

    public String getAnimationType() {
        return mAnimationType;
    }

    public void setAnimationType(String mAnimationType) {
        this.mAnimationType = mAnimationType;
    }

    private void disableTargetAnimation() {
        try {
            ((BaseFlipInFragment) getTargetFragment()).disableAnimations();
        } catch (ClassCastException e) {
            throw new ClassCastException("Target fragment must extend BaseFlipInFragment");
        }
    }

    public boolean isOpeningItem() {
        return isOpeningItem;
    }

    public void setIsOpeningItem(boolean isOpeningItem) {
        this.isOpeningItem = isOpeningItem;
    }
}
