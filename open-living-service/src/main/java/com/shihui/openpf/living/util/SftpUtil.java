package com.shihui.openpf.living.util;

import com.shihui.commons.ApiLogger;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.xfer.FileSystemFile;

public class SftpUtil {
	private static final String HOST = "172.16.88.98";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "123456";

    public static boolean download(String host, String userName, String password, String filePath, String localPath) {
    	ApiLogger.info("SftpUtil : download : host:["+host+"] userName:["+userName+"] password:["+password+"] filePath:["+filePath+"] localPath:["+localPath+"]");
        SSHClient ssh = new SSHClient();
        SFTPClient sftp = null;
        try {
        	ApiLogger.info("SftpUtil : download() : - 1 -");
            ssh.loadKnownHosts();
            ssh.connect(host);
            ApiLogger.info("SftpUtil : download() : - 2 -");
//            ssh.authPublickey(USERNAME);
            ssh.authPassword(userName, password);
            sftp = ssh.newSFTPClient();
            ApiLogger.info("SftpUtil : download() : - 3 -");
            sftp.get(filePath, new FileSystemFile(localPath));
            ApiLogger.info("SftpUtil : download() : - 4 -");
	        sftp.close();
	        ApiLogger.info("SftpUtil : download() : - 5 -");
	        ssh.disconnect();
	        ApiLogger.info("SftpUtil : download() : - 6 -");
	        ssh.close();
	        ApiLogger.info("SftpUtil : download() : - 7 -");
	        return true;
        } catch(Exception e) {
        }  
        try {
	        if( sftp != null) {
		        sftp.close();
	        }
	        ssh.disconnect();
	        ssh.close();
        }catch(Exception e) {
        }
    
        return false;
    }
}
