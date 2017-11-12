package com.example.erik.snapchat_team_nm;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Erik on 2016/9/27.
 */
public class CameraFragment extends Fragment implements SurfaceHolder.Callback,Runnable {

    ImageButton ibTake;
    Button btReverse;
    Button btFlash;
    Button btMemory;
    Button btChat;
    Button btProfile;
    Button btSend;
    Button btStory;
    User user;

    Boolean isSimpleCamera = false;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Camera camera;
    Camera.Parameters parameters;
    boolean isCurrentBackCamera = true;
    boolean isFaceDectOn = false;

    Bitmap photo;
    RelativeLayout rl;

    String friendID;
    int X =500;
    int Y = 500;
    ImageView iv;

    Communicator communicator;

    @Override
    public void run() {
        while (isFaceDectOn){
            try {
                Thread.sleep(1000);
                iv.setX(X+100);
                iv.setY(Y+500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public interface Communicator{

        public void toUserProfile();
        public void backChat();
        public void switchToMemory();
        public void switchToSingleChat(User user);
        public void switchToEdit(Bitmap photo);
        public void switchToStory();
    }


    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
//    DatabaseReference mConditionRef = mRootRef.child("condition");

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String currentID = currentUser.getUid();
    DatabaseReference mFriendListRef = mRootRef.child(currentID).child("friends");

    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_camera, container, false);
        surfaceView = (SurfaceView) view.findViewById(R.id.cameraSurface);
        rl = (RelativeLayout)view.findViewById(R.id.rlCamera);
        surfaceView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                parameters = camera.getParameters();
                if (parameters.getMaxNumDetectedFaces()>0){
                    camera.startFaceDetection();
                    Thread t = new Thread(CameraFragment.this);
                    t.start();
                    isFaceDectOn = true;
                    iv = new ImageView(getContext());
                    rl.addView(iv);
                    iv.setImageResource(R.drawable.face_detection);
                    iv.getLayoutParams().height = 800;
                    iv.getLayoutParams().width = 800;

                    Toast.makeText(getActivity().getApplicationContext(),"Face detection started",Toast.LENGTH_LONG).show();
                }else{
                    iv.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);


        btChat = (Button)view.findViewById(R.id.btChat);
        if(isSimpleCamera){
            btChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    communicator.switchToSingleChat(user);
                }
            });
        }else{
            btChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    communicator.backChat();
                }
            });
        }

        initialize(view);

        return view;
    }

    public void initialize(View view){

        ibTake = (ImageButton) view.findViewById(R.id.btTake);
        ibTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });

        btReverse = (Button) view.findViewById(R.id.btReverse);
        btReverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraSwitch();
            }
        });

        btFlash = (Button) view.findViewById(R.id.btFlash);
        btFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flashOption();
            }
        });
        btMemory = (Button) view.findViewById(R.id.btMemory);
        btMemory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                communicator.switchToMemory();
            }
        });
        btStory = (Button) view.findViewById(R.id.btStory);
        btStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                communicator.switchToStory();
            }
        });
        btProfile = (Button) view.findViewById(R.id.btProfile);
        btProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                communicator.toUserProfile();
            }
        });

    }

    //only use some buttons in single chat
    public void useForSimpleCamera(){
        isSimpleCamera = true;
    }


    public void cameraSwitch(){

        if(isCurrentBackCamera == true){
            camera.stopPreview();
            if (camera!=null){
                camera.release();
                camera=null;
            }
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            camera.setDisplayOrientation(90);
            try {
                camera.setPreviewDisplay(surfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            camera.startPreview();
            isCurrentBackCamera = false;
        }else{
            camera.stopPreview();
            if (camera!=null){
                camera.release();
                camera=null;
            }
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            camera.setDisplayOrientation(90);
            try {
                camera.setPreviewDisplay(surfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            camera.startPreview();
            isCurrentBackCamera = true;
        }

    }

    public void takePhoto() {
        camera.takePicture(new Camera.ShutterCallback() {
            @Override
            public void onShutter() {

            }
        }, null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                Bitmap rotatedphoto;
                if(isCurrentBackCamera == false){
                    //back camera
                    photo = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    //rotate image to the right direction
                    Matrix matrix = new Matrix();
                    matrix.setRotate(90);
                    rotatedphoto = Bitmap.createBitmap(photo, 0, 0,
                            photo.getWidth(), photo.getHeight(),
                            matrix, true);
                }else{
                    photo = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    //rotate image to the right direction
                    Matrix matrix = new Matrix();
                    matrix.setRotate(90);
                    matrix.postScale(1,-1);
                    rotatedphoto = Bitmap.createBitmap(photo, 0, 0,
                            photo.getWidth(), photo.getHeight(),
                            matrix, true);
                }

                //camera.stopPreview();

                communicator.switchToEdit(rotatedphoto);
            }
        });
    }

    public void flashOption() {
        if(parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_OFF)){
            parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            camera.setParameters(parameters);
            Toast.makeText(getActivity(), "Flash is On", Toast.LENGTH_SHORT).show();
        }else{
            parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(parameters);
            Toast.makeText(getActivity(), "Flash is Off", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        camera  = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        camera.setFaceDetectionListener(new Camera.FaceDetectionListener() {
            @Override
            public void onFaceDetection(Camera.Face[] faces, Camera camera) {

                if (faces.length>0){
                    System.out.println("@ Location X "+faces[0].rect.centerX()+ "Location Y: "+faces[0].rect.centerY());
                    X=(faces[0].rect.centerX())*(-1);
                    Y=faces[0].rect.centerY();
                }
            }
        });
        parameters = camera.getParameters();
        camera.setParameters(parameters);
        camera.setDisplayOrientation(90);
        try {
            camera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        camera.release();
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

    public  void setUser(User user){
        this.user=user;
    }
}
