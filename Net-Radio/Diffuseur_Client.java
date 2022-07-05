import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.*;
/**
 Gère la partie communication entre les clients et le diffuseur diff, via socket
**/
public class Diffuseur_Client implements Runnable {
	private Un_Diffuseur diff;
	private Socket socket;
	/**
	Cree une instance de la classe avec le socket et le diffuseur correspondant 
	**/
	public Diffuseur_Client(Socket socket,Un_Diffuseur diff)
	{
		this.socket = socket;
		this.diff = diff;
	}
	public void run()
	{
		try (
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		PrintWriter  pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		) {
			char[] readd = new char[157];
			br.read(readd,0,157);
			String mess = new String(readd);
			System.out.println(mess);
			String type_mess = mess.substring(0,4);
			//Message de type MESS
			if (type_mess.equals("MESS"))
			{
				String id = mess.substring(5,13);
				String message = mess.substring(14,154);
				if(mess.charAt(4) != ' ' || mess.charAt(13) != ' ')
				{
					System.out.println("Mauvais format pour MESS");
				}
				else if (!mess.substring(154,156).equals("\r\n"))
				{
					System.out.println("Mauvais format");
				}
				else
				{
					diff.ajout_message(message,id);
					pw.print("ACKM\r\n");
					pw.flush();
				}
			}
			//Message de type LAST
			else if (type_mess.equals("LAST")) {
				int nb_mess = Integer.parseInt(mess.substring(5,8));
				List<Message> message = new ArrayList<>(diff.getMessageDiffuse());
				int total_msg = message.size();
				if (total_msg < nb_mess)
				{
					nb_mess = total_msg;
				}
				message = message.subList(total_msg - nb_mess,total_msg);
				for (Message msg : message)
				{
					String to_send = "OLDM " + Entite.ajout_zero(String.valueOf(msg.getNumMessage()),4);
					to_send += " " + Entite.ajout_diese(msg.getIdentifiant(),8) + " ";
					to_send += Entite.ajout_diese(msg.getMessage(),140) + "\r\n";
					System.out.println(to_send);
					pw.print(to_send);
					pw.flush();
				}
				pw.print("ENDM\r\n");
				pw.flush();
			}
			//Autre message
			else
			{
				System.out.println("Message " + mess + "non reconnu");
			}
			socket.close();
		} catch(Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
}
