package com.example.erik.snapchat_team_nm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Result;
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
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Erik on 2016/10/1.
 */
public class SingleChatFragment extends Fragment{
    TextView tvChatN;
    //TextView tvTest;
    User user;
    ImageButton ib1;
    ImageButton ib2;
    ImageButton ib3;
    ImageButton ib4;
    ImageButton ib5;
    EditText etEnter;
    ImageButton ibBacktoChat;
    ImageButton ibUserInfo;
    static final int GALLERY_INTENT = 2;
    PopupUser ppUser;

    private Uri imageUri;
    FragmentManager manager = getFragmentManager();

    ListView listView;
    OptimizedMessageAdapter adapter;
    List<Message> messages = new ArrayList<>();
    List<Message> Imagemsg = new ArrayList<>();

    Communicator communicator;
    StorageReference mStorage = FirebaseStorage.getInstance().getReference();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://snapchat-d3c6f.appspot.com");

    Uri downloadUri;

    public interface Communicator{
        public void backToChat();
        public void switchToSimpleCamera(User user);
        public void switchToEditWithUser(User user, Bitmap bitmap);
    }

    public SingleChatFragment(User user){
        this.user = user;
    }

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
//    DatabaseReference mConditionRef = mRootRef.child("condition");

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String currentID = currentUser.getUid();
    DatabaseReference mFriendListRef = mRootRef.child(currentID).child("friends");


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.single_chat_layout, container, false);

        final String friendID = user.userID;
        final DatabaseReference messageRef = mFriendListRef.child(friendID).child("messages");

        final FrameLayout fl = (FrameLayout) view.findViewById(R.id.singleChatFrame);
        tvChatN = (TextView)view.findViewById(R.id.tvChatName);
        tvChatN.setText(user.getUserName());
        ib1 = (ImageButton)view.findViewById(R.id.ibImage);
        ib2 = (ImageButton)view.findViewById(R.id.ibPhone);
        ib3 = (ImageButton)view.findViewById(R.id.ibCamera);
        ib4 = (ImageButton)view.findViewById(R.id.ibVideo);
        ib5 = (ImageButton)view.findViewById(R.id.ibBitmoji);
        ibBacktoChat = (ImageButton)view.findViewById(R.id.ibGoBack);
        ibUserInfo = (ImageButton)view.findViewById(R.id.ibUserInfo);
        //tvTest = (TextView)view.findViewById(R.id.test);
        etEnter = (EditText)view.findViewById(R.id.etType);

        ibUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ppUser = new PopupUser(getContext());
                ppUser.tvUsername.setText(user.getUserName());
                //ppUser.setBackgroundColor(Color.TRANSPARENT);
                fl.addView(ppUser);

                ppUser.btAdd.setVisibility(View.INVISIBLE);
            }
        });

        //stop the moving of viewPage

        fl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        //type enter to send message
        etEnter.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) &&
                        i == EditorInfo.IME_NULL ) {
                    // Perform action on key press
                    String content = etEnter.getText().toString();
                    String sendFromID = currentID;
                    String sendFrom = currentUser.getEmail();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                    sdf.setTimeZone(TimeZone.getTimeZone("GMT+11:00"));
                    String timestamp = sdf.format(new Date());
                    String messageID = String.valueOf(System.currentTimeMillis());
                    String type = "text";

                    //message add into database
                    Message message = new Message(timestamp,content,sendFrom,sendFromID,type);
                    Map<String, Object> postValues = message.toMap();
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put(messageID,postValues);

                    //update your rootRef and the corresponding chat friend messages
                    mFriendListRef.child(friendID).child("messages").updateChildren(childUpdates);
                    mRootRef.child(user.getUserID()).child("friends").child(currentID).child("messages").updateChildren(childUpdates);

                    etEnter.setText("");

                    listView.setSelection(adapter.getCount()-1);

                    return true;
                }
                return false;

            }
        });

        messageRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messages.clear();
                for (DataSnapshot child:dataSnapshot.getChildren()) {

                    //System.out.println("@ "+child.child("userName").getValue().toString());
//                    String userName = messageRef.getParent().getKey();
                    String sendTime = child.child("sendTime").getValue().toString();
                    String content = child.child("content").getValue().toString();
                    String sendFrom= child.child("namefrom").getValue().toString();
                    String sendFromID = child.child("from").getValue().toString();
                    String type = child.child("type").getValue().toString();

                    //add message from the database into message list
                    Message message = new Message (sendTime,content,sendFrom,sendFromID,type);
                    messages.add(message);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView = (ListView)view.findViewById(R.id.chatList);

        adapter = new OptimizedMessageAdapter(getActivity(),R.layout.chat_layout_per_user,messages);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(messages.get(i).getType().equals("image")){
                    Intent generator = new Intent(getContext(),ViewImageActivity.class);
                    generator.putExtra("url",messages.get(i).getContent());
                    getContext().startActivity(generator);
                }

            }
        });


        ibBacktoChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                communicator.backToChat();
            }
        });


        ib1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //open local photo gallery
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_INTENT);

            }
        });

