package tim.bts.inforazia;

import retrofit2.Call;
import retrofit2.http.Body;

import retrofit2.http.Headers;
import retrofit2.http.POST;
import tim.bts.inforazia.notify.Sender;
import tim.bts.inforazia.notify.Response;

public interface APIService  {


    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAB2ahygs:APA91bH4meFvz7osoyC8hQIZoroKCSZ7jRPJAXZoi-eNgRVZUnDULUlNECbKGtejZMmZ-5-PXwmYbsFkGGLWCjkCnc7olJdOfa7HpQmZ5r3HS--HB0h4GB67-5SpPcEvHQnYY_doEezY"

            }
    )

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
