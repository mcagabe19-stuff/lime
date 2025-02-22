package org.haxe.lime;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;

public class CustomFileProvider extends ContentProvider {

    private static final String AUTHORITY = "::APP_PACKAGE::.fileprovider";
    private static final int FILES = 1;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, "*", FILES);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        File file = new File(getContext().getFilesDir(), uri.getLastPathSegment());
        
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + uri.toString());
        }
        
        int modeFlag = ParcelFileDescriptor.MODE_READ_ONLY;

        // If you want to allow write access, check the mode
        if (mode.contains("w")) {
            modeFlag = ParcelFileDescriptor.MODE_READ_WRITE;
        }

        return ParcelFileDescriptor.open(file, modeFlag);
    }

    @Override
    public String getType(Uri uri) {
        // You can return more specific types based on file extension if needed
        return "application/octet-stream";
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        File file = new File(getContext().getFilesDir(), uri.getLastPathSegment());
        try {
            // Handle file creation (writing)
            if (!file.exists()) {
                file.createNewFile(); // Create the file if it doesn't exist
            }
            return Uri.parse("content://com.yourapp.fileprovider/" + file.getName());
        } catch (IOException e) {
            Log.e("CustomFileProvider", "Insert failed", e);
            throw new UnsupportedOperationException("Insert not supported");
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        File file = new File(getContext().getFilesDir(), uri.getLastPathSegment());

        if (file.exists() && file.delete()) {
            return 1; // Return the number of rows deleted (in this case, it's just 1 file)
        }

        return 0; // No file deleted
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        File file = new File(getContext().getFilesDir(), uri.getLastPathSegment());

        if (file.exists()) {
            try {
                // Example of writing data to the file (use `values` or some other input)
                try (OutputStream out = new FileOutputStream(file)) {
                    // Write data to the file (example: use values or other data)
                    out.write("Updated content".getBytes());
                }
                return 1; // Successfully updated 1 file
            } catch (IOException e) {
                Log.e("CustomFileProvider", "Update failed", e);
                return 0;
            }
        }

        return 0; // File does not exist, no update
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        MatrixCursor cursor = new MatrixCursor(new String[]{"_display_name", "_size"});
        File file = new File(getContext().getFilesDir(), uri.getLastPathSegment());

        if (file.exists()) {
            cursor.addRow(new Object[]{file.getName(), file.length()});
        }

        return cursor;
    }
}

