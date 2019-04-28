package com.wk.main;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * <p>
 * Title: Main
 * </p>
 * <p>
 * Description:将GBK和UTF-8格式的工程格式互转，可替换特定后缀名的文件，替换后的文件目录仍然与源文件目录结构相同，默认是GBK转UTF-8
 * 
 * 
 * version 20190522
 * </p>
 *
 */
public class FilesCodingConvert {

	/**
	 * fileFolderPath 源文件存储路径
	 * <p/>
	 * 此目录必须存在
	 */
	public static String sourceFolderPath = File.separator;
	/**
	 * outputFolderPath 输出文件存储路径
	 * <p/>
	 * 如果 输出文件存储路径与源目录路径相同，则源文件会被替换为UTF-8格式
	 * <p/>
	 * 此目录可以不存在，程序会自动创建
	 */
	public static String outputFolderPath = File.separator;
	/**
	 * extensions 要转换的文件后缀名
	 */
	public static String[] extensions = new String[] { "java", "xml" };

	/**
	 * sourceCodingType 源文件编码格式
	 */
	public static String sourceCodingType = "GBK";
	/**
	 * outputCodingType 目标文件编码格式
	 */
	public static String outputCodingType = "UTF-8";

	/**
	 * <p>
	 * Title: main
	 * </p>
	 * <p>
	 * Description:main方法，程序的入口
	 * </p>
	 *
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			 MyLogger.log.info("Version 20190522 ! Thx for use and please contact wangkun.it@qq.com if you have any problem!");
			File f = new File(System.getProperty("user.dir") + "/config.xml");
			SAXReader reader = new SAXReader();
			Document doc = reader.read(f);
			Element root = doc.getRootElement();
			sourceFolderPath = root.elementText("sourceFolderPath");
			outputFolderPath = root.elementText("outputFolderPath");
			extensions = root.elementText("extensions").split("\\ ");
			sourceCodingType = root.elementText("sourceCodingType");
			outputCodingType = root.elementText("outputCodingType");

			String srcDirPath = sourceFolderPath;
			// 转为UTF-8编码格式源码路径
			String utf8DirPath = outputFolderPath;
			if (utf8DirPath.equals("") || utf8DirPath == null) {
				// add default output place
				utf8DirPath = sourceFolderPath;
			}

			// 获取所有java文件
			Collection<File> javaGbkFileCol = listFiles(new File(srcDirPath), extensions, true);
			if (javaGbkFileCol == null) {
				return;
			}

			BytesEncodingDetect s = new BytesEncodingDetect();

			boolean autoDetect = sourceCodingType.equalsIgnoreCase("auto");

			for (File javaGbkFile : javaGbkFileCol) {
				// UTF8格式文件路径
				String utf8FilePath = utf8DirPath
						+ ((File) javaGbkFile).getAbsolutePath().substring(srcDirPath.length());

				if (autoDetect) {
					sourceCodingType = BytesEncodingDetect.javaname[s.detectEncoding(javaGbkFile)];
				}
				MyLogger.log.info("The file " + javaGbkFile.getName() + " encoding:" + sourceCodingType);
				// 使用GBK读取数据，然后用UTF-8写入数据
				if (sourceCodingType.equalsIgnoreCase(outputCodingType)) {
					MyLogger.log.info("The file needn't convert!");
					continue;// 原编码不变
				}
				writeLines(new File(utf8FilePath), readLines(((File) javaGbkFile), sourceCodingType), outputCodingType);
				MyLogger.log.info("Convert " + javaGbkFile + " from " + sourceCodingType + " to " + outputCodingType
						+ " convert success.");

			}
			MyLogger.log.info("Finished!");
		} catch (Exception e) {
			MyLogger.log.error("error");
			e.printStackTrace();
		}
	}

	/**
	 * 查找一个目录下面符合对应扩展名的文件的集合listFiles(File directory, String[]extensions,
	 * boolean recursive)
	 *
	 * @param directory
	 * @param extensions
	 * @param recursive
	 * @return
	 */
	public static Collection<File> listFiles(File directory, String[] extensions, boolean recursive) {

		IOFileFilter filter;

		if (directory == null || !directory.isDirectory()) {
			MyLogger.log.warn("directory is not right.");
			return null;
		}
		if (extensions == null) {

			filter = TrueFileFilter.INSTANCE;

		} else {

			String[] suffixes = toSuffixes(extensions);

			filter = new SuffixFileFilter(suffixes);

		}

		return listFiles(directory, filter,

				(recursive ? TrueFileFilter.INSTANCE : FalseFileFilter.INSTANCE));

	}

