package com.yahoo.americancurry.petpeeve.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.util.Pair;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yahoo.americancurry.petpeeve.R;
import com.yahoo.americancurry.petpeeve.model.Pin;

import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class ComposeFragment extends DialogFragment {
    public static final int MAX_MESSAGE_LENGTH = 500;
    private static final int PICK_CONTACT = 4;
    private static final int PICK_IMAGE= 10;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;


    private EditText etCompose;
    private Pin pin;
    private TextView tvSendTo;
    private ImageButton ibGallery;
    private ImageView ivMedia;
    private ImageButton ibCamera;
    private Uri uri;

    public ComposeFragment() {

    }

    public static ComposeFragment newInstance(String titleOfForm) {
        ComposeFragment fragment = new ComposeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Holo_Dialog_NoActionBar);

        final View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_compose, null);

        dialog.getWindow().setContentView(view);

        final WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.TOP;

        dialog.setCanceledOnTouchOutside(true);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_compose, container);
        getDialog().setCanceledOnTouchOutside(true);

        pin = (Pin) this.getArguments().get("pinInfo");

        final TextView tvComposeNumChars = (TextView) view.findViewById(R.id.tvComposeNumChars);
        etCompose = (EditText) view.findViewById(R.id.etCompose);
        Button btnTweet = (Button) view.findViewById(R.id.btSubmit);

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                composeMessage(v);
            }
        });

        TextWatcher mTextEditorWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                tvComposeNumChars.setText(Integer.toString(MAX_MESSAGE_LENGTH));
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvComposeNumChars.setText(String.valueOf(MAX_MESSAGE_LENGTH - s.length()));
            }

            public void afterTextChanged(Editable s) {
            }
        };
        etCompose.addTextChangedListener(mTextEditorWatcher);


        tvSendTo = (TextView) view.findViewById(R.id.tvSendTo);
        //llReceivers = (LinearLayout) view.findViewById(R.id.llRecivers);

        ImageButton ibAddContact = (ImageButton) view.findViewById(R.id.ibAddContact);
        ibAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                //getActivity().startActivityForResult(intent, PICK_CONTACT);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });

        ibGallery = (ImageButton) view.findViewById(R.id.ibGallery);
        ibGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, PICK_IMAGE);
            }
        });


        ivMedia = (ImageView) view.findViewById(R.id.ivMedia);

        ibCamera = (ImageButton) view.findViewById(R.id.ibCamera);
        ibCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });
        
        return view;
    }

    public void composeMessage(View view) {

        pin.setText(etCompose.getText().toString());
        try {
            pin.save();
        } catch (com.parse.ParseException e) {
            Log.e("ERROR", "Unable to post message to Parse");
            Toast.makeText(getActivity(), "Unable to post message", Toast.LENGTH_SHORT).show();
        }
        this.dismiss();

    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {

        switch (reqCode) {
            case PICK_CONTACT:
                if (resultCode == Activity.RESULT_OK) {

                    Pair<String,String> pair = getContactInfo(data);
                    tvSendTo.setText("  " + pair.first + " X  ");
                }
                break;
            case PICK_IMAGE :
                if (resultCode == Activity.RESULT_OK) {
                    fillImageView(data);
                }
                break;

            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    ivMedia.setImageBitmap(photo);
                }
                break;
        }
    }

    protected Pair<String,String> getContactInfo(Intent intent)
    {
        String name = "";
        String phone = "";
        Cursor cursor =  getActivity().getBaseContext().getContentResolver().query(intent.getData(), null, null, null, null);

        if (cursor.moveToNext())
        {
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));

            String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

            if ( hasPhone.equalsIgnoreCase("1"))
                hasPhone = "true";
            else
                hasPhone = "false" ;

            if (Boolean.parseBoolean(hasPhone))
            {
                Cursor phones = getActivity().getBaseContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,null, null);
                while (phones.moveToNext())
                {
                    phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
                phones.close();
            }
        }  //while (cursor.moveToNext())
        cursor.close();
        if(!name.isEmpty() && !phone.isEmpty()) {
            Pair<String, String> pair = new Pair<>(name, phone);
            return pair;
        }
        else
            return null;
    }

    protected void fillImageView(Intent intent) {
        Uri selectedImage = intent.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        Cursor cursor = getActivity().getBaseContext().getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();

       // ivMedia.setMaxHeight(100);
       // ivMedia.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,100));
        ivMedia.setImageBitmap(BitmapFactory.decodeFile(picturePath));
    }

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
}