package apilbslocation;

import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.IBinder;
import android.util.Log;

public class NetworkLocation extends Service {
	protected WifiManager wifi;
	protected WifiReceiver receiverWifi;
	protected Context context;
	protected BDconnection bd;
	protected DeviceInfo device;
	protected String sb;
	protected static double latitude;
	protected static double longitude;
	protected static boolean status;
	
	public NetworkLocation(Context context, BDconnection bd, DeviceInfo device) {
		this.context = context;
		this.bd = bd;
		this.device = device;
		Location();
	}
	
	public double getLatitude(){
		return NetworkLocation.latitude;
	}
	
	public double getLongitude(){
		return NetworkLocation.longitude;
	}
	
	public boolean statusNet(){
		return status;
	}
	protected void Location(){
	   	
		wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		
		if(!wifi.isWifiEnabled()){
		       wifi.setWifiEnabled(true);
		}
		wifi.startScan(); 
	    receiverWifi = new WifiReceiver(context);
    	
	    context.registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	}
	
	protected void stopUsingNetwork(){
		receiverWifi.clearAbortBroadcast();
		receiverWifi.abortBroadcast();
	}
	
	@Override
	public String toString(){
		return sb;
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public class WifiReceiver extends BroadcastReceiver {
		protected CalculateRSSI calculateRssi = new CalculateRSSI(bd);
		
		public WifiReceiver(Context context) {
		}

		@Override
		public void onReceive(Context context, Intent intent) {
	        List<ScanResult> wifiList = wifi.getScanResults();
	        APinfo[] arrayAP = new APinfo[wifiList.size()];
	        Triangulation triangulation = new Triangulation();
	            
	       	int i;
	        for(i = 0; i < wifiList.size(); i++){
	        	
	        	if(bd.isRegistered(wifiList.get(i).SSID)){
	        		arrayAP[i] = bd.readAP(wifiList.get(i).SSID);
	        		double rssi =  (double) CalculateRSSI.calculateDistanceFSPL((double) wifiList.get(i).level, wifiList.get(i).frequency, arrayAP[i], device);
	        		rssi = calculateRssi.filter(arrayAP[i], rssi);
	        		arrayAP[i].setLastRssi(rssi);
	        		Log.d("NET_DEBUG", i +".aSSID: "+arrayAP[i].getSSID()+" glevel: "+wifiList.get(i).level +" gssid: "+ wifiList.get(i).SSID + "RSSI: "+ rssi);
	        	}
	        }
	        
	                                
	        if(arrayAP.length > 2){
	        	NetworkLocation.status = true;
	        	double ap1[] = {arrayAP[0].getLatitude(), arrayAP[0].getLongitude(), arrayAP[0].getLastRssi()};
	            double ap2[] = {arrayAP[1].getLatitude(), arrayAP[1].getLongitude(), arrayAP[1].getLastRssi()};
	            double ap3[] = {arrayAP[2].getLatitude(), arrayAP[2].getLongitude(), arrayAP[2].getLastRssi()};
	            //double ap2[] = {0, 0, 6};
	            //double ap3[] = {-5, 0, 3};
	            triangulation.triangulation2D(ap1, ap2, ap3);
	    		NetworkLocation.latitude = triangulation.getPointX();
	    		NetworkLocation.longitude = triangulation.getPointY();
	        }else{
	        	NetworkLocation.status = false;
	        }
	                                  
		}
			

	}

}
