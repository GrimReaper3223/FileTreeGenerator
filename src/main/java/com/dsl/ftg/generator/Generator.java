package com.dsl.ftg.generator;

import java.io.Console;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

/**
 * Gera todos os diretorios e arquivos recursivamente
 * 
 * @author Deiv
 */
class Generator implements Engine{

	// parametros
	private Path workDir;
	private String dirName;
	private Path fullPath;
	private int dirDepth;
	private int fileThreshold;	//TODO: para implementacao
	private String extensions;	//NOTE: parcialmente implementado
	private List<String> extensionList;	//TODO: para implementacao

	static final Random RNG = new Random();
	
	public static void main(String[] args) {
		// obtem a instancia do console do sistema
		Console console = Engine.initConsole();

		// se nao existir, mostrar uma mensagem que mostra como executar a aplicacao e
		// sai com codigo -1
		if (console == null) {
			System.err.println("ERROR: Console not available. Start the application with the \'java -jar Generator.jar\' command");
			System.exit(-1);
		}

		Generator generator = new Generator();
		
		// define os parametros
		generator.workDir = Path.of(console.readLine("Enter the path where you want to store the main directory: "));
		generator.dirName = console.readLine("Enter the name of the main directory: ");
		generator.dirDepth = Integer.parseInt(console.readLine("Enter the depth of directory recursion: "));
		generator.fileThreshold = Integer.parseInt(console.readLine("Enter the maximum number of files that can be generated: "));
		generator.extensions = console.readLine("Enter each file extension separated by a comma \',\': ");

		// 1 - Avalia o caminho do diretorio de trabalho e resolve o nome do diretorio principal a este caminho
		// 2 - Processa as extensoes fornecidas. Caso nao haja nenhuma, a lista estara vazia
		generator.fullPath = Engine.evaluateAndResolvePath(generator.workDir, generator.dirName);
		generator.extensionList = Engine.processExtensions(generator.extensions).orElseGet(List::of);
		Engine.generateDirHierarchy(generator.fullPath, generator.dirDepth, generator.fileThreshold, generator.extensionList);
	}
}
