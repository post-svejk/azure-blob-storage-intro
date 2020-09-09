package com.example.azure.blob;

import java.util.Arrays;
import java.util.function.Consumer;

import com.azure.storage.blob.BlobServiceClient;

enum CommandLineParser {
	;

	static Consumer<BlobServiceClient> parseArgs(String[] args) {
		if (args.length == 0) {
			return (b) -> System.out
					.printf("%s: No command or argument specified. "
							+ "Try --help for more information.%n", Btp.PROG);
		}
		if (args[0].equals("--auth-mode")) {
			if (args.length < 2) {
				return (b) -> System.out.printf(
						"%s: Missing argument for --auth-mode. Try --help for more information.%n",
						Btp.PROG);
			}
			Btp.authMode = args[1];
			args = Arrays.copyOfRange(args, 2, args.length);
		}
		if (args[0].equals("--help")) {
			return (b) -> printUsage();
		} else if (args[0].equals("mkdir")) {
			return parseMkdir(args);
		} else if (args[0].equals("rmdir")) {
			return parseRmdir(args);
		} else if (args[0].equals("put")) {
			return parsePut(args);
		} else if (args[0].equals("get")) {
			return parseGet(args);
		} else if (args[0].equals("ls")) {
			return parseLs(args);
		} else {
			final String arg = args[0];
			return (b) -> System.out
					.printf("%s: Unknown argument or command: %s. "
							+ "Try --help for more information.%n", Btp.PROG, arg);
		}
	}

	private static void printUsage() {
		System.out.printf("%s: Usage:%n"
				+ "%s [--help] | [mkdir|rmdir CONTAINER] | get CONTAINER BLOB | put CONTAINER FILE%n"
				+ "  --help\t\t Prints this help.%n"
				+ "  mkdir CONTAINER\t Creates a new container in Azure Blob Storage%n"
				+ "  rmdir CONTAINER\t Deletes the container CONTAINER.%n"
				+ "  put CONTAINER FILE\t Uploads FILE into blob container, CONTAINER.%n"
				+ "  get CONTAINER BLOB\t Downloads BLOB from CONTAINER to current directory.%n"
				+ "  ls CONTAINER\t\tList contents of container.%n", Btp.PROG,
				Btp.PROG);
	}

	private static Consumer<BlobServiceClient> parseMkdir(String[] args) {
		if (args.length != 2) {
			return (b) -> System.out.printf(
					"%s: Too few args for the command mkdir. Try --help for more information.%n",
					Btp.PROG);
		}
		String container = args[1];
		return (b) -> Btp.createContainer(b, container);
	}

	private static Consumer<BlobServiceClient> parseRmdir(String[] args) {
		if (args.length != 2) {
			return (b) -> System.out.printf(
					"%s: Too few args for the command rmdir. Try --help for more information.%n",
					Btp.PROG);
		}
		String container = args[1];
		return (b) -> Btp.deleteContainer(b, container);
	}

	private static Consumer<BlobServiceClient> parsePut(String[] args) {
		if (args.length != 3) {
			return (b) -> System.out.printf(
					"%s: Too few args for the command put. Try --help for more information.%n",
					Btp.PROG);
		}
		return (b) -> Btp.uploadFile(b, args[1], args[2]);
	}

	private static Consumer<BlobServiceClient> parseGet(String[] args) {
		if (args.length < 3) {
			return (b) -> System.out.printf(
					"%s: Too few args for the command get. Try --help for more information.%n",
					Btp.PROG);
		}
		String destination = args.length == 4 ? args[3] : null;
		return (b) -> Btp.downloadBlob(b, args[1], args[2], destination);
	}

	private static Consumer<BlobServiceClient> parseLs(String[] args) {
		if (args.length != 2) {
			return (b) -> System.out.printf(
					"%s: Too few args for the command ls. Try --help for more information.%n",
					Btp.PROG);
		}
		return (b) -> Btp.listBlobs(b, args[1]);
	}

}
