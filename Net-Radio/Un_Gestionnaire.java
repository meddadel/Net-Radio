import java.util.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
/**
 Classe représentant un gestionnaire.
 On peut définir un gestionnaire via mon numéro de port, le nombre maximal de
 diffuseur qu'il peut stocker et une liste de diffuseur.
**/
public final class Un_Gestionnaire
{
	private final int port;
	private final int max_diff;
	private List<List<String>> diffuseurs;
	/**
	Crée une instance de Un_Gestionnaire avec le port et le nombre maximal de
	diffuseur qu'il peut stocker
	**/
	public Un_Gestionnaire(int port,int max_diff)
	{
		if(port < 0 || port > 10000)
		{
			throw new IllegalArgumentException("Port incorrect (entre 0 et 9999)");
		}
		this.port = port;
		this.max_diff = max_diff;
		this.diffuseurs = new ArrayList<>();
	}
	/**
	Renvoie le port sur lequel on peut communiquer avec ce gestionnaire
	**/
	public int getPort()
	{
		return this.port;
	}
	/**
	Renvoie la liste des diffuseurs stockés dans le gestionnaire
	**/
	public synchronized List<List<String>> getDiffuseurs()
	{
		return new ArrayList<>(diffuseurs);
	}
	/**
	Ajoute le diffuseur diff, connecté sur l'adresse ip si cela est possible (ndlr
	la liste des diffuseurs contient moins de max_diff)
	**/
	public synchronized boolean ajout_diff(Un_Diffuseur diff,String ip)
	{
		if(diffuseurs.size() == this.max_diff)
		{
			return false;
		}
		String id = diff.getIdentifiant();
		String port_tcp = String.valueOf(diff.getPortTCP());
		String port_multi = String.valueOf(diff.getPortMulti());
		String addr_mult = diff.getAdresseMulti();
		diffuseurs.add(Arrays.asList(id,port_multi,addr_mult,port_tcp,ip));
		return true;
	}
	/**
	Retire de la liste des diffuseurs, le diffuseur ayant l'identifiant en argument
	**/
	public synchronized boolean retrait_diff(String id)
	{
		Iterator<List<String>> it = this.diffuseurs.iterator();
		while(it.hasNext())
		{
			List<String> l = it.next();
			if(l.get(0).equals(id))
			{
				it.remove();
				return true;
			}
		}
		return false;
	}
	/**
	Démarre le gestionnaire
	**/
	public void lancer()
	{
		try
		{
			ServerSocket server = new ServerSocket(port);
			while(true)
			{
				Socket sock = server.accept();
				Gestionnaire_Service gc = new Gestionnaire_Service(this,sock);
				new Thread(gc).start();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
