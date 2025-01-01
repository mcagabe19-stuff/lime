package org.haxe.lime;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.net.Uri;
import android.util.Log;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.database.ContentObserver;
import androidx.documentfile.provider.DocumentFile;
import org.haxe.extension.Extension;

public class DocumentSystem extends Extension
{
	public static final String LOG_TAG = "DocumentSystem";
	public HashMap<String, DocumentFile> documentFiles = new HashMap<String, DocumentFile>();
	public DocumentFile rootDocument;
	public Uri rootUri;

	public DocumentSystem(String treeUri)
	{
		rootUri = Uri.parse(treeUri);
		rootDocument = DocumentFile.fromTreeUri(mainContext, rootUri);
		cacheDocument(rootDocument, "");

		mainContext.getContentResolver().registerContentObserver(rootUri, true, new ContentObserver(Handler.createAsync(Looper.getMainLooper())) {
			@Override
			public void onChange(boolean selfChange, Uri uri)
			{
				Log.d(LOG_TAG, "Change detected at uri <" + uri.toString() + "> refreshing documents cache!");
				cacheDocument(rootDocument, "");

				super.onChange(selfChange, uri);
			}
		});
	}
	
    public byte[] readBytes(String path) throws IOException
	{
        DocumentFile file = getDocument(path, true);
        if (file == null || !file.exists() || !file.isFile())
		{
            throw new IOException("File not found or is not a file: " + path);
        }

        try (ParcelFileDescriptor pfd = mainContext.getContentResolver().openFileDescriptor(file.getUri(), "r");
             FileInputStream fis = new FileInputStream(pfd.getFileDescriptor())) {
            byte[] bytes = new byte[(int) file.length()];
            fis.read(bytes);
            return bytes;
        }
    }

    public void writeBytes(String path, byte[] data) throws IOException {
        DocumentFile file = getDocument(path, false);
		if (file == null)
		{
            file = createFile(path);
            if (file == null)
			{
                throw new IOException("Failed to create file: " + path);
            }
        }

        try (ParcelFileDescriptor pfd = mainContext.getContentResolver().openFileDescriptor(file.getUri(), "w");
             FileOutputStream fos = new FileOutputStream(pfd.getFileDescriptor())) {
            fos.write(data);
        }
    }

	public void reset(String uriStr)
	{
		documentFiles.clear();
		cacheDocument(rootDocument, "");
	}

	private DocumentFile createFile(String path) {
        DocumentFile current = rootDocument;
        String[] parts = path.split("/");

        for (int i = 0; i < parts.length - 1; i++)
		{
            DocumentFile next = current.findFile(parts[i]);
            if (next == null)
			{
                next = current.createDirectory(parts[i]);
            }
            current = next;
        }

        DocumentFile file = current.createFile("application/octet-stream", parts[parts.length - 1]);
        documentFiles.put(path, file);
        return file;
    }

	private void cacheDocument(DocumentFile root, String curPath)
	{
		for (DocumentFile file : root.listFiles())
		{
			if (file.isDirectory())
			{
				cacheDocument(file, curPath + file.getName() + "/");
			}
			else
			{
				if (!documentFiles.containsKey(curPath))
				{
					documentFiles.put(curPath + file.getName(), file);
					Log.d(LOG_TAG, "Cached a document for " + curPath);
				}
			}
		}
	}

	private DocumentFile getDocument(String path, boolean altCheck)
	{
		if (documentFiles.containsKey(path))
		{
			DocumentFile document = documentFiles.get(path);

			if (document.exists())
			{
				return document;
			}
			else
			{
				Log.e(LOG_TAG, "Document " + rootDocument.getUri().toString() + "with path <" + path + "> Doesn't seem to exist anymore");
				documentFiles.remove(path);
				return null;
			}
			return document.exists() ? document : null;
		}
		else if(altCheck)
		{
			DocumentFile curDocument = rootDocument;
			for (String segment : path.split("/"))
			{
				curDocument = curDocument.findFile(segment);
			}

			if (curDocument != null && !curDocument.isFile())
			{
				documentFiles.put(path, curDocument);
				return curDocument;
			}
			else
			{
				Log.e(LOG_TAG, "Could not find a DocumentFile of path <" + path + "> in Document " + rootDocument.getUri().toString());
				return null;
			}
		}
		else
		{
			return null;
		}
	}
}
