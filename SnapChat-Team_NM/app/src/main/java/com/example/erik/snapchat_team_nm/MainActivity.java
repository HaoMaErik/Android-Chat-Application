package com.example.erik.snapchat_team_nm;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends FragmentActivity implements RegisterFragment.Communicator, LoginFragment.Communicator,
        ChatFragment.Communicator, EnterFragment.Communicator, SingleChatFragment.Communicator, AddFriendsFragment.Communicator,
        CameraFragment.Communicator, ProfileFragment.Communicator, FriendsListFragment.Communicator,DiscoveryFragment.Communicator,
        EditFragment.Communicator,StoryFragment.Communicator, SingleStory.Communicator,SendToFragment.Communicator, MemoryFragment.Communicator{
    static final int NUM_ITEMS = 4;

    FragmentManager manager = getSupportFragmentManager();

    MyAdapter mAdapter;
    ViewPager mPager;
    ProgressDialog mProgressDialog;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://snapchat-d3c6f.appspot.com");

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        NetworkListener nl = new NetworkListener(this);
        nl.startListening(MainActivity.this);

        if(user == null){
            setContentView(R.layout.activity_main);
            EnterFragment ef = new EnterFragment();
            FragmentTransaction t = manager.beginTransaction();
            t.replace(R.id.activityMain, ef);
            t.commit();

            mAuth = FirebaseAuth.getInstance();
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        // User is signed in
                        //Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    } else {
                        // User is signed out
                        //Log.d(TAG, "onAuthStateChanged:signed_out");
                    }

                }

            };

        }else {
            setContentView(R.layout.activity_main);
            mAdapter = new MyAdapter(getSupportFragmentManager());
            mPager = (ViewPager) findViewById(R.id.pages);
            mPager.setAdapter(mAdapter);
            mPager.setOffscreenPageLimit(4);
            mPager.setCurrentItem(1);
        }


    }

    @Override
    public void login(){
        // Define your fragment
        LoginFragment lf = new LoginFragment();

        // begin a transaction
        FragmentTransaction transaction = manager.beginTransaction();
        // call add() function to add this fragment into the view container
        transaction.replace(R.id.activityMain, lf);
        // add the transaction to the stack
        transaction.addToBackStack(null);
        // each transaction should call commit() function to confirm transaction
        transaction.commit();
    }

    @Override
    public void register(){
        // Define your fragment
        RegisterFragment rf = new RegisterFragment();
        // begin a transaction
        FragmentTransaction transaction = manager.beginTransaction();
        // call add() function to add this fragment into the view container
        transaction.replace(R.id.activityMain, rf);
        // add the transaction to the stack
        transaction.addToBackStack(null);
        // each transaction should call commit() function to confirm transaction
        transaction.commit();
    }

    @Override
    public void createAccount(String email, String password) {

        //Log.d(TAG, "createAccount:" + email);


        // [START create_user_with_email]

        mAuth.createUserWithEmailAndPassword(email, password)

                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override

                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds

                        // the auth state listener will be notified and logic to handle the

                        // signed in user can be handled in the listener.

                        if (!task.isSuccessful()) {

                            Toast.makeText(MainActivity.this, R.string.auth_failed,

                                    Toast.LENGTH_SHORT).show();

                        }else {
                            //put the register user into database
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            String currentID = currentUser.getUid();
                            DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
                            mRootRef.child(currentID).child("username").setValue(currentUser.getEmail());
                            mRootRef.child(currentID).child("location").setValue(100+","+30);
                            finish();
                            startActivity(getIntent());
                        }

                        // [START_EXCLUDE]

                        // [END_EXCLUDE]
                    }

                });

        // [END create_user_with_email]

    }

    @Override
    public void signInAccount(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

//                    mProgressDialog.setMessage("Login.....");
//                    mProgressDialog.show();
                //Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    //Log.w(TAG, "signInWithEmail:failed", task.getException());
                    Toast.makeText(MainActivity.this, R.string.auth_failed, Toast.LENGTH_SHORT).show();
                }else{
//                    mProgressDialog.dismiss();

                    Toast.makeText(MainActivity.this, R.string.auth_succ, Toast.LENGTH_SHORT).show();

                    finish();
                    startActivity(getIntent());
                }

                // ...
                }
            });
    }

    public void signOut(){
        FirebaseAuth.getInstance().signOut();
        //finish the current activity and restart it again
        finish();
        startActivity(getIntent());
    }

    public void switchToSingleChat(User user){
        // Define your fragment

        SingleChatFragment scf = new SingleChatFragment(user);

        // begin a transaction
        FragmentTransaction transaction = manager.beginTransaction();
        // call add() function to add this fragment into the view container
        transaction.replace(R.id.activityMain, scf);
        // add the transaction to the stack
        transaction.addToBackStack(null);
        // each transaction should call commit() function to confirm transaction
        transaction.commit();
    }

    @Override
    public void switchToDiscovery() {
        mPager.setCurrentItem(3);
    }

    @Override
    public void storiesToChat() {
        mPager.setCurrentItem(0);
    }

    public void switchToCamera(){
        mPager.setCurrentItem(1);
    }

    @Override
    public void photoDetails(Bitmap bitmap) {
        EditFragment ef = new EditFragment(bitmap);
        // begin a transaction
        FragmentTransaction transaction = manager.beginTransaction();
        // call replace function to replace fragment into the view container
        transaction.replace(R.id.activityMain, ef);
        // add the transaction to the stack
        transaction.addToBackStack(null);
        // each transaction should call commit() function to confirm transaction
        transaction.commit();
    }


    public void switchFromEditToCamera(){
        //
        onBackPressed();
    }

    public void updateMemory(){

        MemoryFragment mf = new MemoryFragment();

        // begin a transaction
        FragmentTransaction transaction = manager.beginTransaction();
        // call add() function to add this fragment into the view container
        transaction.replace(R.id.activityMain, mf);
        // add the transaction to the stack
        transaction.addToBackStack(null);
        // each transaction should call commit() function to confirm transaction
        transaction.commit();
    }

    @Override
    public void switchToSingleStory(StoriesData users) {

        // Define your fragment

        SingleStory ss = new SingleStory(users);

        // begin a transaction
        FragmentTransaction transaction = manager.beginTransaction();
        // call add() function to add this fragment into the view container
        transaction.replace(R.id.activityMain, ss);
        // add the transaction to the stack
        transaction.addToBackStack(null);
        // each transaction should call commit() function to confirm transaction
        transaction.commit();
    }

    @Override
    public void switchToSendPhoto(Bitmap bitmap) {
        // Define your fragment

        SendToFragment stf = new SendToFragment();
        stf.setBitmap(bitmap);

        // begin a transaction
        FragmentTransaction transaction = manager.beginTransaction();
        // call add() function to add this fragment into the view container
        transaction.replace(R.id.activityMain, stf);
        // add the transaction to the stack
        transaction.addToBackStack(null);
        // each transaction should call commit() function to confirm transaction
        transaction.commit();
    }


    public void switchToSimpleCamera(User user){
        // Define your fragment

        CameraFragment cf = new CameraFragment();
        cf.setUser(user);
        cf.useForSimpleCamera();

        // begin a transaction
        FragmentTransaction transaction = manager.beginTransaction();
        // call add() function to add this fragment into the view container
        transaction.replace(R.id.activityMain, cf);
        // add the transaction to the stack
        transaction.addToBackStack(null);
        // each transaction should call commit() function to confirm transaction
        transaction.commit();

    }


    public void switchToStory(){
        mPager.setCurrentItem(2);
    }

    public void showStory(){
        onBackPressed();
        onBackPressed();
        mPager.setCurrentItem(2);
    }


    public void switchFromMemoryToCamera() {
        onBackPressed();
    }

    public void switchToEdit(Bitmap photo){
        EditFragment ef = new EditFragment(photo);
        // begin a transaction
        FragmentTransaction transaction = manager.beginTransaction();
        // call replace function to replace fragment into the view container
        transaction.replace(R.id.activityMain, ef);
        // add the transaction to the stack
        transaction.addToBackStack(null);
        // each transaction should call commit() function to confirm transaction
        transaction.commit();
    }

    public void switchToEditWithUser(User user, Bitmap bitmap){
        EditFragment ef = new EditFragment(bitmap);
        ef.setUser(user);
        // begin a transaction
        FragmentTransaction transaction = manager.beginTransaction();
        // call add() function to add this fragment into the view container
        transaction.replace(R.id.activityMain, ef);
        // add the transaction to the stack
        transaction.addToBackStack(null);
        // each transaction should call commit() function to confirm transaction
        transaction.commit();

    }
    //from single chat to chat
    public void backToChat(){
        onBackPressed();
        ChatFragment chatFragment = (ChatFragment) mAdapter.getItem(0);
        chatFragment.listView.setEnabled(true);
    }

    //from camera to chat
    public void backChat(){
        mPager.setCurrentItem(0);
    }

    @Override
    public void switchToMemory() {
        MemoryFragment mem = new MemoryFragment();

        // begin a transaction
        FragmentTransaction transaction = manager.beginTransaction();
        // call add() function to add this fragment into the view container
        transaction.replace(R.id.activityMain, mem);
        // add the transaction to the stack
        transaction.addToBackStack(null);
        // each transaction should call commit() function to confirm transaction
        transaction.commit();
    }

    public void toUserProfile(){
        ProfileFragment pf = new ProfileFragment();

        // begin a transaction
        FragmentTransaction transaction = manager.beginTransaction();
        // call add() function to add this fragment into the view container
        transaction.replace(R.id.activityMain, pf);
        // add the transaction to the stack
        transaction.addToBackStack(null);
        // each transaction should call commit() function to confirm transaction
        transaction.commit();
    }

    public void toAddFriend(){
        AddFriendsFragment aff = new AddFriendsFragment();

        // begin a transaction
        FragmentTransaction transaction = manager.beginTransaction();
        // call add() function to add this fragment into the view container
        transaction.replace(R.id.activityMain, aff);
        // add the transaction to the stack
        transaction.addToBackStack(null);
        // each transaction should call commit() function to confirm transaction
        transaction.commit();
    }

    public void toAddedMe(){
        AddedMeFragment amf = new AddedMeFragment();

        // begin a transaction
        FragmentTransaction transaction = manager.beginTransaction();
        // call add() function to add this fragment into the view container
        transaction.replace(R.id.activityMain, amf);
        // add the transaction to the stack
        transaction.addToBackStack(null);
        // each transaction should call commit() function to confirm transaction
        transaction.commit();
    }

    public void switchToMyFriends(){
        FriendsListFragment flf = new FriendsListFragment();

        // begin a transaction
        FragmentTransaction transaction = manager.beginTransaction();
        // call add() function to add this fragment into the view container
        transaction.replace(R.id.activityMain, flf);
        // add the transaction to the stack
        transaction.addToBackStack(null);
        // each transaction should call commit() function to confirm transaction
        transaction.commit();
    }

    public void backToUserProfile(){
        onBackPressed();

    }

    public void switchToUsername(){
        // Define your fragment

        AddFriendsByUsername afbu = new AddFriendsByUsername();

        // begin a transaction
        FragmentTransaction transaction = manager.beginTransaction();
        // call add() function to add this fragment into the view container
        transaction.replace(R.id.activityMain, afbu);
        // add the transaction to the stack
        transaction.addToBackStack(null);
        // each transaction should call commit() function to confirm transaction
        transaction.commit();

    }

    @Override
    public void backToStory() {
        onBackPressed();
        StoryFragment storyfragment = (StoryFragment) mAdapter.getItem(2);
        storyfragment.mystorieslistview.setEnabled(true);
        storyfragment.firendstorieslistview.setEnabled(true);
    }


//    private boolean isEmailValid(String email) {
//        //TODO: Replace this with your own logic
//        return email.contains("@");
//    }


    //roll over sections
    private class MyAdapter extends FragmentStatePagerAdapter {
        ChatFragment chf;
        CameraFragment caf;
        DiscoveryFragment df;
        StoryFragment sf;

        public MyAdapter(FragmentManager fm) {
            super(fm);
            this.chf = new ChatFragment();
            this.caf = new CameraFragment();
            this.df = new DiscoveryFragment();
            this.sf = new StoryFragment();
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0){
                return chf;
            }
            else if (position == 1){
                return caf;
            }
            else if(position == 2){
                return sf;
            }
            else
                return df;

        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }


    }


}
