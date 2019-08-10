package apilbslocation;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BDconnection {
	protected SQLiteDatabase myDatabase = null;
	private static String DATABASE_NAME = "lbslocation.db";
    private static String TABLE_NAME = "apinfo";
    private static final String CREATE_TABLE = "create table if not exists "+ TABLE_NAME+"("
    													+ "ssid TEXT PRIMARY KEY,"
    													+ "address TEXT DEFAULT ' ',"
    													+ "txpower REAL NOT NULL,"
    													+ "txantennagain REAL NOT NULL,"
    													+ "txcableloss REAL NOT NULL,"
    													+ "latitude REAL NOT NULL,"
    													+ "longitude REAL NOT NULL);";
    
    private static final String CREATE_TABLE_RSSI = "CREATE TABLE IF NOT EXISTS rssi("
														+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
														+ "rssi REAL NOT NULL,"
														+ "ssid TEXT,"
														+ "FOREIGN KEY(ssid) REFERENCES "+TABLE_NAME+"(ssid));";

	public BDconnection(Context context) {
		myDatabase = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
		Log.i("BD_INFO", myDatabase.getPath());
		myDatabase.execSQL(CREATE_TABLE);
		myDatabase.execSQL(CREATE_TABLE_RSSI);
		
	}
	public void createTableRssi(){
		myDatabase.execSQL(CREATE_TABLE_RSSI);
	}
	public Cursor getCursor(String ssid){
		Cursor c;
		try{
			String asColumnsToReturn[] = {"ssid", "address", "txpower", "txantennagain", "txcableloss", "latitude", "longitude"};
			c = myDatabase.query(TABLE_NAME, asColumnsToReturn, "ssid=?", new String[]{ssid}, null, null, null);
			c.moveToFirst();
		}catch(SQLException erro){
			Log.e("BD_ERRO", "Erro no Cursor de Dados "+ erro.getMessage());
			return null;
		}
		
		return c;
	}
	
	public Cursor getCursor(){
		Cursor c;
		try{
			String asColumnsToReturn[] = {"ssid", "address", "txpower", "txantennagain", "txcableloss", "latitude", "longitude"};
			c = myDatabase.query(TABLE_NAME, asColumnsToReturn, null, null, null, null, null);
		}catch(SQLException erro){
			Log.e("BD_ERRO", "Erro no Cursor de Dados "+ erro.getMessage());
			return null;
		}
		
		return c;
	}
	
	public Cursor getCursorRssi(String ssid){
		Cursor c;
		try{
			//String asColumnsToReturn[] = {"rssi"};
			c = myDatabase.rawQuery("SELECT rssi From rssi WHERE ssid = "+"'"+ssid+"'", null);
			c.moveToFirst();
		}catch(SQLException erro){
			Log.e("BD_ERRO", "Erro no Cursor de Dados "+ erro.getMessage());
			return null;
		}
		
		return c;
	}
	
	public boolean isRegistered(String ssid){
		Cursor c = null;
		try{
			String asColumnsToReturn[] = {"ssid", "address", "txpower", "txantennagain", "txcableloss", "latitude", "longitude"};
			c = myDatabase.query(TABLE_NAME, asColumnsToReturn, "ssid=?", new String[]{ssid}, null, null, null);	
		}catch(SQLException erro){
			c.close();
			Log.e("BD_ERRO", "Erro no Cursor de Dados "+ erro.getMessage());
		}
		if(c.getCount() > 0){
			c.close();
			return true;
		}else{
			c.close();
			return false;
		}
		
	}
	
	public void deleteTable(String table){
		try{
			myDatabase.execSQL("DROP TABLE IF EXISTS "+table);
			Log.i("BD_INFO", "Table "+table+" deletado com sucesso");
		}catch(SQLException e){
			Log.i("BD_INFO", "Erro ao deletar table "+table+" "+e.getMessage());
		}
	}
	
	public double[] readRssi(String ssid){
		Cursor c = getCursorRssi(ssid);
		int count = 0;
		int index = c.getCount();
		double[] rssi = new double[index];
		if(c.moveToFirst()){
			int indexRSSI = c.getColumnIndex("rssi");
			do{
				rssi[count] = c.getDouble(indexRSSI);
				count = count + 1;
			}while(c.moveToNext());
		}
		c.close();
		return rssi;
	}
	
	public int countRssi(String ssid){
		Cursor c = getCursorRssi(ssid);
		return c.getCount();
	}
	
	public int countSsid(){
		Cursor c = null;
		try{
			String asColumnsToReturn[] = {"ssid"};
			c = myDatabase.query(TABLE_NAME, asColumnsToReturn, null, null, null, null, null);	
		}catch(SQLException erro){
			c.close();
			Log.e("BD_ERRO", "Erro no Cursor de Dados "+ erro.getMessage());
		}
		return c.getCount();
	}
	
	public double readLastRssi(String ssid){
		Cursor c = getCursorRssi(ssid);
		int index = c.getCount();
		double rssi = 0;
		if(index>0){
			if(c.moveToLast()){
				int indexRSSI = c.getColumnIndex("rssi");
				rssi = c.getDouble(indexRSSI);
			}
			c.close();
		}
		return rssi;
	}
	
	public void insertRssi(String ssid, double rssi){
		try{
			ContentValues contentvalues = new ContentValues();	
			contentvalues.put("ssid", ssid);
			contentvalues.put("rssi", rssi);
			long status = myDatabase.insert("rssi", null, contentvalues);
			if(status<0)
				Log.w("BD_ERRO", "Dados não inseridos");
			else
				Log.w("BD_INF", "Inserido com sucesso");
		}catch(Exception erro){
			Log.e("BD_ERRO", "Erro ao gravar dados no Banco "+ erro.getMessage());
		}
	}
	
	public void deleteAllRssi(String ssid){
		try{
			myDatabase.rawQuery("DELETE FROM rssi", null);
			Log.i("BD_ERRO", "Deletado com sucesso");
		}catch(SQLException e){
			Log.e("BD_ERRO", "Erro ao deletar dados do Banco "+ e.getMessage());
		}
	}
	
	public int deleteLastRssi(String ssid){
		int result = 0;
		try{
			int count = countRssi(ssid);	
			result = myDatabase.delete("rssi", "id="+String.valueOf(count), null);
			Log.i("BD_ERRO", "Deletado com sucesso: "+ result+" RSSI: "+result);
		}catch(SQLException e){
			Log.e("BD_ERRO", "Erro ao deletar dado do Banco "+ e.getMessage());
		}
		return result; 
	}
	
	public APinfo readAP(String SSID){
		Cursor c = getCursor(SSID);
		APinfo ap = new APinfo();
		try{
			if(c.getCount() > 0){
				int indexSSID = c.getColumnIndex("ssid");
				int indexAddress = c.getColumnIndex("address");
				int indexTxpower = c.getColumnIndex("txpower");
				int indexTxantennagain = c.getColumnIndex("txantennagain");
				int indexTxcableloss = c.getColumnIndex("txcableloss");
				int indexLatitude = c.getColumnIndex("latitude");
				int indexLongitude = c.getColumnIndex("longitude");						
				ap.setSSID(c.getString(indexSSID));
				ap.setAddress(c.getString(indexAddress));
				ap.setTxPower(c.getDouble(indexTxpower));
				ap.setTxAntennaGain(c.getDouble(indexTxantennagain));
				ap.setTxCableLoss(c.getDouble(indexTxcableloss));
				ap.setLatitude(c.getDouble(indexLatitude));
				ap.setLongitude(c.getDouble(indexLongitude));

			}
			c.close();
		}catch(SQLException erro){
			c.close();
			Log.e("BD_ERRO", "Erro ao ler dados do Banco "+ erro.getMessage());
			return null;
		}
		
		return ap;
	}
	
	public List<APinfo> readAll(){
		Cursor c = getCursor();
		List<APinfo> listAp = new ArrayList<APinfo>();
		if(c.moveToFirst()){
			int indexSSID = c.getColumnIndex("ssid");
			int indexAddress = c.getColumnIndex("address");
			int indexTxpower = c.getColumnIndex("txpower");
			int indexTxantennagain = c.getColumnIndex("txantennagain");
			int indexTxcableloss = c.getColumnIndex("txcableloss");
			int indexLatitude = c.getColumnIndex("latitude");
			int indexLongitude = c.getColumnIndex("longitude");
			do{
				APinfo ap = new APinfo();
				listAp.add(ap);
				ap.setSSID(c.getString(indexSSID));
				ap.setAddress(c.getString(indexAddress));
				ap.setTxPower(c.getDouble(indexTxpower));
				ap.setTxAntennaGain(c.getDouble(indexTxantennagain));
				ap.setTxCableLoss(c.getDouble(indexTxcableloss));
				ap.setLatitude(c.getDouble(indexLatitude));
				ap.setLongitude(c.getDouble(indexLongitude));
			}while(c.moveToNext());
		}
		c.close();
		return listAp;
	}
	
	public void insertAP(APinfo ap){
		try{
			Cursor c = getCursor(ap.getSSID());
			if(c.getCount()<1){
				ContentValues contentvalues = new ContentValues();
				contentvalues.put("ssid", ap.getSSID());
				contentvalues.put("address", ap.getAddress());
				contentvalues.put("txpower", ap.getTxPower());
				contentvalues.put("txantennagain", ap.getTxAntennaGain());
				contentvalues.put("txcableloss", ap.getTxCableLoss());
				contentvalues.put("latitude", ap.getLatitude() );
				contentvalues.put("longitude", ap.getLongitude());
				long status = myDatabase.insert("apinfo", null, contentvalues);
				if(status<0)
					Log.w("BD_ERRO", "Dados não inseridos: "+ status);
				else
					Log.w("BD_INF", "Inserido com sucesso");
			}else
				Log.i("BD_INFO", "Dado ja cadastrado: ");
		}catch(Exception erro){
			Log.e("BD_ERRO", "Erro ao gravar dados no Banco "+ erro.getMessage());
		}
		
	}
	
	public void deleteAP(String ssid){
		try{
			myDatabase.delete(TABLE_NAME, "ssid = "+ssid, null);
			Log.w("BD_INF", "Deletado com sucesso");
		}catch(SQLException erro){
			Log.e("BD_ERRO", "Erro ao deletar dados do Banco "+ erro.getMessage());
		}
	}
	
	public void BdClose(){
		myDatabase.close();
	}

}
