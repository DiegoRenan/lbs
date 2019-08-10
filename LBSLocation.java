package apilbslocation;

import android.content.Context;
import android.util.Log;


public class LBSLocation{

	private final Context context;
	protected double gpslatitude;
	protected double gpslongitude;
	protected double netlatitude;
	protected double netlongitude;
	private DeviceInfo device;
	private double latitude;
	private double longitude;
	private float gpsAccuracy = 0;
	private String provider = null;
	protected float sensitive = 10;

	private BDconnection bd;
	private GPSLocation gps;
	private NetworkLocation net;
	
	public LBSLocation(BDconnection database, Context context){
		this.bd = database;
		this.context = context;
		this.bd = database;
		
	}
	
	public void startLocation(){
		location();
	}
	
    public void stopLocation(){
		this.gps.stopUsingGPS();
		this.net.stopUsingNetwork();
		net.onDestroy();
		gps.onDestroy();
	}
	
	private void location(){
		GPSService();
		NetworkService();
		
		if(gpsAccuracy < sensitive && gpsAccuracy != 0){
			this.latitude = this.gpslatitude;
			this.longitude = this.gpslongitude;
			this.provider = "gps";
		}else{
			if(net.statusNet()){
				this.latitude = this.netlatitude;
				this.longitude = this.netlongitude;
				this.provider = "network";
				sensitive = 10;
			}else
				sensitive = 100;
		}
	}
	
    private void GPSService(){
    	gps = new GPSLocation(context);
    	if(gps.canGetLocation()){
    		this.gpsAccuracy = gps.getAccuracy();
    		Log.d("GPS_DEGUBG", "Accuracy: "+gpsAccuracy);
    		this.gpslatitude = gps.getLatitude();
    		this.gpslongitude = gps.getLongitude();
    	}
    		
    }
    
    public void setDevice(DeviceInfo device){
    	this.device = device;
    }
        
	private void NetworkService(){
		net = new NetworkLocation(context, bd, device);
		this.netlatitude = net.getLatitude();
		this.netlongitude = net.getLongitude();
    }
    
    public double getLatitude(){
    	return this.latitude;
    }
    
    public float getGpsAccuracy(){
		return this.gpsAccuracy;
	}
    
    public double getLongitude(){
    	return this.longitude;
    }     	
    
    public String getUsingProvider(){
    	return provider;
    }

}
