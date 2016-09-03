package com.shihui.openpf.living.util;

import com.shihui.commons.ApiLogger;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.xfer.FileSystemFile;

public class SftpUtil {

	public static boolean download(String host, String userName, String password, String filePath, String localPath) {
        SSHClient ssh = new SSHClient();
        SFTPClient sftp = null;
        boolean ret = false;
        try {
            ssh.loadKnownHosts();
            ssh.connect(host);
            ssh.authPassword(userName, password);
            sftp = ssh.newSFTPClient();
            sftp.get(filePath, new FileSystemFile(localPath));
	        ret = true;
        } catch(Exception e) {
        }  finally {
	        try {
		        if( sftp != null)
			        sftp.close();
		        if(ssh.isConnected())
		        	ssh.disconnect();
		        ssh.close();
	        }catch(Exception e) {
	        }
        }
        ApiLogger.info("SftpUtil: download() : ret : " + ret);
        return ret;
    }
}
