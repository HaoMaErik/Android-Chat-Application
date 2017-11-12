package com.example.erik.snapchat_team_nm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Erik on 2016/10/16.
 */
public class SendToFragment extends Fragment {

    ImageButton ibBack;
    ImageButton ibToFriendList;

    TextView tvMyStory;
    TextView tvMyMemories;
    Bitmap bitmap;

    Communicator communicator;

    ListView listView;
    OptimizedUserAdapter adapter;
    List<User> users = new ArrayList<>();

    Uri downloadUrl;
    String friendID;


    public interface Communicator{
        public void toAddFriend();
        public void switchToSingleChat(User user);
        public void showStory();
        public void updateMemory();
    }

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://snapchat-d3c6f.appspot.com");
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String currentID = currentUser.getUid();
    DatabaseReference mFriendListRef = mRootRef.child(currentID).child("friends");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_send_photo, container, false);

        ibBack = (ImageButton)view.findViewById(R.id.ibBacktoEdit);
        ibToFriendList = (ImageButton)view.findViewById(R.id.ibToAddFriends);
        ibToFriendList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                communicator.toAddFriend();
            }
        });

        tvMyStory = (TextView)view.findViewById(R.id.tvMyStory);
        tvMyStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //upload image to storage
                String imageID = String.valueOf(System.currentTimeMillis());

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                StorageReference filePath = storageRef.child("Photos").child(currentID).child("myStory").child(imageID +".jpg");
                UploadTask uploadTask = filePath.putBytes(data);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        downloadUrl = taskSnapshot.getDownloadUrl();

                        //send to my story path

                        String content = downloadUrl.toString();
                        String sendFromID = currentID;
                        String sendFrom = currentUser.getEmail();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                        sdf.setTimeZone(TimeZone.getTimeZone("GMT+11:00"));
                        String timeStamp = sdf.format(new Date());
                        String messageID = String.valueOf(System.currentTimeMillis());
                        String type = "image";

                        //message add into database
                        Message message = new Message(timeStamp,content,sendFrom,sendFromID,type);
                        Map<String, Object> postValues = message.toMap();
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put(messageID,postValues);

                        //update your rootRef and the corresponding chat friend messages
                        mRootRef.child(currentID).child("myStory").updateChildren(childUpdates);

                        //move to story section
                        communicator.showStory();

                    }
                });

            }
        });

        tvMyMemories = (TextView)view.findViewById(R.id.tvMyMemories);
        tvMyMemories.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                downloadToMemory(bitmap);
                Toast.makeText(getActivity().getApplicationContext(), "send successfully", Toast.LENGTH_LONG).show();
                communicator.updateMemory();

            }
        });


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

        listView = (ListView)view.findViewById(R.id.SendToFriendsListView);
        adapter = new OptimizedUserAdapter(getActivity(), R.layout.single_user_layout,users);
        //set the adapter for your ListView
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                //upload image to storage
                String imageID = String.valueOf(System.currentTimeMillis());

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                StorageReference filePath = storageRef.child("Photos").child(currentID).child(imageID +".jpg");
                UploadTask uploadTask = filePath.putBytes(data);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        downloadUrl = taskSnapshot.getDownloadUrl();

                        //send to the specific user
                        friendID = users.get(position).getUserID();
                        String content = downloadUrl.toString();
                        String sendFromID = currentID;
                        String sendFrom = currentUser.getEmail();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                        sdf.setTimeZone(TimeZone.getTimeZone("GMT+11:00"));
                        String timeStamp = sdf.format(new Date());
                        String messageID = String.valueOf(System.currentTimeMillis());
                        String type = "image";

                        //message add into database
                        Message message = new Message(timeStamp,content,sendFrom,sendFromID,type);
                        Map<String, Object> postValues = message.toMap();
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put(messageID,postValues);

                        //update your rootRef and the corresponding chat friend messages
                        mFriendListRef.child(friendID).child("messages").updateChildren(childUpdates);
                        mRootRef.child(friendID).child("friends").child(currentID).child("messages").updateChildren(childUpdates);
                        }
                });

//                communicator.switchToSingleChat(users.get(position));
                listView.setEnabled(false);
                //notifies that the data has been changed and any View reflecting the data set
                // should refresh itself
                adapter.notifyDataSetChanged();

                communicator.switchToSingleChat(users.get(position));
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

    public void downloadToMemory(Bitmap bitmap) {

        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyMemory");
        if (!dir.exists()) {
            dir.mkdir();
        }
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(dir.getPath() + File.separator + "IMG_" + timestamp + ".jpg");
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(mediaFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mediaFile)));
    }

    public  void setBitmap(Bitmap bitmap){
        this.bitmap=bitmap;
    }

}
