package com.epicshaggy.filepicker;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.epicshaggy.filepicker.capacitorfilepicker.R;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

@NativePlugin(requestCodes = {FilePicker.FILE_PICK})
public class FilePicker extends Plugin {

    protected static final int FILE_PICK = 1010;

    private String[] getAllowedFileTypes(JSArray fileTypes) {
        ArrayList<String> typeList = new ArrayList<>();
        for (int i = 0; i < fileTypes.length(); i++) {
            try {
                typeList.add(fileTypes.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (typeList.size() > 0) {
            String[] accept = typeList.toArray(new String[0]);
            return accept;
        }
        return null;
    }

    @PluginMethod()
    public void showFilePicker(PluginCall call) {
        saveCall(call);
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        try {
            if(call.getArray("fileTypes").length() == 2){
                if (call.getArray("fileTypes").toString().contains("video") && call.getArray("fileTypes").toString().contains("image")) {
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"video/*", "image/*"});
                }else{
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"*/*"});
                }
            }else {
                if (call.getArray("fileTypes").toString().contains("image")) {
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*"});
                } else if (call.getArray("fileTypes").toString().contains("video")) {
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"video/*"});
                } else {
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"*/*"});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(call, intent, FILE_PICK);
    }

    @Override
    protected void handleOnActivityResult(int requestCode, int resultCode, Intent data) {
        super.handleOnActivityResult(requestCode, resultCode, data);
        PluginCall call = getSavedCall();

        switch (resultCode) {
            case Activity.RESULT_OK:
                if (requestCode == FILE_PICK) {
                    if (data != null) {

                        String mimeType = getContext().getContentResolver().getType(data.getData());
                        String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);

                        Cursor c = getContext().getContentResolver().query(data.getData(), null, null, null, null);
                        c.moveToFirst();
                        String name = c.getString(c.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        long size = c.getLong(c.getColumnIndex(OpenableColumns.SIZE));

                        JSObject ret = new JSObject();
                        try {
                            String path = copyFileToInternalStorage(data.getData(), getContext().getString(R.string.app_name));
                            path = path.startsWith("file://") ? path : "file://" + path;
                            ret.put("uri", path);
                        } catch (Exception e) {
                            e.printStackTrace();
                            ret.put("uri", "");
                        }
                        ret.put("name", name);
                        ret.put("mimeType", mimeType);
                        ret.put("extension", extension);
                        ret.put("size", size);
                        call.resolve(ret);
                    }
                }
                break;
            case Activity.RESULT_CANCELED:
                call.reject("File picking was cancelled.");
                break;
            default:
                call.reject("An unknown error occurred.");
                break;
        }
    }

    private String copyFileToInternalStorage(Uri uri, String newDirName) {
        Uri returnUri = uri;

        Cursor returnCursor = getContext().getContentResolver().query(returnUri, new String[]{
                OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE
        }, null, null, null);


        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));

        File output;
        if (!newDirName.equals("")) {
            File dir = new File(getContext().getFilesDir() + "/" + newDirName);
            if (!dir.exists()) {
                dir.mkdir();
            }
            output = new File(getContext().getFilesDir() + "/" + newDirName + "/" + name);
        } else {
            output = new File(getContext().getFilesDir() + "/" + name);
        }
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(output);
            int read = 0;
            int bufferSize = 1024;
            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }

            inputStream.close();
            outputStream.close();

        } catch (Exception e) {

            Log.e("Exception", e.getMessage());
        }

        return output.getPath();
    }
}
