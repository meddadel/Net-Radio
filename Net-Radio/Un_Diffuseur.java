import java.util.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
/**
 Une instance de Un_Diffuseur représente un diffuseur. Il a de ce fait
 	- Un port de multi-diffusion,
	- Un port pour recevoir les messages utilisateurs
	- Une addresse IPv4 de multi-diffusion,
	- Un identifiant.
 Il a également une liste des messages à diffuser (à voir comment la remplir),
 et une liste des messages deja diffusés.
**/
public final class Un_Diffuseur {
	private final int port_multi;
	private final int port_tcp;
	private final String addr_multi;
	private final String identifiant;
	private List<Message> mess_a_diff;
	private List<Message> mess_diffuse;
	private int compteur;
	/**
	 Crée un diffuseur avec un nom de fichier correspondant à sa configuration
	 et de la liste de message à diffuser.
	 Si le fichier ne correspond pas au format voulu, on lève une BadConfigFileException
	**/
	public Un_Diffuseur(String config_file, List<String> mess) throws BadConfigFileException
	{
		List<String> infos = recup_info(config_file);
		//Fichier inexistant
		if(infos == null)
		{
			throw new BadConfigFileException("Fichier de configuration inexistant");
		}
		//Fichier n'ayant pas le bon nombre d'argument
		if (infos.size() != 4)
		{
			throw new BadConfigFileException("Fichier de configuration au mauvais format");
		}
		this.identifiant = infos.get(0);
		if(this.identifiant.length() > 8)
		{
			throw new BadConfigFileException("identifiant trop long");
		}
		this.addr_multi = infos.get(1);
		int p_m = Integer.parseInt(infos.get(2));
		//Port de multi-diffusion incorrect
		if (p_m >= 10000 || p_m < 0)
		{
			throw new BadConfigFileException("Port de multi diffusion incorrect");
		}
		this.port_multi = p_m;
		int p_t = Integer.parseInt(infos.get(3));
		//Port de communication TCP incorrect
		if (p_t >= 10000 || p_t < 0)
		{
			throw new BadConfigFileException("Port TCP incorrect");
		}
		this.port_tcp = p_t;
		this.mess_a_diff = new ArrayList<>();
		for(String msg : mess)
		{
			mess_a_diff.add(new Message(this.identifiant,msg));
		}
		this.mess_diffuse = new ArrayList<>();
		this.compteur = 0;
	}
	/**
	Crée un diffuseur avec les informations en arguments et une liste de diffusion vide
	**/
	public Un_Diffuseur(String id,String addr_multi,int port_multi,int port_tcp)
	{
		if (id.length() > 8)
		{
			throw new IllegalArgumentException("Identifiant trop long");
		}
		this.identifiant = id;
		this.addr_multi = addr_multi;
		if(port_multi > 9999 || port_multi < 0)
		{
			throw new IllegalArgumentException("Port Multi incorrect(doit être compris entre 0 et 9999)");
		}
		this.port_multi = port_multi;
		if(port_tcp > 9999 || port_tcp < 0)
		{
			throw new IllegalArgumentException("Port TCP incorrect(doit être compris entre 0 et 9999)");
		}
		this.port_tcp = port_tcp;
		this.mess_a_diff = new ArrayList<>();
		this.mess_diffuse = new ArrayList<>();
		this.compteur = 0;
	}
	/**
		Récupere les informations de configuration pour le diffuseur
	**/
	public static List<String> recup_info(String filename)
	{
		File f = new File(filename);
		if (f.exists())
		{
			List<String> list_args = new ArrayList<>();
			try(BufferedReader d = new BufferedReader(new FileReader(f));) {
				String current_line = "";
				while((current_line = d.readLine())!=null)
				{
					list_args.add(current_line);
				}
				return list_args;
			}catch (Exception e) {
				e.printStackTrace();
			}
			return list_args;
		}
		else
		{
			return null;
		}
	}
	/**
	 Renvoie la valeur du compteur courant
	**/
	public synchronized int getCompteur()
	{
		return this.compteur;
	}
	/**
	 Incremente la valeur du compteur
	**/
	public synchronized void incrCompteur()
	{
		compteur = (compteur + 1) % 10000;
	}
	/**
	Renvoie l'identifiant du diffuseur
	**/
	public String getIdentifiant()
	{
		return this.identifiant;
	}
	/**
	Renvoie l'adresse de multi-diffusion du diffuseur
	**/
	public String getAdresseMulti()
	{
		return this.addr_multi;
	}
	/**
	Renvoie le numéro du port de multi-diffusion du diffuseur
	**/
	public int getPortMulti()
	{
		return this.port_multi;
	}
	/**
	Renvoie le numéro du port permettant de communiquer avec les utilisateurs
	**/
	public int getPortTCP()
	{
		return this.port_tcp;
	}
	/**
	Renvoie la liste courante des messages à diffuser
	**/
	public List<Message> getMessageADiffuser()
	{
		return this.mess_a_diff;
	}
	/**
	Ajoute le message écrit par identifiant dans les messages à diffuser
	**/
	public synchronized void ajout_message(String message, String identifiant)
	{
		this.mess_a_diff.add(0,new Message(identifiant,message));
	}
	/**
	Retire le message en tete de liste et l'ajoute à la liste des messages
	diffusés
	**/
	public synchronized void diffusion_message(Message mess)
	{
		this.mess_a_diff.remove(0);
		//Le client ne pouvant demander que 999 messages maximum
		if(this.mess_diffuse.size() == 1000)
		{
			this.mess_diffuse.remove(0);
		}
		this.mess_diffuse.add(new Message(getCompteur(),mess.getIdentifiant(),mess.getMessage()));
		//Ajout des messages diffuses à la fin des messages à diffuser afin de ne pas être à court de message
		if(mess.getIdentifiant().equals(this.getIdentifiant()))
			this.mess_a_diff.add(new Message(mess.getIdentifiant(),mess.getMessage()));
		incrCompteur();
	}
	/**
	Renvoie la liste des messages deja diffusés
	**/
	public synchronized List<Message> getMessageDiffuse()
	{
		return this.mess_diffuse;
	}
	/**
	Démarre la mission du diffuseur
	**/
	public void lancer()
	{
		new Thread(new Diffuseur_Multi(this)).start();
		new Thread(new Diffuseur_TCP(this)).start();
	}
}
