package com.example.lawrence.getconnected.Interface;

import com.example.lawrence.getconnected.models.Hall;

import java.util.List;

public interface IBranchLoadListener {
    void OnBranchLoadSuccess(List<Hall> HallList);
    void OnBranchLoadFailed(String message);
}
