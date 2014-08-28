package net.ion.nsearcher.central;

import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import net.ion.framework.util.WithinThreadExecutor;
import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;


public class TestIndexConfig extends ISTestCase{

	
	public void testSetIndexConfig() throws Exception {
		CentralConfig config = CentralConfig.newRam().indexConfigBuilder().setMaxBufferedDocs(100).parent();
		
		Central cen = config.build() ;
		cen.newIndexer().index(new IndexJob<Void>() {
			public Void handle(IndexSession session) throws Exception {
				assertEquals(100, session.indexWriterConfig().getMaxBufferedDocs()) ;
				return null;
			}
		}) ;
		
		cen.close(); 
	}
	
	public void testDefaultIndexConfig() throws Exception {
		Central cen = CentralConfig.newRam().build() ;
		assertEquals(StandardAnalyzer.class, cen.indexConfig().indexAnalyzer().getClass()) ;
		assertEquals(true, cen.indexConfig().indexExecutor() != null) ; // single
		
		cen.close(); 
	}

}
