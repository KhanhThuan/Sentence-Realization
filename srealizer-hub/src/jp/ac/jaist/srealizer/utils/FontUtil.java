package jp.ac.jaist.srealizer.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import de.unisb.cs.st.infoZilla.Helpers.RegExHelper;

public class FontUtil {
	
	private static int[][] maps = {
			 { 97, 224, 225, 7843, 227, 7841, 226, 7847, 7845, 7849, 7851, 7853, 259, 7857, 7855, 7859, 7861, 7863}, 	// a
			 { 65, 192, 193, 7842, 195, 7840, 194, 7846, 7844, 7848, 7850, 7852, 258, 7856, 7854, 7858, 7860, 7862},	// A
			 { 117, 249, 250, 7911, 361, 7909, 432, 7915, 7913, 7917, 7919, 7921},		// u
			 { 85, 217, 218, 7910, 360, 7908, 431, 7914, 7912, 7916, 7918, 7920},		// U
			 { 101, 232, 233, 7867, 7869, 7865, 234, 7873, 7871, 7875, 7877, 7879},		// e
			 { 69, 200, 201, 7866, 7868, 7864, 202, 7872, 7870, 7874, 7876, 7878},		// E
			 { 111, 242, 243, 7887, 245, 7885, 244, 7891, 7889, 7893, 7895, 7897, 417, 7901, 7899, 7903, 7905, 7907},	// o
			 { 79, 210, 211, 7886, 213, 7884, 212, 7890, 7888, 7892, 7894, 7896, 416, 7900, 7898, 7902, 7904, 7906},	// O
			 { 105, 236, 237, 7881, 297, 7883},		// i
			 { 73, 204, 205, 7880, 296, 7882},	// I
			 { 89, 7922, 221, 7926, 7928, 7924}, // Y
			 { 121, 7923, 253, 7927, 7929, 7925},	// y
			 { 100, 272,273},	// d 
			 { 68, 272}	// D

			 
	 };
	public static Map UTFTONONE(){
		Map m = new HashMap();
		for(int i = 0; i < maps.length; i++){
			for(int j = 1; j < maps[i].length ;j++)
				m.put(new Integer(maps[i][j]),new Integer(maps[i][0]));
		}
		return m;
	}
	private static String[] refine(String[] values) {
        ArrayList list = new ArrayList();
        int count = 0;
        for(int i =0; i <values.length; i++){
            int code;
            String[] numStr = values[i].split(";");
           // System.out.println(numStr[0]);
            try{
                code= Integer.parseInt(numStr[0].trim());
              
            }catch(NumberFormatException e){
                code = 0;
               
                //e.printStackTrace();
            }
            if(code >= 192){
                list.add(String.valueOf(code));
                count++;
            }
          }
        String[] results = new String[count];
        for(int i = 0;i < count; i++)
        results[i] = (String)list.get(i);
                
        return results;
    }
	
	public static String NCR2UTF(String inputStr){
		  String outStr = "";
		   String[] values = inputStr.split("(&#[\\d]+;)");
		   Iterable<MatchResult> abnormalChs = RegExHelper.findMatches(Pattern.compile("(&#[\\d]+;)"), inputStr);
		   
		   Iterator<MatchResult> it = abnormalChs.iterator();
		   int i = 0;
		   while(it.hasNext()){
			   MatchResult m = it.next();
			  // System.out.println(m);
			   if(i < values.length)
			   outStr += values[i++];
			   String s = "";
			   for(int j =m.start(); j < m.end(); j++){
				   s+= inputStr.charAt(j);
			   }
			   s = s.replaceAll("[&#;]","");
			   int codepoint;
			   try{
                   codepoint = Integer.parseInt(s.trim());
               }catch(NumberFormatException e){
                   codepoint = 0;
               }
               outStr += (char)(codepoint);
			   //System.out.println(s);
		   }
		  while (i < values.length) {
			   outStr += values[i++];
		  }
		  return outStr;
		  
	}
	
