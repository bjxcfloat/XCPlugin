package xc.lib.host.contentprovider;

import android.content.pm.ProviderInfo;
import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// ContentProvider管理器,支持并发
public class ContentProviderManager {
    private volatile static ContentProviderManager contentProviderManager = null;
    private static byte[] _lock = new byte[0];
    private ConcurrentHashMap<String, ContentProviderInfo> hm;

    public static ContentProviderManager getInstance() {
        if (contentProviderManager == null) {
            synchronized (_lock) {
                if (contentProviderManager == null) {
                    contentProviderManager = new ContentProviderManager();

                }
            }
        }
        return contentProviderManager;
    }

    private ContentProviderManager() {

        hm = new ConcurrentHashMap<>();
    }

    public void put(String clsname, ContentProviderInfo cp) {

        cp.cp.onCreate();
        hm.put(clsname, cp);
    }

    public ContentProviderInfo get(Uri url) {

        for (Map.Entry<String, ContentProviderInfo> entry : hm.entrySet()) {
            String path = url.getEncodedPath();

            if (path.contains("/" + entry.getKey() + "/")) {
                return entry.getValue();
            }

        }
        return null;
    }

    public void disposeContentProvider(HashMap<String, ProviderInfo> providerInfos) {
        List<String> keys = new ArrayList<>();
        for (Map.Entry<String, ContentProviderInfo> entry : hm.entrySet()) {
            if (providerInfos.containsKey(entry.getValue().clsName)) {
                entry.getValue().cp = null;
                keys.add(entry.getKey());

            }

        }
        for (String s : keys)
            hm.remove(s);

    }


}
