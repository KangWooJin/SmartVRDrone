package com.example.kang.smartVRDrone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;


import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;

public class PointSettingActivity extends Activity implements MapView.OpenAPIKeyAuthenticationResultListener, MapView.MapViewEventListener, MapView.POIItemEventListener {

    private static final String LOG_TAG = "MapViewDemoActivity";

    private MapView mMapView;
    private MapPOIItem mDefaultMarker;

    private MapPOIItem removeMarker = null;
    ArrayList<MapPOIItem> marker = new ArrayList<MapPOIItem>();


    ArrayList<String> pointName = new ArrayList<String>();

    String currMapPOIItem = "";

    ImageView checkBtn ;
    ImageView xBtn ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_setting);

        mMapView = new MapView(this);

        /*
        MapLayout mapLayout = new MapLayout(this);
        mMapView = mapLayout.getMapView();
        */

        mMapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY);
        mMapView.setOpenAPIKeyAuthenticationResultListener(this);
        mMapView.setMapViewEventListener(this);
        mMapView.setPOIItemEventListener(this);
        mMapView.setMapType(MapView.MapType.Standard);



        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mMapView);


        checkBtn = (ImageView) findViewById(R.id.checkBtn);
        xBtn = (ImageView) findViewById(R.id.xBtn);


        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PointSettingActivity.this);

                builder.setTitle("자율주행 정보전송")        // 제목 설정
                        .setMessage("좌표를 전송하시겠습니까?")        // 메세지 설정
                        .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            // 확인 버튼 클릭시 설정
                            public void onClick(DialogInterface dialog, int whichButton) {
                                finish();
                            }
                        })
                        .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            // 취소 버튼 클릭시 설정
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = builder.create();    // 알림창 객체 생성
                dialog.show();    // 알림창 띄우기
            }
        });

        xBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDefaultMarker(mMapView);
            }
        });

    }

    @Override
    public void onMapViewInitialized(MapView mapView) {
        Log.i(LOG_TAG, "MapView had loaded. Now, MapView APIs could be called safely");
        //mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(36.1456546, 128.39238319999998), 2, true);
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapCenterPoint) {
       // MapPoint.GeoCoordinate mapPointGeo = mapCenterPoint.getMapPointGeoCoord();
        //Log.i(LOG_TAG, String.format("MapView onMapViewCenterPointMoved (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude));
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int zoomLevel) {
        Log.i(LOG_TAG, String.format("MapView onMapViewZoomLevelChanged (%d)", zoomLevel));
    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

        //MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
       // Log.i(LOG_TAG, String.format("MapView onMapViewSingleTapped (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude));
    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

        //MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();

        /*
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("DaumMapLibrarySample");
        alertDialog.setMessage(String.format("Double-Tap on (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude));
        alertDialog.setPositiveButton("OK", null);
        alertDialog.show();
        */


    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

        //MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();

        /*
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("DaumMapLibrarySample");
        alertDialog.setMessage(String.format("Long-Press on (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude));
        alertDialog.setPositiveButton("OK", null);
        alertDialog.show();
        */

        createDefaultMarker(mapView, mapPoint);


    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

        //MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        //Log.i(LOG_TAG, String.format("MapView onMapViewDragStarted (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude));

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

        //MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        //Log.i(LOG_TAG, String.format("MapView onMapViewDragEnded (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude));

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        Log.i(LOG_TAG, String.format("MapView onMapViewMoveFinished (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude));

    }

    @Override
    public void onDaumMapOpenAPIKeyAuthenticationResult(MapView mapView, int resultCode, String resultMessage) {

        Log.i(LOG_TAG, String.format("Open API Key Authentication Result : code=%d, message=%s", resultCode, resultMessage));
    }

    /* 마커부분*/
    private void createDefaultMarker(MapView mapView, MapPoint mapPoint) {
        mDefaultMarker = new MapPOIItem();

        String name = "Point" + (mapView.getPOIItems().length+1);
        mDefaultMarker.setItemName(name);
        mDefaultMarker.setTag(0);

        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        MapPoint current_maker_point = MapPoint.mapPointWithGeoCoord(mapPointGeo.latitude, mapPointGeo.longitude);


        mDefaultMarker.setMapPoint(current_maker_point);
        mDefaultMarker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        mDefaultMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);

        mapView.addPOIItem(mDefaultMarker);
        //pointName.add(name+pointName.size());
       // marker.add(mDefaultMarker);



        mapView.selectPOIItem(mDefaultMarker, true);
        mapView.setMapCenterPoint(current_maker_point, false);

        addPolyline1();
    }

    private void deleteDefaultMarker(MapView mapView) {

        if ( removeMarker == null )
            return ;


        mapView.removePOIItem(removeMarker);
        removeMarker = null ;
        addPolyline1();
    }

    /* 마크 관리*/
    public void onPOIItemSelected(MapView mapView, MapPOIItem poiItem)
    {
        removeMarker = poiItem;
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }


    /*마크 선 관리*/

    private void addPolyline1() {
        MapPolyline existingPolyline = mMapView.findPolylineByTag(1000);
        if (existingPolyline != null) {
            mMapView.removePolyline(existingPolyline);
        }

        MapPolyline polyline1 = new MapPolyline();
        polyline1.setTag(1000);
        polyline1.setLineColor(Color.argb(128, 255, 51, 0));
        int len = mMapView.getPOIItems().length;
        MapPOIItem[] mp = mMapView.getPOIItems();
        String name = "Point";
        for ( int i = 0 ; i < mMapView.getPOIItems().length ; ++i )
        {
            mp[i].setItemName(name+(i+1));
            polyline1.addPoint(mp[i].getMapPoint());
        }
        mMapView.addPolyline(polyline1);


        MapPointBounds mapPointBounds = new MapPointBounds(polyline1.getMapPoints());
        int padding = 100; // px
        mMapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding));
    }

}
