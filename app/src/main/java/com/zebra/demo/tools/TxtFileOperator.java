package com.zebra.demo.tools;

import android.content.Context;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class TxtFileOperator {

    public final static String HISTORY_RFID_FILE_NAME = "ebsRfidData.txt";
    public final static String FILTER_FILE_NAME = "ebsRfidFilter.txt";

    private static FileOutputStream fos;
    private static BufferedWriter writer;

//    public static boolean openFile(Context context, String fileName) {
//        try {
//            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
//            writer = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return false;
//        }
//        return true;
//    }

    public static <T> boolean writeFile(Context context, String fileName, List<T> objList) {
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
            for (int i = 0; i < objList.size(); i++) {
                writer.write(JSON.toJSONString(objList.get(i)));
                writer.newLine();
            }
            writer.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return true;
    }
//    public static boolean closeFile() {
//        try {
//            if (writer != null) {
//                writer.close();
//            }
//            if (fos != null) {
//                fos.close();
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return false;
//        }
//        return true;
//    }

    public static <T> List<T> readJsonFromFile(Context context, String fileName, Class<T> clazz) {
        List<T> list = new ArrayList<T>();
        try {

            try (FileInputStream fis = context.openFileInput(fileName);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"))) {

                String line = "";
                while ((line = reader.readLine()) != null) {
                    list.add(JSON.parseObject(line, clazz));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public static <T> void writeJsonToFile(List<T> data, Context context, String fileName) {

        try {
            try (FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"))) {

                for (int i = 0; i < data.size(); i++) {
                    writer.write(JSON.toJSONString(data.get(i)));
                    writer.newLine();
                    writer.flush();
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
