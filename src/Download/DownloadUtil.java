package Download;

public class DownloadUtil {

	//��Դ��·��
	private String path;
	//���ر���·��
	private String targetFile;
	//�����߳���
	private int threadNum;
	private DownThread[] threads;
	//�ļ���С
	private int fileSize;
	
	public DownloadUtil(String path, String targetFile, int threadNum) {
		super();
		this.path = path;
		this.targetFile = targetFile;
		this.threadNum = threadNum;
		threads = new DownThread[threadNum];
	}

	public void download(){
		URL url = new URL(path);
	}
	
	public double getCompeleteRate(){
		
	}
	
	private class DownThread extends Thread{
		
	}
}