package ydkim2110.com.androidbarberbooking.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import ydkim2110.com.androidbarberbooking.Adapter.MyhostelAreaAdapter;
import ydkim2110.com.androidbarberbooking.Common.Common;
import ydkim2110.com.androidbarberbooking.Common.SpaceItemDecoration;
import ydkim2110.com.androidbarberbooking.Interface.IAllHostelAreaLoadListener;
import ydkim2110.com.androidbarberbooking.Interface.IBranchLoadListener;
import ydkim2110.com.androidbarberbooking.Model.EventBus.UnableNextButton;
import ydkim2110.com.androidbarberbooking.Model.hostelArea;
import ydkim2110.com.androidbarberbooking.R;

public class BookingStep1Fragment extends Fragment implements IAllHostelAreaLoadListener, IBranchLoadListener {

    private static final String TAG = BookingStep1Fragment.class.getSimpleName();

    //private LocalBroadcastManager mLocalBroadcastManager;
    // Variable
    private CollectionReference allHostelAreaRef;
    private CollectionReference branchRef;

    private IAllHostelAreaLoadListener iAllHostelAreaLoadListener;
    private IBranchLoadListener iBranchLoadListener;

    @BindView(R.id.spinner)
    MaterialSpinner spinner;
    @BindView(R.id.recycler_HostelArea)
    RecyclerView recycler_HostelArea;
    @BindView(R.id.no_item)
    TextView no_item;

    private Unbinder unBinder;

    private AlertDialog dialog;

    private static BookingStep1Fragment instance;

    public static BookingStep1Fragment getInstance() {
        if (instance == null) {
            instance = new BookingStep1Fragment();
        }
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
///gender/gents/Branch/4jydSfTfDi3o26owKCFp/Hostel
        allHostelAreaRef = FirebaseFirestore.getInstance().collection("gender");

        iAllHostelAreaLoadListener = this;
        iBranchLoadListener = this;

        dialog = new SpotsDialog.Builder().setContext(getActivity()).build();

        //mLocalBroadcastManager = LocalBroadcastManager.getInstance(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_booking_step_one, container,false);
        unBinder = ButterKnife.bind(this, view);

        initView();
        loadAllSalon();

        return view;
    }

    private void initView() {
        Log.d(TAG, "initView: called!!");
        recycler_HostelArea.setHasFixedSize(true);
        recycler_HostelArea.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recycler_HostelArea.addItemDecoration(new SpaceItemDecoration(4));
    }

    private void loadAllSalon() {
        Log.d(TAG, "loadAllSalon: called!!");
        allHostelAreaRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> list = new ArrayList<>();
                            list.add("choose gender.");
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                list.add(documentSnapshot.getId());
                            }
                            iAllHostelAreaLoadListener.onAllHostelAreaLoadSuccess(list);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        iAllHostelAreaLoadListener.onAllHostelAreaLoadFailed(e.getMessage());
                    }
                });
    }

    @Override
    public void onAllHostelAreaLoadSuccess(List<String> areaNameList) {
        Log.d(TAG, "onAllHostelAreaLoadSuccess: called!!");
        spinner.setItems(areaNameList);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                Log.d(TAG, "onItemSelected: position: "+position);
                if (position > 0) {
                    makeNextBtnEnableFalse();
                    loadBranchOfCity(item.toString());
                } else {
                    makeNextBtnEnableFalse();
                    recycler_HostelArea.setVisibility(View.GONE);
                }
            }
        });
    }

    private void makeNextBtnEnableFalse() {
        Log.d(TAG, "makeNextBtnEnableFalse: called!!");
        //Intent intent = new Intent(Common.KEY_UNABLE_BUTTON_NEXT);
        //mLocalBroadcastManager.sendBroadcast(intent);

        EventBus.getDefault().postSticky(new UnableNextButton(true));
    }

    @Override
    public void onAllHostelAreaLoadFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void loadBranchOfCity(String genderName) {
        Log.d(TAG, "loadBranchOfCity: called!! passed item is "+genderName);
        dialog.show();

        Common.gender = genderName;

        branchRef = FirebaseFirestore.getInstance()
                .collection("gender")
                .document(genderName)
                .collection("Branch");

        branchRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<hostelArea> list = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                hostelArea hostelArea = documentSnapshot.toObject(hostelArea.class);
                                // add salonId
                                hostelArea.setHoatelAreaId(documentSnapshot.getId());
                                list.add(hostelArea);
                            }
                            iBranchLoadListener.onAllBranchLoadSuccess(list);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        iBranchLoadListener.onAllBranchLoadFailed(e.getMessage());
                    }
                });
    }

    @Override
    public void onAllBranchLoadSuccess(List<hostelArea> hostelAreaList) {
        Log.d(TAG, "onAllBranchLoadSuccess: called!!");
        if (hostelAreaList.size() == 0) {
            no_item.setVisibility(View.VISIBLE);
            recycler_HostelArea.setVisibility(View.GONE);
        } else {
            MyhostelAreaAdapter adapter = new MyhostelAreaAdapter(getActivity(), hostelAreaList);
            recycler_HostelArea.setAdapter(adapter);
            no_item.setVisibility(View.GONE);
            recycler_HostelArea.setVisibility(View.VISIBLE);
        }

        dialog.dismiss();
    }

    @Override
    public void onAllBranchLoadFailed(String message) {
        Log.d(TAG, "onAllBranchLoadFailed: called!!");
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }
}
