package com.yahoo.americancurry.petpeeve.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.util.Pair;
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
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.parse.ParseFile;
import com.yahoo.americancurry.petpeeve.R;
import com.yahoo.americancurry.petpeeve.model.Pin;
import com.yahoo.americancurry.petpeeve.utils.GoogleMapsUtil;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ComposeFragment extends DialogFragment {
    public static final int MAX_MESSAGE_LENGTH = 500;
    private static final int PICK_CONTACT = 4;
    private static final int PICK_IMAGE = 10;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;


    private EditText etCompose;
    private Pin pin;
    private ImageButton ibGallery;
    private ImageView ivMedia;
    private ImageButton ibCamera;
    private Uri uri;
    private LinearLayout llSendTo;
    private Map<String,String> recepientList;
    private Bitmap bitmapMedia = null;

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

        recepientList = new HashMap<>();

        pin = (Pin) this.getArguments().get("pinInfo");

        etCompose = (EditText) view.findViewById(R.id.etCompose);
        Button btnTweet = (Button) view.findViewById(R.id.btSubmit);

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                composeMessage(v);
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

        llSendTo = (LinearLayout) view.findViewById(R.id.llSendTo);
        llSendTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                //getActivity().startActivityForResult(intent, PICK_CONTACT);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });

        final TextView tvAddressDesc = (TextView) view.findViewById(R.id.tvAddressDesc);
        GoogleMapsUtil.getReverseGeocoding(pin.getLocationCentreLatitude(), pin.getLocationCentreLongitude(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("results");
                    if (results.length() > 0) {
                        String
                                formattedAddress = ((JSONObject) results.get(0)).getString("formatted_address");
                        tvAddressDesc.setText("Near " + formattedAddress);
                    }

                } catch (JSONException e) {
                }
            }
        });

        return view;
    }

    public void composeMessage(View view) {

        pin.setText(etCompose.getText().toString());

        if(!recepientList.isEmpty()) {
            pin.setRecipientPhone(recepientList.entrySet().iterator().next().getValue());
            pin.setRecipientName(recepientList.entrySet().iterator().next().getKey());
        }

        if(bitmapMedia!=null) {
            ParseFile parseFile = putBitmapToParseFile(bitmapMedia);
            pin.setParseFile(parseFile);
        }

        try {
            pin.save();
        } catch (com.parse.ParseException e) {
            Log.e("ERROR", "Unable to post message to Parse");
            Toast.makeText(getActivity(), "Unable to post message", Toast.LENGTH_SHORT).show();
        }
        this.dismiss();

    }

    ParseFile putBitmapToParseFile(Bitmap bitmap) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        ParseFile parseFile = new ParseFile(byteArray);
        parseFile.saveInBackground();
        return parseFile;
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {

        switch (reqCode) {
            case PICK_CONTACT:
                if (resultCode == Activity.RESULT_OK) {

                    Pair<String, String> pair = getContactInfo(data);
                    if(pair!=null) {
                        recepientList.put(pair.first, pair.second);

                        TextView textView = new TextView(getActivity());
                        textView.setText(pair.first + " X ");
                        textView.setBackground(getResources().getDrawable(R.drawable.custom_button));
                        textView.setTextColor(Color.BLACK);
                        textView.setPadding(10, 10, 10, 10);
                        textView.setTextSize(12);

                        LinearLayout.LayoutParams lastTxtParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lastTxtParams.setMargins(0, 0, 10, 0);
                        textView.setLayoutParams(lastTxtParams);

                        llSendTo.addView(textView);
                    }
                }
                break;
            case PICK_IMAGE:

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

    protected Pair<String, String> getContactInfo(Intent intent) {

        String name = "";
        String phone = "";
        // Get the URI and query the content provider for the phone number
        Uri contactUri = intent.getData();
        String[] projection = new String[]{ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor cursor = getActivity().getBaseContext().getContentResolver().query(contactUri, projection,
                null, null, null);
        // If the cursor returned is valid, get the phone number
        if (cursor != null && cursor.moveToFirst()) {
            int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);

            phone = cursor.getString(numberIndex);
            name = cursor.getString(nameIndex);
        }

        cursor.close();
        if (!name.isEmpty() && !phone.isEmpty()) {
            Pair<String, String> pair = new Pair<>(name, phone);
            return pair;
        } else
            return null;
    }

    protected void fillImageView(Intent intent) {
        Uri selectedImage = intent.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = getActivity().getBaseContext().getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();

        bitmapMedia = BitmapFactory.decodeFile(picturePath);
        ivMedia.setImageBitmap(bitmapMedia);
    }

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    /**
     * Create a file Uri for saving an image or video
     */
    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
}