package com.lumachrome.bms.bms3.maps;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yanagi Hisato on 9/1/2015.
 */
public class updateAndRequestajsonPosition {

    private static ArrayList<String> idJson = new ArrayList<String>();
    private static ArrayList<Float> latitudeJson = new ArrayList<Float>();
    private static ArrayList<Float> longitudeJson = new ArrayList<Float>();

    private int index=0;

    public int getSize(){

        return idJson.size();
    }

    public String getIDPrajurit(int i){

        return idJson.get(i);
    }

    public float getLatitude(int i){

        return latitudeJson.get(i);
    }

    public float getLongitude(int i){
        return longitudeJson.get(i);
    }

    public void updateAndRequest(String url, final String idPrajurit, final double latitude, final double longitude){

        RequestQueue requesQueue = AppController.getInstance().getRequestQueue();
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json=new JSONObject(response);
                            JSONArray array = json.getJSONArray("BMS");

                            for(int i=0;i<array.length();i++) {
                                JSONObject c = array.getJSONObject(i);
                                String idpajurit = c.getString("id");
                                String latitudejson = c.getString("lat");
                                String longitudejson = c.getString("long");

                                idJson.add(idpajurit);
                                latitudeJson.add(Float.valueOf(latitudejson));
                                longitudeJson.add(Float.valueOf(longitudejson));
                                index++;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_prajurit", idPrajurit);
                params.put("latitude", String.valueOf(latitude));
                params.put("longitude", String.valueOf(longitude));
                return params;
            }
        };
        requesQueue.add(postRequest);
    }
}
