package apilbslocation;

public class APinfo {
	private double TxPower; // dbm
	private double TxAntennaGain;//dbi
	private double TxCableLoss;//db
	private String SSID;
	private String address;
	private double latitude;
	private double longitude;
	private double lastRssi;
	
	public APinfo(){
		this.address = "";
		this.SSID = "";
		this.TxPower = 20.0;
		this.TxAntennaGain = 5.0;
		this.TxCableLoss = 0.3;
		this.latitude = 0;
		this.longitude = 0;
		this.lastRssi = 0;
	}
	
	public APinfo(String ssid, double latitude, double longitude){
		this.address = "";
		this.SSID = ssid;
		this.TxPower = 20.0;
		this.TxAntennaGain = 5.0;
		this.TxCableLoss = 0.3;
		this.latitude = latitude;
		this.longitude = longitude;
		this.lastRssi = 0;
	}
	
	public APinfo(String address, String ssid, double txpower, double txantennagain, double txcableloss, double latitude, double longitude){
		this.address = address;
		this.SSID = ssid;
		this.TxPower = txpower;
		this.TxAntennaGain = txantennagain;
		this.TxCableLoss = txcableloss;
		this.latitude = latitude;
		this.longitude = longitude;
		this.lastRssi = 0;
	}
	
	public double getLastRssi(){
		return this.latitude;
	}
	
	public double getLatitude(){
		return this.latitude;
	}
	public void setLastRssi(double rssi){
		this.lastRssi = rssi;
	}
	
	public double getLongitude(){
		return this.longitude;
	}
	
	public double getTxPower(){
		return this.TxPower;
	}
	
	public double getTxAntennaGain(){
		return this.TxAntennaGain;
	}
	
	public double getTxCableLoss(){
		return this.TxCableLoss;
	}
	
	public String getSSID(){
		return this.SSID;
	}
	
	public String getAddress(){
		return this.address;
	}
	
	public void setLatitude(double latitude){
		this.latitude = latitude;
	}
	
	public void setLongitude(double longitude){
		this.longitude = longitude;
	}
	
	public void setTxPower(double txpower){
		this.TxPower = txpower;
	}
	
	public void setTxAntennaGain(double txantennagain){
		this.TxAntennaGain = txantennagain;
	}
	
	public void setTxCableLoss(double cableloss){
		this.TxCableLoss = cableloss;
	}
	
	public void setSSID(String ssid){
		this.SSID = ssid;
	}
	
	public void setAddress(String address){
		this.address = address;
	}
	
	
	@Override
	public String toString(){
		String result = "SSID: "+ SSID+", Address: "+address+", TxPower; "+TxPower+", AntennaGain: "+TxAntennaGain+", TxCableloss: "
				+ TxCableLoss+", Latitude: "+latitude+", Longitude: "+longitude;
		return result;
		
	}
	
}

