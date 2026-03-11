package nimblix.in.HealthCareHub.service;

import nimblix.in.HealthCareHub.response.*;

import java.util.List;

public interface DashboardService {
    ApiResponse<DashboardSummaryResponse> getDashboardSummary();
    ApiResponse<List<AdmissionDischargeActivityResponse>> getAdmissionsDischargesActivity();
    ApiResponse<List<SpecializationDistributionResponse>> getSpecializationsDistribution();
    ApiResponse<List<SurgeryEmergencyActivityResponse>> getSurgeriesEmergenciesActivity();
    ApiResponse<List<HospitalOverviewResponse>> getHospitalOverview();

}
