package io.echosystem.ebr.exception;

/**
 *
 * @author Rodolpho Picolo <rodolphopicolo@gmail.com>
 */
public class ArgumentoNaoInformado extends Exception {
    public ArgumentoNaoInformado(String nomeArgumento){
        super("Argumento não informado: " + nomeArgumento);
    }
}
