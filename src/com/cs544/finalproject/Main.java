/*
 * This class is part of BlogSentimentForFinance(BSFF) project
 * Function: convert protostream files to XML files
 * Author: Zinan "Mylo" Xing
 * Date: 3/18/2013
 * 
 */

package com.cs544.finalproject;

import java.util.List;

import nu.xom.Element;

import org.jsoup.Jsoup;

import com.spinn3r.api.protobuf.ContentApi.Author;
import com.spinn3r.api.protobuf.ContentApi.Entry;
import com.spinn3r.api.protobuf.ContentApi.PermalinkEntry;


public class Main {
	
	private static final String PATH_PLAIN_TEXT_FOLDER = "plain_text";
	private static final String PATH_XML_FILES_FOLDER = "xml_files";
	
	public static void main(String args[]) {
		String file = "995.protostream";
		try {
			List<Entry> entries = ProtoStreamUtils.read(file);
			int titleCnt = 0;
			int fileCnt = 0;
			String currentXMLString = "";
			
			fileCnt = getFileCount(file);
			
			// convert from HTML to XML for each blog in protostream file
			for(int i = 0; i < entries.size(); ++i) {
				Entry entry = entries.get(i);
				PermalinkEntry pe = entry.getPermalinkEntry();
				String text = "", html = "", title = "";
				// print the title of the entry
				if (pe.hasTitle()) {
					title = pe.getTitle();
					System.out.println(titleCnt + " Title: " + title);	// output title to console to keep track
					++titleCnt;
				}

				// if there is content then extract and print it
				// Note: the content includes the full html
				if (pe.hasContent()) {
					html = ProtoStreamUtils.contentToString(pe.getContent());
					text = getTextFromHTML(html);
					text = text.replaceAll("\\<.*?\\>", ""); 	// eliminate HTML tags
					
					String authors = getAuthors(pe);
					
					if(!text.equals("")) {
						// write separate files (blog posts) to plain text
						GeneralUtils.writeToFile(text, PATH_PLAIN_TEXT_FOLDER + "/" + i + ".txt");
						
						title = cleanXML(title);
						authors = cleanXML(authors);
						text = cleanXML(text);
						String xml = getXMLString(title, authors, text);
						currentXMLString += xml;
					}
//					System.out.println(text);
				}

				// if there is any content where the chrome/boilerplate stuff has been removed, then extract and print it					
//					if (pe.hasContentExtract())
//						System.out.println(ProtoStreamUtils.contentToStringWithoutHTML(pe.getContentExtract()));
			}
			
			currentXMLString = "<root>" + currentXMLString + "</root>";
			GeneralUtils.writeToFile(currentXMLString, PATH_XML_FILES_FOLDER + "/" + fileCnt + ".xml");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * get author names given pe
	 */
	private static String getAuthors(PermalinkEntry pe) {
		String authors = "";
		// if only 1 author, then no ";"
		if(pe.getAuthorCount() == 1) {		// get NO. of authors
			for (Author author : pe.getAuthorList()) {
				authors += author.getName();
			}
		} else {
			//if multiple author, separated by ";"
			for (Author author : pe.getAuthorList()) {
				authors += author.getName() + ";";
			}
		}
		return authors;
	}
	
	/**
	 * get count of protostream file given filename
	 */
	private static int getFileCount(String fileName) {
		String s = fileName.substring(0, fileName.indexOf(".protostream"));
		return Integer.parseInt(s);
	}
	
	/**
	 * clean up text so that it can be used for XML creation
	 * @param s
	 * @return
	 */
	private static String cleanXML(String s) {
		String str = removeInvalidXMLCharacters(s);
		str = removeXMLMarkups(str);
		return str;
	}
	
	/**
	 * remove invalid XML chars
	 * @param s
	 * @return
	 */
	public static String removeInvalidXMLCharacters(String s)
    {
        StringBuilder out = new StringBuilder();

        int codePoint;
        int i = 0;

        while (i < s.length())
        {
            // This is the unicode code of the character.
            codePoint = s.codePointAt(i);
            if ((codePoint == 0x9) ||
                    (codePoint == 0xA) ||
                    (codePoint == 0xD) ||
                    ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                    ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
                    ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)))
            {
                out.append(Character.toChars(codePoint));
            }
            i += Character.charCount(codePoint);
        }
        return out.toString();
    }

    /**
     * Remove all characters that are valid XML markups.
     * @param s
     * @return
     */
    public static String removeXMLMarkups(String s)
    {
        StringBuffer out = new StringBuffer();
        char[] allCharacters = s.toCharArray();
        for (char c : allCharacters)
        {
            if ((c == '\'') || (c == '<') || (c == '>') || (c == '&') || (c == '\"'))
            {
                continue;
            }
            else
            {
                out.append(c);
            }
        }
        return out.toString();
    }
	
    /**
     * get XML string
     * 
     * @param blogTitle
     * @param blogText
     * @return
     */
	private static String getXMLString(String blogTitle, String blogAuthors, String blogText) {
//		Element root = new Element("root");
		Element entry = new Element("blog");
//		root.appendChild(entry);
		
		Element title = new Element("title");
		title.appendChild(blogTitle);
//		System.out.println(title.toXML());
		
		Element authors = new Element("authors");
		authors.appendChild(blogAuthors);
//		System.out.println(authors.toXML());
		
		Element content = new Element("content");
		content.appendChild(blogText);
//		System.out.println(content.toXML());
		
		entry.appendChild(title);
		entry.appendChild(authors);
		entry.appendChild(content);
		
		return entry.toXML();
	}
	
	/**
	 * eliminate all HTML tags
	 * 
	 * @param html
	 * @return
	 */
	private static String getTextFromHTML(String html) {
		return Jsoup.parse(html).text();
	}
}
