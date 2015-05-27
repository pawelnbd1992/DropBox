package com.example.pawel.dropbox;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxServerException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class UploadFile extends AsyncTask<Void, Void, Boolean>{

    private DropboxAPI dropboxApi;
    private String path;
    private Context context;
    private DropboxAPI.UploadRequest mRequest;
    private File file;
    private String name;


    public UploadFile(Context context, DropboxAPI dropboxApi, String path,File file,String name ) {
        super();
        this.dropboxApi = dropboxApi;
        this.path = path;
        this.context = context;
        this.file=file;
        this.name=name;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        try {
            try {
                dropboxApi.metadata(path, 0, null, false, null);
            }
            catch(DropboxServerException e) {
                dropboxApi.createFolder(path);
            }
            //File file = new File(Environment.getExternalStorageDirectory(),"Pictures/s.jpg");
            FileInputStream is = new FileInputStream(file);
            dropboxApi.putFile(path+name, is, file.length(), null,null);

            Log.i("Script", "Revision HASH (uploadFile):");
            return true;
        }
        catch (DropboxException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if(result) {
            Toast.makeText(context, "Plik wgrany!",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Błąd podczas wgrywania zdjęcia!",
                    Toast.LENGTH_LONG).show();
        }
    }
}
