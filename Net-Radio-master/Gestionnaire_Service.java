import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.*;
/**
Gére la communication entre un gestionnaire et une autre entite
**/
public class Gestionnaire_Service implements Runnable,Entite
{
	private Un_Gestionnaire gestion;
	private Socket socket;
	/**
	Cree une instance avec le gestionnaire et le socket en argument
	**/
	public Gestionnaire_Service(Un_Gestionnaire gestion, Socket socket)
	{
		this.gestion = gestion;
		this.socket = socket;
	}
	public void run()
	{
		try (
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		PrintWriter  pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		) {
			char[] readd = new char[157];
			int r = br.read(readd,0,157);
			String mess = new String(readd,0,r);
			String type_mess = mess.substring(0,4);
			System.out.println(type_mess);
			//Message recu List -> msg d'un client, traitement immediat
			if (type_mess.equals("LIST"))
			{
				if (mess.equals("LIST\r\n")){
					List<List<String>> list_diff = gestion.getDiffuseurs();
					int numdiff = (list_diff.size() > 99)? 99:list_diff.size();
					pw.print("LINB " + Entite.ajout_zero(String.valueOf(numdiff),2) + "\r\n");
					pw.flush();
					list_diff = list_diff.subList(0,numdiff);
					for(List<String> diff : list_diff)
					{
						String id = diff.get(0);
						String port1 = diff.get(1);
						String ip1 = diff.get(2);
						String port2 = diff.get(3);
						String ip2 = diff.get(4);
						String msg = "ITEM " + id + " " + ip1 + " " + port1 + " " + ip2 + " " + port2 + "\r\n";
						System.out.println(msg);
						pw.print(msg);
						pw.flush();
					}
				}
				else
				{
					System.out.println("Mauvais format pour LIST");
				}
			}
			//Message REGI -> msg gestionnaire
			else if(type_mess.equals("REGI"))
			{
				System.out.println(mess);
				String id = mess.substring(5,13);
				String ip1 = mess.substring(14,29);
				String port1 = mess.substring(30,34);
				String ip2 = mess.substring(35,50);
				String port2 = mess.substring(51,55);
				Un_Diffuseur diff = new Un_Diffuseur(id,ip1,Integer.parseInt(port1),Integer.parseInt(port2));
				boolean ok = gestion.ajout_diff(diff,ip2);
				//Encore de la place -> envoi REOK + gestion communication deleguee a Gestionnaire_Diffuseur
				if(ok)
				{
					pw.print("REOK\r\n");
					System.out.println("REOK pour " + diff.getIdentifiant());
					pw.flush();
					Gestionnaire_Diffuseur g = new Gestionnaire_Diffuseur(socket,gestion,diff);
					Thread t =  new Thread(g);
					t.start();
					t.join();
				}
				//Plus de place -> envoi message RENO et fermeture connexion
				else
				{
					System.out.println("RENO pour " + diff.getIdentifiant());
					pw.print("RENO\r\n");
					pw.flush();
				}
			}
			else
			{
				System.out.println("Message inconnu");
			}
			socket.close();
		} catch(Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
}
