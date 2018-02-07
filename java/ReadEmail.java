import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;

import javax.mail.Session;
import javax.mail.Store;

public class ReadEmail
{
    private  final  static  String  PROPS_FILE = "email.properties";
    String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
    String appConfigPath = rootPath + PROPS_FILE;

    Properties properties = new Properties();

    public ReadEmail()
    {
        try {
            properties.load(new FileInputStream(appConfigPath));

            Authenticator auth = new EmailAuthenticator(properties.getProperty("address"), properties.getProperty("password"));
            Session session = Session.getDefaultInstance(properties, auth);
            session.setDebug(false);
            Store store = session.getStore();

            // Connect to mail server
            store.connect(properties.getProperty("imap"), properties.getProperty("address"), properties.getProperty("password"));

            // folder for inbox messages
            Folder inbox = store.getFolder("INBOX");

            // open the folder in read-only mode
            inbox.open(Folder.READ_ONLY);

            System.out.println("Total messages : " + String.valueOf(inbox.getMessageCount()));
            if (inbox.getMessageCount() == 0)
                return;
            Message messages[] = inbox.getMessages();

            // write to console
            for (Message message : messages){
                Multipart mp = (Multipart) message.getContent();
                BodyPart  bp = (BodyPart) mp.getBodyPart(0);
                if (message.getFileName() == null)
                    System.out.println("message : '" + bp.getContent() + "'");
                else
                    System.out.println("file : '" + bp.getFileName() + "'");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void main(String[] args)
    {
        new ReadEmail();

    }
}
