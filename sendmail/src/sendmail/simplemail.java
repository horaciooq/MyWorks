package sendmail;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import 
javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.JOptionPane;
public class simplemail  {
	public void SendMail() {
		final String Username="horaciooq@yahoo.com.mx";
		final String PassWord="Xeon2015?";
		String To="hochoa@smartsol.com.mx";
		String Subject="test";
		String Mensage="test message";
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.mail.yahoo.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(Username, PassWord);
                    }
                });

        try {

        	Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("horaciooq@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse("horacio.ochoa_smarts@banorte.com"));
			message.setSubject("Testing Subject");
			BodyPart cuerpoMensaje = new MimeBodyPart();
			cuerpoMensaje.setContent("<h1>Dear Mail Crawler</h1>,"
				+ "\n\n No spam to my email, please!", "text/html");
			Multipart multiparte = new MimeMultipart();
			multiparte.addBodyPart(cuerpoMensaje);
			cuerpoMensaje = new MimeBodyPart();
	        String nombreArchivo = "C:\\test\\Editor-Fortify\\results.xml";
	        DataSource fuente = new FileDataSource(nombreArchivo);
	        cuerpoMensaje.setDataHandler(new DataHandler(fuente));
	        cuerpoMensaje.setFileName(nombreArchivo);
	        multiparte.addBodyPart(cuerpoMensaje);
	        message.setContent(multiparte);

 
			Transport.send(message);
 
			System.out.println("Done");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
