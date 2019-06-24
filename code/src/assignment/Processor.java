package assignment;

public class Processor {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path = System.getProperty("user.dir") + "/inputData";
		
		FileProcessor fileProcessor = new FileProcessor(4, path);
		fileProcessor.processFiles();
		
		
	}

}
