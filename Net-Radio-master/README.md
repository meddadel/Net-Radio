# Net-Radio
## Compilation
Pour compiler l'intégralité du projet, il suffit de lancer la commande `make net-radio`.
Si on veut juste compiler une entité du projet (diffuseur,client ou gestionnaire),
il faut taper la commande `make entite` où `entite` peut être `Diffuseur`, `Client`
ou `Gestionnaire`.
La commande `make clean` supprimera tous les fichiers produits lors de la compilation.

## Exécution
* Une fois la compilation effectuée, on peut lancer les différentes entités comme suit:
	* `java Diffuseur diff-config` pour le diffuseur, avec `diff-config` le nom du fichier de
configuration de la forme explicitée dans la partie dédiée.
	* `./client client-config (path)` pour le client, avec `client-config` le nom
du fichier de configuration voulue (dont la forme sera expliquée dans la partie dédiée),
et `path` le chemin du fichier où l'on veut rediriger les messages reçus en multi-diffusion.
`path`. `path` est optionnel; s'il n'est pas renseigné, les messages seront affichés
dans la sortie standard.
	* `java Gestionnaire port nb_max_diff` où `port` est le numéro du port où il écoute (doit être inférieur à 10000)
et `nb_max_diff` est le nombre maximal de diffuseur qu'il peut stocker.

### Format des fichiers de configuration
#### Diffuseur
Un fichier de configuration pour le diffuseur est de cette forme :
```
id
addr_multi
port_multi
port_tcp
```
où id est l'identifiant du diffuseur et doit être d'une longueur maximale de 8 caractères,
addr_multi est l'adresse de multi-diffusion (au format a.b.c.d) sur laquelle il diffuse les messages, port_multi est le port (< 10000) pour la multi-diffusion, et port_tcp est le port
pour les communications en TCP (< 10000).
Le fichier `diff-config.txt` en est un exemple.
#### Client
 Un fichier de configuration pour le client est de cette forme :
```
 id
 addr_multi
 port_multi
 addr_diff
 port_tcp
 ```
 où id est l'identifiant du client et doit être d'une longueur maximale de 8 caractères,
 addr_multi est l'adresse de multi-diffusion (au format a.b.c.d) sur laquelle il se connecte pour recevoir les messages, port_multi est le port (< 10000) pour la multi-diffusion, addr_diff est l'adresse du diffuseur (au format a.b.c.d) et port_tcp est le port
 pour les communications en TCP du diffuseur (< 10000).
 Le fichier `client-config.txt` en est un exemple.

## Utilisation
### Client
* Quand on lance le client, on se retrouve face un menu nous proposant trois choix:
	* `Envoi Message` (touche 1): Le client envoie un message au diffuseur pour le diffuser
sur le canal de multi-diffusion.
	* `Dernier Message` (touche 2): On demande au diffuseur les derniers messages qu'il
a envoyés. Pour ce faire, après le choix 2, on demande le nombre voulu (entre 0 et 999).
Les messages seront affichées dans la sortie standard.
	* `Liste des diffuseurs` (touche 3): Une fois ce choix réalisé, on demande l'adresse
et le port du gestionnaire avec lequel on souhaite interagir. On affiche alors les
diffuseurs enregistrés (leur nom,leur adresse et port de multi-diffusion,leur adresse et
port pour recevoir les messages des clients) dans ce gestionnaire.
	* `Arret du client` (touche 4): Quitte le client.

### Gestionnaire
Le gestionnaire ne demande pas de manipulations pendant son exécution. Pour le quitter,
il faut effectuer un Ctrl-C.

### Diffuseur
Une fois lancé, le diffuseur demande à l'utilisateur de taper sur la touche `1` pour
s'enregistrer auprès d'un gestionnaire. Il demande alors l'adresse et le port sur lesquels on peut contacter le gestionnaire. Pour le quitter,
un Ctrl-C est demandé.

## Architecture     
Le projet est structuré à partir des 3 entités du programme (client, diffuseur et gestionnaire).
### Diffuseur
* Le diffuseur est la partie du programme ayant le plus de code. Il se compose ainsi:
	* Diffuseur.java : classe qui permet de lancer le diffuseur en tant que tel. Elle crée une instance de `Un_Diffuseur` et le lance.
	* Un_Diffuseur.java : Représente le diffuseur.
	* Diffuseur_Multi.java : Gère la diffusion des messages sur l'adresse de multi-diffusion d'une instance d'`Un_Diffuseur`.
	* Diffuseur_TCP.java : Gère les communications en TCP du diffuseur. Pour cela, elle lance un thread avec une instance de `Diffuseur_Gestionnaire`,
puis à chaque connexion d'un client, elle lance un thread avec une instance de `Diffuseur_Client`.
	* Diffuseur_Client.java : Gère la communication entre un client et le diffuseur.
	* Diffuseur_Gestionnaire.java : Gère la communication entre le diffuseur et un gestionnaire
### Client
* Le client est la seule partie du projet réalisée en C. Elle comporte deux fichiers :
	* Client.c : Lance le client et ses communications. Il crée une variable de type
`Client` et lance deux threads- un avec la fonction `multi_cast` pour la partie Multicast et
un avec la fonction `tcp` pour la partie tcp.
	* Client.h : Contient le type `Client`, représentant le client et la fonction pour
le créer.
	* Message.h : Contient les constantes de taille pour les messages et les
fonctions permettant de les vérifier / afficher.
### Gestionnaire
* Pour le gestionnaire, on avons crée trois fichiers :
	* Gestionnaire.java : Lance le gestionnaire, représenté sous la forme d'une instance de `Un_Gestionnaire`.
	* Un_Gestionnaire.java : Représente un gestionnaire
	* Gestionnaire_Service.java : Gère les communications du gestionnaire. S'il reçoit un message
`REGI`, il lance un thread avec une instance de `Gestionnaire_Diffuseur`. Sinon, il traite le message lui-même.
	* Gestionnaire_Diffuseur.java : Gère les communications entre le gestionnaire et un diffuseur.
### Fichiers Auxiliaires
* Le programme a également quelques fichiers supplémentaires.
	* Message.java : Représente un message `DIFF`. À savoir un numéro, un contenu et un auteur.
Surtout utilisé dans `Un_Diffuseur`.
	* Entité.java : Interface contenant des fonctions utiles pour créer un message valide ou rendre
un message du réseau plus lisible.
	* BadConfigFileException.java : Exception levée quand le fichier de configuration du diffuseur n'est
pas au bon format. Utilisée dans `Diffuseur`.
 	* message-diff.txt : Contient les messages à diffuser par le diffuseur.
	* client-config.txt : Fichier de configuration valide pour le client.
	* diff-config.txt : Fichier de configuration valide pour le diffuseur.
	* Bad_config_diff.txt : Fichier de configuration incorrect (identifiant trop long)
pour le diffuseur.
