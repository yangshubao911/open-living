package com.shihui.openpf.living.util;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.xfer.FileSystemFile;

public class SftpUtil {
	private static final String HOST = "172.16.88.98";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "123456";

    public static boolean download(String host, String userName, String password, String filePath, String localPath) {
        SSHClient ssh = new SSHClient();
        SFTPClient sftp = null;
        try {
            ssh.loadKnownHosts();
            ssh.connect(host);
//            ssh.authPublickey(USERNAME);
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
		        ssh.disconnect();
		        ssh.close();
	        }
        }catch(Exception e) {
        }
    
        return false;
    }
}
