/**
Exception levée quand le fichier de configuration n'est pas au bon format
**/
public class BadConfigFileException extends Exception
{
	public BadConfigFileException(String msg)
	{
		super(msg);
	}
}
