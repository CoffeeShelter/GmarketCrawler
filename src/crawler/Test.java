package crawler;

import java.util.Vector;

import crawler.gmarket.CrawlerManagement;

public class Test {

	public static void main(String[] args) {
		Vector<String> paths = new Vector<>();
		CrawlerManagement c = new CrawlerManagement();
		
		paths = c.getDetailImage("http://item.gmarket.co.kr/Item?goodscode=1816071184");
		for(String path : paths) {
			System.out.println(path);
		}
	}

}
