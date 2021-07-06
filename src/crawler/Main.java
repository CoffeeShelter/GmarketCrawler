package crawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import crawler.fileSystem.FileManagement;
import crawler.gmarket.CrawlerManagement;

public class Main {
	private final static int MAX_PAGE = 50;
	private final static int MAX_COUNT = 100;
	private final static String MAINFOLDER = "Gmarket_210706";

	public static void main(String[] args) {
		CrawlerManagement crawler = new CrawlerManagement();
		FileManagement fileManagement = new FileManagement();

		try {
			File folder = new File(MAINFOLDER);
			if (!folder.exists()) {
				folder.mkdir();
			}
			// 1쪽 부터 MAX_PAGE 쪽 까지 반복
			// 한 페이지당 MAX_COUNT 개의 상품 확인
			for (int page = 1; page <= MAX_PAGE; page++) {
				String url = "http://browse.gmarket.co.kr/list?category=200001192&k=24&p=" + Integer.toString(page);
				for (int count = 1; count <= MAX_COUNT; count++) {
					// 시작 전 시간
					long beforeTime = System.currentTimeMillis();

					File file = new File(MAINFOLDER + "/info.txt");
					BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
					// 상품 주소 얻기
					String productURL = crawler.getProductAddress(url, count);
					if (productURL == null) {
						writer.write(page + "페이지 " + count + "번째 url 오류");
						writer.flush();
						writer.close();
						System.out.println("URL 오류");
						continue;
					}
					String title = crawler.getTitle(productURL);
					String reNameTitle = crawler.reNameTitle(title);
					if (reNameTitle.length() > 150) {
						reNameTitle = reNameTitle.substring(0, 150);
						System.out.println("상품 제목 150자 이상 ... 변경 후 제목: " + reNameTitle);
					}
					Vector<String> mainImage = crawler.getMainImage(productURL);
					Vector<String> detailImage = crawler.getDetailImage(productURL);

					String writeString = title + "\t" + reNameTitle + "\t" + productURL + "\n";
					writer.write(writeString);

					writer.flush();
					writer.close();
					
					// 이미 존재 하는 파일 이면 파일 명 뒤에 현재 _page_count 추가 하여 저장
					if (fileManagement.makeFolder(MAINFOLDER, reNameTitle) == -1) {
						reNameTitle = reNameTitle + "_" + Integer.toString(page) + "_" + Integer.toString(count);
						fileManagement.makeFolder(MAINFOLDER, reNameTitle);
					}
					fileManagement.makeFolder(MAINFOLDER + "/" + reNameTitle, "Main Image");
					fileManagement.makeFolder(MAINFOLDER + "/" + reNameTitle, "Detail Image");

					int i = 1;
					for (String imageURL : mainImage) {
						crawler.saveImage(imageURL, MAINFOLDER + "/" + reNameTitle + "/Main Image",
								reNameTitle + "_main_" + Integer.toString(i));
						i += 1;
					}

					i = 1;
					for (String imageURL : detailImage) {
						crawler.saveImage(imageURL, MAINFOLDER + "/" + reNameTitle + "/Detail Image",
								reNameTitle + "_detail_" + Integer.toString(i));
						i += 1;
					}

					System.out.println(page + "페이지 " + count + "번째 : " + reNameTitle + " 저장 완료");

					long afterTime = System.currentTimeMillis();
					long time = (afterTime - beforeTime) / 1000;
					System.out.println("걸린 시간 : " + time + "초");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
