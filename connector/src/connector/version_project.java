package connector;

public class version_project {
private int Version_ID;
private String VersionName;
private int Project_ID;
private int Criticas;
private int Altas;
private Boolean Bandera;

public version_project (){
	}

public version_project(String name, int version,int proyecto, int criticas, int altas, Boolean bandera){
	VersionName=name;
	Version_ID=version;
	Project_ID=proyecto;
	Criticas=criticas;
	Altas=altas;
	Bandera=bandera;
	} 
public String GetVersionName(){
	return VersionName;
}
public int GetVersion_ID(){
	return Version_ID;
	}
public int GetProject_ID(){
	return Project_ID;
	}
public int GetCriticas(){
	return Criticas;
	}
public int GetAltas(){
	return Altas;
	}
public Boolean GetBandera(){
	return Bandera;
	}
public void SetVersionName(String name){
	VersionName=name;
	}
public void SetProject_ID(int a){
	Project_ID=a;
	}
public void SetCriticas(int a){
	Criticas=a;
	}
public void SetAltas(int a){
	Altas=a;
	}
public void SetBandera(Boolean a){
	Bandera=a;
	}
public void SetVersion_ID(int a){
	Version_ID=a;
}
}
