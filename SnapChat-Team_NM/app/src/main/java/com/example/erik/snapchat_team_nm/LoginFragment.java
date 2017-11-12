package com.example.erik.snapchat_team_nm;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Erik on 2016/9/27.
 */
public class LoginFragment extends Fragment{
    TextView tvLg;
    EditText etEa;
    EditText etPw;
    Button btLg;

    Communicator communicator;

    public interface Communicator{
        public void signInAccount(String email, String password);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_login, container, false);
        tvLg = (TextView) view.findViewById(R.id.tvLogin);
        etEa = (EditText) view.findViewById(R.id.edEmail);
        etPw = (EditText) view.findViewById(R.id.edPassword);
        btLg = (Button) view.findViewById(R.id.btLogin);

        etEa.setText("111@1.com");
        etPw.setText("123456");

        btLg.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                communicator.signInAccount(etEa.getText().toString(),etPw.getText().toString());

            }
        });

        return view;
    }
    @Override
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
