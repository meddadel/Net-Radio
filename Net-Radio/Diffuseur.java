import java.util.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
/**
 Permet la création d'un diffuseur. On demande en argument le chemin du
 fichier de configuration
**/
public class Diffuseur {
	public static void main(String[] args) {
		List<String> f = new ArrayList<>();
		//Ajout message du fichier message-diff.txt
		f.add("Bienvenue sur ce diffuseur\nCes messages sont des noms d'anime ou de manga");
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("message-diff.txt")));
			String anime = "";
			while((anime = br.readLine()) != null)
			{
				f.add(anime);
			}
			br.close();
			Un_Diffuseur diff = new Un_Diffuseur(args[0],f);
			diff.lancer();
		}
		//Fichier de configuration fourni invalide
		catch(BadConfigFileException e)
		{
			String error_msg = e.getMessage();
			System.out.println(e);
		}
		//Absence du nom du fichier de configuration
		catch (ArrayIndexOutOfBoundsException fich) {
			System.out.println("Pour lancer le diffuseur, il faut le fichier de configuration en argument");
		}
		//Absence du fichier contenant les messages
		catch (FileNotFoundException misse) {
			System.out.println("Fichier message-diff.txt introuvable à la racine");
		}
		catch(Exception e1)
		{
			e1.printStackTrace();
		}
	}
}
