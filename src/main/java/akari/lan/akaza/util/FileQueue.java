package akari.lan.akaza.util;

import java.io.File;
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
	
	private Logger logger = Logger.getLogger(FileQueue.class);
	private MappedByteBuffer queueBuffer;
	
	
	public FileQueue(String dataFilePath) throws Exception {
		// TODO Auto-generated constructor stub
		
		File queue = new File(dataFilePath);
		
		boolean existAlready = false;
		
		if(!queue.exists()){
			if(queue.createNewFile()){
				logger.info("Create Queue File:"+dataFilePath);
			}else{
				logger.info("Could Not Create Queue File:"+dataFilePath);
				throw new Exception("Could Not Create Queue File:"+dataFilePath);
			}
		}else{
			existAlready = true;
		}
		
		/*if(!queue.exists()){
			logger.error("Queue File Doesn't Exits!");
			throw new Exception("Queue File Doesn't Exits!");
		}*/
		if(queue.isDirectory()){
			logger.error("Queue File is Directory!");
			throw new Exception("Queue File is Directory!");
		}
		if(!queue.canRead()||!queue.canWrite()){
			logger.error("Queue File invalid Permissions!");
			throw new Exception("Queue File invalid Permissions!");
		}
		
		@SuppressWarnings("resource")
		RandomAccessFile queueFile = new RandomAccessFile(queue, "rwd");
		
		queueBuffer = queueFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, FileQueue.BUFFERLENGTH);
		
		if(existAlready){
			try {
				long fileSig = queueBuffer.getLong(0);
				if(fileSig != SIGNATURE){
					throw new Exception("File Is Not A AkazaMQ File!");
				}
			} catch (IndexOutOfBoundsException e) {
				// TODO: handle exception
				throw new Exception("File Is Not A AkazaMQ File!");
			}			
		}else{
			queueBuffer.putLong(0, SIGNATURE);
			queueBuffer.put((byte)1);
		}
		queueBuffer.force();
		Thread.sleep(100);
		byte[] temp = new byte[9];
		queueBuffer.get(temp);
		System.out.println(temp[0]);
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
