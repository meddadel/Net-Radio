#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
//Constante sur la taille des composantes d'un message (sans \r\n)
#define SIZE_TYPE 4
#define SIZE_ID 8
#define SIZE_IP 15
#define SIZE_MSG 140
#define SIZE_PORT 4
#define SIZE_NMSG_DIFF 4
#define SIZE_NMSG_LAST 3
#define SIZE_NDIF_LIBN 2
#define SIZE_MESS (SIZE_TYPE + SIZE_ID + SIZE_MSG + 2)
#define SIZE_DIFF (SIZE_TYPE + SIZE_NMSG_DIFF + SIZE_ID + SIZE_MSG + 3)
#define SIZE_ITEM (SIZE_TYPE + 2*SIZE_IP + 2*SIZE_PORT + SIZE_ID + 5)
#define SIZE_LIBN (SIZE_TYPE + SIZE_NDIF_LIBN + 1)
/**
Verifie si le composant num_msg est au bon format
Renvoie 1 si c'est le cas, 0 sinon
**/
int verif_num(char * nb, int lg)
{
	if(strlen(nb) != lg)
	{
		return 0;
	}
	for(int i = 0; i < lg; i++)
	{
		if(nb[i] < '0' && nb[i] > '9')
		{
			return 0;
		}
	}
	return 1;
}
/*
 Verifie si le message en argument respecte le format d'un message de type DIFF.
 Si ce message n'est pas au bon format, on ecrit la raison dans fd
 Renvoie 1 si c'est le cas, 0 sinon
*/
int verif_diff(int fd, char message[SIZE_DIFF + 2])
{
	char type_mess[5];
	memcpy(type_mess,message,SIZE_TYPE);
	type_mess[4] = '\0';
	if(strcmp(type_mess,"DIFF"))
	{
		write(fd,"Mauvais message DIFF\n",22);
		return 0;
	}
	if(message[SIZE_DIFF + 1] != '\n' || message[SIZE_DIFF] != '\r')
	{
		write(fd,"Ne finis pas bien\n",19);
		return 0;
	}
	char id[SIZE_ID+1],num_msg[SIZE_NMSG_DIFF+1],msg[SIZE_MSG+1];
	memcpy(num_msg,message + 5,SIZE_NMSG_DIFF);
	memcpy(id,message + 10,SIZE_ID);
	memcpy(msg,message + 20,SIZE_MSG);
	id[SIZE_ID] = '\0';
	num_msg[SIZE_NMSG_DIFF] = '\0';
	msg[SIZE_MSG] = '\0';
	if(message[4] != ' ' || message[9] != ' ' || message[18] != ' ')
	{
		write(fd,"Mauvais Format\n",16);
		return 0;
	}
	if(!verif_num(num_msg,4))
	{
		write(fd,"Mauvais numero message\n",25);
		return 0;
	}
	return 1;
}
/**
Retire les # de bourage du mot
**/
char * retrait_diese(char * mot)
{
	int i = strlen(mot) - 1;
	while(mot[i] == '#')
	{
		i--;
	}
	i++;
	char * new_mot = malloc(i+1);
	strncpy(new_mot,mot,i);
	new_mot[i] = '\0';
	return new_mot;
}
/**
Affiche le message diff en argument, en lui enlevant les #
**/
void affichage_diff(int fd,char * diff)
{
	char msg[SIZE_MSG+1],num_msg[SIZE_NMSG_DIFF+1],pseudo[SIZE_ID+1];
	strncpy(msg,diff + 19,SIZE_MSG);
	strncpy(num_msg,diff + 5,SIZE_NMSG_DIFF);
	strncpy(pseudo,diff + 10,SIZE_ID);
	msg[SIZE_MSG] = '\0';
	num_msg[SIZE_NMSG_DIFF] = '\0';
	pseudo[SIZE_ID] = '\0';
	char diff_final[SIZE_DIFF+1];
	sprintf(diff_final,"DIFF %s %s %s\n",num_msg,retrait_diese(pseudo),retrait_diese(msg));
	write(fd,diff_final,strlen(diff_final));
}
/*
Verifie si le message en argument est un message LIBN au bon format.
Renvoie 1 si c'est le cas. 0 sinon
*/
int verif_linb(char * message)
{
	if (strncmp(message,"LINB",4)==0)
	{
		if (strlen(message) == SIZE_LIBN + 2)
		{
			if(message[4] == ' ' && message[7] == '\r' && message[8] == '\n')
			{
				if (message[5] >= '0' && message[5] <= '9' && message[6] >= '0' && message[6] <= '9')
				{
					return 1;
				}
			}
		}
	}
	return 0;
}
