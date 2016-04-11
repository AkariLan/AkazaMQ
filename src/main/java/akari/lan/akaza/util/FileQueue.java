package akari.lan.akaza.util;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

import org.apache.log4j.Logger;

public class FileQueue implements Queue<byte[]> {
	
	Logger logger = Logger.getLogger(FileQueue.class);
	
	public FileQueue(File queue) throws Exception {
		// TODO Auto-generated constructor stub
		if(!queue.exists()){
			logger.error("Queue File Doesn't Exits!");
			throw new Exception("Queue File Doesn't Exits!");
		}
		if(queue.isDirectory()){
			logger.error("Queue File is Directory!");
			throw new Exception("Queue File is Directory!");
		}
		if(!queue.canRead()||!queue.canWrite()){
			logger.error("Queue File invalid Permissions!");
			throw new Exception("Queue File invalid Permissions!");
		}
		
		
		
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
