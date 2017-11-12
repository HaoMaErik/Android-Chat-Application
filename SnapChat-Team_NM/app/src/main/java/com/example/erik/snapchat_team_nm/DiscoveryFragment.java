package com.example.erik.snapchat_team_nm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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
 * Created by Erik on 2016/9/27.
 */
public class DiscoveryFragment extends Fragment {
    TextView textView1;
    TextView textView2;
    TextView textView3;
    TextView textView4;
    TextView textView5;
    TextView textView6;

    ImageView imageView01;
    ImageView imageView02;
    ImageView imageView03;
    ImageView imageView04;
    ImageView imageView05;
    ImageView imageView06;

    Communicator communicator;

    List<Discovery> discoveryList = new ArrayList<>();

    public interface Communicator{
        public void switchToStory();
    }

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String currentID = currentUser.getUid();
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_discovery, container, false);



        //DatabaseReference mInterestedTopicRef = mRootRef.child(currentID).child("interested topic");
        mRootRef.addValueEventListener(new ValueEventListener() {
            int compareValue = -1;
            String maxKey = "Travel";
            String secondMax = "Sport";
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(currentID).child("interested topic").getValue() != null){
                    for (DataSnapshot child:dataSnapshot.child(currentID).child("interested topic").getChildren()){
                        if(Integer.parseInt(child.getValue().toString()) > compareValue){
                            compareValue = Integer.parseInt(child.getValue().toString());
                            maxKey = child.getKey();
                        }
                    }
                    for (DataSnapshot child:dataSnapshot.child(currentID).child("interested topic").getChildren()){
                        if((Integer.parseInt(child.getValue().toString()) > compareValue)&&(!(child.getKey().equals(maxKey)))){
                            compareValue = Integer.parseInt(child.getValue().toString());
                            secondMax = child.getKey();
                        }
                    }
                }

                int i=0;
                //3 data from max count value
                for (DataSnapshot child:dataSnapshot.child("Discovery").child("category").child(maxKey).getChildren()){
                    String coverUrl = child.child("coverImage").getValue().toString();
                    String name = child.child("name").getValue().toString();
                    String url = child.child("url").getValue().toString();
                    String type = maxKey;

                    Discovery d = new Discovery(coverUrl,name,url,type);
                    discoveryList.add(d);
                    i++;
                    if (i==3){break;}
                }
                //2 data from second max count value
                for(DataSnapshot child:dataSnapshot.child("Discovery").child("category").child(secondMax).getChildren()){
                    String coverUrl = child.child("coverImage").getValue().toString();
                    String name = child.child("name").getValue().toString();
                    String url = child.child("url").getValue().toString();
                    String type = secondMax;

                    Discovery d = new Discovery(coverUrl,name,url,type);
                    discoveryList.add(d);
                    i++;
                    if (i==2){break;}
                }
                //additional 1 data
                String coverUrl = dataSnapshot.child("Discovery").child("category").child("World").child("W3").child("coverImage").getValue().toString();
                String name = dataSnapshot.child("Discovery").child("category").child("World").child("W3").child("name").getValue().toString();
                String url = dataSnapshot.child("Discovery").child("category").child("World").child("W3").child("url").getValue().toString();
                String type = "World";

                Discovery d = new Discovery(coverUrl,name,url,type);
                discoveryList.add(d);

                System.out.println("D "+discoveryList);
                textView1 = (TextView)view.findViewById(R.id.textView3);
                textView1.setText(discoveryList.get(0).getName());
                imageView01 = (ImageView)view.findViewById(R.id.imageView01);
                PicassoClient.downloadImage(getContext(),discoveryList.get(0).getCoverImageUrl(),imageView01);
                imageView01.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mRootRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(!dataSnapshot.child(currentID).child("interested topic").getValue().toString().equals(null)){
                                    int count = Integer.parseInt(dataSnapshot.child(currentID).
                                            child("interested topic").child(discoveryList.get(0).getType()).getValue().toString());
                                    count++;
                                }else{
                                    mRootRef.child(currentID).child("interested topic").child(discoveryList.get(0).getType()).setValue(1);
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        Intent intent = new Intent(getActivity(), DiscoveryDetail.class);

                        intent.putExtra("url", discoveryList.get(0).getUrl());

                        startActivity(intent);
                    }
                });

                textView2 = (TextView)view.findViewById(R.id.textView4);
                textView2.setText(discoveryList.get(1).getName());
                imageView02 = (ImageView)view.findViewById(R.id.imageView02);
                PicassoClient.downloadImage(getContext(),discoveryList.get(1).getCoverImageUrl(),imageView02);
                imageView02.setOnClickListener(new View.OnClickListener(){

                    public void onClick(View v){
                        mRootRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(!dataSnapshot.child(currentID).child("interested topic").getValue().toString().equals(null)){
                                    int count = Integer.parseInt(dataSnapshot.child(currentID).
                                            child("interested topic").child(discoveryList.get(1).getType()).getValue().toString());
                                    count++;
                                }else{
                                    mRootRef.child(currentID).child("interested topic").child(discoveryList.get(1).getType()).setValue(1);
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        Intent intent = new Intent(getActivity(), DiscoveryDetail.class);
                        intent.putExtra("url", discoveryList.get(1).getUrl());
                        startActivity(intent);
                    }
                });

                textView3 = (TextView)view.findViewById(R.id.textView5);
                textView3.setText(discoveryList.get(2).getName());
                imageView03 = (ImageView)view.findViewById(R.id.imageView03);
                PicassoClient.downloadImage(getContext(),discoveryList.get(2).getCoverImageUrl(),imageView03);
                imageView03.setOnClickListener(new View.OnClickListener(){

                    public void onClick(View v){
                        mRootRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(!dataSnapshot.child(currentID).child("interested topic").getValue().toString().equals(null)){
                                    int count = Integer.parseInt(dataSnapshot.child(currentID).
                                            child("interested topic").child(discoveryList.get(2).getType()).getValue().toString());
                                    count++;
                                }else{
                                    mRootRef.child(currentID).child("interested topic").child(discoveryList.get(2).getType()).setValue(1);
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        Intent intent = new Intent(getActivity(), DiscoveryDetail.class);
                        intent.putExtra("url", discoveryList.get(2).getUrl());
                        startActivity(intent);
                    }
                });

                textView4 = (TextView)view.findViewById(R.id.textView6);
                textView4.setText(discoveryList.get(3).getName());
                imageView04 = (ImageView)view.findViewById(R.id.imageView04);
                PicassoClient.downloadImage(getContext(),discoveryList.get(3).getCoverImageUrl(),imageView04);
                imageView04.setOnClickListener(new View.OnClickListener(){

                    public void onClick(View v){
                        mRootRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(!dataSnapshot.child(currentID).child("interested topic").getValue().toString().equals(null)){
                                    int count = Integer.parseInt(dataSnapshot.child(currentID).
                                            child("interested topic").child(discoveryList.get(3).getType()).getValue().toString());
                                    count++;
                                }else{
                                    mRootRef.child(currentID).child("interested topic").child(discoveryList.get(3).getType()).setValue(1);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        Intent intent = new Intent(getActivity(), DiscoveryDetail.class);
                        intent.putExtra("url", discoveryList.get(3).getUrl());
                        startActivity(intent);
                    }
                });

                textView5 = (TextView)view.findViewById(R.id.textView7);
                textView5.setText(discoveryList.get(4).getName());
                imageView05 = (ImageView)view.findViewById(R.id.imageView05);
                PicassoClient.downloadImage(getContext(),discoveryList.get(4).getCoverImageUrl(),imageView05);
                imageView05.setOnClickListener(new View.OnClickListener(){

                    public void onClick(View v){
                        mRootRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(!dataSnapshot.child(currentID).child("interested topic").getValue().toString().equals(null)){
                                    int count = Integer.parseInt(dataSnapshot.child(currentID).
                                            child("interested topic").child(discoveryList.get(4).getType()).getValue().toString());
                                    count++;
                                }else{
                                    mRootRef.child(currentID).child("interested topic").child(discoveryList.get(4).getType()).setValue(1);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        Intent intent = new Intent(getActivity(), DiscoveryDetail.class);
                        intent.putExtra("url", discoveryList.get(4).getUrl());
                        startActivity(intent);
                    }
                });

                textView6 = (TextView)view.findViewById(R.id.textView9);
                textView6.setText(discoveryList.get(5).getName());
                imageView06 = (ImageView)view.findViewById(R.id.imageView06);
                PicassoClient.downloadImage(getContext(),discoveryList.get(5).getCoverImageUrl(),imageView06);
                imageView06.setOnClickListener(new View.OnClickListener(){

                    public void onClick(View v){
                        mRootRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(!dataSnapshot.child(currentID).child("interested topic").getValue().toString().equals(null)){
                                    int count = Integer.parseInt(dataSnapshot.child(currentID).
                                            child("interested topic").child(discoveryList.get(5).getType()).getValue().toString());
                                    count++;
                                }else{
                                    mRootRef.child(currentID).child("interested topic").child(discoveryList.get(5).getType()).setValue(1);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        Intent intent = new Intent(getActivity(), DiscoveryDetail.class);
                        intent.putExtra("url", discoveryList.get(5).getUrl());
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ImageButton imagebutton = (ImageButton)view.findViewById(R.id.imageButton);

        imagebutton.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){

                communicator.switchToStory();
            }
        });


        return view;

    }

    public void onAttach (Activity activity){
        super.onAttach(activity);
        try {
            communicator=(Communicator)activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString()+"must implement communicator infterface");
        }
    }
}
