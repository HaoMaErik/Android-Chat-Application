package com.example.erik.snapchat_team_nm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Erik on 2016/10/7.
 */
public class AddFriendsFragment extends Fragment{
    Button btAddUsername;
    Button btAddSnapcode;
    Button btAddNear;
    Button btQR;
    ImageButton ibGoBack;

    Communicator communicator;

    public interface Communicator{
        public void backToUserProfile();
        public void switchToUsername();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.addfriends, container, false);
        btAddUsername = (Button)view.findViewById(R.id.addByUsername);
        btAddSnapcode = (Button)view.findViewById(R.id.addbySnapcode);
        btAddNear = (Button)view.findViewById(R.id.addNearby);
        btQR = (Button)view.findViewById(R.id.btMyQRcode);
        ibGoBack = (ImageButton)view.findViewById(R.id.ibBacktoProfile);

        ibGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                communicator.backToUserProfile();
            }
        });

        btAddUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                communicator.switchToUsername();
            }
        });
        btAddSnapcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reader = new Intent(getContext(),ReaderActivity.class);
                getContext().startActivity(reader);
            }
        });
        btQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                String username = currentUser.getEmail();
                //pass variables from fragment to activity
                Intent generator = new Intent(getContext(),QRGeneratorActivity.class);
                generator.putExtra("username",username);
                getContext().startActivity(generator);
            }
        });
        btAddNear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent generator = new Intent(getContext(),GetLocationActivity.class);
                getContext().startActivity(generator);
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
