import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;


import javax.mail.Session;
import javax.mail.Store;

public class ReadEmail {

    public static void readEmail() {
        final String PROPS_FILE = "email.properties";
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String appConfigPath = rootPath + PROPS_FILE;
        Properties properties = new Properties();
        Logger logger = LoggerFactory.getLogger(ReadEmail.class);

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

                logger.info("Total messages : {}", String.valueOf(inbox.getMessageCount()));

                // write to console
                for (Message message : inbox.getMessages()) {
                    BodyPart bp = ((Multipart) message.getContent()).getBodyPart(0);
                    if (message.getFileName() == null) {
                        System.out.println("message : " + bp.getContent());
                    } else {
                        System.out.println("file : " + bp.getFileName());
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        readEmail();
    }

}
