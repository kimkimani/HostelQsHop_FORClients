package ydkim2110.com.androidbarberbooking.Common;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.core.app.NotificationCompat;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;
import ydkim2110.com.androidbarberbooking.Database.CartDAO;
import ydkim2110.com.androidbarberbooking.Database.DataSource.CartRepository;
import ydkim2110.com.androidbarberbooking.Database.DataSource.ICartDataSource;
import ydkim2110.com.androidbarberbooking.Model.Hostel;
import ydkim2110.com.androidbarberbooking.Model.BookingInformation;
import ydkim2110.com.androidbarberbooking.Model.MyToken;
import ydkim2110.com.androidbarberbooking.Model.ShoppingItem;
import ydkim2110.com.androidbarberbooking.Model.hostelArea;
import ydkim2110.com.androidbarberbooking.Model.User;
import ydkim2110.com.androidbarberbooking.PDF;
import ydkim2110.com.androidbarberbooking.R;
import ydkim2110.com.androidbarberbooking.Retrofit.IDrinkShopAPI;
import ydkim2110.com.androidbarberbooking.Retrofit.RetrofitP;
import ydkim2110.com.androidbarberbooking.Retrofit.RetrofitScalarsClient;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class Common {

    public static final String BASE_URL = "http://tipsscorepro.com/";
    public static final String API_CHECKOUT_URL = "http://tipsscorepro.com/Payment/checkout.php";
    public static final String API_TOKEN_URL = "http://tipsscorepro.com/Payment/main.php";
    public static final String KEY_ENABLE_BUTTON_NEXT = "ENABLE_BUTTON_NEXT";
    public static final String KEY_UNABLE_BUTTON_NEXT = "UNABLE_BUTTON_NEXT";
    public static final String KEY_SALON_STORE = "SALON_STORE";
    public static final String KEY_BARBER_LOAD_DONE = "BARBER_LOAD_DONE";
    public static final String KEY_DISPLAY_TIME_SLOT = "DISPLAY_TIME_SLOT";
    public static final String KEY_STEP = "STEP";
    public static final int MAX_NOTIFICATION_PER_LOAD = 10;
    public static final String KEY_BARBER_SELECTED = "BARBER_SELECTED";
    public static final int TIME_SLOT_TOTAL = 20;
    public static final Object DISABLE_TAG = "DISABLE";
    public static final String KEY_TIME_SLOT = "TIME_SLOT";
    public static final String KEY_CONFIRM_BOOKING = "CONFIRM_BOOKING";
    public static final String EVENT_URI_CACHE = "URI_EVENT_SAVE";
    public static final String TITLE_KEY = "title";
    public static final String CONTENT_KEY = "content";
    public static final String LOGGED_KEY = "UserLogged";
    public static final String RATING_INFORMATION_KEY = "RATING_INFORMATION";

    public static final String RATING_STATE_KEY = "RATING_STATE";
    public static final String RATING_SALON_ID = "RATING_SALON_ID";
    public static final String RATING_SALON_NAME = "RATING_SALON_NAME";
    public static final String RATING_BARBER_ID = "RATING_BARBER_ID";
    public static final double DEFAULT_PRICE = 30000;
    public static final String MONEY_SIGN = "Ksh";

    public static String currentBookingId = "";
    public static User currentUser;
    public static hostelArea currentHostelArea;
    public static Hostel currentHostel;
    public  static List<ShoppingItem> mShoppingItemList;
    public static BookingInformation currentBooking;
    public static int currentTimeSlot=-1;
    public static int step = 0;
    public static String IS_LOGIN = "IsLogin";
    public static String gender = "";

    public static Calendar bookingDate = Calendar.getInstance();
    public static SimpleDateFormat simpleFormatDate = new SimpleDateFormat("dd_MM_yyyy");
    public static CartRepository cartRepository;

    public static String convertTimeSlotToString(int position) {
        switch (position) {
            case 0:
                return "RM 1 HEAD 1";
            case 1:
                return "RM 1 HEAD 2";
            case 2:
                return "RM 2 HEAD 1";
            case 3:
                return "RM 2 HEAD 2";
            case 4:
                return "RM 3 HEAD 1";
            case 5:
                return "RM 3 HEAD 2";
            case 6:
                return "RM 4 HEAD 1";
            case 7:
                return "RM 4 HEAD 2";
            case 8:
                return "RM 5 HEAD 1";
            case 9:
                return "RM 5 HEAD 2";
            case 10:
                return "RM 6 HEAD 1";
            case 11:
                return "RM 6 HEAD 2";
            case 12:
                return "RM 7 HEAD 1";
            case 13:
                return "RM 7 HEAD 2";
            case 14:
                return "RM 8 HEAD 1";
            case 16:
                return "RM 9 HEAD 1";
            case 17:
                return "RM 9 HEAD 2";
            case 18:
                return "RM 10 HEAD 1";
            case 19:
                return "RM 10 HEAD 2";
            case 20:
                return "RM 10 HEAD 3";
                default:
                    return "Closed!";
        }
    }
    public static IDrinkShopAPI getAPI() {
        return RetrofitP.getClient(BASE_URL).create(IDrinkShopAPI.class);
    }
    public static IDrinkShopAPI getScalarsAPI() {
        return RetrofitScalarsClient.getScalarsClient(BASE_URL).create(IDrinkShopAPI.class);
    }

    public static String convertTimeSlotToStringKey(Timestamp timestamp) {
        Date date = timestamp.toDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");
        return simpleDateFormat.format(date);
    }

    public static String formatShoppingItemName(String name) {
        return name.length() > 13 ? new StringBuilder(name.substring(0, 10)).append("...").toString() : name;
    }

    public static void showNotification(Context context, int noti_id, String title, String content, Intent intent) {
        PendingIntent pendingIntent = null;
        if (intent != null) {
            pendingIntent = PendingIntent.getActivity(context,
                    noti_id,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

        String NOTIFICATION_CHANNEL_ID = "HostelBooking_client_app";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "My Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});

            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);

        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(false)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));

        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent);
        }

        Notification notification = builder.build();

        notificationManager.notify(noti_id, notification);
    }

    public static void showRatingDialog(Context context, String stateName, String salonID,
                                        String salonName, String barberId) {
        Log.d(TAG, "showRatingDialog: called!!");
        // First, we need get DocumentReference of Hostel
        DocumentReference barberNeedRateRef = FirebaseFirestore.getInstance()
                .collection("gender")
                .document(stateName)
                .collection("Branch")
                .document(salonID)
                .collection("Hostel")
                .document(barberId);

        barberNeedRateRef.get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: "+e.getMessage());
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Log.d(TAG, "onComplete: called!!");
                        if (task.isSuccessful()) {
                            Hostel hostelRate = task.getResult().toObject(Hostel.class);
                            hostelRate.setHostelId(task.getResult().getId());

                            // Create View for dialog
                            View view = LayoutInflater.from(context)
                                    .inflate(R.layout.layout_rating_dialog, null);

                            // Widget
                            TextView txt_salon_name = view.findViewById(R.id.txt_salon_name);
                            TextView txt_barber_name = view.findViewById(R.id.txt_hostel_name);
                            AppCompatRatingBar ratingBar = view.findViewById(R.id.rating);

                            // Set Info
                            txt_salon_name.setText(salonName);
                            txt_barber_name.setText(hostelRate.getName());

                            // Create Dialog
                            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                                    .setView(view)
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // IF select OK, we will update
                                            // rating information to FireStore

                                            Double original_rating = hostelRate.getRating();
                                            Long ratingTimes = hostelRate.getRatingTimes();
                                            float userRating = ratingBar.getRating();

                                            Double finalRating = (original_rating+userRating);

                                            // Update barber
                                            Map<String, Object> data_update = new HashMap<>();
                                            data_update.put("rating", finalRating);
                                            // ++ratingTimes, not (ratingTimes+1) or (ratingTimes++)
                                            // because we need INCREASE FIRST AND USE
                                            data_update.put("ratingTimes", ++ratingTimes);
                                            
                                            barberNeedRateRef.update(data_update)
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(context, "Thank you for rating!", Toast.LENGTH_SHORT).show();
                                                                // Remove Key
                                                                Paper.init(context);
                                                                Paper.book().delete(Common.RATING_INFORMATION_KEY);
                                                            }
                                                        }
                                                    });
                                        }
                                    })
                                    .setNegativeButton("SKIP", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // If select Skip, we just dismiss dialog
                                            dialog.dismiss();
                                        }
                                    })
                                    .setNeutralButton("NEVER", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // If select Never,
                                            // That mean no rating, we will delete key
                                            Paper.init(context);
                                            Paper.book().delete(Common.RATING_INFORMATION_KEY);

                                        }
                                    });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                });


    }

    public static String getAppPath(Context context) {
        File dir = new File(android.os.Environment.getExternalStorageDirectory()

                + File.separator
                + context.getResources().getString((R.string.app_name))
                + File.separator
        );
        if (dir.exists())

            dir.mkdir();
            return dir.getPath()+File.separator;




    }

    public static enum TOKEN_TYPE {
        CLIENT,
        Admin,
        MANAGER
    }

    public static void updateToken(Context context, String token) {

        AccessToken accessToken = AccountKit.getCurrentAccessToken();
        if (accessToken != null) {
            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(Account account) {
                    MyToken myToken = new MyToken();
                    myToken.setToken(token);
                    // Because token come from client app
                    myToken.setTokenType(TOKEN_TYPE.CLIENT);
                    myToken.setUserPhone(account.getPhoneNumber().toString());

                    FirebaseFirestore.getInstance()
                            .collection("Tokens")
                            .document(account.getPhoneNumber().toString())
                            .set(myToken)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });
                }

                @Override
                public void onError(AccountKitError accountKitError) {

                }
            });
        }
        else {
            Paper.init(context);
            String user = Paper.book().read(Common.LOGGED_KEY);
            if (user != null) {
                if (!TextUtils.isEmpty(user)) {
                    MyToken myToken = new MyToken();
                    myToken.setToken(token);
                    // Because token come from client app
                    myToken.setTokenType(TOKEN_TYPE.CLIENT);
                    myToken.setUserPhone(user);

                    FirebaseFirestore.getInstance()
                            .collection("Tokens")
                            .document(user)
                            .set(myToken)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });
                }
            }
        }
    }
}
