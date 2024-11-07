package com.example.eatathome.Server.Remote;

import com.example.eatathome.Server.Models.MyResponseRes;
import com.example.eatathome.Server.Models.SenderRes;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIServiceRes {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAATN8V4k:APA91bHEHH0FtYisQSL8nPwa3FZTaTk0Tf56TKLfRnJeFLL0tVOTjdiqXmjX07HKLYzEKmoi_9XdvddFDDBzEMiBe7_p8hyLGQCucVP4tfRmaUKsZmNlSsyFJI1K-jzP2wBIPwME4tOk"
            }

    )

    @POST("fcm/send")
    Call<MyResponseRes> sendNotification(@Body SenderRes body);
}
