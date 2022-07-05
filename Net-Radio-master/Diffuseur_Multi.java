import java.util.*;
import java.io.*;
import java.net.*;
/**
 Gére la partie multi-diffusion du diffuseur diff
**/
public final class Diffuseur_Multi implements Runnable{
	private Un_Diffuseur diff;
	/**
	Cree une instance de la classe avec le diffuseur en arguement
	**/
	public Diffuseur_Multi(Un_Diffuseur diff)
	{
		this.diff = diff;
	}
   public void run()
   {
	   try{
		   DatagramSocket env = new DatagramSocket();
		   byte[]data=new byte[1024];
		   int port_multi = diff.getPortMulti();
		   String addr_multi = diff.getAdresseMulti();
		   while(true)
		   {
			   //Envoi de message toutes les 0.75 secondes
			   Thread.sleep(750);
			   synchronized(diff)
			   {
				   //Si la liste est vide, on refait un tour de boucle 
				   if (diff.getMessageADiffuser().size() == 0)
				   {
					   continue;
				   }
				   Message msg = diff.getMessageADiffuser().get(0);
				   String identifiant = Entite.ajout_diese(msg.getIdentifiant(),8);
				   String message_a_diff = Entite.ajout_diese(msg.getMessage(),140);
				   String compt_to_send = String.valueOf(diff.getCompteur());
 				   compt_to_send = Entite.ajout_zero(compt_to_send,4);
 				   String mess = "DIFF " + compt_to_send + " " + identifiant + " " + message_a_diff + "\r\n";
 				   data = mess.getBytes();
 				   DatagramPacket diffuse = new DatagramPacket(data,data.length,InetAddress.getByName(addr_multi),port_multi);
				   env.send(diffuse);
				   diff.diffusion_message(msg);
				}
			}

		} catch(Exception e){
			e.printStackTrace();
		}

	}
}
