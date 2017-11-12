package com.example.erik.snapchat_team_nm;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Erik on 2016/10/12.
 */
public class ProfileFragment extends Fragment {
    TextView tvName;
    Button btAddMe;
    Button btAddFriend;
    Button btMyFriendList;
    Button btCamera;
    Button btLt;

    Communicator communicator;

    public interface Communicator{
        public void toAddedMe();
        public void toAddFriend();
        public void switchToMyFriends();
        public void signOut();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);
        btLt = (Button)view.findViewById(R.id.btLogOut);

        btLt.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                communicator.signOut();

            }
        });
        tvName = (TextView)view.findViewById(R.id.btpName);
        btAddMe = (Button)view.findViewById(R.id.btAddme);
        btAddFriend = (Button)view.findViewById(R.id.btAddfriend);
        btMyFriendList = (Button)view.findViewById(R.id.btMyfriends);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String Username = currentUser.getEmail();
        tvName.setText(Username);

        btAddMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                communicator.toAddedMe();
            }
        });

        btAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                communicator.toAddFriend();
            }
        });

        btMyFriendList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                communicator.switchToMyFriends();
            }
        });

        //stop the moving of viewPage
        FrameLayout fl = (FrameLayout) view.findViewById(R.id.profileFrame);
        fl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });


        return view;

    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            communicator = (Communicator) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString()+" must implement communicator interface");
        }
    }
}
