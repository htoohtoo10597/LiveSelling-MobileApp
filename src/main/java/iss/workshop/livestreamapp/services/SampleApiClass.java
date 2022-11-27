package iss.workshop.livestreamapp.services;

import java.util.List;

import iss.workshop.livestreamapp.models.Stream;
import retrofit2.Call;
import retrofit2.http.*;

public interface SampleApiClass {
    //this class will serve as an example, might need to create more helper classes for api fetches

}

/*
=========== FOR POST ============
TextView item = findViewById
TextView item2 = findViewById

RetrofitService rfServ = new RetrofitService
SampleApiClass sac = rfServ.getRetrofit().create(SampleApiClass.class);

setOnClickListener(
    String name = String.valueOf(item.getText());
    .
    .
    .

    Entity entity = new Entity();
    -- call the setters

    sac.call_function(entity)
        .enqueue(Callback<Entity>
            onResponse: on success
            onFailure: on failure
        )

)

===========================

========== FOR GET ===========

RetrofitService rfServ = new RetrofitService
SampleApiClass sac = rfServ.getRetrofit().create(SampleApiClass.class);

sac.getAll().enqueue(CallBack<List<Entity>>(){
    onResponse() populateList(response.body())
    onFailure: toast maybe?
})

populateList(List<Entity> body){
       //create ListAdapter passing the listinside the adapter
       //.setAdapter on List (classic code)
}

===============================

 */