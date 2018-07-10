package com.navercorp.board;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BoardApplication {

	public static final String OS = System.getProperty("os.name").toLowerCase();

	public static void main(String[] args) {
		
		setSampleImg();
		
		SpringApplication.run(BoardApplication.class, args);

	}
	
	/**
	 * 서버를 구동할때 샘플이미지를 Resources location에 복사해둔다.
	 */
	public static void setSampleImg() {
		String path = "C:/img/groups";
		String OS = BoardApplication.OS;
		
		
		if (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") >= 0) {
			path = "/home/img/groups";
		}
		
		File dir = new File(path);
		if (!dir.isDirectory()) {
			dir.mkdirs();
		} 
		File serverFile = new File(path + File.separator + "sample.png");
		
		try {
			InputStream inputStream = BoardApplication.class.getResourceAsStream("/static/img/groups/sample.png");
			BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
			OutputStream outputStream = new FileOutputStream(serverFile);
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
			
			byte[] buffer = new byte[1024];
			
			int length;
			
			while ((length = bufferedInputStream.read(buffer)) >= 0) {
				bufferedOutputStream.write(buffer, 0, length);
			}
			
			bufferedInputStream.close();
			bufferedOutputStream.close();
			inputStream.close();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
