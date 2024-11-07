package com.example.eatathome.Server.Service;

import com.example.eatathome.Client.Constant.Constant;
import com.example.eatathome.Server.Constant.ConstantRes;
import com.example.eatathome.Server.Models.TokenRes;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseIdServiceRes extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String tokenRefreshed = FirebaseInstanceId.getInstance().getToken();
        if (ConstantRes.currentUser != null)
        updateTokenToFirebase(tokenRefreshed);
    }

    private void updateTokenToFirebase(String tokenRefreshed) {
        if (ConstantRes.currentUser!=null){
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference tokens = db.getReference("Tokens");
            TokenRes token = new TokenRes(tokenRefreshed, true);
            // false because token send from client app

            tokens.child(ConstantRes.currentUser.getPhone()).setValue(token);
        }

    }
}
