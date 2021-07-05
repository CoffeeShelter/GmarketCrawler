package crawler.fileSystem;

import java.io.File;

public class FileManagement {
	// 파일 생성
	public int makeFolder(String path, String name) {
		String newPath = path + "/" + name;
		File Folder = new File(newPath);

		if (!Folder.exists()) {
			try {
				Folder.mkdir();
				return 0; // 폴더 생성 성공
			} catch (Exception e) {
				e.printStackTrace();
				return -2; // 폴더 생성 에러
			}
		}

		return -1; // 이미 폴더가 존재 할 시
	}
}
