package io.launchowl.viewvalidationapp;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class UserRepository {
    private final Set<User> users = new HashSet<>(Arrays.asList(
       new User("realteal"), new User("drysandpiper"), new User("thunderousgull"), new User("regalkangaroo")
    ));

    public void getUser(final String userName, final OnuserRetrievedListener onUuserRetrievedListener) {
        final Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                onUuserRetrievedListener.onUserRetrieved(findUser(userName));
            }
        };


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(300, 1300));
                    Message message = handler.obtainMessage();
                    message.sendToTarget();

                } catch (InterruptedException e ) { }

            }
        });
        thread.start();
    }

    User findUser(String userName) {
        for (User user : users) {
            if (user.userName().equals(userName)) {
                return user;
            }
        }

        return null;
    }

    interface OnuserRetrievedListener {
        void onUserRetrieved(User user);
    }
}
