package apilbslocation;

public class DeviceInfo {
		private double RAntennaGain;//dbi
		private double RCableLoss;//db
		private double RSensitivity;//dbm
		private double RSignalPowerZero;//dbm
		
		public DeviceInfo(){
			this.RAntennaGain = 24.0;
			this.RCableLoss = 0.9;
			this.RSensitivity = -90.0;
			this.RSignalPowerZero = -20.0;
		}
		
		public DeviceInfo(double antennaGain, double cableLoss, double sensitivity){
			this.RAntennaGain = antennaGain;
			this.RCableLoss = cableLoss;
			this.RSensitivity = sensitivity;
		}
		
		public double getRAntennaGain(){
			return RAntennaGain;
		}
		
		public double getRCableLoss(){
			return RCableLoss;
		}
		
		public double getRSensitivity(){
			return RSensitivity;
		}
		
		public double getRSignalPowerZero(){
			return RSignalPowerZero;
		}
		
}

