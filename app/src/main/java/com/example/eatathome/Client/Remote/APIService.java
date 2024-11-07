package com.example.eatathome.Client.Remote;

import com.example.eatathome.Client.Model.MyResponse;
import com.example.eatathome.Client.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAATN8V4k:APA91bHEHH0FtYisQSL8nPwa3FZTaTk0Tf56TKLfRnJeFLL0tVOTjdiqXmjX07HKLYzEKmoi_9XdvddFDDBzEMiBe7_p8hyLGQCucVP4tfRmaUKsZmNlSsyFJI1K-jzP2wBIPwME4tOk"
            }

    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
