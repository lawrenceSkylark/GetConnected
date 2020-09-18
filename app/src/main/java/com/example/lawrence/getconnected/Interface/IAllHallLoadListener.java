package com.example.lawrence.getconnected.Interface;

import java.util.List;

public interface IAllHallLoadListener {
    void OnAllHallLoadSuccess(List<String> AreaNameList);
    void OnAllHallLoadFailed(String message);
}
