package com.example.erik.snapchat_team_nm;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Erik on 2016/10/12.
 */
public class FriendsListFragment extends Fragment {
    ImageButton btBack;

    ListView listView;
    OptimizedUserAdapter adapter;
    List<User> users = new ArrayList<>();

    Communicator communicator;

    public interface Communicator{
        public void backToUserProfile();
        public void switchToSingleChat(User user);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_friends_list, container, false);
        btBack = (ImageButton)view.findViewById(R.id.ibBacktoUser);

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                communicator.backToUserProfile();
            }
        });

        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentID = currentUser.getUid();
        DatabaseReference mFriendListRef = mRootRef.child(currentID).child("friends");

        mFriendListRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users.clear();
                for (DataSnapshot child:dataSnapshot.getChildren()) {

                    //System.out.println("@ "+child.child("userName").getValue().toString());
                    String userName = child.child("userName").getValue().toString();
                    String addedDate = child.child("addedDate").getValue().toString();
                    String userID = child.getKey();

                    User user = new User (userName,addedDate,userID);
                    users.add(user);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        DatabaseReference mUserRef = mRootRef.child();

//        final User user1 = new User();

        listView = (ListView)view.findViewById(R.id.FriendListView);
        adapter = new OptimizedUserAdapter(getActivity(), R.layout.single_user_layout,users);
        //set the adapter for your ListView
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // remove the clicked item from arraylist
                communicator.switchToSingleChat(users.get(position));
                listView.setEnabled(false);
                //notifies that the data has been changed and any View reflecting the data set
                // should refresh itself
                adapter.notifyDataSetChanged();
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
                viewHolder.tvAddDate = (TextView)view.findViewById(R.id.tvAddDate);
                // store the viewHolder into the view
                view.setTag(viewHolder);
            }else {
                view = convertView;
                //recover the viewHolder that store the previous instance components again
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.tvUsername.setText(user.getUserName());
            viewHolder.tvAddDate.setText(user.getAddDate());
            return view;
        }
        // Define a ViewHolder class
        class ViewHolder{
            ImageView imageView;
            TextView tvUsername;
            TextView tvAddDate;
        }
    }
}
