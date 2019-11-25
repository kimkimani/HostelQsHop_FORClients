package ydkim2110.com.androidbarberbooking.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import ydkim2110.com.androidbarberbooking.Interface.IRecyclerItemSelectedListener;
import ydkim2110.com.androidbarberbooking.Model.Hostel;
import ydkim2110.com.androidbarberbooking.Model.EventBus.EnableNextButton;
import ydkim2110.com.androidbarberbooking.R;

public class MyHostelAdapter extends RecyclerView.Adapter<MyHostelAdapter.MyViewHolder> {

    private static final String TAG = MyHostelAdapter.class.getSimpleName();

    private Context mContext;
    private List<Hostel> mHostelList;
    private List<CardView> mCardViewList;
    //private LocalBroadcastManager mLocalBroadcastManager;

    public MyHostelAdapter(Context context, List<Hostel> hostelList) {
        mContext = context;
        mHostelList = hostelList;
        mCardViewList = new ArrayList<>();
        //mLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.layout_hostel, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txt_barber_name.setText(mHostelList.get(position).getName());
        if (mHostelList.get(position).getRatingTimes() != null) {
            holder.ratingBar.setRating(mHostelList.get(position).getRating().floatValue() /
                    mHostelList.get(position).getRatingTimes());
        }
        else {
            holder.ratingBar.setRating(0);
        }

        if (!mCardViewList.contains(holder.card_barber)) {
            mCardViewList.add(holder.card_barber);
        }

        holder.setIRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelected(View view, int position) {
                // Set Background for all item not choice
                for (CardView cardView : mCardViewList) {
                    cardView.setCardBackgroundColor(mContext.getResources()
                        .getColor(android.R.color.white));
                }

                // Set Background for choice
                holder.card_barber.setCardBackgroundColor(mContext.getResources()
                        .getColor(android.R.color.holo_orange_dark));

                // Send local broadcast to enable button next
//                Intent intent = new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
//                intent.putExtra(Common.KEY_BARBER_SELECTED, mHostelList.get(position));
//                intent.putExtra(Common.KEY_STEP, 2);
//                mLocalBroadcastManager.sendBroadcast(intent);

                // EventBus
                EventBus.getDefault().postSticky(new EnableNextButton(2, mHostelList.get(position)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mHostelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView txt_barber_name;
        private RatingBar ratingBar;
        private CardView card_barber;

        IRecyclerItemSelectedListener mIRecyclerItemSelectedListener;

        public void setIRecyclerItemSelectedListener(IRecyclerItemSelectedListener IRecyclerItemSelectedListener) {
            mIRecyclerItemSelectedListener = IRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            card_barber = itemView.findViewById(R.id.card_barber);
            txt_barber_name = itemView.findViewById(R.id.hostel_name);
            ratingBar = itemView.findViewById(R.id.rtb_hostel);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mIRecyclerItemSelectedListener.onItemSelected(v, getAdapterPosition());
        }
    }
}
