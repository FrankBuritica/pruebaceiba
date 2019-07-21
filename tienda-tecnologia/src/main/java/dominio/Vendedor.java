package dominio;

import dominio.repositorio.RepositorioProducto;
import persistencia.entitad.ProductoEntity;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import dominio.excepcion.GarantiaExtendidaException;
import dominio.repositorio.RepositorioGarantiaExtendida;

public class Vendedor {

    public static final String EL_PRODUCTO_TIENE_GARANTIA = "El producto ya cuenta con una garantia extendida";
    public static final String EL_PRODUCTO_NO_TIENE_GARANTIA = "El producto no cuenta con garantia extendida";

    private RepositorioProducto repositorioProducto;
    private RepositorioGarantiaExtendida repositorioGarantia;

    
    public Vendedor(RepositorioProducto repositorioProducto, RepositorioGarantiaExtendida repositorioGarantia) {
        this.repositorioProducto = repositorioProducto;
        this.repositorioGarantia = repositorioGarantia;

    }

    public void generarGarantia(String codigo, String nombreCliente, Date fechaActual) {

		double precioGarantia = 0.0;
    	int intDias = 0, intLunes = 0;
    	Date fechaFinGarantia;

    	if(validarVocales(codigo))
    		throw new GarantiaExtendidaException(EL_PRODUCTO_NO_TIENE_GARANTIA);    	

    	if (tieneGarantia(codigo))
    		throw new GarantiaExtendidaException(EL_PRODUCTO_TIENE_GARANTIA);
    	
    	Producto producto = repositorioProducto.obtenerPorCodigo(codigo);
    	
    	if (producto.getPrecio() > 500000.00){
			precioGarantia += producto.getPrecio() * 0.2;
			intDias = 200;			
			fechaFinGarantia = addDiasGarantia(fechaActual , intDias);
			intLunes = cantidadLunes(fechaActual, fechaFinGarantia);
			intDias += intLunes;
			
    	}else {
			precioGarantia += producto.getPrecio() * 0.1;
			intDias = 100;
			
    	}    	
    	fechaFinGarantia = addDiasGarantia(fechaActual , intDias);    	    	
    	
    	if (!validarDiaHabil(fechaFinGarantia))
    		fechaFinGarantia = addDiasGarantia(fechaActual , 1);
    	
    	
    	GarantiaExtendida garantia = new GarantiaExtendida(producto, fechaActual, fechaFinGarantia , precioGarantia, nombreCliente);
    	repositorioGarantia.agregar(garantia);
    	
    }
    
    private Date addDiasGarantia(Date fecha, int dias) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(fecha);    	
    	calendar.add(Calendar.DAY_OF_YEAR, dias);
    	return calendar.getTime();
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

    private boolean validarDiaHabil(Date fecha) {
    	return fecha.getDay() != 0 ? true : false;
    }
    
    private int cantidadLunes(Date fechaActual, Date fechaFinal) {
    	
    	Calendar inicio = Calendar.getInstance();
    	Calendar fin = Calendar.getInstance();
    	int intLunes = 0;
    	inicio.setTime(fechaActual);
    	fin.setTime(fechaFinal);
    	
    	while (!fin.before(inicio)) {
			if(inicio.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY ) {
				intLunes++;
			}
			inicio.add(Calendar.DATE, 1);
		}
    	
    	return intLunes;
    }
}
