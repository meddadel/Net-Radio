/**
Permet la creation d'un gestionnaire. Attend en argument le port et
le nombre maximal de diffuseur contenus dans le gestionnaire
**/
public class Gestionnaire {
	public static void main(String[] args) {
		//Absence du port et / ou du nombre maximal de diffuseur -> affichage erreur + arret programme
		if(args.length != 2)
		{
			System.out.println("Un gestionnaire doit être lancé avec 2 arguments: son port, et son nombre maximal de diffuseur");
			return;
		}
		try {
			int port = Integer.parseInt(args[0]);
			int nb_diff = Integer.parseInt(args[1]);
			Un_Gestionnaire gest = new Un_Gestionnaire(port,nb_diff);
			gest.lancer();
		}
		//Argument invalide lors de la création du gestionnaire
		catch (IllegalArgumentException e) {
			System.out.println("Erreur Gestionnaire " + e.getMessage());
		}
	}
}
