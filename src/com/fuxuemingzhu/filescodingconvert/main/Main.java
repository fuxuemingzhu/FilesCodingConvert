package com.fuxuemingzhu.filescodingconvert.main;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.*;

import java.io.*;
import java.util.Collection;

/**
 * <p>
 * Title: Main
 * </p>
 * <p>
 * Description:��GBK��UTF-8��ʽ�Ĺ��̸�ʽ��ת�����滻�ض���׺�����ļ����滻����ļ�Ŀ¼��Ȼ��Դ�ļ�Ŀ¼�ṹ��ͬ��Ĭ����GBKתUTF-8
 * </p>
 *
 */
public class Main {

    /**
     * fileFolderPath Դ�ļ��洢·��
     * <p/>
     * ��Ŀ¼�������
     */
    public static String sourceFolderPath = "D:\\Eclipse\\Eclipse-Android\\ConvertTest";
    /**
     * outputFolderPath ����ļ��洢·��
     * <p/>
     * ��� ����ļ��洢·����ԴĿ¼·����ͬ����Դ�ļ��ᱻ�滻ΪUTF-8��ʽ
     * <p/>
     * ��Ŀ¼���Բ����ڣ�������Զ�����
     */
    public static String outputFolderPath = "D:\\Eclipse\\Eclipse-Android\\ConvertTest2";
    /**
     * extensions Ҫת�����ļ���׺��
     */
    public static String[] extensions = new String[]{"java", "xml"};

    /**
     * sourceCodingType Դ�ļ������ʽ
     */
    public static String sourceCodingType = "GBK";
    /**
     * outputCodingType Ŀ���ļ������ʽ
     */
    public static String outputCodingType = "UTF-8";

    /**
     * <p>
     * Title: main
     * </p>
     * <p>
     * Description:main��������������
     * </p>
     *
     * @param args
     */
    public static void main(String[] args) {

        try {
            String srcDirPath = sourceFolderPath;
            // תΪUTF-8�����ʽԴ��·��
            String utf8DirPath = outputFolderPath;
            Collection javaGbkFileCol;
            // ��ȡ����java�ļ�
            javaGbkFileCol = listFiles(new File(srcDirPath), extensions, true);
            if (javaGbkFileCol == null) {
                return;
            }
            for (Object javaGbkFile : javaGbkFileCol) {
                // UTF8��ʽ�ļ�·��
                String utf8FilePath = utf8DirPath + ((File) javaGbkFile).getAbsolutePath().substring(srcDirPath.length());
                // ʹ��GBK��ȡ���ݣ�Ȼ����UTF-8д������
                writeLines(new File(utf8FilePath), readLines(((File) javaGbkFile), sourceCodingType), outputCodingType);
                System.out.println("files convert success.");
            }
        } catch (Exception e) {
            System.out.println("error");
            e.printStackTrace();
        }
    }

    /**
     * ����һ��Ŀ¼������϶�Ӧ��չ�����ļ��ļ���listFiles(File directory, String[]extensions, boolean recursive)
     *
     * @param directory
     * @param extensions
     * @param recursive
     * @return
     */
    public static Collection<File> listFiles(File directory, String[] extensions, boolean recursive) {

        IOFileFilter filter;

        if (directory == null || !directory.isDirectory()) {
            System.out.println("directory is not right.");
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
     * ���ļ��е��������еĿ�����һ����Ӧ�����list<String>��ȥ
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
     * �Ѽ����е����ݸ��ݶ�Ӧ�ַ�������б���������뵽�ļ���
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

        } finally {

            IOUtils.closeQuietly(out);

        }

    }

    /**
     * ����ָ�����ļ���ȡһ���µ��ļ��������openOutputStream (File file)
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
     * ����ָ�����ļ���ȡһ���µ��ļ���������openInputStream(File file)
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
     * ����һ��Ŀ¼������϶�Ӧ��չ�����ļ��ļ���
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


        //Setup effective file filter

        IOFileFilter effFileFilter = FileFilterUtils.and(fileFilter,

                FileFilterUtils.notFileFilter(DirectoryFileFilter.INSTANCE));


        //Setup effective directory filter

        IOFileFilter effDirFilter;

        if (dirFilter == null) {

            effDirFilter = FalseFileFilter.INSTANCE;

        } else {

            effDirFilter = FileFilterUtils.and(dirFilter,

                    DirectoryFileFilter.INSTANCE);

        }


        //Find files

        Collection<File> files = new java.util.LinkedList<File>();

        innerListFiles(files, directory,

                FileFilterUtils.or(effFileFilter, effDirFilter));

        return files;

    }

    /**
     * ����һ�����˹����ȡһ��Ŀ¼�µ��ļ�
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
     * ��ָ�����ַ��������ɺ�׺����ʽ�ַ�������
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
