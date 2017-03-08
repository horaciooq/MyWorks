public class TrianguloPascal{

		 public static void main (String[] args){
			System.out.print("Un Triangulo de Pascal\n\n\n");
			
			int [][] pascal = new int[10][10]; //un arreglo de dos dimensiones
			int r,k;
			
			/* Inicializar la matriz*/
			
			for(r=0; r<10; r++){     
		       for(k=0; k<10; k++){
		          pascal[r][k]= 0; 
		       }                  
		    }
		    
		    /* Poner los primeros "1" */
		    pascal[0][0] = pascal[1][0] = pascal[1][1] = 1;
		    
		    /* Poner columna y diagonal de "1" */
		    for(r=2; r<10; r++){
		        pascal[r][0] = pascal[r][r] = 1;     
		    }
		    
		    /* Calculo de los valores de Pascal*/
		    for(r=2; r<10; r++){
		      for(k=1; k<r; k++){     
		       pascal[r][k] =  pascal[r-1][k-1]+pascal[r-1][k]; //Qué debe ir en vez del 8 para resolver la pregunta del foro 3 
		      }               
		    }              
		                  
			/* Ver el arreglo bidimensional: */
			
			for(r=0; r<10; r++){
		       
		       for(k=0; k<10; k++){
		          if (pascal[r][k]!=0){
		              System.out.print("  "+ pascal[r][k]); 
		          }
		       }            
		             
		       System.out.print("\n");
		    }
		
		}   
}




