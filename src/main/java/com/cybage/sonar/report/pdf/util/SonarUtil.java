package com.cybage.sonar.report.pdf.util;

public class SonarUtil {

	public static String getConversion(Integer minutes){
		Integer hours = null;
		Integer days = null;
		
		if(minutes>=60 && minutes < 1140){
			hours = minutes / 60;
			minutes = minutes % 60;
			return hours + "h " + minutes + "min";
		}else if(minutes >= 1140){
			days = minutes / 1140;
			//minutes = minutes - (minutes * days);
			minutes = minutes % 1140;
			hours = minutes / 60;
			minutes = minutes % 60;
			return days + "d " + hours + "h " + minutes + "min";
		}else{
			return minutes + "min";
		}
	}
}
