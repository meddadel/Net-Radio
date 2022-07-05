/**
Classe contenant des fonctions servant dans le formatage des différents
composants des messages
**/
public interface Entite {
	/**
	Renvoie mot à la bonne longueur (lg_voulu), en le complémentant avec des '#'
	Renvoie null si mot a plus de lg_voulu caracères
	**/
	public static String ajout_diese(String mot,int lg_voulu)
	{
		int lg_mot = mot.length();
		if (lg_mot > lg_voulu)
		{
			return null;
		}
		String mot_complet = new String(mot);
		for (int i = lg_mot; i < lg_voulu; i++)
		{
			mot_complet += "#";
		}
		return mot_complet;
	}
	/**
	Renvoie mot à la bonne longueur, en ajoutant des "0" au début de celui-ci
	**/
	public static String ajout_zero(String mot,int lg_voulu)
	{
		int lg_mot = mot.length();
		if (lg_mot > lg_voulu)
		{
			return null;
		}
		String mot_complet = new String(mot);
		for (int i = lg_mot; i < lg_voulu; i++)
		{
			mot_complet = "0" +  mot_complet;
		}
		return mot_complet;
	}
	/**
	Prend une adresse ip et l'adapte pour respecter le format des adresses ip
	dans les messages
	**/
	public static String convert_ip(String ip)
	{
		String[] m = ip.split("\\.");
		String new_ip = new String();
		for (int i = 0; i < m.length;i++)
		{
			new_ip += ajout_zero(m[i],3);
			if(i != 3)
			{
				new_ip += ".";
			}
		}
		return new_ip;
	}
	/**
	Retire les dieses de bourrage
	**/
	public static String retrait_diese(String mot)
	{
		int i = mot.length() - 1;
		while (mot.charAt(i) == '#')
		{
			i--;
		}
		return mot.substring(0,i+1);
	}
}
