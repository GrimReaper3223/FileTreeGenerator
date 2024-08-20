package com.dsl.ftg.engine;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 
 * @author Deiv
 */
public class GeneratedEntitiesHistogram implements FileVisitor<Path> {
	
	// variaveis para criar um histograma
	// fileCount sera atualizada nao levando em conta as extensoes pois ainda e possivel arquivos sem extensoes serem gerados
	static Map<String, Integer> extensions = new HashMap<>();
	static int fileCount;
	static int dirCount;
	
	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		updateDirCount();
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		updateFileCount();
		updateExtensionMap(file);
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		System.err.format("ERROR: %s does not accessible.%nException caught: %s", file, exc);
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	/**
	 * Atualiza a contagem de arquivos criados
	 * 
	 * @author Deiv
	 */
	private static void updateFileCount() {
		fileCount++;
	}
	
	/**
	 * Atualiza a contagem de diretorios criados
	 * 
	 * @author Deiv
	 */
	private static void updateDirCount() {
		dirCount++;
	}
	/**
	 * Atualiza o histograma
	 * 
	 * @param file o arquivo para capturar a extensao
	 * @author Deiv
	 */
	private static void updateExtensionMap(Path file) {
		Function<Path, String> extensionExtractor = path -> {
			String stringPath = file.getFileName().toString();
			return stringPath.substring(stringPath.lastIndexOf("."));
		};
		
		// extensao do arquivo atual
		String extension = extensionExtractor.apply(file);
		
		//verifica se ja existe esta extensao. se sim, incrementa o valor dela em 1. se nao, cria uma nova entrada
		if(!extensions.containsKey(extension)) {
			extensions.put(extension, 1);
			return;
		} 
		extensions.computeIfPresent(extension, (key, value) -> value += 1);
	}
}
