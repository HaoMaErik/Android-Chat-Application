package com.example.erik.snapchat_team_nm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.Toast;
import android.content.Context;
import android.os.Handler;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


/**
 * Created by Erik on 2016/9/27.
 */
public class StoryFragment extends Fragment{
    TextView titlestories;

    ImageButton pointDiscovery, chatShift, cameraShift;

    ImageView image1, image2, image3, image4, image5, image6;

    SearchView searchView;

    Context context;

    String currentSearchTip;

    MyHandler handler;

    ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(10);

    Communicator communicator;

    ListView mystorieslistview, firendstorieslistview;

    OptimizedStoryAdapter mstoryadapter,fstoryadapter;

    List<StoriesData> mystories = new ArrayList<>();

    List<StoriesData> friendstories = new ArrayList<>();

    StoriesData sd;

    public interface Communicator{
        public void switchToDiscovery();
        public void storiesToChat();
        public void switchToCamera();
        public void switchToSingleStory(StoriesData user);
    }

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String currentID = currentUser.getUid();
    DatabaseReference mStoryRef = mRootRef.child(currentID).child("myStory");
    DatabaseReference mfriendRef = mRootRef.child(currentID).child("friends");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        final View view = inflater.inflate(R.layout.activity_story, container, false);

        context = getActivity().getApplicationContext();

        titlestories = (TextView)view.findViewById(R.id.storiesTitleView);

        pointDiscovery = (ImageButton)view.findViewById(R.id.imageButton1);

        pointDiscovery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                communicator.switchToDiscovery();

            }

        });

        chatShift = (ImageButton)view.findViewById(R.id.chatShift);

        chatShift.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                communicator.storiesToChat();

            }

        });

        cameraShift = (ImageButton)view.findViewById(R.id.cameraShift);

        cameraShift.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                communicator.switchToCamera();

            }

        });

        //show public storys
        image1 = (ImageView)view.findViewById(R.id.imageView01);

        image1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), StoriesWebsite.class);
                intent.putExtra("url", "https://www.yahoo.com/news/world/");
                startActivity(intent);

            }

        });

        image2 = (ImageView)view.findViewById(R.id.imageView02);

        image2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), StoriesWebsite.class);
                intent.putExtra("url", "https://www.yahoo.com/style/tagged/travel/");
                startActivity(intent);

            }

        });

        image3 = (ImageView)view.findViewById(R.id.imageView03);

        image3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), StoriesWebsite.class);
                intent.putExtra("url", "https://au.sports.yahoo.com/");
                startActivity(intent);

            }

        });

        image4 = (ImageView)view.findViewById(R.id.imageView04);

        image4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), StoriesWebsite.class);
                intent.putExtra("url", "https://nz.lifestyle.yahoo.com/");
                startActivity(intent);

            }

        });

        image5 = (ImageView)view.findViewById(R.id.imageView05);

        image5.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), StoriesWebsite.class);
                intent.putExtra("url", "https://au.finance.yahoo.com/");
                startActivity(intent);

            }

        });

        image6 = (ImageView)view.findViewById(R.id.imageView06);

        image6.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), StoriesWebsite.class);
                intent.putExtra("url", "https://au.news.yahoo.com/entertainment");
                startActivity(intent);

            }

        });

