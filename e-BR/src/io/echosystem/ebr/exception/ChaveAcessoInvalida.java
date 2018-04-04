package io.echosystem.ebr.exception;

/**
 *
 * @author Rodolpho Picolo <rodolphopicolo@gmail.com>
 */
public class ChaveAcessoInvalida extends Exception {
    public ChaveAcessoInvalida(Throwable cause){
        super(cause);
    }
    
    public ChaveAcessoInvalida(String motivo){
        super(motivo);
    }
}
