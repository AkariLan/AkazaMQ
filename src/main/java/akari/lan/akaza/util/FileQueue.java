package akari.lan.akaza.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import org.apache.log4j.Logger;

public class FileQueue implements Queue<byte[]> {

	public static long SIGNATURE = 0x414B415A414D51L; // AKAZAMQ 8BYTE
	public static long BUFFERLENGTH = 0x10000; // 64kb

	public File cacheDir; // Every Queue must have different Dir
	private Logger logger = Logger.getLogger(FileQueue.class);
	//private MappedByteBuffer writeBuffer;
	//private RandomAccessFile writeFile;
	//private Object[] writePair = new Object[2]; //0 MappedByteBuffer 1 RandomAccessFile
	//private MappedByteBuffer readBuffer;
	//private RandomAccessFile readFile;
	//private Object[] readPair = new Object[2];
	private Buffile writeBf;
	private Buffile readBf;

	public FileQueue(String dataFileDir) throws Exception {
		// TODO Auto-generated constructor stub
		File cacheFileDir = new File(dataFileDir);
		boolean existAlready = false;
		// create dir
		if (!cacheFileDir.exists()) {
			try {
				if (cacheFileDir.mkdirs()) {
					logger.info("Create Queue Cache File Dir.");
				} else {
					logger.info("Can Not Create Queue Cache File Dir.");
					throw new Exception("Can Not Create Queue Cache File Dir.");
				}
			} catch (Exception e) {
				// TODO: handle exception
				logger.info("Can Not Create Queue Cache File Dir.");
				throw new Exception("Can Not Create Queue Cache File Dir.", e);
			}
		} else {
			existAlready = true;
		}
		cacheDir = cacheFileDir;

		// load file
		if (existAlready) {
			// verify file
			File[] files = cacheFileDir.listFiles();
			if (files.length > 0) {
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory() || !isAkazaFile(files[i])) {
						logger.error("Wrong Dir! Contain Wrong File: " + files[i].getPath());
						throw new Exception("Wrong Dir! Contain Wrong File: " + files[i].getPath());
					}
				}
			}
			// locate read write queue
			// local write, write finished == 0
			for (int i = 0; i < files.length; i++) {

			}

		} else {
			// create new queue file
			writeBf = createNewCacheFile();
			readBf = writeBf;
		}
	}// end con
	
	private Buffile locateNextWriteFileBuffer() throws Exception{
		List<File> cacheFiles = verifyAndReturnCacheFiles();
		if(cacheFiles==null){
			return null;
		}
		File maybeWrite = cacheFiles.get(cacheFiles.size()-1);
		
		InputStream in = new FileInputStream(maybeWrite);
		byte[] writeHead = new byte[9];
		in.read(writeHead);
		in.close();
		short writeFlag = writeHead[8];
		if(writeFlag != 0){
			maybeWrite = null;
		}
		
		if(maybeWrite!=null){
			RandomAccessFile writeFile = new RandomAccessFile(maybeWrite, "rwd");
			MappedByteBuffer writeBuffer = writeFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0,
					FileQueue.BUFFERLENGTH);
			Buffile writeBuffile = new Buffile(writeBuffer, writeFile);
			
			return writeBuffile;
		}else{
			return null;
		}
		
	}

	private Buffile locateNextReadFileBuffer() throws Exception {
		List<File> cacheFiles = verifyAndReturnCacheFiles();
		if(cacheFiles==null){
			return null;
		}
		File nextRead = null;		
		for(int i = 0;i<cacheFiles.size();i++){
			File temp = cacheFiles.get(i);
			InputStream in = new FileInputStream(temp);
			byte[] fileHead = new byte[10];
			in.read(fileHead);
			short readflag = fileHead[9];
			if (readflag==0) {
				nextRead = temp;
				break;
			}
			in.close();
		}		
		if(nextRead==null){
			return null;
		}else{
			RandomAccessFile readFile = new RandomAccessFile(nextRead, "rwd");
			MappedByteBuffer readBuffer = readFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0,
					FileQueue.BUFFERLENGTH);
			Buffile readBuffile = new Buffile(readBuffer, readFile);
			
			return readBuffile;
		}		
	}

	/**
	 * @return All Exist Cache File In Time Order
	 * @throws Exception
	 */
	private List<File> verifyAndReturnCacheFiles() throws Exception {
		File[] files = cacheDir.listFiles();
		if(files.length<=0){
			return null;
		}
		List<File> cacheFiles = Arrays.asList(files);

		if (files.length > 0) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory() || !isAkazaFile(files[i])) {
					logger.error("Wrong Dir! Contain Wrong File: " + files[i].getPath());
					throw new Exception("Wrong Dir! Contain Wrong File: " + files[i].getPath());
				}
			}
		}
		if(cacheFiles.size()>1)
		Collections.sort(cacheFiles, new Comparator<File>() {
			@Override
			public int compare(File arg0, File arg1) {
				// TODO Auto-generated method stub
				return arg0.getName().compareTo(arg1.getName());
			}
		});
		return cacheFiles;
	}

	/**
	 * @return New Write Buffer
	 * @throws Exception
	 */
	private Buffile createNewCacheFile() throws Exception {
		String dataFilePath = cacheDir.getPath() + System.currentTimeMillis() + ".aka";
		File queue = new File(dataFilePath);
		if (!queue.exists()) {
			try {
				if (queue.createNewFile()) {
					logger.info("Create Queue File:" + dataFilePath);
				} else {
					logger.info("Could Not Create Queue File:" + dataFilePath);
					throw new Exception("Could Not Create Queue File:" + dataFilePath);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				logger.info("Could Not Create Queue File:" + dataFilePath);
				throw new Exception("Could Not Create Queue File:" + dataFilePath);
			}
		} else {
			throw new Exception("File Already Exist:" + dataFilePath);
		}

		/*
		 * if(!queue.exists()){ logger.error("Queue File Doesn't Exits!"); throw
		 * new Exception("Queue File Doesn't Exits!"); }
		 * if(queue.isDirectory()){ logger.error("Queue File is Directory!");
		 * throw new Exception("Queue File is Directory!"); }
		 */
		if (!queue.canRead() || !queue.canWrite()) {
			logger.error("Queue File invalid Permissions!");
			throw new Exception("Queue File invalid Permissions!");
		}

		RandomAccessFile queueFile = new RandomAccessFile(queue, "rwd");
		MappedByteBuffer queueBuffer = queueFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0,
				FileQueue.BUFFERLENGTH);
		queueBuffer.putLong(SIGNATURE); // SIGNATURE
		queueBuffer.put((byte) 0); // write finished? 0:unfinished 1:finished
		queueBuffer.put((byte) 0); // read finished? 0: 1:
		queueBuffer.putLong(32); // write offset, can write at 32nd byte (index
									// of 0)
		queueBuffer.putLong(32); // read offset, same

		queueBuffer.force();// flush
		
		Buffile buffile = new Buffile(queueBuffer, queueFile);
		
		return buffile;
	}

	private boolean isAkazaFile(File file) throws IOException {
		if (!file.exists() || file.isDirectory())
			return false;
		if (file.length() < 32)
			return false;

		byte[] fileHead = new byte[8];

		InputStream inputStream = new FileInputStream(file);
		inputStream.read(fileHead);
		inputStream.close();

		long fileSig = byte2long(fileHead);

		// System.out.println(fileSig);

		if (fileSig != SIGNATURE)
			return false;
		return true;
	}

	private long byte2long(byte[] bytes) {
		long result = 0;
		long temp = 0;
		for (int i = 0; i < 8; i++) {
			temp = bytes[i] & 0xffL;
			temp <<= ((7 - i) * 8);
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

	private class Buffile{
		private MappedByteBuffer mappedByteBuffer;
		private RandomAccessFile randomAccessFile;
		public Buffile(MappedByteBuffer mbb, RandomAccessFile raf){
			this.mappedByteBuffer = mbb;
			this.randomAccessFile = raf;
		}
		public MappedByteBuffer getBuf(){
			return mappedByteBuffer;
		}
		public void setBuf(MappedByteBuffer mbb){
			this.mappedByteBuffer = mbb;
		}
		public RandomAccessFile getFile(){
			return randomAccessFile;
		}
		public void setFile(RandomAccessFile file){
			this.randomAccessFile = file;
		}
	}	
}
