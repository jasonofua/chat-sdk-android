package co.chatsdk.android.app;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import java.util.HashMap;

import co.chatsdk.core.error.ChatSDKException;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.session.Configuration;
import co.chatsdk.firebase.FirebaseNetworkAdapter;
import co.chatsdk.firebase.file_storage.FirebaseFileStorageModule;
import co.chatsdk.firebase.push.FirebasePushModule;
import co.chatsdk.ui.manager.BaseInterfaceAdapter;

/**
 * Created by itzik on 6/8/2014.
 */
public class AppObj extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        Context context = getApplicationContext();

        Configuration.Builder config = new Configuration.Builder(context);
//        builder.firebaseRootPath("firebase_v4_web_new_4");
        config.firebaseRootPath("18_10");
        config.googleMaps("AIzaSyCwwtZrlY9Rl8paM0R6iDNBEit_iexQ1aE");
        config.publicRoomCreationEnabled(false);
        config.pushNotificationSound("default");
        config.pushNotificationsForPublicChatRoomsEnabled(true);

        try {
            ChatSDK.initialize(config.build(), new BaseInterfaceAdapter(context), new FirebaseNetworkAdapter());
        }
        catch (ChatSDKException e) {

        }

        FirebaseFileStorageModule.activate();
        FirebasePushModule.activate();
//        FirebaseUIModule.activate(context, EmailAuthProvider.PROVIDER_ID, PhoneAuthProvider.PROVIDER_ID);

        testHash();

    }

    void testHash () {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", "Ben");
        map.put("pet", "cat");
        map.put("address/number", "125");
        map.put("address/city", "Berlin");
        map.put("address/abode/room", "1");

        HashMap<String, Object> mapA = new HashMap<>();
        HashMap<String, Object> mapB = new HashMap<>();
        HashMap<String, Object> mapC = new HashMap<>();

        mapC.put("room", "1");

        mapB.put("number", "125");
        mapB.put("city", "Berlin");
        mapB.put("abode", mapC);

        mapA.put("name", "Ben");
        mapA.put("pet", "cat");
        mapA.put("address", mapB);

        HashMap<String, String> test1 = flatten(mapA);
        HashMap<String, Object> test2 = expand(map);

        // Recursive functions

    }

    public HashMap<String, String> flatten (HashMap<String, Object> map) {
        HashMap <String, String> flattenedMap = null;

        for (String key : map.keySet()) {
            Object value = map.get(key);
            if (value instanceof HashMap) {
                HashMap<String, Object> childHash = (HashMap<String, Object>) value;
                for (String childKey : childHash.keySet()) {
                    String compoundKey = key + "/" + childKey;
                    Object childValue = childHash.get(childKey);
                    if (childValue instanceof HashMap) {
                        HashMap<String, String> childChildHash = (HashMap<String, String>) childValue;
                        for (String childChildKey : childChildHash.keySet()) {
                            String compoundCompoundKey = compoundKey + "/" + childChildKey;
                            String childChildValue = childChildHash.get(childChildKey);
                            flattenedMap.put(compoundCompoundKey, childChildValue);
                        }
                    }
                    else {
                        String stringChildValue = childValue.toString();
                        flattenedMap.put(compoundKey, stringChildValue);
                    }
                }
            }
            else {
                String stringValue = value.toString();
                flattenedMap.put(key, stringValue);
            }
        }
        return flattenedMap;
    }

    public HashMap<String, Object> expand (HashMap<String, String> map) {
        return null;
    }

    @Override
    protected void attachBaseContext (Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