	public static String NCR2UTF2(String inputStr){// error with string containing number only
        String outStr = "";
          String[] values = inputStr.split("(&#[\\d]+;)");
          String[] values1= inputStr.split("(&#)");
           values1 = refine(values1);
           System.out.println(values.length + ", " + values1.length);
       //    System.out.println(values[0] +"," + values1[0]);
         int j = 0;
          for( int i = 0; i < ((values1.length > values.length) ? values1.length : values.length); i++){
             if(values.length > 0 && i < values.length)
              outStr += values[i];
             
              if(j < values1.length){
                 String s = values1[j++];
                 int codepoint;
                   try{
                       codepoint = Integer.parseInt(s.trim());
                   }catch(NumberFormatException e){
                       codepoint = 0;
                   }
                   if(codepoint !=0) 
                   outStr += (char)(codepoint);
                 
              }
          }
          
        return outStr;
    }
	public static String UTF2NCR(String inputStr){
		String outStr = "";
		for (int i = 0; i < inputStr.length(); i++)
		{
			int codePoint = inputStr.codePointAt(i);
			String codeStr = "";
			// Skip over the second char in a surrogate pair
			if (codePoint > 0xffff)
			{
				i++;
			   
			}
			//System.out.println(codePoint);
			 if(codePoint > 0 && codePoint < 192) codeStr += (char)codePoint;
			 else codeStr +="&#"+codePoint +";";
			outStr += codeStr;
		}
		return outStr;
	}
	public static String UTF2CODENCR(String inputStr){
		String outStr = "";
		for (int i = 0; i < inputStr.length(); i++)
		{
			int codePoint = inputStr.charAt(i);
			String codeStr = "";
			// Skip over the second char in a surrogate pair
			if (codePoint > 0xffff)
			{
				i++;
			   
			}
			//System.out.println(codePoint);
			 if(codePoint != 32 ) codeStr += ", " + codePoint;
			
			outStr += codeStr;
		}
		return "{" + outStr.substring(1) + "},";
	}
	public static String UTF2NONE(Map m,String inputStr){
		String outStr = "";
		for (int i = 0; i < inputStr.length(); i++)
		{
			int codePoint = inputStr.charAt(i);
			
			 if(m.containsKey(new Integer(codePoint))) outStr += (char)((Integer)m.get(new Integer(codePoint))).intValue();
			 else outStr +=  (char) codePoint;
			 
			
		}
		return outStr;
	}
	public static void main(String[] args) throws IOException{
		System.out.println(FontUtil.UTF2NONE(FontUtil.UTFTONONE(), "Đi"));
		System.out.println((int)'D');
		String s = "Đ đ ";
		/*for(int i =0; i < s.length(); i++)
			System.out.println((int) s.charAt(i));*/



		System.out.println(FontUtil.NCR2UTF("Ki&#7875;m tra ti&#7871;n &#273;&#7897;\nvanthuan"));
		System.out.println(FontUtil.UTF2NCR("Thầy nhập các từ cách nhau bởi dấu phẩy."));
		System.out.println(FontUtil.UTF2NCR("Nơi lưu trữ:"));

		System.out.println(FontUtil.UTF2NCR("Ngày bắt đầu:"));
		System.out.println(FontUtil.UTF2NCR("Hạn kết thúc:"));
		System.out.println(FontUtil.UTF2NCR("Hồ sơ công việc:"));
		System.out.println(FontUtil.UTF2NCR("Người nhập ý kiến"));
		System.out.println(FontUtil.UTF2NCR("Nhóm phòng ban"));
		System.out.println(FontUtil.UTF2NCR("Nội dung chỉ đạo"));
		System.out.println(FontUtil.UTF2NCR("Thời điểm chỉ đạo"));
		System.out.println(FontUtil.UTF2NCR("Người nhập ý kiến"));


	}
}
