package com.example.erik.snapchat_team_nm;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
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

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

/**
 * Created by Erik on 2016/10/13.
 */
public class GetLocationActivity extends AppCompatActivity{
    Button btFind;
    Button btSend;
    TextView tv;
    RelativeLayout rl;
    LocationManager locationManager;
    LocationListener locationListener;
    Double longitude;
    Double latitude;
    PopupUser ppUser;
    ImageButton ib;

    ListView listView;
    OptimizedUserAdapter adapter;
    List<User> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getlocation);

        final DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String currentID = currentUser.getUid();
        final DatabaseReference mFriendListRef = mRootRef.child(currentID).child("friends");

        rl = (RelativeLayout)findViewById(R.id.GPSlocation);
        btSend = (Button)findViewById(R.id.btSendlocation);
        btFind = (Button)findViewById(R.id.btFindNearby);
        tv = (TextView)findViewById(R.id.tv);
        ib = (ImageButton)findViewById(R.id.ibBack);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            boolean change=true;
            @Override

            public void onLocationChanged(Location location) {
                tv.append("\n "+"jingdu "+location.getLongitude()+" weidu "+location.getLatitude());
                longitude = location.getLongitude();
                latitude = location.getLatitude();

                System.out.println("lo"+longitude);
                System.out.println("la"+latitude);
                if(change)
                {
                    mRootRef.child(currentUser.getUid()).child("location").setValue(longitude.toString()+","+latitude.toString());
                    change=false;
                }


            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }

        };


        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("aaaaaaaa");
                locationManager.requestLocationUpdates("gps", 2000, 0, locationListener);
                System.out.println("bbbbbbb");

            }

        });

        btFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRootRef.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        users.clear();
                        String Username = currentUser.getEmail();
                        for (DataSnapshot child:dataSnapshot.getChildren()) {
                            //filter out self info
                            if(!(Username.equals(child.child("username").getValue().toString()))){
                                //System.out.println("@ "+child.child("userName").getValue().toString());
                                System.out.println("AAAAAAAAA:"+child.child("username").getValue());
                                String userName = child.child("username").getValue().toString();
                                String userID = child.getKey();
                                String location = child.child("location").getValue().toString();

                                //covert strings to double
                                String[] ar=location.split(",");
                                double lo=abs(Double.parseDouble(ar[0]));
                                double la=abs(Double.parseDouble(ar[1]));

                                //calculate distance between users (meters)
                                System.out.println("aaa "+ lo);
                                System.out.println("aaa "+ longitude);
                                System.out.println("aaa "+ la);
                                System.out.println("aaa "+ latitude);
                                double distance = abs(lo-abs(longitude))*100000 + abs(la-abs(latitude))*111000;

                                //put the nearby users in the list
                                if(distance <= 5000){
                                    User user = new User (userName,location,userID);
                                    users.add(user);
                                }
                            }

                        }
                        if(users.size() == 0){
                            Toast.makeText(GetLocationActivity.this, "This person is already your friend!", Toast.LENGTH_SHORT).show();
                        }
                        adapter.notifyDataSetChanged();
//                        mRootRef.removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        listView = (ListView)findViewById(R.id.NearbyList);
        adapter = new OptimizedUserAdapter(GetLocationActivity.this, R.layout.single_user_layout,users);
        //set the adapter for your ListView
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                mFriendListRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mFriendListRef.removeEventListener(this);

                        if((dataSnapshot.child(users.get(position).getUserID()).getValue() == null)){
                            //pop up mini view for the selected user
//                                System.out.println("aaaaaaaaaaaa"+getContext());
                            ppUser = new PopupUser(getBaseContext());

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
                                    Toast.makeText(GetLocationActivity.this, R.string.add_success, Toast.LENGTH_SHORT).show();

                                }
                            });
                        }else{
                            //pop up mini view for the selected user
                            ppUser = new PopupUser(getBaseContext());
                            ppUser.tvUsername.setText(users.get(position).getUserName());
                            rl.addView(ppUser);

                            ppUser.btAdd.setVisibility(View.INVISIBLE);

                            Toast.makeText(GetLocationActivity.this, "This person is already your friend!", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                adapter.notifyDataSetChanged();
            }
        });

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
