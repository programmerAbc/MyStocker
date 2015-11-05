package com.example.mystocker;

public class StockInfo {

	String no;
	String name;
	String opening_price;
	String closing_price;
	String current_price;
	String max_price;
	String min_price;
	boolean badNO;
    byte[] chart;
	public StockInfo() {
		no = "";
		name = "";
		opening_price = "0";
		closing_price = "0";
		current_price = "0";
		max_price = "0";
		min_price = "0";
		chart=null;
		badNO = true;
	}

	public byte[] getChart(){
		return chart;
	}

	public void setChart(byte[] chart){
		this.chart=chart;
	}
	
	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOpening_price() {
		return opening_price;
	}

	public void setOpening_price(String opening_price) {
		this.opening_price = opening_price;
	}

	public String getClosing_price() {
		return closing_price;
	}

	public void setClosing_price(String closing_price) {
		this.closing_price = closing_price;
	}

	public String getCurrent_price() {
		return current_price;
	}

	public void setCurrent_price(String current_price) {
		this.current_price = current_price;
	}

	public String getMax_price() {
		return max_price;
	}

	public void setMax_price(String max_price) {
		this.max_price = max_price;
	}

	public String getMin_price() {
		return min_price;
	}

	public void setMin_price(String min_price) {
		this.min_price = min_price;
	}

	public boolean isBadNO() {
		return badNO;
	}

	public void setBadNO(boolean badNO) {
		this.badNO = badNO;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return no;
	}

	public void reset() {
		name = "NULL";
		opening_price = "0";
		closing_price = "0";
		current_price = "0";
		max_price = "0";
		min_price = "0";
		chart=null;
		badNO = true;
	}

	public void copyFrom(StockInfo sinfo) {
		name = sinfo.getName();
		opening_price = sinfo.getOpening_price();
		closing_price = sinfo.getClosing_price();
		current_price = sinfo.getCurrent_price();
		max_price = sinfo.getMax_price();
		min_price = sinfo.getMin_price();
		badNO = sinfo.isBadNO();
		chart=sinfo.chart;
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		return no.equals(((StockInfo) o).getNo());
	}
}
