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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Erik on 2016/10/10.
 */
public class AddedMeFragment extends Fragment {
    ImageButton ibBack;
    ListView listView;

    OptimizedUserAdapter adapter;
    List<FriendRequest> requests = new ArrayList<>();

    RelativeLayout rl;
    PopupAddUser ppAddingUser;


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.added_me, container, false);
        ibBack = (ImageButton)view.findViewById(R.id.ibBacktoFriends);
        rl = (RelativeLayout)view.findViewById(R.id.AddedMe);

        final DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String currentID = currentUser.getUid();
        final DatabaseReference mAddedMeListRef = mRootRef.child(currentID).child("addedMe");
        final DatabaseReference mFriendListRef = mRootRef.child(currentID).child("friends");

        mAddedMeListRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                requests.clear();
                for (DataSnapshot child:dataSnapshot.getChildren()) {

                    //System.out.println("@ "+child.child("userName").getValue().toString());
                    String requestName = child.child("username").getValue().toString();
                    String requestID = child.getKey();

                    FriendRequest fr = new FriendRequest(requestID,requestName);
                    requests.add(fr);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView = (ListView)view.findViewById(R.id.FriendRequestList);
        adapter = new OptimizedUserAdapter(getActivity(), R.layout.single_user_layout,requests);
        //set the adapter for your ListView
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                final FriendRequest frr = requests.get(position);

                    //pop up mini view for the selected user
                    ppAddingUser = new PopupAddUser(getContext());
                    ppAddingUser.tvUsername.setText(frr.getRequestName());
                    rl.addView(ppAddingUser);

                    RelativeLayout reLy = ppAddingUser;
                    reLy.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            return true;
                        }
                    });

                    ppAddingUser.btAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //add friend to the friend list
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            sdf.setTimeZone(TimeZone.getTimeZone("GMT+11:00"));

                            String addedDate = sdf.format(new Date());
                            String userName = frr.getRequestName();


                            HashMap<String, Object> result = new HashMap<>();
                            result.put("addedDate",addedDate);
                            result.put("userName",userName);

                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put(frr.getRequestID(),result);

                            //add that person's info to my friend list
                            mFriendListRef.updateChildren(childUpdates);

                            String addedDate1 = sdf.format(new Date());
                            String userName1 = currentUser.getEmail();
                            HashMap<String, Object> result1 = new HashMap<>();
                            result1.put("addedDate",addedDate1);
                            result1.put("userName",userName1);

                            Map<String, Object> childUpdates1 = new HashMap<>();
                            childUpdates1.put(currentID,result1);

                            //add my info to that person's friend list
                            mRootRef.child(frr.getRequestID()).child("friends").updateChildren(childUpdates1);

                            Toast.makeText(getActivity(), "Adding Successful!", Toast.LENGTH_SHORT).show();

                            //delete this request from the request list
                            mAddedMeListRef.child(frr.getRequestID()).removeValue();


                        }
                    });

                adapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    public class OptimizedUserAdapter extends ArrayAdapter<FriendRequest> {
        private int resourceId;
        public OptimizedUserAdapter(Context context, int resource, List<FriendRequest> objects) {
            super(context, resource, objects);
            this.resourceId =resource;
        }
        public View getView(int position, View convertView, ViewGroup parent){
            // get one object content
            FriendRequest fr = getItem(position);
            System.out.println("@ "+fr.getRequestName());
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
            viewHolder.tvUsername.setText(fr.getRequestName());
            viewHolder.tvID.setText(fr.getRequestID());
            return view;
        }
        // Define a ViewHolder class
        class ViewHolder{
            ImageView imageView;
            TextView tvUsername;
            TextView tvID;
        }
    }

    public class PopupAddUser extends RelativeLayout {
        ImageView ivUser;
        TextView tvUsername;
        Button btAdd;

        public PopupAddUser(Context context) {
            super(context);
            LayoutInflater.from(context).inflate(R.layout.add_friend_confirm,this);
            ivUser = (ImageView)findViewById(R.id.ivAddedUserImage);
            tvUsername = (TextView)findViewById(R.id.tvAddedUser);
            btAdd = (Button)findViewById(R.id.btAdded);
        }

    }
}
