package com.example.erik.snapchat_team_nm;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.os.CountDownTimer;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Erik on 2016/10/15.
 */
public class SingleStory extends Fragment {

    Communicator communicator;

    ImageButton back;

    StoriesData user;

    private MyCountDownTimer cdt;

    TextView timer;

    public interface Communicator{
        public void backToStory();
    }

    public SingleStory (StoriesData user){
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.single_story_show, container, false);

        //stop the moving of viewPage
        FrameLayout flstories = (FrameLayout)view.findViewById(R.id.singlestoryshow);

        cdt = new MyCountDownTimer(5000, 1000);
        cdt.start();

        flstories.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        ImageView ImageviewStories = (ImageView)view.findViewById(R.id.imageViewsinglestory);

        Picasso.with(getContext()).load(user.getContent()).into(ImageviewStories);

        back = (ImageButton)view.findViewById(R.id.ibBacktoStory);

        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                communicator.backToStory();

            }

        });

        timer = (TextView)view.findViewById(R.id.counttimer);


        return view;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            communicator = (SingleStory.Communicator) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString()+" must implement communicator interface");
        }
    }

    //timer action
    class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        public void onFinish() {

            communicator.backToStory();

        }
        public void onTick(long millisUntilFinished) {
            timer.setText("last(" + millisUntilFinished / 1000 + "s)");
        }
    }


}
