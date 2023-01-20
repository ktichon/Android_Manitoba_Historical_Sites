package com.example.winnipeghistoricalsites;

import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Fragment that gets and displays the directions to a historical site
 */
public class HistoricalSiteDirectionsFragment extends Fragment {
    private HistoricalSiteDetailsViewModel mViewModel;
    private HistoricalSite currentSite;
    private RequestQueue queue;
    View mainView;
    private Location currentLocation;



    public HistoricalSiteDirectionsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainView = view;
        queue = Volley.newRequestQueue(getContext());
        mViewModel = new ViewModelProvider(getActivity()).get(HistoricalSiteDetailsViewModel.class);
        currentSite = mViewModel.getCurrentSite().getValue();


        //Updates the user location when they move. Not necessary right now, but could be in the future
        currentLocation = mViewModel.getCurrentLocation().getValue();
        mViewModel.getCurrentLocation().observe(getViewLifecycleOwner(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                try{
                    currentLocation = location;
                } catch (Exception e)
                {
                    Log.e("Error", "Fragment Directions: Error updating user location\n" + e.getMessage());
                }


            }
        } );


        getDirectionsApi(currentSite);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_historical_site_directions, container, false);
    }



    //Gets direction information from the google directions api
    public void getDirectionsApi(HistoricalSite site)
    {

        if (currentLocation != null)
        {
            String origin = "origin=" + currentLocation.getLatitude()+","+ currentLocation.getLongitude();
            String destination = "destination=";
            if (site.placeId != null)
                destination += "place_id:" + site.placeId;
            else
                destination += site.location.getLatitude() + "," + site.location.getLongitude();
            String alternatives = "alternatives=false";

            String departureTime = "departure_time=now";
            String mode = "mode=driving";
            String units = "units=metric";
            String directionUrl = getString(R.string.directions_Api_Link) + origin + "&" + destination + "&" + alternatives + "&" + departureTime + "&" + mode + "&" + units + "&key=" + getString(R.string.google_maps_additions_key);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, directionUrl, null, new Response.Listener<JSONObject>() {


                        @Override
                        public void onResponse(JSONObject response) {



                            try {
                                String status = response.getString("status");
                                if (status.equals( "OK"))
                                {
                                    //JSONArray routesArray = response.getJSONArray("routes");
                                    JSONObject routesObject = response.getJSONArray("routes").getJSONObject(0);
                                    JSONObject overViewPolyLineJson = routesObject.getJSONObject("overview_polyline");

                                    JSONObject legObject = routesObject.getJSONArray("legs").getJSONObject(0);
                                    String duration = legObject.getJSONObject("duration_in_traffic").getString("text");
                                    String distance = legObject.getJSONObject("distance").getString("text");
                                    String startAddress = legObject.getString("start_address");
                                    String endAddress = legObject.getString("end_address");
                                    JSONArray allSteps = legObject.getJSONArray("steps");
                                    String summary = routesObject.getString("summary");



                                    //Display

                                    TextView tvHeader = mainView.findViewById(R.id.tvDistanceHeader);
                                    tvHeader.setText(duration + " (" + distance + ")");

                                    TextView tvSummary = mainView.findViewById(R.id.tvSummary);
                                    tvSummary.setText("via " + summary);

                                    TextView tvStartEnd = mainView.findViewById(R.id.tvStartEnd);
                                    tvStartEnd.setText(startAddress.substring(0, startAddress.indexOf(",")) + " to " + endAddress.substring(0, endAddress.indexOf(",")));





                                    Log.i("info", routesObject.toString());


                                    try{
                                        PolylineOptions directionLine = new PolylineOptions();


                                    }catch (Exception e)
                                    {
                                        Log.e("Error", "getDirectionsApi: Error parsing data\n" + e.getMessage());
                                    }
                                }
                                else {
                                    Log.e("Error", "getDirectionsApi: Result Status = " + status);
                                    Toast.makeText(mainView.getContext(), "There was an issue getting directions", Toast.LENGTH_SHORT).show();
                                }





                            }catch (Exception e) {
                                Log.e("Error", "getDirectionsApi: Error extracting Direction data\n" + e.getMessage());
                            }

                        }
                    }, getJsonError);
            queue.add(jsonObjectRequest);}
        else {
            Toast.makeText(mainView.getContext(), "Please make sure that you have enabled us to access your location.", Toast.LENGTH_LONG).show();
        }
    }
    //Error fetching api
    private Response.ErrorListener getJsonError = error -> {
        Toast.makeText(mainView.getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
        Log.e("Error", "Directions getJsonError: Error fetching directions json\n" + error.getMessage());
    };
}