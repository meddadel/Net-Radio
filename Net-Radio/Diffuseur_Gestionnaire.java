import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.*;
/**
Gére la communication entre un diffuseur et un gestionnaire
**/
public class Diffuseur_Gestionnaire implements Runnable{
	private String addr_diff; //Adresse du diffuseur
	private Un_Diffuseur diff; //Le diffuseur
	/**
	Crée une instance de la classe avec le diffuseur en argument et son adresse IP
	**/
	public Diffuseur_Gestionnaire(Un_Diffuseur diff,String ip)
	{
		this.addr_diff = ip;
		this.diff = diff;
	}
	public void run()
	{
		try (Scanner s = new Scanner(System.in);){
			while(true)
				{
					System.out.println("Tapez 1 pour être enregistré dans un gestionnaire");
					int c = s.nextInt();
					if (c == 1)
					{
						break;
					}
				}
				System.out.println("Adresse du gestionnaire ?");
				String addr = s.next();
				System.out.println("Port ?");
				int port = s.nextInt();
				Socket sock = new Socket(addr,port);
				BufferedReader br=new BufferedReader(new InputStreamReader(sock.getInputStream()));
				PrintWriter pw=new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
				String mess = "REGI " + Entite.ajout_diese(diff.getIdentifiant(),8) + " " + Entite.convert_ip(diff.getAdresseMulti());
				mess += " " + Entite.ajout_zero(String.valueOf(diff.getPortMulti()),4) + " " + Entite.convert_ip(addr_diff);
				mess += " " + Entite.ajout_zero(String.valueOf(diff.getPortTCP()),4);
				pw.print(mess + "\r\n");
				pw.flush();
				char[] readd = new char[6];
				br.read(readd,0,6);
				String msg_retour = new String(readd);
				System.out.print(msg_retour);
				//Diffuseur enregistré dans le gestionnaire
				if(msg_retour.equals("REOK\r\n"))
				{
					while(true)
					{
						br.read(readd,0,6);
						String d = new String(readd);
						if(d != null && d.equals("RUOK\r\n"))
						{
							pw.print("IMOK\r\n");
							pw.flush();
						}
						else
							break;
					}
				}
				//Enregistrement impossible
				else if(msg_retour.equals("RENO\r\n"))
				{
					System.out.println("RENO -> Impossible d'enregistrer");
				}
				else
				{
					System.out.println("Message " + msg_retour + " inconnu");
				}
				br.close();
				pw.close();
				sock.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
