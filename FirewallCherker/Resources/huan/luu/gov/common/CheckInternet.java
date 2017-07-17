package huan.luu.gov.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;

public class CheckInternet {
	private static final String URL_DESTINATION = "http://ipinfo.io/json";
	private String ISPName;
	private String IP;
	
	
	public boolean isConnectedToInternet() {
		WebLink weblink = new WebLink("http://google.com", -1);
		return weblink.isAvalableLink();
	}
	
	private void getNetworkInfomation() throws IOException, JSONException {
		InputStream is = new URL(URL_DESTINATION).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			System.out.println(jsonText);
			JSONObject json = new JSONObject(jsonText);
			ISPName = json.getString("org");
			IP = json.getString("ip");
		} finally {
			is.close();
		}
	}
	
	
	public String getISPName() throws JSONException, IOException {
		if(ISPName == null) {
			getNetworkInfomation();
		}
		return ISPName;
	}
	
	public String getMyIP() throws JSONException, IOException {
		if(IP == null) {
			getNetworkInfomation();
		}
		return IP;
	}
	
	private String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

}
