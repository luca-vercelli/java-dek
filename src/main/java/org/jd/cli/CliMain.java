package org.jd.cli;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * CLI application
 * 
 * @author luca vercelli 2021
 *
 */
public class CliMain {

	public static final String VERSION = "1.0 2021";

	public static void usage() {
		PrintStream o = System.out;

		o.println("Usage:");
		o.println("  java org.jd.core.v1.cli [-h |-v| [options] <paths>]");
		o.println("Decompile .class files. Allowed paths include: a single .class file,");
		o.println("a single .jar/.war/.zip file, a root classes folder.");
		o.println();
		o.println("Actions:");
		o.println("    -h | --help                  print this help message then exit");
		o.println("    -v | --version               print version then exit");
		o.println();
		o.println("Options:");
		o.println("    -d | --destination <path>    java files folder (default classes folder)");
		o.println("    --override                   override existing java files");
		o.println("    --escape-unicode             escape unidoce characters");
		o.println("    --no-line-numbers            omit line numbers");
	}

	public static void version() {
		System.out.println(VERSION);
	}

	public static void main(String[] args) {

		if (args.length == 0) {
			System.err.println("Missing arguments.");
			usage();
			System.exit(1);
		}

		if ("-h".equals(args[0]) || "--help".equals(args[0])) {
			usage();
			return;
		}
		if ("-v".equals(args[0]) || "--version".equals(args[0])) {
			version();
			return;
		}

		int i = 0; // current CLI argument
		List<File> sources = new ArrayList<>();
		File destFolder = null;
		boolean override = false;
		boolean escapeUnicode = false;
		boolean printLineNumbers = true;

		while (i < args.length) {
			if ("-d".equals(args[i]) || "--destination".equals(args[i])) {
				String folder = args[++i];
				if (!folder.isEmpty()) {
					destFolder = new File(folder);
				}
			} else if ("--override".equals(args[i])) {
				override = true;
			} else if ("--escape-unicode".equals(args[i])) {
				escapeUnicode = true;
			} else if ("--no-line-numbers".equals(args[i])) {
				printLineNumbers = false;
			} else if (args[i].startsWith("-")) {
				System.err.println("Unknown option: " + args[i]);
				usage();
				System.exit(1);
			} else {
				sources.add(new File(args[i].trim()));
			}
			++i;
		}
		if (sources.isEmpty()) {
			System.err.println("No sources given.");
			usage();
			System.exit(1);
		}

		boolean success = true;
		for (File s : sources) {
			Application application = new Application(s, destFolder, override, escapeUnicode, printLineNumbers);
			success &= application.run();
		}
		System.exit(success ? 0 : 1);
	}
}
