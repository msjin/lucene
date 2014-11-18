package com.index;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Indexer {
	
	private IndexWriter indexWriter;
	
	public static void main(String[] args) throws IOException {
		long start = System.currentTimeMillis();
		//指定一个目录创建索引
		String indexDir = "E:\\work\\lucene_index";
		//需要建立索引的文件
		String dataDir = "E:/work/eclipseProject/lucene_demo/resources/README.txt";
		Indexer indexer = new Indexer(indexDir);
		int numIndexed = 0;
		try{
			numIndexed = indexer.index(dataDir, new TextFilesFilter());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			indexer.close();
		}
		long end = System.currentTimeMillis();
		System.out.println("indexing " + numIndexed + " files took" + 
		(end - start) + " milliseconds");
	}
	
	private static class TextFilesFilter implements FileFilter{
		public boolean accept(File path){
			return path.getName().toLowerCase().endsWith(".txt");
		}
	}
	public Indexer(String indexDir) throws IOException{
		Directory dir = FSDirectory.open(new File(indexDir));
		
		Analyzer analyzer = new StandardAnalyzer(Version.LATEST);
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
		indexWriter = new IndexWriter(dir,config);
	}
	
	public void close() throws IOException{
		indexWriter.close();
	}

	public int index(String dataDir, FileFilter filter) throws IOException{
		File ff =  new File(dataDir);
		if(ff.exists()){
			System.out.println("exists");
		}
//		File[] files = ff.listFiles();
//		for(File f:files){
//			if(!f.isDirectory() && !f.exists() && f.isHidden() &&
//					f.canRead() && (filter == null || filter.accept(f))){
//				indexFile(f);
//			}
//		}
		indexFile(ff);
		return indexWriter.numDocs();
	}
	
	private void indexFile(File f) throws IOException{
		Document doc = getDocument(f);
		if(indexWriter.getConfig().getOpenMode() == OpenMode.CREATE){
			indexWriter.addDocument(doc);
		}else{
			indexWriter.updateDocument(new Term("path",f.getPath()), doc);
		}
	}
	
	protected Document getDocument(File f) throws FileNotFoundException{
		Document doc = new Document();
		doc.add(new StringField("path", f.getPath(), Field.Store.YES));
		doc.add(new LongField("modified", f.lastModified(), Field.Store.NO));
		doc.add(new TextField("contents", new FileReader(f)));
		return doc;
	}
}
