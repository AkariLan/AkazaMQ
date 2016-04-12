package akari.lan.akaza;

import java.util.Queue;

import akari.lan.akaza.util.FileQueue;

public class FileQueueTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			Queue<byte[]> fileQueue = new FileQueue("./cacheData/");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
