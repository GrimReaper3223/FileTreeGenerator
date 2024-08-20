package com.dsl.ftg.engine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Contem os metodos que processam argumentos do usuario
 * 
 * @author Deiv
 */
public class ProcessData {

	private ProcessData() {}
	
	/**
	 * Processa as extensoes fornecidas
	 * 
	 * @param exts a string contendo as extensoes separadas por virgula
	 * @author Deiv
	 */
	public static Optional<List<String>> processExtensions(String extensions) {
			return Optional.of(Pattern.compile(",")				// compila um padrao
						   .splitAsStream(extensions)			// divide as extensoes como um stream de acordo com o padrao compilado
						   .map(String::trim)					// elimina espacos em branco nas extremidades das strings
						   .collect(Collectors.toList()));		// retorna uma lista de tokens envolvida em um optional (pode ou nao estar vazio)
	}
	
	/**
	 * Avalia e resolve o caminho absoluto passado e o nome do diretorio para ser criado neste caminho
	 * 
	 * @author Deiv
	 */
	public static Path evaluateAndResolvePath(Path workDir, String dirName) {
		Pattern pattern = Pattern.compile("[\\w -[0-9]]*");
		Matcher matcher = pattern.matcher(dirName.trim());
		
		Path fullPath = null;
		
		Predicate<Path> isExists = Files::exists;
		Predicate<Path> isNotExists = Files::notExists;
		Predicate<Path> existsAndIsDirectory = path -> Files.exists(path) && Files.isDirectory(path);
		Predicate<Path> existsAndIsOther = path -> Files.exists(path) && !Files.isDirectory(path);
		
		if (!matcher.matches()){
			System.err.println("\nERROR: Name of main directory is not valid\n\nExiting...");
			System.exit(-1);
		}
		
		// verifica se o caminho existe. se nao existir, cria o diretorio principal no caminho do user.home
		if (isNotExists.test(workDir)) {
			System.err.format("WARNING: The path [%s] does not exist%n", workDir);
			System.out.println("Resolving path to user home directory...");
			fullPath = Paths.get(System.getProperty("user.home"), dirName).toAbsolutePath();
			System.out.format("INFO: Path was resolved from %s to %s%n", workDir, fullPath);
			
		// se o diretorio junto com o nome do diretorio principal ja existir, entao apenas resolver os nomes 
		} else if(isExists.test(workDir.resolve(dirName))) {
			System.out.println("INFO: Directory already exists.");
			fullPath = workDir.resolve(dirName);
		
		// se existir e nao for um diretorio, sobe um nivel no caminho do diretorio e entao vincula o nome do diretorio principal ao caminho completo
		} else if (existsAndIsOther.test(workDir)) {
			System.out.println("The path provided is not a directory. Getting the parent directory and resolving the name of the main directory...");
			fullPath = workDir.getParent().resolve(dirName).toAbsolutePath();

		// em casos normais, o diretorio sera apenas resolvido
		} else if (existsAndIsDirectory.test(workDir)) {
			System.out.println("INFO: Resolving name of main directory...");
			fullPath = workDir.resolve(dirName);
		}
		
		//retorna o caminho do diretorio final
		return fullPath;
	}
	
	/**
 	 * Cria e imprime o histograma de todos os arquivos e diretorios gerados
	 * 
	 * @param fullPath o caminho completo e resolvido
	 * @author Deiv
	 */
	public static void generateHistogram(Path fullPath) {
		try {
			Files.walkFileTree(fullPath, new GeneratedEntitiesHistogram());
			
			StringBuilder sb = new StringBuilder();
			GeneratedEntitiesHistogram.extensions.entrySet()
												 .stream()
												 .forEach(entry -> sb.append('\t')
														 			 .append(entry.getKey())
														 			 .append(" - ")
														 			 .append(entry.getValue())
														 			 .append(" files with this extension;")
														 			 .append(System.lineSeparator()));
			
			
			
			System.out.format("""
					
					[SUCCESS] Generation finished!
					Here is a histogram of how many files and directories were generated:
						
					---------------------------------------------------------------------
					Directories: %d dirs;
					
					Files: %d files (Including files without extensions);
					
					Extensions: 
					%s
					---------------------------------------------------------------------
						
					Check %s to view generated dir and files :)
					""", GeneratedEntitiesHistogram.dirCount, GeneratedEntitiesHistogram.fileCount, sb.toString(), fullPath);

		} catch(IOException e) {
			System.err.println(e.getMessage());
		}
	}
}
