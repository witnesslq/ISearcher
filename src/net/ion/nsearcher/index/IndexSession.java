package net.ion.nsearcher.index;

import java.io.IOException;

import net.ion.nsearcher.common.MyDocument;
import net.ion.nsearcher.common.SearchConstant;
import net.ion.nsearcher.common.MyDocument.Action;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.search.SingleSearcher;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;

public class IndexSession {

	private final CentralConfig config ;
	private final Directory dir ;
	private final Analyzer analyzer ;
	private final SingleSearcher searcher;
	
	private IndexWriter writer ;
	private final IndexWriterConfig wconfig ;
	IndexSession(CentralConfig config, Directory dir, SingleSearcher searcher, Analyzer analyzer) {
		this.config = config ;
		this.dir = dir ;
		this.searcher = searcher ;
		this.analyzer = analyzer ;
		this.wconfig = config.writerConfig().buildIndexWriter(analyzer);
	}

	public static IndexSession create(CentralConfig config, Directory dir, SingleSearcher searcher, Analyzer analyzer) {
		return new IndexSession(config, dir, searcher, analyzer);
	}

	public void begin(String owner) throws IOException {
		this.writer = new IndexWriter(dir, wconfig);
	}

	public void release() {
		
	}
	
	

	public IndexReader reader() throws IOException{
		return searcher.indexReader() ;
	}
	
	public Action insertDocument(MyDocument doc) throws IOException {
		writer.addDocument(doc.toLuceneDoc()) ;
		return Action.Insert ;
	}
	
	public Action updateDocument(MyDocument doc) throws IOException{
		final Document idoc = doc.toLuceneDoc();
		writer.updateDocument(new Term(SearchConstant.ISKey, idoc.get(SearchConstant.ISKey)), idoc) ;
		return Action.Update ;
	}

	public IndexSession end() throws IOException{
		try {
			commit() ;
		} finally {
			writer.close() ;
			release() ;
		}
		return this ;
	}
	
	private void commit() throws CorruptIndexException, IOException{
		if (alreadyCancelled) return ;
		writer.commit() ;
	}
	

	private boolean alreadyCancelled = false ;
	public void cancel() throws IOException {
		this.alreadyCancelled = true ;
		writer.rollback() ;
	}
	
	public IndexSession rollback() throws IOException {
		if (alreadyCancelled) return this ;
		this.alreadyCancelled = true ;
		writer.rollback() ;
		return this ;
	}

	public Action deleteDocument(MyDocument doc) throws IOException {
		writer.deleteDocuments(new Term(SearchConstant.ISKey)) ;
		return Action.Delete;
	}
	
	public Action deleteAll() throws IOException {
		writer.deleteAll();
		return Action.DeleteAll;
	}

	public Action deleteTerm(Term term) throws IOException {
		writer.deleteDocuments(term);
		return Action.DeleteAll;
	}

	public Action deleteQuery(Query query) throws IOException {
		writer.deleteDocuments(query);
		return Action.DeleteAll;
	}

//	public Map loadHashMap() {
//		Map<String, HashBean> map = new HashMap<String, HashBean>();
//
//		IndexReader reader = central.getIndexReader() ;
//
//		for (int i = 0, last = reader.maxDoc(); i < last; i++) {
//			if (reader.isDeleted(i))
//				continue;
//			Document doc = reader.document(i);
//			HashBean bean = new HashBean(getIdValue(doc), getBodyValue(doc));
//			map.put(getIdValue(doc), bean);
//		}
//
//		return Collections.unmodifiableMap(map);
//	}


	public String getIdValue(Document doc) {
		return doc.get(SearchConstant.ISKey);
	}

	public String getBodyValue(Document doc) {
		return doc.get(SearchConstant.ISBody);
	}

	public IndexWriterConfig getIndexWriterConfig() {
		return wconfig ;
	}



}