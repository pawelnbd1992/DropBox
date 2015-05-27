package com.example.pawel.dropbox;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session;
import com.dropbox.client2.session.TokenPair;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private static final String TAG ="OJOJ" ;
    private LinearLayout container;
    private DropboxAPI dropboxAPI;
    private boolean isUserLoggedIn;
    private Button loginButton;
    private Button upload;
    private Button list;
    private Button zrob;
    private String mCameraFileName;
    final static private int NEW_PICTURE = 1;

    private final static String DROPBOX_FILE_DIR="/DropboDemo";
    private final static String DROPBOX_NAME="dropbox_prefs";
    private final static String ACCESS_KEY="0004xgftwq8is1d";
    private final static String ACCESS_SECRET="l8g9dvu9cf1xa5o";
    private final static Session.AccessType ACCESS_TYPE= Session.AccessType.DROPBOX;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = (Button) findViewById(R.id.login);
        loginButton.setOnClickListener(this);


        zrob = (Button) findViewById(R.id.zrob_zdjecie);
        zrob.setOnClickListener(this);


        loggedIn(false);
        AppKeyPair appKeyPair = new AppKeyPair(ACCESS_KEY,ACCESS_SECRET);
        AndroidAuthSession session;

        SharedPreferences prefs = getSharedPreferences(DROPBOX_NAME,0);
        String key = prefs.getString(ACCESS_KEY,null);
        String secret= prefs.getString(ACCESS_SECRET,null);

        if(key !=null && secret!=null){

            AccessTokenPair token = new AccessTokenPair(key,secret);
            session= new AndroidAuthSession(appKeyPair,ACCESS_TYPE,token);
        }else{
            session= new AndroidAuthSession(appKeyPair,ACCESS_TYPE);
        }

        dropboxAPI = new DropboxAPI(session);

    }


///akies
    @Override
    protected void onResume() {
        super.onResume();

        AndroidAuthSession session = (AndroidAuthSession)dropboxAPI.getSession();
        if(session.authenticationSuccessful()) {
            try {
                session.finishAuthentication();

                TokenPair tokens = session.getAccessTokenPair();
                SharedPreferences prefs = getSharedPreferences(DROPBOX_NAME, 0);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(ACCESS_KEY, tokens.key);
                editor.putString(ACCESS_SECRET, tokens.secret);
                editor.commit();

                loggedIn(true);
            } catch (IllegalStateException e) {
                Toast.makeText(this, "B³¹d podczas autoryzacji", Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.login:
                if(isUserLoggedIn){
                    dropboxAPI.getSession().unlink();
                    loggedIn(false);
                }else{
                    ((AndroidAuthSession)dropboxAPI.getSession()).startAuthentication(MainActivity.this);
                }
                break;
            //case R.id.upload :
                //UploadFile uploadFile = new UploadFile(this, dropboxAPI,DROPBOX_FILE_DIR);
               // uploadFile.execute();
               // break;
            case R.id.zrob_zdjecie:
                Intent intent = new Intent();

                // Picture from camera
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

                // This is not the right way to do this, but for some reason, having
                // it store it in
                // MediaStore.Images.Media.EXTERNAL_CONTENT_URI isn't working right.

                Date date = new Date();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd-kk-mm-ss");

                String newPicFile = df.format(date) + ".jpg";
                String outPath = "/sdcard/" + newPicFile;
                File outFile = new File(outPath);

                mCameraFileName = outFile.toString();
                Uri outuri = Uri.fromFile(outFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outuri);
                Log.i(TAG, "Importing New Picture: " + mCameraFileName);
                try {
                    startActivityForResult(intent, NEW_PICTURE);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }

                break;

            default:
                break;



        }

    }


    public void loggedIn(boolean userLoggedIn) {
        isUserLoggedIn = userLoggedIn;
        //upload.setEnabled(userLoggedIn);
        //upload.setBackgroundColor(userLoggedIn ? Color.BLUE : Color.GRAY);
        loginButton.setText(userLoggedIn ? "Logout" : "Log in");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_PICTURE) {
            // return from file upload
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = null;
                if (data != null) {
                    uri = data.getData();
                }
                if (uri == null && mCameraFileName != null) {
                    uri = Uri.fromFile(new File(mCameraFileName));
                }
                File file = new File(mCameraFileName);

                if (uri != null) {
                    UploadFile upload = new UploadFile(this, dropboxAPI,DROPBOX_FILE_DIR,file,mCameraFileName);
                    upload.execute();
                }
            } else {
                Log.w(TAG, "B³¹d intencji  "
                        + resultCode);
            }
        }
    }
}
