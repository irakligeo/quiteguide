package com.mmgct.quitguide2.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.fragments.dialogs.TriggerPickerDialog;
import com.mmgct.quitguide2.fragments.listeners.DialogDismissListener;
import com.mmgct.quitguide2.fragments.listeners.OnAnimationActionListener;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.HistoryItem;
import com.mmgct.quitguide2.models.Slip;
import com.mmgct.quitguide2.utils.Common;

/**
 * Created by 35527 on 11/11/2015.
 */
public class SlippedFragment extends BaseNoHeaderFragment implements DialogDismissListener {

    private View rootView;
    private String mTriggerSelection;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_slipped, container, false);
        bindListeners();
        return rootView;
    }

    private void save() {
        Slip slip = new Slip();
        slip.setDate(Common.millisAtStartOfDay(System.currentTimeMillis()));
        if (mTriggerSelection != null){
            slip.setTrigger(DbManager.getInstance().getTriggerByDesc(mTriggerSelection));
        }
        DbManager.getInstance().createSlip(slip);
        DbManager.getInstance().createHistoryItem(new HistoryItem(System.currentTimeMillis(), slip, HistoryItem.SLIP));
    }

    private void bindListeners() {

        //Close btn
        rootView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                mCallbacks.popBackStack();
            }
        });

        // Trigger selector
        final TriggerPickerDialog triggerPickerDialog = new TriggerPickerDialog();
        triggerPickerDialog.setTargetFragment(this, 0);

        rootView.findViewById(R.id.slip_trigger_selector).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (triggerPickerDialog != null) {
                    triggerPickerDialog.show(getFragmentManager(), "");
                }
            }
        });
        /*
        rootView.findViewById(R.id.btn_keep_going).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // GA
                mTrackerHost.send(getString(R.string.category_slipped), getString(R.string.action_keep_going));
                mTrackerHost.send(getString(R.string.category_slipped)+"|"+getString(R.string.action_keep_going));
                save();
                mCallbacks.popBackStack();
            }
        });
        final Fragment target = this;
        rootView.findViewById(R.id.btn_new_quitdate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // GA
                mTrackerHost.send(getString(R.string.category_slipped), getString(R.string.action_new_quit_date_set));
                mTrackerHost.send(getString(R.string.category_slipped)+"|"+getString(R.string.action_new_quit_date_set));
                save();
                QuitDateFragment quitDateFragment = new QuitDateFragment();
                Bundle args = new Bundle();
                args.putBoolean(QuitDateFragment.SHOW_CLOSE_BTN_KEY, true);
                quitDateFragment.setArguments(args);
                quitDateFragment.setTargetFragment(target, 0);
                flipOut();
                mCallbacks.onAddNavigationAction(quitDateFragment, "", true);
            }
        });*/

        // Continue button
        rootView.findViewById(R.id.btn_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Slipped2Fragment slip2Fragment = new Slipped2Fragment();
                slip2Fragment.setOnAnimationActionListener(new OnAnimationActionListener() {
                    @Override
                    public void disableAnimation() {
                        Common.mDisableWhatsNewAnim = true;
                        disableAnimations();
                    }

                    @Override
                    public void enableAnimation() {
                        Common.mDisableWhatsNewAnim = false;
                        enableAnimations();
                    }

                    @Override
                    public void startTransitionAnimation(boolean enter) {
                        if (!isAnimationDisabled()) {
                            if (enter) {
                                flipIn();
                            } else {
                                flipOut();
                            }
                        }
                    }
                });
                save();
                mCallbacks.onAddAnimationNavigationAction(slip2Fragment,
                        Slipped2Fragment.TAG, R.animator.card_flip_left_in,
                        R.animator.card_flip_left_out,
                        R.animator.card_flip_right_in,
                        R.animator.card_flip_right_out,
                        true);

            }
        });
    }

    public void flipOut() {
        Animator flipOut = AnimatorInflater.loadAnimator(getActivity(), R.animator.card_flip_left_out);
        flipOut.setTarget(rootView);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(flipOut);
        animatorSet.start();
    }

    public void flipIn() {
        Animator flipOut = AnimatorInflater.loadAnimator(getActivity(), R.animator.card_flip_right_in);
        flipOut.setTarget(rootView);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(flipOut);
        animatorSet.start();
    }



    @Override
    public void onDialogDismissed(Bundle args) {
        mTriggerSelection = args.getString(TriggerPickerDialog.ARG_KEY);
        if (mTriggerSelection == null || mTriggerSelection.equals("")) {
            ((TextView)rootView.findViewById(R.id.trigger_selection)).setText(getResources().getString(R.string.default_trigger));
        } else {
            // GA
            mTrackerHost.send(getString(R.string.category_slipped), getString(R.string.action_slipped), mTriggerSelection);
            mTrackerHost.send(getString(R.string.category_slipped)+"|"+getString(R.string.action_slipped)+"|"+mTriggerSelection);
            ((TextView) rootView.findViewById(R.id.trigger_selection)).setText(mTriggerSelection);
        }
    }
}
