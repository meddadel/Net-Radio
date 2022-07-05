import java.util.*;
import java.io.*;
import java.net.*;
/**
 Gère les communications TCP du diffuseur diff
**/
public final class Diffuseur_TCP implements Runnable {
	private Un_Diffuseur diff;
	/**
	Cree une instance de la classe avec le diffuseur en argument
	**/
	public Diffuseur_TCP(Un_Diffuseur diff)
	{
		this.diff = diff;
	}
	public void run()
	{
		try {
			int port = diff.getPortTCP();
			ServerSocket server = new ServerSocket(port);
			InetAddress address_diff = server.getInetAddress();
			//Gestion des communications Diffuseur-Gestionnaire
			new Thread(new Diffuseur_Gestionnaire(diff,InetAddress.getLocalHost().getHostAddress())).start();
			//Gestion communication Diffuseur-Client
			while(true)
			{
				Socket sock = server.accept();
				Diffuseur_Client dc = new Diffuseur_Client(sock,diff);
				new Thread(dc).start();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
