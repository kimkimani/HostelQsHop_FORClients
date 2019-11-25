package ydkim2110.com.androidbarberbooking.Interface;

import java.util.List;

import ydkim2110.com.androidbarberbooking.Model.hostelArea;

public interface IBranchLoadListener {
    void onAllBranchLoadSuccess(List<hostelArea> areaNameList);
    void onAllBranchLoadFailed(String message);
}
