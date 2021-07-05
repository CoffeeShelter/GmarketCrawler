package crawler.gmarket;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Vector;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

// 시작 페이지
// http://browse.gmarket.co.kr/list?category=200001192&k=24&p=1

public class CrawlerManagement {

	private final static int MAX_COUNT = 100;
	private final static int MAX_PAGE = 50;

	public CrawlerManagement() {

	}

	// 상품 주소 구하기
	// url : 상품 리스트 주소
	// num : 상품 번호
	public String getProductAddress(String url, int num) {
		String selector;
		char[] urlCharArray = url.toCharArray();
		String tmp = "";
		tmp += urlCharArray[url.length() - 2];
		tmp += urlCharArray[url.length() - 1];

		// 1 페이지 일 때 selector 주소
		if (tmp.equals("=1")) {
			selector = "#section__inner-content-body-container > div:nth-child(4) > div:nth-child("
					+ Integer.toString(num)
					+ ") > div.box__item-container > div.box__information > div.box__information-major > div.box__item-title > span > a";
		}
		// 1 페이지가 아닐 때 selector 주소
		else {
			selector = "#section__inner-content-body-container > div:nth-child(2) > div:nth-child("
					+ Integer.toString(num)
					+ ") > div.box__item-container > div.box__information > div.box__information-major > div.box__item-title > span > a";
		}
		Connection conn = Jsoup.connect(url);

		try {
			Document html = conn.get();
			Elements elements = html.select(selector);
			String productAddress = elements.attr("href");
			if (productAddress != null || productAddress != "")
				return productAddress;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	// 상품 명 구하기
	// url : 상품 주소
	public String getTitle(String url) {
		String selector;

		selector = "#itemcase_basic > div > h1";
		Connection conn = Jsoup.connect(url);

		try {
			Document html = conn.get();
			Elements elements = html.select(selector);
			String title = elements.text();

			return title;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	// 상품 명 변경
	// '/' 또는 '\' 이 들어가있는 상품명의 경우 상품명을 경로로 인식하므로 변경
	// 변경 방법 : '/' , '\' 제거
	// title : 변경할 상품 명
	public String reNameTitle(String title) {
		String newTitle = title;

		newTitle = newTitle.replaceAll("\\\\", "");
		newTitle = newTitle.replaceAll("/", "");

		return newTitle;
	}

	// 이미지 주소 얻기 ( 메인 이미지 = 상단 프로필 이미지 )
	// url : 상품 주소
	public Vector<String> getMainImage(String url) {
		String selector = "#container > div.item-topinfowrap > div.thumb-gallery > div.box__viewer-container > ul.viewer > li > a > img";
		Connection conn = Jsoup.connect(url);

		Vector<String> imagePath = new Vector<>();

		try {
			Document html = conn.get();
			Elements elements = html.select(selector);

			for (Element element : elements) {
				imagePath.add(element.attr("src"));
			}

			if (imagePath.size() > 0 && imagePath.size() != 1) {
				imagePath.remove(0);
			}

			return imagePath;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 이미지 주소 얻기 ( 상세 설명 광고 이미지 = 하단 이미지 )
	// url = 상품 주소
	public Vector<String> getDetailImage(String url) {
		String selector = "#vip-tab_detail > div.box__detail-view.js-toggle-content > div.vip-detailarea_seller > iframe";
		Connection conn = Jsoup.connect(url);

		Vector<String> imagePath = new Vector<>();

		try {
			Document html = conn.get();
			Elements elements = html.select(selector);
			String detailURL = elements.attr("src");

			conn = Jsoup.connect(detailURL);
			html = conn.get();

			selector = "#basic_detail_html > div.ee-contents > div.ee-module > div.ee-image > img";
			elements = html.select(selector);

			for (Element element : elements) {
				imagePath.add(element.attr("src"));
			}

			return imagePath;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void saveImage(String strUrl, String filePath, String fileName) {

		URL url = null;
		InputStream in = null;
		OutputStream out = null;

		if (fileName == null) {
			System.out.println("파일 명 오류");
			return;
		}

		try {
			System.out.println(fileName + " 저장중...");
			url = new URL(strUrl);

			in = url.openStream();
			out = new FileOutputStream(filePath + "/" + fileName + ".jpg"); // 저장경로

			while (true) {
				// 이미지를 읽어온다.
				int data = in.read();
				if (data == -1) {
					break;
				}
				// 이미지를 쓴다.
				out.write(data);

			}

			in.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
