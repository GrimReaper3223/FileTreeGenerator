package com.dsl.ftg.generator;

import java.io.Console;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Gera todos os diretorios e arquivos recursivamente
 * 
 * @author Deiv
 */
public class Generator {

	// parametros
	private Path workDir;
	private String dirName;
	private Path fullPath;
	private int dirDepth;
	private int fileThreshold;	//TODO: para implementacao
	private String extensions;	//NOTE: parcialmente implementado
	private List<String> extensionList;	//TODO: para implementacao

	public static void main(String[] args) {
		// obtem a instancia do console do sistema
		Console console = initConsole();

		// se nao existir, mostrar uma mensagem que mostra como executar a aplicacao e
		// sai com codigo -1
		if (console == null) {
			System.err.println("ERROR: Console not available. Start the application with the \'java -jar Generator.jar\' command");
			System.exit(-1);
		}

		Generator generator = new Generator();
		
		// define os parametros
		generator.workDir = Paths.get(console.readLine("Enter the path where you want to store the main directory: "));
		generator.dirName = console.readLine("Enter the name of the main directory: ");
		generator.dirDepth = Integer.parseInt(console.readLine("Enter the depth of directory recursion: "));
		generator.fileThreshold = Integer.parseInt(console.readLine("Enter the maximum number of files that can be generated: "));
		generator.extensions = console.readLine("Enter each file extension separated by a comma \',\': ");

		// 1 - Avalia o caminho do diretorio de trabalho e resolve o nome do diretorio principal a este caminho
		// 2 - Processa as extensoes fornecidas. Caso nao haja nenhuma, a lista estara vazia
		generator.fullPath = evaluateAndResolvePath(generator.workDir, generator.dirName);
		generator.extensionList = processExtensions(generator.extensions).orElseGet(List::of);
		generateDirs(generator.fullPath, generator.dirDepth);
	}

	private static Console initConsole() {
		return System.console();
	}

	/**
	 * Processa as extensoes fornecidas
	 * 
	 * @param exts a string contendo as extensoes separadas por virgula
	 * @author Deiv
	 */
	private static Optional<List<String>> processExtensions(String extensions) {
			return Optional.of(Pattern.compile(",")				// compila um padrao
						   .splitAsStream(extensions)			// divide as extensoes como um stream de acordo com o padrao compilado
						   .map(String::trim)					// elimina espacos em branco nas extremidades das strings
						   .toList());							// retorna uma lista de tokens envolvida em um optional (pode ou nao estar vazio)
	}

	/**
	 * Avalia e resolve o caminho passado e o nome do diretorio para ser criado neste caminho
	 * 
	 * @author Deiv
	 */
	private static Path evaluateAndResolvePath(Path workDir, String dirName) {
		Path fullPath = null;
		// verifica se o caminho existe e resolve o nome do diretorio principal no caminho
		if (Files.notExists(workDir)) {
			System.err.format("ERROR: The path [%s] does not exist%n", workDir);
			System.exit(-1);

		} else if (Files.exists(workDir) && !Files.isDirectory(workDir)) {
			System.out.println("The path provided is not a directory. Getting the parent directory and resolving the name of the main directory...");
			fullPath = workDir.toAbsolutePath().resolve(workDir.getParent()).resolve(Path.of(dirName));

		} else if (Files.exists(workDir) && Files.isDirectory(workDir)) {
			System.out.println("Resolving name of main directory...");
			fullPath = workDir.resolve(Path.of(dirName));
		}
		return fullPath;
	}
	
	/**
	 * Gera os diretorios recursivamente
	 * 
	 * @param fullPath o caminho completo para gerar os diretorios recursivamente
	 * @param dirDepth a profundidade de recursao
	 * @author Deiv
	 */
	private static void generateDirs(final Path fullPath, int dirDepth) {
		// faz uma copia dos parametros para evitar alteracoes diretamente no objeto
		Path path = fullPath;
		int dirCount = dirDepth;
		int dirNumber = 1;
		
		try {
			// cria o diretorio principal
			Files.createDirectory(fullPath);
			
			// se decidirmos criar 5 subniveis de diretorio dentro de /Test, o exemplo ficaria assim:
			//	/Test/Dir1-1/Dir2-1/Dir3-1/Dir4-1/Dir5-1
			//  /Test/Dir1-2/Dir2-2/Dir3-2/Dir4-2
			//  /Test/Dir1-3/Dir2-3/Dir3-3...
			do {
				// cria os diretorios no primeiro nivel
				for(int levelDepth = 1; levelDepth <= dirCount; levelDepth++) {
					path = path.resolve(Path.of(String.format("Dir%d-%d", levelDepth, dirNumber)));
					Files.createDirectories(path);
				}
				// subtrai em 1 a quantidade de diretorios de iteracao, incrementa em 1 o numero do diretorio e 
				// redefine o caminho para o diretorio pai inicial
				dirCount--;
				dirNumber++;
				path = fullPath;
				
			} while(dirCount > 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("\nEnd of execution! \nCheck "+ fullPath +" to view generated dir and files.\n");
	}
}
