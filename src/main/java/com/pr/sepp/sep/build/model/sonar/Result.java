package com.pr.sepp.sep.build.model.sonar;


import lombok.Data;

import java.util.ArrayList;

@Data
public class Result {
	private Paging paging;
	private ArrayList<Measures> measures;

	@Data
	public static class Paging {
		private String pageIndex;
		private String pageSize;
		private String total;
	}

	@Data
	public static class Measures {
		private String metric;
		private ArrayList<History> history;

		@Data
		public static class History {
			private String date;
			private double value;
		}
	}
}
