package io.echosystem.ebr;

import io.echosystem.ebr.config.Config;
import io.echosystem.ebr.exception.FalhaLeituraConfiguracoes;

/**
 *
 * @author Rodolpho Picolo <rodolphopicolo@gmail.com>
 */
public class EBR {
    public static void main(String[] args) throws FalhaLeituraConfiguracoes{
        Config config = Config.load();
        System.out.println(config);
    }
}
