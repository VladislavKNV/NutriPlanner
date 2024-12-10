package com.example.nutriPlanner.Helpers;

import com.example.nutriPlanner.Model.ApiModels.AddFavoriteFoodRequest;
import com.example.nutriPlanner.Model.ApiModels.AddFavoriteFoodResponse;
import com.example.nutriPlanner.Model.ApiModels.AddFoodIntakeRequest;
import com.example.nutriPlanner.Model.ApiModels.AddFoodIntakeResponse;
import com.example.nutriPlanner.Model.ApiModels.AddOrUpdateBMIRequest;
import com.example.nutriPlanner.Model.ApiModels.AddOrUpdateBMIResponse;
import com.example.nutriPlanner.Model.ApiModels.CheckEmailRequest;
import com.example.nutriPlanner.Model.ApiModels.CheckEmailResponse;
import com.example.nutriPlanner.Model.ApiModels.ForgotPasswordRequest;
import com.example.nutriPlanner.Model.ApiModels.LoginRequest;
import com.example.nutriPlanner.Model.ApiModels.LoginResponse;
import com.example.nutriPlanner.Model.ApiModels.RegisterRequest;
import com.example.nutriPlanner.Model.ApiModels.RegisterResponse;
import com.example.nutriPlanner.Model.ApiModels.UpdateLastLoginRequest;
import com.example.nutriPlanner.Model.ApiModels.UpdatePasswordRequest;
import com.example.nutriPlanner.Model.ApiModels.UpdateUserInfoRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    @POST("register")
    Call<RegisterResponse> register(@Body RegisterRequest request);

    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @PUT("updateLastLogin")
    Call<Void> updateLastLogin(@Body UpdateLastLoginRequest updateLastLoginRequest);

    @PUT("updateUserInfo")
    Call<Void> updateUserInfo(@Body UpdateUserInfoRequest updateUserInfoRequest);

    @PUT("updatePassword")
    Call<Void> updatePassword(@Body UpdatePasswordRequest request);

    @POST("addFavoriteFood")
    Call<AddFavoriteFoodResponse> addFavoriteFood(@Body AddFavoriteFoodRequest request);

    @DELETE("deleteFavoriteFood/{id}")
    Call<Void> deleteFavoriteFood(@Path("id") int favoriteFoodId);
    @POST("addFoodIntake")
    Call<AddFoodIntakeResponse> addFoodIntake(@Body AddFoodIntakeRequest request);

    @DELETE("deleteFoodIntake/{id}")
    Call<Void> deleteFoodIntake(@Path("id") int id);

    @POST("addOrUpdateBMI")
    Call<AddOrUpdateBMIResponse> addOrUpdateBMI(@Body AddOrUpdateBMIRequest request);

    @POST("forgotPassword")
    Call<Void> forgotPassword(@Body ForgotPasswordRequest forgotPasswordRequest);

    @POST("checkEmail")
    Call<CheckEmailResponse> checkEmail(@Body CheckEmailRequest request);
}
