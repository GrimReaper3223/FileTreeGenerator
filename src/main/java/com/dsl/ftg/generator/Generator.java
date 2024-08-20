package com.dsl.ftg.generator;

import java.io.Console;
import java.nio.file.Path;
import java.util.List;

import com.dsl.ftg.engine.EntityGenerator;
import com.dsl.ftg.engine.InitInstances;
import com.dsl.ftg.engine.ProcessData;

/**
 * Gera todos os diretorios e arquivos recursivamente
 * 
 * @author Deiv
 */
class Generator {

	// parametros
	private Path workDir;
	private String dirName;
	private Path fullPath;
	private int dirDepth;
	private int fileThreshold;
	private String extensions;	
	private List<String> extensionList;
	
	public static void main(String[] args) {
		// obtem a instancia do console do sistema
		Console console = InitInstances.initConsole();

		// se nao existir, mostrar uma mensagem que mostra como executar a aplicacao e
		// sai com codigo -1
		if (console == null) {
			System.err.println("ERROR: Console not available. Start the application with the \'java -jar Generator.jar\' command");
			System.exit(-1);
		}

		Generator generator = new Generator();
		
		// por enquanto, a unica excecao conhecida que pode ser lancada daqui e a NumberFormatException
		// a aplicacao nao espera se recuperar desta excecao
		try {
			// define os parametros
			generator.workDir = Path.of(console.readLine("Enter the path where you want to store the main directory: "));
			generator.dirName = console.readLine("Enter the name of the main directory: ");
			generator.dirDepth = Integer.parseInt(console.readLine("Enter the depth of directory recursion [or 0 if you don't want nested directories]: "));
			generator.fileThreshold = Integer.parseInt(console.readLine("Enter the maximum number of files that can be generated [or 0 if you don't want to generate files]: "));
			generator.extensions = console.readLine("Enter each file extension separated by a comma \',\' [or press 'Enter' if you do not want to specify extensions]: ");
			
		} catch (NumberFormatException nfe) {
			System.err.println("\nERROR: Please, enter only numbers\n\nExiting...");
			System.exit(-1);
		}
		
		// 1 - Avalia o caminho do diretorio de trabalho e resolve o nome do diretorio principal a este caminho
		// 2 - Processa as extensoes fornecidas. Caso nao haja nenhuma, a lista estara vazia
		// 3 - Gera todas as entidades (arquivos e diretorios)
		// 4 - Procesa um histograma e mostra os dados na tela
		generator.fullPath = ProcessData.evaluateAndResolvePath(generator.workDir, generator.dirName);
		generator.extensionList = ProcessData.processExtensions(generator.extensions).orElseGet(List::of);
		EntityGenerator.generateDirHierarchy(generator.fullPath, generator.dirDepth, generator.fileThreshold, generator.extensionList);
		ProcessData.generateHistogram(generator.fullPath);
	}
}
