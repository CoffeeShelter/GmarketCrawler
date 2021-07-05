package crawler;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GmarketCrawler {
	private static final String CRAWLING_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.122 Safari/537.36";
	private static final int CRAWLING_TIMEOUT = 200 * 1000;

	private static final int pagePerCnt = 100;

	// ũ�ѷ� ����
	public void execute(ArrayList<String> merchanDocURLList) {
		String merchanDocListURL = "http://browse.gmarket.co.kr/list?category=200001192&t=a&k=24&p=";
		int totalPageCnt = getTotalMerchandiseCnt(merchanDocListURL);
		System.out.println("�� ������ �� : " + totalPageCnt);

		// ��ǰ �󼼹��� ����
		for (int i = 1; i <= 1; i++) {
			// ��ǰ �󼼹��� ����
			String page_url = merchanDocListURL + i;
			setMerchandiseDocumentList(merchanDocURLList, page_url);
		}
		System.out.println("merchanDocURLList_size = " + merchanDocURLList.size());
	}

	// �� ��ǰ �� ��ȯ
	public int getTotalMerchandiseCnt(String merchanDocListURL) {
		int totalPageCnt = 0;
		Document doc = getDocument(merchanDocListURL, 0);
		String totalCount = doc.select(
				"div.box__component.box__component-service-tab.type--simple > div.box__tab-area > ul.list__tab > li:eq(0) > a > span.text__item-count")
				.text().replace(",", "");
//		System.out.println("��ǰ �� ���� : " + totalCount);
		totalPageCnt = 300;/* (Integer.parseInt(totalCount) / pagePerCnt) + 1; */
		return totalPageCnt;
	}

	// ��ü ��ǰ ���� ����
	private void setMerchandiseDocumentList(ArrayList<String> merchanURLList, String merchanDocListURL) {
		System.out.println(merchanDocListURL + " / ������ ���� ���� ��...");
		try {
			// ��ǰ ����Ʈ ���� ȹ��
			Document merchanListDoc = getDocument(merchanDocListURL, 0);
			// ��ǰ ����Ʈ ����
			Elements merchanList = merchanListDoc
					.select("div#section__inner-content-body-container > div[module-design-id=15] > div");
			for (Element merchandise : merchanList) {
				String productURL = merchandise.select("div.box__item-container > div.box__image > a").attr("href");
				merchanURLList.add(productURL);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			System.out.println("null point ����");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(merchanDocListURL + ": Nutrione ũ�Ѹ� ����");
		}
	}

	// ���� ��ȯ
	private Document getDocument(String url, int cnt) {
		Document doc = null;

		// �õ�ȸ�� �ʰ�
		if (cnt > 10)
			return null;

		try {
			Connection conn = Jsoup.connect(url);
			conn.userAgent(CRAWLING_USER_AGENT);
			conn.timeout(CRAWLING_TIMEOUT);
			conn.header("Accept-Encoding", "gzip, deflate, br");
			conn.header("Accept-Language", "ko-KR,ko;q=0.9");
			doc = conn.execute().parse();
		} catch (HttpStatusException e) {
			if (e.getStatusCode() >= 500) {
				System.out.println("http " + e.getStatusCode() + " ���� �߻� ��õ� url: " + url);
				sleep(500, 1000);
				return getDocument(url, cnt++);
			} else {
				System.out.println("Gmarket ũ�Ѹ� ����: " + e.getStatusCode());
				e.printStackTrace();
			}
		} catch (ConnectException e) {
			System.out.println("���� Ÿ�� �ƿ� ��õ� url: " + url);
			sleep(500, 1000);
			return getDocument(url, cnt++);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return doc;
	}

	protected synchronized Response execute(Connection conn) throws IOException {
		sleep(500, 1000);
		return conn.execute();
	}

	private long sleep(long min, long max) {
		try {
			long millis = ThreadLocalRandom.current().nextLong(min, max + 1l);
			Thread.sleep(millis);
			return millis;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
