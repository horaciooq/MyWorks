public class Semana3{
  public static void main( String args[ ] ) {

       Humano spiderMan;
       spiderMan  = new Humano("Peter Parker", "castaño", "negros");

       Humano maryJane = new Humano("Mary Jane Watson", "rojo", "azules");
 
       Humano [ ]     docOctopus;
       docOctopus   = new Humano[ 8 ] ;
 
       docOctopus[ 0 ] = spiderMan;
       docOctopus[ 1 ] = maryJane;
       docOctopus[ 2 ] = new Humano("May Parker", "blanco", "cafes");
       docOctopus[ 3 ] = maryJane.concebir(spiderMan, "Benjamin Richard");
      
       for( int k = 0; k < docOctopus.length; k++) {
       		if (docOctopus[ k ] != null){
       			System.out.println( docOctopus[ k ].perfil()  );
       		}
       }
  }
}

class Humano{
  private String nombre;
  private String cabello;
  private String ojos;

  public Humano(String nombre, String cabello, String ojos){
      this.nombre = nombre;
      this.ojos = ojos;
      this.cabello = cabello;
  }
  public Humano(Humano ella, Humano el, String nombre){
      this.nombre = nombre;
      this.cabello = ella.cabello;
      this.ojos = el.ojos;
  }
  public String perfil(){
      return "Nombre: "+nombre+" Ojos: "+ojos+" Cabello: "+cabello;
  }
  public Humano concebir (Humano conyuge, String nombreHijo){
      Humano nuevoSer = new Humano(this, conyuge, nombreHijo);
      return nuevoSer;
  }
}