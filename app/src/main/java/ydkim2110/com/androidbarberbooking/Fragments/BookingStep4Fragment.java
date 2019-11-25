package ydkim2110.com.androidbarberbooking.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.interfaces.HttpResponseCallback;
import com.braintreepayments.api.internal.HttpClient;
import com.braintreepayments.api.models.PaymentMethodNonce;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.StringValue;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import ydkim2110.com.androidbarberbooking.Common.Common;
import ydkim2110.com.androidbarberbooking.Database.CartDatabase;
import ydkim2110.com.androidbarberbooking.Database.CartItem;
import ydkim2110.com.androidbarberbooking.Database.DatabaseUtils;
import ydkim2110.com.androidbarberbooking.Interface.ICartItemLoadLitener;
import ydkim2110.com.androidbarberbooking.Interface.ICartItemUpdateListener;
import ydkim2110.com.androidbarberbooking.Interface.ISumCartListener;
import ydkim2110.com.androidbarberbooking.Model.BookingInformation;
import ydkim2110.com.androidbarberbooking.Model.EventBus.ConfirmBookingEvent;
import ydkim2110.com.androidbarberbooking.Model.FCMResponse;
import ydkim2110.com.androidbarberbooking.Model.FCMSendData;
import ydkim2110.com.androidbarberbooking.Model.MyNotification;
import ydkim2110.com.androidbarberbooking.Model.MyToken;
import ydkim2110.com.androidbarberbooking.PDF;
import ydkim2110.com.androidbarberbooking.R;
import ydkim2110.com.androidbarberbooking.Retrofit.IDrinkShopAPI;
import ydkim2110.com.androidbarberbooking.Retrofit.IFCMApi;
import ydkim2110.com.androidbarberbooking.Retrofit.RetrofitClient;
import ydkim2110.com.androidbarberbooking.WalletActivity;
import ydkim2110.com.androidbarberbooking.permission.PermissionsActivity;
import ydkim2110.com.androidbarberbooking.permission.PermissionsChecker;
import ydkim2110.com.androidbarberbooking.utils.FileUtils;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


import static com.facebook.accountkit.internal.AccountKitController.getApplicationContext;
import static ydkim2110.com.androidbarberbooking.Common.Common.API_CHECKOUT_URL;
import static ydkim2110.com.androidbarberbooking.Common.Common.API_TOKEN_URL;
import static ydkim2110.com.androidbarberbooking.permission.PermissionsActivity.PERMISSION_REQUEST_CODE;
import static ydkim2110.com.androidbarberbooking.permission.PermissionsChecker.REQUIRED_PERMISSION;
import static ydkim2110.com.androidbarberbooking.utils.LogUtils.LOGE;

public class BookingStep4Fragment extends Fragment implements ICartItemLoadLitener {

    private static final String TAG = BookingStep4Fragment.class.getSimpleName();
    String token, amount;
    private IDrinkShopAPI mService;
    private IDrinkShopAPI mServiceScalars;
    TextView textView;
    @BindView(R.id.txt_total)
    TextView total;
    LinearLayout group_payment;
    HashMap<String, String> params;
    private static BookingStep4Fragment instance;
    public static ISumCartListener mListener;
    private static final int REQUEST_CODE = 1234;
    private boolean aBoolean;
    //String API_GET_TOKEN = API_TOKEN_URL;
   // String API_CHECKOUT = API_CHECKOUT_URL;

    public static BookingStep4Fragment getInstance() {
        if (instance == null) {
            instance = new BookingStep4Fragment();
        }
        return instance;
    }
    Context mContext;

