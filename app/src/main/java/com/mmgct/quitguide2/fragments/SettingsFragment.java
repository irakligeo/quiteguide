package com.mmgct.quitguide2.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.mmgct.quitguide2.IntroActivity;
import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.fragments.dialogs.DecisionDialog;
import com.mmgct.quitguide2.fragments.listeners.DialogDismissListener;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.Profile;


/**
 * Created by 35527 on 12/2/2015.
 */
public class SettingsFragment extends BaseFragment implements DialogDismissListener, View.OnClickListener {
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        setHeaderTitles();
        bindListeners();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String participantId = DbManager.getInstance().getProfile().getParticipantId();
        if (participantId != null && !participantId.equals("null")) {
            ((EditText)rootView.findViewById(R.id.edt_participant_id)).setText(participantId);
        }
        // Set version number
        String versionName = null;
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            versionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "Unknown Version";
            e.printStackTrace();
        }
        ((TextView)rootView.findViewById(R.id.txt_version_number)).setText(versionName);
    }



    @Override
    public void onStop() {
        super.onStop();
        EditText edtParticipantID = (EditText) rootView.findViewById(R.id.edt_participant_id);
        String participantId = null;
        Profile profile = DbManager.getInstance().getProfile();
        if (edtParticipantID != null && !(participantId = edtParticipantID.getText().toString()).trim().equals("")){
            if (!participantId.equals(profile.getParticipantId())) {
                mTrackerHost.send(getString(R.string.category_respondent_participant_id), getString(R.string.action_registration), participantId);
                mTrackerHost.send(getString(R.string.category_respondent_participant_id)+"|"+getString(R.string.action_registration)+"|"+participantId);
                profile.setParticipantId(participantId);
                DbManager.getInstance().updateprofile(profile);
            }
        } else {
            profile.setParticipantId("null");
            DbManager.getInstance().updateprofile(profile);
        }
    }

    private void bindListeners() {
        rootView.findViewById(R.id.lyt_settings_reset_profile).setOnClickListener(this);
        rootView.findViewById(R.id.lyt_settings_send_feedback).setOnClickListener(this);
        rootView.findViewById(R.id.lyt_settings_about).setOnClickListener(this);

        ((EditText)rootView.findViewById(R.id.edt_participant_id)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Update custom dimension
                mTrackerHost.getTracker().set("&cd1", s.toString().trim().equals("") ? "null" : s.toString());
            }
        });

        /*Switch notificationToggle = (Switch) rootView.findViewById(R.id.switch_settings_notifications);
        notificationToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                State state = DbManager.getInstance().getState();
                state.setAllowNotification(isChecked);
                DbManager.getInstance().updateState(state);
            }
        });
        notificationToggle.setChecked(DbManager.getInstance().getState().isAllowNotification());*/

        rootView.findViewById(R.id.lyt_settings_notification).setOnClickListener(this);
    }

    private void setHeaderTitles() {
        View settingsHeader = rootView.findViewById(R.id.header_settings_settings);
        View notificationsHeader = rootView.findViewById(R.id.header_settings_notifications);
        View applicationHeader = rootView.findViewById(R.id.header_settings_application);

        ((TextView)settingsHeader.findViewById(R.id.header_title)).setText(getResources().getString(R.string.settings));
        ((TextView)notificationsHeader.findViewById(R.id.header_title)).setText(getResources().getString(R.string.notifications));
        ((TextView)applicationHeader.findViewById(R.id.header_title)).setText(getResources().getString(R.string.application));
    }

    @Override
    public void onDialogDismissed(Bundle args) {
        if (args != null) {
            String choice = args.getString(DecisionDialog.DECISION_KEY, "null");
            if (choice.equals("yes")) {
                DbManager DbInstance = DbManager.getInstance();
                DbInstance.getState().setIntroMode(true);
                DbInstance.updateState(DbInstance.getState());
                DbInstance.reset();
                Intent intent = new Intent(getActivity().getApplicationContext(), IntroActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
    }

    @Override
    public synchronized void onClick(final View v) {
        // No double tapping
        v.setEnabled(false);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                v.setEnabled(true);
            }
        }, 250);

        switch(v.getId()) {
            case(R.id.lyt_settings_reset_profile):
                DialogFragment df = DecisionDialog.newInstance("პროფილის წაშლა\n\nყველა შენი ისტორია, ჩანაწერი ჟურნალში, ამ აპლიკაციით გადაღებული სურათები წაიშლება. დარწმუნებული ხარ რომ გინდა რომ წაშალო შენი პროფილი?");
                df.setTargetFragment(this, 0);
                df.show(getFragmentManager(), "");
                break;
            case(R.id.lyt_settings_send_feedback):
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", getResources().getString(R.string.email_addr_send_feedback), null));
                String versionName = null;
                try {
                    PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                    versionName = pInfo.versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    versionName = "Unknown Version";
                    e.printStackTrace();
                }

                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "QuitGuide Android Version " + versionName + " Feedback");
                startActivity(Intent.createChooser(emailIntent, "Send Feedback"));
                break;
            case(R.id.lyt_settings_about):
                AboutFragment f = new AboutFragment();
                f.setTargetFragment(this, 0);
                Animator animator = AnimatorInflater.loadAnimator(getActivity(), R.animator.oa_slide_left_out);
                animator.setTarget(rootView);
                animator.start();
                mCallbacks.onAddNavigationAction(f, "", true);
                break;
            case(R.id.lyt_settings_notification):
                Fragment frag = new ManageNotificationsFragment();
                Bundle args = new Bundle();
                args.putString(ManageNotificationsFragment.SEQUENCE_EXTRA_KEY, ManageNotificationsFragment.SEQUENCE_FROM_SETTINGS);
                frag.setArguments(args);
                mCallbacks.onAnimatedNavigationAction(frag,
                        ManageNotificationsFragment.TAG,
                        R.animator.oa_slide_left_in,
                        R.animator.oa_slide_left_out,
                        R.animator.oa_slide_right_in,
                        R.animator.oa_slide_right_out,
                        true);
                break;

        }
    }
}
