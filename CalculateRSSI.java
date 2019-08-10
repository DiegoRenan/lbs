package apilbslocation;

import android.util.Log;

public class CalculateRSSI {
	//private static APinfo AP = new APinfo();
    private static DeviceInfo Device = new DeviceInfo();
    private BDconnection bd;
	 
	public CalculateRSSI(BDconnection database) {
		this.bd = database;
	}
	
    protected static double calculateDistanceRSSI(double levelInDb, double freqInMHz, String BSSID){
			/**
			 **********FORMULA DA DISTANCIA************
			 *Po(dbm) = Signal Power at zero distance;
			 *Fm() = Received Signal Strength - Receiver Sensitivity
			 *Pr(dbm) = Signal Power Received
			 *n = Path-Loss Exponent, ranges from 2.7 to 4.3
			 *D(m) = 10^[ (Po - Fm - Pr - 10 * n * log10(f) + 30 * n - 27.55) /10* n]; 
			 */
		double SignalPowerZero = Device.getRSignalPowerZero();
		double FadeMargin  = levelInDb - Device.getRSensitivity();
		double SignalPowerReceived = levelInDb;
		double PathLoss = 2.7;
		double exp =   (SignalPowerZero - FadeMargin - SignalPowerReceived - ((10 * PathLoss) * Math.log10(freqInMHz)) + (30.0 * PathLoss) - 27.55)/(10 * PathLoss);
		return Math.pow(10.0, exp);
    }
    
    protected static double calculateDistanceFSPL(double levelInDb, double freqInMHz, APinfo ap, DeviceInfo device)    {
    	/*
    	    * ***********FORMULA DA DISTANCIA*******
    	    * Free Space Path Loss = FSPL
    	    * f = frequency (MHz)
   
    	    * c = the speed of light in a vacuum, 2.99792458 × 10^8 m/s
    	    * k = 20log10(4*PI/c)
    	    * k = -27.55 for d(meters) and f(MHz) 
    	    * FSPL (dB) = 20log10(d) + 20log10(f) + K
    	    * Fade Margin = Received Signal Strength - Receiver Sensitivity
    	    * FSPL = Tx Power - Tx Cable Loss + Tx Antenna Gain + Rx Antenna Gain - Rx Cable Loss - Rx Sensitivity - Fade Margin
    	    * Distance(m) = 10(FSPL – 27.55 – 20log10(f))/20
    	    * 
    	* */
    	double FadeMargin  = levelInDb - device.getRSensitivity();
    	//double FadeMargin = 25;
    	double FSPL = ap.getTxPower() - ap.getTxCableLoss() + ap.getTxAntennaGain() + device.getRAntennaGain() - device.getRCableLoss() - device.getRSensitivity() - FadeMargin; 
    	double exp = (FSPL - 27.55 - (20 * Math.log10(freqInMHz))) / 20.0;
    	return Math.pow(10.0, exp);
    	

    }

    
    private void swap(int max, int pos, double[] vetor){
		double aux = vetor[max];
		vetor[max] = vetor[pos];
		vetor[pos] = aux;
	}
    
    private void leftRightHeap(int i, int n, double[] vetor){
		int max = 2 * i + 1, right = max + 1;
		
		if(max < n){
			if(right < n && vetor[max] < vetor[right])
				max = right;				
			if(vetor[max] > vetor[i]){
				swap(max, i, vetor);
				leftRightHeap(max, n, vetor);
			
		}}
				
	}
	
	private void maxHeap(double[] vetor){
    		for(int i = vetor.length/2 - 1; i >= 0; i--)
    			leftRightHeap(i, vetor.length, vetor);
	}
	
    private double[] ordena(double[] vetor){ 
    	maxHeap(vetor);
		int n = vetor.length;
		for(int i = n - 1; i > 0; i--){
			swap(i, 0, vetor);
			leftRightHeap(0, --n, vetor);
		}		
		return vetor;
    }
		
    public double filter(APinfo ap, double distance){
    	String ssid = ap.getSSID();
    	double rssi = distance;
    	
    	double lastRssi = bd.readLastRssi(ssid);
    	bd.insertRssi(ssid, rssi);
    	
    	int index = bd.countRssi(ssid);
    	double[] v = new double[index];
	    double[] vetor = new double[index];
	    v = bd.readRssi(ssid).clone();
	    
	    vetor = ordena(v);
	    
	    for(int i = 0; i <vetor.length; i++){
	    	Log.d("RSSI_DEBUG", "Vetor = "+i+"["+vetor[i]+"]");
	    }
	    		
	    	// 1 - Mediana
	    int n = vetor.length;
	    int pos = n/2;
	    double mediana = vetor[pos];
	    double[] desvios = new double[n];
	    
	    //2 - Desvios
	    for(int i = 0; i < n; i++){
	    	desvios[i] = Math.abs(vetor[i] - mediana);
	    	//Log.d("RSSI_DEBUG", "Desvio["+i+"]: "+ desvios[i]);
	    }
	    		
	    //3 - Media dos desvios
	    double soma = 0;
	    for(int i = 0; i < n; i++)
	    	soma = soma + desvios[i];
	    double MDA = soma/n;
	    Log.d("RSSI_DEBUG", "Media dos desvio: "+ MDA);
	    	
	    //4 - Z escore
	    double[] z = new double[vetor.length]; 
	    for(int i = 0; i < n; i++){
		   z[i] = Math.abs(0.6745 * (desvios[i] / MDA));
	    }
	    int aux = 0;
	    	
	    while( (aux < vetor.length-1) && (vetor[aux] != distance)){
		   aux = aux + 1;
	    }
	    Log.d("RSSI_DEBUG", "distance: "+distance);
	    //Log.d("RSSI_DEBUG", "Vetor["+aux+"]: "+vetor[aux]);
	    Log.d("RSSI_DEBUG", "Erro "+z[aux]);
	    
	    if(z[aux] > 3.0){
			bd.deleteLastRssi(ssid);
			rssi = lastRssi;
	    }
	   
    	if(bd.countRssi(ssid)> 25){
    		bd.deleteTable("rssi");
    		bd.createTableRssi();
    	}
    	return rssi;
    }
    
}
