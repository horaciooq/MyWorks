package com.ss.ssc;


public class Vulnerabilidad {
	private String Instanceid;
	private String Categoria;
	private String Abstracto;
	private String Prioridad;
	private String sinkArchivo;
	private String sinkLocalizacion;
	private int sinkLinea;
	private String sinkSnippet;
	private String sourceArchivo;
	private String sourceLocalizacion;
	private int sourceLinea;
	private String sourceSnippet;
	private String Audit;
	private String Comentario;
	private String estado;
	public Vulnerabilidad(){
		Instanceid=null;
		Categoria=null;
		Abstracto=null;
		Prioridad=null;
		sinkArchivo=null;
		sinkLocalizacion=null;
		sinkLinea=0;
		sinkSnippet=null;
		sourceArchivo=null;
		sourceLocalizacion=null;
		sourceLinea=0;
		sourceSnippet=null;
		Audit="No Set";
		estado=null;
		Comentario="";
	}
	public Vulnerabilidad(String instanceid, String categoria, String abstracto, String prioridad,
			String sinkarchivo, String sinklocalizacion, int sinklinea, String sinksnippet, String sourcearchivo,
			String sourcelocalizacion, int sourcelinea, String sourcesnippet, String comentario){
		Instanceid=instanceid;
		Categoria=categoria;
		Abstracto=abstracto;
		Prioridad=prioridad;
		sinkArchivo=sinkarchivo;
		sinkLocalizacion=sinklocalizacion;
		sinkLinea=sinklinea;
		sinkSnippet=sinksnippet;
		sourceArchivo=sourcearchivo;
		sourceLocalizacion=sourcelocalizacion;
		sourceLinea=sourcelinea;
		sourceSnippet=sourcesnippet;
		Comentario=comentario;
	}
	public void setEstado(String str){
		estado=str;
	}
	public void setComentario(String str){
		Comentario=str;
	}
	public String getEstado(){
		return estado;
	}
	public void setInstanceid(String str){
		Instanceid=str;
	}
	public void setCategoria(String str){
		Categoria=str;
	}
	public void setAbstracto(String str){
		Abstracto=str;
	}
	public void setAudit(String str){
		Audit=str;
	}
	public void setPrioridad(String str){
		Prioridad=str;
	}
	public void setsinkArchivo (String str){
		sinkArchivo=str;
	}
	public void setsinkLocalizacion(String str){
		sinkLocalizacion=str;
	}
	public void setsinkLinea(int num){
		sinkLinea=num;
	}
	public void setsinkSnippet(String str){
		sinkSnippet=str;
	}
	public void setsourceArchivo(String str){
		sourceArchivo=str;
	}
	public void setsourceLocalizacion(String str){
		sourceLocalizacion=str;
	}
	public void setsourceLinea(int num){
		sourceLinea=num;
	}
	public void setsourceSnippet(String str){
		sourceSnippet=str;
	}
	public String getInstanceid(){
		return Instanceid;
	}
	public String getAudit(){
		return Audit;
	}
	public String getCategoria(){
		return Categoria;
	}
	public String getComentario(){
		return Comentario;
	}
	public String getAbstracto(){
		return Abstracto;
	}
	public String getProridad(){
		return Prioridad;
	}
	public String getsinkArchivo(){
		return sinkArchivo;
	}
	public String getsinkLocalizacion(){
		return sinkLocalizacion;
	}
	public int getsinkLinea(){
		return sinkLinea;
	}
	public String getsinkSnippet(){
		return sinkSnippet;
	}
	public String getsourceArchivo(){
		return sourceArchivo;
	}
	public String getsourceLocalizacon(){
		return sourceLocalizacion;
	}
	public int getsourceLinea(){
		return sourceLinea;
	}
	public String getsourceSnippet(){
		return sourceSnippet;
	}
}
