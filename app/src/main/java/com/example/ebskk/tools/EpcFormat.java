package com.example.ebskk.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.example.ebskk.activity.UHFMainActivity;

public class EpcFormat {

	private static List<EpcData> epcResList = new ArrayList<EpcData>();

    public static void loadFile() {
		try {

			String fileName =  "20220406120632_F_F036251_tab_bzgl_wzdm.txt"; // 替换为你的文件路径
			InputStream is = UHFMainActivity.assetManager.open(fileName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			//BufferedReader reader = new BufferedReader(new FileReader(fileName));
	
			String line;
	
			while ((line = reader.readLine()) != null) {
				String[] arrStr = line.split("	");
                //正常数据固定13列
                if (arrStr.length != 13)
                {
                    continue;
                }
				//System.out.println(arrStr[2]);
//				System.out.println(arrStr[3]);
//				System.out.println(arrStr[7]);
//				System.out.println(arrStr[11]);
				EpcData data = new EpcData();
				data.set_lsm(arrStr[2]);
				data.set_name(arrStr[3]);
				data.set_model(arrStr[7]);
				data.set_code(arrStr[11]);
				epcResList.add(data);
			}

			 
		} catch (IOException e) {

			e.printStackTrace();

		}
		
	}
	
	public static List<EpcData> formatEpc(List<String> epcList) {
		List<EpcData> retList = new ArrayList<EpcData>();
		
        for (String epc : epcList)
        {
            //正常epc字符串长度为52
            if (epc.length() != 52)
            {
            	retList.add(new EpcData());
                continue;
            }
            if (!epc.startsWith("050400"))
            {
            	retList.add(new EpcData());
                continue;
            }
            String code = "";
            String name = "";
            String model = "";
            String count = "0";
            String batch = "";
            String type = "";
            String createTime = "";
            String createCompany = "";

            EpcData epcData = new EpcData();

            String lsm = String.valueOf(Integer.parseInt(epc.substring(19, 27),16));
            
            for (EpcData data : epcResList) {
            	if (data.get_lsm().equals(lsm)) {
            		epcData.set_code(data.get_code());
            		epcData.set_name(data.get_name());
            		epcData.set_model(data.get_model());
            		break;
            	}
            }

            count = String.valueOf(Integer.parseInt(epc.substring(34, 37),16));
            epcData.set_count(count);

            batch = String.valueOf(Integer.parseInt(epc.substring(38, 41),16));
            epcData.set_batch(batch);

            String box = epc.substring(37, 38);
            epcData.set_box(box);

            switch (epc.substring(41, 42))
            {
                case "0":
//                    type = "1";
                    type= "单标签";
                    break;
                case "8":
//                    type = "1/4";
                    type = "四标签（1/4）";
                    break;
                case "9":
                    type = "四标签（2/4）";
                    break;
                case "A":
                    type = "四标签（3/4）";
                    break;
                case "B":
                    type = "四标签（4/4）";
                    break;
            }
            epcData.set_type(type);

            createTime = epc.substring(43, 45) + "年" + epc.substring(45, 47) + "月" + epc.substring(47, 49) + "日";
            epcData.set_createTime(createTime);

            createCompany = epc.substring(7, 8);
            int codeHex = Integer.parseInt(epc.substring(8, 10), 16) / 4;
            createCompany += String.valueOf(codeHex);
            createCompany += epc.substring(10, 11);
            codeHex = Integer.parseInt(epc.substring(11, 13), 16) / 4;
            createCompany += String.valueOf(codeHex);
            createCompany += epc.substring(13, 14);
            codeHex = Integer.parseInt(epc.substring(14, 16), 16) / 4;
            createCompany += String.valueOf(codeHex);
            createCompany += epc.substring(16, 17);
            epcData.set_createCompany(createCompany);

            retList.add(epcData);
        }

        return retList;
	}
	
}