//        searchView = (SearchView)view.findViewById(R.id.searchView);
//
//        searchView.setIconified(true);
//
//        searchView.setOnSearchClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                titlestories.setVisibility(View.INVISIBLE);
//
//            }
//
//        });
//
//        searchView.setOnCloseListener(new OnCloseListener() {
//
//            @Override
//            public boolean onClose() {
//                // to avoid click x button and the edittext hidden
//                return true;
//            }
//
//        });
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                Toast.makeText(context, "begin search", Toast.LENGTH_SHORT).show();
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                if (newText != null && newText.length() > 0) {
//                    currentSearchTip = newText;
//                    showSearchTip(newText);
//                }
//                return true;
//            }
//
//        });

        //get all my stories data
        mStoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mystories.clear();
                for (DataSnapshot child:dataSnapshot.getChildren()) {

                    System.out.println("@ "+child.getKey());
                    String imageUrl = child.child("content").getValue().toString();
                    String fromID = child.child("from").getValue().toString();
                    String nameFrom = child.child("namefrom").getValue().toString();
                    String sendTime = child.child("sendTime").getValue().toString();
                    String type = child.child("type").getValue().toString();
//                    System.out.println("url "+imageUrl);
//                    System.out.println("id "+fromID);
//                    System.out.println("name "+nameFrom);
//                    System.out.println("time "+sendTime);
//                    System.out.println("type "+type);
                    StoriesData storiesData = new StoriesData(sendTime,imageUrl,nameFrom,fromID,type);
                    mystories.add(storiesData);
                    //System.out.println("data "+storiesData);
                }
                mstoryadapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        System.out.println("mlist "+mystories);
        mystorieslistview = (ListView)view.findViewById(R.id.listMystories);

        mstoryadapter = new OptimizedStoryAdapter(getActivity(), R.layout.stroies_structure, mystories);

        mystorieslistview.setAdapter(mstoryadapter);

        mystorieslistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // remove the clicked item from arraylist
                communicator.switchToSingleStory(mystories.get(position));

                mystorieslistview.setEnabled(false);
                //notifies that the data has been changed and any View reflecting the data set
                // should refresh itself
                mstoryadapter.notifyDataSetChanged();
            }
        });


        //show all friends stories
        mRootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendstories.clear();
                for (DataSnapshot child : dataSnapshot.child(currentID).child("friends").getChildren()) {
                    //find friend id
                    String friendID = child.getKey();

                    //get friends story

                    for (DataSnapshot friend : dataSnapshot.child(friendID).child("myStory").getChildren()) {
                        //System.out.println("@ "+child.getKey());
                        String imageUrl = friend.child("content").getValue().toString();
                        String fromID = friend.child("from").getValue().toString();
                        String nameFrom = friend.child("namefrom").getValue().toString();
                        String sendTime = friend.child("sendTime").getValue().toString();
                        String type = friend.child("type").getValue().toString();

                        StoriesData storiesData = new StoriesData(sendTime, imageUrl, nameFrom, fromID, type);
                        friendstories.add(storiesData);
//                                System.out.println("data "+storiesData);
//                                System.out.println("id "+ fromID);
                    }
                }

                fstoryadapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        System.out.println("Flist "+friendstories);
        firendstorieslistview = (ListView)view.findViewById(R.id.listFriendstories);

        fstoryadapter = new OptimizedStoryAdapter(getActivity(), R.layout.stroies_structure, friendstories);

        firendstorieslistview.setAdapter(fstoryadapter);

        firendstorieslistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                // remove the clicked item from arraylist
                communicator.switchToSingleStory(friendstories.get(position));

                firendstorieslistview.setEnabled(false);
                //notifies that the data has been changed and any View reflecting the data set
                // should refresh itself
                fstoryadapter.notifyDataSetChanged();

                //friendstories.get(position).setCounter();


            }
        });

        // show keyboard
//        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
//                | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return view;
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        try{
            communicator = (Communicator) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString()+" must implement communicator interface");
        }
    }

    public void showSearchTip(String newText) {
        // excute after 500ms, and when excute, judge current search tip and newText
        schedule(new SearchTipThread(newText), 500);
    }

    class SearchTipThread implements Runnable {

        String newText;

        public SearchTipThread(String newText){
            this.newText = newText;
        }

        public void run() {
            // keep only one thread to load current search tip, u can get data from network here
            if (newText != null && newText.equals(currentSearchTip)) {
                handler.sendMessage(handler.obtainMessage(1, newText + " search tip"));
            }
        }
    }

    public ScheduledFuture<?> schedule(Runnable command, long delayTimeMills) {
        return scheduledExecutor.schedule(command, delayTimeMills, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                getActivity().onBackPressed();
                return true;
            }
        }
        return false;
    }

    private class MyHandler extends Handler {

        @Override
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case 1:
                    Toast.makeText(context, (String)msg.obj, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    public class OptimizedStoryAdapter extends ArrayAdapter<StoriesData> {
        private int resourceId;
        public OptimizedStoryAdapter(Context context, int resource, List<StoriesData> objects) {
            super(context, resource, objects);
            this.resourceId =resource;
        }
        public View getView(int position, View convertView, ViewGroup parent){
            // get one object content
            StoriesData sd = getItem(position);

            ViewHolder viewHolder;
            View view;
            if (convertView==null){
                //create a new ViewHolder
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(getContext()).inflate(resourceId,null);
                // store instance components into the viewHolder
                viewHolder.imageView = (ImageView) view.findViewById(R.id.ivThumb);
                viewHolder.tvUsername =(TextView)view.findViewById(R.id.tvName);
                viewHolder.tvAddDate = (TextView)view.findViewById(R.id.tvTime);
                // store the viewHolder into the view
                view.setTag(viewHolder);
            }else {
                view = convertView;
                //recover the viewHolder that store the previous instance components again
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.tvUsername.setText(sd.getMessageFromName());
            viewHolder.tvAddDate.setText(sd.getSendTime());
            PicassoClient.downloadImage(getContext(),sd.getContent(),viewHolder.imageView);
            //Picasso.with(getContext()).load(sd.getContent()).into(viewHolder.imageView);
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
