package com.example.homely.ui.admin.viewmodel;

import com.example.homely.data.model.*;
import com.example.homely.data.repository.*;

import java.util.*;

/**
 * ViewModel cho admin.
 */

public class AdminViewModel {
    private final AdminRepository repository;

    public AdminViewModel(AdminRepository repository) {
        this.repository = repository;
    }

    public void getReports() {
        // TODO: call repository.getReports
    }

    public LiveData<Resource<List<Report>>> getReportsList() {
        return null;
    }

    public void resolveReport(String reportId) {
        // TODO: call repository.resolveReport
    }

    public void approveRoom(String roomId) {
        // TODO: call repository.approveRoom
    }
}
