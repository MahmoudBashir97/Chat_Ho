package com.chatho.chatho.Api_Interface;

import com.chatho.chatho.pojo.send;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface api_Interface {

    //لما تبعت الداتا مع بعض زي مثلا عبارة عن package
    @Headers({"Authorization: key=AAAAWgnVk88:APA91bFtPXAiE9tx8V_SmqGvxj36-sLpniex0SpoacQvejdBDVRSLFvK_NOH2bKV-H9pB6H3QZkzCbylCX-B-CWgxTj5dWRst6uhB8Fi7GZI1xXFAtfs_RyMfOY-1zHmHDnRlQ0vBAzP",
            "Content-Type:application/json"})
    @POST("fcm/send")
    public Call<send> storedata(@Body send send);

    //لما تبعت الداتا مع بعض زي مثلا عبارة عن package
    @Headers({"Authorization: key=AAAAWgnVk88:APA91bFtPXAiE9tx8V_SmqGvxj36-sLpniex0SpoacQvejdBDVRSLFvK_NOH2bKV-H9pB6H3QZkzCbylCX-B-CWgxTj5dWRst6uhB8Fi7GZI1xXFAtfs_RyMfOY-1zHmHDnRlQ0vBAzP",
            "Content-Type:application/json"})
    @POST("fcm/send")
    public Call<com.chatho.chatho.Model.send> chatRequest(@Body com.chatho.chatho.Model.send send);
}
