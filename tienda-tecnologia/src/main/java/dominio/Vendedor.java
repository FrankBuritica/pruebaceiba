package dominio;

import dominio.repositorio.RepositorioProducto;
import persistencia.entitad.ProductoEntity;
import dominio.excepcion.GarantiaExtendidaException;
import dominio.repositorio.RepositorioGarantiaExtendida;

public class Vendedor {

    public static final String EL_PRODUCTO_TIENE_GARANTIA = "El producto ya cuenta con una garantia extendida";
    public static final String EL_PRODUCTO_NO_TIENE_GARANTIA = "El producto no cuenta con garantia extendida";

    private RepositorioProducto repositorioProducto;
    private RepositorioGarantiaExtendida repositorioGarantia;
    //private RepositorioProductoPersistente repositorio
    
    public Vendedor(RepositorioProducto repositorioProducto, RepositorioGarantiaExtendida repositorioGarantia) {
        this.repositorioProducto = repositorioProducto;
        this.repositorioGarantia = repositorioGarantia;

    }

    public void generarGarantia(String codigo, String nombreCliente) {

    		//Producto producto = repositorioProducto.obtenerPorCodigo(codigo);
    	if(validarVocales(codigo))
    		new GarantiaExtendidaException(EL_PRODUCTO_NO_TIENE_GARANTIA);
    	
    	if (tieneGarantia(codigo))
    		new GarantiaExtendidaException(EL_PRODUCTO_TIENE_GARANTIA);
    	
    	//implementacion de garantia extendida 
    }

    public boolean tieneGarantia(String codigo) {
    	Producto producto = repositorioGarantia.obtenerProductoConGarantiaPorCodigo(codigo);
    	
    	return producto != null ? true : false;
    		
    }
    
    private boolean validarVocales(String codigo) {
    	
    	
    	long total = codigo.toLowerCase().chars()
    	          .mapToObj(i -> (char) i).
    	          filter((l)->l == 'a' || l=='e' || l=='i' || l=='o' || l=='u')
    	          .count();
    	
    	
    	return total == 3 ? true :false;
    }

}
