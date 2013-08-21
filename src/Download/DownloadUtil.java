package Download;

public class DownloadUtil {

	//资源的路径
	private String path;
	//下载保存路径
	private String targetFile;
	//启用线程数
	private int threadNum;
	private DownThread[] threads;
	//文件大小
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