package com.shihui.openpf.living.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import com.shihui.commons.ApiLogger;

import com.shihui.openpf.living.io3rd.CheckFile;
import com.shihui.openpf.living.io3rd.CheckItem;
import com.shihui.openpf.living.io3rd.CheckRefundeVo;
import com.shihui.openpf.living.io3rd.RefundeFile;
import com.shihui.openpf.living.io3rd.RefundeItem;

public class FTPUtil {
	private static final String SPLIT = "|";
	private static final String CHECKFILE_SUFFIX = ".txt";
	private static final String REFUNDEFILE_SUFFIX = "Refunde.txt";
//    private static Logger log = LoggerFactory.getLogger(FTPUtil.class.getName());

    
    public static CheckRefundeVo downFile(String url, int port,String username, String password, String checkPath,String refundePath) {
    	CheckRefundeVo vo = new CheckRefundeVo();
    	//
        FTPClient ftp = new FTPClient();
        try {
            int reply;
            ftp.connect(url, port);
            //如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
            ftp.login(username, password);//登录
            reply = ftp.getReplyCode();

            ftp.enterLocalPassiveMode();   //这句重要，不行换enterRemoteActiveMode 看看

//            log.info("FTPUtil--reply:"+reply);
            ApiLogger.error("FTPUtil--reply:" + reply);
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return null;
            }
            //
            vo.setCheckFile(downCheckFile(ftp,checkPath));
            vo.setRefundeFile(downRefundeFile(ftp, refundePath));
            //
            ftp.logout();
            return vo;
        } catch (Exception e) {
//            log.error("downFile error!!",e);
        	ApiLogger.error("downFile error!!" + e.getMessage());
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (Exception ioe) {
                }
            }
        }
        return null;
    }
    private static CheckFile downCheckFile(FTPClient ftp, String remotePath) throws Exception{
        ftp.changeWorkingDirectory(remotePath);//转移到FTP服务器目录
        FTPFile[] fs = ftp.listFiles();
        if( fs.length <= 0)
        	return null;
        
        for(FTPFile ff:fs){
//            log.info("FTPUtil--filename--"+ff.getName());
        	ApiLogger.error("FTPUtil--filename--" + ff.getName());
            String name = ff.getName();
            if(ff.isFile() && CHECKFILE_SUFFIX.compareToIgnoreCase(name.substring(name.length()-CHECKFILE_SUFFIX.length())) == 0){
            	return analyseCheckFile(ftp.retrieveFileStream(name));
            }
        }
        return null;
    }
    private static  RefundeFile downRefundeFile(FTPClient ftp, String remotePath) throws Exception {
        ftp.changeWorkingDirectory(remotePath);//转移到FTP服务器目录
        FTPFile[] fs = ftp.listFiles();
        if( fs.length <= 0)
        	return null;
        
        for(FTPFile ff:fs){
//            log.info("FTPUtil--filename--"+ff.getName());
        	ApiLogger.error("FTPUtil--filename--" + ff.getName());
            String name = ff.getName();
            if(ff.isFile() && REFUNDEFILE_SUFFIX.compareToIgnoreCase(name.substring(name.length()-REFUNDEFILE_SUFFIX.length())) == 0){
            	return analyseRefundeFile(ftp.retrieveFileStream(name));
            }
        }
        return null;    	
    }
    public static CheckFile analyseCheckFile(InputStream in) throws Exception {
    	BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
    	
    	CheckFile checkFile = new CheckFile();
    	String line1 = br.readLine();
    	if( line1 == null )
    		return null;
    	checkFile.setBjceg(line1);
    	
    	String line2 = br.readLine();
    	if(line2 == null )
    		return null;
    	String[] line2Array= line2.split(SPLIT);
    	if(line2Array.length < 2)
    		return null;
    	checkFile.setTotalMoney(Integer.parseInt(line2Array[0]));
    	checkFile.setTotalAmount(Integer.parseInt(line2Array[1]));
    	
    	String line3 = br.readLine();
    	if(line3 == null)
    		return null;
    	String[] line3Array = line3.split(SPLIT);
    	if(line3Array.length < 2)
    		return null;
    	checkFile.setSuccessMoney(Integer.parseInt(line3Array[0]));
    	checkFile.setSuccessAmount(Integer.parseInt(line3Array[0]));
    	
    	String linex;
    	String[] linexArray;
    	ArrayList<CheckItem> checkList = new ArrayList<CheckItem>();
    	while((linex = br.readLine()) != null) {
    		linexArray = linex.split(SPLIT);
    		if(linexArray.length < 6)
    			return null;
    		
    		CheckItem checkItem = new CheckItem();
    		checkItem.setBillNo(linexArray[0]);
    		checkItem.setPay(linexArray[1]);
    		checkItem.setPayDate(linexArray[2]);
    		checkItem.setBankBillNo(linexArray[3]);
    		checkItem.setSign(linexArray[4]);
    		checkItem.setMessage(linexArray[5]);
    		checkList.add(checkItem);
    	}
    	br.close();
    	
    	if(checkList.size() <= 0)
    		return null;
    	checkFile.setCheckList(checkList);
    	return checkFile;    	
    }
    public static  RefundeFile analyseRefundeFile(InputStream in) throws Exception {
    	BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
    	RefundeFile refundeFile = new RefundeFile();
    	
    	String line1 = br.readLine();
    	if(line1 == null )
    		return null;
    	String[] line1Array= line1.split(SPLIT);
    	if(line1Array.length < 2)
    		return null;
    	refundeFile.setTotalMoney(Integer.parseInt(line1Array[0]));
    	refundeFile.setTotalAmount(Integer.parseInt(line1Array[1]));
    	   	
    	String linex;
    	String[] linexArray;
    	ArrayList<RefundeItem> refundeList = new ArrayList<RefundeItem>();
    	while((linex = br.readLine()) != null) {
    		linexArray = linex.split(SPLIT);
    		if(linexArray.length < 3)
    			return null;
    		
    		RefundeItem refundeItem = new RefundeItem();
    		refundeItem.setSerial(Integer.parseInt(linexArray[0]));
    		refundeItem.setPayDate(linexArray[1]);
    		refundeItem.setBillNo(linexArray[2]);
    		refundeList.add(refundeItem);
    	}
    	br.close();
    	
    	if(refundeList.size() <= 0)
    		return null;
    	refundeFile.setRefundeList(refundeList);
    	
    	return refundeFile;
    }
}
