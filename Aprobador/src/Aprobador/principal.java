package Aprobador;


import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;

import com.ca.harvest.jhsdk.hutils.JCaHarvestException;

public class principal {

	public static void main(String[] args) {
		CommandLineParser parser = new DefaultParser();
		Options options = new Options();		
		HelpFormatter formatter = new HelpFormatter();
		Option help = new Option( "help", "print this message" );
		Option broker   = OptionBuilder.withArgName( "broker" )
                .hasArg()
                .withDescription(  "utiliza el nombre del broker para conectarse" )
                .create( "b" );
		Option usuario   = OptionBuilder.withArgName( "usuario" )
                .hasArg()
                .withDescription(  "utiliza el usuario dado para logearse al broker" )
                .create( "u" );
		Option contrasena = OptionBuilder.withArgName( "pass" )
                .hasArg()
                .withDescription(  "declara el password del usuario" )
                .create( "pw" );
		Option paquete   = OptionBuilder.withArgName( "paquete" )
                .hasArg()
                .withDescription(  "paquete del cual se cambiara el formulario" )
                .create( "p" );
		Option bandera   = OptionBuilder.withArgName( "bandera" )
                .hasArg()
                .withDescription(  "Bandera que se le pondra al formulario" )
                .create( "f" );
		Option archivodfo   = OptionBuilder.withArgName( "dfo" )
                .hasArg()
                .withDescription(  "se utiliza un archivo DFP para el logueo" )
                .create( "eh" );
		options.addOption(broker);
		options.addOption(usuario);
		options.addOption(contrasena);
		options.addOption(paquete);
		options.addOption(bandera);
		options.addOption(archivodfo);
		 try {
			CommandLine line = parser.parse( options, args );
			if (line.hasOption("help")){
				formatter.printHelp( "-b <broker> -u <usuario> -pw <contraseña> -p <paquete> -f <bandera>", options );
			}
			if( line.hasOption( "b" ) ) {
				if(line.hasOption("eh")){
					if(line.hasOption("p")){
						if(line.hasOption("f")){
							if(line.getOptionValue("f").equals("Aprobado") || line.getOptionValue("f").equals("No Aprobado")){
								cambiarformulario(line.getOptionValue("b"),line.getOptionValue("eh"),line.getOptionValue("p"), line.getOptionValue("f"));
							}else{
								System.out.println("El Valor de la bandera solo puede ser Aprobado / No Aprobado");
								formatter.printHelp( "ant", options );
							}
						}else{
							System.out.println("Se requiere especificar el valor para el paquete Aprobado/No Aprobado");
							formatter.printHelp( "ant", options );
						}
					}else{
						System.out.println("se necesita especificar un paquete");
						formatter.printHelp( "ant", options );
					}
				}else{
					if(line.hasOption("u")){
						if(line.hasOption("pw")){
							if(line.hasOption("p")){
								if(line.hasOption("f")){
									if(line.getOptionValue("f").equals("Aprobado") || line.getOptionValue("f").equals("No Aprobado")){
										cambiarformulario(line.getOptionValue("b"),line.getOptionValue("u"),line.getOptionValue("pw"),line.getOptionValue("p"), line.getOptionValue("f"));
									}else{
										System.out.println("El Valor de la bandera solo puede ser Aprobado / No Aprobado");
										formatter.printHelp( "ant", options );
									}
								}else{
									System.out.println("Se requiere especificar el valor para el paquete Aprobado/No Aprobado");
									formatter.printHelp( "ant", options );
								}
							}else{
								System.out.println("Es necesario especificar un paquete");
								formatter.printHelp( "ant", options );
							}
						}else{
							System.out.println("Es necesario especificar una contraseña");
							formatter.printHelp( "ant", options );
						}
					}else{
						System.out.println("Se necesita especificar un usuario");
						formatter.printHelp( "ant", options );
					}
					
				}
			}else{
				System.out.println("Se necesita especificar un broker");
				formatter.printHelp( "ant", options );
			}
		
		} catch (UnrecognizedOptionException e) {
			// TODO Auto-generated catch block
			System.out.println("La opcion "+e.getOption()+" no es valida");
			} catch(MissingArgumentException e){
			System.out.println("Falta el argumento para "+e.getMessage());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			System.out.println("Error");
		}
		 
	}
		
		
	//--------------------------------------------------------------------
	private static void cambiarformulario(String broker, String user, String pass, String pkg, String flag){
		if(autentificacion(user)){
			credenciales usuario=new credenciales(user,pass);
			try {
				harvestform  hf= new harvestform(broker,usuario);
				hf.modificaForm(pkg, flag);
				System.out.println(pkg+"::"+flag);
			} catch (JCaHarvestException e) {
				System.out.println("Error de conexion");
				e.printStackTrace();
			}
		}else{
			System.out.println("USuario no autorizado para aprobar el paquete");
		
		}
	}
	private static void cambiarformulario(String broker, String dfo, String pkg, String flag){
			try {
				harvestform  hf= new harvestform(broker,dfo);
				hf.modificaForm(pkg, flag);
				System.out.println(pkg+"::"+flag);
			} catch (JCaHarvestException e) {
				System.out.println("Error de conexion");
				e.printStackTrace();
			}
	}
	private static boolean autentificacion(String usr){
		Boolean valido=false;
		String [] aceptados={"harvest","hooqh870","FTFYBNTE"};
		for(int x=0;x<aceptados.length;x++){
			if(aceptados[x].equals(usr)){
				valido=true;;
				break;
			}else{
				valido=false;
			}
			
		}
		return valido;
		
	}

}
