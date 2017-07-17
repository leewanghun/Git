package huan.luu.gov.common;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import huan.luu.gov.gui.support.URLTableModel;
import huan.luu.gov.main.FirewallChecker;

public class WebLink implements Runnable {
	private static final Pattern TITLE_TAG = Pattern.compile("\\<title>(.*)\\</title>",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	public String link;
	// private List<String> byPassList;
	private int num, reposnCode;
	private String status, title;
	private URLTableModel tableModel;

	/**
	 * Kiem tra lien ket co bi chan hay khong. Tra ve True neu lien ket nay bi
	 * chan.
	 * 
	 * @param link: link.
	 * @param num: number of link in the view (table).
	 * @return
	 */
	public WebLink(String link, int num) {
		this.link = link;
		this.num = num;
	}

	/**
	 * Kiem tra url co sang sang hay khong. Tra ve true neu truy cap duoc. Tra ve false neu bi chan.
	 * @return
	 */
	public boolean isAvalableLink() {
		boolean bRet = true;
		HttpURLConnection connection = null;
		status = "Checking";
		try {
			URL u = new URL(link);
			connection = (HttpURLConnection) u.openConnection();
			connection.setConnectTimeout(35000);
			connection.setReadTimeout(35000);
			reposnCode = connection.getResponseCode();
			title = getContent(connection);
			
			//tiep tuc kiem tra neu redirect
			int count = 3;// thoat sau 3 lan
			while(reposnCode == 301){// redirection
				count--;
				String newLink = connection.getHeaderField("Location");
				u = new URL(newLink);
				connection = (HttpURLConnection) u.openConnection();
				connection.setConnectTimeout(35000);
				connection.setReadTimeout(35000);
				reposnCode = connection.getResponseCode();
				title = getContent(connection);
				if (count == 0) break;
			}
			
			if (200 <= reposnCode && reposnCode <= 399) {
				if (title == null) {
//					bRet = false;
				} else if (title.contains("AppServ Open Project")) {
					bRet = false;
				}
			} else {
				bRet = true;
			}
		} catch (MalformedURLException e) {
			bRet = false;
			System.out.println(link + "\t" + e);
			
		} catch (IOException e) {
			bRet = false;
			System.out.println(link + "\t" + e);
		} catch (Throwable e) {
			bRet = false;
			System.out.println(link + "\t" + e);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		return bRet;
	}

	private String getContent(HttpURLConnection connection) throws Exception {
		// read the response body, using BufferedReader for performance
		InputStream in = connection.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.defaultCharset()));
		int n = 0, totalRead = 0;
		char[] buf = new char[1024];
		StringBuilder content = new StringBuilder();

		// read until EOF or first 8192 characters
		while (totalRead < 8192 && (n = reader.read(buf, 0, buf.length)) != -1) {
			content.append(buf, 0, n);
			totalRead += n;
		}
		reader.close();

		// extract the title
		Matcher matcher = TITLE_TAG.matcher(content);
		if (matcher.find()) {
			/*
			 * replace any occurrences of whitespace (which may include line
			 * feeds and other uglies) as well as HTML brackets with a space
			 */
			return matcher.group(1).replaceAll("[\\s\\<>]+", " ").trim();
		} else
			return null;
	}

	@Override
	public void run() {

		this.tableModel.setValueAt("Checking", this.num - 1, 2);
		if (isAvalableLink()) {
			this.tableModel.setValueAt("OK", this.num - 1, 2);
		} else {
			this.tableModel.setValueAt("Error", this.num - 1, 2);
		}
		this.tableModel.setValueAt(Integer.valueOf(this.reposnCode), this.num - 1, 3);
		this.tableModel.setValueAt(this.title, this.num - 1, 4);
		FirewallChecker.COUNT++;
	}

	public void setTableModel(URLTableModel tableModel) {
		this.tableModel = tableModel;
	}
}
