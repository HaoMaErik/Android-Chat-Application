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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Erik on 2016/9/27.
 */
public class RegisterFragment extends Fragment {
    Button btRg;
    EditText etReEmail;
    EditText etRePassword;

    Communicator communicator;

    public interface Communicator{
        public void createAccount(String email, String password);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_register, container, false);
        btRg = (Button) view.findViewById(R.id.btRegister);
        etReEmail = (EditText) view.findViewById(R.id.edReEmail);
        etRePassword = (EditText) view.findViewById(R.id.edRePassword);
        btRg.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                communicator.createAccount(etReEmail.getText().toString(),etRePassword.getText().toString());
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
