#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <pthread.h>
#include "Client.h"
Client  * client;
/*
 Gére la réception des messages sur le multi-cast
*/
void * multi_cast(void * args)
{
	int sock = socket(PF_INET,SOCK_DGRAM,0);
	int ok=1;
	int r=setsockopt(sock,SOL_SOCKET,SO_REUSEADDR,&ok,sizeof(ok));
	if(r == -1)
	{
		perror("Erreur setsockopt");
		exit(EXIT_FAILURE);
	}
	struct sockaddr_in address_sock;
	address_sock.sin_family=AF_INET;
	address_sock.sin_port=htons(atoi(client->port_multi));
	address_sock.sin_addr.s_addr=htonl(INADDR_ANY);
	r=bind(sock,(struct sockaddr *)&address_sock,sizeof(struct sockaddr_in));
	if(r==-1)
	{
		perror("Bind Multicast");
		exit(EXIT_FAILURE);
	}
	struct ip_mreq mreq;
	mreq.imr_multiaddr.s_addr=inet_addr(client->addr_multi);
	mreq.imr_interface.s_addr=htonl(INADDR_ANY);
	r=setsockopt(sock,IPPROTO_IP,IP_ADD_MEMBERSHIP,&mreq,sizeof(mreq));
	int fd = 0;
	memcpy(&fd,args,sizeof(int));
	char tampon[SIZE_DIFF+2];
	//Tant que le client n'appuie pas sur 4, on continue
	while(!client->arret){
		int rec=recv(sock,tampon,SIZE_DIFF+2,0);
		if(rec < 0)
			break;

		tampon[rec]='\0';
		//On verifie si le message DIFF est au bon format

		//Par la taille
		if(rec != SIZE_DIFF+2)
		{
			write(fd,"Mauvaise taille de message DIFF\n",33);
			continue;
		}
		//Par la composition du message
		if (verif_diff(fd,tampon))
		{
			affichage_diff(fd,tampon);
		}
	}
	if(fd != 1)
	{
		close(fd);
	}
	return NULL;
}
/*
Gére la partie tcp entre le client et le diffuseur
*/
void * tcp(void * args)
{
	struct sockaddr_in adress_sock;
	adress_sock.sin_family = AF_INET;
	int sock = 0;
	char **msgs = malloc (10 * sizeof(char *));
	msgs[0] = "Coucou\nJe suis un message";
	msgs[1] = "Gloire a l'Olympique Lyonnais";
	msgs[2] = "Vive Jean-Michel Aulas";
	msgs[3] = "Propos tenus par un fervant supporter lyonnais";
	msgs[4] = NULL;
	int taille_msg = 4;
	int index_msgs = 0;
	//Tant que le client n'appuie pas sur 4, on continue
	while(!client->arret)
	{
		int choix;
		int nb_msg = 0;
		int t = 0;
		char address_gest[16];
		int port_gest = 0;
		printf("Que souhaitez-vous ?\n1-Envoi Message 2-Dernier message 3-Liste de diffuseur par un gestionnaire 4-Arret du client\n");
		scanf("%d",&choix);
		switch (choix) {
			//Envoi Message au diffuseur
			case 1:
				t = 0;
				adress_sock.sin_port = htons(atoi(client->port_tcp));
				inet_aton(client->addr_diff,&adress_sock.sin_addr);
				break;
			//Dernier message au diffuseur
			case 2:
				t = 1;
				printf("Quel nombre de messages ?\n");
				scanf("%d",&nb_msg);
				adress_sock.sin_port = htons(atoi(client->port_tcp));
				inet_aton(client->addr_diff,&adress_sock.sin_addr);
				break;
			//Liste diffuseur a un gestionnaire
			case 3:
				t = 2;
				printf("Quelle adresse pour le gestionnaire ?\n");
				scanf("%s",address_gest);
				printf("Le port ?\n");
				scanf("%d",&port_gest);
				adress_sock.sin_port = htons(port_gest);
				inet_aton(address_gest,&adress_sock.sin_addr);
				break;
			//Demande d'arret
			case 4:
				printf("Arret du client\n");
				client->arret = 1;
				continue;
				break;
			default:
				printf("Vous n'avons pas compris votre demande\n");
				continue;
		}
		sock = socket(PF_INET,SOCK_STREAM,0);
		int r = connect(sock,(struct sockaddr *)&adress_sock,sizeof(struct sockaddr_in));
		if(r != -1)
		{
			//Envoi message
			if (t == 0)
			{
				char to_send[SIZE_MESS + 5];
				char message[SIZE_MSG+2];
				sprintf(message,"%s",msgs[index_msgs]);
				for(int i = strlen(message); i < SIZE_MSG;i++)
				{
					message[i] = '#';
				}
				index_msgs = (index_msgs + 1) % taille_msg;
				message[SIZE_MSG] = '\0';
				sprintf(to_send,"MESS %s %s\r\n",client->id,message);
				char mess[SIZE_TYPE+3];
				send(sock,to_send,SIZE_MESS + 2,0);
				int r = recv(sock,mess,SIZE_TYPE + 2,0);
				mess[r] = '\0';
				if(strcmp(mess,"ACKM\r\n")==0)
				{
					printf("%s\n",mess);
				}
				else
				{
					printf("Mauvais format pour mess\n");
				}
				close(sock);
			}
			//Demande Dernier message
			if(t == 1)
			{
				char to_send[11];
				char nbmsg[4];
				sprintf(nbmsg,"%03d",nb_msg);
				sprintf(to_send,"LAST %s\r\n",nbmsg);
				printf("%s\n",to_send);
				send(sock,to_send,10,0);
				for(int i = 0; i < nb_msg; i++)
				{
					char buffer[1000];
					int r = recv(sock,buffer,1000,0);
					buffer[r] = '\0';
					printf("REcu %s\n", buffer);
					if(strcmp("ENDM\r\n",buffer)==0)
					{
						break;
					}

				}
				close(sock);
			}
			//Liste diffuseur par un gestionnaire
			if(t == 2)
			{
				char to_send[]="LIST\r\n";
				send(sock,to_send,6,0);
				char nb_diff_msg[9];
				int r = recv(sock,nb_diff_msg,9,0);
				nb_diff_msg[r] = '\0';
				printf("%s\n",nb_diff_msg);
				if (verif_linb(nb_diff_msg))
				{
					char nb_diffs[3];
					strncpy(nb_diffs,nb_diff_msg + 5,2);
					nb_diffs[2] = '\0';
					int nb_diff = atoi(nb_diffs);
					printf("%d\n",nb_diff);
					for (int i = 0; i < nb_diff; i++)
					{
						char tmp[SIZE_ITEM + 3];
						int r = recv(sock,tmp,SIZE_ITEM + 2,0);
						tmp[r] = '\0';
						printf("%s\n",tmp);
					}
				}
				else
				{
					printf("Mauvais format de message LIBN %s\n",nb_diff_msg);
				}
				close(sock);
			}
		}
		else
		{
			perror("Connect TCP");
			break;
		}
	}
	return NULL;
}
int main(int argc, char const *argv[]) {
	if (argc < 2)
	{
		write(STDERR_FILENO,"Veuillez indiquer un fichier de configuration\n",48);
		return 0;
	}
	char * fichier = malloc(strlen(argv[1]));
	strcpy(fichier,argv[1]);
	fichier[strlen(argv[1])] = '\0';
	//Creation du client
	client = create_client(fichier);
	if(client == NULL)
	{
		write(STDERR_FILENO,"Fichier de configuration incorrect\n",36);
		return EXIT_FAILURE;
	}
	int fd_multi = 1;
	if(argc == 3)
	{
		fd_multi = open(argv[2], O_WRONLY | O_CREAT | O_TRUNC, S_IRWXU);
		if(fd_multi == -1)
		{
			perror("Fichier pour afficher les messages de multi_cast invalides");
			return EXIT_FAILURE;
		}
	}
	pthread_t t1,t2;
	pthread_create(&t1,NULL,multi_cast,&fd_multi);
	pthread_create(&t2,NULL,tcp,NULL);
	pthread_join(t1,NULL);
	pthread_join(t2,NULL);
	free(client);
	return 0;
}
