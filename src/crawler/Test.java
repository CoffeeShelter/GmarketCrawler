package crawler;

import crawler.gmarket.CrawlerManagement;

public class Test {

	public static void main(String[] args) {
		String title = "<8:2 비타민 ., 안녕>";
		CrawlerManagement c = new CrawlerManagement();
		System.out.println(c.reNameTitle(title));
	}

}
