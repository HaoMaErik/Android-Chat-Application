package com.example.erik.snapchat_team_nm;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Erik on 2016/10/9.
 */
public class PopupUser extends RelativeLayout {
    ImageView ivUser;
    TextView tvUsername;
    Button btAdd;

    public PopupUser(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.add_friend_interface,this);
        ivUser = (ImageView)findViewById(R.id.ivUserImage);
        tvUsername = (TextView)findViewById(R.id.tvUser);
        btAdd = (Button)findViewById(R.id.btAdd);
    }

}
