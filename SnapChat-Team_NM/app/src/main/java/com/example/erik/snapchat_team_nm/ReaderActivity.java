package com.example.erik.snapchat_team_nm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ReaderActivity extends AppCompatActivity {
    RelativeLayout rl;
    PopupUser ppUser;
    String friendID="aaaaaaaaa";
    static final int QR_READER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        rl = (RelativeLayout)findViewById(R.id.readerRL);


        final Activity activity = this;

        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scanning SnapCode...");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

            System.out.println("QRQRQRQR");
            final DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

            final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            final String currentID = currentUser.getUid();
            final DatabaseReference mFriendListRef = mRootRef.child(currentID).child("friends");
            //final String[] friendID = new String[1];

            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if(result != null){
                if(result.getContents()==null){

                    Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_LONG).show();
                }
                else {
                    final String scanResult = result.getContents();

                    mRootRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
//                        mRootRef.removeEventListener(this);
                            for (DataSnapshot child:dataSnapshot.getChildren()){
                                if(child.child("username").getValue().equals(scanResult)){
                                    friendID = child.getKey().toString();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    mFriendListRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //mFriendListRef.removeEventListener(this);

                            //verify if the user already in your friend list
                            if(dataSnapshot.child(friendID).getValue() == null){
                                //verify if you scanning your own snapcode, which is not allowed
                                if(!scanResult.equals(currentUser.getEmail())){
                                    ppUser = new PopupUser(getBaseContext());

                                    ppUser.tvUsername.setText(scanResult);
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

                                            mRootRef.child(friendID).child("addedMe").
                                                    child(requestID).child("username").setValue(requestName);

                                            Toast.makeText(ReaderActivity.this, "Request sent...",Toast.LENGTH_LONG).show();

                                        }
                                    });
                                }else{
                                    //pop up mini view for the selected user
                                    ppUser = new PopupUser(getBaseContext());
                                    ppUser.tvUsername.setText(scanResult + "->This is YOU!");
                                    rl.addView(ppUser);

                                    ppUser.btAdd.setVisibility(View.INVISIBLE);

                                    Toast.makeText(ReaderActivity.this, "You cannot add yourself as friend!!",Toast.LENGTH_LONG).show();
                                }


                            }else{
                                //pop up mini view for the selected user
                                ppUser = new PopupUser(getBaseContext());
                                ppUser.tvUsername.setText(scanResult);
                                rl.addView(ppUser);

                                ppUser.btAdd.setVisibility(View.INVISIBLE);
                                Toast.makeText(ReaderActivity.this, "This person is already your friend!!!",Toast.LENGTH_LONG).show();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    //Toast.makeText(this, result.getContents(),Toast.LENGTH_LONG).show();
                }
            }
            else {
                super.onActivityResult(requestCode, resultCode, data);
            }


    }
}
