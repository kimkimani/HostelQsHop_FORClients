package ydkim2110.com.androidbarberbooking.Interface;

import java.util.List;

public interface IAllHostelAreaLoadListener {
    void onAllHostelAreaLoadSuccess(List<String> areaNameList);
    void onAllHostelAreaLoadFailed(String message);
}
