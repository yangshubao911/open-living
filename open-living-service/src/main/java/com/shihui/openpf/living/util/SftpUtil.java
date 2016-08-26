package com.shihui.openpf.living.util;

import com.shihui.commons.ApiLogger;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.xfer.FileSystemFile;

public class SftpUtil {

	public static boolean download(String host, String userName, String password, String filePath, String localPath) {
    	ApiLogger.info("SftpUtil : download : host:["+host+"] userName:["+userName+"] password:["+password+"] filePath:["+filePath+"] localPath:["+localPath+"]");
        SSHClient ssh = new SSHClient();
        SFTPClient sftp = null;
        try {
            ssh.loadKnownHosts();
            ssh.connect(host);
            ssh.authPassword(userName, password);
            sftp = ssh.newSFTPClient();
            sftp.get(filePath, new FileSystemFile(localPath));
	        sftp.close();
	        ssh.disconnect();
	        ssh.close();
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
