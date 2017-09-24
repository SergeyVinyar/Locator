package ru.vinyarsky.locator.net;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public final class NetRepository {

    private final SputnikApi api = new Retrofit.Builder()
            .baseUrl("http://search.maps.sputnik.ru/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(SputnikApi.class);

    /**
     * @param limit Max number of points
     */
    public Observable<ArrayList<NetAddress>> getAddressList(String placeName, int limit) {
        return api.searchFor(placeName, limit)
                .map(data -> {
                    ArrayList<NetAddress> result = new ArrayList<NetAddress>();
                    if (data != null && data.getResult() != null && data.getResult().getAddress() != null) {
                        List<Address> addresses = data.getResult().getAddress();
                        for (int i = 0; i < addresses.size(); i++) {
                            List<Feature> features = addresses.get(i).getFeatures();
                            if (features != null) {
                                for (int j = 0; j < features.size(); j++) {
                                    Feature feature = features.get(j);
                                    Properties properties = feature.getProperties();
                                    Geometry geometry = feature.getGeometry();
                                    if (properties != null && geometry != null && geometry.getGeometries().get(0) != null) {
                                        Geometry_ geometry_ = geometry.getGeometries().get(0);
                                        if (geometry_ != null && geometry_.getCoordinates() != null && geometry_.getCoordinates().size() == 2) {
                                            // OMG! We got it.
                                            result.add(new NetAddress(properties.getDisplayName(), geometry_.getCoordinates().get(1), geometry_.getCoordinates().get(0)));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    return result;
                });
    }

    interface SputnikApi {

        @GET("/search/addr")
        Observable<SputnikContract> searchFor(@Query("q") String placeName, @Query("addr_limit") int limit);
    }
}
