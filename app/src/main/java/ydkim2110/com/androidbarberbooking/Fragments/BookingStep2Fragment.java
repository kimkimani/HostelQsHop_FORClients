package ydkim2110.com.androidbarberbooking.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ydkim2110.com.androidbarberbooking.Adapter.MyHostelAdapter;
import ydkim2110.com.androidbarberbooking.Common.SpaceItemDecoration;
import ydkim2110.com.androidbarberbooking.Model.EventBus.BarberDoneEvent;
import ydkim2110.com.androidbarberbooking.R;

public class BookingStep2Fragment extends Fragment {

    private static final String TAG = BookingStep2Fragment.class.getSimpleName();

    private static BookingStep2Fragment instance;

    public static BookingStep2Fragment getInstance() {
        if (instance == null) {
            instance = new BookingStep2Fragment();
        }
        return instance;
    }
    ///gender/gents/Branch/4jydSfTfDi3o26owKCFp/Hostel
    private Unbinder mUnbinder;
    // private LocalBroadcastManager mLocalBroadcastManager;

    @BindView(R.id.recycler_Hostel)
    RecyclerView recycler_Hostel;
    @BindView(R.id.no_item)
    TextView no_item;

    /**
     * modify to Event BUs
     * @since : 2019-06-29 오전 8:50
    **/
    // we need listen broadcast(from bookactivity) and set recycler view
//    private BroadcastReceiver barberDoneReceiver = new BroadcastReceiver() {
////        @Override
////        public void onReceive(Context context, Intent intent) {
////            Log.d(TAG, "onReceive: Step2 BroadcastReceiver called!!");
////            ArrayList<Hostel> barberArrayList = intent.getParcelableArrayListExtra(Common.KEY_BARBER_LOAD_DONE);
////            // Create adapter
////            if (barberArrayList.size() == 0) {
////                no_item.setVisibility(View.VISIBLE);
////                recycler_Hostel.setVisibility(View.GONE);
////            } else {
////                no_item.setVisibility(View.GONE);
////                recycler_Hostel.setVisibility(View.VISIBLE);
////                MyHostelAdapter adapter = new MyHostelAdapter(getContext(), barberArrayList);
////                recycler_Hostel.setAdapter(adapter);
////            }
////        }
////    };

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
    public void setBarberAdapter(BarberDoneEvent event) {
        MyHostelAdapter adapter = new MyHostelAdapter(getContext(), event.getBarberList());
        recycler_Hostel.setAdapter(adapter);
    }
    //=============================================================================

    /**
     * remove localBroadcast 
     * @since : 2019-06-29 오전 8:51
    **/
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mLocalBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        //mLocalBroadcastManager.registerReceiver(barberDoneReceiver, new IntentFilter(Common.KEY_BARBER_LOAD_DONE));


    }

    @Override
    public void onDestroy() {
        //mLocalBroadcastManager.unregisterReceiver(barberDoneReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view =  inflater.inflate(R.layout.fragment_booking_step_two, container,false);

        mUnbinder = ButterKnife.bind(this, view);

        initView();

        return view;
    }

    private void initView() {
        recycler_Hostel.setHasFixedSize(true);
        recycler_Hostel.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recycler_Hostel.addItemDecoration(new SpaceItemDecoration(4));
    }

}
