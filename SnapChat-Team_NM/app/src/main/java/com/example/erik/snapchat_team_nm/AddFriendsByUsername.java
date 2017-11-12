package com.example.erik.snapchat_team_nm;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Erik on 2016/10/9.
 */
public class AddFriendsByUsername extends Fragment {


    FriendRequest fr;
    PopupUser ppUser;
    ImageButton ibBack;
    Button btSearch;
    EditText etUsername;
    RelativeLayout rl;


    ListView listView;
    OptimizedUserAdapter adapter;
    List<User> users = new ArrayList<>();

//    Communicator communicator;
//
//    public interface Communicator{
//        public void backToAddFriends();
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.add_by_username, container, false);
        ibBack = (ImageButton)view.findViewById(R.id.ibBacktoAddFriends);
        btSearch = (Button)view.findViewById(R.id.btSearch);
        etUsername = (EditText)view.findViewById(R.id.etSearchUsername);
        rl = (RelativeLayout)view.findViewById(R.id.AddFriendByUsername);


        final DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String currentID = currentUser.getUid();
        final DatabaseReference mFriendListRef = mRootRef.child(currentID).child("friends");

        System.out.println(currentUser);//com.google.android.gms.internal.zzadh@494f695
        System.out.println(currentID);//2O3NPyR3ZQNVGgKeQm3SL0PrX4u1
        System.out.println(currentUser.getDisplayName());// null
        System.out.println(currentUser.getEmail());//111@1.com

        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRootRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        users.clear();
                        String Username = etUsername.getText().toString();
                        for (DataSnapshot child:dataSnapshot.getChildren()) {

                            if ((Username.equals(child.child("username").getValue().toString()))&&
                                    (!Username.equals(currentUser.getEmail()))){
                                String userName = Username;
                                String userID = child.getKey();
                                User user = new User (userName,userID);
                                users.add(user);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        mRootRef.removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });


        listView = (ListView)view.findViewById(R.id.UsernameList);
        adapter = new OptimizedUserAdapter(getActivity(), R.layout.single_user_layout,users);
        //set the adapter for your ListView
        listView.setAdapter(adapter);

        //((mRootRef.child(currentID).child("friends").child(users.get(position).getUserID()) != currentID)
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                mFriendListRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mFriendListRef.removeEventListener(this);

                        if((dataSnapshot.child(users.get(position).getUserID()).getValue() == null)){
                            //pop up mini view for the selected user
//                                System.out.println("aaaaaaaaaaaa"+getContext());
                            ppUser = new PopupUser(getContext());

                            ppUser.tvUsername.setText(users.get(position).getUserName());
                            rl.addView(ppUser);

                            RelativeLayout reLy = ppUser;
                            reLy.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View view, MotionEvent motionEvent) {
                                    return true;
                                }
                            });

                            ppUser.btAdd.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    //send the add friend request to the user
                                    String requestName = currentUser.getEmail();
                                    String requestID = currentID;
                                    mRootRef.child(users.get(position).userID).child("addedMe").
                                            child(requestID).child("username").setValue(requestName);
                                    Toast.makeText(getActivity(), R.string.add_success, Toast.LENGTH_SHORT).show();

                                }
                            });
                        }else{
                            //pop up mini view for the selected user
                            ppUser = new PopupUser(getContext());
                            ppUser.tvUsername.setText(users.get(position).getUserName());
                            rl.addView(ppUser);

                            ppUser.btAdd.setVisibility(View.INVISIBLE);

                            Toast.makeText(getActivity(), "This person is already your friend!", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                adapter.notifyDataSetChanged();
            }
        });

        return view;

    }


    public class OptimizedUserAdapter extends ArrayAdapter<User> {
        private int resourceId;
        public OptimizedUserAdapter(Context context, int resource, List<User> objects) {
            super(context, resource, objects);
            this.resourceId =resource;
        }
        public View getView(int position, View convertView, ViewGroup parent){
            // get one object content
            User user = getItem(position);
            System.out.println("@ "+user.getUserName());
            ViewHolder viewHolder;
            View view;
            if (convertView==null){
                //create a new ViewHolder
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(getContext()).inflate(resourceId,null);
                // store instance components into the viewHolder
                viewHolder.imageView = (ImageView) view.findViewById(R.id.ivMessageIcon);
                viewHolder.tvUsername =(TextView)view.findViewById(R.id.tvUsername);
                viewHolder.tvID = (TextView)view.findViewById(R.id.tvAddDate);
                // store the viewHolder into the view
                view.setTag(viewHolder);
            }else {
                view = convertView;
                //recover the viewHolder that store the previous instance components again
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.tvUsername.setText(user.getUserName());
            viewHolder.tvID.setText(user.getUserID());
            return view;
        }
        // Define a ViewHolder class
        class ViewHolder{
            ImageView imageView;
            TextView tvUsername;
            TextView tvID;
        }
    }

}
