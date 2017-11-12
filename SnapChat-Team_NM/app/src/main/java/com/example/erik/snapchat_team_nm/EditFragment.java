package com.example.erik.snapchat_team_nm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
 * Created by Erik on 2016/10/15.
 */
public class EditFragment extends Fragment {
    Button btSend;
    Button btDedit;
    Button btSave;
    Button btCancel;
    Button btTedit;
    Button btEedit;
    Button btClear;
    ImageButton timer;
    ImageButton fivesecond;
    ImageButton foursecond;
    ImageButton threesecond;
    ImageButton twosecond;
    ImageButton onesecond;
    EditText et;
    Button btAdd;
    String friendID;
    User user;
    private ImageView img;
    private ViewGroup rootLayout;
    private int _xDelta;
    private int _yDelta;
    RelativeLayout rlNew;
    FrameLayout fl;
    ImageView ivAdditional;
    DrawingView dv ;
    private Paint mPaint;
    int countertimer;
    GridView emojiView;
    OptimizedEmojiAdapter adapter;
    List<Emoji> emojis = new ArrayList<>();

    Bitmap Tcombined;
    Bitmap photo;
    ImageView edView;
    RelativeLayout innerRL;
//    Boolean isSimpleCamera = false;
    Uri downloadUrl;

    Communicator communicator;

    public interface Communicator {
        public void switchFromEditToCamera();
        public void switchToSendPhoto(Bitmap bitmap);
        public void switchToSingleChat(User user);
    }

    public EditFragment(Bitmap photo){
        this.photo = photo;
    }

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
//    DatabaseReference mConditionRef = mRootRef.child("condition");

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String currentID = currentUser.getUid();
    DatabaseReference mFriendListRef = mRootRef.child(currentID).child("friends");
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://snapchat-d3c6f.appspot.com");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_editphoto, container, false);
        rlNew = new RelativeLayout(getContext());

        ImageView ivAnother = new ImageView(getContext());
        ivAnother.setImageBitmap(photo);
        rlNew.addView(ivAnother);

        fl = (FrameLayout)view.findViewById(R.id.Additional);
        ivAdditional = (ImageView)view.findViewById(R.id.ivShow);
        ivAdditional.setImageBitmap(photo);

        //stop the moving of viewPage
        RelativeLayout rl = (RelativeLayout)view.findViewById(R.id.EditLayout);
        rl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        innerRL = (RelativeLayout)view.findViewById(R.id.Draw);
        et = (EditText)view.findViewById(R.id.etEdit);
        et.setDrawingCacheEnabled(true);
        btAdd = (Button)view.findViewById(R.id.btADDText);

        edView = (ImageView) view.findViewById(R.id.ivShowImage);
        edView.setImageBitmap(photo);

        btDedit = (Button) view.findViewById(R.id.btDedit);

        btDedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dv = new DrawingView(getContext());
                fl.addView(dv);
                mPaint = new Paint();
                mPaint.setAntiAlias(true);
                mPaint.setDither(true);
                mPaint.setColor(Color.BLUE);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeJoin(Paint.Join.ROUND);
                mPaint.setStrokeCap(Paint.Cap.ROUND);
                mPaint.setStrokeWidth(12);

            }
        });

        btTedit = (Button) view.findViewById(R.id.btTedit);
        btTedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                textPhoto(photo);
                et.setVisibility(View.VISIBLE);
                btAdd.setVisibility(View.VISIBLE);

                btAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        TextView tv = new TextView(getContext());

                        tv.setText(et.getText().toString());
                        tv.setTextColor(Color.RED);
                        tv.setTextSize(20);
                        tv.setX(500);
                        tv.setY(500);

                        //rootLayout.addView(tv);
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(1000,300);
                        tv.setLayoutParams(layoutParams);
                        tv.setOnTouchListener(new ChoiceTouchListener());

                        fl.addView(tv);

