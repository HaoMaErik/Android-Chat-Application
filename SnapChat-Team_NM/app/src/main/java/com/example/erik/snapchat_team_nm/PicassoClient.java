package com.example.erik.snapchat_team_nm;

/**
 * Created by Erik on 2016/10/17.
 */
import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class PicassoClient {
    public static void downloadImage(Context c, String url, ImageView imageView){
        if (url!=null && url.length()>0){
            Picasso.with(c).load(url).fit().tag(c).placeholder(R.drawable.snapchat_userenter).into(imageView);
        }else {
            Picasso.with(c).load(R.drawable.snapchat_userenter).fit().tag(c).into(imageView);
        }
    }
}