	/**
	 * 把文件中的内容逐行的拷贝到一个对应编码的list<String>中去
	 *
	 * @param file
	 * @param encoding
	 * @return
	 * @throws IOException
	 */
	public static String readLines(File file, String encoding) throws IOException {

		InputStream in = null;

		try {

			in = openInputStream(file);

			return IOUtils.toString(in, encoding);

		} finally {

			IOUtils.closeQuietly(in);

		}

	}

	/**
	 * 把集合中的内容根据对应字符编码和行编码逐项插入到文件中
	 *
	 * @param file
	 * @param data
	 * @param encoding
	 * @throws IOException
	 */
	public static void writeLines(File file, String data, String encoding) throws IOException {

		OutputStream out = null;

		try {

			out = openOutputStream(file);

			IOUtils.write(data, out, encoding);
			// IOUtils.write(data, out);

		} finally {

			IOUtils.closeQuietly(out);

		}

	}

	/**
	 * 根据指定的文件获取一个新的文件输出流：openOutputStream (File file)
	 *
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static FileOutputStream openOutputStream(File file) throws IOException {

		if (file.exists()) {

			if (file.isDirectory()) {

				throw new IOException("File'" + file + "' exists but is a directory");

			}

			if (file.canWrite() == false) {

				throw new IOException("File '" + file + "' cannot be written to");

			}

		} else {

			File parent = file.getParentFile();

			if (parent != null && parent.exists() == false) {

				if (parent.mkdirs() == false) {

					throw new IOException("File '" + file + "' could not be created");

				}

			}

		}

		return new FileOutputStream(file);

	}

	/**
	 * 根据指定的文件获取一个新的文件输入流：openInputStream(File file)
	 *
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static FileInputStream openInputStream(File file) throws IOException {

		if (file.exists()) {

			if (file.isDirectory()) {

				throw new IOException("File '" + file + "' exists but is adirectory");

			}

			if (file.canRead() == false) {

				throw new IOException("File '" + file + "' cannot be read");

			}

		} else {

			throw new FileNotFoundException("File '" + file + "' does notexist");

		}

		return new FileInputStream(file);

	}

	/**
	 * 查找一个目录下面符合对应扩展名的文件的集合
	 *
	 * @param directory
	 * @param fileFilter
	 * @param dirFilter
	 * @return
	 */
	public static Collection<File> listFiles(

			File directory, IOFileFilter fileFilter, IOFileFilter dirFilter) {

		if (!directory.isDirectory()) {

			throw new IllegalArgumentException(

					"Parameter'directory' is not a directory");

		}

		if (fileFilter == null) {

			throw new NullPointerException("Parameter 'fileFilter' is null");

		}

		// Setup effective file filter

		IOFileFilter effFileFilter = FileFilterUtils.and(fileFilter,

				FileFilterUtils.notFileFilter(DirectoryFileFilter.INSTANCE));

		// Setup effective directory filter

		IOFileFilter effDirFilter;

		if (dirFilter == null) {

			effDirFilter = FalseFileFilter.INSTANCE;

		} else {

			effDirFilter = FileFilterUtils.and(dirFilter,

					DirectoryFileFilter.INSTANCE);

		}

		// Find files

		Collection<File> files = new java.util.LinkedList<File>();

		innerListFiles(files, directory,

				FileFilterUtils.or(effFileFilter, effDirFilter));

		return files;

	}

	/**
	 * 根据一个过滤规则获取一个目录下的文件
	 *
	 * @param files
	 * @param directory
	 * @param filter
	 */
	private static void innerListFiles(Collection<File> files, File directory,

			IOFileFilter filter) {

		File[] found = directory.listFiles((FileFilter) filter);

		if (found != null) {

			for (File file : found) {

				if (file.isDirectory()) {

					innerListFiles(files, file, filter);

				} else {

					files.add(file);

				}

			}

		}

	}

	/**
	 * 把指定的字符串数组变成后缀名格式字符串数组
	 *
	 * @param extensions
	 * @return
	 */
	public static String[] toSuffixes(String[] extensions) {

		String[] suffixes = new String[extensions.length];

		for (int i = 0; i < extensions.length; i++) {

			suffixes[i] = "." + extensions[i];

		}

		return suffixes;

	}
}
