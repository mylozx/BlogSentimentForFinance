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

import com.spinn3r.api.protobuf.ContentApi.Entry;
import com.spinn3r.api.protobuf.ContentApi.PermalinkEntry;


public class Main {
	
	private static final String PATH_PLAIN_TEXT_FOLDER = "plain_text";
	private static final String PATH_XML_FILES_FOLDER = "xml_files";
	
	public static void main(String args[]) {
		String file = "995.protostream";
		try {
			List<Entry> entries = ProtoStreamUtils.read(file);
			
			for(int i = 0; i < entries.size(); ++i) {
				Entry entry = entries.get(i);
				PermalinkEntry pe = entry.getPermalinkEntry();
				String text = "", html = "", title = "";
				// print the title of the entry
				if (pe.hasTitle()) {
					title = pe.getTitle();
					System.out.println("Title: " + title);
				}
				// see how many authors we have and print their names
//					System.out.println("# of Authors: " + pe.getAuthorCount());
//					for (Author author : pe.getAuthorList())
//						System.out.println(author.getName());

				// if there is content then extract and print it
				// Note: the content includes the full html
				if (pe.hasContent()) {
					html = ProtoStreamUtils.contentToString(pe.getContent());
					text = getTextFromHTML(html);
					text = text.replaceAll("\\<.*?\\>", ""); 
					if(!text.equals("")) {
						GeneralUtils.writeToFile(text, PATH_PLAIN_TEXT_FOLDER + "/" + i + ".txt");
						
						title = cleanXML(title);
						text = cleanXML(text);
						String xml = getXMLString(title, text);
						GeneralUtils.writeToFile(xml, PATH_XML_FILES_FOLDER + "/" + i + ".xml");
					}
//					System.out.println(text);
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
	private static String getXMLString(String blogTitle, String blogText) {
		Element root = new Element("root");
		Element entry = new Element("blog");
		root.appendChild(entry);
		
		Element title = new Element("title");
		title.appendChild(blogTitle);
		
		Element content = new Element("content");
		content.appendChild(blogText);
		
		entry.appendChild(title);
		entry.appendChild(content);
		
		return root.toXML();
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
