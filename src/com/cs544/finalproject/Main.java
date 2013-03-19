package com.cs544.finalproject;

import java.util.List;

import org.jsoup.Jsoup;

import com.spinn3r.api.protobuf.ContentApi.Entry;
import com.spinn3r.api.protobuf.ContentApi.PermalinkEntry;

public class Main {
	
	private static final String PATH_PLAIN_TEXT_FOLDER = "plain_text";
	
	public static void main(String args[]) {
		String file = "995.protostream";
		try {
			List<Entry> entries = ProtoStreamUtils.read(file);
			
			for(int i = 0; i < entries.size(); ++i) {
				Entry entry = entries.get(i);
				PermalinkEntry pe = entry.getPermalinkEntry();
				String text = "", html = "";
				// print the title of the entry
//					if (pe.hasTitle())
//						System.out.println("Title: " + pe.getTitle());
				// see how many authors we have and print their names
//					System.out.println("# of Authors: " + pe.getAuthorCount());
//					for (Author author : pe.getAuthorList())
//						System.out.println(author.getName());

				// if there is content then extract and print it
				// Note: the content includes the full html
				if (pe.hasContent()) {
					html = ProtoStreamUtils.contentToString(pe.getContent());
					text = getTextFromHTML(html);
					System.out.println(text);
				}
				
				if(!text.equals("")) {
					GeneralUtils.writeToFile(text, PATH_PLAIN_TEXT_FOLDER + "/" + i + ".txt");
				}

				// if there is any content where the chrome/boilerplate stuff has been removed, then extract and print it					
//					if (pe.hasContentExtract())
//						System.out.println(ProtoStreamUtils.contentToStringWithoutHTML(pe.getContentExtract()));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// eliminate all HTML tags
	private static String getTextFromHTML(String html) {
		return Jsoup.parse(html).text();
	}
}
