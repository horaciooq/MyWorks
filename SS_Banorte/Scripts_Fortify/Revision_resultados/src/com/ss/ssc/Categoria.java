package com.ss.ssc;


public class Categoria {
	private String Nombre;
	private String Abstracto;
	private String Explicacion;
	private String Recomendacion;
	private String MetaInfo;
	private int Count;
	public Categoria (){
		Nombre=null;
		Abstracto=null;
		Explicacion=null ;
		Recomendacion=null;
		MetaInfo="";
		Count=0;
	}
	public Categoria (String nombre, String abstracto, String explicacion, String recomendacion,int count, String metainfo){
		Nombre=nombre;
		Abstracto=abstracto;
		Explicacion=explicacion ;
		Recomendacion=recomendacion;
		Count=count;
		MetaInfo=metainfo;
	}
	public void setMetaInfo(String str){
		MetaInfo=str;
	}
	public void addMetaInfo(String str){
		MetaInfo=MetaInfo+str;
	}
	public void setNombre(String str){
		Nombre=str;
	}
	public void setAbstracto(String str){
		Abstracto=str;
	}
	public void setExplicacion(String str){
		Explicacion=str;
	}
	public void setRecomendacion(String str){
		Recomendacion=str;
	}
	public void setcount(int num){
		Count=num;
	}
	public String getMetaInfo(){
		return MetaInfo;
	}
	public String getNombre(){
		return Nombre;
	}
	public String getAbstracto(){
		return Abstracto;
	}
	public String getExplicacion(){
		return Explicacion;
	}
	public String getRecomendacion(){
		return Recomendacion;
	}
	public int getcount(){
		return Count;
	}
}
