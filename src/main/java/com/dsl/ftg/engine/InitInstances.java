package com.dsl.ftg.engine;

import java.io.Console;
import java.util.Random;

/**
 * Contem as funcoes que fazem parte do motor da aplicacao
 * 
 * @author Deiv
 */
public class InitInstances {

	private InitInstances() {}
	
	static final Random RNG = new Random();
	
	/**
	 * Verifica a existencia do console
	 * 
	 * @return o console existente ou null caso nao exista
	 * @author Deiv
	 */
	public static Console initConsole() {
		return System.console();
	}
}
