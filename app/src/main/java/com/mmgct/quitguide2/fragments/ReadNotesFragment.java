package com.mmgct.quitguide2.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.Note;
import com.mmgct.quitguide2.models.PictureNote;
import com.mmgct.quitguide2.utils.Common;
import com.mmgct.quitguide2.utils.ImgUtils;
import java.io.File;
import java.io.IOException;

/**
 * Created by 35527 on 11/30/2015.
 */
public class ReadNotesFragment extends BaseFlipInFragment {

    private static final String TAG = "ReadNotesFragment";
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_read_notes, container, false);
        bindListeners();
        return rootView;
    }



    private void showRandomContent() {
        int rand = (int) (Math.random() * 2);
        if (rand == 0) {
            showRandNote();
        } else {
            showRandPic();
        }
    }

    private void showRandPic() {
        final ImageView imgRandomPic = (ImageView) rootView.findViewById(R.id.random_picture);
        imgRandomPic.setVisibility(View.VISIBLE);
        PictureNote picNote = DbManager.getInstance().getRandomPictureNote();
        if (picNote == null)
            return;
        String picturePath = picNote.getPicturePath();
        File picFile = new File(picturePath);
        if (!picFile.exists()){
            showRandomContent();
        } else {
            Glide.with(getActivity()).load(picturePath).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    if (imgRandomPic != null) {
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                        imgRandomPic.setLayoutParams(lp);
                        imgRandomPic.setBackground(getResources().getDrawable(R.drawable.white_frame));
                    }
                    return false;
                }
            }).into(imgRandomPic);

        }
    }

    private void showRandNote() {
        ImageView imgRandomPic = (ImageView) rootView.findViewById(R.id.random_picture);
        imgRandomPic.setVisibility(View.GONE);
        TextView txtRandomNote = (TextView) rootView.findViewById(R.id.random_tip);
        Note note = DbManager.getInstance().getRandomNote();
        if (note != null) {
            txtRandomNote.setText(note.getNote());
        }
    }

    private void bindListeners() {
        rootView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayBackAnimation = false;
                disableTargetAnimation();
                mCallbacks.popBackStack();
                mCallbacks.popBackStack();
                mCallbacks.popBackStack();
            }
        });

        rootView.findViewById(R.id.random_picture).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rootView.findViewById(R.id.random_picture).getViewTreeObserver().removeOnGlobalLayoutListener(this);
                showRandomContent();
            }
        });
    }

    private void disableTargetAnimation() {
        try {
            ((BaseFlipInFragment) getTargetFragment()).disableAnimations();
        } catch (ClassCastException e) {
            throw new ClassCastException("Target fragment must extend BaseFlipInFragment");
        }
    }
}
