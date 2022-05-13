package com.example.tachographanalysis.workinfo;

import com.example.tachographanalysis.DigitalAnalysisController;
import com.example.tachographanalysis.analogueAnalysis.ChangeColor;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WorkInfo {
    public static JSONArray getDailyActivity(String activity) throws ParseException {
        JSONArray json=new JSONArray();
        System.out.println(activity);
        String[] activity2=activity.split(" ");
        System.out.println(Arrays.toString(activity2));
        for (int i=0;i<activity2.length;i++){
            if(activity2[i].equals("Work")||activity2[i].equals("Break")||activity2[i].equals("Drive")){
                JSONObject j2=new JSONObject();
                j2.put("start",activity2[i+2]);
                if(i+7<activity2.length) {
                    j2.put("stop", activity2[i + 7]);
                    j2.put("czas", timeDiffrence2(activity2[i +2],activity2[i + 7]) );
                    j2.put("czas2", ChangeColor.ktoraGodzina((int)timeDiffrence2(activity2[i +2],activity2[i + 7])) );
                }else {
                    j2.put("stop", "24:00");
                    j2.put("czas", timeDiffrence2(activity2[i +2],"24:00") );
                    j2.put("czas2", ChangeColor.ktoraGodzina((int) timeDiffrence2(activity2[i +2],"24:00")) );
                }
                j2.put("activity",activity2[i]);
                json.put(j2);
            }
        }
        return json;
    }
    public static long timeDiffrence2(String t1, String t2) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Date date1 = format.parse(t1);
        Date date2  = format.parse(t2);
        return  (date2.getTime() - date1.getTime())/1000/60;
    }
}
