package assignment;
import java.io.*;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.Arrays;
import java.util.List;

public class FileProcessor {
	int numOfThreads;
	String path;
	ForkJoinPool threadPool;
	public FileProcessor(int numOfThreads, String path) {
		this.numOfThreads = numOfThreads;
		this.path = path;
		threadPool = new ForkJoinPool(numOfThreads);
	}
	
	public void processFiles() {
		File[] files = (new File(path)).listFiles();
		List<File> listOfFiles = Arrays.asList(files);
		if (listOfFiles == null || listOfFiles.size() == 0) {
			System.out.println("No file or invalid file input");
			return;
		}
		System.out.println("start parsing files");
		listOfFiles.forEach(f -> {
			FileInfo file = new FileInfo(f.getName());
			submitJob(f, file);
		});

	}
	
	public void submitJob(File f, FileInfo fileInfo) {
		if (threadPool == null) return;
		try {
	         threadPool.submit(() -> {
				try {
					UrlProcessor urlProcessor = new UrlProcessor();
					unZipFile(f).parallel().forEach(
							         line -> {
							        	 process(line, fileInfo, urlProcessor); 
							        });
					System.out.println("File name: " + fileInfo.fileName 
							         + " Total: " + fileInfo.total
							         + ". Succeeded: " + fileInfo.success 
							         + ". Failed: " + fileInfo.failure);
				} catch (Exception e) {
					System.out.println("Processing error " + e);
				} 
			}).get();
	    } catch (Exception e ) {
	    	System.out.println("Errors " + e);
	    }

	}
	
	public Stream<String> unZipFile(File f) throws FileNotFoundException, IOException {
	    FileInputStream file = null;
        BufferedInputStream buffer = null;
        GZIPInputStream zip = null;

        try {
            file = new FileInputStream(f.getAbsoluteFile());
            buffer = new BufferedInputStream(file);
            zip = new GZIPInputStream(buffer);
        } catch( IOException e ) {
            if (file != null) {
               try {
                    file.close();
                } catch(IOException ex ) {
                	System.out.println("FileStream Closing Error " + ex);
              }
            }
            if (buffer != null) {
               try {
                    buffer.close();
                } catch(IOException ex) {
                	System.out.println("BufferStream Closing Error " + ex);
              }
            }
            if (zip != null) {
                try {
                    zip.close();
                } catch(IOException ex ) {
                	System.out.println("ZipInstream Closing Error " + ex);
               }
            }
            throw new UncheckedIOException(e);
        } 
        InputStreamReader input = null;
        try {
        	input = new InputStreamReader(zip);
        	BufferedReader br = new BufferedReader(input);
        	return br.lines().onClose(() -> {
           	   if (br != null) {
                    try {
                        br.close();
                     } catch(IOException ex) {
                    	 System.out.println("buffer reading error " + ex);
                     }
                }
           });
        } catch (Exception e){
        	System.out.println("File reading error " + e);
        	return null;
        }
      
	}

	
	
	private void process(String line, FileInfo fileInfo, 
			UrlProcessor urlProcessor) {
		urlProcessor.sendHttpGet(line, fileInfo);
	}
}

