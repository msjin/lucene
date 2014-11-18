package com.index;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Searcher {
	
	private IndexSearcher searcher;
	
	public static void main(String[] args) throws IOException, ParseException {
		long start = System.currentTimeMillis();
		//指定索引目录
		String indexDir = "E:\\work\\lucene_index";
		//搜索关键字
		String keyWord = "lucene";
		
		Searcher s = new Searcher(indexDir);
		s.search(indexDir, keyWord);
		
		long end = System.currentTimeMillis();
		System.out.println("searching " + (end - start) + " milliseconds");
	}
	
	public Searcher(String indexDir) throws IOException{
		File file = new File(indexDir);
		if(!file.exists()){
			System.out.println("indexDir not exist"); 
        	return; 
		}
		IndexReader reader = DirectoryReader.open(FSDirectory.open(file));
		searcher = new IndexSearcher(reader);
	}
	
	public void search(String indexDir, String keyWord) throws IOException, ParseException{
		
		Analyzer analyzer = new StandardAnalyzer(Version.LATEST);
		QueryParser parser = new QueryParser(Version.LATEST, "contents",analyzer);
		Query query = parser.parse(keyWord);
		
//		QueryBuilder qBuilder = new QueryBuilder(analyzer);
//		Query query = qBuilder.createPhraseQuery("contents", keyWord);
//		TermQuery q = new TermQuery(new Term("contents", keyWord));
		TopDocs docs = searcher.search(query, 10);
		
		for(ScoreDoc doc:docs.scoreDocs){
			Document document = searcher.doc(doc.doc);
			List<IndexableField> fields = document.getFields();
			for(IndexableField f:fields){
				System.out.println(f.name()+"\t"+f.stringValue());
			}
//			System.out.println(document.get("path")+"\t"+document.get("title")+"\t"+document.get("contents"));
		}
	}
}
