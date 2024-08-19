package com.dsl.ftg.generator;

import java.io.Console;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Contem as funcoes que fazem parte do motor da aplicacao
 * 
 * @author Deiv
 */
interface Engine {

	/**
	 * Verifica a existencia do console
	 * 
	 * @return o console existente ou null caso nao exista
	 * @author Deiv
	 */
	static Console initConsole() {
		return System.console();
	}
	
	/**
	 * Processa as extensoes fornecidas
	 * 
	 * @param exts a string contendo as extensoes separadas por virgula
	 * @author Deiv
	 */
	static Optional<List<String>> processExtensions(String extensions) {
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
	static Path evaluateAndResolvePath(Path workDir, String dirName) {
		Path fullPath = null;
		
		Predicate<Path> isNotExists = Files::notExists;
		Predicate<Path> isExists = Files::exists;
		Predicate<Path> existsAndIsOther = path -> Files.exists(path) && !Files.isDirectory(path);
		Predicate<Path> existsAndIsDirectory = path -> Files.exists(path) && Files.isDirectory(path);
		
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
			fullPath = workDir.resolve(workDir.getParent()).resolve(dirName).toAbsolutePath();

		// em casos normais, o diretorio sera apenas resolvido
		} else if (existsAndIsDirectory.test(workDir)) {
			System.out.println("Resolving name of main directory...");
			fullPath = workDir.resolve(dirName);
		}
		
		//retorna o caminho do diretorio final
		return fullPath;
	}
	
	/**
	 * Gera os arquivos e diretorios recursivamente
	 * 
	 * @param fullPath o caminho completo para gerar os diretorios recursivamente
	 * @param dirDepth a profundidade de recursao
	 * @param fileThreshold o limite de geracao de arquivos
	 * @param extensionList a lista contendo as extensoes, se houver
	 * @author Deiv
	 */
	static void generateDirHierarchy(final Path fullPath, int dirDepth, int fileThreshold, List<String> extensionList) {
		// faz uma copia dos parametros para evitar alteracoes diretamente no objeto
		// dirNumber fornece o numero do diretorio gerado
		Path path = fullPath;
		int dirCount = dirDepth, dirNumber = 1;
		
		try {
			// cria os diretorios necessarios e gera os arquivos iniciais neste diretorio
			Files.createDirectories(path);
			generateFiles(fullPath, randAlgorithm(fileThreshold, extensionList), 1);
			
			do {
				// cria a hierarquia de diretorios e de arquivos
				for(int levelDepth = 1; levelDepth <= dirCount; levelDepth++) {
					path = path.resolve(String.format("Dir$%d-#%d", levelDepth, dirNumber));
					Files.createDirectories(path);
					generateFiles(path, randAlgorithm(fileThreshold, extensionList), levelDepth);
				}
				// Quanto todo o primeiro conjunto de diretorios for criado recursivamente, decrementar em -1 a quantidade total restante
				// O numero do proximo conjunto de diretorios recursivos deve ser incrementado em +1
				// Atribui a variavel local path o caminho original passado ao metodo
				dirCount--;
				dirNumber++;
				path = fullPath;
				
			} while(dirCount > 0);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		System.out.println("\nEnd of execution! \nCheck "+ fullPath +" to view generated dir and files.\n");
	}
	
	/**
	 * Gera arbitrariamente os arquivos
	 * 
	 * @param fullPath o caminho completo para onde gerar os arquivos
	 * @param entry uma entrada cuja chave e a quantidade de arquivos para gerar e o valor e uma lista de extensoes embaralhadas
	 * @param fileLevel o nivel de profundidade da geracao de arquivos que e o mesmo do diretorio
	 * @throws IOException a excecao a ser tratada no nivel superior
	 * @author Deiv
	 */
	static void generateFiles(Path fullPath, Map.Entry<Integer, List<String>> entry, int fileLevel) throws IOException {
		int fileThreshold = entry.getKey();
		List<String> extensionIndex = entry.getValue();
		
		for(int fileNumber = 1; fileNumber <= fileThreshold; fileNumber++) {
			Files.createFile(fullPath.resolve(String.format("File$%d-#%d%s", fileLevel, fileNumber, extensionIndex.get(fileNumber - 1))));
		}
	}
	
	/**
	 * Algoritmo de geracao arbitraria de arquivos
	 * 
	 * @param fileThreshold o limite de arquivos a ser gerado
	 * @param extensionList a lista contendo as extensoes para ser anexada ao caminho do arquivo final 
	 * @return um mapa, onde a chave e a quantidade de arquivos a ser gerada e o valor e uma lista das extensoes selecionadas pelo algoritmo 
	 * para vincular ao nome do arquivo. A quantidade de extensoes geradas leva em conta o valor escolhido para geracao desses arquivos
	 * @author Deiv
	 */
	static Map.Entry<Integer, List<String>> randAlgorithm(int fileThreshold, List<String> extensions) {
		
		// obtem um valor maximo de arquivos para gerar e embaralha a lista de extensoes
		// apos embaralhar a lista de extensoes, um stream executa a randomizacao dos valores desta lista em outra
		// isso garante maxima aleatoriedade sobre os dados retornados
		int maxFileToGenerate = Generator.RNG.nextInt(0, fileThreshold + 1);
		Collections.shuffle(extensions);
		List<String> indexes = Generator.RNG.ints(maxFileToGenerate, 0, extensions.size())
							   .boxed()
							   .map(val -> extensions.get(val))
							   .toList();
		
		return Map.entry(maxFileToGenerate, indexes);	// retorna uma entrada de mapa que contem a quantidade de arquivos para gerar e as extensoes
	}
};
