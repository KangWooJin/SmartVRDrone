package com.example.kang.smartVRDrone;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class CurrentGPSActivity extends Activity implements MapView.OpenAPIKeyAuthenticationResultListener, MapView.MapViewEventListener {

    private static final String LOG_TAG = "MapViewDemoActivity";

    private MapView mMapView;
    private MapPOIItem mDefaultMarker;
    ImageView currGeo ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_gps);

        mMapView = new MapView(this);

        /*
        MapLayout mapLayout = new MapLayout(this);
        mMapView = mapLayout.getMapView();
        */

        mMapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY);
        mMapView.setOpenAPIKeyAuthenticationResultListener(this);
        mMapView.setMapViewEventListener(this);

        mMapView.setMapType(MapView.MapType.Standard);



        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mMapView);


        currGeo = (ImageView) findViewById(R.id.currGeo);



        currGeo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMapViewInitialized(mMapView);
            }
        });

    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

        Log.i(LOG_TAG, "MapView had loaded. Now, MapView APIs could be called safely");
        //mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(36.1456546, 128.39238319999998), 2, true);
        createDefaultMarker(mapView,MapPoint.mapPointWithGeoCoord(36.1456546, 128.39238319999998));

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        Log.i(LOG_TAG, String.format("MapView onMapViewMoveFinished (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude));
    }

    @Override
    public void onDaumMapOpenAPIKeyAuthenticationResult(MapView mapView, int i, String s) {

    }

    private void createDefaultMarker(MapView mapView, MapPoint mapPoint) {
        mDefaultMarker = new MapPOIItem();

        String name = "CurrentPoint";
        mDefaultMarker.setItemName(name);
        mDefaultMarker.setTag(0);

        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        MapPoint current_maker_point = MapPoint.mapPointWithGeoCoord(mapPointGeo.latitude, mapPointGeo.longitude);


        mDefaultMarker.setMapPoint(current_maker_point);
        mDefaultMarker.setMarkerType(MapPOIItem.MarkerType.YellowPin);


        mapView.addPOIItem(mDefaultMarker);
        //pointName.add(name+pointName.size());
        // marker.add(mDefaultMarker);



        mapView.selectPOIItem(mDefaultMarker, true);
        mapView.setMapCenterPoint(current_maker_point, false);


    }
}
