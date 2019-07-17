package com.example;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class LoadModules {

	public static void main(String[] args) throws Exception {
		String host = "localhost";
		int port = 8000;
		String username = "admin";
		String password = "admin";

		DatabaseClient client = DatabaseClientFactory.newClient(host, port,
			new DatabaseClientFactory.DigestAuthContext(username, password));

		try {
			GenericDocumentManager mgr = client.newDocumentManager();
			DocumentWriteSet set = mgr.newWriteSet();
			File dir = new File("dhf-modules");
			Files.walkFileTree(dir.toPath(), new FileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
					System.out.println("File: " + file.toString());
					set.add(file.toString(), new FileHandle(file.toFile()).withFormat(Format.TEXT));
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc) {
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
					return FileVisitResult.CONTINUE;
				}
			});

			System.out.println("Writing DocumentWriteSize, size: " + set.size());
			long start = System.currentTimeMillis();
			mgr.write(set);
			System.out.println("Finished writing, time: " + (System.currentTimeMillis() - start));
		} finally {
			client.release();
		}
	}
}
