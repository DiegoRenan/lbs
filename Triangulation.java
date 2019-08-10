package apilbslocation;

import android.util.Log;

public class Triangulation {

	private static double x = 0;
	private static double y= 0;
	
	public Triangulation(){}
	
	protected void triangulation2D(double[] ap1, double[] ap2, double[] ap3){
		/**
		 * x == Latitude
		 * y == Longitude
		 * r ==  raio
		 * Ap[0] == x, Ap[1] == y, Ap[2] == r, Ap[3] == SSID
		 *  
		 */
		double d1 = (Math.pow(ap1[2], 2) - Math.pow(ap3[2], 2)) - (Math.pow(ap1[0], 2) - Math.pow(ap3[0], 2)) - (Math.pow(ap1[1], 2) - Math.pow(ap3[1], 2));  
		double d2 = (Math.pow(ap2[2], 2) - Math.pow(ap3[2], 2)) - (Math.pow(ap2[0], 2) - Math.pow(ap3[0], 2)) - (Math.pow(ap2[1], 2) - Math.pow(ap3[1], 2));
		double u1 = 2*(ap3[0] - ap1[0]);
		double v1 = 2*(ap3[1] - ap1[1]);
		double u2 = 2*(ap3[0] - ap2[0]);
		double v2 = 2*(ap3[1] - ap2[1]);
		double m;
		
		if(u2==0){
			m = 0;
		}else
			m = (-1) * (u2 / u1);
		
		
		u2 = (m * u1) + u2;
		v2 = (m * v1) + v2;
		d2 = (m * d1) + d2;
		
		if(v2==0){
			y = 0;
		}else		
			y = d2/v2;
			
		x = (d1 - (v1 * y)) / u1;
		Log.d("TRI_DEBUG","X: "+ x+" Y: "+ y);
			
	}
	
	protected double getPointX(){
		return this.x;
	}
	
	protected double getPointY(){
		return this.y;
	}
	


}