    PermissionsChecker checker;
    private SimpleDateFormat mSimpleDateFormat;
    private LocalBroadcastManager mLocalBroadcastManager;
    private AlertDialog mDialog;
    private IFCMApi mIFCMApi;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    @BindView(R.id.txt_booking_barber_text)
    TextView txt_booking_barber_text;
    @BindView(R.id.txt_booking_time_text)
    TextView txt_booking_time_text;
    @BindView(R.id.txt_salon_salon_address)
    TextView txt_salon_salon_address;
    @BindView(R.id.txt_salon_name)
    TextView txt_salon_name;
    @BindView(R.id.txt_salon_open_hours)
    TextView txt_salon_open_hours;
    @BindView(R.id.txt_salon_phone)
    TextView txt_salon_phone;
    @BindView(R.id.txt_salon_website)
    TextView txt_salon_website;

    String ordernumber;

    private CartDatabase mCartDatabase;
    private Button confirm;
    private Button pay;
    private static final String ARG_TEXT = "argText";
    private static final String ARG_NUMBER = "argNumber";

//    private String text;
    private String number = String.valueOf(Common.DEFAULT_PRICE);

//    public static BookingStep4Fragment newInstance(String number) {
//        BookingStep4Fragment fragment = new BookingStep4Fragment();
//        Bundle args = new Bundle();
////        args.putString(ARG_TEXT, text);
//        args.putString(ARG_NUMBER, number);
//        fragment.setArguments(args);
//        return fragment;
//    }


