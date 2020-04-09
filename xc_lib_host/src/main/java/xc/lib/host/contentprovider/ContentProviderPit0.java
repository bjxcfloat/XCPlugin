package xc.lib.host.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

// ContentProvider宿主容器，用于插件的ContentProvider走生命周期,执行业务
public class ContentProviderPit0 extends ContentProvider {

    private static String authority = "privoders0";

    public ContentProviderPit0() {
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

//        Log.e("sadsd","host-delete-"+uri.getAuthority()+"-"+uri.getEncodedPath()+"-"+uri.getEncodedQuery());
        ContentProviderInfo cp = ContentProviderManager.getInstance().get(uri);

        if (cp != null) {
            return cp.cp.delete(uri, selection, selectionArgs);


        }
        return -1;
    }

    @Override
    public String getType(Uri uri) {
        ContentProviderInfo cp = ContentProviderManager.getInstance().get(uri);

        if (cp != null) {
            return cp.cp.getType(uri );


        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        ContentProviderInfo cp = ContentProviderManager.getInstance().get(uri);

        if (cp != null) {
            return cp.cp.insert(uri,   values);


        }
        return null;
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        ContentProviderInfo cp = ContentProviderManager.getInstance().get(uri);

        if (cp != null) {
            return cp.cp.query( uri,   projection,   selection,
                     selectionArgs,   sortOrder);


        }
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        ContentProviderInfo cp = ContentProviderManager.getInstance().get(uri);

        if (cp != null) {
            return cp.cp.update(   uri,   values,   selection,
                    selectionArgs);


        }
        return -1;
    }
}
