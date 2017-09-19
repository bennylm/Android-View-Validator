package io.launchowl.viewvalidation.sampleapp;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A mock user repository.
 */
public class UserRepository {

    // A collection of existing usernames.
    private final Set<User> users = new HashSet<>(Arrays.asList(
       new User("realkiwi"), new User("happyorange"), new User("iceapple"), new User("coolblueberry")
    ));


    /**
     * Retrieves a user.
     *
     * @param userName username
     * @param onUuserRetrievedListener an {@link OnuserRetrievedListener}
     */
    void getUser(final String userName, final OnuserRetrievedListener onUuserRetrievedListener) {

        /*
         * Create a Handler to provide response on the main UI thread. If
         * nUserRetrievesListener.onUserRetrieved(User user) is called
         * on a separate thread then a FATAL EXCEPTION will occur and an
         * exception will be thrown.
         *
         * In this case, android.util.AndroidRuntimeException: Animators may only be run on Looper threads.
         *
         * To learn about communicating with the UI thread, see "Communicating with the UI Thread":
         * https://developer.android.com/training/multiple-threads/communicate-ui.html
         *
         */
        final Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                onUuserRetrievedListener.onUserRetrieved(findUser(userName));
            }
        };


        /*
         * Create a Thread that will respond with the result after
         * a random amount of time.
         *
         * The goal is to simulate a remote web service.
         */
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    /* Sleep for a random number of milliseconds. Execute in a Thread to prevent
                     * blocking Android's main  UI thread.
                     */
                    Thread.sleep(ThreadLocalRandom.current().nextInt(300, 1500));

                    // Tell the handler to continue.
                    Message message = handler.obtainMessage();
                    message.sendToTarget();

                } catch (InterruptedException e ) { }

            }
        });
        thread.start();
    }

    /**
     * Locates the user by looking for the provided username in the username collection.
     *
     * @param userName a username
     * @return a {@link User}
     */
    private User findUser(String userName) {
        for (User user : users) {
            if (user.userName().equals(userName)) {
                return user;
            }
        }

        return null;
    }

    /**
     * An interface for responding to a user retrieval request.
     */
    interface OnuserRetrievedListener {
        void onUserRetrieved(User user);
    }
}
