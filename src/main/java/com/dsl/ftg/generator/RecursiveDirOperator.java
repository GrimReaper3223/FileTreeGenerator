package com.dsl.ftg.generator;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * 
 * @author Deiv
 */
public class RecursiveDirOperator extends SimpleFileVisitor<Path> {

	// realizar uma acao antes de visitar o diretorio
	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		try {
			// verifica se o diretorio a ser visitado ja esta criado, se nao, criar
			if (Files.notExists(dir)) {
				Files.createDirectory(dir);
				return FileVisitResult.SKIP_SUBTREE;
			} 
		} catch (IOException e) {
			e.printStackTrace();
		}
		return FileVisitResult.CONTINUE;
	}

	// realizar uma acao ao visitar o diretorio posteriormente
	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		return null;
	}
}
