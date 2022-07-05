import java.util.*;
/**
Cette classe représente les messages diffusés dans le projet.
Un message est écrit dans une entité, son contenu et un possible numéro de message
**/
public final class Message {
	private final String id;
	private final Optional<Integer> num_msg;
	private final String message;
	/**
	Crée un message avec le numéro, l'identifiant et le contenu en argument
	**/
	public Message(int num, String identifiant, String contenu)
	{
		this.num_msg = Optional.of(num);
		this.id = identifiant;
		this.message = contenu;
	}
	/**
	Crée un message avec l'identifiant et le contenu en argument. Il n'a pas de
	numéro attribué
	**/
	public Message(String identifiant, String contenu)
	{
		this.num_msg = Optional.empty();
		this.id = identifiant;
		this.message = contenu;
	}
	/**
	Renvoie le numéro du message s'il existe ou null.
	**/
	public int getNumMessage()
	{
		return this.num_msg.get();
	}
	/**
	Renvoie l'identifiant de l'auteur du message
	**/
	public String getIdentifiant()
	{
		return this.id;
	}
	/**
	Renvoie le contenu du message
	**/
	public String getMessage()
	{
		return this.message;
	}
}
