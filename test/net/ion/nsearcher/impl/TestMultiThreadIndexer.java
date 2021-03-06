package net.ion.nsearcher.impl;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.RandomUtil;
import net.ion.nsearcher.common.MyField;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;

public class TestMultiThreadIndexer extends TestCase {

	public void testIndexer() throws Exception {
		Central c = CentralConfig.newRam().build() ;

		ExecutorService es = Executors.newCachedThreadPool() ;
		List<Future> futures = ListUtil.newList(); 
		for (int i : ListUtil.rangeNum(10)) {
			futures.add(es.submit(new Creater(c, "n" + i + "m"))) ;
		}

		for (Future future : futures) {
			future.get() ;
		}
		
		for (int i = 0; i < 10; i++) {
			Debug.line(c.newSearcher().search("").size()) ;
			Thread.sleep(500) ;
		}
	}
}



class Creater implements Runnable {
	
	private Central central ;
	Creater(Central central, String name){
		this.central = central ; 
	}
	
	public void run(){
	
		int index = 0 ;
		int loop = 1000 ;
		
		while(loop-- > 0){
			try {
				Thread.sleep(RandomUtil.nextRandomInt(50, 100)) ;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			Indexer di = central.newIndexer() ;
			Integer integer = di.index(new IndexJob<Integer>(){
				public Integer handle(IndexSession isession) throws IOException {
					for (int i : ListUtil.rangeNum(10)) {
						WriteDocument doc = isession.newDocument() ;
						doc.add(MyField.number("index", i)) ;
						isession.insertDocument(doc) ;
					}
					return 10 ;
				}
			}) ;

			System.out.print('.') ;
		}
	}
}




