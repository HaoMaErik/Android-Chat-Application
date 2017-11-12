package com.example.erik.snapchat_team_nm;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.sql.SQLOutput;

/**
 * Created by Erik on 2016/9/27.
 */
public class EnterFragment extends Fragment{
    ImageView image;
    Button btLg;
    Button btRg;
    //FragmentManager manager = getFragmentManager();
    Communicator communicator;

    public interface Communicator{
        public void login();
        public void register();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_enter, container, false);
        image = (ImageView) view.findViewById(R.id.imageView);

        btLg = (Button) view.findViewById(R.id.login);
        btLg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                communicator.login();
            }
        });

        btRg = (Button) view.findViewById(R.id.register);
        btRg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                communicator.register();
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