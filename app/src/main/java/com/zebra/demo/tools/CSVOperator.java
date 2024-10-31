package com.zebra.demo.tools;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.zebra.demo.base.LineChartData;
import com.zebra.rfid.api3.TagData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class CSVOperator {
    public static List<TagData> readCsvFile(Context context) {
        List<TagData> list = new ArrayList<TagData>();
        try {

            try (FileInputStream fis = context.openFileInput("ebsRfidData.csv");
                InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                CSVReader reader = new CSVReader(isr)) {

                String[] nextLine;
                int lineNum = 0;
                while ((nextLine = reader.readNext()) != null) {
                    int num = nextLine.length;
                    String str = "";
                    for (int j = 0; j < num; j++) {
                        str = nextLine[j];
                        TagData tag = JSON.parseObject(str, TagData.class);
                        list.add(tag);
                    }
                    lineNum++;
                }
            }
//            reader.close();
//            isr.close();
//            fis.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public static void writeToCsvFile(List<TagData> data, Context context) {

        try {
            try (FileOutputStream fos = context.openFileOutput("ebsRfidData.csv", Context.MODE_PRIVATE);
                 OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
                 CSVWriter csvWriter = new CSVWriter(osw)) {

                fos.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
                String[] strSrr = new String[data.size()];
                for (int i = 0; i < data.size(); i++) {
                    strSrr[i] = JSON.toJSONString(data.get(i));
                }
                csvWriter.writeNext(strSrr);
                // csvWriter.close();
                // fileWriter.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//    public static List<LineChartData> readCsvFile(Context context) {
//        List<LineChartData> list = new ArrayList<LineChartData>();
//        try {
//            /*le file = new File("ebsRfidData.csv");
//            if (!file.exists()) {
//                return list;
//            }*/
//            FileInputStream fis = context.openFileInput("ebsRfidData.csv");
//            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
//            CSVReader reader = new CSVReader(isr);
//            String[] nextLine;
//            int lineNum = 0;
//            while((nextLine = reader.readNext()) != null) {
//                int num = nextLine.length;
//                String str = "";
//                for (int j = 0; j < num; j++) {
//                    str = nextLine[j];
//                    LineChartData tag = JSON.parseObject(str, LineChartData.class);
//                    list.add(tag);
//                }
//                lineNum++;
//            }
//            reader.close();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        Log.i("llllll", String.valueOf(list.size()));
//        System.out.println("22222:"+String.valueOf(list.size()));
//        return list;
//    }
//    public static void writeToCsvFile(List<LineChartData> data, Context context) throws Exception {
////        File file = new File("ebsRfidData.csv");
////        if (file.exists()) {
////            file.delete();
////        }
////        file.createNewFile();
//        try (FileOutputStream fos = context.openFileOutput("ebsRfidData.csv", Context.MODE_PRIVATE);
//             OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
//             CSVWriter csvWriter = new CSVWriter(osw)) {
//
//            fos.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
//            String[] strSrr = new String[data.size()];
//            for (int i = 0; i < data.size(); i++) {
//                strSrr[i] = JSON.toJSONString(data.get(i));
//            }
//            csvWriter.writeNext(strSrr);
//            // csvWriter.close();
//            // fileWriter.close();
//        }
//    }
}
