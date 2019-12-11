package com.pr.sepp.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class CSVFileUtil {
	private FileInputStream fis = null;
	private InputStreamReader isw = null;
	private BufferedReader br = null;

	public CSVFileUtil(String filenNme, String encoding) throws Exception {

		URL url = new URL(filenNme);
		InputStream fin =url.openStream();
		isw = new InputStreamReader(fin, encoding);
		br = new BufferedReader(isw);
	}

	public String readLine() throws Exception {

		StringBuffer readLine = new StringBuffer();
		boolean bReadNext = true;

		while (bReadNext) {
			if (readLine.length() > 0) {
				readLine.append("\r\n");
			}
			String strReadLine = br.readLine();

			if (strReadLine == null) {
				return null;
			}
			readLine.append(strReadLine);

			if (countChar(readLine.toString(), '"', 0) % 2 == 1) {
				bReadNext = true;
			} else {
				bReadNext = false;
			}
		}
		return readLine.toString();
	}

	public static String[] fromCSVLine(String source, int size) {
		ArrayList<String> tmpArray = fromCSVLinetoArray(source);
		if (size < tmpArray.size()) {
			size = tmpArray.size();
		}
		String[] rtnArray = new String[size];
		tmpArray.toArray(rtnArray);
		return rtnArray;
	}

	public static ArrayList<String> fromCSVLinetoArray(String source) {
		if (source == null || source.length() == 0) {
			return new ArrayList<String>();
		}
		int currentPosition = 0;
		int maxPosition = source.length();
		int nextComma = 0;
		ArrayList<String> rtnArray = new ArrayList<String>();
		while (currentPosition < maxPosition) {
			nextComma = nextComma(source, currentPosition);
			rtnArray.add(nextToken(source, currentPosition, nextComma));
			currentPosition = nextComma + 1;
			if (currentPosition == maxPosition) {
				rtnArray.add("");
			}
		}
		return rtnArray;
	}

	public static String toCSVLine(String[] strArray) {
		if (strArray == null) {
			return "";
		}
		StringBuffer cvsLine = new StringBuffer();
		for (int idx = 0; idx < strArray.length; idx++) {
			String item = addQuote(strArray[idx]);
			cvsLine.append(item);
			if (strArray.length - 1 != idx) {
				cvsLine.append(',');
			}
		}
		return cvsLine.toString();
	}

	public static String toCSVLine(ArrayList<String> strArrList) {
		if (strArrList == null) {
			return "";
		}
		String[] strArray = new String[strArrList.size()];
		for (int idx = 0; idx < strArrList.size(); idx++) {
			strArray[idx] = strArrList.get(idx);
		}
		return toCSVLine(strArray);
	}

	private int countChar(String str, char c, int start) {
		int i = 0;
		int index = str.indexOf(c, start);
		return index == -1 ? i : countChar(str, c, index + 1) + 1;
	}

	private static int nextComma(String source, int st) {
		int maxPosition = source.length();
		boolean inquote = false;
		while (st < maxPosition) {
			char ch = source.charAt(st);
			if (!inquote && ch == ',') {
				break;
			} else if ('"' == ch) {
				inquote = !inquote;
			}
			st++;
		}
		return st;
	}

	private static String nextToken(String source, int st, int nextComma) {
		StringBuffer strb = new StringBuffer();
		int next = st;
		while (next < nextComma) {
			char ch = source.charAt(next++);
			if (ch == '"') {
				if ((st + 1 < next && next < nextComma) && (source.charAt(next) == '"')) {
					strb.append(ch);
					next++;
				}
			} else {
				strb.append(ch);
			}
		}
		return strb.toString();
	}

	private static String addQuote(String item) {
		if (item == null || item.length() == 0) {
			return "\"\"";
		}
		StringBuffer sb = new StringBuffer();
		sb.append('"');
		for (int idx = 0; idx < item.length(); idx++) {
			char ch = item.charAt(idx);
			if ('"' == ch) {
				sb.append("\"\"");
			} else {
				sb.append(ch);
			}
		}
		sb.append('"');
		return sb.toString();
	}
}