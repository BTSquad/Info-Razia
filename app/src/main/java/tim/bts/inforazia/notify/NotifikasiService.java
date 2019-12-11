package tim.bts.inforazia.notify;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import tim.bts.inforazia.R;
import tim.bts.inforazia.view.HomeActivity;

public class NotifikasiService extends FirebaseMessagingService {

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private static final String CHANNEL_ID = "info-razia";


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String sented = remoteMessage.getData().get("sented");

        if (firebaseUser != null && sented.equals(firebaseUser.getUid()))
        {
            tampilNotif(remoteMessage);
        }else {
            tampilNotif(remoteMessage);
        }


    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        if (firebaseUser != null)
        {
            updateToken(s);
        }

    }

    private void updateToken(String s) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("tokens");
        Token token = new Token(s);
        reference.child(firebaseUser.getUid()).setValue(token);

    }

    public void tampilNotif(RemoteMessage remoteMessage){

        createNotificationChannel();

        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        RemoteMessage.Notification notification = remoteMessage.getNotification();

        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.pop_up_logo)
                .setContentTitle("Razia Terbaru")
                .setContentText(body)
               .setContentIntent(pendingIntent)
                // Set the intent that will fire when the user taps the notification
                .setAutoCancel(true);

        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(9999, builder.build());


//        Picasso.get().load(icon).into(new Target() {
//            @Override
//            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                builder.setLargeIcon(bitmap);
//           }
//
//            @Override
//            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
//
//            }
//
//            @Override
//            public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//            }
//        });



    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}