package akari.lan.akaza.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import org.apache.log4j.Logger;

public class FileQueue implements Queue<byte[]> {
	
	public static long SIGNATURE = 0x414B415A414D51L; //AKAZAMQ 8BYTE	
	public static long BUFFERLENGTH = 0x10000; //64kb
	public static String cacheDir = "";
	
	private Logger logger = Logger.getLogger(FileQueue.class);
	private MappedByteBuffer writeBuffer;
	private MappedByteBuffer readBuffer;
	
	
	public FileQueue(String dataFileDir) throws Exception {
		// TODO Auto-generated constructor stub
		
		File cacheFileDir = new File(dataFileDir);
		boolean existAlready = false;
		
		//create dir
		if(!cacheFileDir.exists()){
			try {
				if(cacheFileDir.mkdirs()){
					logger.info("Create Queue Cache File Dir.");
				}else{
					logger.info("Can Not Create Queue Cache File Dir.");
					throw new Exception("Can Not Create Queue Cache File Dir.");
				}				
			} catch (Exception e) {
				// TODO: handle exception
				logger.info("Can Not Create Queue Cache File Dir.");
				throw new Exception("Can Not Create Queue Cache File Dir.",e);
			}
		}else{
			existAlready = true;
		}
		cacheDir = dataFileDir;
		
		//load file		
		if(existAlready){
			//verify file
			File[] files = cacheFileDir.listFiles();
			if(files.length>0){
				for(int i = 0;i<files.length;i++){
					if(!isAkazaFile(files[i])){
						logger.error("Wrong Dir! Contain Wrong File.");
						throw new Exception("Wrong Dir! Contain Wrong File.");
					}
				}
			}
			//locate read write queue
			
		}else{
			//create new queue file
			writeBuffer = createNewCacheFile();
			readBuffer = writeBuffer;
		}	
	}

	private MappedByteBuffer createNewCacheFile() throws Exception{
		
		String dataFilePath = cacheDir+System.currentTimeMillis()+".aka";
		
		File queue = new File(dataFilePath);		
		if(!queue.exists()){
			try {
				if(queue.createNewFile()){
					logger.info("Create Queue File:"+dataFilePath);
				}else{
					logger.info("Could Not Create Queue File:"+dataFilePath);
					throw new Exception("Could Not Create Queue File:"+dataFilePath);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				logger.info("Could Not Create Queue File:"+dataFilePath);
				throw new Exception("Could Not Create Queue File:"+dataFilePath);
			}
		}else{
			throw new Exception("File Already Exist:"+dataFilePath);
		}
		
		/*if(!queue.exists()){
			logger.error("Queue File Doesn't Exits!");
			throw new Exception("Queue File Doesn't Exits!");
		}
		if(queue.isDirectory()){
			logger.error("Queue File is Directory!");
			throw new Exception("Queue File is Directory!");
		}*/
		if(!queue.canRead()||!queue.canWrite()){
			logger.error("Queue File invalid Permissions!");
			throw new Exception("Queue File invalid Permissions!");
		}
		
		@SuppressWarnings("resource")
		RandomAccessFile queueFile = new RandomAccessFile(queue, "rwd");
		
		MappedByteBuffer queueBuffer = queueFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, FileQueue.BUFFERLENGTH);
		
		
		queueBuffer.putLong(SIGNATURE); //SIGNATURE		
		queueBuffer.put((byte) 0); // write finished? 0:unfinished 1:finished
		queueBuffer.put((byte) 0); // read finished? 0: 1:
		queueBuffer.putLong(32);   // write offset, can write at 32nd byte (index of 0)
		queueBuffer.putLong(32);   // read offset, same
		
		queueBuffer.force();//flush	
		return queueBuffer;
	}
	
	private boolean isAkazaFile(File file) throws IOException{
		if(!file.exists()||file.isDirectory())
			return false;
		if(file.length()<32)
			return false;
		
		byte[] fileHead = new byte[8];
		
		InputStream inputStream = new FileInputStream(file);
		inputStream.read(fileHead);
		
		
		long fileSig = byte2long(fileHead);
		
		System.out.println(fileSig);
		
		if(fileSig != SIGNATURE)
			return false;
		return true;		
	}
	
	private long byte2long(byte[] bytes){
		long result = 0;
		long temp = 0;
		for(int i = 0;i<8;i++){
			temp = bytes[i] & 0xffL;
			temp <<= ((7-i)*8);
			result |= temp;
		}
		return result;
	}
	
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator<byte[]> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends byte[]> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean add(byte[] e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean offer(byte[] e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public byte[] remove() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] poll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] element() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] peek() {
		// TODO Auto-generated method stub
		return null;
	}

	

}
