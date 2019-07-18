package org.example;

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
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		String username = args[2];
		String password = args[3];
		String database = args[4];

		final int limit = args.length == 6 ? Integer.parseInt(args[5]) : 0;

		System.out.println(String.format("Connecting to %s:%d/%s as user %s", host, port, database, username));
		DatabaseClient client = DatabaseClientFactory.newClient(host, port, database,
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
					if (limit <= 0 || set.size() < limit) {
						System.out.println("File: " + file.toString());
						set.add(file.toString(), new FileHandle(file.toFile()).withFormat(Format.TEXT));
					}
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