//                        Bitmap bmp = Bitmap.createBitmap(et.getDrawingCache());
//                        System.out.println("photo "+photo);
//                        System.out.println("text "+bmp);
//                        Tcombined = combineImages(photo, bmp);
//                        edView.setImageBitmap(Tcombined);
//                        photo = Tcombined;

                        et.setText("");
                        et.setVisibility(View.INVISIBLE);
                        btAdd.setVisibility(View.INVISIBLE);
                    }
                });

            }

        });

        btCancel = (Button) view.findViewById(R.id.btCancel);
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                communicator.switchFromEditToCamera();
            }
        });
        btSave = (Button) view.findViewById(R.id.btSave);
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadImagePublic();
            }
        });

        btSend = (Button)view.findViewById(R.id.btSend);

        if(user == null){
            btSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    communicator.switchToSendPhoto(photo);
                }
            });
        }else{
            btSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //upload image to storage
                    String imageID = String.valueOf(System.currentTimeMillis());

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
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
                            friendID = user.getUserID();
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

                    communicator.switchToSingleChat(user);
                }
            });
        }



        //add emojis to the list
        Emoji emoji1 = new Emoji(R.mipmap.d_aini);
        Emoji emoji2 = new Emoji(R.mipmap.d_aoteman);
        Emoji emoji3 = new Emoji(R.mipmap.d_baibai);
        Emoji emoji4 = new Emoji(R.mipmap.d_beishang);
        Emoji emoji5 = new Emoji(R.mipmap.d_bishi);
        Emoji emoji6 = new Emoji(R.mipmap.d_bizui);
        Emoji emoji7 = new Emoji(R.mipmap.d_chanzui);
        Emoji emoji8 = new Emoji(R.mipmap.d_chijing);
        Emoji emoji9 = new Emoji(R.mipmap.d_dahaqi);
        Emoji emoji10 = new Emoji(R.mipmap.d_dalian);
        Emoji emoji11 = new Emoji(R.mipmap.d_ding);
        Emoji emoji12 = new Emoji(R.mipmap.d_doge);
        emojis.add(emoji1);
        emojis.add(emoji2);
        emojis.add(emoji3);
        emojis.add(emoji4);
        emojis.add(emoji5);
        emojis.add(emoji6);
        emojis.add(emoji7);
        emojis.add(emoji8);
        emojis.add(emoji9);
        emojis.add(emoji10);
        emojis.add(emoji11);
        emojis.add(emoji12);

        //show them in the GridView
        emojiView = (GridView)view.findViewById(R.id.gridView);
        adapter = new OptimizedEmojiAdapter(getActivity(), R.layout.layout_per_emoji,emojis);
        //set the adapter for GridView
        emojiView.setAdapter(adapter);
        rootLayout = (ViewGroup)view.findViewById(R.id.EditLayout);

        btEedit = (Button) view.findViewById(R.id.btEedit);
        btEedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emojiView.setVisibility(View.VISIBLE);
            }
        });

        emojiView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ImageView iv = new ImageView(getContext());
                iv.setImageResource(emojis.get(i).getImageUri());
                iv.setX(300);
                iv.setY(500);
                //rootLayout.addView(iv);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(200, 200);
                iv.setLayoutParams(layoutParams);
                iv.setOnTouchListener(new ChoiceTouchListener());

                fl.addView(iv);

                emojiView.setVisibility(View.GONE);
            }
        });
        timer = (ImageButton) view.findViewById(R.id.bttimer);
        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fivesecond.setVisibility(View.VISIBLE);
                foursecond.setVisibility(View.VISIBLE);
                threesecond.setVisibility(View.VISIBLE);
                twosecond.setVisibility(View.VISIBLE);
                onesecond.setVisibility(View.VISIBLE);
            }
        });

        fivesecond = (ImageButton) view.findViewById(R.id.fivesec);
        fivesecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countertimer = 5;
                fivesecond.setVisibility(View.INVISIBLE);
                foursecond.setVisibility(View.INVISIBLE);
                threesecond.setVisibility(View.INVISIBLE);
                twosecond.setVisibility(View.INVISIBLE);
                onesecond.setVisibility(View.INVISIBLE);
                Toast.makeText(getActivity().getApplicationContext(), "timer is set to 5s", Toast.LENGTH_LONG).show();
            }
        });

        foursecond = (ImageButton) view.findViewById(R.id.foursec);
        foursecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countertimer = 4;
                fivesecond.setVisibility(View.INVISIBLE);
                foursecond.setVisibility(View.INVISIBLE);
                threesecond.setVisibility(View.INVISIBLE);
                twosecond.setVisibility(View.INVISIBLE);
                onesecond.setVisibility(View.INVISIBLE);
                Toast.makeText(getActivity().getApplicationContext(), "timer is set to 5s", Toast.LENGTH_LONG).show();
            }
        });

        threesecond = (ImageButton) view.findViewById(R.id.thirdsec);
        threesecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countertimer = 3;
                fivesecond.setVisibility(View.INVISIBLE);
                foursecond.setVisibility(View.INVISIBLE);
                threesecond.setVisibility(View.INVISIBLE);
                twosecond.setVisibility(View.INVISIBLE);
                onesecond.setVisibility(View.INVISIBLE);
                Toast.makeText(getActivity().getApplicationContext(), "timer is set to 5s", Toast.LENGTH_LONG).show();
            }
        });

        twosecond = (ImageButton) view.findViewById(R.id.secondsec);
        twosecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countertimer = 2;
                fivesecond.setVisibility(View.INVISIBLE);
                foursecond.setVisibility(View.INVISIBLE);
                threesecond.setVisibility(View.INVISIBLE);
                twosecond.setVisibility(View.INVISIBLE);
                onesecond.setVisibility(View.INVISIBLE);
                Toast.makeText(getActivity().getApplicationContext(), "timer is set to 5s", Toast.LENGTH_LONG).show();
            }
        });

        onesecond = (ImageButton) view.findViewById(R.id.onesec);
        onesecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countertimer = 1;

                //invisible all item
                fivesecond.setVisibility(View.INVISIBLE);
                foursecond.setVisibility(View.INVISIBLE);
                threesecond.setVisibility(View.INVISIBLE);
                twosecond.setVisibility(View.INVISIBLE);
                onesecond.setVisibility(View.INVISIBLE);
                Toast.makeText(getActivity().getApplicationContext(), "timer is set to 5s", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }


    public void downloadImagePublic() {

        fl.setDrawingCacheEnabled(true);
        Bitmap bmp = Bitmap.createBitmap(fl.getDrawingCache());
        fl.setDrawingCacheEnabled(false);
//        edView.setImageBitmap(bmp);
        photo = bmp;

        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MySnapChat");
        if (!dir.exists()) {
            dir.mkdir();
        }
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(dir.getPath() + File.separator + "IMG_" + timestamp + ".jpg");
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(mediaFile);
            photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
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
        Toast.makeText(getActivity().getApplicationContext(), "download successfully", Toast.LENGTH_LONG).show();
    }
    public Bitmap drawPhoto(Bitmap photo){
        return null;
        //return bitmap
    }


    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            communicator = (Communicator) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement communicator interface");
        }
    }

    //adapter for putting emojis in the GridView
    public class OptimizedEmojiAdapter extends ArrayAdapter<Emoji> {
        private int resourceId;
        public OptimizedEmojiAdapter(Context context, int resource, List<Emoji> objects) {
            super(context, resource, objects);
            this.resourceId =resource;
        }
        public View getView(int position, View convertView, ViewGroup parent){
            // get one object content
            Emoji emoji = getItem(position);

            ViewHolder viewHolder;
            View view;
            if (convertView==null){
                //create a new ViewHolder
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(getContext()).inflate(resourceId,null);
                // store instance components into the viewHolder
                viewHolder.imageView = (ImageView) view.findViewById(R.id.ivEmoji);

                // store the viewHolder into the view
                view.setTag(viewHolder);
            }else {
                view = convertView;
                //recover the viewHolder that store the previous instance components again
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.imageView.setImageResource(emoji.getImageUri());
            return view;
        }
        // Define a ViewHolder class
        class ViewHolder{
            ImageView imageView;
        }
    }

    //for moving concepts by touching
    private final class ChoiceTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent event) {
            final int X = (int) event.getRawX();
            final int Y = (int) event.getRawY();
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    FrameLayout.LayoutParams lParams = (FrameLayout.LayoutParams) view.getLayoutParams();
                    _xDelta = X - lParams.leftMargin;
                    _yDelta = Y - lParams.topMargin;
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    break;
                case MotionEvent.ACTION_MOVE:
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view
                            .getLayoutParams();
                    layoutParams.leftMargin = X - _xDelta;
                    layoutParams.topMargin = Y - _yDelta;
                    layoutParams.rightMargin = -250;
                    layoutParams.bottomMargin = -250;
                    view.setLayoutParams(layoutParams);
                    break;
            }
            fl.invalidate();
            return true;
        }
    }

    //class for drawing
    public class DrawingView extends View {

        public int width;
        public int height;
        private Bitmap mBitmap;
        private Canvas mCanvas;
        private Path mPath;
        private Paint mBitmapPaint;
        Context context;
        private Paint circlePaint;
        private Path circlePath;

        public DrawingView(Context c) {
            super(c);
            context = c;
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.BLUE);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(4f);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath( mPath,  mPaint);
            canvas.drawPath( circlePath,  circlePaint);
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }

        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;

                circlePath.reset();
                circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
            }
        }

        private void touch_up() {
            mPath.lineTo(mX, mY);
            circlePath.reset();
            // commit the path to our offscreen
            mCanvas.drawPath(mPath,  mPaint);
            // kill this so we don't double draw
            mPath.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
    }



    public  void setUser(User user){
        this.user=user;
    }
}
