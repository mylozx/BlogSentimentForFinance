package com.cs544.finalproject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.spinn3r.api.EntryDecoderFactory;
import com.spinn3r.api.protobuf.ContentApi;
import com.spinn3r.api.protobuf.ContentApi.Content;
import com.spinn3r.api.protobuf.ContentApi.Entry;
import com.spinn3r.api.util.CompressedBLOB;
import com.spinn3r.api.util.Decoder;
import com.spinn3r.api.util.EncodingException;
import com.spinn3r.api.util.ProtoStreamEncoder;
/**
 * ProtoStreamUtils provides easy functionality to read and write files in the protostream format
 * @author niels
 *
 */
public class ProtoStreamUtils
{
	/**
	 * Writes the entries to a file. The file uses the ProtoStream format
	 * @param entries The entries to be written
	 * @param output The file to write the entries to.
	 * @throws IOException
	 */
	public static void write(List<Entry> entries, File output) throws IOException
	{
		new File(output.getParent()).mkdirs();		
		FileOutputStream outstream = new FileOutputStream(output);
		// NOTE: there seems to be a bug in ProtoStream Encoder, where you must change
		// the line builder.setDefaultEntryType( entryType ) to builder.setDefaultEntryType("contentAPI.Entry")
		// otherwise things break when reading the file again.
		ProtoStreamEncoder<Entry> encoder = ProtoStreamEncoder.newStreamEncoder(outstream, Entry.class, null);
		encoder.writeAll(entries);
		encoder.closeStream();
	}
	/**
	 * Writes the entries to a file. The file uses the ProtoStream format
	 * @param entries The entries to be written
	 * @param file The file to write the entries to.
	 * @throws IOException
	 */
	public static void write(List<Entry> entries, String file) throws IOException
	{
		write(entries, new File(file));
	}
	/**
	 * Reads in a file in ProtoStream format and returns its Entry(s).
	 * @param inFile The file to read.
	 * @return A list of Entry(s).
	 * @throws FileNotFoundException 
	 * @throws IOException
	 */
	public static List<Entry> read(File inFile) throws FileNotFoundException
	{
		EntryDecoderFactory factory = EntryDecoderFactory.newFactory();
		Decoder<ContentApi.Entry> decoder = factory.get(inFile);

		List<Entry> entries = new ArrayList<Entry>();
		try
		{
			for (Entry entry = decoder.read() ; entry != null ; entry = decoder.read() )
			{
				entries.add(entry);			
			}
		}
		catch (Exception e)
		{			
			e.printStackTrace();
		}
		return entries;
	}
	/**
	 * Reads in a file in ProtoStream format and returns its Entry(s).
	 * @param inFile The file to read.
	 * @return A list of Entry(s).
	 * @throws IOException
	 */
	public static List<Entry> read(String file) throws IOException
	{
		return read(new File(file));
	}
	/**
	 * Gets the textual representation of the content.
	 * @param content The content.
	 * @return The text string of the content.
	 * @throws EncodingException
	 * @throws IOException 
	 * @throws IOException
	 */
	public static String contentToString(Content content) throws EncodingException, IOException
	{		
		return new CompressedBLOB(content.getData().toByteArray()).decompress();
	}
	
	/**
	 * Gets the textual representation of the content. This function uses the regular expression
	 * <[^>]*> to remove HTML tags.
	 * @param content The content.
	 * @return The text string of the content without HTML tags.
	 * @throws EncodingException
	 * @throws IOException 
	 * @throws IOException
	 */
	public static String contentToStringWithoutHTML(Content content) throws EncodingException, IOException
	{		
		CompressedBLOB cb = new CompressedBLOB(content.getData().toByteArray());
		String s = cb.decompress();
		if (s == null)
			return "";
		return s.replaceAll("<[^>]*>","");
	}
}
