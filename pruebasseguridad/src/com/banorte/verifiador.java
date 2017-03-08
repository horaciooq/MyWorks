package com.banorte;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class verifiador {
	public verifiador(){
		
	}
	public static String validar(String input) {
	    // Descomposición canónica
	    String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
	    // Nos quedamos únicamente con los caracteres ASCII
	    Pattern pattern = Pattern.compile("\\p{ASCII}+");
	    return pattern.matcher(normalized).replaceAll("");
	}//remove2
}
