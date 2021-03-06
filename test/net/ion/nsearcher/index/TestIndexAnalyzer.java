package net.ion.nsearcher.index;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.nsearcher.common.SearchConstant;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.search.Searcher;

import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;

public class TestIndexAnalyzer extends TestCase{

	public void testDefaultIndexerAnalyzer() throws Exception {
		Central c = CentralConfig.newRam().indexConfigBuilder().indexAnalyzer(new StandardAnalyzer(SearchConstant.LuceneVersion)).build() ;
		Indexer indexer = c.newIndexer() ;
		
		assertEquals(StandardAnalyzer.class, c.indexConfig().indexAnalyzer().getClass()) ;
		assertEquals(StandardAnalyzer.class, indexer.analyzer().getClass()) ;

		Searcher searhcer = c.newSearcher() ;
		assertEquals(StandardAnalyzer.class, c.searchConfig().queryAnalyzer().getClass()) ;
		assertEquals(StandardAnalyzer.class, searhcer.queryAnalyzer().getClass()) ;
	}
	
	
	public void testAfterChangeIndexAnalyzer() throws Exception {
		Central c = CentralConfig.newRam().indexConfigBuilder().indexAnalyzer(new StandardAnalyzer(SearchConstant.LuceneVersion)).build() ;
		Indexer indexer = c.newIndexer() ;
		
		c.indexConfig().indexAnalyzer(new CJKAnalyzer(SearchConstant.LuceneVersion)) ;
		assertEquals(CJKAnalyzer.class, c.indexConfig().indexAnalyzer().getClass()) ;
		assertEquals(CJKAnalyzer.class, indexer.analyzer().getClass()) ;
		
		indexer.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				WriteDocument doc = isession.newDocument("bleujin") ;
				doc.text("flag", "태극기가") ;
				isession.updateDocument(doc) ;
				return null;
			}
		}) ;

		assertEquals(1, c.newSearcher().search("기가").size()) ; 
		c.destroySelf();
	}
	
	public void testAfterChangeQueryAnalyzer() throws Exception {
		Central c = CentralConfig.newRam().indexConfigBuilder().indexAnalyzer(new CJKAnalyzer(SearchConstant.LuceneVersion)).parent().searchConfigBuilder()
				.queryAnalyzer(new CJKAnalyzer(SearchConstant.LuceneVersion)).build() ;

		Searcher searcher = c.newSearcher() ;
		assertEquals(CJKAnalyzer.class, searcher.queryAnalyzer().getClass()) ; 

		Indexer indexer = c.newIndexer() ;
		indexer.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				WriteDocument doc = isession.newDocument("bleujin") ;
				doc.text("flag", "태극기가") ;
				isession.updateDocument(doc) ;
				return null;
			}
		}) ;


		c.searchConfig().queryAnalyzer(new CJKAnalyzer(SearchConstant.LuceneVersion)) ;
		Debug.line(searcher.queryAnalyzer()) ;
		assertEquals(1, searcher.search("기가").size()) ; 

		c.destroySelf();
	}
	
	
	
}
