package com.banorte;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class verificador {
	String lista []={"C:\\App\\Reportes\\Reporte_usuario.txt",
			"C:\\App\\Reportes\\Reporte_corporativo.txt" }; 	
	public String verificar(int id) {
		return lista[id];
	}

}
