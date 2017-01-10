package com.mmgct.quitguide2.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.fragments.listeners.HeaderCallbacks;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.Journal;
import com.mmgct.quitguide2.utils.Common;

/**
 * Created by 35527 on 12/1/2015.
 */
public class EditJournalFragment extends BaseFragment {

    private View rootView;
    private HeaderCallbacks mHeaderCallbacks;
    private Journal mJournal;
    private boolean mShowHeader;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mHeaderCallbacks = (HeaderCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement HeaderCallbacks");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHeaderCallbacks.showSave();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_edit_journal, container, false);
        if(isShowHeader()) {
            rootView.findViewById(R.id.header).setVisibility(View.VISIBLE);
        }
        setDate();
        setSaveListener();
        bindListeners();

        // If opening a saved journal
        Bundle args;
        if ((args = getArguments()) != null && args.containsKey(JournalFragment.EXISTING_JOURNAL_KEY)) {
            mJournal = (Journal) args.get(JournalFragment.EXISTING_JOURNAL_KEY);
            ((EditText)rootView.findViewById(R.id.edt_edit_journal_title)).setText(mJournal.getTitle());
            ((EditText)rootView.findViewById(R.id.edt_edit_journal_entry)).setText(mJournal.getEntry());
        }

        return rootView;
    }

    private void bindListeners() {
        final EditText title = (EditText) rootView.findViewById(R.id.edt_edit_journal_title);
        final EditText entry = (EditText) rootView.findViewById(R.id.edt_edit_journal_entry);

        rootView.findViewById(R.id.header_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(title, entry);
            }
        });

        rootView.findViewById(R.id.header_black_back_nav).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.popBackStack();
            }
        });
    }

    private void setSaveListener() {
        final EditText title = (EditText) rootView.findViewById(R.id.edt_edit_journal_title);
        final EditText entry = (EditText) rootView.findViewById(R.id.edt_edit_journal_entry);

        mHeaderCallbacks.setOnSaveListener(new HeaderCallbacks.OnSaveListener() {
            @Override
            public void onSaved() {
                save(title, entry);
            }
        });
    }

    private void save(EditText title, EditText entry) {
        boolean update = true;
        if (mJournal == null) {
            mJournal = new Journal();
            update = false;
        }
        mJournal.setDate(System.currentTimeMillis());
        mJournal.setTitle(title.getText().toString());
        mJournal.setEntry(entry.getText().toString());
        if (!update) {
            DbManager.getInstance().createJournal(mJournal);
        } else {
            DbManager.getInstance().updateJournal(mJournal);
        }
        mCallbacks.popBackStack();
    }

    private void setDate() {
        ((TextView)rootView.findViewById(R.id.txt_edit_journal_date)).setText(Common.formatTimestamp("MMMM d, yyyy h:mm a", System.currentTimeMillis()));
    }

    @Override
    public void onDestroyView() {
        JournalFragment f = (JournalFragment) getTargetFragment();
        Animator animator = AnimatorInflater.loadAnimator(getActivity(), R.animator.oa_slide_right_in);
        animator.setTarget(f.getView());
        animator.start();
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        JournalFragment f = (JournalFragment) getTargetFragment();
        f.populateJournals((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE));

        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }

        mHeaderCallbacks.showMain();
    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        return enter ? AnimatorInflater.loadAnimator(getActivity(), R.animator.oa_slide_left_in)
                : AnimatorInflater.loadAnimator(getActivity(), R.animator.oa_slide_right_out);
    }

    public boolean isShowHeader() {
        return mShowHeader;
    }

    public void setShowHeader(boolean mShowHeader) {
        this.mShowHeader = mShowHeader;
    }
}
