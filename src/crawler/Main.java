package crawler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Main {

	private final static int MAX_COUNT = 100;
	private final static int MAX_PAGE = 15;

	public static void main(String[] args) {
		// 검색 시작 주소 :
		// https://browse.gmarket.co.kr/search?keyword=건강기능식품&k=32&p=1

		int pageNum = 1; // 페이지 수

		try {
			for (int page = pageNum; page <= MAX_PAGE; page++) {
				String URL = "https://browse.gmarket.co.kr/search?keyword=건강기능식품&k=32&p=" + Integer.toString(page);
				System.out.println(URL);
				Connection conn = Jsoup.connect(URL);
				Document html = conn.get(); // conn.post();

				for (int count = 1; count <= MAX_COUNT; count++) {
					String selector = String.format(
							"#section__inner-content-body-container > div:nth-child(2) > div:nth-child(%d) > div.box__item-container > div.box__image > a",
							count);

					if (page == 1) {
						selector = String.format(
								"#section__inner-content-body-container > div:nth-child(4) > div:nth-child(%d) > div.box__item-container > div.box__image > a",
								count);
					}

					Elements elem = html.select(selector);
					// > div.box__information > div.box__information-major > div.box__item-title >
					// span > a > span.text__item

					String URL2 = elem.attr("href");
					if (URL2 == null || URL2 == "") {
						System.out.println("url 오류");
						continue;
					}
					Connection conn2 = Jsoup.connect(URL2);
					Document html2 = conn2.get();
					/*
					Elements elem2 = html2.select(
							"#container > div.item-topinfowrap > div.thumb-gallery > div.box__viewer-container > ul.viewer > li > a > img");
					*/
					Elements elem2 = html2.select(
							"#vip-tab_detail > div.box__detail-view.js-toggle-content.on > div.vip-detailarea_seller");
					// #basic_detail_html > center:nth-child(1) > img
					System.out.println(page + " 페이지 " + count + " 번째 이미지");
					System.out.println(elem2);
					

					// System.out.println(page + " 페이지 " + count + " 번째 이미지 : " +
					// elem2.attr("src"));

					// saveImage(elem2.attr("src"), getTitle(URL, page, count));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 상품명 얻기
	public static String getTitle(String url, int page, int count) {
		String title = "";

		String selector = String.format(
				"#section__inner-content-body-container > div:nth-child(2) > div:nth-child(%d) > div.box__item-container > div.box__information > div.box__information-major > div.box__item-title > span > a > span.text__item",
				count);

		if (page == 1) {
			selector = String.format(
					"#section__inner-content-body-container > div:nth-child(4) > div:nth-child(%d) > div.box__item-container > div.box__information > div.box__information-major > div.box__item-title > span > a > span.text__item",
					count);
		}
		Connection conn = Jsoup.connect(url);
		Document html;
		try {
			html = conn.get();
			Elements elem = html.select(selector);
			title = elem.attr("title");

			return title;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	// 이미지 다운로드
	public void getImage() {
		int page = 1; // 페이지 수

		try {

			for (page = 1; page <= MAX_PAGE; page++) {
				String URL = "http://browse.gmarket.co.kr/list?category=200001192&k=24&p=" + Integer.toString(page);
				System.out.println(URL);
				Connection conn = Jsoup.connect(URL);
				Document html = conn.get(); // conn.post();

				for (int count = 1; count <= 2; count++) {
					String selector = String.format(
							"#section__inner-content-body-container > div:nth-child(2) > div:nth-child(%d) > div.box__item-container > div.box__image > a",
							count);

					if (page == 1) {
						selector = String.format(
								"#section__inner-content-body-container > div:nth-child(4) > div:nth-child(%d) > div.box__item-container > div.box__image > a",
								count);
					}

					Elements elem = html.select(selector);
					// > div.box__information > div.box__information-major > div.box__item-title >
					// span > a > span.text__item

					String URL2 = elem.attr("href");
					if (URL2 == null || URL2 == "") {
						System.out.println("url 오류");
						continue;
					}
					Connection conn2 = Jsoup.connect(URL2);
					Document html2 = conn2.get();

					Elements elem2 = html2.select(
							"#container > div.item-topinfowrap > div.thumb-gallery > div.box__viewer-container > ul.viewer > li > a > img");

					System.out.println(page + " 페이지 " + count + " 번째 이미지 : " + elem2.attr("src"));

					saveImage(elem2.attr("src"), Integer.toString(page) + "_test_" + Integer.toString(count));
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void saveImage(String strUrl, String fileName) throws IOException {

		URL url = null;
		InputStream in = null;
		OutputStream out = null;

		if (fileName == null) {
			System.out.println("파일 명 오류");
			return;
		}

		try {

			url = new URL(strUrl);

			in = url.openStream();

			System.out.println("전 파일명: " + fileName);
			fileName = fileName.replaceAll("\\\\", " ");
			fileName = fileName.replaceAll("/", " ");
			System.out.println("후 파일명: " + fileName);
			out = new FileOutputStream("Gmarket images_210702/" + fileName + ".jpg"); // 저장경로

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

		} finally {

			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}

		}
	}

}
