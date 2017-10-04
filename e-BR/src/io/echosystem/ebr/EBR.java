package io.echosystem.ebr;

import io.echosystem.ebr.config.Config;
import io.echosystem.ebr.exception.FalhaLeituraConfiguracoes;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;

/**
 *
 * @author Rodolpho Picolo <rodolphopicolo@gmail.com>
 */
public class EBR {
    public static void main(String[] args) throws FalhaLeituraConfiguracoes {
        Config config = Config.load();
        try {
            //Apenas para visualizar o resultado da leitura do config.
            byte[] bytes = MarshallerCache.marshall(config);
            System.out.println(new String(bytes));
        } catch (JAXBException ex) {
            throw new FalhaLeituraConfiguracoes(ex);
        }
        

    }
}
