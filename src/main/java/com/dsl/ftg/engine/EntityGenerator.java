package com.dsl.ftg.engine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Contem os metodos estaticos de geracao arbitraria de entidades
 * 
 * @author Deiv
 */
public class EntityGenerator {

	private EntityGenerator() {}
	
	/**
	 * Gera os arquivos e diretorios recursivamente
	 * 
	 * @param fullPath o caminho completo para gerar os diretorios recursivamente
	 * @param dirDepth a profundidade de recursao
	 * @param fileThreshold o limite de geracao de arquivos
	 * @param extensionList a lista contendo as extensoes, se houver
	 * @author Deiv
	 */
	public static void generateDirHierarchy(final Path fullPath, int dirDepth, int fileThreshold, List<String> extensionList) {
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
	private static void generateFiles(Path fullPath, Map.Entry<Integer, List<String>> entry, int fileLevel) throws IOException {
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
	private static Map.Entry<Integer, List<String>> randAlgorithm(int fileThreshold, List<String> extensions) {
		
		// obtem um valor maximo de arquivos para gerar e embaralha a lista de extensoes
		// apos embaralhar a lista de extensoes, um stream executa a randomizacao dos valores desta lista em outra
		// isso garante maxima aleatoriedade sobre os dados retornados
		int maxFileToGenerate = InitInstances.RNG.nextInt(0, fileThreshold + 1);
		Collections.shuffle(extensions);
		List<String> indexes = InitInstances.RNG.ints(maxFileToGenerate, 0, extensions.size())
							   	  .boxed()
							   	  .map(extensions::get)
							   	  .toList();
		
		return Map.entry(maxFileToGenerate, indexes);	// retorna uma entrada de mapa que contem a quantidade de arquivos para gerar e as extensoes
	}
}
