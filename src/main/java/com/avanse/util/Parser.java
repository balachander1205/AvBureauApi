package com.avanse.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
	
	public static void main(String[] args){
		StringBuilder sb=new StringBuilder();
		sb.append("TUEF12AVANSE                     0000NB69968888_C2C3               100247420031427062018180915PN03N010122PRATAPSINHA KIRATSHIHA0207BAGHELE07083004196508012ID03I010102010210AEVPB3703QPT03T0101109850029038030201SC10CIBILTUSCR010201020210030827062018040500781250210260211270213PA03A010140SHANIWAR PURA NEAR DR KORAPE HOUSEAKOLA06022707064440010802010902011008280620179001YPA03A020140HOUSE FIRST LANE BEHIND JAIN LAWN TADIYA0211NAGAR AKOLA06022707064440010802010902011008091220169001YTL04T0010210AVANSE FIN0310PUN/0027940402080501408080707201709081004201811083004201812072030330130721186632830000000000000STD000000000000000300801042018310801072017IQ04I0010108200620180410AVANSE FIN05026106071000000IQ04I0020108150620180410AVANSE FIN05020806072500000IQ04I0030108280620170410AVANSE FIN05020806072500000IQ04I0040108091220160413NOT DISCLOSED05020806073000000ES0700008680102**‼TUEF12AVANSE                     0000NB69968888_C2C3               100247420031427062018180915");
	
	Map<String,String> parsedMap=new HashMap<String,String>();
	
	Pattern p = Pattern.compile("TUEF*");
    Matcher m = p.matcher(sb.toString());
    int i=1;
    while(m.find()) {
    	
        String value = m.group();
        System.out.println("Group"+i +":"+ value);
        i++;
    }
    System.out.println("parsed finished");
		
	}

}
