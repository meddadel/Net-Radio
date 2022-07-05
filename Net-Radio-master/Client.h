#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include "Message.h"
//Structure pour modéliser un utilisateur
typedef struct Client_t{
	char id[SIZE_ID + 1];
	char addr_multi[SIZE_IP + 1];
	char port_multi[SIZE_PORT + 1];
	char addr_diff[SIZE_IP + 1];
	char port_tcp[SIZE_PORT + 1];
	int arret; //Variable pour savoir si on s'arrete ou non
} Client;
/*
Crée un client à partir d'un fichier de configuration.
Si celui n'existe pas, ou est incorrect, on renvoie NULL
*/
Client * create_client(char * filename)
{
	int fd = open(filename,O_RDONLY);
	if(fd == -1)
	{
		return NULL;
	}
	char contenu_fich[55];
	int a = read(fd,contenu_fich,55);
	contenu_fich[a] = '\0';
	//Tableau contenant les index délimitant les caractéristiques de configuration
	int indexes[5] = { 0 };
	int i = 0;
	int j = 0;
	char id[SIZE_ID + 1], addr_multi[SIZE_IP + 1], port_multi[SIZE_PORT + 1],addr_diff[SIZE_IP + 1],port_tcp[SIZE_PORT + 1];
	while (contenu_fich[i] != '\0')
	{
		while(contenu_fich[i] != '\n')
		{
			i++;
		}
		indexes[j] = i;
		i++;
		j++;
	}
	for (size_t i = 0; i < 5; i++) {
		int prec = 0;
		int act = indexes[i];
		if(i != 0)
		{
			prec = indexes[i-1] + 1;
		}
		int taille = act - prec;
		switch (i) {
			case 0:
				if (taille > SIZE_ID)
				{
					return NULL;
				}
				strncpy(id,contenu_fich + prec,taille);
				id[taille] = '\0';
				break;
			case 1:
				if (taille > SIZE_IP)
				{
					return NULL;
				}
				strncpy(addr_multi,contenu_fich + prec,taille);
				addr_multi[taille] = '\0';
				break;
			case 2:
				if (taille > SIZE_PORT)
				{
					return NULL;
				}
				strncpy(port_multi,contenu_fich + prec,taille);
				port_multi[taille] = '\0';
				break;
			case 3:
				if (taille > SIZE_IP)
				{
					return NULL;
				}
				strncpy(addr_diff,contenu_fich + prec,taille);
				addr_diff[taille] = '\0';
				break;
			case 4:
				if (taille > SIZE_PORT)
				{
					return NULL;
				}
				strncpy(port_tcp,contenu_fich + prec,taille);
				port_tcp[taille] = '\0';
				break;
		}
	}
	Client * client = malloc(sizeof(Client));
	sprintf(client->id,"%s",id);
	sprintf(client->addr_multi,"%s",addr_multi);
	sprintf(client->addr_diff,"%s",addr_diff);
	sprintf(client->port_multi,"%s",port_multi);
	sprintf(client->port_tcp,"%s",port_tcp);
	client->arret = 0;
	return client;
}