//
        ib3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                communicator.switchToSimpleCamera(user);
            }
        });


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == getActivity().RESULT_OK){
            //everything processed successfully

            if(requestCode == GALLERY_INTENT){

                //if we are here, we are hearing back from the image gallery.

                //the address of the image on the SD Card.

                Uri imageUri = data.getData();
                try {
                    InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                    communicator.switchToEditWithUser(user,bitmap);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }



//                StorageReference filePath = storageRef.child("Photos").child(currentID).child(imageUri.getLastPathSegment());
//
//                filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                        downloadUri = taskSnapshot.getDownloadUrl();
//                        //Picasso.with(getContext()).load(downloadUri).fit().centerCrop().into(imageView);
//                        Toast.makeText(getActivity(),"upload done.",Toast.LENGTH_LONG).show();
//                    }
//                });


                //pass the url to edit fragment
                //EditFragment ef = new EditFragment();

                //ef.setUri(downloadUri);

                // begin a transaction
//                FragmentTransaction transaction = manager.beginTransaction();
//                // call add() function to add this fragment into the view container
//                transaction.replace(R.id.activityMain, ef);
//                // add the transaction to the stack
//                transaction.addToBackStack(null);
//                // each transaction should call commit() function to confirm transaction
//                transaction.commit();

                //declare a stream to read the image data from the SD Card.
//                InputStream inputStream;
//
//                try {
//                    inputStream = getContentResolver().openInputStream(imageUri);
//
//                    //get a bitmap from the stream.
//                    Bitmap image = BitmapFactory.decodeStream(inputStream);
//
//                    //bitmap pass to edit photo section
//                    //show the image to the user
//
//                    //ivImage.setImageBitmap(image);
//
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                    Toast.makeText(this,"Unable to open image",Toast.LENGTH_LONG).show();
//                }
            }
        }
    }


//    public void currentUser(User currentUser){
//
//        this.user = currentUser;
//    }

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

    public class OptimizedMessageAdapter extends ArrayAdapter<Message> {
        private int resourceId;
        public OptimizedMessageAdapter(Context context, int resource, List<Message> objects) {
            super(context, resource, objects);
            this.resourceId =resource;
        }
        public View getView(int position, View convertView, ViewGroup parent){
            // get one object content
            Message message = getItem(position);
            //System.out.println("@ "+user.getUserName());
            ViewHolder viewHolder;
            View view;
            if (convertView==null){
                //create a new ViewHolder
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(getContext()).inflate(resourceId,null);
                // store instance components into the viewHolder
                viewHolder.imageView = (ImageView) view.findViewById(R.id.ivChatIcon);
                viewHolder.tvChatname =(TextView)view.findViewById(R.id.tvChatUsername);
                viewHolder.tvTime = (TextView)view.findViewById(R.id.tvSendTime);
                viewHolder.tvContent = (TextView)view.findViewById(R.id.tvContent);
                viewHolder.ivChatImage = (ImageView)view.findViewById(R.id.ivChatImage);

                // store the viewHolder into the view
                view.setTag(viewHolder);

            }else {
                view = convertView;
                //recover the viewHolder that store the previous instance components again
                viewHolder = (ViewHolder) view.getTag();
            }
            if(message.getType().equals("image")){
                viewHolder.tvContent.setVisibility(View.INVISIBLE);
                viewHolder.ivChatImage.setVisibility(View.VISIBLE);
                viewHolder.tvChatname.setText(message.getMessageFromName());
                viewHolder.tvTime.setText(message.getSendTime());

                PicassoClient.downloadImage(getContext(),message.getContent(),viewHolder.ivChatImage);
                //Picasso.with(getContext()).load(message.getContent()).into(viewHolder.ivChatImage);

            }else{
                viewHolder.tvContent.setVisibility(View.VISIBLE);
                viewHolder.ivChatImage.setVisibility(View.GONE);
                viewHolder.tvChatname.setText(message.getMessageFromName());
                viewHolder.tvTime.setText(message.getSendTime());
                viewHolder.tvContent.setText(message.getContent());
            }

            return view;
        }
        // Define a ViewHolder class
        class ViewHolder{
            ImageView imageView;
            TextView tvChatname;
            TextView tvTime;
            TextView tvContent;
            ImageView ivChatImage;
        }
    }


}
