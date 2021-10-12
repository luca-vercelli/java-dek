package org.jd.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jd.core.v1.api.Decompiler;
import org.jd.core.v1.api.Loader;
import org.jd.core.v1.api.Printer;
import org.jd.core.v1.service.StandardDecompiler;
import org.jd.core.v1.service.loader.DirectoryLoader;
import org.jd.core.v1.service.loader.SingleFileLoader;
import org.jd.core.v1.service.loader.ZipLoader;
import org.jd.core.v1.service.printer.PlainTextPrinter;

/**
 * CLI application
 * 
 * @author luca vercelli 2021
 *
 */
public class Application {

	private File source;
	private File destination;
	private boolean override;
	private boolean escapeUnicode;
	private boolean printLineNumbers;
	private int fileType;

	public Application(File source, File destination, boolean override, boolean escapeUnicode,
			boolean printLineNumbers) {
		this.source = source;
		this.destination = destination;
		this.override = override;
		this.escapeUnicode = escapeUnicode;
		this.printLineNumbers = printLineNumbers;
	}

	/**
	 * Main elaborative procedure
	 * 
	 * @return success
	 */
	public boolean run() {
		fileType = prepareFolders(source, destination);

		switch (fileType) {
		case TYPE_FOLDER:
			return decompileSingleFolder();
		case TYPE_ZIP:
			return decompileSingleZip();
		case TYPE_CLASS:
			return decompileSingleClassFile();
		default:
			return false;
		}
	}

	/**
	 * Main elaborative procedure for decompilation of a folder of classes
	 * 
	 * @return success
	 */
	protected boolean decompileSingleFolder() {
		addToClassPath(source); // this allows resolution of inner classes, I hope

		Loader loader = new DirectoryLoader(source);
		boolean success = true;
		for (String internalName : getClassesInFolder(source)) {
			success &= runCommonCode(loader, internalName);
		}
		return success;
	}

	/**
	 * Main elaborative procedure for decompilation of an archive of classes
	 * 
	 * @return success
	 */
	protected boolean decompileSingleZip() {
		addToClassPath(source); // this allows resolution of inner classes, I hope

		ZipLoader loader;
		try {
			loader = new ZipLoader(new FileInputStream(source));
		} catch (IOException e) {
			System.err.println("I/O Exception accessing file: " + source + " : " + e.getMessage());
			return false;
		}
		boolean success = true;
		for (String internalName : getClassesInZip(loader)) {
			success &= runCommonCode(loader, internalName);
		}
		return success;
	}

	/**
	 * Return all non-inner classes inside zip archive
	 * 
	 * @param loader
	 * @return
	 */
	protected List<String> getClassesInZip(ZipLoader loader) {
		List<String> ret = new ArrayList<>();
		for (String fileName : loader.getMap().keySet()) {
			if (fileName.endsWith(".class") && !fileName.contains("$")) {
				ret.add(fileName.substring(0, fileName.length() - 6));
			}
		}
		return ret;
	}

	/**
	 * Main elaborative procedure for decompilation of a single class
	 * 
	 * @return success
	 */
	protected boolean decompileSingleClassFile() {
		SingleFileLoader loader;
		try {
			loader = new SingleFileLoader(source);
		} catch (IOException e) {
			System.err.println("I/O Exception loading file: " + source + " : " + e.getMessage());
			return false;
		}
		return runCommonCode(loader, loader.getInternalTypeName());
	}

