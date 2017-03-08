package Aprobador;

public class credenciales {
	private String usuario;
	private String contrasena;
	public credenciales(String usr, String pw){
		usuario=usr;
		contrasena=pw;
	}
	public String getusuario(){
		return usuario;
	}
	public String getcontrasena(){
		return contrasena;
	}

}
