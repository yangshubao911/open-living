package com.shihui.openpf.living.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.shihui.openpf.living.io3rd.CheckFile;
import com.shihui.openpf.living.io3rd.CheckItem;
import com.shihui.openpf.living.io3rd.RefundeFile;
import com.shihui.openpf.living.io3rd.RefundeItem;

public class FileUtil {
	private static final String SPLIT = "\\|";
	private static final String CHECKFILE_SUFFIX = "HZKY_*_1.txt";
	private static final String REFUNDEFILE_SUFFIX = "HZKY*01Refunde.txt";

    public static File getCheckFile(String host, String userName, String password, String path) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(new Date());
    	calendar.add(Calendar.DAY_OF_MONTH, -1);
    	String date = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
    	String fileName = CHECKFILE_SUFFIX.replace("*", date);
    	String filePath = path + fileName;
    	
    	if(!SftpUtil.download(host, userName, password, filePath, filePath))
    		return null;
    	
    	File file = new File(filePath);
    	
    	return file.exists() && file.isFile() ? file : null;
    }
    public static File getRefundeFile(String host, String userName, String password, String path) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(new Date());
    	calendar.add(Calendar.DAY_OF_MONTH, -1);
    	String date = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
    	String fileName = REFUNDEFILE_SUFFIX.replace("*", date);
    	String filePath = path + fileName;
    	
    	if(!SftpUtil.download(host, userName, password, filePath, filePath))
    		return null;
   	
    	File file = new File(filePath);
    	
    	return file.exists() && file.isFile() ? file : null;
    }    
   
    public static CheckFile getCheckFile(File checkFile) {
    	try {
	    	FileInputStream in = new FileInputStream(checkFile);
	    	CheckFile cf = analyseCheckFile(in);
	    	in.close();
	    	return cf;
    	} catch(Exception e) {
    		
    	}
    	return null;
    }
    public static  RefundeFile getRefundeFile(File refundeFile) {
    	try {
	    	FileInputStream in = new FileInputStream(refundeFile);
	    	RefundeFile rf = analyseRefundeFile(in);
	    	in.close();
	    	return rf;
    	} catch(Exception e) {
    		
    	}
    	return null;
    }
    //
    public static CheckFile analyseCheckFile(InputStream in) throws Exception {
    	BufferedReader br = new BufferedReader(new InputStreamReader(in, "GBK"));
    	CheckFile checkFile = new CheckFile();

    	String line1 = br.readLine();
    	if( line1 == null )
    		return null;
    	checkFile.setBjceg(line1.trim());
    	String line2 = br.readLine();
    	if(line2 == null )
    		return null;
    	String[] line2Array= line2.split(SPLIT);

    	if(line2Array.length < 2)
    		return null;
    	checkFile.setTotalMoney(Integer.parseInt(line2Array[0].trim()));
    	checkFile.setTotalAmount(Integer.parseInt(line2Array[1].trim()));

    	String line3 = br.readLine();
    	if(line3 == null)
    		return null;
    	String[] line3Array = line3.split(SPLIT);
    	if(line3Array.length < 2)
    		return null;
    	checkFile.setSuccessMoney(Integer.parseInt(line3Array[0].trim()));
    	checkFile.setSuccessAmount(Integer.parseInt(line3Array[0].trim()));

    	String linex;
    	String[] linexArray;
    	ArrayList<CheckItem> checkList = new ArrayList<CheckItem>();
    	while((linex = br.readLine()) != null) {
    		linexArray = linex.split(SPLIT);
    		if(linexArray.length < 6)
    			return null;
    		
    		CheckItem checkItem = new CheckItem();
    		checkItem.setBillNo(linexArray[0].trim());
    		checkItem.setPay(linexArray[1].trim());
    		checkItem.setPayDate(linexArray[2].trim());
    		checkItem.setBankBillNo(linexArray[3].trim());
    		checkItem.setSign(linexArray[4].trim());
    		checkItem.setMessage(linexArray[5].trim());
    		checkList.add(checkItem);
    	}
    	br.close();

    	if(checkList.size() <= 0)
    		return null;
    	checkFile.setCheckList(checkList);
    	return checkFile;    	
    }
    public static  RefundeFile analyseRefundeFile(InputStream in) throws Exception {
    	BufferedReader br = new BufferedReader(new InputStreamReader(in, "GBK"));
    	RefundeFile refundeFile = new RefundeFile();
    	
    	String line1 = br.readLine();
    	if(line1 == null )
    		return null;
    	String[] line1Array= line1.split(SPLIT);
    	if(line1Array.length < 2)
    		return null;
    	refundeFile.setTotalMoney(Integer.parseInt(line1Array[0].trim()));
    	refundeFile.setTotalAmount(Integer.parseInt(line1Array[1].trim()));
    	   	
    	String linex;
    	String[] linexArray;
    	ArrayList<RefundeItem> refundeList = new ArrayList<RefundeItem>();
    	while((linex = br.readLine()) != null) {
    		linexArray = linex.split(SPLIT);
    		if(linexArray.length < 3)
    			return null;
    		
    		RefundeItem refundeItem = new RefundeItem();
    		refundeItem.setSerial(Integer.parseInt(linexArray[0].trim()));
    		refundeItem.setPayDate(linexArray[1].trim());
    		refundeItem.setBillNo(linexArray[2].trim());
    		refundeList.add(refundeItem);
    	}
    	br.close();
    	
    	if(refundeList.size() <= 0)
    		return null;
    	refundeFile.setRefundeList(refundeList);
    	
    	return refundeFile;
    }
}