	protected boolean runCommonCode(Loader loader, String internalName) {
		Decompiler decompiler = StandardDecompiler.getInstance();
		Printer printer = new PlainTextPrinter(escapeUnicode, printLineNumbers);
		try {
			decompiler.decompile(loader, printer, internalName);
		} catch (Exception e) {
			System.err.println("Exception while decompiling " + internalName + " : " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		String pathAndName = fileType != TYPE_CLASS ? internalName
				: internalName.substring(internalName.lastIndexOf("/"));
		try {
			writeFile(printer.toString(), destination, pathAndName, override);
			System.out.println(internalName);
		} catch (IOException e) {
			System.err.println("Exception while writing " + destination.getPath() + File.separator + internalName
					+ " : " + e.getMessage());
			return false;
		}
		return true;
	}

	public static final int TYPE_CLASS = 0;
	public static final int TYPE_ZIP = 1;
	public static final int TYPE_FOLDER = 2;

	/**
	 * @return type of src file, or -1 on error
	 */
	public int prepareFolders(File src, File dest) {
		if (!source.exists()) {
			System.err.println("File or folder does not exists: " + src);
			return -1;
		}
		if (!source.canRead()) {
			System.err.println("File or folder not readable:" + src);
			return -1;
		}
		int fileType;
		String fileNameLowercase = source.getName().toLowerCase();
		if (source.isDirectory()) {
			fileType = TYPE_FOLDER;
			if (destination == null) {
				destination = source;
			}
		} else if (fileNameLowercase.endsWith(".class")) {
			fileType = TYPE_CLASS;
			if (destination == null) {
				destination = source.getParentFile();
			}
		} else if (fileNameLowercase.endsWith(".jar") || fileNameLowercase.endsWith(".zip")
				|| fileNameLowercase.endsWith(".war")) {
			fileType = TYPE_ZIP;
			if (destination == null) {
				destination = source.getParentFile();
			}
		} else if (fileNameLowercase.endsWith(".ear")) {
			System.err.println("Warning, an EAR unlikely contains classes to decompile:" + src);
			fileType = TYPE_ZIP;
			if (destination == null) {
				destination = source.getParentFile();
			}
		} else {
			System.err.println("Unknown file type:" + src);
			return -1;
		}

		if (!destination.exists()) {
			destination.mkdirs();
		} else if (!destination.isDirectory()) {
			System.err.println("Destination folder is not a directory.");
			return -1;
		}
		if (!destination.canWrite()) {
			System.err.println("Destination folder is not writable.");
			return -1;
		}
		return fileType;
	}

	/**
	 * Add a folder to classpath.
	 * 
	 * @see https://stackoverflow.com/a/7884406/5116356
	 */
	@SuppressWarnings("deprecation")
	public void addToClassPath(File folder) {
		URL u;
		try {
			u = folder.toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e); // can this happen ?!?
		}
		URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class<URLClassLoader> urlClass = URLClassLoader.class;
		try {
			Method method = urlClass.getDeclaredMethod("addURL", new Class[] { URL.class });
			method.setAccessible(true);
			method.invoke(urlClassLoader, new Object[] { u });
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	public List<String> getClassesInFolder(File srcFolder) {
		List<File> list = new ArrayList<>();
		listClasses(srcFolder.listFiles(), list);
		List<String> ls = list.stream() //
				.map(x -> getClassName(srcFolder, x)) //
				.collect(Collectors.toList());
		return ls;
	}

	/**
	 * 
	 * @param srcFolder C:\some\path
	 * @param classFile C:\some\path\package\to\File.class
	 * @return
	 */
	public String getClassName(File srcFolder, File classFile) {
		String s = classFile.getPath();
		final int beginIndex = srcFolder.getPath().length() + 1;
		final int difflen = ".class".length();
		s = s.substring(beginIndex, s.length() - difflen);
		if (s.startsWith("/")) {
			s = s.substring(1);
		}
		return s;
	}

	private void listClasses(File[] files, List<File> result) {
		for (File file : files) {
			if (file.isDirectory()) {
				listClasses(file.listFiles(), result);
			} else if (file.getName().endsWith(".class") && !file.getName().contains("$")) {
				result.add(file);
			}
		}
	}

	/**
	 * Write source code <code>src</code> to file
	 * <code>destFolder/pathAndName</code>.
	 * 
	 * @param src
	 * @param destFolder
	 * @param pathAndName
	 * @param overwrite   if file already exists, overwrite it.
	 * @throws IOException
	 */
	public void writeFile(String src, File destFolder, String pathAndName, boolean overwrite) throws IOException {
		File f = new File(destFolder, pathAndName + ".java");
		if (f.exists() && !overwrite) {
			System.err.println("Skipping existing file " + f.getPath());
			return;
		}
		f.getParentFile().mkdirs();
		try (FileWriter fw = new FileWriter(f)) {
			fw.write(src);
		}
	}
}
