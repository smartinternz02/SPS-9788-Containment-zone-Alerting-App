package com.saravana.containmentzonealertapp;


import com.saravana.containmentzonealertapp.models.LocationResponse;

import io.reactivex.rxjava3.core.Observable;

interface ILocationService {

     Observable<LocationResponse> observeLocation();
}
