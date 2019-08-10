package apilbslocation;

import java.util.HashMap;
import java.util.Map;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;


public class GPSLocation extends Service implements LocationListener{

	private final Context context;
	private boolean isGPSEnable = false;
	private boolean canGetLocation = false;
	
	protected Location location;
	protected LocationManager locationManager;
	private double latitude;
	private double longitude;
	
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; //meters
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * (1/2); // 0.5 minute
		
	public GPSLocation(Context context) {
		this.context = context;
		getLocation();
	}

	protected Location getLocation() {
		try{
			
			locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
			isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			if(isGPSEnable){
				this.canGetLocation = true;
				if(location == null){
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					if(locationManager != null){
						location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						if(location != null){
							latitude = location.getLatitude();
							longitude = location.getLongitude();
							Log.d("GPS_DEGUBG", "Latitude: "+latitude+ "Longitude: "+longitude);
						}
					}
				}
			} 
		}catch(Exception e){
			e.printStackTrace();
		}
		return location;
	}
	
	public double getLatitude(){
		if(location != null){
			latitude = location.getLatitude();
		}
		
		return latitude;
	}
	
	public double getLongitude(){
		if(location != null){
			longitude = location.getLongitude();
		}
		return longitude;
	}
	
	public float getAccuracy(){
		float accuracy = 0;
		try{
			accuracy = location.getAccuracy();
		}catch(Exception e){
			accuracy = 0;
		}
		return accuracy;
	}
	
	public void stopUsingGPS(){
		if(locationManager != null){
			locationManager.removeUpdates(GPSLocation.this);
		}
	}
    
	public boolean canGetLocation() {
        return this.canGetLocation;
    }

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.v("GPS", "Provider disabled " + provider);
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.v("GPS", "Provider enabled " + provider);
		
	}
	
    private static final Map<Integer, String> providerStatusMap = new HashMap<Integer, String>() {
        {
            put(LocationProvider.AVAILABLE, "Available");
            put(LocationProvider.OUT_OF_SERVICE, "Out of Service");
            put(LocationProvider.TEMPORARILY_UNAVAILABLE,
                    "Temporarily Unavailable");
            put(-1, "Not Reported");
        }
    };

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
        int satellites = extras.getInt("satellites", -1);

        String statusInfo = String.format("Provider: %s, status: %s, satellites: %d", provider, providerStatusMap.get(status), satellites);
        Log.v("GPS", statusInfo);
    }
	

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