    //    void btn_place_order() {
//        mDialog.show();
//
//        DatabaseUtils.getAllCart(CartDatabase.getInstance(getContext()), this);
//    }
//
//    @OnClick(R.id.pay)
//    void pay() {
//        submitPayment();
//    }
    private void addToUserBooking(BookingInformation bookingInformation) {
        Log.d(TAG, "addToUserBooking: called");

        // First, create new collection
        CollectionReference userBooking = FirebaseFirestore.getInstance()
                .collection("User")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Booking");

        // Get current date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        Timestamp todayTimestamp = new Timestamp(calendar.getTime());

        // Check if exist document in this collection
        // if have any document with field done = false;
        userBooking
                .whereGreaterThanOrEqualTo("timestamp", todayTimestamp)
                .whereEqualTo("done", false)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty()) {
                            // Set data
                            userBooking.document()
                                    .set(bookingInformation)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            // Create notification
                                            MyNotification myNotification = new MyNotification();
                                            myNotification.setUid(UUID.randomUUID().toString());
                                            myNotification.setTitle(
                                                    Common.currentHostelArea.getName() +
                                                            "--" +
                                                            Common.currentHostel.getName() +
                                                            "--" +
                                                            "NEW BOOKING"
                                            );
                                            myNotification.setContent("You have new booking from ----" +
                                                    Common.currentUser.getName() +
                                                    "----PhoneNumber----" +
                                                    Common.currentUser.getPhoneNumber()
                                                    + "--ROOM--" + Common.currentBooking.getTime()
                                            );

                                            // We will only filter notification with 'read' is false on barber staff
                                            myNotification.setRead(false);
                                            myNotification.setServerTimestamp(new Timestamp(calendar.getTime()));
///gender/gents/Branch/4jydSfTfDi3o26owKCFp/Hostel
                                            // Submit Notification to 'Notifications' collection of Hostel
                                            FirebaseFirestore.getInstance()
                                                    .collection("gender")
                                                    .document(Common.gender)
                                                    .collection("Branch")
                                                    .document(Common.currentHostelArea.getHoatelAreaId())
                                                    .collection("Hostel")
                                                    .document(Common.currentHostel.getHostelId())
                                                    // If  it not available, it will be create automatically
                                                    .collection("Notifications")
                                                    // Create unique key
                                                    .document(myNotification.getUid())
                                                    .set(myNotification)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // First, get Token base on Hostel id
                                                            FirebaseFirestore.getInstance()
                                                                    .collection("Tokens")
                                                                    .whereEqualTo("userPhone", Common.currentHostel.getUsername())
                                                                    .limit(1)
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @SuppressLint("CheckResult")
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            if (task.isSuccessful() && task.getResult().size() > 0) {
                                                                                MyToken myToken = new MyToken();
                                                                                for (DocumentSnapshot tokenSnapshot : task.getResult()) {
                                                                                    myToken = tokenSnapshot.toObject(MyToken.class);
                                                                                }

                                                                                // Create data to send
                                                                                FCMSendData sendRequest = new FCMSendData();
                                                                                Map<String, String> dataSend = new HashMap<>();
                                                                                dataSend.put(Common.TITLE_KEY, "New Booking");
                                                                                dataSend.put(Common.CONTENT_KEY, "You have new booking from user "
                                                                                        + Common.currentUser.getName());

                                                                                sendRequest.setTo(myToken.getToken());
                                                                                sendRequest.setData(dataSend);

                                                                                mCompositeDisposable.add(mIFCMApi.sendNotification(sendRequest)
                                                                                        .subscribeOn(Schedulers.io())
                                                                                        .observeOn(AndroidSchedulers.mainThread())
                                                                                        .subscribe(new Consumer<FCMResponse>() {
                                                                                            @Override
                                                                                            public void accept(FCMResponse fcmResponse) throws Exception {

                                                                                                mDialog.dismiss();

                                                                                                // addToCalendar(Common.bookingDate,
                                                                                                // Common.convertTimeSlotToString(Common.currentTimeSlot));
//                                                                                                resetStaticData();
                                                                                                Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        }, new Consumer<Throwable>() {
                                                                                            @Override
                                                                                            public void accept(Throwable throwable) throws Exception {
                                                                                                Log.d(TAG, "NOTIFICATION_ERROR: " + throwable.getMessage());
                                                                                                //   addToCalendar(Common.bookingDate,
                                                                                                //  Common.convertTimeSlotToString(Common.currentTimeSlot));
                                                                                                resetStaticData();
//                                                                                                getActivity().finish();
                                                                                                Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        }));
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            mDialog.dismiss();
                                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            if (mDialog.isShowing())
                                mDialog.dismiss();

                            resetStaticData();
                            // close activity
//                            getActivity().finish();
                            Toast.makeText(getContext(), "Success!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

//    private void addToCalendar(Calendar bookingDate, String startDate) {
//        Log.d(TAG, "addToCalendar: called!!");
//
//        String startTime = Common.convertTimeSlotToString(Common.currentTimeSlot);
//        String[] convertTime = startTime.split("~");
//
//        String[] startTimeConvert = convertTime[0].split(":");
//        int startHourInt = Integer.parseInt(startTimeConvert[0].trim());
//        int startMinInt = Integer.parseInt(startTimeConvert[1].trim());
//
//        String[] endTimeConvert = convertTime[1].split(":");
//        int endHourInt = Integer.parseInt(endTimeConvert[0].trim());
//        int endMinInt = Integer.parseInt(endTimeConvert[1].trim());
//
//        Calendar startEvent = Calendar.getInstance();
//        startEvent.setTimeInMillis(bookingDate.getTimeInMillis());
//        startEvent.set(Calendar.HOUR_OF_DAY, startHourInt); // Set event start hour
//        startEvent.set(Calendar.MINUTE, startMinInt); // Set event start min
//
//        Calendar endEvent = Calendar.getInstance();
//        endEvent.setTimeInMillis(bookingDate.getTimeInMillis());
//        endEvent.set(Calendar.HOUR_OF_DAY, endHourInt); // Set event start hour
//        endEvent.set(Calendar.MINUTE, endMinInt); // Set event start min
//
//        // After we have startEvent and endEvent, convert, convert it to format String
//        SimpleDateFormat calendarDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
//        String startEventTime = calendarDateFormat.format(startEvent.getTime());
//        String endEventTime = calendarDateFormat.format(endEvent.getTime());
//
//        addToDeviceCalendar(startEventTime, endEventTime, "Haircut Booking",
//                new StringBuilder("Hair cut from")
//                        .append(startTime)
//                        .append(" with ")
//                        .append(Common.currentHostel.getName())
//                        .append(" at ")
//                        .append(Common.currentHostelArea.getName()).toString(),
//                                new StringBuilder("Address: ").append(Common.currentHostelArea.getAddress()).toString());
//    }toString

    private void addToDeviceCalendar(String startEventTime, String endEventTime, String title,
                                     String description, String location) {
        Log.d(TAG, "addToDeviceCalendar: called!!");

        SimpleDateFormat calendarDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        try {
            Date start = calendarDateFormat.parse(startEventTime);
            Date end = calendarDateFormat.parse(endEventTime);

            ContentValues event = new ContentValues();

            event.put(CalendarContract.Events.CALENDAR_ID, getCalendar(getContext()));
            event.put(CalendarContract.Events.TITLE, title);
            event.put(CalendarContract.Events.DESCRIPTION, description);
            event.put(CalendarContract.Events.EVENT_LOCATION, location);

            event.put(CalendarContract.Events.DTSTART, start.getTime());
            event.put(CalendarContract.Events.DTEND, end.getTime());
            event.put(CalendarContract.Events.ALL_DAY, 0);
            event.put(CalendarContract.Events.HAS_ALARM, 1);

            String timezone = TimeZone.getDefault().getID();
            event.put(CalendarContract.Events.EVENT_TIMEZONE, timezone);

            Uri calendars;
            if (Build.VERSION.SDK_INT >= 8)
                calendars = Uri.parse("content://com.android.calendar/events");
            else
                calendars = Uri.parse("content://calendar/events");

            Uri uri_save = getActivity().getContentResolver().insert(calendars, event);

            // Save to cache
            Paper.init(getActivity());
            Paper.book().write(Common.EVENT_URI_CACHE, uri_save.toString());


        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private String getCalendar(Context context) {
        Log.d(TAG, "getCalendar: called!!");

        // Get default calendar ID of Calendar of Gmail
        String gmailIdCalendar = "";
        String projection[] = {"_id", "calendar_displayName"};
        Uri calendars = Uri.parse("content://com.android.calendar/calendars");

        ContentResolver contentResolver = context.getContentResolver();
        // Select all calendar
        Cursor managedCursor = contentResolver.query(calendars, projection, null, null, null);
        if (managedCursor.moveToFirst()) {
            String calName;
            int nameCol = managedCursor.getColumnIndex(projection[1]);
            int idCol = managedCursor.getColumnIndex(projection[0]);
            do {
                calName = managedCursor.getString(nameCol);
                if (calName.contains("@gmail.com")) {
                    gmailIdCalendar = managedCursor.getString(idCol);
                    break;
                }
            } while (managedCursor.moveToNext());
            managedCursor.close();
        }
        return gmailIdCalendar;
    }

    private void resetStaticData() {
        Log.d(TAG, "resetStaticData: called!!");

        Common.step = 0;
        Common.currentTimeSlot = -1;
        Common.currentHostelArea = null;
        Common.currentHostel = null;
        Common.bookingDate.add(Calendar.DATE, 0);
    }

    Unbinder mUnbinder;

//    BroadcastReceiver confirmBookingReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Log.d(TAG, "onReceive: called!!");
//            setData();
//        }
//    };


    //=============================================================================
    // EventBus start
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void setDataBooking(ConfirmBookingEvent event) {
        if (event.isConfirm()) {
            setData();
        }
    }
    //=============================================================================

    private void setData() {
        Log.d(TAG, "setData: called!!");
        txt_booking_barber_text.setText(Common.currentHostel.getName());
        txt_booking_time_text.setText(new StringBuilder(Common.convertTimeSlotToString(Common.currentTimeSlot))
                .append(" at ")
                .append(mSimpleDateFormat.format(Common.bookingDate.getTime())));

        txt_salon_salon_address.setText(Common.currentHostelArea.getAddress());
        txt_salon_website.setText(Common.currentHostelArea.getWebsite());
        txt_salon_name.setText(Common.currentHostelArea.getName());
        txt_salon_open_hours.setText(Common.currentHostelArea.getOpenHours());

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: started!!");

        mIFCMApi = RetrofitClient.getInstance().create(IFCMApi.class);


        // Ally format for date display on confirm
        mSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        //mLocalBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        //mLocalBroadcastManager.registerReceiver(confirmBookingReceiver, new IntentFilter(Common.KEY_CONFIRM_BOOKING));

        mDialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
    }

    @Override
    public void onDestroy() {
        //mLocalBroadcastManager.unregisterReceiver(confirmBookingReceiver);
        mCompositeDisposable.clear();
        super.onDestroy();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.d(TAG, "onCreate: started!!");
        View view = inflater.inflate(R.layout.fragment_booking_step_four, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mCartDatabase = CartDatabase.getInstance(getContext());
        // DatabaseUtils.getAllCart(mCartDatabase,getInstance());



        mContext = getApplicationContext();

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        checker = new PermissionsChecker(getContext());


        group_payment = (LinearLayout) view.findViewById(R.id.payment_group);
        total.setText(number);

       //

//
//        SharedPreferences preferences = getActivity().getSharedPreferences("MyData",
//                Context.MODE_PRIVATE);
//        String name=preferences.getString("name", DEFAULT);
//        if(name.equals(DEFAULT)){
//            Toast.makeText(getContext(),"error Data",Toast.LENGTH_LONG).show();
//        }
//        else {
//            total.setText(name);
//
//            Toast.makeText(getContext(),"Loaded Data"+total,Toast.LENGTH_LONG).show();
//
//
//        }


//        if (getArguments() != null) {
//            number = getArguments().getString(ARG_TEXT);
////            number = getArguments().getInt(ARG_NUMBER);
//        }
//
//        total.setText( number);

        pay = view.findViewById(R.id.btn_confirm);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checker.lacksPermissions(REQUIRED_PERMISSION)) {
                    PermissionsActivity.startActivityForResult(getActivity(), PERMISSION_REQUEST_CODE, REQUIRED_PERMISSION);
                } else {
                    createPdf(FileUtils.getAppPath(mContext) + "Qless.pdf");
                }
                Confirm();

            }
        });

        confirm = view.findViewById(R.id.pay);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (checker.lacksPermissions(REQUIRED_PERMISSION)) {
//                    PermissionsActivity.startActivityForResult(getActivity(), PERMISSION_REQUEST_CODE, REQUIRED_PERMISSION);
//                } else {
//                    createPdf(FileUtils.getAppPath(mContext) + "Qless.pdf");
//                }
                submitPayment();
            }
        });
      //  loadToken();
        new getToken().execute();
        generateCode();


        return view;
    }

    private void Confirm() {
        mDialog.show();
//
        DatabaseUtils.getAllCart(CartDatabase.getInstance(getContext()), this);
    }

    @Override
    public void onGetAllItemFromCartSuccess(List<CartItem> cartItemList) {
        Log.d(TAG, "onGetAllItemFromCartSuccess: called!!");
        // we wil have all item on cart
        // Process TimeStamp
        // We will use TimeStamp to filter all booking with date is greater today
        // For only display all future booking
//        String startTime = Common.convertTimeSlotToString(Common.currentTimeSlot);
//        String[] convertTime = startTime.split("~");
//
//        String[] startTimeConvert = convertTime[0].split(":");
//        int startHourInt = Integer.parseInt(startTimeConvert[0].trim());
//        int startMinInt = Integer.parseInt(startTimeConvert[1].trim());

        Calendar bookingDateWithoutHours = Calendar.getInstance();
        bookingDateWithoutHours.setTimeInMillis(Common.bookingDate.getTimeInMillis());
//        bookingDateWithoutHours.set(Calendar.HOUR_OF_DAY, startHourInt);
//        bookingDateWithoutHours.set(Calendar.MINUTE, startMinInt);

        // Create timestamp object and apply to BookingInformation
        Timestamp timestamp = new Timestamp(bookingDateWithoutHours.getTime());

        // creating booking information
        BookingInformation bookingInformation = new BookingInformation();

        bookingInformation.setCityBook(Common.gender);
        bookingInformation.setTimestamp(timestamp);
        // Always false, because we will use this field to filter for display
        bookingInformation.setDone(false);
        bookingInformation.setHostelId(Common.currentHostel.getHostelId());
        bookingInformation.setHostelName(Common.currentHostel.getName());
        bookingInformation.setCustomerName(Common.currentUser.getName());
        bookingInformation.setCustomerPhone(Common.currentUser.getPhoneNumber());
        bookingInformation.setHoatelAreaId(Common.currentHostelArea.getHoatelAreaId());
        bookingInformation.setHoatelAreaAddress(Common.currentHostelArea.getAddress());
        bookingInformation.setHoatelAreaName(Common.currentHostelArea.getName());

        bookingInformation.setTime(mSimpleDateFormat.format(bookingDateWithoutHours.getTime()));
//                .append(" at ")
//                .append(mSimpleDateFormat.format(bookingDateWithoutHours.getTime())).toString());

//        bookingInformation.setTime(new StringBuilder(Common.convertTimeSlotToString(Common.currentTimeSlot))
//                .append(" at ")
//                .append(mSimpleDateFormat.format(bookingDateWithoutHours.getTime())).toString());
        bookingInformation.setSlot(Long.valueOf(Common.currentTimeSlot));
        // Add cart item list to booking information
        bookingInformation.setCartItemList(cartItemList);
///gender/gents/Branch/4jydSfTfDi3o26owKCFp/Hostel
        // submit barber document
        DocumentReference bookingDate = FirebaseFirestore.getInstance()
                .collection("gender")
                .document(Common.gender)
                .collection("Branch")
                .document(Common.currentHostelArea.getHoatelAreaId())
                .collection("Hostel")
                .document(Common.currentHostel.getHostelId())
                .collection(Common.simpleFormatDate.format(Common.bookingDate.getTime()))
                .document(String.valueOf(Common.currentTimeSlot));

        // write data
        bookingDate.set(bookingInformation)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Here we can write an function to check
                        // If already exist an booking, we will prevent new booking

                        // After add success booking information, just clear cart
                        DatabaseUtils.clearCart(CartDatabase.getInstance(getContext()));
                        addToUserBooking(bookingInformation);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "" + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void submitPayment() {
        // Payment
        DropInRequest dropInRequest = new DropInRequest().clientToken(token);
        startActivityForResult(dropInRequest.getIntent(getContext()), REQUEST_CODE);

    }

    private void sendPayments() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_CHECKOUT_URL,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.toString().contains("Successful")) {
                            confirm.setVisibility(View.VISIBLE);
                            Toast.makeText(getActivity(), "Payment Success", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Payment Failed", Toast.LENGTH_SHORT).show();
                            pay.setVisibility(View.GONE);
                        }
                        Log.d("Response", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Err", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if (params == null)
                    return null;
                Map<String, String> params = new HashMap<>();
                for (String key : params.keySet()) {
                    params.put(key, params.get(key));
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(mRetryPolicy);
        queue.add(stringRequest);
    }
    private void sendPayment() {
        Log.d(TAG, "sendPayment: called");
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_CHECKOUT_URL,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.toString().contains("Successful")) {
                            confirm.setVisibility(View.VISIBLE);
                            pay.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "Payment Success", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Payment Failed", Toast.LENGTH_SHORT).show();
                        }
                        Log.d("Response", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Err", error.toString());
            }
        });
//        mServiceScalars.payment(params.get("nonce"), params.get("amount"))
//                .enqueue(new Callback<String>() {
//                    @Override
//                    public void onResponse(Call<String> call, retrofit2.Response<String> response) {
//                        if (response.body().toString().contains("Successful")) {
//                            Toast.makeText(getContext(), "Transaction successful", Toast.LENGTH_SHORT).show();
//
//
//                        } else {
//                            Toast.makeText(getContext(), "Transaction failed", Toast.LENGTH_SHORT).show();
//                        }
//
//                        Log.d(TAG, "onResponse: INFO " + response.body());
//                    }
//
//                    @Override
//                    public void onFailure(Call<String> call, Throwable t) {
//                        Log.d(TAG, "onFailure: INFO " + t.getMessage());
//                    }
//                })
//        ;
    }

    private void loadToken() {
        Log.d(TAG, "loadToken: called");

        final android.app.AlertDialog waitingDialog = new SpotsDialog.Builder()
                .setContext(getContext()).build();
        waitingDialog.show();
        waitingDialog.setMessage("Please wait...");

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Common.API_TOKEN_URL, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(TAG, "onFailure: called");
                waitingDialog.dismiss();
               // btn_place_order.setEnabled(false);
//                Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getContext(), "failed to generate", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, "onSuccess: called");
                waitingDialog.dismiss();

                token = responseString;
              //  btn_place_order.setEnabled(true);

            }
        });
    }

    private class getToken extends AsyncTask {
        ProgressDialog mDailog;

        @Override
        protected Object doInBackground(Object[] objects) {
            HttpClient client = new HttpClient();
            client.get(API_TOKEN_URL, new HttpResponseCallback() {
                @Override
                public void success(final String responseBody) {
                    mDailog.dismiss();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            group_payment.setVisibility(View.VISIBLE);
                            token = responseBody;
                        }
                    });
                }

                @Override
                public void failure(Exception exception) {
                    mDailog.dismiss();
                    Toast.makeText(getContext(), "failed to generate", Toast.LENGTH_SHORT).show();

                    Log.d("Err", exception.toString());
                }
            });
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDailog = new ProgressDialog(getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog);
            mDailog.setCancelable(false);
            mDailog.setMessage("Loading Wallet, Please Wait");
            mDailog.show();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == PermissionsActivity.PERMISSIONS_GRANTED) {
                Toast.makeText(mContext, "Permission Granted to Save", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "Permission not granted, Try again!", Toast.LENGTH_SHORT).show();
            }
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce nonce = result.getPaymentMethodNonce();
                String strNonce = nonce.getNonce();
                if(!total.getText().toString().isEmpty())
                {
                    amount = String.valueOf(total);
                    params = new HashMap<>();
                    params.put("amount", amount);
                    params.put("nonce", strNonce);

                    sendPayments();
                }
                else {
                    Toast.makeText(getContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                }


            }

            else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getContext(), "Payment cancelled", Toast.LENGTH_SHORT).show();
            }
            else {
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Log.e(TAG, "onActivityResult: "+error.getMessage());
            }
        }

    }

    public void createPdf(String dest) {

        if (new File(dest).exists()) {
            new File(dest).delete();
        }

        try {
            Document document =new Document();
            PdfWriter.getInstance(document ,new FileOutputStream(dest) );
            document.open();
            document.setPageSize(PageSize.A4);
            document.addCreationDate();
            document.addAuthor("kim");
            document.addCreator("kimani joseph");
            BaseColor  colorAccent =new BaseColor(0,123,150,120);
            float fontsize= 20.0f;
            float valuefontsize= 26.0f;
            BaseFont fontName =BaseFont.createFont("asset/fonts/BrandonMedium.otf", "UTf-8",BaseFont.EMBEDDED);
            Font tittlefont = new Font(fontName,36.0f, Font.NORMAL, BaseColor.BLACK);
            addNewItem(document,"Oder Details", Element.ALIGN_CENTER,tittlefont);

            Font orderNumberFont = new Font(fontName,valuefontsize,Font.NORMAL,colorAccent);
            addNewItem(document,"Oder Numder", Element.ALIGN_LEFT,orderNumberFont);


            Font orderNumberValueFont = new Font(fontName,fontsize,Font.NORMAL,BaseColor.BLACK);
            addNewItem(document,ordernumber, Element.ALIGN_LEFT,orderNumberValueFont);
            addLineSeparator(document);

            addNewItem(document,"Order Date", Element.ALIGN_LEFT,orderNumberFont);
            addNewItem(document, String.valueOf(Common.currentBooking.getTimestamp()), Element.ALIGN_LEFT,orderNumberValueFont);
            addLineSeparator(document);

            addNewItem(document,"Client Name", Element.ALIGN_LEFT,orderNumberFont);
            addNewItem(document,Common.currentUser.getName(), Element.ALIGN_LEFT,orderNumberValueFont);
            addLineSeparator(document);

            addLineSeparator(document);
            addNewItem(document,"product details", Element.ALIGN_CENTER,tittlefont);
            BookingInformation bookingInformation = new BookingInformation();

            addLineSeparator(document);
            addNewITemWithLeftAndRight(document,"Area",Common.currentHostelArea.getName(),tittlefont,orderNumberValueFont);
            addNewITemWithLeftAndRight(document,"Adress",Common.currentHostelArea.getAddress(),tittlefont,orderNumberValueFont);
            addLineSeparator(document);

            addLineSeparator(document);
            addNewITemWithLeftAndRight(document,"Hostel",Common.currentHostel.getName(),tittlefont,orderNumberValueFont);
            addNewITemWithLeftAndRight(document,"Room number",Common.currentBooking.getTime(),tittlefont,orderNumberValueFont);
            addLineSeparator(document);

            addLineSeparator(document);
            addNewITemWithLeftAndRight(document,"fghjk","657.00",tittlefont,orderNumberValueFont);
            addNewITemWithLeftAndRight(document,"3467","876ewe.00",tittlefont,orderNumberValueFont);
            addLineSeparator(document);

            addLineSeparator(document);
            addNewITemWithLeftAndRight(document,"fghjk","657.00",tittlefont,orderNumberValueFont);
            addNewITemWithLeftAndRight(document,"3467","876ewe.00",tittlefont,orderNumberValueFont);
            addLineSeparator(document);


            addLineSeparator(document);
            addNewITemWithLeftAndRight(document,"Total","4000000000",tittlefont,orderNumberValueFont);
            document.close();

            Toast.makeText(mContext, "Created... :)", Toast.LENGTH_SHORT).show();

            FileUtils.openFile(mContext, new File(dest));

        } catch (IOException | DocumentException ie) {
            LOGE("createPdf: Error " + ie.getLocalizedMessage());
        } catch (ActivityNotFoundException ae) {
            Toast.makeText(mContext, "No application found to open this file.", Toast.LENGTH_SHORT).show();
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == PermissionsActivity.PERMISSIONS_GRANTED) {
//            Toast.makeText(mContext, "Permission Granted to Save", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(mContext, "Permission not granted, Try again!", Toast.LENGTH_SHORT).show();
//        }
//    }


    private void addNewITemWithLeftAndRight(Document document, String textleft,
                                            String textright, Font textFontright,Font textFontleft) throws DocumentException {
        Chunk chunkTextLeft = new Chunk(textleft,textFontleft);
        Chunk chunkTextRight = new Chunk(textright,textFontright);
        Paragraph p = new Paragraph(chunkTextLeft);
        p.add(new Chunk(new VerticalPositionMark()));
        p.add(chunkTextRight);
        document.add(p);

    }

    private void addLineSeparator(Document document) throws DocumentException {
        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));
        addlinespace(document);
        document.add(new Chunk(lineSeparator));
        addlinespace(document);
    }

    private void addlinespace(Document document) throws DocumentException {
        document.add(new Paragraph(""));
    }

    private void addNewItem(Document document, String text, int align ,Font font)throws DocumentException {
        Chunk chunk = new Chunk(text,font);
        Paragraph paragraph = new Paragraph(chunk);
        paragraph.setAlignment(align);
        document.add(paragraph);


    }
    public void  generateCode(){
        Date mydate=new Date();
        SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault());
        String date= format.format(mydate);
        Random r = new Random();
        int n = 100000 + r.nextInt(900000);
        ordernumber =String.valueOf(n);
    }


}


