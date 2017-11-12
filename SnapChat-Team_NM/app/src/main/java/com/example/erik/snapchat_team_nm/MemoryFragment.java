package com.example.erik.snapchat_team_nm;

/**
 * Created by leely on 2016/10/3.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MemoryFragment extends Fragment {
    TextView tvMemory;
    ImageButton btSnap;
    Button btRoll;
    GridView memoryGrid;
    ImageView Mimage;
    Button share;
    GridAdapter adapter;
    List<Bitmap> photos = new ArrayList<Bitmap>();
    Bitmap bitmap;
    int res = R.drawable.add_friend_image;
    LinearLayout delview;

    //public static final int abc = R.drawable.d_haixiu;
    //Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(), abc);
    SendToFragment sending = new SendToFragment();

    static final int GALLERY_INTENT = 2;
    static final int SHARE_INTENT = 3;

    Communicator communicator;

    public interface Communicator {
        public void switchToCamera();
        public void photoDetails(Bitmap bitmap);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_memory, container, false);
        delview = (LinearLayout) view.findViewById(R.id.linear_del);
        memoryGrid = (GridView)view.findViewById(R.id.memoryGrid);
        share = (Button)view.findViewById(R.id.btShare);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Getpic();
            }
        });


        btSnap = (ImageButton) view.findViewById(R.id.btSnaps);
        btSnap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                communicator.switchToCamera();
            }
        });
        btRoll = (Button) view.findViewById(R.id.btRoll);
        btRoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePhoto();
            }
        });

        photos = getMemory();
        adapter = new GridAdapter(photos);
        //set the adapter for your ListView
        memoryGrid.setAdapter(adapter);
        memoryGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // remove the clicked item from arraylist
                communicator.photoDetails(photos.get(position));

                memoryGrid.setEnabled(false);
                //notifies that the data has been changed and any View reflecting the data set
                // should refresh itself
                adapter.notifyDataSetChanged();
            }
        });
        memoryGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,final int position, long id) {

                delview.setVisibility(View.VISIBLE);
                delview.findViewById(R.id.tv_del).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delview.setVisibility(View.GONE);
                        bitmap = photos.get(position);
                        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, null,null));
                        File file = new File(uri.getPath());
                        if (file.exists()){
                            file.delete();
                        }
                        photos.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
                delview.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delview.setVisibility(View.GONE);
                    }
                });

                return true;
            }
        });
        return view;
    }



    public class GridAdapter extends BaseAdapter {
        private List<Bitmap> photos = new ArrayList<Bitmap>();
        public GridAdapter(List<Bitmap> bitmaps)
        {
            this.photos = bitmaps;
        }
        @Override
        public int getCount() {
            // How many items are in the data set represented by this Adapter.
            return photos.size();
        }
        @Override
        public Object getItem(int position) {
            // Get the data item associated with the specified position in the data set.
            return photos.get(position);
        }
        @Override
        public long getItemId(int position) {
            // Get the row id associated with the specified position in the list.
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View layout = View.inflate(getActivity(),R.layout.image_item,null);
            ImageView mImage = (ImageView)layout.findViewById(R.id.Mimage);
            Bitmap mbitmap = photos.get(position);
            mImage.setImageBitmap(mbitmap);
            return layout;
        }

        public Bitmap zoomBitmap(Bitmap oldBitmap, int newWidth, int newHeight) {
            int width = oldBitmap.getWidth();
            int height = oldBitmap.getHeight();
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap newbm = Bitmap.createBitmap(oldBitmap, 0, 0, width, height, matrix,
                    true);
            return newbm;
        }
    }
    public void choosePhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_INTENT);
    }

    private List<Bitmap> getMemory() {
        List<Bitmap> imagePathList = new ArrayList<Bitmap>();
        String filePath = Environment.getExternalStorageDirectory().toString() + "/Pictures/MyMemory";
        File fileAll = new File(filePath);
        File[] files = fileAll.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            Bitmap bmp = BitmapFactory.decodeFile(file.toString());
            imagePathList.add(bmp);
        }
        return imagePathList;
    }
    public void Getpic() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == GALLERY_INTENT) {
                Uri imageuri = data.getData();
                Bitmap rBitmap = null;
                try {
                    rBitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), imageuri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                communicator.photoDetails(rBitmap);
            }
            if (requestCode == SHARE_INTENT){
                Uri uri = data.getData();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(intent,"Share to"));
            }
        }
    }

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
}